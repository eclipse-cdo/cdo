/*
 * Copyright (c) 2015, 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Various static helper methods.
 *
 * @author Eike Stepper
 * @since 3.5
 */
public final class CollectionUtil
{
  private CollectionUtil()
  {
  }

  public static <T> Iterator<T> dump(Iterator<T> it)
  {
    List<T> list = new ArrayList<>();
    while (it.hasNext())
    {
      list.add(it.next());
    }

    System.out.println(list);
    return list.iterator();
  }

  /**
   * @since 3.16
   */
  public static <T> boolean addNotNull(Collection<? super T> c, T e)
  {
    if (e != null)
    {
      return c.add(e);
    }

    return false;
  }

  /**
   * @since 3.16
   */
  public static <K, V> List<K> removeAll(Map<K, V> map, BiPredicate<K, V> predicate)
  {
    List<K> keys = new ArrayList<>();

    for (Map.Entry<K, V> entry : map.entrySet())
    {
      K key = entry.getKey();
      V value = entry.getValue();

      if (predicate.test(key, value))
      {
        keys.add(key);
      }
    }

    for (K key : keys)
    {
      map.remove(key);
    }

    return keys;
  }
}
