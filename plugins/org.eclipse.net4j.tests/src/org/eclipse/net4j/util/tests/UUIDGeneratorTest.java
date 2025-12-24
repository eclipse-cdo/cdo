/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.UUIDGenerator;
import org.eclipse.net4j.util.io.IOUtil;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class UUIDGeneratorTest extends AbstractOMTest
{
  public void testCodec()
  {
    byte[] uuid = new byte[16];
    long start = System.currentTimeMillis();

    for (int b3 = Byte.MIN_VALUE; b3 <= Byte.MAX_VALUE; b3++)
    {
      IOUtil.OUT().println(b3);
      for (int b2 = Byte.MIN_VALUE; b2 <= Byte.MAX_VALUE; b2++)
      {
        for (int b1 = Byte.MIN_VALUE; b1 <= Byte.MAX_VALUE; b1++)
        {
          for (int off = 0; off < 13; off++)
          {
            check(uuid, b1, b2, b3, off);
          }
        }
      }
    }

    IOUtil.OUT().println("Millis: " + (System.currentTimeMillis() - start));
  }

  private static void check(byte[] uuid, int b1, int b2, int b3, int off)
  {
    uuid[0 + off] = (byte)b1;
    uuid[2 + off] = (byte)b2;
    uuid[3 + off] = (byte)b3;

    String encoded = UUIDGenerator.DEFAULT.encode(uuid);
    byte[] decoded = UUIDGenerator.DEFAULT.decode(encoded);

    for (int i = 0; i < 3; i++)
    {
      assertEquals(uuid[i + off], decoded[i + off]);
    }
  }
}
