/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import java.lang.reflect.Array;

/**
 * Various static helper methods.
 *
 * @author Eike Stepper
 * @since 3.4
 */
public final class ArrayUtil
{
  private ArrayUtil()
  {
  }

  public static <T> T[] add(T[] array, T element)
  {
    int length = array.length;

    @SuppressWarnings("unchecked")
    T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), length + 1);

    System.arraycopy(array, 0, newArray, 0, length);
    newArray[length] = element;
    return newArray;
  }

  public static <T> T[] remove(T[] array, T element)
  {
    int length = array.length;
    for (int i = 0; i < array.length; i++)
    {
      T t = array[i];
      if (ObjectUtil.equals(t, element))
      {
        @SuppressWarnings("unchecked")
        T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), length - 1);

        if (i != 0)
        {
          System.arraycopy(array, 0, newArray, 0, i);
        }

        int next = i + 1;
        if (next <= length)
        {
          System.arraycopy(array, next, newArray, i, length - next);
        }

        return newArray;
      }
    }

    return array;
  }

  public static String toString(Object[] array)
  {
    StringBuilder builder = new StringBuilder();
    builder.append('{');

    for (int i = 0; i < array.length; i++)
    {
      Object element = array[i];
      if (i != 0)
      {
        builder.append(", ");
      }

      builder.append(element);
    }

    builder.append('}');
    return builder.toString();
  }
}
