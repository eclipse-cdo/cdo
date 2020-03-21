/*
 * Copyright (c) 2008, 2010-2012, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim -  Bug 262875: java.nio.BufferUnderFlowException https://bugs.eclipse.org/bugs/show_bug.cgi?id=262875
 */
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * An {@link OutputStream output stream} that fragments the written byte sequence into fixed-sized {@link IBuffer
 * buffers} and passes them to configured {@link IBufferHandler buffer handler}.
 *
 * @author Eike Stepper
 */
public class BufferOutputStream extends OutputStream
{
  public static final boolean DEFAULT_PROPAGATE_CLOSE = false;

  private static final boolean DISABLE_BULK_WRITE = Boolean.getBoolean("org.eclipse.net4j.buffer.BufferOutputStream.disableBulkWrite");

  private final IBufferProvider bufferProvider;

  private final IBufferHandler bufferHandler;

  private IBuffer currentBuffer;

  private short channelID;

  private Throwable error;

  public BufferOutputStream(IBufferHandler bufferHandler, IBufferProvider bufferProvider, short channelID)
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

  public BufferOutputStream(IBufferHandler bufferHandler, short channelID)
  {
    this(bufferHandler, extractBufferProvider(bufferHandler), channelID);
  }

  /**
   * @since 2.0
   */
  public Throwable getError()
  {
    return error;
  }

  /**
   * @since 2.0
   */
  public void setError(Throwable error)
  {
    this.error = error;
  }

  @Override
  public void write(int b) throws IOException
  {
    throwExceptionOnError();
    flushIfFilled();
    ensureBufferPrivate();

    // If this was called with a primitive byte with a negative value,
    // the implicit conversion prepended 24 leading 1's. We'll undo those.
    b = b & 0xFF;

    currentBuffer.put((byte)b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException
  {
    if (DISABLE_BULK_WRITE)
    {
      super.write(b, off, len);
      return;
    }

    if (b == null)
    {
      throw new NullPointerException();
    }

    if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0)
    {
      throw new IndexOutOfBoundsException();
    }

    while (len > 0)
    {
      throwExceptionOnError();
      flushIfFilled();
      ensureBufferPrivate();

      ByteBuffer byteBuffer = currentBuffer.getByteBuffer();

      int bytesToPut = Math.min(len, byteBuffer.remaining());
      currentBuffer.put(b, off, bytesToPut);

      off += bytesToPut;
      len -= bytesToPut;
    }
  }

  /**
   * Flushes the current buffer, it's handled over to the buffer handler.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see #currentBuffer
   * @see IBufferHandler#handleBuffer(IBuffer)
   */
  @Override
  public void flush() throws IOException
  {
    flushPrivate();
  }

  /**
   * Flushes the current buffer if it has no remaining space.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void flushIfFilled() throws IOException
  {
    if (currentBuffer != null && !currentBuffer.hasRemaining())
    {
      flushPrivate();
    }
  }

  private void flushPrivate()
  {
    if (currentBuffer != null)
    {
      bufferHandler.handleBuffer(currentBuffer);
      currentBuffer = null;
    }
  }

  public void flushWithEOS() throws IOException
  {
    flushWithEOS(false);
  }

  /**
   * @since 4.4
   */
  public void flushWithEOS(boolean ccam) throws IOException
  {
    throwExceptionOnError();
    ensureBufferPrivate();
    currentBuffer.setEOS(true);
    currentBuffer.setCCAM(ccam);
    flushPrivate();
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
      currentBuffer = null;
      super.close();
    }
  }

  @Override
  public String toString()
  {
    return "BufferOutputStream";
  }

  /**
   * Ensures that this BufferOutputStream has a buffer. If the current buffer was flushed a new one is fetched from the
   * buffer provider.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see #flush()
   * @see IBufferProvider#provideBuffer()
   */
  protected void ensureBuffer() throws IOException
  {
    ensureBufferPrivate();
  }

  private void ensureBufferPrivate()
  {
    if (currentBuffer == null)
    {
      currentBuffer = bufferProvider.provideBuffer();
      currentBuffer.setErrorHandler(throwable -> setError(throwable));
      currentBuffer.startPutting(channelID);
    }
  }

  /**
   * Throws an exception if there's an error.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see #error
   */
  private void throwExceptionOnError() throws IOException
  {
    if (error != null)
    {
      if (error instanceof IOException)
      {
        throw (IOException)error;
      }

      if (error instanceof RuntimeException)
      {
        throw (RuntimeException)error;
      }

      throw new IORuntimeException(error);
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

    throw new IllegalArgumentException("Buffer handler unable to provide buffers");
  }
}
