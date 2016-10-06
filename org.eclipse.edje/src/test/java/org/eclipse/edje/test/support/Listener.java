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

package org.eclipse.edje.test.support;

import org.eclipse.edje.Peripheral;
import org.eclipse.edje.RegistrationEvent;
import org.eclipse.edje.RegistrationListener;
import org.junit.Assert;

public class Listener<D extends Peripheral> implements RegistrationListener<D> {

	public Peripheral[] expectedPeripherals;

	private int expectedPeripheralsRegisteredPtr;
	private int expectedPeripheralsUnregisteredPtr;

	private final int stateRegistered;
	private final int stateUnregistered;

	/**
	 * @param expectedPeripherals
	 *            expected registered/unregistered peripherals in the given
	 *            order
	 */
	public Listener(Peripheral[] expectedPeripherals, int stateRegistered, int stateUnregistered) {
		this.expectedPeripherals = expectedPeripherals;
		this.stateRegistered = stateRegistered;
		this.stateUnregistered = stateUnregistered;
		this.expectedPeripheralsRegisteredPtr = -1;
		this.expectedPeripheralsUnregisteredPtr = -1;
	}

	@Override
	public void peripheralRegistered(RegistrationEvent<D> event) {
		check(event, "Registered", expectedPeripherals, ++expectedPeripheralsRegisteredPtr);
		SynchroSupport.notifyState(stateRegistered);
	}

	private void check(RegistrationEvent<D> event, String string, Peripheral[] expectedPeripherals, int ptr) {
		if (ptr >= expectedPeripherals.length) {
			// unexpected callback
			Assert.assertTrue("ListenerPeripheral" + string + "-unexpected", false);
		}
		Assert.assertEquals("ListenerPeripheral" + string, event.getPeripheral(), expectedPeripherals[ptr]);
	}

	@Override
	public void peripheralUnregistered(RegistrationEvent<D> event) {
		check(event, "Unregistered", expectedPeripherals, ++expectedPeripheralsUnregisteredPtr);
		SynchroSupport.notifyState(stateUnregistered);
	}

}