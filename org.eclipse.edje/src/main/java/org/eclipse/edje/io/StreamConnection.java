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

package org.eclipse.edje.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface defines a bidirectional connection: a connection on which an
 * {@link InputStream} and an {@link OutputStream} can be opened.
 */
public interface StreamConnection extends InputConnection, OutputConnection {

}
