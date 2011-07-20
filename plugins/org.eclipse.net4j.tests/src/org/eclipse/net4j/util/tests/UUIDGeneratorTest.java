/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.UUIDGenerator;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class UUIDGeneratorTest extends AbstractOMTest
{
  public void testCodec()
  {
    byte[] uuid = new byte[16];
    long start = System.currentTimeMillis();

    for (int b3 = Byte.MIN_VALUE; b3 <= Byte.MAX_VALUE; b3++)
    {
      for (int b2 = Byte.MIN_VALUE; b2 <= Byte.MAX_VALUE; b2++)
      {
        // System.out.println(b3 + ", " + b2);
        for (int b1 = Byte.MIN_VALUE; b1 <= Byte.MAX_VALUE; b1++)
        {
          for (int off = 0; off < 13; off++)
          {
            check(uuid, b1, b2, b3, off);
          }
        }
      }
    }

    long millis = System.currentTimeMillis() - start;
    System.out.println("Millis: " + millis);
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
