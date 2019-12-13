/*
 * Copyright (c) 2007, 2008, 2010-2013, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DataInputExtender implements ExtendedDataInput, Closeable
{
  private DataInput input;

  public DataInputExtender(DataInput input)
  {
    this.input = input;
  }

  @Override
  public boolean readBoolean() throws IOException
  {
    return input.readBoolean();
  }

  @Override
  public byte readByte() throws IOException
  {
    return input.readByte();
  }

  @Override
  public char readChar() throws IOException
  {
    return input.readChar();
  }

  @Override
  public double readDouble() throws IOException
  {
    return input.readDouble();
  }

  @Override
  public float readFloat() throws IOException
  {
    return input.readFloat();
  }

  @Override
  public void readFully(byte[] b, int off, int len) throws IOException
  {
    input.readFully(b, off, len);
  }

  @Override
  public void readFully(byte[] b) throws IOException
  {
    input.readFully(b);
  }

  @Override
  public int readInt() throws IOException
  {
    return input.readInt();
  }

  @Override
  public String readLine() throws IOException
  {
    return input.readLine();
  }

  @Override
  public long readLong() throws IOException
  {
    return input.readLong();
  }

  @Override
  public short readShort() throws IOException
  {
    return input.readShort();
  }

  @Override
  public int readUnsignedByte() throws IOException
  {
    return input.readUnsignedByte();
  }

  @Override
  public int readUnsignedShort() throws IOException
  {
    return input.readUnsignedShort();
  }

  @Override
  public String readUTF() throws IOException
  {
    return input.readUTF();
  }

  /**
   * @since 3.7
   */
  @Override
  public int readVarInt() throws IOException
  {
    return ExtendedIOUtil.readVarInt(input);
  }

  /**
   * @since 3.7
   */
  @Override
  public long readVarLong() throws IOException
  {
    return ExtendedIOUtil.readVarLong(input);
  }

  @Override
  public byte[] readByteArray() throws IOException
  {
    return ExtendedIOUtil.readByteArray(input);
  }

  @Override
  public Object readObject() throws IOException
  {
    return ExtendedIOUtil.readObject(input);
  }

  @Override
  public Object readObject(ClassLoader classLoader) throws IOException
  {
    return ExtendedIOUtil.readObject(input, classLoader);
  }

  @Override
  public Object readObject(ClassResolver classResolver) throws IOException
  {
    return ExtendedIOUtil.readObject(input, classResolver);
  }

  @Override
  public String readString() throws IOException
  {
    return ExtendedIOUtil.readString(input);
  }

  /**
   * @since 3.0
   */
  @Override
  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
  {
    return ExtendedIOUtil.readEnum(input, type);
  }

  /**
   * @since 3.4
   */
  @Override
  public Throwable readException() throws IOException
  {
    return ExtendedIOUtil.readException(input);
  }

  @Override
  public int skipBytes(int n) throws IOException
  {
    return input.skipBytes(n);
  }

  /**
   * @since 3.6
   */
  @Override
  public void close() throws IOException
  {
    if (input instanceof Closeable)
    {
      ((Closeable)input).close();
    }
  }
}
