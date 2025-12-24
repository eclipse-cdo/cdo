/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class XOROutputStream extends DelegatingOutputStream
{
  private int[] key;

  private int index;

  public XOROutputStream(OutputStream out, int... key)
  {
    super(out);
    this.key = key;
  }

  public int[] getKey()
  {
    return key;
  }

  @Override
  public void write(int b) throws IOException
  {
    if (key != null && key.length != 0)
    {
      if (index == key.length)
      {
        index = 0;
      }

      b = b & 0xFF ^ key[index++] & 0xFF;
    }

    super.write(b);
  }
}
