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
import org.eclipse.edje.test.peripherals.UART;
import org.junit.Assert;
import org.junit.Test;

public class TestPeripheralManagerPermission01 {
	public static Class<TestPeripheralManagerPermission01> clazz = TestPeripheralManagerPermission01.class;

	@Test
	public void testModify() {
		UART uart1 = new UART("com1", new HashMap<String, String>());
		checkNotInList(UART.class, uart1);

		SecurityManager allowed = new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission("name=com1,class=org.eclipse.edje.test.peripherals.UART",
						PeripheralManagerPermission.READ_MODIFY) });
		SecurityManager read_only = new TestSecurityManager(
				new PeripheralManagerPermission[] { new PeripheralManagerPermission(
						"name=com1,class=org.eclipse.edje.test.peripherals.UART", PeripheralManagerPermission.READ) });

		System.setSecurityManager(allowed);
		checkRegister(UART.class, uart1, true);
		checkInList(UART.class, uart1);

		System.setSecurityManager(read_only);
		checkUnregister(uart1, false);
		checkInList(UART.class, uart1);

		System.setSecurityManager(allowed);
		checkUnregister(uart1, true);
		checkNotInList(UART.class, uart1);
	}

	@Test
	public void testRead() {
		final UART uart1 = new UART("com1", new HashMap<String, String>());
		checkNotInList(UART.class, uart1);

		SecurityManager modify_only = new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission("name=com1,class=org.eclipse.edje.test.peripherals.UART",
						PeripheralManagerPermission.MODIFY) });
		SecurityManager read_only = new TestSecurityManager(
				new PeripheralManagerPermission[] { new PeripheralManagerPermission(
						"name=com1,class=org.eclipse.edje.test.peripherals.UART", PeripheralManagerPermission.READ) });
		SecurityManager both = new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission("name=com1,class=org.eclipse.edje.test.peripherals.UART",
						PeripheralManagerPermission.READ_MODIFY) });

		System.setSecurityManager(modify_only);
		TestRegistrationListener<UART> listener = new TestRegistrationListener<>(uart1);
		PeripheralManager.addRegistrationListener(listener, UART.class);
		checkRegister(UART.class, uart1, true);
		checkNotInList(UART.class, uart1);
		synchronized (listener) {
			if (listener.event == null) {
				try {
					listener.wait(5000);
				} catch (InterruptedException e) {
				}
			}
			// because we don't have read access
			Assert.assertTrue(listener.event == null);
		}

		System.setSecurityManager(read_only);
		checkInList(UART.class, uart1);

		System.setSecurityManager(both);
		checkUnregister(uart1, true);
		synchronized (listener) {
			if (listener.event == null) {
				try {
					listener.wait(5000);
				} catch (InterruptedException e) {
				}
			}
			// true because we have both read & modify access
			Assert.assertTrue(listener.event != null);
			Assert.assertTrue(listener.event.getPeripheral().equals(uart1));
			Assert.assertTrue(listener.event.isRegistration() == false);
		}

		System.setSecurityManager(read_only);
		checkNotInList(UART.class, uart1);
		PeripheralManager.removeRegistrationListener(listener);
	}

	@Test
	public void testWildcard() {
		UART uart1 = new UART("com1", new HashMap<String, String>());
		UART uart2 = new UART("com2", new HashMap<String, String>());
		checkNotInList(UART.class, uart1);
		checkNotInList(UART.class, uart2);

		SecurityManager any = new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission("name=com*,class=org.eclipse.edje.test.peripherals.UART",
						PeripheralManagerPermission.READ_MODIFY) });
		SecurityManager only1 = new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission("name=com1,class=org.eclipse.edje.test.peripherals.UART",
						PeripheralManagerPermission.READ_MODIFY) });
		SecurityManager only2 = new TestSecurityManager(new PeripheralManagerPermission[] {
				new PeripheralManagerPermission("name=*2,class=org.eclipse.edje.test.peripherals.UART",
						PeripheralManagerPermission.READ_MODIFY) });

		System.setSecurityManager(any);
		checkRegister(UART.class, uart1, true);
		checkRegister(UART.class, uart2, true);
		checkInList(UART.class, uart1);
		checkInList(UART.class, uart2);

		System.setSecurityManager(only1);
		checkInList(UART.class, uart1);
		checkNotInList(UART.class, uart2);

		System.setSecurityManager(only2);
		checkUnregister(uart2, true);
		checkNotInList(UART.class, uart2);
		checkNotInList(UART.class, uart1);

		System.setSecurityManager(only1);
		checkUnregister(uart1, true);
		checkNotInList(UART.class, uart1);
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

	public static class TestRegistrationListener<P extends Peripheral> implements RegistrationListener<P> {

		private final P peripheral;
		private RegistrationEvent<P> event;

		public TestRegistrationListener(P peripheral) {
			super();
			this.peripheral = peripheral;
		}

		@Override
		public void peripheralRegistered(RegistrationEvent<P> event) {
			if (event.getPeripheral().equals(peripheral)) {
				synchronized (this) {
					this.event = event;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					this.notifyAll();
				}
			}
		}

		@Override
		public void peripheralUnregistered(RegistrationEvent<P> event) {
			if (event.getPeripheral().equals(peripheral)) {
				synchronized (this) {
					this.event = event;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					this.notifyAll();
				}
			}
		}

		/**
		 * Gets the event.
		 *
		 * @return the event.
		 */
		public RegistrationEvent<P> getEvent() {
			return event;
		}

	}
}
