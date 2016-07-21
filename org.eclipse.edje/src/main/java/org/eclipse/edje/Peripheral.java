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

package org.eclipse.edje;

/**
 * Common interface for a Peripheral. A peripheral is uniquely identified with its ID and its hardware descriptor.
 */
public interface Peripheral {

	/**
	 * This value is returned by {@link #getName()} when this peripheral is unregistered and its name is unavailable.
	 */
	String UNKNOWN_NAME = "UNKNOWN";

	/**
	 * This value is returned by {@link #getId()} when this peripheral is unregistered and its ID is unavailable.
	 */
	int UNKNOWN_ID = -1;

	/**
	 * Returns the peripheral hardware descriptor.
	 * 
	 * @param <P>
	 *            the type of the peripheral
	 * @return the peripheral hardware descriptor
	 */
	<P extends Peripheral> HardwareDescriptor<P> getDescriptor();

	/**
	 * Returns the name of the peripheral.
	 * 
	 * @return the peripheral name, {@link #UNKNOWN_NAME} if this peripheral is not registered and its name is
	 *         unavailable.
	 */
	String getName();

	/**
	 * Returns the ID of the peripheral. ID is a positive integer.
	 * 
	 * @return the peripheral ID, {@link #UNKNOWN_ID} if this peripheral is not registered and its ID is unavailable.
	 */
	String getId();

	/**
	 * Returns the parent of this peripheral in the hardware topology.
	 * 
	 * @return null if it is a root peripheral.
	 */
	Peripheral getParent();

	/**
	 * Returns the children of this peripheral in the hardware topology.
	 * 
	 * @return an empty array if it is a leaf peripheral.
	 */
	Peripheral[] getChildren();

}
