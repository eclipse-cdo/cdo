/*
 * Copyright (c) 2007, 2011-2013, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * @author Eike Stepper
 */
public final class BufferUtil
{
  public static final String UTF8_CHAR_SET_NAME = "UTF-8"; //$NON-NLS-1$

  private static final Charset CHARSET = Charset.forName(UTF8_CHAR_SET_NAME);

  private static final byte FALSE = (byte)0;

  private static final byte TRUE = (byte)1;

  private static final short NULL_STRING = -1;

  private BufferUtil()
  {
  }

  /**
   * @deprecated Use {@link #putString(ByteBuffer, String, boolean)}
   */
  @Deprecated
  public static byte[] toUTF8(String str)
  {
    if (str == null)
    {
      return new byte[0];
    }

    byte[] bytes;
    String test;

    try
    {
      bytes = str.getBytes(UTF8_CHAR_SET_NAME);
      test = new String(bytes, UTF8_CHAR_SET_NAME);
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw WrappedException.wrap(ex);
    }

    if (!test.equals(str))
    {
      throw new IllegalArgumentException("String not encodable: " + str); //$NON-NLS-1$
    }

    return bytes;
  }

  /**
   * @deprecated Use {@link #getString(ByteBuffer)}
   */
  @Deprecated
  public static String fromUTF8(byte[] bytes)
  {
    try
    {
      return new String(bytes, UTF8_CHAR_SET_NAME);
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @deprecated Use {@link #putString(ByteBuffer, String, boolean)}
   */
  @Deprecated
  public static void putUTF8(ByteBuffer byteBuffer, String str)
  {
    putString(byteBuffer, str, false);
  }

  public static void putObject(ByteBuffer byteBuffer, Object object) throws IOException
  {
    if (object != null)
    {
      byteBuffer.put(TRUE);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream stream = new ObjectOutputStream(baos);
      stream.writeObject(object);

      byte[] array = baos.toByteArray();
      putByteArray(byteBuffer, array);
    }
    else
    {
      byteBuffer.put(FALSE);
    }
  }

  public static Object getObject(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException
  {
    boolean nonNull = byteBuffer.get() == TRUE;
    if (nonNull)
    {
      byte[] array = getByteArray(byteBuffer);
      ByteArrayInputStream bais = new ByteArrayInputStream(array);
      ObjectInputStream stream = new ObjectInputStream(bais);
      return stream.readObject();
    }

    return null;
  }

  public static void putByteArray(ByteBuffer byteBuffer, byte[] array)
  {
    short length = array == null ? 0 : (short)array.length;
    byteBuffer.putShort(length); // BYTE_ARRAY_PREFIX
    if (length != 0)
    {
      byteBuffer.put(array);
    }
  }

  public static byte[] getByteArray(ByteBuffer byteBuffer)
  {
    short length = byteBuffer.getShort();
    byte[] array = new byte[length];
    if (length != 0)
    {
      byteBuffer.get(array);
    }

    return array;
  }

  public static void putString(ByteBuffer byteBuffer, String str, boolean bestEffort)
  {
    int sizePosition = byteBuffer.position();
    byteBuffer.putShort(NULL_STRING); // Placeholder for size

    if (str != null)
    {
      CharsetEncoder encoder = CHARSET.newEncoder();
      CharBuffer input = CharBuffer.wrap(str);

      int start = byteBuffer.position();
      int max = -1;

      for (;;)
      {
        CoderResult result = encoder.encode(input, byteBuffer, true);
        if (result.isError())
        {
          if (result.isOverflow() && bestEffort)
          {
            if (max == -1)
            {
              max = (int)(byteBuffer.remaining() / encoder.maxBytesPerChar());
            }
            else
            {
              --max;
            }

            if (max > 0)
            {
              str = str.substring(0, max);
              byteBuffer.position(start);
              continue;
            }
          }

          try
          {
            result.throwException();
          }
          catch (Exception ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        break;
      }

      int end = byteBuffer.position();
      short size = (short)Math.abs(end - start);

      byteBuffer.position(sizePosition);
      byteBuffer.putShort(size);
      byteBuffer.position(end);
    }
  }

  public static String getString(ByteBuffer byteBuffer)
  {
    short size = byteBuffer.getShort();
    if (size == NULL_STRING)
    {
      return null;
    }

    if (size == 0)
    {
      return StringUtil.EMPTY;
    }

    byte[] bytes = new byte[size];
    byteBuffer.get(bytes);

    try
    {
      return new String(bytes, UTF8_CHAR_SET_NAME);
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw WrappedException.wrap(ex);
    }
  }
}
