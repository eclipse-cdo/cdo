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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class BufferOutputStream extends OutputStream
{
  public static final boolean DEFAULT_PROPAGATE_CLOSE = false;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_BUFFER_STREAM,
      BufferOutputStream.class);

  private BufferHandler bufferHandler;

  private BufferProvider bufferProvider;

  private Buffer currentBuffer;

  private short channelIndex;

  public BufferOutputStream(BufferHandler bufferHandler, BufferProvider bufferProvider,
      short channelIndex)
  {
    if (bufferHandler == null)
    {
      throw new IllegalArgumentException("bufferHandler == null"); //$NON-NLS-1$
    }

    if (bufferProvider == null)
    {
      throw new IllegalArgumentException("bufferProvider == null"); //$NON-NLS-1$
    }

    this.bufferHandler = bufferHandler;
    this.bufferProvider = bufferProvider;
    this.channelIndex = channelIndex;
  }

  public BufferOutputStream(BufferHandler bufferHandler, short channelIndex)
  {
    this(bufferHandler, extractBufferProvider(bufferHandler), channelIndex);
  }

  @Override
  public void write(int b) throws IOException
  {
    ensureBuffer();
    if (TRACER.isEnabled())
    {
      TRACER.trace("--> " + HexUtil.toHex(b) //$NON-NLS-1$
          + (b >= 32 ? " " + Character.toString((char)b) : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    ByteBuffer buffer = currentBuffer.getByteBuffer();
    buffer.put((byte)b);

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
    return "BufferOutputStream"; //$NON-NLS-1$
  }

  protected void ensureBuffer()
  {
    if (currentBuffer == null)
    {
      currentBuffer = bufferProvider.provideBuffer();
      currentBuffer.startPutting(channelIndex);
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

    throw new IllegalArgumentException("Buffer handler unable to provide buffers"); //$NON-NLS-1$
  }
}
