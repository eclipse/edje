/*
 * Java
 *
 * Copyright 2014 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.eclipse.edje.comm;

import org.eclipse.edje.Peripheral;
import org.eclipse.edje.io.Connectable;
import org.eclipse.edje.io.StreamConnection;

/**
 * This interface defines a logical serial port, on which a
 * {@link CommConnection} can be open. A {@link CommPort} can represent hardware
 * ports such as UART or USB CDC / ACM classes. On a logical serial port can be
 * open at most one {@link StreamConnection} at a time.
 *
 * @see CommConnection
 */
public interface CommPort extends Peripheral, Connectable {

}
