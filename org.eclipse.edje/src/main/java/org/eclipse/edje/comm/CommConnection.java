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

package org.eclipse.edje.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.edje.io.BitsInput;
import org.eclipse.edje.io.BitsOutput;
import org.eclipse.edje.io.Connection;
import org.eclipse.edje.io.Connector;
import org.eclipse.edje.io.StreamConnection;

/**
 * This interface defines a {@link Connection} to a logical serial port, which
 * is a {@link StreamConnection}. There is no way from this side of the
 * connection to know the connection state on the other side (particularly,
 * closing the other side has no effect on this side; a thread blocked on
 * {@link InputStream#read()} will infinitely block).
 * <p>
 * The generic URL format described in {@link Connector} is specified as
 * following:
 * <ul>
 * <li><code>protocol</code>: comm</li>
 * <li><code>name</code>: the {@link CommPort} name</li>
 * <li><code>params</code>: optional connection parameters in the following list
 * </li>
 * <li><code>baudrate</code>: positive integer (9600 by default)</li>
 * <li><code>bitsperchar</code>: positive integer (8 by default)</li>
 * <li><code>stopbits</code>:1|2|1.5 (1 by default)</li>
 * <li><code>parity</code>:odd|even|none (none by default)</li>
 * </ul>
 */
public interface CommConnection extends StreamConnection {

	/**
	 * Gets the current configured baudrate.
	 *
	 * @return the current configured baudrate.
	 */
	public abstract int getBaudrate();

	/**
	 * Configures the baudrate. If the given baudrate cannot be configured,
	 * connection is configured with valid baudrate that can be retrieved by
	 * {@link #getBaudrate()}.
	 *
	 * @param baudrate
	 *            the new baudrate
	 * @return the previous baudrate.
	 */
	public abstract int setBaudrate(int baudrate);

	/**
	 * Returns an {@link InputStream} which implements the {@link BitsInput}
	 * interface. This implementation is useful when this {@link Connection}
	 * supports more than 8 bits per frame.
	 */
	@Override
	public abstract InputStream openInputStream() throws IOException;

	/**
	 * Returns an {@link OutputStream} which implements the {@link BitsOutput}
	 * interface. This implementation is useful when this {@link Connection}
	 * supports more than 8 bits per frame.
	 */
	@Override
	public abstract OutputStream openOutputStream() throws IOException;
}