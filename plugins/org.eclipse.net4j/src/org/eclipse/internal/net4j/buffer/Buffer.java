/*
 * Copyright (c) 2007-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.IErrorHandler;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalBuffer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class Buffer implements InternalBuffer
{
  private static final byte FLAG_EOS = 1 << 0;

  private static final byte FLAG_CCAM = 1 << 1;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER, Buffer.class);

  private static AtomicInteger lastID = new AtomicInteger();

  private int id = lastID.incrementAndGet();

  private IErrorHandler errorHandler;

  private IBufferProvider bufferProvider;

  private ByteBuffer byteBuffer;

  private BufferState state = BufferState.INITIAL;

  private short channelID;

  private byte flags;

  public Buffer(IBufferProvider provider, short capacity)
  {
    bufferProvider = provider;
    byteBuffer = ByteBuffer.allocateDirect(capacity);
  }

  public final int getID()
  {
    return id;
  }

  @Override
  public boolean isEOS()
  {
    return (flags & FLAG_EOS) != 0;
  }

  @Override
  public void setEOS(boolean eos)
  {
    if (eos)
    {
      flags |= FLAG_EOS;
    }
    else
    {
      flags &= ~FLAG_EOS;
    }
  }

  @Override
  public boolean isCCAM()
  {
    return (flags & FLAG_CCAM) != 0;
  }

  @Override
  public void setCCAM(boolean ccam)
  {
    if (ccam)
    {
      flags |= FLAG_CCAM;
    }
    else
    {
      flags &= ~FLAG_CCAM;
    }
  }

  @Override
  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  @Override
  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  @Override
  public short getChannelID()
  {
    if (state == BufferState.INITIAL || state == BufferState.READING_HEADER)
    {
      throw new IllegalStateException(toString());
    }

    return channelID;
  }

  public void setChannelID(short channelID)
  {
    this.channelID = channelID;
  }

  @Override
  public short getCapacity()
  {
    return (short)byteBuffer.capacity();
  }

  @Override
  public BufferState getState()
  {
    return state;
  }

  public void setState(BufferState state)
  {
    this.state = state;
  }

  @Override
  public ByteBuffer getByteBuffer()
  {
    return byteBuffer;
  }

  public void setByteBuffer(ByteBuffer buffer)
  {
    byteBuffer = buffer;
  }

  @Override
  public void clear()
  {
    state = BufferState.INITIAL;
    channelID = NO_CHANNEL;
    flags = 0;
    byteBuffer.clear();
  }

  @Override
  public void release()
  {
    try
    {
      if (state != BufferState.RELEASED)
      {
        state = BufferState.RELEASED;
        errorHandler = null;

        if (bufferProvider != null)
        {
          bufferProvider.retainBuffer(this);
        }
      }
    }
    catch (Error ex)
    {
      // Don't swallow errors.
      throw ex;
    }
    catch (Exception ex)
    {
      // A normal exception can only occur if the buffer provider implementation is buggy.
      // Do not simply continue in this case because it's not known in which state this buffer is then.
      throw new Error("Implementation error in buffer provider " + bufferProvider, ex);
    }
  }

  @Override
  public void dispose()
  {
    state = BufferState.DISPOSED;
    bufferProvider = null;
    byteBuffer = null;
  }

  @Override
  public ByteBuffer startGetting(SocketChannel socketChannel) throws IOException
  {
    try
    {
      if (state != BufferState.INITIAL && state != BufferState.READING_HEADER && state != BufferState.READING_BODY)
      {
        throw new IllegalStateException(toString());
      }

      if (state == BufferState.INITIAL)
      {
        byteBuffer.limit(HEADER_SIZE);
        state = BufferState.READING_HEADER;
      }

      if (state == BufferState.READING_HEADER)
      {
        readChannel(socketChannel, byteBuffer);

        if (byteBuffer.hasRemaining())
        {
          // Limit is set to HEADER_SIZE.
          // Header is not fully received, yet.
          return null;
        }

        // Header is fully received.
        channelID = byteBuffer.getShort(CHANNEL_ID_POS);
        short payloadSize = byteBuffer.getShort(PAYLOAD_SIZE_POS);

        if (payloadSize == 0)
        {
          setEOS(true);
        }
        else if (payloadSize < 0)
        {
          setEOS(true);
          payloadSize = (short)-payloadSize;
        }

        try
        {
          byteBuffer.limit(HEADER_SIZE + payloadSize);
        }
        catch (IllegalArgumentException ex)
        {
          if (payloadSize < 0)
          {
            throw new IllegalArgumentException(toString() + ": New limit " + payloadSize + " is < 0" + (isEOS() ? " (EOS)" : ""), ex);
          }

          int capacity = byteBuffer.capacity();
          if (payloadSize > capacity)
          {
            throw new IllegalArgumentException(toString() + ": New limit " + payloadSize + " is > " + capacity + (isEOS() ? " (EOS)" : ""), ex);
          }

          throw new IllegalArgumentException(toString() + ": " + ex.getMessage(), ex);
        }

        state = BufferState.READING_BODY;
      }

      readChannel(socketChannel, byteBuffer);
      if (byteBuffer.hasRemaining())
      {
        return null;
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Read " + byteBuffer.limit() + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
            + (isEOS() ? " (EOS)" : "") + StringUtil.NL + formatContent(false)); //$NON-NLS-1$ //$NON-NLS-2$
      }

      byteBuffer.position(HEADER_SIZE);
      state = BufferState.GETTING;
      return byteBuffer;
    }
    catch (IOException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  @Override
  public ByteBuffer startPutting(short channelID)
  {
    try
    {
      if (state == BufferState.PUTTING)
      {
        if (channelID != this.channelID)
        {
          throw new IllegalArgumentException(toString() + ": channelID != this.channelID"); //$NON-NLS-1$
        }
      }
      else if (state != BufferState.INITIAL)
      {
        throw new IllegalStateException(toString());
      }
      else
      {
        state = BufferState.PUTTING;
        this.channelID = channelID;

        byteBuffer.clear();
        byteBuffer.position(HEADER_SIZE);
      }

      return byteBuffer;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  /**
   * @return <code>true</code> if the buffer has been completely written, <code>false</code> otherwise.
   */
  @Override
  public boolean write(SocketChannel socketChannel) throws IOException
  {
    try
    {
      if (state != BufferState.PUTTING && state != BufferState.WRITING)
      {
        throw new IllegalStateException(toString());
      }

      if (state == BufferState.PUTTING)
      {
        if (channelID == NO_CHANNEL)
        {
          throw new IllegalStateException(toString() + ": channelID == NO_CHANNEL"); //$NON-NLS-1$
        }

        int payloadSize = byteBuffer.position() - HEADER_SIZE;
        boolean eos = isEOS();
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
        byteBuffer.putShort(channelID);
        byteBuffer.putShort((short)payloadSize);
        byteBuffer.position(0);
        state = BufferState.WRITING;
      }

      int numBytes = socketChannel.write(byteBuffer);
      if (numBytes == -1)
      {
        throw new IOException(toString() + ": Channel closed"); //$NON-NLS-1$
      }

      if (byteBuffer.hasRemaining())
      {
        return false;
      }

      clear();
      return true;
    }
    catch (IOException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  @Override
  public void flip()
  {
    try
    {
      if (state != BufferState.PUTTING)
      {
        throw new IllegalStateException(toString());
      }

      byteBuffer.flip();
      byteBuffer.position(HEADER_SIZE);
      state = BufferState.GETTING;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  @Override
  public void compact()
  {
    byteBuffer.compact();
  }

  @Override
  public int getPosition()
  {
    return byteBuffer.position();
  }

  @Override
  public void setPosition(int position)
  {
    byteBuffer.position(position);
  }

  @Override
  public int getLimit()
  {
    return byteBuffer.limit();
  }

  @Override
  public void setLimit(int limit)
  {
    byteBuffer.limit(limit);
  }

  @Override
  public boolean hasRemaining()
  {
    return byteBuffer.hasRemaining();
  }

  @Override
  public byte get()
  {
    return byteBuffer.get();
  }

  @Override
  public void get(byte[] dst)
  {
    byteBuffer.get(dst);
  }

  @Override
  public short getShort()
  {
    return byteBuffer.getShort();
  }

  @Override
  public int getInt()
  {
    return byteBuffer.getInt();
  }

  @Override
  public String getString()
  {
    return BufferUtil.getString(byteBuffer);
  }

  @Override
  public void put(byte b)
  {
    byteBuffer.put(b);
  }

  @Override
  public void put(byte[] src, int offset, int length)
  {
    byteBuffer.put(src, offset, length);
  }

  @Override
  public void putShort(short value)
  {
    byteBuffer.putShort(value);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Buffer@{0}[{1}]", id, state); //$NON-NLS-1$
  }

  @Override
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

  @Override
  public IErrorHandler getErrorHandler()
  {
    return errorHandler;
  }

  @Override
  public void setErrorHandler(IErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  public void handleError(Throwable t)
  {
    try
    {
      if (errorHandler != null)
      {
        errorHandler.handleError(t);
      }
      else if (TRACER.isEnabled())
      {
        TRACER.trace(t);
      }
    }
    finally
    {
      release();
    }
  }

  private void readChannel(SocketChannel socketChannel, ByteBuffer byteBuffer) throws ClosedChannelException
  {
    try
    {
      int numBytes = socketChannel.read(byteBuffer);
      if (numBytes == -1)
      {
        throw new IOException(toString() + ": Channel has reached end-of-stream");
      }
    }
    catch (ClosedChannelException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      ClosedChannelException exception = new ClosedChannelException();
      exception.initCause(ex);
      throw exception;
    }
  }

  private static String formatHex(String hex)
  {
    StringBuilder builder = new StringBuilder();

    int length = hex.length();
    for (int i = 0; i < length; i++)
    {
      builder.append(hex.charAt(i));
      if (i % 2 == 1 && i < length - 1)
      {
        builder.append(' ');
      }
    }

    return builder.toString();
  }

  private static void decodeBuffer(String hex) throws IOException
  {
    System.out.println("Buffer: " + formatHex(hex));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(HexUtil.hexToBytes(hex)));

    short channelID = in.readShort();

    boolean eos = false;
    short payloadSize = in.readShort();
    if (payloadSize < 0)
    {
      payloadSize = (short)-payloadSize;
      eos = true;
    }

    System.out.println("channelID:     " + channelID);
    System.out.println("payloadSize:   " + payloadSize);
    System.out.println("eos:           " + eos);

    String type = "request";
    int correlationID = in.readInt();
    if (correlationID < 0)
    {
      correlationID = -correlationID;
      type = "response";
    }
    else if (in.available() >= 2)
    {
      short signalID = in.readShort();
      System.out.println("signalID:      " + signalID);
    }

    System.out.println("correlationID: " + correlationID);
    System.out.println("type:          " + type);
    System.out.println();
  }

  public static String dump(ByteBuffer byteBuffer)
  {
    return dump(byteBuffer, false);
  }

  public static String dump(ByteBuffer byteBuffer, boolean fullCapacity)
  {
    final int position = byteBuffer.position();
    final int limit = byteBuffer.limit();

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < limit; i++)
    {
      dumpByte(byteBuffer, builder, i);
    }

    if (fullCapacity)
    {
      int capacity = byteBuffer.capacity();
      if (limit < capacity)
      {
        byteBuffer.limit(capacity);

        try
        {
          builder.append("\n--");
          for (int i = limit; i < capacity; i++)
          {
            dumpByte(byteBuffer, builder, i);
          }
        }
        finally
        {
          byteBuffer.limit(limit);
        }
      }
    }

    return "pos\u00A0" + position + "/" + limit + ":" + builder;
  }

  private static void dumpByte(ByteBuffer byteBuffer, StringBuilder builder, int i)
  {
    int b = byteBuffer.get(i) & 0xFF; // Dump unsigned int instead of signed byte.
    builder.append('\u00A0'); // NO-BREAK SPACE. A bug in JDT's detail formatter hates normal spaces.
    builder.append(b);
  }

  public static void main(String[] args) throws Exception
  {
    decodeBuffer("0001ffea0000026b001a0000000101ff000000000000058d00");
    decodeBuffer("0001fffafffffd9500");
    decodeBuffer("0001ffea00000064001a0000000101ff000000000000040c00");
    decodeBuffer("0001fffaffffff9c00");
  }
}
