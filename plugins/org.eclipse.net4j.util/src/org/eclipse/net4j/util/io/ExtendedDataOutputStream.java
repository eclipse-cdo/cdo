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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

  public void writeByteArray(byte[] b) throws IOException
  {
    if (b != null)
    {
      writeInt(b.length);
      write(b);
    }
    else
    {
      writeInt(-1);
    }
  }

  public void writeString(String str) throws IOException
  {
    if (str != null)
    {
      writeByteArray(str.getBytes());
    }
    else
    {
      writeInt(-1);
    }
  }

  public void writeObject(Object object) throws IOException
  {
    ObjectOutputStream wrapper = new ObjectOutputStream(this);
    wrapper.writeObject(object);
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
