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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

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

  private static final int EOS_OFFSET = 1;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_BUFFER,
      BufferImpl.class);

  private static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

  private BufferProvider bufferProvider;

  private short channelIndex;

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

  public short getChannelIndex()
  {
    if (state == State.INITIAL || state == State.READING_HEADER)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    return channelIndex;
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
    channelIndex = NO_CHANNEL;
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
      channelIndex = byteBuffer.getShort();
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

    if (TRACER.isEnabled())
    {
      TRACER.trace("Read " + byteBuffer.limit() + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
          + (eos ? " (EOS)" : "") + NL + formatContent()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    byteBuffer.flip();
    state = State.GETTING;
    return byteBuffer;
  }

  public ByteBuffer startPutting(short channelIndex)
  {
    if (state == State.PUTTING)
    {
      if (channelIndex != this.channelIndex)
      {
        throw new IllegalArgumentException("channelIndex != this.channelIndex"); //$NON-NLS-1$
      }
    }
    else if (state != State.INITIAL)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }
    else
    {
      state = State.PUTTING;
      this.channelIndex = channelIndex;

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
      if (channelIndex == NO_CHANNEL)
      {
        throw new IllegalStateException("channelIndex == NO_CHANNEL"); //$NON-NLS-1$
      }

      int payloadSize = byteBuffer.position() - BufferImpl.HEADER_SIZE + EOS_OFFSET;
      if (eos)
      {
        payloadSize = -payloadSize;
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Writing " + (payloadSize - 1) + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
            + (eos ? " (EOS)" : "") + NL + formatContent()); //$NON-NLS-1$ //$NON-NLS-2$
      }

      byteBuffer.flip();
      byteBuffer.putShort(channelIndex);
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

  public void flip()
  {
    if (state != State.PUTTING)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    byteBuffer.flip();
    byteBuffer.position(HEADER_SIZE);
    state = State.GETTING;
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
      if (state != State.GETTING)
      {
        byteBuffer.flip();
      }
      
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
