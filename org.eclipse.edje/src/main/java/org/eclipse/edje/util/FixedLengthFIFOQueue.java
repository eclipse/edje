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
 * Queue with FIFO management. Characteristics:
 * <ul>
 * <li>Fixed buffer size</li>
 * <li>Simple synchronization on buffer array</li>
 * <li>A {@link QueueFullException} is thrown if the queue is full</li>
 * </ul>
 * 
 * @param <T>
 *            data type
 */
public class FixedLengthFIFOQueue<T> extends Queue<T> {

	/**
	 * List of data.
	 */
	public Object[] buffer;

	/**
	 * pointer on next data to read.
	 */
	public int ptrBegin;

	/**
	 * pointer on next to add.
	 */
	public int ptrEnd;

	/**
	 * Creates a queue with FIFO management.
	 * 
	 * @param bufferSize
	 *            the FIFO size
	 */
	public FixedLengthFIFOQueue(int bufferSize) {
		super();
		this.buffer = new Object[bufferSize + 1]; // +1: one index in the queue
													// is always empty
	}

	/**
	 * Returns the oldest data or waits for it. This method blocks until data is
	 * available.
	 * 
	 * @return the oldest data added to the FIFO
	 */
	@Override
	public T poll() {
		// NOTE: read do not need writeMonitor
		synchronized (buffer) {
			while (ptrBegin == ptrEnd) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
			}
			Object data = buffer[ptrBegin];
			buffer[ptrBegin] = null;
			ptrBegin = (ptrBegin + 1) % buffer.length;
			return (T) data;
		}
	}

	/**
	 * This method adds the <code>data</code> to the FIFO. When the FIFO is
	 * full, {@link QueueFullException} is thrown.
	 * 
	 * @param data
	 *            the new data to be added to the FIFO
	 */
	public void add(T data) {
		synchronized (buffer) {
			int eventQueueLength = buffer.length;
			// Check if the queue is not full
			int nextEnd = (ptrEnd + 1) % eventQueueLength; // ptrEnd is read
															// without accessing
															// readMonitor
			if (nextEnd == ptrBegin) {
				// fifo is full
				throw new QueueFullException(data);
			}
			// add the new event at the last index : ptrEnd
			buffer[ptrEnd] = data;
			ptrEnd = (ptrEnd + 1) % eventQueueLength;
			// notify eventually the waiting pumpEvent Thread
			buffer.notify();
		}
	}

}
