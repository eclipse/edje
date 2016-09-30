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

/**
 * A {@link RegistrationEvent} is created as soon as a {@link Peripheral} is
 * registered or unregistered in the {@link Peripheral} pool. This event is sent
 * to the {@link RegistrationListener}.
 * 
 * @param <P>
 *            the type of the peripheral
 * 
 * @see RegistrationListener
 */
public class RegistrationEvent<P extends Peripheral> {

	/**
	 * The type of the peripheral.
	 */
	private final P peripheral;

	/**
	 * The class of the registered peripheral.
	 */
	Class<P> registeredClass;

	/**
	 * <code>true</code> for register event, <code>false</code> for unregister
	 * event.
	 */
	boolean add;

	/**
	 * The registry instance which has created this event.
	 */
	PeripheralRegistry registry;

	/**
	 * Creates a new {@link RegistrationEvent} with the specified peripheral.
	 * 
	 * @param registry
	 *            the registry instance which has created this event
	 * 
	 * @param peripheral
	 *            the registered or unregistered peripheral
	 * @param registeredClass
	 *            the class used to register the peripheral
	 * @param add
	 *            <code>true</code> for register event, <code>false</code> for
	 *            unregister event
	 */
	RegistrationEvent(PeripheralRegistry registry, P peripheral, Class<P> registeredClass, boolean add) {
		this.registry = registry;
		this.peripheral = peripheral;
		this.registeredClass = registeredClass;
		this.add = add;
	}

	/**
	 * Returns the registered or unregistered peripheral.
	 */
	public P getPeripheral() {
		return peripheral;
	}

}
