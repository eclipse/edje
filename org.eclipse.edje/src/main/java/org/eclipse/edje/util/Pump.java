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
	 * The uncaught exception handler of the queue, may be null
	 */
	private final UncaughtExceptionHandler uncaughtExceptionHandler;

	/**
	 * The priority of the thread used to run the pump
	 */
	private final int priority;

	/**
	 * Create a pump on the queue.
	 *
	 * @param queue
	 *            the queue to poll
	 * @param threadPriority
	 *            the requested priority of the thread used to run the pump
	 * @param ueh
	 *            the uncaught exception handler
	 */
	public Pump(Queue<T> queue, int threadPriority, UncaughtExceptionHandler ueh) {
		this.queue = queue;
		this.priority = threadPriority;
		this.uncaughtExceptionHandler = ueh;
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
	 * The default behaviour is invoke the
	 * {@link java.lang.Thread.UncaughtExceptionHandler} passed in the
	 * constructor, if any, and continue.
	 *
	 * @param e
	 *            the error thrown during the poll
	 */
	public void crash(Throwable e) {
		if (uncaughtExceptionHandler != null) {
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
		}
	}

	/**
	 * Gets the requested priority for the thread running the pump.
	 *
	 * @return the priority.
	 * @see Thread#setPriority(int)
	 */
	public int getPriority() {
		return priority;
	}
}
