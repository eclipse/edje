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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.edje.HardwareDescriptor;
import org.eclipse.edje.Peripheral;

public class PropertiesDescriptor<P extends Peripheral> implements HardwareDescriptor<P> {

	private final Map<String, String> properties;
	private final String name;

	public PropertiesDescriptor(String name, Map<String, String> properties) {
		this.name = name;
		this.properties = properties;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	@Override
	public String[] getPropertyNames() {
		Set<String> keys = properties.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	@Override
	public String[] getPropertyValues() {
		Collection<String> values = properties.values();
		return values.toArray(new String[values.size()]);
	}

}
