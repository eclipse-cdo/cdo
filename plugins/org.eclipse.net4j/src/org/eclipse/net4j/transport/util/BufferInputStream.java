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
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class BufferInputStream extends InputStream implements BufferHandler
{
  public static final long NO_TIMEOUT = -1;

  public static final long DEFAULT_MILLIS_BEFORE_TIMEOUT = NO_TIMEOUT;

  public static final long DEFAULT_MILLIS_INTERRUPT_CHECK = 100;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_BUFFER_STREAM,
      BufferInputStream.class);

  private BlockingQueue<Buffer> buffers = new LinkedBlockingQueue();

  private Buffer currentBuffer;

  private boolean eos;

  public BufferInputStream()
  {
  }

  public void handleBuffer(Buffer buffer)
  {
    buffers.add(buffer);
  }

  @Override
  public int read() throws IOException
  {
    if (eos && currentBuffer == null)
    {
      // End of sequence
      return -1;
    }

    if (!ensureBuffer())
    {
      // Timeout or interrupt
      return -1;
    }

    final int result = currentBuffer.getByteBuffer().get() & 0xff;
    if (TRACER.isEnabled())
    {
      TRACER.trace("<-- " + HexUtil.toHex(result) //$NON-NLS-1$
          + (result >= 32 ? " " + Character.toString((char)result) : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (!currentBuffer.getByteBuffer().hasRemaining())
    {
      currentBuffer.release();
      currentBuffer = null;
    }

    return result;
  }

  @Override
  public void close() throws IOException
  {
    buffers = null;
    currentBuffer = null;
    super.close();
  }

  @Override
  public String toString()
  {
    return "BufferInputStream"; //$NON-NLS-1$
  }

  protected boolean ensureBuffer() throws IOException
  {
    final long check = getMillisInterruptCheck();
    final long timeout = getMillisBeforeTimeout();

    try
    {
      if (timeout == NO_TIMEOUT)
      {
        while (currentBuffer == null)
        {
          if (buffers == null)
          {
            // Stream has been closed
            return false;
          }

          currentBuffer = buffers.poll(check, TimeUnit.MILLISECONDS);
        }
      }
      else
      {
        final long stop = System.currentTimeMillis() + timeout;
        while (currentBuffer == null)
        {
          if (buffers == null)
          {
            // Stream has been closed
            return false;
          }

          final long remaining = stop - System.currentTimeMillis();
          if (remaining <= 0)
          {
            return false;
          }

          currentBuffer = buffers.poll(Math.min(remaining, check), TimeUnit.MILLISECONDS);
        }
      }
    }
    catch (InterruptedException ex)
    {
      throw new IOException("Interrupted"); //$NON-NLS-1$
    }

    eos = currentBuffer.isEOS();
    return true;
  }

  public long getMillisBeforeTimeout()
  {
    return DEFAULT_MILLIS_BEFORE_TIMEOUT;
  }

  public long getMillisInterruptCheck()
  {
    return DEFAULT_MILLIS_INTERRUPT_CHECK;
  }
}
