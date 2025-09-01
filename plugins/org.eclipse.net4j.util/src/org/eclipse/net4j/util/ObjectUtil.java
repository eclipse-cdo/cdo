/*
 * Copyright (c) 2007, 2011-2013, 2015, 2016, 2020, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.collection.Closeable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Various static helper methods.
 *
 * @author Eike Stepper
 */
public final class ObjectUtil
{
  private ObjectUtil()
  {
  }

  /**
   * @since 3.6
   */
  public static boolean never()
  {
    return false;
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

  /**
   * A collision-free hash code for small sets (&lt;=4) of small, positive integers (&lt;=128)
   *
   * @since 3.2
   */
  public static int hashCode(int... values)
  {
    int hash = 0;
    for (int i = 0; i < values.length; i++)
    {
      hash += values[i];
      hash = (hash << 7) - hash;
    }

    return hash;
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

  /**
   * @since 3.1
   */
  public static <T> boolean isEmpty(T[] array)
  {
    return array == null || array.length == 0;
  }

  /**
   * @since 3.1
   */
  public static <T extends Map<?, ?>> boolean isEmpty(Map<?, ?> map)
  {
    return map == null || map.isEmpty();
  }

  /**
   * @since 3.1
   */
  public static <T extends Collection<?>> boolean isEmpty(Collection<?> collection)
  {
    return collection == null || collection.isEmpty();
  }

  /**
   * @since 3.1
   */
  public static boolean isEmpty(String string)
  {
    return string == null || string.length() == 0;
  }

  /**
   * @since 3.26
   */
  public static <T> int size(T[] array)
  {
    return array == null ? 0 : array.length;
  }

  /**
   * @since 3.26
   */
  public static <T extends Map<?, ?>> int size(Map<?, ?> map)
  {
    return map == null ? 0 : map.size();
  }

  /**
   * @since 3.26
   */
  public static <T extends Collection<?>> int size(Collection<?> collection)
  {
    return collection == null ? 0 : collection.size();
  }

  /**
   * @since 3.26
   */
  public static int size(String string)
  {
    return string == null ? 0 : string.length();
  }

  /**
   * @since 3.3
   */
  public static Exception close(Object object)
  {
    try
    {
      if (object instanceof Closeable)
      {
        Closeable closeable = (Closeable)object;
        closeable.close();
      }
      else if (object instanceof java.io.Closeable)
      {
        java.io.Closeable closeable = (java.io.Closeable)object;
        closeable.close();
      }
    }
    catch (Exception ex)
    {
      return ex;
    }

    return null;
  }

  /**
   * @since 3.3
   */
  public static <T> T notNull(T object)
  {
    if (object == null)
    {
      throw new NullPointerException();
    }

    return object;
  }

  /**
   * @since 3.28
   */
  public static <T> T requireNonNullElse(T obj, T defaultObj)
  {
    return obj != null ? obj : Objects.requireNonNull(defaultObj, "defaultObj");
  }

  /**
   * @since 3.28
   */
  public static <T> T requireNonNullElseGet(T obj, Supplier<? extends T> supplier)
  {
    return obj != null ? obj : Objects.requireNonNull(Objects.requireNonNull(supplier, "supplier").get(), "supplier.get()");
  }

  /**
   * Attempts to cast an {@code object} as an instance of the given {@code type}.
   *
   * @param object an object to cast to some {@code type}
   * @param type the type to cast the {@code object} to
   * @return the {@code object} or {@code null} if it is not of the required {@code type}
   *
   * @since 3.4
   */
  public static <T> T tryCast(Object object, Class<T> type)
  {
    if (type.isInstance(object))
    {
      return type.cast(object);
    }

    return null;
  }

  /**
   * @since 3.13
   */
  public static <T> void forEachSafe(Iterable<T> iterable, Consumer<T> consumer)
  {
    if (iterable != null)
    {
      for (T object : iterable)
      {
        try
        {
          consumer.accept(object);
        }
        catch (RuntimeException | Error ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @since 3.13
   */
  public static <T> void forEachSafe(T[] array, Consumer<T> consumer)
  {
    if (array != null)
    {
      for (int i = 0; i < array.length; i++)
      {
        try
        {
          consumer.accept(array[i]);
        }
        catch (RuntimeException | Error ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }
}
