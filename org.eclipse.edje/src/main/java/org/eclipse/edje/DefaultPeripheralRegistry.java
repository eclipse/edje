/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Guillaume Balan, MicroEJ} - initial API and implementation and/or initial documentation
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
	private final HashMap<Class<? extends Peripheral>, ClassRecord> peripheralClassRecords;

	/**
	 * Creates a peripheral registry.
	 */
	protected DefaultPeripheralRegistry() {
		peripheralClassRecords = new HashMap<Class<? extends Peripheral>, ClassRecord>();
	}

	@Override
	public <P extends Peripheral> void checkModify(Class<P> peripheralType) {
		check(PeripheralManagerPermission.MODIFY, peripheralType);
	}

	@Override
	public <P extends Peripheral> void checkRead(Class<P> peripheralType) {
		check(PeripheralManagerPermission.READ, peripheralType);
	}

	/**
	 * Checks the given permission for the given peripheral type.
	 * 
	 * @param permission
	 *            the permission to check.
	 * @param peripheralType
	 *            the peripheral type.
	 */
	private <P extends Peripheral> void check(String permission, Class<P> peripheralType) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(new PeripheralManagerPermission<P>(permission, peripheralType));
		}
	}

	@Override
	public <P extends Peripheral> void addRegistrationListener(RegistrationListener<P> listener,
			Class<P> peripheralType) {
		synchronized (peripheralClassRecords) {
			ClassRecord record = getPeripheralClassRecord(peripheralType);
			record.addListener(listener);
		}
	}

	@Override
	public <P extends Peripheral> void removeRegistrationListener(RegistrationListener<P> listener) {
		HashMap<Class<? extends Peripheral>, ClassRecord> records = peripheralClassRecords;
		synchronized (records) {
			Iterator<Class<? extends Peripheral>> it = records.keySet().iterator();
			while (it.hasNext()) {
				Class<? extends Peripheral> c = it.next();
				ClassRecord dc = records.get(c);
				dc.removeListener(listener);
				if (dc.isEmpty()) {
					it.remove();// free the record
				}
			}
		}
	}

	@Override
	public <P extends Peripheral> void register(Class<P> peripheralType, P peripheral) {
		Class<?> peripheralClass = peripheral.getClass();
		HashMap<Class<? extends Peripheral>, ClassRecord> records = peripheralClassRecords;
		synchronized (records) {
			// check for already added
			for (Class<?> c : records.keySet()) {
				if (c.isAssignableFrom(peripheralClass)) {
					ClassRecord dc = records.get(c);
					if (dc.indexOf(peripheral) != -1) {
						throw new IllegalArgumentException();
					}
				}
			}

			// add the peripheral
			ClassRecord record = getPeripheralClassRecord(peripheralType);
			record.addPeripheral(peripheral);
		}
	}

	@Override
	public void unregister(Class<? extends Peripheral> peripheralType, Peripheral peripheral) {
		HashMap<Class<? extends Peripheral>, ClassRecord> records = peripheralClassRecords;
		synchronized (records) {
			ClassRecord dc = records.get(peripheralType);
			if (dc.indexOf(peripheral) != -1) {
				dc.removePeripheral(peripheral);
				if (dc.isEmpty()) {
					records.remove(peripheralType);// free the record
				}
			}
		}
	}

	@Override
	public <P extends Peripheral> Iterator<P> list(Class<P> peripheralType) {
		return list(new SubTypesFilter<P>(peripheralType));
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
	private <P extends Peripheral> Iterator<P> list(ClassFilter classFilter) {
		final Class<? extends Peripheral>[] classes = getRegisteredClasses(classFilter);
		return new PeripheralIterator<P>(classes);
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
	private Class<? extends Peripheral>[] getRegisteredClasses(ClassFilter classFilter) {
		synchronized (peripheralClassRecords) {
			// take a snapshot of keys
			Set<Class<? extends Peripheral>> classesSet = peripheralClassRecords.keySet();
			List<Class<? extends Peripheral>> classesVect = new ArrayList<Class<? extends Peripheral>>();
			for (Class<? extends Peripheral> c : classesSet) {
				if (classFilter.check(c)) {
					classesVect.add(c); // here conversion is true for sure
										// (because of isAssignableFrom())
				}
			}
			Class<? extends Peripheral>[] classes = new Class[classesVect.size()];
			classesVect.toArray(classes);
			return classes;
		}
	}

	/**
	 * Returns {@link ClassRecord} for type of peripheral.
	 * 
	 * @param peripheralType
	 *            type of peripheral
	 * @return a {@link ClassRecord}
	 */
	private <P extends Peripheral> ClassRecord getPeripheralClassRecord(Class<P> peripheralType) {

		// synchronization on #peripheralClassRecords must be done by the caller
		// if necessary

		ClassRecord dc = peripheralClassRecords.get(peripheralType);
		if (dc == null) {
			dc = new ClassRecord();
			peripheralClassRecords.put(peripheralType, dc);
		}
		return dc;
	}

	@Override
	public RegistrationEvent<?> newRegistrationEvent(Peripheral peripheral, Class<? extends Peripheral> registeredClass,
			boolean add) {
		return new RegistrationEvent(this, peripheral, registeredClass, add);
	}

	@Override
	public void executeEvent(Pump<RegistrationEvent<? extends Peripheral>> pump,
			RegistrationEvent<? extends Peripheral> data) {
		executeEvent(pump, data, new SuperTypesFilter(data.registeredClass));
	}

	private RegistrationEvent<? extends Peripheral> getData(Class<? extends Peripheral> c,
			RegistrationEvent<? extends Peripheral> data) {
		return data;
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
	private void executeEvent(Pump<RegistrationEvent<? extends Peripheral>> pump,
			RegistrationEvent<? extends Peripheral> data, ClassFilter classFilter) {
		Class<? extends Peripheral>[] registeredClasses = getRegisteredClasses(classFilter);
		if (data.add) {
			for (Class<? extends Peripheral> c : registeredClasses) {
				ClassRecord dc = getPeripheralClassRecord(c);
				for (RegistrationListener listener : dc.listeners) {
					try {
						listener.peripheralRegistered(getData(c, data));
					} catch (Throwable e) {
						pump.crash(e);
					}
				}
			}
		} else {
			// Peripheral unregistered: notify listeners for all supertypes of
			// the peripheral class
			for (Class<? extends Peripheral> c : registeredClasses) {
				ClassRecord dc = getPeripheralClassRecord(c);
				for (RegistrationListener listener : dc.listeners) {
					try {
						listener.peripheralUnregistered(getData(c, data));
					} catch (Throwable e) {
						pump.crash(e);
					}
				}
			}
		}
	}

	@Override
	public Class<? extends Peripheral> getRegisteredClass(Peripheral peripheral) {
		Class<?> peripheralClass = peripheral.getClass();
		HashMap<Class<? extends Peripheral>, ClassRecord> records = peripheralClassRecords;
		synchronized (records) {
			Iterator<Class<? extends Peripheral>> it = records.keySet().iterator();
			while (it.hasNext()) {
				Class<? extends Peripheral> c = it.next();
				if (c.isAssignableFrom(peripheralClass)) {
					ClassRecord dc = records.get(c);
					if (dc.indexOf(peripheral) != -1) {
						return c;
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
		private final Class<? extends Peripheral>[] classes;

		/**
		 * Current class counter.
		 */
		private int classPtr;

		/**
		 * Current record.
		 */
		private ClassRecord currentRecord;

		/**
		 * Current peripheral counter.
		 */
		private int peripheralPtr;

		/**
		 * Next peripheral in list.
		 */
		private Peripheral next;

		/**
		 * Create a peripherals iterator.
		 * 
		 * @param classes
		 *            types of peripherals
		 */
		PeripheralIterator(Class<? extends Peripheral>[] classes) {
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
		private Peripheral findNext() {
			while (true) {
				if (currentRecord == null) {
					// find next record
					Class<? extends Peripheral> c;
					try {
						c = classes[++classPtr];
					} catch (ArrayIndexOutOfBoundsException e) {
						return null;
					}

					ClassRecord dc = peripheralClassRecords.get(c);
					if (dc != null) { // may have been removed since the
										// snapshot has been taken
						currentRecord = dc;
						peripheralPtr = -1;
					} else {
						continue; // find next record
					}
				}

				// here, currentRecord != null
				ArrayList<Peripheral> peripherals = currentRecord.peripherals;
				try {
					return peripherals.get(++peripheralPtr);
				} catch (IndexOutOfBoundsException e) {
					currentRecord = null;
					continue; // find next record
				}
			}
		}

		@Override
		public P next() {
			Peripheral next = this.next;
			this.next = null;
			return (P) next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Filters the class according the type of the peripheral.
	 */
	static interface ClassFilter {

		/**
		 * @param recordedClass
		 *            the class to check
		 * @return true when the class satisfied the subclass condition
		 */
		<E extends Peripheral> boolean check(Class<E> recordedClass);
	}

	/**
	 * Determines if the class of specified parameter is either the same as, or
	 * is a superclass of, the class represented by the peripheral.
	 * 
	 * @param <P>
	 *            the type of the peripheral
	 */
	static class SuperTypesFilter<P extends Peripheral> implements ClassFilter {

		/**
		 * The type of the peripheral.
		 */
		final Class<?> peripheralType;

		/**
		 * @param peripheralType
		 *            the type of the peripheral
		 */
		SuperTypesFilter(Class<P> peripheralType) {
			this.peripheralType = peripheralType;
		}

		@Override
		public <E extends Peripheral> boolean check(Class<E> recordedClass) {
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
	static class SubTypesFilter<P extends Peripheral> implements ClassFilter {

		/**
		 * The type of the peripheral.
		 */
		final Class<?> peripheralType;

		/**
		 * @param peripheralType
		 *            the type of the peripheral
		 */
		SubTypesFilter(Class<P> peripheralType) {
			this.peripheralType = peripheralType;
		}

		@Override
		public <E extends Peripheral> boolean check(Class<E> recordedClass) {
			return peripheralType.isAssignableFrom(recordedClass);
		}
	}

	/**
	 * Peripherals and listeners registered on the same class.
	 */
	static class ClassRecord {

		/**
		 * Listeners on peripherals of this class.
		 */
		final ArrayList<RegistrationListener<?>> listeners;

		/**
		 * Peripherals registered for this class.
		 */
		final ArrayList<Peripheral> peripherals;

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
		void addListener(RegistrationListener<?> listener) {
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
		void addPeripheral(Peripheral p) {
			if (p == null) {
				throw new IllegalArgumentException();
			}
			peripherals.add(p);
		}

		/**
		 * @param p
		 *            the peripheral to remove
		 * @return true if peripheral has been removed
		 */
		boolean removePeripheral(Peripheral p) {
			return peripherals.remove(p);
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
		 * @return -1 if not found
		 */
		public int indexOf(Peripheral p) {
			return peripherals.indexOf(p);
		}
	}
}
