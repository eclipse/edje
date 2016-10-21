/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Sebastien Eon, MicroEJ} - initial API and implementation and/or initial documentation
 *    {Laurent Lagosanto, MicroEJ} - additional implementation, refactoring
 *******************************************************************************/

package org.eclipse.edje.util;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class which performs some checks on arrays.
 */
public class Util {

	/**
	 * Forbidden constructor: {@link Util} cannot be instantiated.
	 */
	private Util() {
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

	/**
	 * Reads a configuration resource named as the provided key to retrieve a
	 * name in it. The name is actually stored as a single line string.
	 *
	 * @param keyName
	 *            the name of the resource to read
	 * @return the read name if any, or null.
	 */
	public static String readConfigurableName(String keyName) {
		try (InputStreamReader in = new InputStreamReader(Util.class.getResourceAsStream("/" + keyName))) {
			StringBuffer buf = new StringBuffer();
			int x;
			char c = 0;
			// skip spaces
			while ((x = in.read()) != -1) {
				c = (char) x;
				if ((c != ' ') && (c != '\t')) {
					break;
				}
			}
			if ((c == 0) || (c == '#')) {
				return null;
			}
			buf.append(c);
			// accumulate non space chars
			while ((x = in.read()) != -1) {
				c = (char) x;
				if ((c == ' ') || (c == '\t') || (c == '#') || (c == '\r') || (c == '\n')) {
					break;
				}
				buf.append(c);
			}
			return buf.toString();
		} catch (NullPointerException e) {
			// silently ignored
		} catch (IOException e) {
			// silently ignored
		}
		return null;
	}
}
