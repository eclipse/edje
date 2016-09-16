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

import org.eclipse.edje.Peripheral;
import org.eclipse.edje.PeripheralManager;
import org.eclipse.edje.comm.CommPort;
import org.eclipse.edje.test.peripherals.UART;
import org.eclipse.edje.test.peripherals.UsbPeripheral;
import org.eclipse.edje.test.support.Util;
import org.junit.Assert;

/**
 * Static tests
 */
public class TestPeripheralManagerStatic01 {

	static Class<TestPeripheralManagerStatic01> clazz = TestPeripheralManagerStatic01.class;

	public static void main(String[] args) {
		testAddRemoveList();
		System.out.println("test done without error");
	}

	private static void testAddRemoveList() {
		Peripheral[] peripheralsBefore = Util.toArray(PeripheralManager.list());
		Assert.assertTrue("ListCommPort01", Util.isEmpty(Util.toArray(PeripheralManager.list(CommPort.class))));
		Assert.assertTrue("ListUART01", Util.isEmpty(Util.toArray(PeripheralManager.list(UART.class))));
		Assert.assertTrue("ListUsbPeripheral01",
				Util.isEmpty(Util.toArray(PeripheralManager.list(UsbPeripheral.class))));

		// register uart1 as a CommPort
		CommPort uart1 = new UART("com1", new HashMap<String, String>());
		PeripheralManager.register(CommPort.class, uart1);

		// register again on the same class => error
		try {
			PeripheralManager.register(CommPort.class, uart1);
			Assert.assertTrue("RegisterAgainCommPort-KO", false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue("RegisterAgainCommPort-EXC", true);
		}
		// register again on an other class => error
		try {
			PeripheralManager.register(UART.class, (UART) uart1);
			Assert.assertTrue("RegisterAgainCommPort-KO", false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue("RegisterAgainCommPort-EXC", true);
		}

		Peripheral[] peripheralAfter = Util.toArray(PeripheralManager.list());
		Assert.assertTrue("AddRemove01", Util.isIncluded(peripheralsBefore, peripheralAfter));
		Assert.assertTrue("AddRemove02", Util.isIncluded(uart1, peripheralAfter));

		Assert.assertTrue("ListCommPort02", Util.equals(Util.toArray(PeripheralManager.list(CommPort.class)), uart1));
		Assert.assertTrue("ListUART02", Util.isEmpty(Util.toArray(PeripheralManager.list(UART.class))));
		Assert.assertTrue("ListUsbPeripheral02",
				Util.isEmpty(Util.toArray(PeripheralManager.list(UsbPeripheral.class))));

		// register uart2 as an UART (which is also a CommPort)
		UART uart2 = new UART("com1", new HashMap<String, String>());
		PeripheralManager.register(UART.class, uart2);
		Peripheral[] uart1Anduart2 = new Peripheral[] { uart1, uart2 };

		Peripheral[] peripheralAfter2 = Util.toArray(PeripheralManager.list());
		Assert.assertTrue("AddRemove01-2", Util.isIncluded(peripheralsBefore, peripheralAfter2));
		Assert.assertTrue("AddRemove02-2", Util.isIncluded(uart1Anduart2, peripheralAfter2));

		Assert.assertTrue("ListCommPort02-2",
				Util.equals(Util.toArray(PeripheralManager.list(CommPort.class)), uart1Anduart2));
		Assert.assertTrue("ListUART02-2", Util.equals(Util.toArray(PeripheralManager.list(UART.class)), uart2)); // only
		// uart2
		// has
		// been
		// registered
		// as
		// an
		// UART
		Assert.assertTrue("ListUsbPeripheral02-2",
				Util.isEmpty(Util.toArray(PeripheralManager.list(UsbPeripheral.class))));

		PeripheralManager.unregister(uart1);
		PeripheralManager.unregister(uart2);
		Peripheral[] peripheralAfterRemove = Util.toArray(PeripheralManager.list());
		Assert.assertTrue("AfterRemove01", Util.equals(peripheralsBefore, peripheralAfterRemove));

		Assert.assertTrue("ListCommPort03", Util.isEmpty(Util.toArray(PeripheralManager.list(CommPort.class))));
		Assert.assertTrue("ListUART03", Util.isEmpty(Util.toArray(PeripheralManager.list(UART.class))));
		Assert.assertTrue("ListUsbPeripheral03",
				Util.isEmpty(Util.toArray(PeripheralManager.list(UsbPeripheral.class))));
	}

}