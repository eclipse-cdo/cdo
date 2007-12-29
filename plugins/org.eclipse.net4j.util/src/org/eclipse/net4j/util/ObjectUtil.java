/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;

import java.lang.reflect.Array;

/**
 * @author Eike Stepper
 */
public final class ObjectUtil
{
  private ObjectUtil()
  {
  }

  public static boolean equals(Object o1, Object o2)
  {
    if (o1 == null)
    {
      return o2 == null;
    }

    return o1.equals(o2);
  }

  public static int hashCode(Object o)
  {
    if (o == null)
    {
      return 0;
    }

    return o.hashCode();
  }

  public static int hashCode(long num)
  {
    return (int)(num >> 32) ^ (int)(num & 0xffffffff);
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] appendtoArray(T[] array, T... elements)
  {
    T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length + elements.length);
    System.arraycopy(array, 0, result, 0, array.length);
    System.arraycopy(elements, 0, result, array.length, elements.length);
    return result;
  }
}
