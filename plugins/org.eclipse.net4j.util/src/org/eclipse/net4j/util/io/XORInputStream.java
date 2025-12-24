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
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public class XORInputStream extends DelegatingInputStream
{
  private int[] key;

  private int index;

  public XORInputStream(InputStream in, int... key)
  {
    super(in);
    this.key = key;
  }

  public int[] getKey()
  {
    return key;
  }

  @Override
  public int read() throws IOException
  {
    int b = super.read();
    if (b != -1)
    {
      if (key != null && key.length != 0)
      {
        if (index == key.length)
        {
          index = 0;
        }

        b = b & 0xFF ^ key[index++] & 0xFF;
      }
    }

    return b;
  }
}
