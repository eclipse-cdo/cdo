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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.BufferProvider;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public final class BufferUtil
{
  public static final short DEFAULT_BUFFER_CAPACITY = 4096;

  public static final String UTF8_CHAR_SET_NAME = "UTF-8";

  private BufferUtil()
  {
  }

  public static BufferProvider createBufferFactory(short bufferCapacity)
  {
    return new BufferFactoryImpl(bufferCapacity);
  }

  public static BufferProvider createBufferFactory()
  {
    return new BufferFactoryImpl(DEFAULT_BUFFER_CAPACITY);
  }

  public static BufferProvider createBufferPool(BufferProvider factory)
  {
    return new BufferPoolImpl(factory);
  }

  public static BufferProvider createBufferPool(short bufferCapacity)
  {
    return createBufferPool(createBufferFactory(bufferCapacity));
  }

  public static BufferProvider createBufferPool()
  {
    return createBufferPool(DEFAULT_BUFFER_CAPACITY);
  }

  public static BufferProvider getBufferProvider(Object object)
  {
    if (object instanceof BufferProvider)
    {
      return (BufferProvider)object;
    }

    if (object == null)
    {
      throw new IllegalArgumentException("object == null");
    }

    throw new IllegalArgumentException("Unable to provide buffers: " + object);
  }

  public static byte[] toUTF8(String str)
  {
    if (str == null)
    {
      return new byte[0];
    }

    try
    {
      byte[] bytes = str.getBytes(UTF8_CHAR_SET_NAME);
      String test = new String(bytes, UTF8_CHAR_SET_NAME);
      if (!str.equals(test))
      {
        throw new IllegalArgumentException("String not encodable: " + str);
      }

      return bytes;
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw new RuntimeException(ex);
    }
  }

  public static String fromUTF8(byte[] bytes)
  {
    try
    {
      return new String(bytes, UTF8_CHAR_SET_NAME);
    }
    catch (UnsupportedEncodingException ex)
    {
      // This should really not happen
      throw new RuntimeException(ex);
    }
  }

  public static void putByteArray(ByteBuffer byteBuffer, byte[] array)
  {
    byteBuffer.putShort((short)array.length);
    if (array.length != 0)
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

  public static void putUTF8(ByteBuffer byteBuffer, String str)
  {
    byte[] bytes = BufferUtil.toUTF8(str);
    if (bytes.length > byteBuffer.remaining())
    {
      throw new IllegalArgumentException("String too long: " + str);
    }

    putByteArray(byteBuffer, bytes);
  }
}
