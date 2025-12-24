/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An input stream for reading a {@link Reader}.
 *
 * @author Eike Stepper
 * @since 3.13
 */
public class ReaderInputStream extends InputStream
{
  private static final int MASK = 0xFF;

  private final Charset charset;

  private Reader reader;

  private CharBuffer chars = CharBuffer.allocate(IOUtil.DEFAULT_BUFFER_SIZE);

  private ByteBuffer bytes;

  public ReaderInputStream(Reader reader, String encoding) throws IOException
  {
    this.reader = reader;
    charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
  }

  public ReaderInputStream(Reader reader) throws IOException
  {
    this(reader, null);
  }

  @Override
  public int available() throws IOException
  {
    checkClosed();
    ensureBytes();

    if (bytes == null)
    {
      return 0;
    }

    return bytes.remaining();
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException
  {
    checkClosed();

    if (len == 0)
    {
      return 0;
    }

    ensureBytes();
    if (bytes == null)
    {
      return IOUtil.EOF;
    }

    int count = 0;
    do
    {
      ensureBytes();

      if (bytes == null)
      {
        // No more to read.
        break;
      }

      int toRead = Math.min(bytes.remaining(), len - count);
      bytes.get(b, off + count, toRead);
      count = count + toRead;
    } while (count < len);

    return count;
  }

  @Override
  public int read() throws IOException
  {
    checkClosed();

    ensureBytes();
    if (bytes == null)
    {
      return IOUtil.EOF;
    }

    return bytes.get() & MASK;
  }

  @Override
  public void close() throws IOException
  {
    if (reader != null)
    {
      reader.close();
      reader = null;
      bytes = null;
      chars = null;
    }
  }

  private void ensureBytes() throws IOException
  {
    if (bytes == null || !bytes.hasRemaining())
    {
      checkClosed();

      Buffer buffer = chars;
      buffer.rewind();

      int count = reader.read(chars);
      if (count > 0)
      {
        buffer.flip();
        bytes = charset.encode(chars);
      }
      else
      {
        bytes = null;
      }
    }
  }

  private void checkClosed() throws IOException
  {
    if (reader == null)
    {
      throw new IOException("Reader is closed"); //$NON-NLS-1$
    }
  }
}
