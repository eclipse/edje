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

import java.io.IOException;
import java.security.Permission;

import org.eclipse.edje.io.Connection;
import org.eclipse.edje.io.ConnectionFactory;
import org.eclipse.edje.io.ConnectionPermission;
import org.eclipse.edje.io.Connector;
import org.eclipse.edje.test.support.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TestConnectionPermission01 {
	/**
	 * Ensure the class are loaded to avoid infinite recursion in the security
	 * manager
	 */
	@SuppressWarnings("unused")
	private static Class<?> REQUIRED_CLASSES[] = { ConnectionFactory.class, ConnectionPermission.class };

	@After
	public void reset() {
		System.setSecurityManager(null);
	}

	@Test
	public void testOpen() {
		checkOpen(true);
	}

	@Test
	public void testOpenNoPermission() {
		// puts on a security manager that allows only access to the "custom:"
		// protocol
		System.setSecurityManager(new SecurityManager() {
			@Override
			public void checkPermission(Permission perm) {
				if (perm instanceof ConnectionPermission) {
					ConnectionPermission cp = (ConnectionPermission) perm;
					if (cp.getName().startsWith("custom:")) {
						throw new SecurityException();
					}
				}
			}
		});
		checkOpen(false);
	}

	private void checkOpen(boolean expectedSuccess) {
		try (Connection c = Connector.open(this.getClass().getPackage().getName() + ".connection", "custom:name")) {
			Util.check("checkOpenCustom-OK", c != null, expectedSuccess);
		} catch (IOException e) {
			Assert.assertTrue("checkOpenCustom-IOE", false);
		} catch (SecurityException e) {
			Assert.assertTrue("checkOpenCustom-EXC", !expectedSuccess);
		}
	}
}
