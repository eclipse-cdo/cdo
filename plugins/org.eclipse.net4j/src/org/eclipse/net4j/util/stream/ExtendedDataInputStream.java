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
package org.eclipse.net4j.util.stream;

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

  public byte[] readByteArray() throws IOException
  {
    int length = readInt();
    if (length < 0)
    {
      throw new IndexOutOfBoundsException();
    }

    byte[] b = new byte[length];
    read(b);
    return b;
  }

  public String readString() throws IOException
  {
    try
    {
      return new String(readByteArray());
    }
    catch (IndexOutOfBoundsException ex)
    {
      return null;
    }
  }
}
