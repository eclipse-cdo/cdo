/*
 * Copyright (c) 2007, 2008, 2010-2013, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DataOutputExtender implements ExtendedDataOutput, Closeable
{
  private DataOutput output;

  public DataOutputExtender(DataOutput output)
  {
    this.output = output;
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException
  {
    output.write(b, off, len);
  }

  @Override
  public void write(byte[] b) throws IOException
  {
    output.write(b);
  }

  @Override
  public void write(int b) throws IOException
  {
    output.write(b);
  }

  @Override
  public void writeBoolean(boolean v) throws IOException
  {
    output.writeBoolean(v);
  }

  @Override
  public void writeByte(int v) throws IOException
  {
    output.writeByte(v);
  }

  @Override
  public void writeBytes(String s) throws IOException
  {
    output.writeBytes(s);
  }

  @Override
  public void writeChar(int v) throws IOException
  {
    output.writeChar(v);
  }

  @Override
  public void writeChars(String s) throws IOException
  {
    output.writeChars(s);
  }

  @Override
  public void writeDouble(double v) throws IOException
  {
    output.writeDouble(v);
  }

  @Override
  public void writeFloat(float v) throws IOException
  {
    output.writeFloat(v);
  }

  @Override
  public void writeInt(int v) throws IOException
  {
    output.writeInt(v);
  }

  @Override
  public void writeLong(long v) throws IOException
  {
    output.writeLong(v);
  }

  @Override
  public void writeShort(int v) throws IOException
  {
    output.writeShort(v);
  }

  @Override
  public void writeUTF(String str) throws IOException
  {
    output.writeUTF(str);
  }

  /**
   * @since 3.7
   */
  @Override
  public void writeVarInt(int v) throws IOException
  {
    ExtendedIOUtil.writeVarInt(output, v);
  }

  /**
  * @since 3.7
  */
  @Override
  public void writeVarLong(long v) throws IOException
  {
    ExtendedIOUtil.writeVarLong(output, v);
  }

  @Override
  public void writeByteArray(byte[] b) throws IOException
  {
    ExtendedIOUtil.writeByteArray(output, b);
  }

  @Override
  public void writeObject(Object object) throws IOException
  {
    ExtendedIOUtil.writeObject(output, object);
  }

  @Override
  public void writeString(String str) throws IOException
  {
    ExtendedIOUtil.writeString(output, str);
  }

  /**
   * @since 3.0
   */
  @Override
  public void writeEnum(Enum<?> literal) throws IOException
  {
    ExtendedIOUtil.writeEnum(output, literal);
  }

  /**
   * @since 3.4
   */
  @Override
  public void writeException(Throwable t) throws IOException
  {
    ExtendedIOUtil.writeException(output, t);
  }

  /**
   * @since 3.6
   */
  @Override
  public void close() throws IOException
  {
    if (output instanceof Closeable)
    {
      ((Closeable)output).close();
    }
  }
}
