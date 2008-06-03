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
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class BufferOutputStream extends OutputStream
{
  public static final boolean DEFAULT_PROPAGATE_CLOSE = false;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, BufferOutputStream.class);

  private IBufferHandler bufferHandler;

  private IBufferProvider bufferProvider;

  private IBuffer currentBuffer;

  private short channelIndex;

  public BufferOutputStream(IBufferHandler bufferHandler, IBufferProvider bufferProvider, short channelIndex)
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

  public BufferOutputStream(IBufferHandler bufferHandler, short channelIndex)
  {
    this(bufferHandler, extractBufferProvider(bufferHandler), channelIndex);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void write(int b) throws IOException
  {
    ensureBuffer();
    if (TRACER.isEnabled())
    {
      TRACER.trace("--> " + HexUtil.formatByte(b) //$NON-NLS-1$
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

  private static IBufferProvider extractBufferProvider(IBufferHandler bufferHandler)
  {
    if (bufferHandler instanceof IBufferProvider)
    {
      return (IBufferProvider)bufferHandler;
    }

    throw new IllegalArgumentException("Buffer handler unable to provide buffers"); //$NON-NLS-1$
  }
}
