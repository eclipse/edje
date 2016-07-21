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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This interface is implemented by a {@link Connection} that is able to open an
 * {@link InputStream}.
 */
public interface InputConnection extends Connection {

	/**
	 * Open an {@link InputStream}.
	 *
	 * @throws IOException
	 *             if a stream is already open
	 * @return the opened {@link InputStream}.
	 */
	InputStream openInputStream() throws IOException;

	/**
	 * Open a {@link DataInputStream}.
	 *
	 * @throws IOException
	 *             if a stream is already open
	 * @return the opened {@link DataInputStream}.
	 */
	DataInputStream openDataInputStream() throws IOException;
}
