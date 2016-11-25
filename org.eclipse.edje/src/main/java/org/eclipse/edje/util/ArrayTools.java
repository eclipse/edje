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
 * Utility class which performs some checks on arrays.
 */
public class ArrayTools {

	/**
	 * Forbidden constructor: {@link ArrayTools} cannot be instantiated.
	 */
	private ArrayTools() {
	}

	/**
	 * Throws an {@link IndexOutOfBoundsException} when the array bounds are
	 * incoherent:
	 * <li>when the <code>offset</code> is negative</li>
	 * <li>when the <code>length</code> to copy is negative</li>
	 * <li>when <code>offset + length</code> is negative</li>
	 * <li>when <code>offset + length</code> is higher than the array's length
	 * </li>.
	 * 
	 * @param arrayLength
	 *            the array's length. Positive for sure (no check)
	 * @param offset
	 *            the offset in array where start the reading
	 * @param length
	 *            the number of data to read from array
	 * @throws IndexOutOfBoundsException
	 *             when at least one argument is incoherent
	 */
	public static void checkBounds(int arrayLength, int offset, int length) throws IndexOutOfBoundsException {
		int offPlusLen = offset + length;
		if (offset < 0) {
			throw new IndexOutOfBoundsException("Negative offset.");
		} else if (length < 0) {
			throw new IndexOutOfBoundsException("Negative length.");
		} else if (offPlusLen > arrayLength) {
			throw new IndexOutOfBoundsException("Offset + length > object length.");
		} else if (offPlusLen < 0) {
			throw new IndexOutOfBoundsException("Offset + length is negative.");
		}
	}
}
