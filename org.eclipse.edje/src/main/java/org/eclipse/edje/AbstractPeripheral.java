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
 * Default abstract implementation of {@link Peripheral}.
 */
public abstract class AbstractPeripheral implements Peripheral {

	/**
	 * Registering state. Not registered on peripheral creation.
	 */
	private boolean isRegistered;

	/**
	 * Returns the registering state.
	 * 
	 * @return <code>true</code> when the peripheral is registered.
	 */
	protected synchronized boolean isRegistered() {
		return isRegistered;
	}

	/**
	 * Registers the peripheral in the pool. {@link #isRegistered()} will return
	 * <code>true</code> after calling this method.
	 * 
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 */
	protected synchronized <P extends Peripheral> void register(Class<P> peripheralType) {
		register(peripheralType, false);
	}

	/**
	 * Registers the peripheral in the pool. {@link #isRegistered()} will return
	 * <code>true</code> after calling this method.
	 * 
	 * @param peripheralType
	 *            the type of the peripheral to be registered
	 * @param staticPeripheral
	 *            <true> to register a non-dynamic peripheral (available on
	 *            startup)
	 */
	protected synchronized <P extends Peripheral> void register(Class<P> peripheralType, boolean staticPeripheral) {
		PeripheralManager.register(peripheralType, (P) this, staticPeripheral);
		isRegistered = true;
	}

	/**
	 * Unregisters the peripheral from the pool. {@link #isRegistered()} will
	 * return <code>false</code> after calling this method.
	 */
	protected synchronized void unregister() {
		PeripheralManager.unregister(this);
		isRegistered = false;
	}
}
