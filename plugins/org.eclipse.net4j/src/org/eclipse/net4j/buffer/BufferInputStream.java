/*
 * Copyright (c) 2008-2012, 2015, 2016, 2018-2020, 2022, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * A {@link IBufferHandler buffer handler} that defragments the passed {@link IBuffer buffers} into a continuous byte
 * sequence which is exposed as an {@link InputStream input stream}.
 * <p>
 * Reading is restricted to one reader thread at a time. Buffers may be supplied concurrently by producer threads, and
 * {@link #abort(RuntimeException)}, {@link #close()}, and {@link #restartTimeout()} may be called during a read.
 * Concurrent close never releases a buffer while the reader accesses its contents. Waiting readers are notified when a
 * buffer arrives, the stream is aborted or closed, or the timeout is restarted. Abort is observed at buffer acquisition
 * boundaries, without per-byte checks. This class does not guarantee fairness or producer priority.
 * <p>
 * A buffer's ownership transfers to this stream when {@link #handleBuffer(IBuffer)} accepts it. The stream releases it
 * after consumption or closure. A reader that has detached an exhausted buffer releases it after leaving the monitor.
 *
 * @author Eike Stepper
 */
public class BufferInputStream extends InputStream implements IBufferHandler
{
  public static final long NO_TIMEOUT = -1;

  public static final long DEFAULT_MILLIS_BEFORE_TIMEOUT = NO_TIMEOUT;

  /**
   * @deprecated Periodic interrupt checks are no longer performed.
   *
   * @since 2.0
   */
  @Deprecated
  public static final long DEFAULT_MILLIS_INTERRUPT_CHECK = 100;

  private static final boolean DISABLE_TIMEOUT = Boolean.getBoolean("org.eclipse.net4j.buffer.BufferInputStream.disableTimeout");

  private static final boolean DISABLE_BULK_READ = Boolean.getBoolean("org.eclipse.net4j.buffer.BufferInputStream.disableBulkRead");

  // The stream monitor guards the deque, currentBuffer, EOS/CCAM, abort cause, timeout deadline, and closure state.
  private Deque<IBuffer> buffers = new ArrayDeque<>();

  private IBuffer currentBuffer;

  /**
   * End Of Stream.
   */
  private boolean eos;

  /**
   * Close Channel After Me.
   */
  private boolean ccam;

  private RuntimeException abortCause;

  private long stopTimeMillis;

  public BufferInputStream()
  {
  }

  /**
   * Returns whether the currently acquired buffer requested that its channel be closed after consumption.
   *
   * @since 4.4
   */
  public boolean isCCAM()
  {
    synchronized (this)
    {
      return ccam;
    }
  }

  public long getMillisBeforeTimeout()
  {
    return DEFAULT_MILLIS_BEFORE_TIMEOUT;
  }

  /**
   * Restarts the current read timeout and wakes a waiting reader to recompute its deadline.
   *
   * @since 2.0
   */
  public void restartTimeout()
  {
    restartTimeout(getMillisBeforeTimeout());
  }

  /**
   * Returns the cause supplied to {@link #abort(RuntimeException)}, or {@code null} if this stream has not been aborted.
   *
   * @since 4.23
   */
  public RuntimeException getAbortCause()
  {
    synchronized (this)
    {
      return abortCause;
    }
  }

  /**
   * Aborts this stream and immediately wakes a thread waiting for its next buffer. The first non-null cause wins; later
   * calls do not replace it. Abort is observed at a buffer-acquisition boundary and does not release queued or acquired
   * buffers, which remain owned until consumed or closed.
   *
   * @param cause
   *          the non-null cause thrown by a waiting or subsequent reader
   * @throws NullPointerException
   *           if {@code cause} is {@code null}
   * @since 4.23
   */
  public void abort(RuntimeException cause)
  {
    Objects.requireNonNull(cause, "cause"); //$NON-NLS-1$

    synchronized (this)
    {
      if (abortCause == null)
      {
        abortCause = cause;
        notifyAll();
      }
    }
  }

  /**
   * Accepts a buffer from a producer. Invoking this method transfers ownership to the stream. If it has already been
   * closed, this method releases the supplied buffer itself; the caller must not release it again.
   *
   * @param buffer
   *          the buffer to accept
   */
  @Override
  public void handleBuffer(IBuffer buffer)
  {
    synchronized (this)
    {
      if (buffers != null)
      {
        // Queue insertion transfers ownership to the stream and wakes readers waiting under this same monitor.
        buffers.addLast(buffer);
        notifyAll();
        return;
      }
    }

    // Ownership was transferred by the call; release a late buffer here after leaving the monitor.
    buffer.release();
  }

  /**
   * Returns the next byte, or {@link IOUtil#EOF} at end of stream.
   * <p>
   * The monitor protects acquisition and access to the current buffer. If this method waits for a buffer, {@link Object#wait()}
   * releases the monitor fully, including this method's reentrant acquisition, so producers and lifecycle operations
   * can proceed. An exhausted buffer is detached under the monitor and released afterward.
   *
   * @throws IOException
   *           if waiting for a buffer times out
   */
  @Override
  public int read() throws IOException
  {
    IBuffer consumedBuffer = null;
    int result;

    synchronized (this)
    {
      if (currentBuffer == null)
      {
        if (eos)
        {
          return IOUtil.EOF;
        }

        if (!ensureBuffer())
        {
          return IOUtil.EOF;
        }
      }

      ByteBuffer byteBuffer = currentBuffer.getByteBuffer();

      int remaining = byteBuffer.remaining();
      if (remaining < 1)
      {
        consumedBuffer = currentBuffer;
        currentBuffer = null;
        result = IOUtil.EOF;
      }
      else
      {
        result = byteBuffer.get() & 0xFF;

        if (remaining == 1)
        {
          consumedBuffer = currentBuffer;
          currentBuffer = null;
        }
      }
    }

    // Release after the protected access ends to avoid invoking provider callbacks under the stream monitor.
    if (consumedBuffer != null)
    {
      consumedBuffer.release();
    }

    return result;
  }

  /**
   * Reads bytes from this stream, acquiring the monitor for at most one buffer per iteration. A read spanning multiple
   * buffers releases the monitor between them so producers and lifecycle operations can proceed. After bytes have been
   * copied, a later timeout or EOS returns the partial result; an exception before any bytes are read is propagated.
   *
   * @param b
   *          the destination array
   * @param off
   *          the destination offset
   * @param len
   *          the maximum number of bytes to read
   * @return the number of bytes read, or {@link IOUtil#EOF} if a positive-length read reaches end of stream before
   *         reading any bytes
   * @throws IOException
   *           if waiting for a buffer fails before any bytes are read
   */
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

    int bytesRead = 0;

    // Each pass holds the monitor for at most one buffer so producers and close can run between buffers.
    while (len > 0)
    {
      IBuffer consumedBuffer = null;

      try
      {
        synchronized (this)
        {
          if (currentBuffer == null)
          {
            if (eos || !ensureBuffer())
            {
              break;
            }
          }

          ByteBuffer byteBuffer = currentBuffer.getByteBuffer();
          int remaining = byteBuffer.remaining();
          int bytesToRead = Math.min(len, remaining);
          byteBuffer.get(b, off, bytesToRead);

          off += bytesToRead;
          len -= bytesToRead;
          bytesRead += bytesToRead;

          if (bytesToRead == remaining)
          {
            // The reader owns this detached buffer and releases it outside the monitor.
            consumedBuffer = currentBuffer;
            currentBuffer = null;
          }
        }
      }
      catch (IOException ex)
      {
        if (bytesRead == 0)
        {
          throw ex;
        }

        break;
      }

      // Release callbacks run between buffers, outside the monitor.
      if (consumedBuffer != null)
      {
        consumedBuffer.release();
      }
    }

    return bytesRead == 0 ? IOUtil.EOF : bytesRead;
  }

  /**
   * Closes the stream, detaches its current and queued buffers, and wakes any waiting reader. Buffer release and channel
   * lifecycle callbacks occur outside the stream monitor.
   *
   * @throws IOException
   *           if the superclass close operation fails
   */
  @Override
  public void close() throws IOException
  {
    Deque<IBuffer> bufferQueue;
    IBuffer buffer;
    boolean closeChannel;

    synchronized (this)
    {
      if (buffers == null)
      {
        return;
      }

      bufferQueue = buffers;
      buffers = null;

      buffer = currentBuffer;
      currentBuffer = null;

      closeChannel = ccam;
      notifyAll();
    }

    // This closer now owns every detached buffer; external release work stays outside the stream monitor.
    if (buffer != null)
    {
      buffer.release();
    }

    if (bufferQueue != null)
    {
      IBuffer queuedBuffer;

      while ((queuedBuffer = bufferQueue.pollFirst()) != null)
      {
        queuedBuffer.release();
      }
    }

    super.close();

    if (closeChannel)
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
    long timeoutMillis = getMillisBeforeTimeout();
    boolean noTimeout = timeoutMillis == NO_TIMEOUT;
    if (!noTimeout)
    {
      restartTimeout();
    }

    try
    {
      synchronized (this)
      {
        while (currentBuffer == null)
        {
          // Check every predicate under the same monitor as wait() to prevent lost notifications.
          checkAborted();

          if (buffers == null)
          {
            return false;
          }

          if (!buffers.isEmpty())
          {
            currentBuffer = buffers.removeFirst();
            eos = currentBuffer.isEOS();
            ccam = currentBuffer.isCCAM();
            break;
          }

          if (noTimeout || DISABLE_TIMEOUT)
          {
            // wait() releases every reentrant hold, allowing producers and close to acquire this monitor.
            wait();
          }
          else
          {
            // Recompute remaining time after notifications and spurious wakeups without resetting the deadline.
            wait(computeTimeout(timeoutMillis));
          }
        }
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw WrappedException.wrap(ex);
    }

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

  private long computeTimeout(long timeoutMillis) throws IOTimeoutException
  {
    long remaining = stopTimeMillis - System.currentTimeMillis();
    if (remaining <= 0)
    {
      // Throw an exception so that the caller can distinguish timeout from end of stream.
      throw new IOTimeoutException("Buffer read timeout expired after " + timeoutMillis + " milliseconds"); //$NON-NLS-1$
    }

    return remaining;
  }

  private void checkAborted()
  {
    RuntimeException abortCause = this.abortCause;
    if (abortCause != null)
    {
      if (abortCause instanceof RemoteException)
      {
        // Only a remote failure has a distinct local detection stack trace to enrich.
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        ((RemoteException)abortCause).setLocalStacktrace(stackTrace);
      }

      throw abortCause;
    }
  }

  private void restartTimeout(long timeoutMillis)
  {
    synchronized (this)
    {
      stopTimeMillis = System.currentTimeMillis() + timeoutMillis;
      notifyAll();
    }
  }

  /**
   * @deprecated Periodic interrupt checks are no longer performed.
   *
   * @since 2.0
   */
  @Deprecated
  public long getMillisInterruptCheck()
  {
    return DEFAULT_MILLIS_INTERRUPT_CHECK;
  }

  /**
   * Returns the exception injected into this stream.
   *
   * @return the injected exception, or {@code null} if none was set
   * @deprecated Use {@link #getAbortCause()}.
   * @since 2.0
   */
  @Deprecated
  public RuntimeException getException()
  {
    synchronized (this)
    {
      return abortCause;
    }
  }

  /**
   * Replaces or clears the historical exception property and wakes a waiting reader. Passing {@code null} clears the
   * cause. Unlike {@link #abort(RuntimeException)}, this compatibility setter may replace an earlier cause.
   *
   * @param exception
   *          the exception to inject, or {@code null} to clear it
   * @deprecated Use {@link #abort(RuntimeException)} to abort the stream.
   * @since 4.0
   */
  @Deprecated
  public void setException(RemoteException exception)
  {
    synchronized (this)
    {
      abortCause = exception;
      notifyAll();
    }
  }
}
