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

import java.security.Permission;

/**
 * This class represents access rights to the peripheral resources.
 *
 * @param <P>
 *            the type of the described peripheral
 */
@SuppressWarnings("serial")
public class PeripheralManagerPermission<P extends Peripheral> extends Permission {

	/**
	 * The {@link #READ} permission is required to access to registered
	 * peripherals.
	 */
	public static final String READ = "read";

	/**
	 * The {@link #MODIFY} permission is required to be able to modify the
	 * registry.
	 */
	public static final String MODIFY = "modify";

	/**
	 * The peripheral class on which the permission is applied.
	 */
	private final Class<P> peripheralClass;

	/**
	 * Creates a {@link PeripheralManager} permission with the specified name
	 * and peripheral class.
	 *
	 * @param name
	 *            either {@link #READ} or {@link #MODIFY} permission name
	 * @param peripheralClass
	 *            the peripheral class on which the permission is applied
	 */
	public PeripheralManagerPermission(String name, Class<P> peripheralClass) {
		super(name);
		this.peripheralClass = peripheralClass;
	}

	/**
	 * Returns the peripheral class on which this permission is required.
	 * 
	 * @return the peripheral class
	 */
	public Class<P> getPeripheralClass() {
		return peripheralClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		PeripheralManagerPermission<?> objPerm = (PeripheralManagerPermission<?>) obj;
		return getName().equals(objPerm.getName()) && peripheralClass.equals(objPerm.peripheralClass);
	}

	@Override
	public String getActions() {
		return "";
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + peripheralClass.hashCode();
	}

	@Override
	public boolean implies(Permission permission) {
		return equals(permission);
	}

}
