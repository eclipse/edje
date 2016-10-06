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

package org.eclipse.edje.test;

import java.util.HashMap;

import org.eclipse.edje.PeripheralManager;
import org.eclipse.edje.test.peripherals.CommPort;
import org.eclipse.edje.test.peripherals.UART;
import org.eclipse.edje.test.peripherals.UsbPeripheral;
import org.eclipse.edje.test.support.Listener;
import org.eclipse.edje.test.support.SynchroSupport;
import org.eclipse.edje.test.support.Util;
import org.junit.Assert;
import org.junit.Test;

public class TestPeripheralManagerDynamic01 {

	public static Class<?> clazz = TestPeripheralManagerDynamic01.class;

	public static final int STATE_LISTENER_PERIPHERALREGISTERED_WAIT = 1;
	public static final int STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT = 2;

	@Test
	public void testListener() {
		final UART uart1 = new UART("com1", new HashMap<String, String>());
		Listener<CommPort> l = new Listener<>(new CommPort[] { uart1 }, STATE_LISTENER_PERIPHERALREGISTERED_WAIT,
				STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT);
		PeripheralManager.addRegistrationListener(l, CommPort.class);

		PeripheralManager.register(CommPort.class, uart1);
		SynchroSupport.waitState(STATE_LISTENER_PERIPHERALREGISTERED_WAIT);

		PeripheralManager.unregister(uart1);
		SynchroSupport.waitState(STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT);

		PeripheralManager.removeRegistrationListener(l);
	}

	@Test
	public void testListenerFilter() {
		final UART uart1 = new UART("com1", new HashMap<String, String>());
		final UART uart2 = new UART("com2", new HashMap<String, String>());
		final UsbPeripheral usb1 = new UsbPeripheral("usb1", new HashMap<String, String>());

		{
			// Listener on CommPort : get the 3 peripherals
			Listener<CommPort> lCommPort = new Listener<>(new CommPort[] { uart1, uart2, usb1 },
					STATE_LISTENER_PERIPHERALREGISTERED_WAIT, STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT);
			PeripheralManager.addRegistrationListener(lCommPort, CommPort.class);
			PeripheralManager.register(CommPort.class, uart1);
			PeripheralManager.register(UART.class, uart2);
			PeripheralManager.register(UsbPeripheral.class, usb1);
			SynchroSupport.waitState(STATE_LISTENER_PERIPHERALREGISTERED_WAIT, 3);
			Assert.assertTrue("ListCommPort",
					Util.equals(Util.toArray(PeripheralManager.list(CommPort.class)), lCommPort.expectedPeripherals));

			PeripheralManager.unregister(uart1);
			PeripheralManager.unregister(uart2);
			PeripheralManager.unregister(usb1);
			SynchroSupport.waitState(STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT, 3);
			PeripheralManager.removeRegistrationListener(lCommPort);
		}
		waitFlush(); // ensure the pump has finished before adding a new
						// listener - otherwise the previous event will be
						// dispatched to the unexpected listener
		{
			Listener<UART> lUART = new Listener<>(new UART[] { uart2 }, STATE_LISTENER_PERIPHERALREGISTERED_WAIT,
					STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT);
			PeripheralManager.addRegistrationListener(lUART, UART.class);
			PeripheralManager.register(CommPort.class, uart1);
			PeripheralManager.register(UART.class, uart2);
			PeripheralManager.register(UsbPeripheral.class, usb1);
			SynchroSupport.waitState(STATE_LISTENER_PERIPHERALREGISTERED_WAIT, 1);
			Assert.assertTrue("ListUART",
					Util.equals(Util.toArray(PeripheralManager.list(UART.class)), lUART.expectedPeripherals));

			PeripheralManager.unregister(uart1);
			PeripheralManager.unregister(uart2);
			PeripheralManager.unregister(usb1);
			SynchroSupport.waitState(STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT, 1);
			PeripheralManager.removeRegistrationListener(lUART);
		}
		waitFlush(); // ensure the pump has finished before adding a new
						// listener - otherwise the previous event will be
						// dispatched to the unexpected listener
		{
			Listener<UsbPeripheral> lUsbPeripheral = new Listener<>(new UsbPeripheral[] { usb1 },
					STATE_LISTENER_PERIPHERALREGISTERED_WAIT, STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT);
			PeripheralManager.addRegistrationListener(lUsbPeripheral, UsbPeripheral.class);
			PeripheralManager.register(CommPort.class, uart1);
			PeripheralManager.register(UART.class, uart2);
			PeripheralManager.register(UsbPeripheral.class, usb1);
			SynchroSupport.waitState(STATE_LISTENER_PERIPHERALREGISTERED_WAIT, 1);
			Assert.assertTrue("ListUsbPeripheral", Util.equals(
					Util.toArray(PeripheralManager.list(UsbPeripheral.class)), lUsbPeripheral.expectedPeripherals));

			PeripheralManager.unregister(uart1);
			PeripheralManager.unregister(uart2);
			PeripheralManager.unregister(usb1);
			SynchroSupport.waitState(STATE_LISTENER_PERIPHERALUNREGISTERED_WAIT, 1);
			PeripheralManager.removeRegistrationListener(lUsbPeripheral);
		}
	}

	private static void waitFlush() {
		// There is currently no callSerially to ensure the Pump is flushed
		// Consider it is flushed after a 1s delay
		SynchroSupport.sleep(1000);
	}
}