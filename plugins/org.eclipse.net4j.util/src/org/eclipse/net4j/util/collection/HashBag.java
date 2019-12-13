/*
 * Copyright (c) 2008-2012, 2014-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class HashBag<T> implements Set<T>
{
  private Map<T, Counter> map;

  public HashBag()
  {
    map = new HashMap<T, Counter>();
  }

  public HashBag(int initialCapacity, float loadFactor)
  {
    map = new HashMap<T, Counter>(initialCapacity, loadFactor);
  }

  public HashBag(int initialCapacity)
  {
    map = new HashMap<T, Counter>(initialCapacity);
  }

  public HashBag(Map<? extends T, ? extends Counter> m)
  {
    map = new HashMap<T, Counter>(m);
  }

  /**
   * @since 3.0
   */
  public int getCounterFor(T o)
  {
    Counter counter = map.get(o);
    if (counter == null)
    {
      return 0;
    }

    return counter.getValue();
  }

  /**
   * @since 3.7
   */
  public int removeCounterFor(T o)
  {
    Counter counter = map.remove(o);
    if (counter == null)
    {
      return 0;
    }

    return counter.getValue();
  }

  @Override
  public boolean add(T o)
  {
    return add(o, 1);
  }

  /**
   * @since 3.4
   */
  public boolean add(T o, int count)
  {
    Counter counter = map.get(o);
    if (counter == null)
    {
      counter = new Counter(count);
      map.put(o, counter);
      return true;
    }

    counter.incValue(count);
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends T> c)
  {
    for (T t : c)
    {
      add(t);
    }

    return true;
  }

  @Override
  public void clear()
  {
    map.clear();
  }

  @Override
  public boolean contains(Object o)
  {
    return map.containsKey(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return map.keySet().containsAll(c);
  }

  @Override
  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  @Override
  public Iterator<T> iterator()
  {
    return map.keySet().iterator();
  }

  @Override
  public boolean remove(Object o)
  {
    return remove(o, 1);
  }

  /**
   * @since 3.4
   */
  public boolean remove(Object o, int count)
  {
    Counter counter = map.get(o);
    if (counter == null)
    {
      return false;
    }

    if (counter.decValue(count) <= 0)
    {
      map.remove(o);
    }

    return true;
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    boolean changed = false;
    for (Object object : c)
    {
      if (remove(object))
      {
        changed = true;
      }
    }

    return changed;
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size()
  {
    return map.size();
  }

  @Override
  public Object[] toArray()
  {
    return map.keySet().toArray();
  }

  @Override
  @SuppressWarnings("hiding")
  public <T> T[] toArray(T[] a)
  {
    return map.keySet().toArray(a);
  }

  @Override
  public String toString()
  {
    return map.toString();
  }

  /**
   * @author Eike Stepper
   */
  private static final class Counter
  {
    private int value;

    public Counter(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return value;
    }

    public int incValue(int count)
    {
      return value += count;
    }

    public int decValue(int count)
    {
      return value -= count;
    }

    @Override
    public String toString()
    {
      return Integer.toString(value);
    }
  }
}
