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
package org.eclipse.internal.net4j.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation note: {@link AbstractCachingMap} does not preserve the
 * "modifyable view" contract of {@link Map#entrySet()} as well as of
 * {@link Map#keySet()}, i.e. they are disconnected sets and modifications
 * applied to them are not applied to their underlying
 * {@link AbstractCachingMap}.
 * <p>
 * 
 * @author Eike Stepper
 */
public abstract class AbstractCachingMap<K, V> extends AbstractDelegatingMap<K, V>
{
  protected AbstractCachingMap()
  {
  }

  protected AbstractCachingMap(Map<? extends K, ? extends V> t)
  {
    super(t);
  }

  public void clear()
  {
    cachedClear();
  }

  public boolean containsKey(Object key)
  {
    return cachedContainsKey(key) || delegatedContainsKey(key);
  }

  public boolean containsValue(Object value)
  {
    return cachedContainsValue(value) || cachedContainsValue(value);
  }

  public Set<Entry<K, V>> entrySet()
  {
    return mergedEntrySet();
  }

  public V get(Object key)
  {
    V result = cachedGet(key);
    if (result == null)
    {
      result = delegatedGet(key);
    }

    return result;
  }

  public boolean isEmpty()
  {
    return cachedIsEmpty() && delegatedIsEmpty();
  }

  public Set<K> keySet()
  {
    return mergedKeySet();
  }

  public V put(K key, V value)
  {
    return cachedPut(key, value);
  }

  public void putAll(Map<? extends K, ? extends V> t)
  {
    cachedPutAll(t);
  }

  public V remove(Object key)
  {
    return cachedRemove(key);
  }

  public int size()
  {
    return keySet().size();
  }

  public Collection<V> values()
  {
    return mergedValues();
  }

  @Override
  public boolean equals(Object obj)
  {
    return mergedEquals(obj);
  }

  @Override
  public int hashCode()
  {
    return mergedHashCode();
  }

  @Override
  public String toString()
  {
    return mergedToString();
  }

  protected void cachedClear()
  {
    getCache().clear();
  }

  protected boolean cachedContainsKey(Object key)
  {
    return getCache().containsKey(key);
  }

  protected boolean cachedContainsValue(Object value)
  {
    return getCache().containsValue(value);
  }

  protected Set<Entry<K, V>> cachedEntrySet()
  {
    return getCache().entrySet();
  }

  protected V cachedGet(Object key)
  {
    return getCache().get(key);
  }

  protected boolean cachedIsEmpty()
  {
    return getCache().isEmpty();
  }

  protected Set<K> cachedKeySet()
  {
    return getCache().keySet();
  }

  protected V cachedPut(K key, V value)
  {
    return getCache().put(key, value);
  }

  protected void cachedPutAll(Map<? extends K, ? extends V> t)
  {
    getCache().putAll(t);
  }

  protected V cachedRemove(Object key)
  {
    return getCache().remove(key);
  }

  protected int cachedSize()
  {
    return getCache().size();
  }

  protected Collection<V> cachedValues()
  {
    return getCache().values();
  }

  protected boolean cachedEquals(Object obj)
  {
    return getCache().equals(obj);
  }

  protected int cachedHashCode()
  {
    return getCache().hashCode();
  }

  protected String cachedToString()
  {
    return getCache().toString();
  }

  protected Set<Entry<K, V>> mergedEntrySet()
  {
    final Map<K, V> merged = new HashMap<K, V>();
    merged.putAll(getDelegate());
    merged.putAll(getCache());
    return merged.entrySet();
  }

  protected Set<K> mergedKeySet()
  {
    final Set<K> merged = new HashSet<K>();
    merged.addAll(getDelegate().keySet());
    merged.addAll(getCache().keySet());
    return merged;
  }

  protected Collection<V> mergedValues()
  {
    final List<V> result = new ArrayList<V>();
    for (K key : keySet())
    {
      result.add(get(key));
    }

    return result;
  }

  /**
   * @see AbstractMap#equals(Object)
   */
  protected boolean mergedEquals(Object o)
  {
    if (o == this)
      return true;

    if (!(o instanceof Map))
      return false;

    Map<K, V> t = (Map<K, V>)o;
    if (t.size() != size())
      return false;

    try
    {
      Iterator<Entry<K, V>> i = entrySet().iterator();
      while (i.hasNext())
      {
        Entry<K, V> e = i.next();
        K key = e.getKey();
        V value = e.getValue();
        if (value == null)
        {
          if (!(t.get(key) == null && t.containsKey(key)))
            return false;
        }
        else
        {
          if (!value.equals(t.get(key)))
            return false;
        }
      }
    }
    catch (ClassCastException unused)
    {
      return false;
    }
    catch (NullPointerException unused)
    {
      return false;
    }

    return true;
  }

  /**
   * @see AbstractMap#hashCode()
   */
  protected int mergedHashCode()
  {
    int h = 0;
    Iterator<Entry<K, V>> i = entrySet().iterator();
    while (i.hasNext())
      h += i.next().hashCode();
    return h;
  }

  /**
   * @see AbstractMap#toString()
   */
  protected String mergedToString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("{");

    Iterator<Entry<K, V>> i = entrySet().iterator();
    boolean hasNext = i.hasNext();
    while (hasNext)
    {
      Entry<K, V> e = i.next();
      K key = e.getKey();
      V value = e.getValue();
      if (key == this)
        buf.append("(this Map)");
      else
        buf.append(key);
      buf.append("=");
      if (value == this)
        buf.append("(this Map)");
      else
        buf.append(value);
      hasNext = i.hasNext();
      if (hasNext)
        buf.append(", ");
    }

    buf.append("}");
    return buf.toString();
  }

  protected abstract Map<K, V> getCache();
}
