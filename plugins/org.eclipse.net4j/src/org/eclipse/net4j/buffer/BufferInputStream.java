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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class BufferInputStream extends InputStream implements IBufferHandler
{
  public static final long NO_TIMEOUT = -1;

  public static final long DEFAULT_MILLIS_BEFORE_TIMEOUT = NO_TIMEOUT;

  public static final long DEFAULT_MILLIS_INTERRUPT_CHECK = 100;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, BufferInputStream.class);

  private BlockingQueue<IBuffer> buffers = new LinkedBlockingQueue<IBuffer>();

  private IBuffer currentBuffer;

  private boolean eos;

  public BufferInputStream()
  {
  }

  public void handleBuffer(IBuffer buffer)
  {
    buffers.add(buffer);
  }

  @SuppressWarnings("deprecation")
  @Override
  public int read() throws IOException
  {
    if (currentBuffer == null)
    {
      if (eos)
      {
        // End of stream
        return -1;
      }

      if (!ensureBuffer())
      {
        // Timeout or interrupt
        return -1;
      }
    }

    final int result = currentBuffer.getByteBuffer().get() & 0xFF;
    if (TRACER.isEnabled())
    {
      TRACER.trace("<-- " + HexUtil.formatByte(result) //$NON-NLS-1$
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
            // Stream has been closed - shutting down
            return false;
          }

          currentBuffer = buffers.poll(check, TimeUnit.MILLISECONDS);
        }
      }
      else
      {
        // TODO Consider something faster than currentTimeMillis(), maybe less accurate?
        final long stop = System.currentTimeMillis() + timeout;
        while (currentBuffer == null)
        {
          if (buffers == null)
          {
            // Stream has been closed - shutting down
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
