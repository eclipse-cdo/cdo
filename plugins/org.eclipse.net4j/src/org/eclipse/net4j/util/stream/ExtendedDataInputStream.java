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
package org.eclipse.net4j.util.stream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author Eike Stepper
 */
public class ExtendedDataInputStream extends DataInputStream implements ExtendedDataInput
{
  public ExtendedDataInputStream(InputStream in)
  {
    super(in);
  }

  public byte[] readByteArray() throws IOException
  {
    int length = readInt();
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

    int bytes = read(b);
    if (bytes != length)
    {
      throw new IOException("Unable to read " + length + " bytes (after " + bytes + " bytes)");
    }

    return b;
  }

  public String readString() throws IOException
  {
    byte[] bytes = readByteArray();
    if (bytes == null)
    {
      return null;
    }

    return new String(bytes);
  }

  public Object readObject() throws IOException, ClassNotFoundException
  {
    ObjectInputStream wrapper = new ObjectInputStream(this);
    return wrapper.readObject();
  }
}
