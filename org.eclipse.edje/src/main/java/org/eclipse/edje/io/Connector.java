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

package org.eclipse.edje.io;

import java.io.IOException;

import org.eclipse.edje.util.Util;

/**
 * This class defines methods for opening a {@link Connection} from an URL. The
 * URL format (which complies with RFC 2396) is on the following format:
 *
 * <pre>
 * [protocol]:[name](;[param])*
 * </pre>
 *
 * Where:
 * <ul>
 * <li><code>protocol</code> defines the connection protocol (for example:
 * <code>comm</code>, <code>http</code>)</li>
 * <li><code>name</code> is the name of the connection to open (for example:
 * <code>/dev/ttyS0</code>, <code>192.168.1.1</code>)</li>
 * <li><code>param</code> is a connection specific parameter
 * </ul>
 * <p>
 * The factory corresponding to the extracted protocol is dynamically bound and
 * its {@link ConnectionFactory#open} method is called on the specified URL.
 * </p>
 *
 * @see ConnectionFactory
 */
public class Connector extends Object {

	/**
	 * Default connection package.
	 */
	public static final String DEFAULT_PACKAGE = "org.eclipse.edje.connection";

	/**
	 * Forbidden constructor: connector cannot be instantiated.
	 */
	private Connector() {
	}

	/**
	 * Creates and opens a {@link Connection} from an URL. The
	 * {@link ConnectionFactory} implementation class must be available in the
	 * default package {@link #DEFAULT_PACKAGE} and sub package <code>xxx</code>
	 * where <code>xxx</code> is the connection protocol. The connection
	 * protocol (<code>xxx</code>) must be the first argument of URL, followed
	 * by ':'. The class name must be <code>ConnectionFactory</code>:
	 *
	 * <pre>
	 * org.eclipse.edje.connection.xxx.ConnectionFactory
	 * </pre>
	 *
	 * @param url
	 *            the connection URL to open
	 * @throws ConnectionNotFoundException
	 *             if connection protocol is not found
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws SecurityException
	 *             if a security manager exists and it does not allow the caller
	 *             to open such kind of connection
	 * @return the opened {@link Connection}.
	 */
	public static Connection open(String url) throws IOException {
		String key = DEFAULT_PACKAGE;
		String packageName = System.getProperty(key);
		// fall back to service name
		if (packageName == null) {
			packageName = Util.readConfigurableName(key);
		}
		if (packageName == null) {
			packageName = key;
		}

		return open(packageName, url);
	}

	/**
	 * Create and open a Connection with the specified {@link ConnectionFactory}
	 * package name. The {@link ConnectionFactory} implementation class must be
	 * available in the given package and sub package <code>xxx</code> where
	 * <code>xxx</code> is the connection protocol. The connection protocol (
	 * <code>xxx</code>) must be the first argument of URL, followed by ':'. The
	 * class name must be <code>ConnectionFactory</code>:
	 *
	 * <pre>
	 * package1.package2.package3.xxx.ConnectionFactory
	 * </pre>
	 *
	 * @param url
	 *            the connection URL to open
	 * @param packageName
	 *            the package name prefix of the {@link ConnectionFactory} to
	 *            bind
	 * @throws ConnectionNotFoundException
	 *             if connection protocol is not found
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws SecurityException
	 *             if a security manager exists and it does not allow the caller
	 *             to open such kind of connection
	 * @see ConnectionFactory
	 * @return the open {@link Connection}.
	 */
	public static Connection open(String packageName, String url) throws IOException {
		String protocol;
		try {
			protocol = url.substring(0, url.indexOf(':', 0));
		} catch (IndexOutOfBoundsException e) {
			throw new ConnectionNotFoundException(url, e);
		}
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(new ConnectionPermission(url));
		}

		Class<ConnectionFactory> connectionClass;
		connectionClass = getConnectionFactoryClass(packageName, url, protocol);

		ConnectionFactory connectionFactory;
		try {
			connectionFactory = connectionClass.newInstance();
			return connectionFactory.open(url);
		} catch (InstantiationException e) {
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			throw new SecurityException();
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<ConnectionFactory> getConnectionFactoryClass(String packageName, String url, String protocol)
			throws ConnectionNotFoundException {
		try {
			StringBuilder sbClass = new StringBuilder(packageName);
			sbClass.append('.').append(protocol).append(".ConnectionFactory");
			return (Class<ConnectionFactory>) Class.forName(sbClass.toString());
		} catch (Exception e1) {
			throw new ConnectionNotFoundException(url);
		}
	}
}
