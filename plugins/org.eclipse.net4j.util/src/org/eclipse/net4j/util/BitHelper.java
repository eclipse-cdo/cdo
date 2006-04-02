/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


public class BitHelper
{
  public static final int LOWEST_BIT = 0x0000001;

  public static int getMask(int bits, int rightPad)
  {
    int mask = LOWEST_BIT;

    // Create bit mask
    for (int i = 0; i < bits - 1; i++)
    {
      mask <<= 1;
      mask |= LOWEST_BIT;
    }

    return mask << rightPad;
  }

  public static int getMask(int bits)
  {
    return getMask(bits, 0);
  }
}
