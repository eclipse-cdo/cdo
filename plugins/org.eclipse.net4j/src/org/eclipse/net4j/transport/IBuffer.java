/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Basic <b>unit of transport</b> in Net4j. A buffer is well prepared for the
 * usage with asynchronous {@link IChannel}s but can also be used with pure
 * {@link SocketChannel}s. All methods of <code>Buffer</code> are
 * non-blocking.
 * <p>
 * Usually buffers are obtained from a {@link IBufferProvider}. Buffers can be
 * acessed, passed around and finally {@link #release() released} to their
 * original provider. The capacity of a buffer is determined by its provider.
 * <p>
 * In addition to its payload data each buffer contains an internal header of
 * four bytes, two of them representing a channel identifier the other two of
 * them denoting the length of the payload data. The payload data may be
 * accessed through a {@link #getByteBuffer() ByteBuffer} (TODO see restrictions
 * below).
 * <p>
 * An example for <b>putting</b> values into a buffer and writing it to a
 * {@link SocketChannel}:
 * <p>
 * 
 * <pre>
 * // Obtain a fresh buffer 
 * Buffer buffer = bufferProvider.getBuffer();
 * 
 * // Start filling the buffer for channelIndex 4711
 * ByteBuffer byteBuffer = buffer.startPutting(4711);
 * byteBuffer.putDouble(15.47);
 * 
 * // Write the contents of the Buffer to a 
 * // SocketChannel without blocking 
 * while (!buffer.write(socketChannel))
 * {
 *   // Do something else
 * }
 * </pre>
 * 
 * An example for reading a buffer from a {@link SocketChannel} and <b>getting</b>
 * values from it:
 * <p>
 * 
 * <pre>
 * // Obtain a fresh buffer 
 * Buffer buffer = bufferProvider.getBuffer();
 * 
 * // Read the contents of the Buffer from a 
 * // SocketChannel without blocking 
 * ByteBuffer byteBuffer;
 * while ((byteBuffer = buffer.startGetting(socketChannel)) == null)
 * {
 *   // Do something else
 * }
 * 
 * // Access the contents of the buffer and 
 * // release it to its provider
 * double value = byteBuffer.getDouble();
 * buffer.release();
 * </pre>
 * 
 * @see IBufferProvider
 * @see IChannel#sendBuffer(Buffer)
 * @see IChannel#setReceiveHandler(IBufferHandler)
 * @see IBufferHandler#handleBuffer(Buffer)
 * @author Eike Stepper
 */
public interface IBuffer
{
  public IBufferProvider getBufferProvider();

  public short getChannelIndex();

  public short getCapacity();

  public ByteBuffer startGetting(SocketChannel socketChannel) throws IOException;

  public ByteBuffer startPutting(short channelIndex);

  public boolean write(SocketChannel socketChannel) throws IOException;

  public void flip();

  public ByteBuffer getByteBuffer();

  public boolean isEOS();

  public void setEOS(boolean eos);

  public void release();

  public void clear();
}
