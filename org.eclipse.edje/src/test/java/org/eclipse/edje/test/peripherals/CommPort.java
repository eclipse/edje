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

import org.eclipse.edje.HardwareDescriptor;
import org.eclipse.edje.Peripheral;

public abstract class CommPort implements Peripheral {

	private final String name;
	private final PropertiesDescriptor descriptor;

	public CommPort(String name, String hwName, HashMap<String, String> properties) {
		this.name = name;
		this.descriptor = new PropertiesDescriptor(hwName, properties);
	}

	@Override
	public <D extends Peripheral> HardwareDescriptor<D> getDescriptor() {
		return descriptor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Peripheral getParent() {
		return null;
	}

	@Override
	public Peripheral[] getChildren() {
		return null;
	}

}
