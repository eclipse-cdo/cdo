/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ReflectUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public class BufferImpl implements Buffer
{
  public static final short HEADER_SIZE = 4;

  public static final short NO_CHANNEL = Short.MIN_VALUE;

  public static boolean TRACE = false;

  private static final int EOS_OFFSET = 1;

  private BufferProvider bufferProvider;

  private short channelID;

  private boolean eos;

  private State state = State.INITIAL;

  private ByteBuffer byteBuffer;

  public BufferImpl(BufferProvider provider, short capacity)
  {
    this.bufferProvider = provider;
    byteBuffer = ByteBuffer.allocateDirect(capacity);
  }

  public boolean isEOS()
  {
    return eos;
  }

  public void setEOS(boolean eos)
  {
    this.eos = eos;
  }

  public BufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(BufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public short getChannelID()
  {
    if (state == State.INITIAL || state == State.READING_HEADER)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    return channelID;
  }

  public short getCapacity()
  {
    return (short)byteBuffer.capacity();
  }

  public ByteBuffer getByteBuffer()
  {
    if (state != State.GETTING && state != State.PUTTING)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    return byteBuffer;
  }

  public State getState()
  {
    return state;
  }

  /**
   * TODO Check for multiply released buffers?
   */
  public void release()
  {
    if (bufferProvider != null)
    {
      bufferProvider.retainBuffer(this);
    }
  }

  public void clear()
  {
    byteBuffer.clear();
    state = State.INITIAL;
    channelID = NO_CHANNEL;
    eos = false;
  }

  public void dispose()
  {
    bufferProvider = null;
    byteBuffer = null;
  }

  public ByteBuffer startGetting(SocketChannel socketChannel) throws IOException
  {
    if (state != State.INITIAL && state != State.READING_HEADER && state != State.READING_BODY)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    if (state == State.INITIAL)
    {
      byteBuffer.limit(BufferImpl.HEADER_SIZE);
      state = State.READING_HEADER;
    }

    if (state == State.READING_HEADER)
    {
      int num = socketChannel.read(byteBuffer);
      if (num == -1)
      {
        throw new ClosedChannelException();
      }

      if (byteBuffer.hasRemaining())
      {
        return null;
      }

      byteBuffer.flip();
      channelID = byteBuffer.getShort();
      short payloadSize = byteBuffer.getShort();
      if (payloadSize < 0)
      {
        eos = true;
        payloadSize = (short)-payloadSize;
      }

      payloadSize -= EOS_OFFSET;

      byteBuffer.clear();
      byteBuffer.limit(payloadSize);
      state = State.READING_BODY;
    }

    // state == State.READING_BODY
    if (socketChannel.read(byteBuffer) == -1)
    {
      throw new ClosedChannelException();
    }

    if (byteBuffer.hasRemaining())
    {
      return null;
    }

    if (TRACE)
    {
      System.out.println(toString() + ": Read " + byteBuffer.limit() + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
          + (eos ? " (EOS)" : "")); //$NON-NLS-1$ //$NON-NLS-2$
      System.out.println(formatContent());
    }

    byteBuffer.flip();
    state = State.GETTING;
    return byteBuffer;
  }

  public ByteBuffer startPutting(short channelID)
  {
    if (state == State.PUTTING)
    {
      if (channelID != this.channelID)
      {
        throw new IllegalArgumentException("channelID != this.channelID"); //$NON-NLS-1$
      }
    }
    else if (state != State.INITIAL)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }
    else
    {
      state = State.PUTTING;
      this.channelID = channelID;

      byteBuffer.clear();
      byteBuffer.position(BufferImpl.HEADER_SIZE);
    }

    return byteBuffer;
  }

  public boolean write(SocketChannel socketChannel) throws IOException
  {
    if (state != State.PUTTING && state != State.WRITING)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    if (state == State.PUTTING)
    {
      if (channelID == NO_CHANNEL)
      {
        throw new IllegalStateException("channelID == NO_CHANNEL"); //$NON-NLS-1$
      }

      int payloadSize = byteBuffer.position() - BufferImpl.HEADER_SIZE + EOS_OFFSET;
      if (eos)
      {
        payloadSize = -payloadSize;
      }

      if (TRACE)
      {
        System.out.println(toString() + ": Writing " + (payloadSize - 1) + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
            + (eos ? " (EOS)" : "")); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println(formatContent());
      }

      byteBuffer.flip();
      byteBuffer.putShort(channelID);
      byteBuffer.putShort((short)payloadSize);
      byteBuffer.position(0);
      state = State.WRITING;
    }

    int numBytes = socketChannel.write(byteBuffer);
    if (numBytes == -1)
    {
      throw new IOException("Channel closed"); //$NON-NLS-1$
    }

    if (byteBuffer.hasRemaining())
    {
      return false;
    }

    clear();
    return true;
  }

  @Override
  public String toString()
  {
    return "Buffer@" + ReflectUtil.getID(this); //$NON-NLS-1$
  }

  public String formatContent()
  {
    final int oldPosition = byteBuffer.position();
    final int oldLimit = byteBuffer.limit();

    try
    {
      byteBuffer.flip();
      if (state == State.PUTTING)
      {
        byteBuffer.position(HEADER_SIZE);
      }

      StringBuilder builder = new StringBuilder();
      while (byteBuffer.hasRemaining())
      {
        byte b = byteBuffer.get();
        HexUtil.appendHex(builder, b < 0 ? ~b : b);
        builder.append(' ');
      }

      return builder.toString();
    }
    finally
    {
      byteBuffer.position(oldPosition);
      byteBuffer.limit(oldLimit);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static enum State
  {
    INITIAL, PUTTING, WRITING, READING_HEADER, READING_BODY, GETTING
  }
}
