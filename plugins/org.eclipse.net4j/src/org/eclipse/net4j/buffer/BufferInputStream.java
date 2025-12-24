/*
 * Copyright (c) 2008-2012, 2015, 2016, 2018-2020, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOTimeoutException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

  private static final boolean DISABLE_BULK_READ = Boolean.getBoolean("org.eclipse.net4j.buffer.BufferInputStream.disableBulkRead");

  private BlockingQueue<IBuffer> buffers = new LinkedBlockingQueue<>();

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

  @Override
  public void handleBuffer(IBuffer buffer)
  {
    // If stream has been closed - ignore the new buffer.
    if (buffers != null)
    {
      buffers.add(buffer);
    }
    else
    {
      buffer.release();
    }
  }

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

    ByteBuffer byteBuffer = currentBuffer.getByteBuffer();

    int remaining = byteBuffer.remaining();
    if (remaining < 1)
    {
      currentBuffer.release();
      currentBuffer = null;

      // End of stream
      return IOUtil.EOF;
    }

    int result = byteBuffer.get() & 0xFF;

    if (remaining - 1 < 1)
    {
      currentBuffer.release();
      currentBuffer = null;
    }

    return result;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException
  {
    if (DISABLE_BULK_READ)
    {
      return super.read(b, off, len);
    }

    if (b == null)
    {
      throw new NullPointerException();
    }

    if (off < 0 || len < 0 || len > b.length - off)
    {
      throw new IndexOutOfBoundsException();
    }

    if (len == 0)
    {
      return 0;
    }

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

    ByteBuffer byteBuffer = currentBuffer.getByteBuffer();
    int remaining = byteBuffer.remaining();
    int bytesToRead = Math.min(len, remaining);

    byteBuffer.get(b, off, bytesToRead);
    if (bytesToRead == remaining)
    {
      // All remaining bytes of the current buffer have been read.
      currentBuffer.release();
      currentBuffer = null;
    }

    off += bytesToRead;
    len -= bytesToRead;
    int bytesRead = bytesToRead;

    try
    {
      while (len > 0)
      {
        if (eos)
        {
          break;
        }

        if (!ensureBuffer())
        {
          break;
        }

        byteBuffer = currentBuffer.getByteBuffer();
        remaining = byteBuffer.remaining();
        bytesToRead = Math.min(len, remaining);

        byteBuffer.get(b, off, bytesToRead);
        if (bytesToRead == remaining)
        {
          // All remaining bytes of the current buffer have been read.
          currentBuffer.release();
          currentBuffer = null;
        }

        off += bytesToRead;
        len -= bytesToRead;
        bytesRead += bytesToRead;
      }
    }
    catch (IOException ee)
    {
    }

    return bytesRead;
  }

  @Override
  public void close() throws IOException
  {
    if (currentBuffer != null)
    {
      currentBuffer.release();
      currentBuffer = null;
    }

    buffers = null;
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

    // int forDebuggingOnly;
    // remaining = Long.MAX_VALUE;

    remaining -= System.currentTimeMillis();
    if (remaining <= 0)
    {
      // Throw an exception so that caller can distinguish between end-of-stream and a timeout
      throw new IOTimeoutException("Buffer read timeout expired after " + getMillisBeforeTimeout() + " milliseconds");
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
