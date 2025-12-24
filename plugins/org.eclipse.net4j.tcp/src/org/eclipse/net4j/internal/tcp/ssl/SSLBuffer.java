/*
 * Copyright (c) 2011, 2012, 2015, 2018, 2020, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
      int capacity = getCapacity();
      limit = limit > capacity ? capacity : limit;

      put(buf.array(), 0, limit);
      buf.position(limit);
      buf.compact();
      getByteBuffer().flip();

      setChannelID(getShort());
      short payloadSize = getShort();

      if (payloadSize < 0)
      {
        setEOS(true);
        payloadSize = (short)-payloadSize;
      }

      setPosition(IBuffer.HEADER_SIZE);
      setState(BufferState.READING_HEADER);

      compact();
      setLimit(payloadSize);
      setState(BufferState.READING_BODY);

      getByteBuffer().flip();
      setState(BufferState.GETTING);

      return getByteBuffer();
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
      ByteBuffer packetSendBuf = sslEngineManager.getPacketSendBuf();
      if (packetSendBuf.position() > 0)
      {
        sslEngineManager.handleWrite(socketChannel);

        if (packetSendBuf.position() > 0)
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

      if (state == BufferState.PUTTING)
      {
        if (getChannelID() == NO_CHANNEL)
        {
          throw new IllegalStateException("channelID == NO_CHANNEL"); //$NON-NLS-1$
        }

        int payloadSize = getPosition() - HEADER_SIZE;
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

        getByteBuffer().flip();
        putShort(getChannelID());
        putShort((short)payloadSize);
        setPosition(0);
        setState(BufferState.WRITING);
      }

      sslEngineManager.getAppSendBuf().put(getByteBuffer());
      sslEngineManager.write(socketChannel);

      if (packetSendBuf.position() > 0)
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
