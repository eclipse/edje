/*
 * Java
 *
 * Copyright 2013-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.eclipse.edje.comm;

import java.io.IOException;

/**
 * This interface defines methods for writing data bits.
 */
public interface BitsOutput {

	/**
	 * Returns the data length in bits (1 to 32 bits).
	 *
	 * @return the data length in bits (1 to 32 bits).
	 */
	public abstract int getLength();

	/**
	 * The data to send is masked using the following formula:<br>
	 * <code>val &amp;= (1 &lt;&lt; getLength()) - 1</code>
	 * 
	 * @param val
	 *            the value to send
	 *
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the output stream has been closed.
	 */
	public abstract void writeBits(int val) throws IOException;

	/**
	 * Writes <code>data.length</code> data from the specified integer array to
	 * this output stream. The general contract for <code>writeBits(data)</code>
	 * is that it should have exactly the same effect as the call
	 * <code>writeBits(data, 0, data.length)</code>.
	 *
	 * @param data
	 *            the data to send
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the output stream has been closed.
	 */
	public abstract void writeBits(int[] data) throws IOException;

	/**
	 * Writes <code>len</code> data from the specified integer array starting at
	 * offset <code>off</code> to this output stream. The general contract for
	 * <code>writeBits(b, off, len)</code> is that some of the data in the array
	 * <code>data</code> are written to the output stream in order; element
	 * <code>data[off]</code> is the first byte written and
	 * <code>b[off+len-1]</code> is the last data written by this operation.<br>
	 * <br>
	 * The <code>write</code> method of <code>OutputStream</code> calls the
	 * <code>write</code> method of one argument on each of the data to be
	 * written out. Subclasses are encouraged to override this method and
	 * provide a more efficient implementation.<br>
	 * <br>
	 * If <code>data</code> is null, a <code>NullPointerException</code> is
	 * thrown.<br>
	 * <br>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>data</code>, then an <code>IndexOutOfBoundsException</code> is
	 * thrown.
	 *
	 * @param data
	 *            the data to send
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of data to write.
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public abstract void writeBits(int[] data, int off, int len) throws IOException;

	/**
	 * Writes <code>data.length</code> data from the specified short array to
	 * this output stream. The general contract for
	 * <code>writeBits(data, signExtends)</code> is that it should have exactly
	 * the same effect as the call
	 * <code>writeBits(b, 0, b.length, signExtends)</code>.
	 *
	 * @param data
	 *            the data to send
	 * @param signExtends
	 *            true to sign-extend the data in case of
	 *            <code>getLength()</code> &gt; 16 (<code>short</code> size)
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the output stream has been closed.
	 */
	public abstract void writeBits(short[] data, boolean signExtends) throws IOException;

	/**
	 * Writes <code>len</code> data from the specified short array starting at
	 * offset <code>off</code> to this output stream. The general contract for
	 * <code>writeBits(b, off, len, signExtends)</code> is that some of the data
	 * in the array <code>data</code> are written to the output stream in order;
	 * element <code>data[off]</code> is the first byte written and
	 * <code>b[off+len-1]</code> is the last data written by this operation.<br>
	 * <br>
	 * The <code>write</code> method of <code>OutputStream</code> calls the
	 * <code>write</code> method of one argument on each of the data to be
	 * written out. Subclasses are encouraged to override this method and
	 * provide a more efficient implementation.<br>
	 * <br>
	 * If <code>data</code> is null, a <code>NullPointerException</code> is
	 * thrown.<br>
	 * <br>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>data</code>, then an <code>IndexOutOfBoundsException</code> is
	 * thrown. <br>
	 * If <code>getLength()</code> is higher than 16 (<code>short</code> size),
	 * the data will are sign extended or not according <code>signExtends</code>
	 * boolean.
	 *
	 * @param data
	 *            the data to send
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of data to write.
	 * @param signExtends
	 *            true to sign-extend the data in case of
	 *            <code>getLength()</code> &gt; 16 (<code>short</code> size)
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public abstract void writeBits(short[] data, int off, int len, boolean signExtends) throws IOException;

	/**
	 * Writes <code>data.length</code> data from the specified short array to
	 * this output stream. The general contract for
	 * <code>writeBits(data, signExtends)</code> is that it should have exactly
	 * the same effect as the call
	 * <code>writeBits(b, 0, b.length, signExtends)</code>.
	 *
	 * @param data
	 *            the data to send
	 * @param signExtends
	 *            true to sign-extend the data in case of
	 *            <code>getLength()</code> &gt; 8 (<code>byte</code> size)
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the output stream has been closed.
	 */
	public abstract void writeBits(byte[] data, boolean signExtends) throws IOException;

	/**
	 * Writes <code>len</code> data from the specified short array starting at
	 * offset <code>off</code> to this output stream. The general contract for
	 * <code>writeBits(b, off, len, signExtends)</code> is that some of the data
	 * in the array <code>data</code> are written to the output stream in order;
	 * element <code>data[off]</code> is the first byte written and
	 * <code>b[off+len-1]</code> is the last data written by this operation.<br>
	 * <br>
	 * The <code>write</code> method of <code>OutputStream</code> calls the
	 * <code>write</code> method of one argument on each of the data to be
	 * written out. Subclasses are encouraged to override this method and
	 * provide a more efficient implementation.<br>
	 * <br>
	 * If <code>data</code> is null, a <code>NullPointerException</code> is
	 * thrown.<br>
	 * <br>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>data</code>, then an <code>IndexOutOfBoundsException</code> is
	 * thrown. <br>
	 * If <code>getLength()</code> is higher than 8 (<code>byte</code> size),
	 * the data will are sign extended or not according <code>signExtends</code>
	 * boolean.
	 *
	 * @param data
	 *            the data to send
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of data to write.
	 * @param signExtends
	 *            true to sign-extend the data in case of
	 *            <code>getLength()</code> &gt; 8 (<code>byte</code> size)
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public abstract void writeBits(byte[] data, int off, int len, boolean signExtends) throws IOException;
}