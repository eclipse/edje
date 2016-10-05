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

package org.eclipse.edje.test.support;

import org.junit.Assert;

public class SynchroSupport {

	private static Object STATE_MONITOR = new Object();
	private final static int STATE_NOT_SET = -1;
	private static int STATE = STATE_NOT_SET;
	private static int STATE_NB_TIMES = 0;

	public static void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			Assert.assertTrue("errSleep", false);
		}
	}

	public static void waitState(int expectedState) {
		waitState(expectedState, 1);
	}

	public static String getThreadDescriptor() {
		return "[" + Thread.currentThread().getName() + "]";
	}

	public static void notifyState(int newState) {
		synchronized (STATE_MONITOR) {
			System.out.println(getThreadDescriptor() + ": notify state" + newState);
			if (!(STATE == STATE_NOT_SET || STATE == newState)) {
				Assert.assertTrue("notifyState", false);
			}
			STATE = newState;
			++STATE_NB_TIMES;
			STATE_MONITOR.notifyAll();
		}
	}

	public static void waitState(int expectedState, int nbTimes) {
		waitState(expectedState, nbTimes, 10000);
	}

	public static void waitState(int expectedState, int nbTimes, int timeout) {
		long start = System.currentTimeMillis();
		synchronized (STATE_MONITOR) {
			while (STATE != expectedState || STATE_NB_TIMES != nbTimes) {
				if ((System.currentTimeMillis() - start) > timeout) {
					throw new AssertionError("waited for more than " + timeout + "ms");
				}
				System.out.println(getThreadDescriptor() + ": waiting for state" + expectedState);
				try {
					STATE_MONITOR.wait(1000);
				} catch (InterruptedException e) {
					throw new AssertionError();
				}
			}
			STATE = STATE_NOT_SET; // reset
			STATE_NB_TIMES = 0; // reset
			System.out.println(getThreadDescriptor() + ": state" + expectedState + " reached");
		}
	}

	public static void clearState() {
		synchronized (STATE_MONITOR) {
			STATE = STATE_NOT_SET;
			STATE_NB_TIMES = 0;
		}
	}

}
