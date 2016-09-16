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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;

import org.eclipse.edje.util.FixedLengthFIFOQueue;
import org.eclipse.edje.util.Pump;

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
		initializeNotificationEventPump();
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
	 * @throws SecurityException
	 *             if a security manager exists and it does not allow the caller
	 *             to listen to peripherals registered with the given type
	 */
	public static <P extends Peripheral> void addRegistrationListener(RegistrationListener<P> listener,
			Class<P> peripheralType) {
		PeripheralRegistry.checkRead(peripheralType);
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
	public static <C extends Peripheral, P extends C> void register(Class<C> peripheralType, P peripheral) {
		register(peripheralType, peripheral, false);
	}

	/**
	 * Registers a new peripheral with the given type. If there is a security
	 * manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#MODIFY} name and the
	 * peripheral type.
	 *
	 * <p>
	 * A static peripheral is a peripheral available on startup. A registration
	 * event is not created when a static peripheral is registered.
	 *
	 * @param <P>
	 *            the type of the peripheral to be registered
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @param peripheral
	 *            the peripheral to be registered
	 * @param staticPeripheral
	 *            <code>true</code> when the peripheral is available on startup
	 * @throws SecurityException
	 *             if a security manager exists and it does not allow the caller
	 *             to register a peripheral with the given type.
	 * @throws IllegalArgumentException
	 *             if the peripheral has already been registered
	 */
	static <P extends Peripheral> void register(Class<P> peripheralType, P peripheral, boolean staticPeripheral) {
		PeripheralRegistry.checkModify(peripheralType);
		PeripheralRegistry registry = PeripheralRegistry;
		registry.register(peripheralType, peripheral);
		if (!staticPeripheral) {
			FixedLengthFIFOQueue<RegistrationEvent<? extends Peripheral>> queue = EventsQueue;
			if (queue != null) {
				queue.add(registry.newRegistrationEvent(peripheral, peripheralType, true));
			}
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
			PeripheralRegistry.checkModify(registeredClass);
			registry.unregister(registeredClass, peripheral);
			FixedLengthFIFOQueue<RegistrationEvent<? extends Peripheral>> queue = EventsQueue;
			if (queue != null) {
				queue.add(registry.newRegistrationEvent(peripheral, registeredClass, false));
			}
		}
	}

	/**
	 * List all registered peripherals. If there is a security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#READ} name and the
	 * {@link Peripheral} class. This is equivalent to:
	 *
	 * <pre>
	 * list(Peripheral.class)
	 * </pre>
	 *
	 * @return an iterator of all registered peripherals.
	 * @throws SecurityException
	 *             if a security manager exists and it doesn't allow the caller
	 *             to list peripherals
	 */
	public static Iterator<Peripheral> list() {
		return list(Peripheral.class);
	}

	/**
	 * List all registered peripherals such as the given type is assignable from
	 * the peripheral class. If there is a security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#READ} action and the
	 * peripheral type.
	 *
	 * @param <P>
	 *            the type of peripherals to list
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @return an iterator of all registered peripherals of the given type
	 * @throws SecurityException
	 *             if a security manager exists and it doesn't allow the caller
	 *             to list peripherals of the given type
	 */
	public static <P extends Peripheral> Iterator<P> list(Class<P> peripheralType) {
		PeripheralRegistry.checkRead(peripheralType);
		return PeripheralRegistry.list(peripheralType);
	}

	/**
	 * Initializes the PeripheralRegistry.
	 */
	private static void initializePeripheralRegistry() {
		String peripheralRegistryImpl = System.getProperty(PeripheralRegistry.class.getName());
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
	private static void initializeNotificationEventPump() {
		// start the dynamic event pump if required
		String prefix = "org.eclipse.edje.eventpump.";
		boolean enable = Boolean.getBoolean(new StringBuilder(prefix).append("enabled").toString());

		if (enable) {
			int size = Integer.getInteger(new StringBuilder(prefix).append("size").toString(),
					DEFAULT_EVENT_BUFFER_SIZE);

			EventsQueue = new FixedLengthFIFOQueue<>(size);
			Thread t = new Thread(new Pump<RegistrationEvent<? extends Peripheral>>(EventsQueue) {

				@Override
				public void execute(RegistrationEvent<? extends Peripheral> data) {
					data.registry.executeEvent(this, data);
				}

			}, "EdjePump");
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
			t.setUncaughtExceptionHandler(exceptionHandler);
			t.setPriority(
					Integer.getInteger(new StringBuilder(prefix).append("priority").toString(), Thread.NORM_PRIORITY));
			t.start();
		}
	}

}
