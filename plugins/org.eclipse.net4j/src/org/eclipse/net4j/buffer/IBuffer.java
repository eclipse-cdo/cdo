/*
 * Copyright (c) 2007-2012, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.IErrorHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Basic <b>unit of transport</b> in Net4j.
 * <p>
 * A buffer is well prepared for the usage with asynchronous {@link IChannel}s but can also be used with pure
 * {@link SocketChannel}s. All methods of <code>IBuffer</code> are non-blocking.
 * <p>
 * Usually buffers are obtained from a {@link IBufferProvider}. Buffers can be accessed, passed around and finally
 * {@link #release() released} to their original provider. The capacity of a buffer is determined by its provider.
 * <p>
 * In addition to its payload data each buffer contains an internal header of four bytes, two of them representing a
 * channel identifier the other two of them denoting the length of the payload data. The payload data may be accessed
 * through a {@link #getByteBuffer() ByteBuffer}.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients.
 * <p>
 * An example for <b>putting</b> values into a buffer and writing it to a {@link SocketChannel}:
 * <p>
 *
 * <pre style="background-color:#ffffc8; border-width:1px; border-style:solid; padding:.5em;">
 * // Obtain a fresh buffer
 * Buffer buffer = bufferProvider.getBuffer(); // Start filling the buffer for channelID 4711 ByteBuffer byteBuffer =
 * buffer.startPutting(4711); byteBuffer.putDouble(15.47); // Write the contents of the Buffer to a // SocketChannel
 * without blocking while (!buffer.write(socketChannel)) { // Do something else }
 * </pre>
 *
 * An example for reading a buffer from a {@link SocketChannel} and <b>getting</b> values from it:
 * <p>
 *
 * <pre style="background-color:#ffffc8; border-width:1px; border-style:solid; padding:.5em;">
 * // Obtain a fresh buffer
 * Buffer buffer = bufferProvider.getBuffer();
 *
 * // Read the contents of the Buffer from a SocketChannel without blocking
 * ByteBuffer byteBuffer;
 * while ((byteBuffer = buffer.startGetting(socketChannel)) == null)
 * {
 *   // Do something else
 * }
 *
 * // Access the contents of the buffer and release it to its provider
 * double value = byteBuffer.getDouble();
 * buffer.release();
 * </pre>
 *
 * @see IBufferProvider
 * @see IChannel#sendBuffer(IBuffer)
 * @see IChannel#setReceiveHandler(IBufferHandler)
 * @see IBufferHandler#handleBuffer(IBuffer)
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IBuffer
{
  /**
   * Possible argument value of {@link #startPutting(short)} and possible return value of {@link #getChannelID()} that
   * indicates that this buffer is not intended to be passed to a {@link SocketChannel}.
   */
  public static final short NO_CHANNEL = Short.MIN_VALUE;

  /**
   * @since 2.0
   */
  public static final short MIN_CHANNEL = 1;

  /**
   * @since 2.0
   */
  public static final short MAX_CHANNEL = Short.MAX_VALUE;

  /**
   * @since 4.8
   */
  public static final int CHANNEL_ID_BYTES = Short.BYTES;

  /**
   * @since 4.8
   */
  public static final int PAYLOAD_SIZE_BYTES = Short.BYTES;

  public static final short HEADER_SIZE = CHANNEL_ID_BYTES + PAYLOAD_SIZE_BYTES;

  /**
   * @since 4.10
   */
  public static final int CHANNEL_ID_POS = 0;

  /**
   * @since 4.10
   */
  public static final int PAYLOAD_SIZE_POS = Short.BYTES;

  /**
   * Returns the {@link IBufferProvider} that has provided this buffer and that this buffer will be returned to when its
   * {@link #release()} method is called.
   */
  public IBufferProvider getBufferProvider();

  /**
   * Returns the channel index value stored in the header of this buffer.
   *
   * @since 2.0
   */
  public short getChannelID();

  /**
   * Returns the capacity of this buffer.
   * <p>
   * The capacity of this buffer is equal to the {@link IBufferProvider#getBufferCapacity() capacity} of the
   * {@link IBufferProvider} that has provided this buffer.
   */
  public short getCapacity();

  /**
   * Returns the internal state of this buffer.
   */
  public BufferState getState();

  /**
   * Tries to read a {@link ByteBuffer} from a {@link SocketChannel} that can be used for getting data.
   * <p>
   * This method is non-blocking and it can be necessary to repeatedly call it. If it was not possible to read a
   * complete header from the <code>SocketChannel</code> <code>null</code> is returned and the state of this buffer is
   * {@link BufferState#READING_HEADER READING_HEADER}. If it was not possible to read a complete body from the
   * <code>SocketChannel</code> <code>null</code> is returned and the state of this buffer is
   * {@link BufferState#READING_BODY READING_BODY}.
   * <p>
   * If a <code>ByteBuffer</code> is returned it <b>may only</b> be used for getting data. It is left to the
   * responsibility of the caller that only the following methods of that <code>ByteBuffer</code> are used:
   * <ul>
   * <li> {@link ByteBuffer#get()}
   * <li> {@link ByteBuffer#get(byte[])}
   * <li> {@link ByteBuffer#get(int)}
   * <li> {@link ByteBuffer#get(byte[], int, int)}
   * <li> {@link ByteBuffer#getChar()}
   * <li> {@link ByteBuffer#getChar(int)}
   * <li> {@link ByteBuffer#getDouble()}
   * <li> {@link ByteBuffer#getDouble(int)}
   * <li> {@link ByteBuffer#getFloat()}
   * <li> {@link ByteBuffer#getFloat(int)}
   * <li> {@link ByteBuffer#getInt()}
   * <li> {@link ByteBuffer#getInt(int)}
   * <li> {@link ByteBuffer#getLong()}
   * <li> {@link ByteBuffer#getLong(int)}
   * <li> {@link ByteBuffer#getShort()}
   * <li> {@link ByteBuffer#getShort(int)}
   * <li>all other methods that do not influence {@link ByteBuffer#position()}, {@link ByteBuffer#limit()} and
   * {@link ByteBuffer#capacity()}
   * </ul>
   *
   * @param socketChannel
   *          The <code>socketChannel</code> to read the {@link ByteBuffer} from.
   * @return A {@link ByteBuffer} that can be used for getting data if it was possible to completely read the data from
   *         the given <code>SocketChannel</code>, <code>null</code> otherwise.
   * @throws IllegalStateException
   *           If the state of this buffer is not {@link BufferState#INITIAL INITIAL},
   *           {@link BufferState#READING_HEADER READING_HEADER} or {@link BufferState#READING_BODY READING_BODY}.
   * @throws IOException
   *           If the <code>SocketChannel</code> has been closed or discovers other I/O problems.
   */
  public ByteBuffer startGetting(SocketChannel socketChannel) throws IllegalStateException, IOException;

  /**
   * Returns a {@link ByteBuffer} that can be used for putting data.
   * <p>
   * Turns the {@link #getState() state} of this buffer into {@link BufferState#PUTTING PUTTING}.
   * <p>
   * The returned <code>ByteBuffer</code> <b>may only</b> be used for putting data. It is left to the responsibility of
   * the caller that only the following methods of that <code>ByteBuffer</code> are used:
   * <ul>
   * <li> {@link ByteBuffer#put(byte)}
   * <li> {@link ByteBuffer#put(byte[])}
   * <li> {@link ByteBuffer#put(ByteBuffer)}
   * <li> {@link ByteBuffer#put(int, byte)}
   * <li> {@link ByteBuffer#put(byte[], int, int)}
   * <li> {@link ByteBuffer#putChar(char)}
   * <li> {@link ByteBuffer#putChar(int, char)}
   * <li> {@link ByteBuffer#putDouble(double)}
   * <li> {@link ByteBuffer#putDouble(int, double)}
   * <li> {@link ByteBuffer#putFloat(float)}
   * <li> {@link ByteBuffer#putFloat(int, float)}
   * <li> {@link ByteBuffer#putInt(int)}
   * <li> {@link ByteBuffer#putInt(int, int)}
   * <li> {@link ByteBuffer#putLong(long)}
   * <li> {@link ByteBuffer#putLong(int, long)}
   * <li> {@link ByteBuffer#putShort(short)}
   * <li> {@link ByteBuffer#putShort(int, short)}
   * <li>all other methods that do not influence {@link ByteBuffer#position()}, {@link ByteBuffer#limit()} and
   * {@link ByteBuffer#capacity()}
   * </ul>
   *
   * @param channelID
   *          The index of an {@link IChannel} that this buffer is intended to be passed to later or {@link #NO_CHANNEL}
   *          .
   * @return A {@link ByteBuffer} that can be used for putting data.
   * @throws IllegalStateException
   *           If the state of this buffer is not {@link BufferState#INITIAL INITIAL} ({@link BufferState#PUTTING
   *           PUTTING} is allowed but meaningless if and only if the given <code>channelID</code> is equal to the
   *           existing <code>channelID</code> of this buffer).
   */
  public ByteBuffer startPutting(short channelID) throws IllegalStateException;

  /**
   * Tries to write the data of this buffer to a {@link SocketChannel}.
   * <p>
   * This method is non-blocking and it can be necessary to repeatedly call it. If it was not possible to completely
   * write the data to the <code>SocketChannel</code> <code>false</code> is returned and the state of this buffer
   * remains {@link BufferState#WRITING WRITING}.
   *
   * @param socketChannel
   *          The <code>socketChannel</code> to write the data to.
   * @return <code>true</code> if it was possible to completely write the data to the <code>SocketChannel</code>,
   *         <code>false</code> otherwise.
   * @throws IllegalStateException
   *           If the state of this buffer is not {@link BufferState#PUTTING PUTTING} or {@link BufferState#WRITING
   *           WRITING}.
   * @throws IOException
   *           If the <code>SocketChannel</code> has been closed or discovers other I/O problems.
   */
  public boolean write(SocketChannel socketChannel) throws IllegalStateException, IOException;

  /**
   * Turns the state of this buffer from {@link BufferState#PUTTING PUTTING} into {@link BufferState#GETTING GETTING}.
   *
   * @throws IllegalStateException
   *           If the state of this buffer is not {@link BufferState#PUTTING PUTTING}.
   */
  public void flip() throws IllegalStateException;

  /**
   * Returns the <code>ByteBuffer</code> that can be used for putting or getting data.
   *
   * @throws IllegalStateException
   *           If the state of this buffer is not {@link BufferState#PUTTING PUTTING} or {@link BufferState#GETTING
   *           GETTING}.
   */
  public ByteBuffer getByteBuffer() throws IllegalStateException;

  /**
   * Returns the <em>End Of Stream</em> flag to indicate whether this buffer is the last buffer in a stream (typically a signal) of buffers.
   */
  public boolean isEOS();

  /**
   * Sets the <em>End Of Stream</em> flag to indicate whether this buffer is the last buffer in a stream (typically a signal) of buffers.
   */
  public void setEOS(boolean eos);

  /**
   * Returns the <em>Close Channel After Me</em> flag.
   *
   * @since 4.4
   */

  public boolean isCCAM();

  /**
   * Sets the <em>Close Channel After Me</em> flag.
   *
   * @since 4.4
   */
  public void setCCAM(boolean ccam);

  /**
   * Releases this buffer to its original {@link IBufferProvider}.
   */
  public void release();

  /**
   * Turns the state of this buffer from any state into {@link BufferState#INITIAL INITIAL}.
   */
  public void clear();

  public String formatContent(boolean showHeader);

  /**
   * @since 2.0
   */
  public IErrorHandler getErrorHandler();

  /**
   * @since 2.0
   */
  public void setErrorHandler(IErrorHandler errorHandler);

  /**
   * @since 4.7
   */
  public void compact();

  /**
   * @since 4.7
   */
  public int getPosition();

  /**
   * @since 4.7
   */
  public void setPosition(int position);

  /**
   * @since 4.7
   */
  public int getLimit();

  /**
   * @since 4.7
   */
  public void setLimit(int limit);

  /**
   * @since 4.7
   */
  public boolean hasRemaining();

  /**
   * @since 4.7
   */
  public byte get();

  /**
   * @since 4.7
   */
  public void get(byte[] dst);

  /**
   * @since 4.7
   */
  public short getShort();

  /**
   * @since 4.7
   */
  public int getInt();

  /**
   * @since 4.7
   */
  public String getString();

  /**
   * @since 4.7
   */
  public void put(byte b);

  /**
   * @since 4.7
   */
  public void put(byte[] src, int offset, int length);

  /**
   * @since 4.7
   */
  public void putShort(short value);
}
