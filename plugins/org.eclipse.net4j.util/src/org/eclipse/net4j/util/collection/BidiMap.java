/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.CheckUtil;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class BidiMap<K, V> extends AbstractMap<K, V>
{
  private final Map<Object, Object> map;

  private final Class<K> keyClass;

  private final Class<V> valueClass;

  private BidiMap(Class<K> keyClass, Class<V> valueClass, Map<Object, Object> map)
  {
    CheckUtil.checkArg(keyClass != valueClass, "Key and value class are the same");
    CheckUtil.checkArg(!keyClass.isAssignableFrom(valueClass), "Key class is assignable from value class");
    CheckUtil.checkArg(!valueClass.isAssignableFrom(keyClass), "Value class is assignable from key class");

    this.keyClass = keyClass;
    this.valueClass = valueClass;
    this.map = map;
  }

  public BidiMap(Class<K> keyClass, Class<V> valueClass)
  {
    this(keyClass, valueClass, new HashMap<>());
  }

  public final Class<K> getKeyClass()
  {
    return keyClass;
  }

  public final Class<V> getValueClass()
  {
    return valueClass;
  }

  public final Map<V, K> invert()
  {
    return new BidiMap<>(valueClass, keyClass, map);
  }

  @Override
  public V put(K key, V value)
  {
    CheckUtil.checkArg(key, "Key is null");
    CheckUtil.checkArg(value, "Value is null");

    @SuppressWarnings("unchecked")
    V oldValue = (V)map.put(key, value);
    map.put(value, key);

    return oldValue;
  }

  @Override
  public V remove(Object key)
  {
    if (keyClass.isInstance(key))
    {
      @SuppressWarnings("unchecked")
      V oldValue = (V)map.remove(key);
      if (oldValue != null)
      {
        map.remove(oldValue);
      }

      return oldValue;
    }

    return null;
  }

  @Override
  public void clear()
  {
    map.clear();
  }

  @Override
  public int size()
  {
    return map.size() / 2;
  }

  @Override
  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key)
  {
    return keyClass.isInstance(key) && map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value)
  {
    return invert().containsKey(value);
  }

  @Override
  public V get(Object key)
  {
    if (keyClass.isInstance(key))
    {
      @SuppressWarnings("unchecked")
      V value = (V)map.get(key);
      return value;
    }

    return null;
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet()
  {
    return new AbstractSet<Map.Entry<K, V>>()
    {
      @Override
      public Iterator<Map.Entry<K, V>> iterator()
      {
        final Iterator<Map.Entry<Object, Object>> delegate = map.entrySet().iterator();

        return new AbstractIterator<Map.Entry<K, V>>()
        {
          @Override
          protected Object computeNextElement()
          {
            while (delegate.hasNext())
            {
              Map.Entry<Object, Object> element = delegate.next();
              if (keyClass.isInstance(element.getKey()))
              {
                return element;
              }
            }

            return END_OF_DATA;
          }
        };
      }

      @Override
      public int size()
      {
        return BidiMap.this.size() / 2;
      }
    };
  }
}
