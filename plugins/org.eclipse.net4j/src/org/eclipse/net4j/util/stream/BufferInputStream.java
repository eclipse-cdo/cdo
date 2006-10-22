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
package org.eclipse.net4j.util.stream;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferHandler;

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

  private BlockingQueue<Buffer> buffers = new LinkedBlockingQueue<Buffer>();

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

    int result = currentBuffer.getByteBuffer().get() - Byte.MIN_VALUE;
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
    return "BufferInputStream";
  }

  protected boolean ensureBuffer() throws IOException
  {
    try
    {
      final long check = getMillisInterruptCheck();
      final long timeout = getMillisBeforeTimeout();

      try
      {
        if (timeout == NO_TIMEOUT)
        {
          while (currentBuffer == null)
          {
            currentBuffer = buffers.poll(check, TimeUnit.MILLISECONDS);
          }
        }
        else
        {
          final long stop = System.currentTimeMillis() + timeout;
          while (currentBuffer == null)
          {
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
        throw new IOException("Interrupted");
      }

      eos = currentBuffer.isEOS();
    }
    catch (RuntimeException ex)
    {
      // TODO Remove
      ex.printStackTrace();
    }
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
