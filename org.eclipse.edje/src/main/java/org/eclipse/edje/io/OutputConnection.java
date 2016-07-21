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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This interface is implemented by a {@link Connection} that is able to open an
 * {@link OutputStream}.
 */
public interface OutputConnection extends Connection {

	/**
	 * Open an {@link OutputStream}.
	 *
	 * @throws IOException
	 *             if a stream is already open
	 * @return the opened {@link OutputStream}.
	 */
	OutputStream openOutputStream() throws IOException;

	/**
	 * Open a {@link OutputStream}.
	 *
	 * @throws IOException
	 *             if a stream is already open
	 * @return the opened {@link DataOutputStream}.
	 */
	DataOutputStream openDataOutputStream() throws IOException;

}
