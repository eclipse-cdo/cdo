/*
 * Copyright (c) 2007, 2008, 2010-2013, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public class ExtendedDataInputStream extends DataInputStream implements ExtendedDataInput
{
  public ExtendedDataInputStream(InputStream in)
  {
    super(in);
  }

  /**
   * @since 3.7
   */
  @Override
  public int readVarInt() throws IOException
  {
    return ExtendedIOUtil.readVarInt(this);
  }

  /**
  * @since 3.7
  */
  @Override
  public long readVarLong() throws IOException
  {
    return ExtendedIOUtil.readVarLong(this);
  }

  @Override
  public byte[] readByteArray() throws IOException
  {
    return ExtendedIOUtil.readByteArray(this);
  }

  @Override
  public String readString() throws IOException
  {
    return ExtendedIOUtil.readString(this);
  }

  @Override
  public Object readObject() throws IOException
  {
    return ExtendedIOUtil.readObject(this);
  }

  /**
   * @since 3.0
   */
  @Override
  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
  {
    return ExtendedIOUtil.readEnum(this, type);
  }

  /**
   * @since 3.4
   */
  @Override
  public Throwable readException() throws IOException
  {
    return ExtendedIOUtil.readException(this);
  }

  @Override
  public Object readObject(ClassLoader classLoader) throws IOException
  {
    return ExtendedIOUtil.readObject(this, classLoader);
  }

  @Override
  public Object readObject(ClassResolver classResolver) throws IOException
  {
    return ExtendedIOUtil.readObject(this, classResolver);
  }

  public static ExtendedDataInputStream wrap(InputStream stream)
  {
    if (stream instanceof ExtendedDataInputStream)
    {
      return (ExtendedDataInputStream)stream;
    }

    return new ExtendedDataInputStream(stream);
  }

  public static InputStream unwrap(InputStream stream)
  {
    if (stream instanceof ExtendedDataInputStream)
    {
      return ((ExtendedDataInputStream)stream).in;
    }

    return stream;
  }
}
