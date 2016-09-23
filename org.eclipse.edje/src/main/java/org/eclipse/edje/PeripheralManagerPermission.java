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

import java.security.BasicPermission;
import java.security.Permission;

/**
 * This class represents access rights to the peripheral resources.
 *
 * @param <P>
 *            the type of the described peripheral
 */
@SuppressWarnings("serial")
public class PeripheralManagerPermission<P extends Peripheral> extends BasicPermission {

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
	 * The c{@link #READ_MODIFY} permission is a convenience shortcut for giving
	 * (or testing for) both permissions.
	 */
	public static final String READ_MODIFY = "read,modify";

	/**
	 * The peripheral class on which the permission is applied.
	 */
	private final Class<P> peripheralClass;

	private final String actions;

	/**
	 * Creates a {@link PeripheralManager} permission with the specified name
	 * and action.<br>
	 * The purpose of this constructor is mostly for the permission-checking
	 * code.
	 *
	 * @param peripheralClass
	 *            the peripheral class on which the permission is applied
	 * @param action
	 *            the action to verify, either {@link #READ} or {@link #MODIFY}
	 *            permission name
	 */
	public PeripheralManagerPermission(Class<P> peripheralClass, String action) {
		super(peripheralClass.getName());
		this.peripheralClass = peripheralClass;
		this.actions = buildActions(action);
	}

	/**
	 * @param action
	 * @return
	 */
	private static String buildActions(String actions) {
		if (READ.equals(actions)) {
			return READ;
		}
		if (MODIFY.equals(actions)) {
			return MODIFY;
		}
		if (READ_MODIFY.equals(actions)) {
			return READ_MODIFY;
		}

		// switch to lowercase, for comparisons
		String tmp = actions.toLowerCase();
		boolean hasREAD = tmp.contains(READ);
		boolean hasMODIFY = tmp.contains(MODIFY);

		if (hasREAD) {
			tmp = tmp.replace(READ, "");
		}
		if (hasMODIFY) {
			tmp = tmp.replace(MODIFY, "");
		}

		// it cannot have anything else than spaces and commas, then
		tmp = tmp.replace(',', ' ').trim();
		if (!tmp.isEmpty()) {
			throw new IllegalArgumentException("Bad permission action format: " + actions);
		}
		if (hasREAD && hasMODIFY) {
			return READ_MODIFY;
		}
		if (hasREAD) {
			return READ;
		}
		if (hasMODIFY) {
			return MODIFY;
		}
		throw new IllegalArgumentException("Bad permission action format: " + actions);
	}

	/**
	 * Creates a {@link PeripheralManager} permission with the specified name
	 * and peripheral class.
	 *
	 * @param name
	 *            the name peripheral class on which the permission is applied
	 * @param actions
	 *            either {@link #READ} or {@link #MODIFY} permission name, or a
	 *            combination of the two
	 */
	@SuppressWarnings("unchecked")
	public PeripheralManagerPermission(String name, String actions) {
		super(name);
		Class<P> c;
		try {
			c = (Class<P>) Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		this.peripheralClass = c;
		this.actions = buildActions(actions);
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
		if (obj == this) {
			return true;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		PeripheralManagerPermission<?> objPerm = (PeripheralManagerPermission<?>) obj;
		return getName().equals(objPerm.getName()) && peripheralClass.equals(objPerm.peripheralClass)
				&& actions.equals(objPerm.actions);
	}

	@Override
	public String getActions() {
		return actions;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + peripheralClass.hashCode() + actions.hashCode();
	}

	@Override
	public boolean implies(Permission permission) {
		if (permission == this) {
			return true;
		}

		if (!(permission instanceof PeripheralManagerPermission)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		PeripheralManagerPermission<Peripheral> periphPerm = (PeripheralManagerPermission<Peripheral>) permission;

		// deals with the permission class name & wildcards
		if (super.implies(periphPerm)) {
			// but we still have to figure out if the actions match
			return impliesActions(periphPerm.actions);
		}

		// now we may want to check on the type
		// any type accepted ?
		if (peripheralClass == null) {
			// we still have to figure out if the actions match
			return impliesActions(periphPerm.actions);
		}

		// are the types equal
		// FIXME: we may want to check for type assignability, but that's a
		// different semantic
		if (peripheralClass.equals(periphPerm.getPeripheralClass())) {
			return impliesActions(periphPerm.actions);
		}
		return false;
	}

	private boolean impliesActions(String otherActions) {
		if (actions.equals(otherActions)) {
			return true;
		}
		// if actions are not equals, then the only possible way they match is
		// that the actions of *this* instance are BOTH, so that it implies
		// any other
		return actions == READ_MODIFY;
	}

}
