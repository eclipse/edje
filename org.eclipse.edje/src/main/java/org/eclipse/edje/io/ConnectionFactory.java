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

/**
 * This interface defines the API for {@link Connection} factories. A Connection
 * factory is a class that implements this interface with the following fully
 * qualified name format:
 *
 * <pre>
 * [packageName].[protocol].ConnectionFactory
 * </pre>
 *
 * @see Connector#open(String)
 */
public interface ConnectionFactory {

	/**
	 * Open a connection with the specified URL.
	 *
	 * @param url
	 *            the connection URL to open
	 * @return the opened {@link Connection}.
	 * @throws ConnectionNotFoundException
	 *             if connection protocol is not found
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	Connection open(String url) throws IOException;
}