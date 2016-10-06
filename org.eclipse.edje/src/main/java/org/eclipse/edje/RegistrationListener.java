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
 * The {@link RegistrationListener} interface defines methods for notification
 * of registered and unregistered peripherals.
 *
 * @param <P>
 *            the type of the peripheral
 *
 * @see PeripheralManager#addRegistrationListener(RegistrationListener, Class)
 *      method
 */
public interface RegistrationListener<P extends Peripheral> {

	/**
	 * This method is called when a new peripheral is registered if it is
	 * allowed to be notified of events on the registered event class.
	 *
	 * @param event
	 *            the peripheral registration event.
	 */
	void peripheralRegistered(RegistrationEvent<P> event);

	/**
	 * This method is called when a new peripheral is unregistered if it is
	 * allowed to be notified of events on the registered event class.
	 *
	 * @param event
	 *            the peripheral unregistration event.
	 */
	void peripheralUnregistered(RegistrationEvent<P> event);

}
