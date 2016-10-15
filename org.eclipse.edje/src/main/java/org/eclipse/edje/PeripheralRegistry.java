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

import java.util.Iterator;

import org.eclipse.edje.util.Pump;

/**
 * Common interface for a {@link Peripheral} registry. A {@link Peripheral}
 * registry holds a pool of registered {@link Peripheral}. This interface allows
 * to create a specific implementation that can be used by the
 * {@link PeripheralManager}.
 */
public interface PeripheralRegistry {

	/**
	 * Finalize the initialization of the registry. This is where the event
	 * thread (if any) is started, and where the static devices are populated
	 * into the registry.
	 *
	 * @param pump
	 *            the event pump, to be started
	 */
	void start(Pump<RegistrationEvent<?>> pump);

	/**
	 * If there is a security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#MODIFY} name and the
	 * peripheral type.
	 *
	 * @param peripheralType
	 *            the type of peripheral
	 */
	<C extends Peripheral, P extends C> void checkModify(Class<C> peripheralType, P peripheral);

	/**
	 * If there is a security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with the {@link PeripheralManagerPermission#READ} name and the
	 * peripheral type.
	 *
	 * @param peripheralType
	 *            the type of peripheral
	 */
	<C extends Peripheral, P extends C> void checkRead(Class<C> peripheralType, P peripheral);

	/**
	 * Adds the given {@link RegistrationListener} to be notified when a
	 * peripheral of the given type is registered or unregistered. The listener
	 * may be registered multiple times on different peripheral types.
	 *
	 * @param listener
	 *            the registration listener
	 * @param peripheralType
	 *            the type of the peripherals to be listened for
	 */
	<P extends Peripheral> void addRegistrationListener(RegistrationListener<P> listener, Class<P> peripheralType);

	/**
	 * Removes the given {@link RegistrationListener} from the list of listeners
	 * that are notified when a peripheral is registered or unregistered. The
	 * listener may have been registered multiple times on different peripheral
	 * types.
	 *
	 * @param listener
	 *            the registration listener
	 */
	<P extends Peripheral> void removeRegistrationListener(RegistrationListener<P> listener);

	/**
	 * List all registered peripherals such as the given type is assignable from
	 * the peripheral class. If there is a security manager, its
	 * {@link SecurityManager#checkPermission(java.security.Permission)} method
	 * is called with {@link PeripheralManagerPermission#READ} action and the
	 * peripheral type.
	 *
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @return an iterator of all registered peripherals of the given type
	 */
	<P extends Peripheral> Iterator<P> list(Class<P> peripheralType);

	/**
	 * Registers a new peripheral with the given type.
	 *
	 * @param <P>
	 *            the type of the peripheral to be registered
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @param peripheral
	 *            the peripheral to be registered
	 * @param createEvent
	 *            if true, the method should return an event in case of success
	 * @param staticPeripheral
	 *            <code>true</code> when the peripheral is available on startup
	 * @return the created RegistrationEvent, if any, or null
	 * @throws IllegalArgumentException
	 *             if the peripheral has already been registered
	 */
	<P extends Peripheral> RegistrationEvent<P> register(Class<P> peripheralType, P peripheral, boolean createEvent,
			boolean staticPeripheral);

	/**
	 * Unregisters the given peripheral. Some peripherals are registered by the
	 * underlying platform and cannot be unregistered.
	 *
	 * @param <P>
	 *            the type of the peripheral to be unregistered
	 * @param peripheralType
	 *            the type of the peripheral to be unregistered
	 * @param peripheral
	 *            the peripheral to be unregistered
	 * @param createEvent
	 *            if true, the method should return an event in case of success
	 * @return the created RegistrationEvent, if any, or null
	 */
	<P extends Peripheral> RegistrationEvent<P> unregister(Class<P> peripheralType, P peripheral, boolean createEvent);

	/**
	 * Executes the registration event.
	 *
	 * @param pump
	 *            the pump which execute the registration event
	 * @param data
	 *            the registration event to execute
	 */
	public <P extends Peripheral> void executeEvent(Pump<RegistrationEvent<?>> pump, RegistrationEvent<P> data);

	/**
	 * Retrieves the class used to register the given peripheral.
	 *
	 * @param peripheral
	 *            the peripheral
	 * @return Class of the registered peripheral or null if the peripheral is
	 *         not registered
	 */
	public <C extends Peripheral, P extends C> Class<C> getRegisteredClass(P peripheral);
}
