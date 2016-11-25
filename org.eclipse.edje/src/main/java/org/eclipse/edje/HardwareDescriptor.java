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
 * Descriptive information of the hardware of a {@link Peripheral}. When all
 * peripherals sharing the same descriptor are unregistered, informations may be
 * unavailable. A {@link HardwareDescriptor} is identified by a set of
 * properties.
 *
 * @param <P>
 *            the type of the described peripheral
 */
public interface HardwareDescriptor<P extends Peripheral> {

	/**
	 * Returns the name of this descriptor.
	 *
	 * @return the name of the descriptor, {@link Peripheral#UNKNOWN_NAME} if
	 *         its name is unavailable.
	 */
	String getName();

	/**
	 * Returns the value of the given property.
	 *
	 * @param propertyName
	 *            the property name
	 * @return null if the property is unknown or is unavailable.
	 */
	String getProperty(String propertyName);

	/**
	 * Returns a snapshot of the properties names available for this descriptor.
	 * Subsequent calls to {@link #getProperty(String)} with properties returned
	 * by this method are not ensured to return a non null value.
	 * <p>
	 * This method returns an empty array if the properties are unavailable.
	 * 
	 * @return an array of properties names.
	 */
	String[] getPropertyNames();

	/**
	 * Returns a snapshot of available properties values for this descriptor.
	 * <p>
	 * This method returns an empty array if the properties are unavailable.
	 * 
	 * @return an array of properties values.
	 */
	String[] getPropertyValues();

}
