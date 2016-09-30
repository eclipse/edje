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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Common behavior for all {@link StreamConnection} i.e. connections that are
 * directly relied to a hardware connection.
 * 
 * <p>
 * This abstract class also manages the {@link Connection}'s close policy: a
 * connection is really closed when both {@link #outputStream} and
 * {@link #inputStream} are closed.
 */
public abstract class AbstractConnection implements StreamConnection {

	/**
	 * Hardware resource ID. Used to make the link between the
	 * {@link AbstractConnection} instance and the hardware connection.
	 */
	protected final int resourceId;

	/**
	 * Current opened {@link OutputStream} (or null).
	 */
	protected OutputStream outputStream;

	/**
	 * Current opened {@link InputStream} (or null).
	 */
	protected InputStream inputStream;

	/**
	 * Close behavior: when {@link Connection#close()} is invoked, no
	 * {@link InputStream} or {@link OutputStream} can be opened. But
	 * {@link Connection} is really closed when both {@link #outputStream} and
	 * {@link #inputStream} are closed. Synchronization is done on this
	 * {@link AbstractConnection} object.
	 */
	private CloseState closeState;

	/**
	 * Creates a {@link Connection} relied to a hardware connection.
	 * 
	 * @param resourceId
	 *            the hardware connection identifier
	 */
	protected AbstractConnection(int resourceId) {
		this.resourceId = resourceId;
		this.closeState = CloseState.NotClosed;
	}

	/**
	 * Opens a {@link AbstractConnectionInputStream} on connection. Ensures the
	 * stream can be opened:
	 * <ul>
	 * <li>the stream is not already opened</li>
	 * <li>the connection is not ready to be closed</li>
	 * <li>the connection is not closed</li>
	 * </ul>
	 * 
	 * @return the opened {@link AbstractConnectionInputStream}
	 * 
	 * @throws IOException
	 *             if a stream cannot be opened or if is already open.
	 */
	@Override
	public final synchronized InputStream openInputStream() throws IOException {
		checkCanOpen(inputStream);
		inputStream = newInputStream();
		return inputStream;
	}

	/**
	 * Opens a {@link AbstractConnectionOutputStream} on connection. Ensures the
	 * stream can be opened:
	 * <ul>
	 * <li>the stream is not already opened</li>
	 * <li>the connection is not ready to be closed</li>
	 * <li>the connection is not closed</li>
	 * </ul>
	 * 
	 * @return the opened {@link AbstractConnectionInputStream}
	 * 
	 * @throws IOException
	 *             if a stream cannot be opened or if is already open.
	 */
	@Override
	public final synchronized OutputStream openOutputStream() throws IOException {
		checkCanOpen(outputStream);
		outputStream = newOutputStream();
		return outputStream;
	}

	@Override
	public final DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	@Override
	public final DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	/**
	 * Ask to close the connection. If the connection has been already closed,
	 * nothing occurs. If at least one stream is not closed, the close is
	 * pending until the both streams will be closed. If both streams are
	 * closed, the connection is closed immediately.
	 */
	@Override
	public final void close() throws IOException {

		if (closeState != CloseState.NotClosed) {
			// Closing an already closed connection has no effect
			return;
		}

		closeState = CloseState.WantsToClose;
		tryClose();
	}

	/**
	 * Creates a {@link AbstractConnectionInputStream} and open an input stream
	 * on hardware connection.
	 * 
	 * @return a new {@link AbstractConnectionInputStream}.
	 * @throws IOException
	 *             when an error occurs when opening a stream on hardware
	 *             connection.
	 */
	protected abstract InputStream newInputStream() throws IOException;

	/**
	 * Creates a {@link AbstractConnectionOutputStream} and open an output
	 * stream on hardware connection.
	 * 
	 * @return a new {@link AbstractConnectionOutputStream}.
	 * @throws IOException
	 *             when an error occurs when opening a stream on hardware
	 *             connection.
	 */
	protected abstract OutputStream newOutputStream() throws IOException;

	/**
	 * Tries to close the connection. The connection is closed only when the
	 * both streams are closed too. Nothing occurs otherwise.
	 */
	private void tryClose() {
		if (closeState == CloseState.WantsToClose && inputStream == null && outputStream == null) {
			// no opened stream => connection can be fully closed now.
			// otherwise the last input stream or output stream will perform the
			// close.
			hardwareClose();
			closeState = CloseState.Closed;
		}
	}

	/**
	 * Removes the input stream from the connection and tries to close the
	 * connection if pending.
	 */
	protected synchronized void removeInputStream() {
		this.inputStream = null;
		tryClose();
	}

	/**
	 * Removes the output stream from the connection and tries to close the
	 * connection if pending.
	 */
	protected synchronized void removeOutputStream() {
		this.outputStream = null;
		tryClose();
	}

	/**
	 * Close the hardware connection. Called only when the
	 * {@link AbstractConnection} instance is ready to be closed.
	 */
	protected abstract void hardwareClose();

	/**
	 * Ensures the stream can be opened.
	 * <ul>
	 * <li>the stream is not already opened</li>
	 * <li>the connection is not ready to be closed</li>
	 * <li>the connection is not closed</li>
	 * </ul>
	 * 
	 * @param stream
	 * @throws IOException
	 */
	private synchronized void checkCanOpen(Closeable stream) throws IOException {

		if (closeState != CloseState.NotClosed) {
			// connection closed or ready to be closed
			throw new IOException("Connection closed.");
		}

		if (stream != null) {
			// the stream is already opened
			throw new IOException("Stream already opened.");
		}
	}

	/**
	 * See {@link #closeState}.
	 */
	static enum CloseState {

		/**
		 * Connection is opened; some streams can be opened on it.
		 */
		NotClosed,

		/**
		 * Connection is ready to be closed; waiting the close of all streams.
		 */
		WantsToClose,

		/**
		 * Connection closed.
		 */
		Closed
	}
}
