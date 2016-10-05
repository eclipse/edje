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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	public static Class<TestPeripheralManagerPermission01> clazz = TestPeripheralManagerPermission01.class;

	public static void TestDisallowRead(UART uart1) {
		checkNotInList(CommPort.class, uart1);
		checkNotInList(UART.class, uart1);
		checkRegisterListener(CommPort.class, new NullRegistrationListener<CommPort>(), true);
		checkRegisterListener(UART.class, new NullRegistrationListener<UART>(), true);
		checkRegisterUnregister(CommPort.class, uart1);
		checkRegister(UART.class, uart1, true);
		checkNotInList(CommPort.class, uart1);
		checkNotInList(UART.class, uart1);
		checkUnregister(uart1, true);
	}

	public static void TestDisallowModify(UART uart1) {
		checkNotInList(CommPort.class, uart1);
		checkNotInList(UART.class, uart1);
		checkRegisterListener(CommPort.class, new NullRegistrationListener<CommPort>(), true);
		checkRegisterListener(UART.class, new NullRegistrationListener<UART>(), true);
		checkRegisterUnregister(CommPort.class, uart1);
		checkRegister(UART.class, uart1, false);
		checkNotInList(UART.class, uart1);
	}

	@Test
	public void testReadPermission() {
		UART uart1 = new UART("com1", new HashMap<String, String>());
		HashMap<String, String> constraints = new HashMap<>();
		constraints.put("name", "*1");
		constraints.put("class", "org.eclipse.edje.test.peripherals.Comm*");
		System.setSecurityManager(
				new TestSecurityManager(
						new PeripheralManagerPermission[] {
								new PeripheralManagerPermission(constraints, PeripheralManagerPermission.READ_MODIFY),
								new PeripheralManagerPermission(
										"name=com1,class=org.eclipse.edje.test.peripherals.UART",
										PeripheralManagerPermission.MODIFY) }));
		TestDisallowRead(uart1);

		System.setSecurityManager(new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission(buildSpec("com1", CommPort.class),
						PeripheralManagerPermission.READ_MODIFY),
				new PeripheralManagerPermission(buildSpec("com1", UART.class), PeripheralManagerPermission.READ) }));
		TestDisallowModify(uart1);

		// Check unregister
		// modify the permission to allow register
		SecurityManager previous = System.getSecurityManager();
		System.setSecurityManager(null);
		checkRegister(UART.class, uart1, true); // register
		// put back the permission to check unregister is disallowed
		System.setSecurityManager(previous);
		checkUnregister(uart1, false);

		// final unregister
		System.setSecurityManager(null);
		checkUnregister(uart1, true);
	}

	/**
	 * @param string
	 * @param name
	 * @param object
	 * @return
	 */
	private static String buildSpec(String name, Class<? extends Peripheral> type) {
		return "name=" + name + ",class=" + type.getName();
	}

	public static <D extends Peripheral> void checkRegisterUnregister(Class<D> c, D peripheral) {
		checkRegister(c, peripheral, true);
		checkInList(c, peripheral);
		checkUnregister(peripheral, true);
		checkNotInList(c, peripheral);
	}

	public static <D extends Peripheral> void checkRegisterListener(Class<D> class1, RegistrationListener<D> listener,
			boolean expectedSuccess) {
		try {
			PeripheralManager.addRegistrationListener(listener, class1);
			Assert.assertTrue("Register-listener-for-" + class1.getSimpleName() + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue("Register-listener-for-" + class1.getSimpleName() + "-EXC", !expectedSuccess);
		}
		PeripheralManager.removeRegistrationListener(listener);
	}

	public static void checkUnregister(Peripheral d, boolean expectedSuccess) {
		try {
			PeripheralManager.unregister(d);
			Assert.assertTrue("Unregister-" + d.getName() + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue("Unregister-" + d.hashCode() + "-EXC", !expectedSuccess);
		}
	}

	public static <D extends Peripheral> void checkRegister(Class<D> class1, D d, boolean expectedSuccess) {
		try {
			PeripheralManager.register(class1, d);
			Assert.assertTrue("Register-as-" + class1.getSimpleName() + "-DEF", expectedSuccess);
		} catch (SecurityException e) {
			Assert.assertTrue("Register-as-" + class1.getSimpleName() + "-EXC", !expectedSuccess);
		}
	}

	public static <C extends Peripheral, P extends C> void checkInList(Class<C> class1, P expected) {
		Iterator<C> list = PeripheralManager.list(class1);
		while (list.hasNext()) {
			C p = list.next();
			if (p == expected) {
				Assert.assertTrue("checkInList", true);
				return;
			}
		}
		Assert.assertTrue("checkInList", false);
	}

	public static <C extends Peripheral, P extends C> void checkNotInList(Class<C> class1, P expected) {
		Iterator<C> list = PeripheralManager.list(class1);
		while (list.hasNext()) {
			C p = list.next();
			if (p == expected) {
				Assert.assertTrue("checkNotInList", false);
				return;
			}
		}
		Assert.assertTrue("checkNotInList", true);
	}

	static class TestSecurityManager extends SecurityManager {

		List<PeripheralManagerPermission> policy = new ArrayList<>();

		TestSecurityManager(PeripheralManagerPermission permission) {
			policy.add(permission);
		}

		TestSecurityManager(PeripheralManagerPermission[] permissions) {
			for (PeripheralManagerPermission p : permissions) {
				policy.add(p);
			}
		}

		@Override
		public void checkPermission(Permission perm) {
			// care only for PeripheralManagerPermissions
			if (perm instanceof PeripheralManagerPermission) {
				for (PeripheralManagerPermission p : policy) {
					if (p.implies(perm)) {
						return;
					}
				}
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
