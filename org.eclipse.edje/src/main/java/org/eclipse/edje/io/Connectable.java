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

import java.io.IOException;

import org.eclipse.edje.Peripheral;

/**
 * Common interface for a class that is able to open a {@link Connection} from
 * it. When a {@link Peripheral} implements {@link Connectable} interface, then
 * the argument of {@link Connectable#openConnection(String)} correspond to the
 * <code>params</code> part of {@link Connector} URL specification.
 */
public interface Connectable {

	/**
	 * Create and open a new {@link Connection}. The given <code>args</code>
	 * {@link String} format depends on the implementation of this interface.
	 * Sub-classes or sub-interfaces must specify the format of this
	 * {@link String} and the type of the returned {@link Connection}.
	 *
	 * @param args
	 *            the parameters of the connection
	 * @return a new connection object.
	 * @see Connector#open(String)
	 * @throws ConnectionNotFoundException
	 *             if connection protocol is not found
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	Connection openConnection(String args) throws IOException;

}
