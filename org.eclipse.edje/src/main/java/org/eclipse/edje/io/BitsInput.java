/*
 * Java
 *
 * Copyright 2013-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.eclipse.edje.io;

import java.io.EOFException;
import java.io.IOException;

/**
 * This interface defines methods for reading data bits.
 */
public interface BitsInput {

	/**
	 * Returns the data length in bits (1 to 32 bits).
	 *
	 * @return the data length in bits (1 to 32 bits).
	 */
	public abstract int getLength();

	/**
	 * Reads the next value of data from the input stream. The value is returned
	 * as an <code>int</code> in the range <code>0</code> to
	 * <code>(1 &lt;&lt; getLength) - 1</code>. If no value is available because
	 * the end of the stream has been reached, the value an EOFException is
	 * thrown. This method blocks until input data is available, the end of the
	 * stream is detected, or an exception is thrown.<br>
	 * <br>
	 * A subclass must provide an implementation of this method.
	 *
	 * @param signExtends
	 *            true to sign-extends the result using the formula
	 *            <code>ret = (ret &lt;&lt; (32-getLength())) &gt;&gt; (32-getLength())</code>
	 * @return the next value
	 * @throws IOException
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the input stream has been closed.
	 * @throws EOFException
	 *             if the end of stream is reached
	 */
	public abstract int readBits(boolean signExtends) throws IOException, EOFException;

	/**
	 * Reads some number of values from the input stream and stores them into
	 * the buffer array <code>data</code>. The number of values actually read is
	 * returned as an integer. This method blocks until input value is
	 * available, end of file is detected, or an exception is thrown.<br>
	 * <br>
	 * If <code>data</code> is null, a <code>NullPointerException</code> is
	 * thrown. If the length of <code>data</code> is zero, then no value are
	 * read and <code>0</code> is returned; otherwise, there is an attempt to
	 * read at least one value. If no value is available because the stream is
	 * at end of file, an <code>EOFException</code> is thrown; otherwise, at
	 * least one value is read and stored into <code>data</code>.<br>
	 * <br>
	 * The first value read is stored into element <code>data[0]</code>, the
	 * next one into <code>data[1]</code>, and so on. The number of value read
	 * is, at most, equal to the length of <code>data</code>. Let <code>k</code>
	 * be the number of values actually read; these values will be stored in
	 * elements <code>data[0]</code> through <code>data[k-1]</code>, leaving
	 * elements <code>data[k]</code> through <code>data[data.length-1]</code>
	 * unaffected.<br>
	 * <br>
	 * If the first value cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.<br>
	 * <br>
	 * The <code>readBits(data, signExtends)</code> method for class
	 * <code>BitsInput</code> has the same effect as:<br>
	 * <code>readBits(data, 0, data.length, signExtends)</code>
	 *
	 * @param data
	 *            the buffer into which the values are read.
	 * @param signExtends
	 *            true to sign-extends the result.
	 * @return the total number of values read into the buffer, or -1 is there
	 *         is no more data because the end of the stream has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract int readBits(int[] data, boolean signExtends) throws IOException;

	/**
	 * Reads up to <code>len</code> values of data from the input stream into an
	 * array of values. An attempt is made to read as many as <code>len</code>
	 * values, but a smaller number may be read, possibly zero. The number of
	 * values actually read is returned as an integer.<br>
	 * <br>
	 * This method blocks until input data is available, end of file is
	 * detected, or an exception is thrown.<br>
	 * <br>
	 * If <code>data</code> is null, a NullPointerException is thrown.<br>
	 * <br>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>b</code>, then an IndexOutOfBoundsException is thrown.<br>
	 * <br>
	 * If <code>len</code> is zero, then no values are read and 0 is returned;
	 * otherwise, there is an attempt to read at least one value. If no value is
	 * available because the stream is at end of file, the value -1 is returned;
	 * otherwise, at least one value is read and stored into <code>data</code>.
	 * <br>
	 * <br>
	 * The first value read is stored into element <code>data[off]</code>, the
	 * next one into <code>data[off+1]</code>, and so on. The number of values
	 * read is, at most, equal to <code>len</code>. Let <code>k</code> be the
	 * number of values actually read; these values will be stored in elements
	 * <code>data[off]</code> through <code>data[off+k-1]</code>, leaving
	 * elements <code>data[off+k]</code> through <code>data[off+len-1]</code>
	 * unaffected.<br>
	 * <br>
	 * In every case, elements <code>data[0]</code> through
	 * <code>data[off]</code> and elements <code>data[off+len]</code> through
	 * <code>data[b.length-1]</code> are unaffected.<br>
	 * <br>
	 * If the first value cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.<br>
	 * <br>
	 * The <code>readBits(data, off, len, signExtends)</code> method for class
	 * BitsInput simply calls the method <code>readBits(signExtends)</code>
	 * repeatedly. If the first such call results in an IOException, that
	 * exception is returned from the call to the <code>readBits(data,
	 * off, len, signExte,ds)</code> method. If any subsequent call to
	 * <code>readBits(boolean)</code> results in a IOException, the exception is
	 * caught and treated as if it were end of file; the values read up to that
	 * point are stored into <code>data</code> and the number of values read
	 * before the exception occurred is returned. Subclasses are encouraged to
	 * provide a more efficient implementation of this method.
	 *
	 * @param data
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in array <code>data</code> at which the data
	 *            is written.
	 * @param len
	 *            the maximum number of values to read.
	 * @param signExtends
	 *            true to sign-extends the result.
	 * @return the total number of bytes read into the buffer, or -1 if there is
	 *         no more data because the end of the stream has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract int readBits(int[] data, int off, int len, boolean signExtends) throws IOException;

	/**
	 * Reads some number of values from the input stream and stores them into
	 * the buffer array <code>data</code>. The number of values actually read is
	 * returned as an integer. This method blocks until input value is
	 * available, end of file is detected, or an exception is thrown.<br>
	 * <br>
	 * If <code>data</code> is null, a <code>NullPointerException</code> is
	 * thrown. If the length of <code>data</code> is zero, then no value are
	 * read and <code>0</code> is returned; otherwise, there is an attempt to
	 * read at least one value. If no value is available because the stream is
	 * at end of file, an <code>EOFException</code> is thrown; otherwise, at
	 * least one value is read and stored into <code>data</code>.<br>
	 * <br>
	 * The first value read is stored into element <code>data[0]</code>, the
	 * next one into <code>data[1]</code>, and so on. The number of value read
	 * is, at most, equal to the length of <code>data</code>. Let <code>k</code>
	 * be the number of values actually read; these values will be stored in
	 * elements <code>data[0]</code> through <code>data[k-1]</code>, leaving
	 * elements <code>data[k]</code> through <code>data[data.length-1]</code>
	 * unaffected.<br>
	 * <br>
	 * If the first value cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.<br>
	 * <br>
	 * The <code>readBits(data, signExtends)</code> method for class
	 * <code>BitsInput</code> has the same effect as:<br>
	 * <code>readBits(data, 0, data.length, signExtends)</code>
	 *
	 * @param data
	 *            the buffer into which the values are read.
	 * @param signExtends
	 *            true to sign-extends the result.
	 * @return the total number of values read into the buffer, or -1 is there
	 *         is no more data because the end of the stream has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract int readBits(short[] data, boolean signExtends) throws IOException;

	/**
	 * Reads up to <code>len</code> values of data from the input stream into an
	 * array of values. An attempt is made to read as many as <code>len</code>
	 * values, but a smaller number may be read, possibly zero. The number of
	 * values actually read is returned as an integer.<br>
	 * <br>
	 * This method blocks until input data is available, end of file is
	 * detected, or an exception is thrown.<br>
	 * <br>
	 * If <code>data</code> is null, a NullPointerException is thrown.<br>
	 * <br>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>b</code>, then an IndexOutOfBoundsException is thrown.<br>
	 * <br>
	 * If <code>len</code> is zero, then no values are read and 0 is returned;
	 * otherwise, there is an attempt to read at least one value. If no value is
	 * available because the stream is at end of file, the value -1 is returned;
	 * otherwise, at least one value is read and stored into <code>data</code>.
	 * <br>
	 * <br>
	 * The first value read is stored into element <code>data[off]</code>, the
	 * next one into <code>data[off+1]</code>, and so on. The number of values
	 * read is, at most, equal to <code>len</code>. Let <code>k</code> be the
	 * number of values actually read; these values will be stored in elements
	 * <code>data[off]</code> through <code>data[off+k-1]</code>, leaving
	 * elements <code>data[off+k]</code> through <code>data[off+len-1]</code>
	 * unaffected.<br>
	 * <br>
	 * In every case, elements <code>data[0]</code> through
	 * <code>data[off]</code> and elements <code>data[off+len]</code> through
	 * <code>data[b.length-1]</code> are unaffected.<br>
	 * <br>
	 * If the first value cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.<br>
	 * <br>
	 * The <code>readBits(data, off, len, signExtends)</code> method for class
	 * BitsInput simply calls the method <code>readBits(signExtends)</code>
	 * repeatedly. If the first such call results in an IOException, that
	 * exception is returned from the call to the <code>readBits(data,
	 * off, len, signExte,ds)</code> method. If any subsequent call to
	 * <code>readBits(boolean)</code> results in a IOException, the exception is
	 * caught and treated as if it were end of file; the values read up to that
	 * point are stored into <code>data</code> and the number of values read
	 * before the exception occurred is returned. Subclasses are encouraged to
	 * provide a more efficient implementation of this method.
	 *
	 * @param data
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in array <code>data</code> at which the data
	 *            is written.
	 * @param len
	 *            the maximum number of values to read.
	 * @param signExtends
	 *            true to sign-extends the result.
	 * @return the total number of bytes read into the buffer, or -1 if there is
	 *         no more data because the end of the stream has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract int readBits(short[] data, int off, int len, boolean signExtends) throws IOException;

	/**
	 * Reads some number of values from the input stream and stores them into
	 * the buffer array <code>data</code>. The number of values actually read is
	 * returned as an integer. This method blocks until input value is
	 * available, end of file is detected, or an exception is thrown.<br>
	 * <br>
	 * If <code>data</code> is null, a <code>NullPointerException</code> is
	 * thrown. If the length of <code>data</code> is zero, then no value are
	 * read and <code>0</code> is returned; otherwise, there is an attempt to
	 * read at least one value. If no value is available because the stream is
	 * at end of file, an <code>EOFException</code> is thrown; otherwise, at
	 * least one value is read and stored into <code>data</code>.<br>
	 * <br>
	 * The first value read is stored into element <code>data[0]</code>, the
	 * next one into <code>data[1]</code>, and so on. The number of value read
	 * is, at most, equal to the length of <code>data</code>. Let <code>k</code>
	 * be the number of values actually read; these values will be stored in
	 * elements <code>data[0]</code> through <code>data[k-1]</code>, leaving
	 * elements <code>data[k]</code> through <code>data[data.length-1]</code>
	 * unaffected.<br>
	 * <br>
	 * If the first value cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.<br>
	 * <br>
	 * The <code>readBits(data, signExtends)</code> method for class
	 * <code>BitsInput</code> has the same effect as:<br>
	 * <code>readBits(data, 0, data.length, signExtends)</code>
	 *
	 * @param data
	 *            the buffer into which the values are read.
	 * @param signExtends
	 *            true to sign-extends the result.
	 * @return the total number of values read into the buffer, or -1 is there
	 *         is no more data because the end of the stream has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract int readBits(byte[] data, boolean signExtends) throws IOException;

	/**
	 * Reads up to <code>len</code> values of data from the input stream into an
	 * array of values. An attempt is made to read as many as <code>len</code>
	 * values, but a smaller number may be read, possibly zero. The number of
	 * values actually read is returned as an integer.<br>
	 * <br>
	 * This method blocks until input data is available, end of file is
	 * detected, or an exception is thrown.<br>
	 * <br>
	 * If <code>data</code> is null, a NullPointerException is thrown.<br>
	 * <br>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>b</code>, then an IndexOutOfBoundsException is thrown.<br>
	 * <br>
	 * If <code>len</code> is zero, then no values are read and 0 is returned;
	 * otherwise, there is an attempt to read at least one value. If no value is
	 * available because the stream is at end of file, the value -1 is returned;
	 * otherwise, at least one value is read and stored into <code>data</code>.
	 * <br>
	 * <br>
	 * The first value read is stored into element <code>data[off]</code>, the
	 * next one into <code>data[off+1]</code>, and so on. The number of values
	 * read is, at most, equal to <code>len</code>. Let <code>k</code> be the
	 * number of values actually read; these values will be stored in elements
	 * <code>data[off]</code> through <code>data[off+k-1]</code>, leaving
	 * elements <code>data[off+k]</code> through <code>data[off+len-1]</code>
	 * unaffected.<br>
	 * <br>
	 * In every case, elements <code>data[0]</code> through
	 * <code>data[off]</code> and elements <code>data[off+len]</code> through
	 * <code>data[b.length-1]</code> are unaffected.<br>
	 * <br>
	 * If the first value cannot be read for any reason other than end of file,
	 * then an IOException is thrown. In particular, an IOException is thrown if
	 * the input stream has been closed.<br>
	 * <br>
	 * The <code>readBits(data, off, len, signExtends)</code> method for class
	 * BitsInput simply calls the method <code>readBits(signExtends)</code>
	 * repeatedly. If the first such call results in an IOException, that
	 * exception is returned from the call to the <code>readBits(data,
	 * off, len, signExte,ds)</code> method. If any subsequent call to
	 * <code>readBits(boolean)</code> results in a IOException, the exception is
	 * caught and treated as if it were end of file; the values read up to that
	 * point are stored into <code>data</code> and the number of values read
	 * before the exception occurred is returned. Subclasses are encouraged to
	 * provide a more efficient implementation of this method.
	 *
	 * @param data
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in array <code>data</code> at which the data
	 *            is written.
	 * @param len
	 *            the maximum number of values to read.
	 * @param signExtends
	 *            true to sign-extends the result.
	 * @return the total number of bytes read into the buffer, or -1 if there is
	 *         no more data because the end of the stream has been reached.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract int readBits(byte[] data, int off, int len, boolean signExtends) throws IOException;
}