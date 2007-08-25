/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class ExtendedIOUtil
{
  private ExtendedIOUtil()
  {
  }

  public static void writeByteArray(DataOutput out, byte[] b) throws IOException
  {
    if (b != null)
    {
      out.writeInt(b.length);
      out.write(b);
    }
    else
    {
      out.writeInt(-1);
    }
  }

  public static void writeString(DataOutput out, String str) throws IOException
  {
    if (str != null)
    {
      writeByteArray(out, str.getBytes());
    }
    else
    {
      out.writeInt(-1);
    }
  }

  public static byte[] readByteArray(DataInput in) throws IOException
  {
    int length = in.readInt();
    if (length < 0)
    {
      return null;
    }

    byte[] b;
    try
    {
      b = new byte[length];
    }
    catch (Throwable t)
    {
      throw new IOException("Unable to allocate " + length + " bytes");
    }

    in.readFully(b);
    return b;
  }

  public static String readString(DataInput in) throws IOException
  {
    byte[] bytes = readByteArray(in);
    if (bytes == null)
    {
      return null;
    }

    return new String(bytes);
  }
}
