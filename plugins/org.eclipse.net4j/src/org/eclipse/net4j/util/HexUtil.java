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
package org.eclipse.net4j.util;

/**
 * @author Eike Stepper
 */
public final class HexUtil
{
  public static final char DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
      'c', 'd', 'e', 'f', };

  private HexUtil()
  {
  }

  public static String toHex(int b)
  {
    assertByte(b);
    return "" + DIGITS[b >> 4] + DIGITS[b & 0xf]; //$NON-NLS-1$
  }

  public static void appendHex(StringBuilder builder, int b)
  {
    assertByte(b);
    builder.append(DIGITS[b >> 4]);
    builder.append(DIGITS[b & 0xf]);
  }

  private static void assertByte(int b)
  {
    if (b < 0 || b > 255)
    {
      throw new IllegalArgumentException("b < 0 || b > 255"); //$NON-NLS-1$
    }
  }
}
