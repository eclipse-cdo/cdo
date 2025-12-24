/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class XORStreamWrapper implements IStreamWrapper
{
  private int[] key;

  public XORStreamWrapper(int[] key)
  {
    this.key = key;
  }

  public int[] getKey()
  {
    return key;
  }

  @Override
  public XORInputStream wrapInputStream(InputStream in) throws IOException
  {
    if (in instanceof XORInputStream)
    {
      return (XORInputStream)in;
    }

    return new XORInputStream(in, key);
  }

  @Override
  public XOROutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (out instanceof XOROutputStream)
    {
      return (XOROutputStream)out;
    }

    return new XOROutputStream(out, key);
  }

  @Override
  public void finishInputStream(InputStream in) throws IOException
  {
  }

  @Override
  public void finishOutputStream(OutputStream out) throws IOException
  {
  }
}
