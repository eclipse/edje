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
 * This interface defines an opened Connection.
 */
public interface Connection extends java.io.Closeable {

	/**
	 * Close the connection. If the connection has already been closed or a
	 * close is pending, this method does nothing. If the connection has
	 * underlying open streams, the connection will be closed only when these
	 * streams will be closed.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	void close() throws IOException;

}
