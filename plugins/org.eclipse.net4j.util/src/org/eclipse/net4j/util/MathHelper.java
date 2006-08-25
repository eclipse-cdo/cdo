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


public class MathHelper
{

  public static boolean isPrime(int number)
  {
    if (number % 2 == 0)
    {
      return false;
    }
    else
    {
      int max = (int) Math.floor(Math.sqrt(number));
      for (int i = 3; i <= max; i += 2)
      {
        if (number % i == 0)
        {
          return false;
        }
      }
    }

    return true;
  }

  public static int getPrime(int min)
  {
    while (!isPrime(min))
    {
      ++min;
    }

    return min;
  }

}
