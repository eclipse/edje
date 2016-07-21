/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Sebastien Eon, MicroEJ} - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.edje.util;

/**
 * Thrown when FIFO pump is full.
 */
public class QueueFullException extends IllegalStateException {

	/**
	 * The data that could not be added to the pump.
	 */
	private final Object data;

	/**
	 * @param data
	 *            the data that could not be added to the FIFO pump.
	 */
	public QueueFullException(Object data) {
		this.data = data;
	}

	/**
	 * @return the data that could not be added to the FIFO pump.
	 */
	public Object getData() {
		return data;
	}

}