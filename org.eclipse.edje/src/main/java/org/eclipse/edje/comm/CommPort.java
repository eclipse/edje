/*******************************************************************************
 * Copyright (c) 2016 IS2T S.A. Operating under the brand name MicroEJ(r).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    {Guillaume Balan, MicroEJ} - initial API and implementation and/or initial documentation
 *    {Laurent Lagosanto, MicroEJ} - additional implementation, refactoring
 *******************************************************************************/

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
