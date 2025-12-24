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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class ExtendedDataOutputStream extends DataOutputStream implements ExtendedDataOutput
{
  public ExtendedDataOutputStream(OutputStream out)
  {
    super(out);
  }

  /**
   * @since 3.7
   */
  @Override
  public void writeVarInt(int v) throws IOException
  {
    ExtendedIOUtil.writeVarInt(this, v);
  }

  /**
   * @since 3.7
   */
  @Override
  public void writeVarLong(long v) throws IOException
  {
    ExtendedIOUtil.writeVarLong(this, v);
  }

  @Override
  public void writeByteArray(byte[] b) throws IOException
  {
    ExtendedIOUtil.writeByteArray(this, b);
  }

  @Override
  public void writeString(String str) throws IOException
  {
    ExtendedIOUtil.writeString(this, str);
  }

  /**
   * @since 3.0
   */
  @Override
  public void writeEnum(Enum<?> literal) throws IOException
  {
    ExtendedIOUtil.writeEnum(this, literal);
  }

  /**
   * @since 3.4
   */
  @Override
  public void writeException(Throwable t) throws IOException
  {
    ExtendedIOUtil.writeException(this, t);
  }

  @Override
  public void writeObject(Object object) throws IOException
  {
    ExtendedIOUtil.writeObject(this, object);
  }

  public static ExtendedDataOutputStream wrap(OutputStream stream)
  {
    if (stream instanceof ExtendedDataOutputStream)
    {
      return (ExtendedDataOutputStream)stream;
    }

    return new ExtendedDataOutputStream(stream);
  }

  public static OutputStream unwrap(OutputStream stream)
  {
    if (stream instanceof ExtendedDataOutputStream)
    {
      return ((ExtendedDataOutputStream)stream).out;
    }

    return stream;
  }
}
