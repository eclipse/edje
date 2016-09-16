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

package org.eclipse.edje.test.peripherals;

import java.util.HashMap;

public class UsbPeripheral extends CommPortImpl {

	public UsbPeripheral(String name, HashMap<String, String> properties) {
		super(name, UsbPeripheral.class.getName(), properties);
	}

}
