/*
 * Copyright (c) 2010-2012, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class LimitedReader extends Reader
{
  private final Reader in;

  private final boolean closeBackingReader;

  private long remaining;

  private long remainingAtMark = 0;

  /**
   * @since 3.20
   */
  public LimitedReader(Reader in, long length, boolean closeBackingReader)
  {
    this.in = in;
    remaining = length;
    this.closeBackingReader = closeBackingReader;
  }

  public LimitedReader(Reader in, long length)
  {
    this(in, length, true);
  }

  @Override
  public int read() throws IOException
  {
    if ((remaining -= 1) < 0)
    {
      return -1;
    }

    return in.read();
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException
  {
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
      remaining -= remaining;
    }

    return len;
  }

  @Override
  public long skip(long n) throws IOException
  {
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
  public void mark(int readlimit) throws IOException
  {
    if (markSupported())
    {
      in.mark(readlimit);
      remainingAtMark = remaining;
    }
  }

  @Override
  public void reset() throws IOException
  {
    in.reset();
    remaining = remainingAtMark;
  }

  @Override
  public void close() throws IOException
  {
    remaining = 0;

    if (closeBackingReader)
    {
      in.close();
    }
  }
}
