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

package org.eclipse.edje.test.support;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.edje.Peripheral;
import org.junit.Assert;

public class Util {

	public static <T> boolean isEmpty(T[] array) {
		return array.length == 0;
	}

	public static <T> boolean equals(T e1, T e2) {
		if (e1 == e2) {
			return true;
		} else if (e1 == null || e2 == null) {
			return false;
		} else {
			return e1.equals(e2);
		}
	}

	public static <T> boolean equals(T[] array, T e) {
		return array.length != 1 ? false : equals(array[0], e);
	}

	public static <T> boolean equals(T[] array1, T[] array2) {
		return array1.length == array2.length && isIncluded(array1, array2) && isIncluded(array2, array1);
	}

	public static <T> boolean isIncluded(T de, T[] array) {
		return indexOf(de, array) != -1;
	}

	public static <T> int indexOf(T e, T[] array) {
		for (int i = array.length; --i >= 0;) {
			if (equals(e, array[i])) {
				return i;
			}
		}
		return -1;
	}

	public static <T> boolean isIncluded(T[] array1, T[] array2) {
		for (T d : array1) {
			if (!isIncluded(d, array2)) {
				return false;
			}
		}
		return true;
	}

	public static Peripheral[] toArray(Iterator<? extends Peripheral> list) {
		ArrayList<Peripheral> vect = new ArrayList<>();
		while (list.hasNext()) {
			vect.add(list.next());
		}
		return vect.toArray(new Peripheral[vect.size()]);
	}

	public static void check(String message, boolean result, boolean expectedSuccess) {
		if (expectedSuccess) {
			Assert.assertTrue(message, result);
		} else {
			Assert.assertFalse(message, result);
		}
	}

}
