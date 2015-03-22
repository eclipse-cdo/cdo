/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.buffer.Buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * All source code same as org.eclipse.internal.net4j.buffer.Buffer except adding SSLEngineManager to constructor and
 * overriding startGetting and write method in order to attach the SSL functional.
 * 
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLBuffer extends Buffer
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SSLBUFFER, SSLBuffer.class);

  private SSLEngineManager sslEngineManager;

  public SSLBuffer(IBufferProvider provider, short capacity, SSLEngineManager sslEngineManager)
  {
    super(provider, capacity);
    this.sslEngineManager = sslEngineManager;
  }

  @Override
  public ByteBuffer startGetting(SocketChannel socketChannel) throws IOException
  {
    BufferState state = getState();
    if (state != BufferState.INITIAL && state != BufferState.READING_HEADER && state != BufferState.READING_BODY)
    {
      throw new IllegalStateException(toString());
    }

    int readSize = 0;

    if (sslEngineManager.getAppRecvBuf().position() > 0)
    {
      readSize = sslEngineManager.getAppRecvBuf().position();
    }
    else
    {
      readSize = sslEngineManager.read(socketChannel);
    }

    if (readSize > 0)
    {
      ByteBuffer buf = sslEngineManager.getAppRecvBuf();
      buf.flip();

      int limit = buf.limit();
      ByteBuffer byteBuffer = getByteBuffer();

      int capacity = byteBuffer.capacity();
      limit = limit > capacity ? capacity : limit;

      byteBuffer.put(buf.array(), 0, limit);
      buf.position(limit);
      buf.compact();
      byteBuffer.flip();

      setChannelID(byteBuffer.getShort());
      short payloadSize = byteBuffer.getShort();

      if (payloadSize < 0)
      {
        setEOS(true);
        payloadSize = (short)-payloadSize;
      }

      payloadSize -= FLAGS_OFFSET;

      byteBuffer.position(IBuffer.HEADER_SIZE);
      setState(BufferState.READING_HEADER);

      byteBuffer.compact();
      byteBuffer.limit(payloadSize);
      setState(BufferState.READING_BODY);

      byteBuffer.flip();
      setState(BufferState.GETTING);

      return byteBuffer;
    }
    else if (readSize < 0)
    {
      throw new ClosedChannelException();
    }

    return null;
  }

  /**
   * @return <code>true</code> if the buffer has been completely written, <code>false</code> otherwise.
   */
  @Override
  public boolean write(SocketChannel socketChannel) throws IOException
  {
    try
    {

      if (sslEngineManager.getPacketSendBuf().position() > 0)
      {
        sslEngineManager.handleWrite(socketChannel);

        if (sslEngineManager.getPacketSendBuf().position() > 0)
        {
          clear();
          return false;
        }

        clear();
        return true;
      }

      BufferState state = getState();
      if (state != BufferState.PUTTING && state != BufferState.WRITING)
      {
        throw new IllegalStateException(toString());
      }

      ByteBuffer byteBuffer = getByteBuffer();
      if (state == BufferState.PUTTING)
      {
        if (getChannelID() == NO_CHANNEL)
        {
          throw new IllegalStateException("channelID == NO_CHANNEL"); //$NON-NLS-1$
        }

        int payloadSize = byteBuffer.position() - IBuffer.HEADER_SIZE + FLAGS_OFFSET;
        if (isEOS())
        {
          payloadSize = -payloadSize;
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace("Writing " + (Math.abs(payloadSize) - 1) + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
              + (isEOS() ? " (EOS)" : "") + StringUtil.NL + formatContent(false)); //$NON-NLS-1$ //$NON-NLS-2$
        }

        byteBuffer.flip();
        byteBuffer.putShort(getChannelID());
        byteBuffer.putShort((short)payloadSize);
        byteBuffer.position(0);
        setState(BufferState.WRITING);
      }

      sslEngineManager.getAppSendBuf().put(byteBuffer);
      sslEngineManager.write(socketChannel);

      if (sslEngineManager.getPacketSendBuf().position() > 0)
      {
        clear();
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
  public void dispose()
  {
    sslEngineManager = null;
    super.dispose();
  }
}
