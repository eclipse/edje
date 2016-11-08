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

package org.eclipse.edje;

/**
 * Common interface for a Peripheral. A peripheral is uniquely identified with
 * its hardware descriptor.
 */
public interface Peripheral {

	/**
	 * This value is returned by {@link #getName()} when this peripheral is
	 * unregistered and its name is unavailable.
	 */
	String UNKNOWN_NAME = "UNKNOWN";

	/**
	 * Returns the peripheral hardware descriptor.
	 *
	 * @return the peripheral hardware descriptor
	 */
	HardwareDescriptor<? extends Peripheral> getDescriptor();

	/**
	 * Returns the name of the peripheral.
	 *
	 * @return the peripheral name, {@link #UNKNOWN_NAME} if this peripheral is
	 *         not registered and its name is unavailable.
	 */
	String getName();
}
