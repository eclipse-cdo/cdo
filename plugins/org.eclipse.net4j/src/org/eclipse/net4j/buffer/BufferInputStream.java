/*
 * Copyright (c) 2008-2012, 2015, 2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOTimeoutException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A {@link IBufferHandler buffer handler} that defragments the passed {@link IBuffer buffers} into a continuous byte
 * sequence which is exposed as an {@link InputStream input stream}.
 *
 * @author Eike Stepper
 */
public class BufferInputStream extends InputStream implements IBufferHandler
{
  public static final long NO_TIMEOUT = -1;

  public static final long DEFAULT_MILLIS_BEFORE_TIMEOUT = NO_TIMEOUT;

  public static final long DEFAULT_MILLIS_INTERRUPT_CHECK = 100;

  private static final boolean DISABLE_TIMEOUT = Boolean.getBoolean("org.eclipse.net4j.buffer.BufferInputStream.disableTimeout");

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, BufferInputStream.class);

  private final boolean tracerEnabled;

  private BlockingQueue<IBuffer> buffers = new LinkedBlockingQueue<IBuffer>();

  private IBuffer currentBuffer;

  /**
   * End Of Stream.
   */
  private boolean eos;

  /**
   * Close Channel After Me.
   */
  private boolean ccam;

  private RemoteException exception;

  private long stopTimeMillis;

  public BufferInputStream()
  {
    tracerEnabled = TRACER.isEnabled();
  }

  /**
   * @since 4.4
   */
  public boolean isCCAM()
  {
    return ccam;
  }

  public long getMillisBeforeTimeout()
  {
    return DEFAULT_MILLIS_BEFORE_TIMEOUT;
  }

  public long getMillisInterruptCheck()
  {
    return DEFAULT_MILLIS_INTERRUPT_CHECK;
  }

  /**
   * @since 2.0
   */
  public void restartTimeout()
  {
    synchronized (this)
    {
      stopTimeMillis = System.currentTimeMillis() + getMillisBeforeTimeout();
    }
  }

  /**
   * @since 2.0
   */
  public RuntimeException getException()
  {
    return exception;
  }

  /**
   * @since 4.0
   */
  public void setException(RemoteException exception)
  {
    this.exception = exception;
  }

  public void handleBuffer(IBuffer buffer)
  {
    // If stream has been closed - ignore the new buffer.
    if (buffers != null)
    {
      buffers.add(buffer);
    }
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
        return IOUtil.EOF;
      }

      if (!ensureBuffer())
      {
        // Timeout or interrupt
        return IOUtil.EOF;
      }
    }

    if (!currentBuffer.hasRemaining())
    {
      // End of stream
      return IOUtil.EOF;
    }

    final int result = currentBuffer.get() & 0xFF;
    if (tracerEnabled)
    {
      TRACER.trace("<-- " + HexUtil.formatByte(result) //$NON-NLS-1$
          + (result >= 32 ? " " + Character.toString((char)result) : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (!currentBuffer.hasRemaining())
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

    if (ccam)
    {
      closeChannel();
    }
  }

  @Override
  public String toString()
  {
    return "BufferInputStream"; //$NON-NLS-1$
  }

  protected boolean ensureBuffer() throws IOException
  {
    final long check = getMillisInterruptCheck();

    try
    {
      boolean noTimeout = getMillisBeforeTimeout() == NO_TIMEOUT;
      if (!noTimeout)
      {
        restartTimeout();
      }

      while (currentBuffer == null)
      {
        throwRemoteExceptionIfExists();

        if (buffers == null)
        {
          // Stream has been closed - shutting down
          return false;
        }

        long timeout;
        if (noTimeout || DISABLE_TIMEOUT)
        {
          timeout = check;
        }
        else
        {
          timeout = computeTimeout(check);
        }

        currentBuffer = buffers.poll(timeout, TimeUnit.MILLISECONDS);
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    eos = currentBuffer.isEOS();
    ccam = currentBuffer.isCCAM();

    return true;
  }

  /**
   * Subclasses may override.
   *
   * @since 4.5
   */
  protected void closeChannel()
  {
    // Do nothing.
  }

  private long computeTimeout(final long check) throws IOTimeoutException
  {
    if (DISABLE_TIMEOUT)
    {
      return Integer.MAX_VALUE;
    }

    long remaining;
    synchronized (this)
    {
      remaining = stopTimeMillis;
    }

    remaining -= System.currentTimeMillis();
    if (remaining <= 0)
    {
      // Throw an exception so that caller can distinguish between end-of-stream and a timeout
      throw new IOTimeoutException();
    }

    return Math.min(remaining, check);
  }

  private void throwRemoteExceptionIfExists()
  {
    if (exception != null)
    {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      exception.setLocalStacktrace(stackTrace);
      throw exception;
    }
  }
}
