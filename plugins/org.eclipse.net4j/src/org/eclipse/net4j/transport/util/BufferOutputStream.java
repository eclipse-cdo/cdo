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
package org.eclipse.net4j.transport.util;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferHandler;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class BufferOutputStream extends OutputStream
{
  public static final boolean DEFAULT_PROPAGATE_CLOSE = false;

  public static boolean TRACE = false;

  private BufferHandler bufferHandler;

  private BufferProvider bufferProvider;

  private Buffer currentBuffer;

  private short channelID;

  public BufferOutputStream(BufferHandler bufferHandler, BufferProvider bufferProvider,
      short channelID)
  {
    if (bufferHandler == null)
    {
      throw new IllegalArgumentException("bufferHandler == null");
    }

    if (bufferProvider == null)
    {
      throw new IllegalArgumentException("bufferProvider == null");
    }

    this.bufferHandler = bufferHandler;
    this.bufferProvider = bufferProvider;
    this.channelID = channelID;
  }

  public BufferOutputStream(BufferHandler bufferHandler, short channelID)
  {
    this(bufferHandler, extractBufferProvider(bufferHandler), channelID);
  }

  @Override
  public void write(int b) throws IOException
  {
    ensureBuffer();
    if (TRACE)
    {
      System.out.println("--> " + HexUtil.toHex(b)
          + (b >= 32 ? " " + Character.toString((char)b) : ""));
    }

    ByteBuffer buffer = currentBuffer.getByteBuffer();
    buffer.put((byte)(b > Byte.MAX_VALUE ? ~(b + Byte.MIN_VALUE) : b));

    if (!buffer.hasRemaining())
    {
      flush();
    }
  }

  @Override
  public void flush() throws IOException
  {
    if (currentBuffer != null)
    {
      bufferHandler.handleBuffer(currentBuffer);
      currentBuffer = null;
    }
  }

  public void flushWithEOS() throws IOException
  {
    ensureBuffer();
    currentBuffer.setEOS(true);
    flush();
  }

  @Override
  public void close() throws IOException
  {
    try
    {
      if (isPropagateClose())
      {
        LifecycleUtil.deactivate(bufferHandler);
      }
    }
    finally
    {
      bufferHandler = null;
      bufferProvider = null;
      currentBuffer = null;
      super.close();
    }
  }

  @Override
  public String toString()
  {
    return "BufferOutputStream";
  }

  protected void ensureBuffer()
  {
    if (currentBuffer == null)
    {
      currentBuffer = bufferProvider.provideBuffer();
      currentBuffer.startPutting(channelID);
    }
  }

  protected boolean isPropagateClose()
  {
    return DEFAULT_PROPAGATE_CLOSE;
  }

  private static BufferProvider extractBufferProvider(BufferHandler bufferHandler)
  {
    if (bufferHandler instanceof BufferProvider)
    {
      return (BufferProvider)bufferHandler;
    }

    throw new IllegalArgumentException("Buffer handler unable to provide buffers");
  }
}
