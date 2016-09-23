/*
 * Java
 *
 * Copyright 2014 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.eclipse.edje.gpio;

import org.eclipse.edje.Peripheral;

/**
 * This class configures and drives digital or analog pins. A pin is identified
 * with its port and the pin number of its port. Each port has a unique ID which
 * is implementation specific. Pin numbers are a zero-based indexes.
 */
public interface GPIOPort extends Peripheral {

	/**
	 * Specify the pin mode.
	 *
	 * @see #setMode(int, Mode)
	 */
	public enum Mode {

		/**
		 * Digital input pin mode.
		 */
		DIGITAL_INPUT,

		/**
		 * Digital input with internal pull-up resistor pin mode.
		 */
		DIGITAL_INPUT_PULLUP,

		/**
		 * Digital input with internal pull-down resistor pin mode.
		 */
		DIGITAL_INPUT_PULLDOWN,

		/**
		 * Digital output pin mode.
		 */
		DIGITAL_OUTPUT,

		/**
		 * Analog input pin mode.
		 */
		ANALOG_INPUT,

		/**
		 * Analog output pin mode.
		 */
		ANALOG_OUTPUT;
	}

	/**
	 * Configures the pin mode using the enumeration {@link Mode}.
	 *
	 * @param pin
	 *            number of the pin to configure
	 * @param mode
	 *            mode to configure the pin in
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable
	 * @throws NullPointerException
	 *             when <code>mode</code> is null
	 */
	public void setMode(int pin, Mode mode);

	/**
	 * Gets the digital value of the specified pin.<br>
	 *
	 * @param pin
	 *            number of the pin to get the value from
	 *
	 * @return <code>true</code> when the GPIO digital value is currently
	 *         <i>high</i>.
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable
	 */
	public boolean getDigitalValue(int pin);

	/**
	 * Sets a digital value on the specified pin.<br>
	 *
	 * @param pin
	 *            pin number
	 * @param value
	 *            digital pin value: <code>true</code> for <i>high</i>,
	 *            <code>false</code> for <i>low</i>.
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable
	 */
	public void setDigitalValue(int pin, boolean value);

	/**
	 * Gets the analog value of the specified pin (a value between
	 * <code>0</code> and the targeted hardware ADC maximum value).<br>
	 *
	 * @param pin
	 *            pin number
	 *
	 * @return analog pin value.
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable
	 */
	public int getAnalogValue(int pin);

	/**
	 * Gets the maximum analog value that the specified pin can support in the
	 * current configuration.<br>
	 * It can be the maximum value that can be read in input mode, or written in
	 * output mode. The value may vary with the configuration and may be
	 * different between pins and may depend on modes. (e.g. there can be a
	 * 10-bit DAC and a 12-bit ADC on the target).
	 *
	 * @param pin
	 *            pin number
	 *
	 * @return the maximum analog value for the specified pin, in the current
	 *         configuration
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable
	 */
	public int getAnalogMaxValue(int pin);

	/**
	 * Gets the minimum analog value that the specified pin can support in the
	 * current configuration.<br>
	 * It can be the maxminimumimum value that can be read in input mode, or
	 * written in output mode. The value may vary with the configuration and may
	 * be different between pins and may depend on modes. (e.g. there can be a
	 * 10-bit DAC and a 12-bit ADC on the target).
	 *
	 * @param pin
	 *            pin number
	 *
	 * @return the minimum analog value for the specified pin, in the current
	 *         configuration
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable
	 */
	public int getAnalogMinValue(int pin);

	/**
	 * Sets an analog value on the specified pin.
	 *
	 * @param pin
	 *            pin number
	 * @param value
	 *            pin value.
	 * @throws IllegalArgumentException
	 *             when the combination port / pin is unreachable or when the
	 *             value is invalid
	 */
	public void setAnalogValue(int pin, int value);
}
