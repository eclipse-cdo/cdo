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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class XORStreamWrapper implements IStreamWrapper<XORInputStream, XOROutputStream>
{
  private byte[] key;

  public XORStreamWrapper(byte[] key)
  {
    this.key = key;
  }

  public byte[] getKey()
  {
    return key;
  }

  public XORInputStream wrapInputStream(InputStream in) throws IOException
  {
    if (in instanceof XORInputStream)
    {
      return (XORInputStream)in;
    }

    return new XORInputStream(in, key);
  }

  public XOROutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (out instanceof XOROutputStream)
    {
      return (XOROutputStream)out;
    }

    return new XOROutputStream(out, key);
  }

  public void finishInputStream(XORInputStream in) throws IOException
  {
  }

  public void finishOutputStream(XOROutputStream out) throws IOException
  {
  }
}
