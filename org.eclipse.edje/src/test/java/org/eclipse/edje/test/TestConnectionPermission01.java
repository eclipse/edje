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
import org.junit.Assert;
import org.junit.Test;

public class TestConnectionPermission01 {
	public static Class<TestConnectionPermission01> clazz = TestConnectionPermission01.class;

	/**
	 * Ensure the class is loaded.
	 */
	private static Class<ConnectionFactory> REQUIRE_CLASS = ConnectionFactory.class;

	@Test
	public void testPermissions() {
		checkOpen(true);
		System.setSecurityManager(new SecurityManager() {
			@Override
			public void checkPermission(Permission perm) {
				if (perm instanceof ConnectionPermission) {
					ConnectionPermission cp = (ConnectionPermission) perm;
					if (cp.getName().startsWith("xxx:")) {
						throw new SecurityException();
					}
				}
			}
		});
		checkOpen(false);
		System.setSecurityManager(null);
		checkOpen(true);
	}

	private static void checkOpen(boolean expectedSuccess) {
		try (Connection c = Connector.open(clazz.getPackage().getName() + ".connection", "xxx:name")) {
			Util.check("checkOpenXXX-OK", c != null, expectedSuccess);
		} catch (IOException e) {
			Assert.assertTrue("checkOpenXXX-IOE", false);
		} catch (SecurityException e) {
			Assert.assertTrue("checkOpenXXX-EXC", !expectedSuccess);
		}
	}
}
