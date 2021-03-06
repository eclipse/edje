/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Guillaume Balan, MicroEJ} - initial API and implementation and/or initial documentation
 *    {Laurent Lagosanto, MicroEJ} - additional implementation, refactoring
 *******************************************************************************/

package org.eclipse.edje;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edje.util.Pump;

/**
 * Default implementation of {@link PeripheralRegistry}. This implementation
 * uses a {@link HashMap}.
 */
public class DefaultPeripheralRegistry implements PeripheralRegistry {

	/**
	 * Use same HashMap for Peripheral & Listener.
	 * <p>
	 * Assumption: most likely listeners and peripherals are attached to the
	 * same class
	 * <p>
	 * => Filtering is faster because check access is done once on the key
	 */
	private final HashMap<Class<? extends Peripheral>, ClassRecord<?>> peripheralClassRecords;

	/**
	 * Creates a peripheral registry.
	 */
	protected DefaultPeripheralRegistry() {
		peripheralClassRecords = new HashMap<>();
	}

	@Override
	public <C extends Peripheral, P extends C> void checkModify(Class<C> peripheralType, P peripheral) {
		check(peripheralType, peripheral, PeripheralManagerPermission.MODIFY);
	}

	@Override
	public <C extends Peripheral, P extends C> void checkRead(Class<C> peripheralType, P peripheral) {
		check(peripheralType, peripheral, PeripheralManagerPermission.READ);
	}

	/**
	 * Checks the given action for the given peripheral type.
	 *
	 * @param action
	 *            the permission to check.
	 * @param peripheralType
	 *            the peripheral type.
	 */
	private <C extends Peripheral, P extends C> void check(Class<C> peripheralType, P peripheral, String action) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(new PeripheralManagerPermission(peripheralType, peripheral, action));
		}
	}

	@Override
	public <P extends Peripheral> void addRegistrationListener(RegistrationListener<P> listener,
			Class<P> peripheralType) {
		synchronized (peripheralClassRecords) {
			ClassRecord<P> record = getPeripheralClassRecord(peripheralType);
			record.addListener(listener);
		}
	}

	@Override
	public <P extends Peripheral> void removeRegistrationListener(RegistrationListener<P> listener) {
		HashMap<Class<? extends Peripheral>, ClassRecord<?>> records = peripheralClassRecords;
		synchronized (records) {
			Iterator<Class<? extends Peripheral>> it = records.keySet().iterator();
			while (it.hasNext()) {
				Class<? extends Peripheral> c = it.next();
				ClassRecord<?> cr = records.get(c);
				cr.removeListener(listener);
				if (cr.isEmpty()) {
					it.remove();// free the record
				}
			}
		}
	}

	@Override
	public <P extends Peripheral> RegistrationEvent<P> register(Class<P> peripheralType, P peripheral,
			boolean createEvent, boolean staticPeripheral) {
		Class<? extends Peripheral> peripheralClass = peripheral.getClass();
		HashMap<Class<? extends Peripheral>, ClassRecord<?>> records = peripheralClassRecords;
		synchronized (records) {
			// check for already added
			for (Class<?> c : records.keySet()) {
				if (c.isAssignableFrom(peripheralClass)) {
					ClassRecord<?> cr = records.get(c);
					if (cr.contains(peripheral)) {
						throw new IllegalArgumentException();
					}
				}
			}

			// add the peripheral
			ClassRecord<P> record = getPeripheralClassRecord(peripheralType);
			record.addPeripheral(peripheral, staticPeripheral);
			if (createEvent) {
				return newRegistrationEvent(peripheral, peripheralType, true);
			}
		}
		return null;
	}

	@Override
	public <P extends Peripheral> RegistrationEvent<P> unregister(Class<P> peripheralType, P peripheral,
			boolean createEvent) {
		HashMap<Class<? extends Peripheral>, ClassRecord<?>> records = peripheralClassRecords;
		synchronized (records) {
			ClassRecord<?> cr = records.get(peripheralType);
			if (cr != null && cr.contains(peripheral)) {
				@SuppressWarnings("unchecked")
				ClassRecord<P> crp = (ClassRecord<P>) cr;
				crp.removePeripheral(peripheral);
				if (cr.isEmpty()) {
					records.remove(peripheralType);// free the record
				}
				if (createEvent) {
					return newRegistrationEvent(peripheral, peripheralType, false);
				}
			}
		}
		return null;
	}

	@Override
	public <P extends Peripheral> Iterator<P> list(Class<P> peripheralType) {
		return list(new SubTypesFilter<>(peripheralType));
	}

	/**
	 * Returns an iterator on currently registered classes matching the given
	 * type. Gets all registered classes such as peripheralType is assignable
	 * from (sub-types) or all registered classes such as the class is
	 * assignable from the given type (super-types).
	 *
	 * @param classFilter
	 *            the filter (subclass or superclass) to use.
	 * @return an iterator on currently registered classes
	 */
	private <P extends Peripheral> Iterator<P> list(ClassFilter<P> classFilter) {
		Class<P>[] classes = getRegisteredClasses(classFilter);
		return new PeripheralIterator<>(classes);
	}

	/**
	 * Takes a snapshot of currently registered classes matching the given type.
	 * Gets all registered classes such as peripheralType is assignable from
	 * (sub-types) or all registered classes such as the class is assignable
	 * from the given type (super-types).
	 *
	 * @param classFilter
	 *            the filter (subclass or superclass) to use.
	 * @return a snapshot of currently registered classes
	 */
	private <P extends Peripheral> Class<P>[] getRegisteredClasses(ClassFilter<P> classFilter) {
		synchronized (peripheralClassRecords) {
			// take a snapshot of keys
			Set<Class<? extends Peripheral>> classesSet = peripheralClassRecords.keySet();
			List<Class<? extends Peripheral>> classesVect = new ArrayList<>();
			for (Class<? extends Peripheral> c : classesSet) {
				if (classFilter.check(c)) {
					classesVect.add(c); // here conversion is true for sure
										// (because of isAssignableFrom())
				}
			}
			Class<P>[] classes = buildClassArray(classesVect.size());
			classesVect.toArray(classes);
			return classes;
		}
	}

	@SuppressWarnings("unchecked")
	private <P extends Peripheral> Class<P>[] buildClassArray(int size) {
		return new Class[size];
	}

	/**
	 * Returns {@link ClassRecord} for type of peripheral.
	 *
	 * @param peripheralType
	 *            type of peripheral
	 * @return a {@link ClassRecord}
	 */
	private <P extends Peripheral> ClassRecord<P> getPeripheralClassRecord(Class<P> peripheralType) {

		// synchronization on #peripheralClassRecords must be done by the caller
		// if necessary

		ClassRecord<?> cr = peripheralClassRecords.get(peripheralType);
		if (cr == null) {
			cr = new ClassRecord<P>();
			peripheralClassRecords.put(peripheralType, cr);
		}
		@SuppressWarnings("unchecked")
		ClassRecord<P> crp = (ClassRecord<P>) cr;
		return crp;
	}

	<C extends Peripheral, P extends C> RegistrationEvent<C> newRegistrationEvent(P peripheral,
			Class<C> registeredClass, boolean add) {
		return new RegistrationEvent<>(this, peripheral, registeredClass, add);
	}

	@Override
	public <P extends Peripheral> void executeEvent(Pump<RegistrationEvent<?>> pump, RegistrationEvent<P> data) {
		executeEvent(pump, data, new SuperTypesFilter<>(data.getRegisteredClass()));
	}

	/**
	 * Notifies the listeners on a registration / unregistration event.
	 *
	 * @param pump
	 *            the pump which manages the pool of events
	 * @param data
	 *            the registration event
	 * @param classFilter
	 *            the type of the peripheral
	 */
	private <P extends Peripheral> void executeEvent(Pump<RegistrationEvent<?>> pump, RegistrationEvent<P> data,
			ClassFilter<P> classFilter) {
		Class<P>[] registeredClasses = getRegisteredClasses(classFilter);
		if (data.isRegistration()) {
			for (Class<P> c : registeredClasses) {
				ClassRecord<P> cr = getPeripheralClassRecord(c);

				for (RegistrationListener<P> listener : cr.listeners) {
					try {
						try {
							checkRead(c, data.getPeripheral());
						} catch (SecurityException ex) {
							// we skip this if you can't read it
							continue;
						}
						listener.peripheralRegistered(data);
					} catch (Throwable e) {
						pump.crash(e);
					}
				}
			}
		} else {
			// Peripheral unregistered: notify listeners for all supertypes of
			// the peripheral class
			for (Class<P> c : registeredClasses) {
				ClassRecord<P> cr = getPeripheralClassRecord(c);
				for (RegistrationListener<P> listener : cr.listeners) {
					try {
						try {
							checkRead(c, data.getPeripheral());
						} catch (SecurityException ex) {
							// we skip this if you can't read it
							continue;
						}
						listener.peripheralUnregistered(data);
					} catch (Throwable e) {
						pump.crash(e);
					}
				}
			}
		}
	}

	@Override
	public <C extends Peripheral, P extends C> Class<C> getRegisteredClass(P peripheral) {
		Class<?> peripheralClass = peripheral.getClass();
		HashMap<Class<? extends Peripheral>, ClassRecord<?>> records = peripheralClassRecords;
		synchronized (records) {
			Iterator<Class<? extends Peripheral>> it = records.keySet().iterator();
			while (it.hasNext()) {
				Class<? extends Peripheral> c = it.next();
				if (c.isAssignableFrom(peripheralClass)) {
					@SuppressWarnings("unchecked")
					Class<C> registeredClass = (Class<C>) c;
					ClassRecord<?> cr = records.get(registeredClass);
					if (cr.contains(peripheral)) {
						return registeredClass;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Iterates on an array of type of peripherals.
	 *
	 * @param <P>
	 *            type of the peripheral
	 */
	private class PeripheralIterator<P extends Peripheral> implements Iterator<P> {

		/**
		 * Lists of classes of peripherals.
		 */
		private final Class<P>[] classes;

		/**
		 * Current class counter.
		 */
		private int classPtr;

		/**
		 * Current record.
		 */
		private ClassRecord<P> currentRecord;

		/**
		 * Current peripheral counter.
		 */
		private int peripheralPtr;

		/**
		 * Next peripheral in list.
		 */
		private P next;

		/**
		 * Create a peripherals iterator.
		 *
		 * @param classes
		 *            types of peripherals
		 */
		PeripheralIterator(Class<P>[] classes) {
			this.classes = classes;
			this.classPtr = -1;
		}

		@Override
		public boolean hasNext() {
			if (next == null) {
				next = findNext();
			}
			return next != null;
		}

		/**
		 * Retrieves next peripheral.
		 *
		 * @return next peripheral
		 */
		private P findNext() {
			while (true) {
				if (currentRecord == null) {
					// find next record
					Class<P> c;
					try {
						c = classes[++classPtr];
					} catch (ArrayIndexOutOfBoundsException e) {
						return null;
					}

					@SuppressWarnings("unchecked")
					ClassRecord<P> cr = (ClassRecord<P>) peripheralClassRecords.get(c);
					if (cr != null) { // may have been removed since the
										// snapshot has been taken
						currentRecord = cr;
						peripheralPtr = -1;
					} else {
						continue; // find next record
					}
				}

				// here, currentRecord != null
				ArrayList<PeripheralEntry<P>> peripherals = currentRecord.peripherals;
				try {
					P p = peripherals.get(++peripheralPtr).peripheral;
					try {
						checkRead(classes[classPtr], p);
					} catch (SecurityException ex) {
						// we skip this if you can't read it
						continue;
					}
					return p;
				} catch (IndexOutOfBoundsException e) {
					currentRecord = null;
					continue; // find next record
				}
			}
		}

		@Override
		public P next() {
			P next = this.next;
			this.next = null;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Filters the class according the type of the peripheral.
	 */
	static interface ClassFilter<C extends Peripheral> {

		/**
		 * @param recordedClass
		 *            the class to check
		 * @return true when the class satisfied the subclass condition
		 */
		boolean check(Class<? extends Peripheral> recordedClass);
	}

	/**
	 * Determines if the class of specified parameter is either the same as, or
	 * is a superclass of, the class represented by the peripheral.
	 *
	 * @param <P>
	 *            the type of the peripheral
	 */
	static class SuperTypesFilter<P extends Peripheral> implements ClassFilter<P> {

		/**
		 * The type of the peripheral.
		 */
		final Class<P> peripheralType;

		/**
		 * @param peripheralType
		 *            the type of the peripheral
		 */
		SuperTypesFilter(Class<P> peripheralType) {
			this.peripheralType = peripheralType;
		}

		@Override
		public boolean check(Class<? extends Peripheral> recordedClass) {
			return recordedClass.isAssignableFrom(peripheralType);
		}
	}

	/**
	 * Determines if the class of peripheral is either the same as, or is a
	 * superclass of, the class represented by the specified parameter.
	 *
	 * @param <P>
	 *            the type of the peripheral
	 */
	static class SubTypesFilter<P extends Peripheral> implements ClassFilter<P> {

		/**
		 * The type of the peripheral.
		 */
		final Class<P> peripheralType;

		/**
		 * @param peripheralType
		 *            the type of the peripheral
		 */
		SubTypesFilter(Class<P> peripheralType) {
			this.peripheralType = peripheralType;
		}

		@Override
		public boolean check(Class<? extends Peripheral> recordedClass) {
			return peripheralType.isAssignableFrom(recordedClass);
		}
	}

	static class PeripheralEntry<P extends Peripheral> {
		final boolean isStatic;
		final P peripheral;

		PeripheralEntry(P peripheral, boolean isStatic) {
			if (peripheral == null) {
				throw new NullPointerException();
			}
			this.isStatic = isStatic;
			this.peripheral = peripheral;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isStatic ? 1231 : 1237);
			result = prime * result + ((peripheral == null) ? 0 : peripheral.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			PeripheralEntry<?> other = (PeripheralEntry<?>) obj;
			if (isStatic != other.isStatic) {
				return false;
			}
			if (!peripheral.equals(other.peripheral)) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Peripherals and listeners registered on the same class.
	 */
	static class ClassRecord<P extends Peripheral> {

		/**
		 * Listeners on peripherals of this class.
		 */
		final ArrayList<RegistrationListener<P>> listeners;

		/**
		 * Peripherals registered for this class.
		 */
		final ArrayList<PeripheralEntry<P>> peripherals;

		/**
		 * Creates a record for a list of listeners and peripheral.
		 */
		ClassRecord() {
			listeners = new ArrayList<>();
			peripherals = new ArrayList<>();
		}

		/**
		 * @param listener
		 *            the listener to add
		 */
		void addListener(RegistrationListener<P> listener) {
			if (listener == null) {
				throw new IllegalArgumentException();
			}
			listeners.add(listener);
		}

		/**
		 * @param listener
		 *            the listener to remove
		 */
		void removeListener(RegistrationListener<?> listener) {
			listeners.remove(listener);
		}

		/**
		 * @param p
		 *            the peripheral to add
		 */
		void addPeripheral(P p, boolean isStatic) {
			if (p == null) {
				throw new IllegalArgumentException();
			}
			peripherals.add(new PeripheralEntry<>(p, isStatic));
		}

		/**
		 * @param p
		 *            the peripheral to remove
		 * @return true if peripheral has been removed
		 */
		boolean removePeripheral(P p) {
			// create a template for the entry to be removed
			// (must NOT be static)
			PeripheralEntry<P> entry = new PeripheralEntry<>(p, false);
			return peripherals.remove(entry);
		}

		/**
		 * @return true when both listeners and peripherals lists are empty.
		 */
		boolean isEmpty() {
			return listeners.size() == 0 && peripherals.size() == 0;
		}

		/**
		 * @param p
		 *            the peripheral to look for
		 * @return true if found
		 */
		public boolean contains(Peripheral p) {
			for (PeripheralEntry<P> entry : peripherals) {
				if (p.equals(entry.peripheral)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void start(Pump<RegistrationEvent<?>> pump) {
		if (pump != null) {
			Thread t = new Thread(pump, "EdjePump");
			t.setPriority(pump.getPriority());
			t.start();
		}
	}
}
