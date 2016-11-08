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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;

import org.eclipse.edje.util.FixedLengthFIFOQueue;
import org.eclipse.edje.util.Pump;
import org.eclipse.edje.util.Util;

/**
 * The {@link PeripheralManager} holds the {@link Peripheral} registry. It
 * allows to register, unregister, list peripherals. Registered
 * {@link RegistrationListener} are notified when a peripheral is registered or
 * unregistered.
 */
public class PeripheralManager {

	// Notification mechanism is optional. When disabled, no thread is
	// created. See #initializeNotificationEventPump() to enable to notification
	// mechanism

	/**
	 * Default size of the pump which manages the notification mechanism.
	 */
	private static final int DEFAULT_EVENT_BUFFER_SIZE = 10;

	/**
	 * When null, the event pump mechanism is disabled.
	 */
	private static FixedLengthFIFOQueue<RegistrationEvent<? extends Peripheral>> EventsQueue;

	/**
	 * Context local storage instance.
	 */
	private static PeripheralRegistry PeripheralRegistry;

	static {
		initializePeripheralRegistry();
		Pump<RegistrationEvent<?>> pump = initializeNotificationEventPump();
		PeripheralRegistry.start(pump);
	}

	/**
	 * Forbidden constructor: manager cannot be instantiated.
	 */
	private PeripheralManager() {
	}

	/**
	 * Adds the given {@link RegistrationListener} to be notified when a
	 * peripheral of the given type is registered or unregistered. If there is a
	 * security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with the {@link PeripheralManagerPermission#READ} name and the
	 * peripheral type. The listener may be registered multiple times on
	 * different peripheral types.
	 *
	 * @param <P>
	 *            the type of the peripherals to be listened for
	 * @param listener
	 *            the registration listener
	 * @param peripheralType
	 *            the type of the peripherals to be listened for
	 */
	public static <P extends Peripheral> void addRegistrationListener(RegistrationListener<P> listener,
			Class<P> peripheralType) {
		PeripheralRegistry.addRegistrationListener(listener, peripheralType);
	}

	/**
	 * Removes the given {@link RegistrationListener} from the list of listeners
	 * that are notified when a peripheral is registered or unregistered. The
	 * listener may have been registered multiple times on different peripheral
	 * types.
	 *
	 * @param <P>
	 *            the type of the peripheral listened for
	 * @param listener
	 *            the registration listener
	 */
	public static <P extends Peripheral> void removeRegistrationListener(RegistrationListener<P> listener) {
		PeripheralRegistry.removeRegistrationListener(listener);
	}

	/**
	 * Registers a new peripheral with the given type. If there is a security
	 * manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#MODIFY} name and the
	 * peripheral type.
	 *
	 * @param <P>
	 *            the type of the peripheral to be registered
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @param peripheral
	 *            the peripheral to be registered
	 * @throws SecurityException
	 *             if a security manager exists and it does not allow the caller
	 *             to register a peripheral with the given type.
	 * @throws IllegalArgumentException
	 *             if the peripheral has already been registered
	 */
	public static <P extends Peripheral> void register(Class<P> peripheralType, P peripheral) {
		PeripheralRegistry.checkModify(peripheralType, peripheral);
		PeripheralRegistry registry = PeripheralRegistry;
		FixedLengthFIFOQueue<RegistrationEvent<? extends Peripheral>> queue = EventsQueue;
		RegistrationEvent<P> event = registry.register(peripheralType, peripheral, queue != null, false);
		if (event != null) {
			queue.add(event);
		}
	}

	/**
	 * Unregisters the given peripheral. If there is a security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#MODIFY} name and the
	 * peripheral type on which it has been registered. Some peripherals are
	 * registered by the underlying platform and cannot be unregistered.
	 *
	 * @param peripheral
	 *            the peripheral to be unregistered
	 * @throws SecurityException
	 *             if a security manager exists and it does not allow the caller
	 *             to unregister a peripheral
	 */
	public static <P extends Peripheral> void unregister(P peripheral) {
		PeripheralRegistry registry = PeripheralRegistry;
		Class<P> registeredClass = registry.getRegisteredClass(peripheral);
		if (registeredClass != null) {
			PeripheralRegistry.checkModify(registeredClass, peripheral);
			FixedLengthFIFOQueue<RegistrationEvent<? extends Peripheral>> queue = EventsQueue;
			RegistrationEvent<P> event = registry.unregister(registeredClass, peripheral, queue != null);
			if (event != null) {
				queue.add(event);
			}
		}
	}

	/**
	 * List all registered peripherals. Actually, the list is filtered out of
	 * the peripherals that the caller doesn't have the
	 * {@link PeripheralManagerPermission} to
	 * {@link PeripheralManagerPermission#READ} them. <br>
	 * This is equivalent to:
	 *
	 * <pre>
	 * list(Peripheral.class)
	 * </pre>
	 *
	 * @return an iterator of all registered peripherals.
	 */
	public static Iterator<Peripheral> list() {
		return list(Peripheral.class);
	}

	/**
	 * List all registered peripherals such as the given type is assignable from
	 * the peripheral class. Actually, the list is filtered out of the
	 * peripherals that the caller doesn't have the
	 * {@link PeripheralManagerPermission} to
	 * {@link PeripheralManagerPermission#READ} them.
	 *
	 * @param <P>
	 *            the type of peripherals to list
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @return an iterator of all registered peripherals of the given type
	 */
	public static <P extends Peripheral> Iterator<P> list(Class<P> peripheralType) {
		return PeripheralRegistry.list(peripheralType);
	}

	/**
	 * Finds the fisrt peripheral that is compatible with the given class and
	 * that has the specified name. Actually, the list to search into is first
	 * filtered out of the peripherals that the caller doesn't have the
	 * {@link PeripheralManagerPermission} to
	 * {@link PeripheralManagerPermission#READ} them.
	 *
	 * @param <P>
	 *            the type of peripherals to list
	 * @param peripheralType
	 *            the type of the peripheral to be found
	 * @param peripheralName
	 *            the type of the peripheral to be found
	 * @return a peripheral of the given type, with the specified name, or
	 *         <code/>null</code> if no such peripheral is found.
	 * @throws NullPointerException
	 *             if the specified name is null
	 */
	public static <P extends Peripheral> P find(Class<P> peripheralType, String peripheralName) {
		Iterator<P> list = PeripheralManager.list(peripheralType);
		while (list.hasNext()) {
			P p = list.next();
			if (peripheralName.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Initializes the PeripheralRegistry.
	 */
	private static void initializePeripheralRegistry() {
		String key = PeripheralRegistry.class.getName();
		String peripheralRegistryImpl = System.getProperty(key);
		// fall back to service name
		if (peripheralRegistryImpl == null) {
			peripheralRegistryImpl = Util.readConfigurableName(key);
		}
		if (peripheralRegistryImpl != null) {
			try {
				Class<?> peripheralRegistryImplClass = Class.forName(peripheralRegistryImpl);
				PeripheralRegistry = (PeripheralRegistry) peripheralRegistryImplClass.newInstance();
			} catch (Exception e) {
				// Error while instantiating the custom PeripheralRegistry
			}
		}
		if (PeripheralRegistry == null) {
			// no custom PeripheralRegistry or error during its instantiation
			PeripheralRegistry = new DefaultPeripheralRegistry();
		}
	}

	/**
	 * Creates the notification event pump.
	 */
	private static Pump<RegistrationEvent<?>> initializeNotificationEventPump() {
		// start the dynamic event pump if required
		String prefix = "org.eclipse.edje.eventpump.";
		boolean enable = Boolean.getBoolean(new StringBuilder(prefix).append("enabled").toString());

		if (enable) {
			int size = Integer.getInteger(new StringBuilder(prefix).append("size").toString(),
					DEFAULT_EVENT_BUFFER_SIZE);

			EventsQueue = new FixedLengthFIFOQueue<>(size);
			UncaughtExceptionHandler exceptionHandler = null;
			String handlerClass = System.getProperty(new StringBuilder(prefix).append("exceptionHandler").toString(),
					null);
			if (handlerClass != null) {
				try {
					exceptionHandler = (UncaughtExceptionHandler) Class.forName(handlerClass).newInstance();
				} catch (Throwable e) {
					throw new AssertionError(e);
				}
			} else {
				// default handler
				exceptionHandler = new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						e.printStackTrace();
					}
				};
			}
			int priority = Integer.getInteger(new StringBuilder(prefix).append("priority").toString(),
					Thread.NORM_PRIORITY);
			Pump<RegistrationEvent<?>> pump = new Pump<RegistrationEvent<?>>(EventsQueue, priority, exceptionHandler) {

				@Override
				public void execute(RegistrationEvent<? extends Peripheral> data) {
					data.registry.executeEvent(this, data);
				}

			};
			return pump;
		} else {
			return null;
		}
	}

}
