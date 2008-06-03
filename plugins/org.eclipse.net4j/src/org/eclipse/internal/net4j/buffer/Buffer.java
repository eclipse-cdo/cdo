/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public class Buffer implements InternalBuffer
{
  private static final int EOS_OFFSET = 1;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER, Buffer.class);

  private IBufferProvider bufferProvider;

  private short channelIndex;

  private boolean eos;

  private BufferState state = BufferState.INITIAL;

  private ByteBuffer byteBuffer;

  public Buffer(IBufferProvider provider, short capacity)
  {
    bufferProvider = provider;
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

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public short getChannelIndex()
  {
    if (state == BufferState.INITIAL || state == BufferState.READING_HEADER)
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
    if (state != BufferState.GETTING && state != BufferState.PUTTING)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    return byteBuffer;
  }

  public BufferState getState()
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
    state = BufferState.INITIAL;
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
    if (state != BufferState.INITIAL && state != BufferState.READING_HEADER && state != BufferState.READING_BODY)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    if (state == BufferState.INITIAL)
    {
      byteBuffer.limit(IBuffer.HEADER_SIZE);
      state = BufferState.READING_HEADER;
    }

    if (state == BufferState.READING_HEADER)
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
      state = BufferState.READING_BODY;
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
          + (eos ? " (EOS)" : "") + StringUtil.NL + formatContent(false)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    byteBuffer.flip();
    state = BufferState.GETTING;
    return byteBuffer;
  }

  public ByteBuffer startPutting(short channelIndex)
  {
    if (state == BufferState.PUTTING)
    {
      if (channelIndex != this.channelIndex)
      {
        throw new IllegalArgumentException("channelIndex != this.channelIndex"); //$NON-NLS-1$
      }
    }
    else if (state != BufferState.INITIAL)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }
    else
    {
      state = BufferState.PUTTING;
      this.channelIndex = channelIndex;

      byteBuffer.clear();
      byteBuffer.position(IBuffer.HEADER_SIZE);
    }

    return byteBuffer;
  }

  /**
   * @return <code>true</code> if the buffer has been completely written, <code>false</code> otherwise.
   */
  public boolean write(SocketChannel socketChannel) throws IOException
  {
    if (state != BufferState.PUTTING && state != BufferState.WRITING)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    if (state == BufferState.PUTTING)
    {
      if (channelIndex == NO_CHANNEL)
      {
        throw new IllegalStateException("channelIndex == NO_CHANNEL"); //$NON-NLS-1$
      }

      int payloadSize = byteBuffer.position() - IBuffer.HEADER_SIZE + EOS_OFFSET;
      if (eos)
      {
        payloadSize = -payloadSize;
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Writing " + (Math.abs(payloadSize) - 1) + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
            + (eos ? " (EOS)" : "") + StringUtil.NL + formatContent(false)); //$NON-NLS-1$ //$NON-NLS-2$
      }

      byteBuffer.flip();
      byteBuffer.putShort(channelIndex);
      byteBuffer.putShort((short)payloadSize);
      byteBuffer.position(0);
      state = BufferState.WRITING;
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
    if (state != BufferState.PUTTING)
    {
      throw new IllegalStateException("state == " + state); //$NON-NLS-1$
    }

    byteBuffer.flip();
    byteBuffer.position(IBuffer.HEADER_SIZE);
    state = BufferState.GETTING;
  }

  @Override
  public String toString()
  {
    return "Buffer@" + ReflectUtil.getID(this); //$NON-NLS-1$
  }

  @SuppressWarnings("deprecation")
  public String formatContent(boolean showHeader)
  {
    final int oldPosition = byteBuffer.position();
    final int oldLimit = byteBuffer.limit();

    try
    {
      if (state != BufferState.GETTING)
      {
        byteBuffer.flip();
      }

      if (state == BufferState.PUTTING && !showHeader)
      {
        byteBuffer.position(IBuffer.HEADER_SIZE);
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
}
