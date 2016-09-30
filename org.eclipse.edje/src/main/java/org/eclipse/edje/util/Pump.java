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

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * A pump poll on a {@link Queue}. Characteristics:
 * <ul>
 * <li>Once Pump is started, it cannot be stopped</li>
 * </ul>
 * 
 * @param <T>
 *            the queue data type
 * 
 * @see Queue
 */
public abstract class Pump<T> implements Runnable {

	/**
	 * The queue the pump polls.
	 */
	private final Queue<T> queue;

	/**
	 * Create a pump on the queue.
	 * 
	 * @param queue
	 *            the queue to poll
	 */
	public Pump(Queue<T> queue) {
		this.queue = queue;
	}

	/**
	 * The <code>Pump</code>'s <code>Runnable run</code> method.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				T data = queue.poll();
				// Then, execute the event
				execute(data);
			} catch (Throwable e) {
				crash(e);
			}
		}
	}

	/**
	 * Process the data previously returned by {@link #run()}.
	 * 
	 * @param data
	 *            the data
	 */
	public abstract void execute(T data);

	/**
	 * Called when an error occurred during {@link #run()}.<br/>
	 * The default behavior is invoke the Pump thread
	 * {@link UncaughtExceptionHandler#uncaughtException(Thread, Throwable)} and
	 * continue.
	 * 
	 * @param e
	 *            the error thrown during the poll
	 */
	public void crash(Throwable e) {
		Thread currentThread = Thread.currentThread();
		UncaughtExceptionHandler ueh = currentThread.getUncaughtExceptionHandler();
		if (ueh != null) {
			ueh.uncaughtException(currentThread, e);
		}
	}
}
