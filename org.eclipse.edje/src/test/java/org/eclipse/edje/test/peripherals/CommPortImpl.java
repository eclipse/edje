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

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.edje.comm.CommPort;
import org.eclipse.edje.io.Connection;

public abstract class CommPortImpl implements CommPort {

	private final String name;
	private final PropertiesDescriptor<CommPortImpl> descriptor;

	public CommPortImpl(String name, String hwName, HashMap<String, String> properties) {
		this.name = name;
		this.descriptor = new PropertiesDescriptor<>(hwName, properties);
	}

	@Override
	public PropertiesDescriptor<CommPortImpl> getDescriptor() {
		return descriptor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Connection openConnection(String args) throws IOException {
		return null;
	}

}
