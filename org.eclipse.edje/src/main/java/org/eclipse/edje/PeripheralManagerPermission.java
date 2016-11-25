/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Guillaume Balan, MicroEJ} - initial API and implementation and/or initial documentation
 *    {Laurent Lagosanto, MicroEJ} - additional implementation, refactoring
 *******************************************************************************/

package org.eclipse.edje;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents access rights to the peripheral resources.
 *
 */
@SuppressWarnings("serial")
public class PeripheralManagerPermission extends Permission {

	/**
	 * The {@link #READ} permission is required to access to registered
	 * peripherals.
	 */
	public static final String READ = "read";

	/**
	 * The {@link #MODIFY} permission is required to be able to modify the
	 * registry, i.e. to register or unregister peripherals.
	 */
	public static final String MODIFY = "modify";

	/**
	 * The {@link #READ_MODIFY} permission is a convenience shortcut for giving
	 * (or testing for) both permissions.
	 */
	public static final String READ_MODIFY = "read,modify";

	private final String actions;

	private final Map<String, String> constraints;

	/**
	 * Creates a {@link PeripheralManagerPermission} permission with the
	 * specified Peripheral type, the specified Peripheral instance and action.
	 * <br>
	 * The purpose of this constructor is mostly for the permission-checking
	 * code.
	 *
	 * @param <C>
	 *            the peripheral registration type, a subclass of Peripheral
	 * @param <P>
	 *            the actual peripheral type, a subclass of C
	 *
	 * @param peripheralType
	 *            the registration type on which the permission is checked
	 * @param peripheral
	 *            the peripheral on which the permission is checked
	 * @param action
	 *            the action to verify, either {@link #READ} or {@link #MODIFY}
	 *            permission name
	 */
	public <C extends Peripheral, P extends C> PeripheralManagerPermission(Class<C> peripheralType, P peripheral,
			String action) {
		super(buildName(peripheralType, peripheral));
		this.actions = buildActions(action);
		this.constraints = new HashMap<>();
		this.constraints.put("name", peripheral.getName());
		this.constraints.put("class", peripheralType.getName());
		HardwareDescriptor<? extends Peripheral> desc = peripheral.getDescriptor();
		if (desc != null) {
			for (String name : desc.getPropertyNames()) {
				String value = desc.getProperty(name);
				if (value != null) {
					this.constraints.put(name, value);
				}
			}
		}
	}

	private static String buildSpecString(Map<String, String> constraints) {
		StringBuilder spec = new StringBuilder();
		for (String name : constraints.keySet()) {
			String value = constraints.get(name);
			if (value != null) {
				spec.append(',').append(name).append('=').append(value);
			}
		}
		return spec.toString();
	}

	private static <C extends Peripheral, P extends C> String buildName(Class<C> peripheralType, P peripheral) {
		StringBuilder spec = new StringBuilder();
		spec.append("name=").append(peripheral.getName());
		spec.append(",class=").append(peripheralType.getName());
		HardwareDescriptor<?> desc = peripheral.getDescriptor();
		if (desc != null) {
			for (String property : desc.getPropertyNames()) {
				String value = desc.getProperty(property);
				if (value != null) {
					spec.append(',').append(property).append('=').append(value);
				}
			}
		}
		return spec.toString();
	}

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
	 * Creates a {@link PeripheralManager} permission with the provided device
	 * constraints specification string. <br>
	 * This constructor is intended to be used when building the security
	 * policy.<br>
	 *
	 * The device specification constraints string must be a comma-separated
	 * list of value pairs, each value pair being formed like this:
	 * &lt;name&gt;=&lt;value&gt;.<br>
	 *
	 * The values themselves can contain the wildcard character ('*') like in
	 * the following examples:
	 * <li>"*" : means any value (basically a way to mandate the property to be
	 * set)
	 * <li>"prefix*" : means any value starting with "prefix"
	 * <li>"*suffix" : means any value ending with "suffix"
	 * <li>"prefix*suffix" : means any value both starting with "prefix" and
	 * ending with "suffix" with anything in between.
	 *
	 * Specifying constraints means that the all the properties exposed by the
	 * {@link HardwareDescriptor} of the {@link Peripheral} must fall under
	 * these constraints, with the name and the registration class of the
	 * peripheral being considered as regular properties.
	 *
	 * @param spec
	 *            the device specification string.
	 * @param actions
	 *            either {@link #READ} or {@link #MODIFY} permission name, or a
	 *            combination of the two
	 */
	public PeripheralManagerPermission(String spec, String actions) {
		super(spec);
		this.actions = buildActions(actions);
		this.constraints = buildConstraints(spec);
	}

	/**
	 * Creates a {@link PeripheralManager} permission with the provided device
	 * constraints and the specified actions. <br>
	 * This constructor is intended to be used when building the security policy
	 * and is a shortcut for the
	 * {@link PeripheralManagerPermission#PeripheralManagerPermission(String, String)}
	 * constructor, to be used, for instance, if the security policy s not built
	 * from a policy text file.
	 *
	 * @param constraints
	 *            the device properties constraints, as a name-value Map.
	 * @param actions
	 *            either {@link #READ} or {@link #MODIFY} permission name, or a
	 *            combination of the two
	 */
	public PeripheralManagerPermission(Map<String, String> constraints, String actions) {
		super(buildSpecString(constraints));
		this.actions = buildActions(actions);
		this.constraints = constraints;
	}

	private static List<String> split(String string, char separator)
			throws NullPointerException, IndexOutOfBoundsException {
		List<String> list = new ArrayList<>();
		// manage first elements
		int start = 0;
		int end = string.indexOf(separator);

		while (end != -1) {
			String element = string.substring(start, end).trim();
			if (element.length() > 0) {
				list.add(element);
			}
			start = end + 1;
			end = string.indexOf(separator, start);
		}
		// manage last element
		String element = string.substring(start, string.length()).trim();
		if (element.length() > 0) {
			list.add(element);
		}
		return list;
	}

	private Map<String, String> buildConstraints(String spec) {
		Map<String, String> constraints = new HashMap<>();
		List<String> items = split(spec, ',');
		for (String item : items) {
			List<String> pair = split(item, '=');
			if (pair.size() != 2) {
				throw new IllegalArgumentException("Wrong format for spec: " + item);
			}
			String name = pair.get(0);
			String value = pair.get(1);
			constraints.put(name, value);
		}
		return constraints;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		PeripheralManagerPermission objPerm = (PeripheralManagerPermission) obj;
		return getName().equals(objPerm.getName()) && actions.equals(objPerm.actions);
	}

	@Override
	public String getActions() {
		return actions;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + actions.hashCode();
	}

	@Override
	public boolean implies(Permission permission) {
		if (permission == this) {
			return true;
		}

		if (!(permission instanceof PeripheralManagerPermission)) {
			return false;
		}

		PeripheralManagerPermission that = (PeripheralManagerPermission) permission;

		// are the actions compatible ?
		if (!impliesActions(that.actions)) {
			return false;
		}

		// same spec ?
		// (spec is stored in the name field)
		String thisSpec = getName();
		if ((thisSpec != null) && thisSpec.equals(that.getName())) {
			return true;
		}

		Map<String, String> thisConstraints = this.constraints;
		if (thisConstraints != null) {
			for (String property : thisConstraints.keySet()) {
				String thisValue = thisConstraints.get(property);
				if (thisValue != null) {
					String thatValue = that.constraints.get(property);
					if (thatValue == null) {
						return false;
					}
					if (!match(thisValue, thatValue)) {
						return false;
					}
				}
			}
		}

		// if we reach this point:
		// - the actions are compatible
		// - all of the constraints match
		return true;
	}

	/**
	 * Checks whether the specified value matches the specified constraint. The
	 * constraint may contain:
	 * <li>an optional prefix string
	 * <li>an option wildcard character ('*')
	 * <li>an optional suffix string
	 *
	 * @param constraint
	 *            the constraint to match with
	 * @param value
	 *            he string to compare to the constraint
	 * @return
	 */
	private boolean match(String constraint, String value) {
		if (constraint.equals(value)) {
			return true;
		}
		int wildcardOffset = constraint.indexOf('*');
		if (wildcardOffset == -1) {
			// they are not equals, and there's no wildcard in the constraint
			return false;
		}
		int otherWildcardOffset = constraint.indexOf('*', wildcardOffset + 1);
		if (otherWildcardOffset != -1) {
			throw new IllegalArgumentException("Illegal constraint format: " + constraint);
		}
		// same prefix ?
		if (!value.regionMatches(0, constraint, 0, wildcardOffset)) {
			return false;
		}
		int suffixLen = constraint.length() - wildcardOffset - 1;

		return value.regionMatches(value.length() - suffixLen, constraint, wildcardOffset + 1, suffixLen);
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
