/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Laurent Lagosanto, MicroEJ} - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.edje.test;

import org.eclipse.edje.DefaultPeripheralRegistry;
import org.eclipse.edje.HardwareDescriptor;
import org.eclipse.edje.Peripheral;
import org.eclipse.edje.RegistrationEvent;
import org.eclipse.edje.util.Pump;

public class TestPeripheralRegistry extends DefaultPeripheralRegistry {

	private final class TestPeripheral implements Peripheral {
		private final String name;

		public TestPeripheral(String name) {
			this.name = name;
		}

		@Override
		public Peripheral getParent() {
			return null;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public HardwareDescriptor<? extends Peripheral> getDescriptor() {
			return new HardwareDescriptor<Peripheral>() {

				@Override
				public String getName() {
					return TestPeripheral.this.name;
				}

				@Override
				public String getProperty(String propertyName) {
					return null;
				}

				@Override
				public String[] getPropertyNames() {
					return null;
				}

				@Override
				public String[] getPropertyValues() {
					return null;
				}
			};
		}

		@Override
		public Peripheral[] getChildren() {
			return null;
		}
	}

	@Override
	public void start(Pump<RegistrationEvent<?>> pump) {
		super.start(pump);

		// register a static TestPeripheral for test purposes
		register(Peripheral.class, new TestPeripheral("test"), false, true);
	}

}
