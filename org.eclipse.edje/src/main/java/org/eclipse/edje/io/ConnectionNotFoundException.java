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
import java.io.Serializable;

/**
 * This exception is thrown when a connection protocol is unknown.
 * 
 * @see Connector#open(String)
 */
public class ConnectionNotFoundException extends IOException {

	/**
	 * {@link Serializable} UID.
	 */
	private static final long serialVersionUID = -2707914215500561768L;

	/**
	 * Constructs an {@code ConnectionNotFoundException} with {@code null} as
	 * its error detail message.
	 */
	public ConnectionNotFoundException() {
		super();
	}

	/**
	 * Constructs an {@code ConnectionNotFoundException} with the specified
	 * detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method)
	 */
	public ConnectionNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code ConnectionNotFoundException} with the specified
	 * detail message and cause.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method)
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 */
	public ConnectionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
