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

package org.eclipse.edje.io;

import java.io.Serializable;
import java.security.Permission;

/**
 * This class represents rights access {@link Permission} to open a
 * {@link Connection}. The name is the URL of the connection to open.
 * 
 * @see Connector#open(String)
 */
public class ConnectionPermission extends Permission {

	/**
	 * {@link Serializable} UID.
	 */
	private static final long serialVersionUID = -8885976374034978006L;

	/**
	 * Constructs a permission with the specified name.
	 * 
	 * @param name
	 *            name of the ConnectionPermission object being created.
	 * 
	 */
	public ConnectionPermission(String name) {
		super(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		ConnectionPermission objPerm = (ConnectionPermission) obj;
		return getName().equals(objPerm.getName());
	}

	@Override
	public String getActions() {
		return "";
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean implies(Permission permission) {
		return equals(permission);
	}

}
