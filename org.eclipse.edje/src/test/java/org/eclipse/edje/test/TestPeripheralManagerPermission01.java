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

import java.security.Permission;
import java.util.HashMap;

import org.eclipse.edje.Peripheral;
import org.eclipse.edje.PeripheralManager;
import org.eclipse.edje.PeripheralManagerPermission;
import org.eclipse.edje.RegistrationEvent;
import org.eclipse.edje.RegistrationListener;
import org.eclipse.edje.test.peripherals.CommPort;
import org.eclipse.edje.test.peripherals.UART;
import org.junit.Assert;
import org.junit.Test;

public class TestPeripheralManagerPermission01 {
	public static Class clazz = TestPeripheralManagerPermission01.class;

	public static void TestDisallowRead(UART uart1) {
		checkList("ListCommPort", CommPort.class, true);
		checkList("ListUART", UART.class, false);
		checkRegisterListener("RegisterListenerCommPort", CommPort.class, new NullRegistrationListener<CommPort>(),
				true);
		checkRegisterListener("RegisterListenerUART", UART.class, new NullRegistrationListener<UART>(), false);
		checkRegisterUnregister("AsCommPort", CommPort.class, uart1);
		checkRegisterUnregister("AsUART", UART.class, uart1);
	}

	public static void TestDisallowModify(UART uart1) {
		checkList("ListCommPort", CommPort.class, true);
		checkList("ListUART", UART.class, true);
		checkRegisterListener("RegisterListenerCommPort", CommPort.class, new NullRegistrationListener<CommPort>(),
				true);
		checkRegisterListener("RegisterListenerUART", UART.class, new NullRegistrationListener<UART>(), true);
		checkRegisterUnregister("AsCommPort", CommPort.class, uart1);
		checkRegister("AsUART", UART.class, uart1, false);
	}

	@Test
	public void testReadPermission() {
		UART uart1 = new UART("com1", new HashMap<String, String>());
		System.setSecurityManager(new SecurityManagerDisallowReadUART());
		TestDisallowRead(uart1);

		System.setSecurityManager(new SecurityManagerDisallowModifyUART());
		TestDisallowModify(uart1);

		// Check unregister
		// modify the permission to allow register
		SecurityManager previous = System.getSecurityManager();
		System.setSecurityManager(null);
		checkRegister("AsUART", UART.class, uart1, true); // register
		// put back the permission to check unregister is disallowed
		System.setSecurityManager(previous);
		checkUnregister("AsUART", UART.class, uart1, false);

		// final unregister
		System.setSecurityManager(null);
		checkUnregister("AsUART", UART.class, uart1, true);
	}

	public static <D extends Peripheral> void checkRegisterUnregister(String string, Class<D> c, D peripheral) {
		checkRegister(string, c, peripheral, true);
		checkUnregister(string, c, peripheral, true);
	}

	public static <D extends Peripheral> void checkRegisterListener(String test, Class<D> class1,
			RegistrationListener<D> listener, boolean expectedSuccess) {
		try {
			PeripheralManager.addRegistrationListener(listener, class1);
			Assert.assertTrue(test + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue(test + "-EXC", !expectedSuccess);
		}
		PeripheralManager.removeRegistrationListener(listener);
	}

	public static <D extends Peripheral> void checkUnregister(String string, Class<D> class1, D d,
			boolean expectedSuccess) {
		try {
			PeripheralManager.unregister(d);
			Assert.assertTrue("Unregister" + string + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue("Unregister" + string + "-EXC", !expectedSuccess);
		}
	}

	public static <D extends Peripheral> void checkRegister(String string, Class<D> class1, D d,
			boolean expectedSuccess) {
		try {
			PeripheralManager.register(class1, d);
			Assert.assertTrue("Register" + string + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue("Register" + string + "-EXC", !expectedSuccess);
		}
	}

	public static <D extends Peripheral> void checkList(String string, Class<D> class1, boolean expectedSuccess) {
		try {
			PeripheralManager.list(class1);
			Assert.assertTrue(string + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue(string + "-EXC", !expectedSuccess);
		}
	}

	public static class SecurityManagerDisallowReadUART extends SecurityManagerDisallowUART {
		@Override
		String getPermissionName() {
			return PeripheralManagerPermission.READ;
		}
	}

	public static class SecurityManagerDisallowModifyUART extends SecurityManagerDisallowUART {
		@Override
		String getPermissionName() {
			return PeripheralManagerPermission.MODIFY;
		}
	}

	static abstract class SecurityManagerDisallowUART extends SecurityManager {
		@Override
		public void checkPermission(Permission perm) {
			PeripheralManagerPermission<? extends Peripheral> p;
			try {
				p = (PeripheralManagerPermission<? extends Peripheral>) perm;
			} catch (ClassCastException e) {
				return;
			}
			if (perm.getName() == getPermissionName()) {
				if (UART.class.isAssignableFrom(p.getPeripheralClass())) {
					throw new SecurityException();
				}
			}
		}

		abstract String getPermissionName();
	}

	public static class SecurityManagerDisallowALL extends SecurityManager {
		private boolean disable;

		public void disable() {
			this.disable = true;
		}

		@Override
		public void checkPermission(Permission perm) {
			if (!disable) {
				throw new SecurityException();
			}
		}
	}

	public static class NullRegistrationListener<D extends Peripheral> implements RegistrationListener<D> {

		@Override
		public void peripheralRegistered(RegistrationEvent<D> event) {
		}

		@Override
		public void peripheralUnregistered(RegistrationEvent<D> event) {
		}

	}
}
