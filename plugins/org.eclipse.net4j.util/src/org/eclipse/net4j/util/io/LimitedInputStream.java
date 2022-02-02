/*
 * Copyright (c) 2011, 2012, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class LimitedInputStream extends InputStream
{
  private static final long CLOSED = Long.MIN_VALUE;

  private final InputStream in;

  private final boolean closeBackingStream;

  private long remaining;

  private long remainingAtMark;

  /**
   * @since 3.17
   */
  public LimitedInputStream(InputStream in, long length, boolean closeBackingStream)
  {
    this.in = in;
    remaining = length;
    this.closeBackingStream = closeBackingStream;
  }

  public LimitedInputStream(InputStream in, long length)
  {
    this(in, length, true);
  }

  @Override
  public int read() throws IOException
  {
    checkOpen();

    if (remaining <= 0)
    {
      return -1;
    }

    remaining -= 1;
    return in.read();
  }

  @Override
  public int read(byte[] cbuf, int off, int len) throws IOException
  {
    checkOpen();

    if (remaining <= 0)
    {
      return -1;
    }

    if (len > remaining)
    {
      len = (int)remaining;
    }

    len = in.read(cbuf, off, len);
    if (len > 0)
    {
      remaining -= len;
    }
    else
    {
      remaining = 0;
    }

    return len;
  }

  @Override
  public long skip(long n) throws IOException
  {
    checkOpen();

    if (n > remaining)
    {
      n = remaining;
    }

    remaining -= n = in.skip(n);
    return n;
  }

  @Override
  public boolean markSupported()
  {
    return in.markSupported();
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    if (markSupported() && remaining != CLOSED)
    {
      in.mark(readlimit);
      remainingAtMark = remaining;
    }
  }

  @Override
  public synchronized void reset() throws IOException
  {
    if (markSupported())
    {
      checkOpen();
      in.reset();
      remaining = remainingAtMark;
    }
  }

  @Override
  public void close() throws IOException
  {
    remaining = CLOSED;

    if (closeBackingStream)
    {
      in.close();
    }
  }

  private void checkOpen() throws IOException
  {
    if (remaining == CLOSED)
    {
      throw new IOException("Stream is closed");
    }
  }
}
