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
package org.eclipse.net4j.util.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Implementation note: {@link AbstractDelegatingMap} does not necessarily
 * preserve the "modifyable view" contract of {@link Map#entrySet()} as well as
 * of {@link Map#keySet()}, i.e. they might be disconnected sets and
 * modifications applied to them might not be applied to their underlying
 * {@link AbstractDelegatingMap}.
 * <p>
 * 
 * @author Eike Stepper
 */
public abstract class AbstractDelegatingMap<K, V> implements Map<K, V>
{
  protected AbstractDelegatingMap()
  {
  }

  protected AbstractDelegatingMap(Map<? extends K, ? extends V> t)
  {
    putAll(t);
  }

  public void clear()
  {
    delegatedClear();
  }

  public boolean containsKey(Object key)
  {
    return delegatedContainsKey(key);
  }

  public boolean containsValue(Object value)
  {
    return delegatedContainsValue(value);
  }

  public Set<Entry<K, V>> entrySet()
  {
    return delegatedEntrySet();
  }

  public V get(Object key)
  {
    return delegatedGet(key);
  }

  public boolean isEmpty()
  {
    return delegatedIsEmpty();
  }

  public Set<K> keySet()
  {
    return delegatedKeySet();
  }

  public V put(K key, V value)
  {
    return delegatedPut(key, value);
  }

  public void putAll(Map<? extends K, ? extends V> t)
  {
    delegatedPutAll(t);
  }

  public V remove(Object key)
  {
    return delegatedRemove(key);
  }

  public int size()
  {
    return delegatedSize();
  }

  public Collection<V> values()
  {
    return delegatedValues();
  }

  @Override
  public boolean equals(Object obj)
  {
    return delegatedEquals(obj);
  }

  @Override
  public int hashCode()
  {
    return delegatedHashCode();
  }

  @Override
  public String toString()
  {
    return delegatedToString();
  }

  protected void delegatedClear()
  {
    getDelegate().clear();
  }

  protected boolean delegatedContainsKey(Object key)
  {
    return getDelegate().containsKey(key);
  }

  protected boolean delegatedContainsValue(Object value)
  {
    return getDelegate().containsValue(value);
  }

  protected Set<Entry<K, V>> delegatedEntrySet()
  {
    return getDelegate().entrySet();
  }

  protected V delegatedGet(Object key)
  {
    return getDelegate().get(key);
  }

  protected boolean delegatedIsEmpty()
  {
    return getDelegate().isEmpty();
  }

  protected Set<K> delegatedKeySet()
  {
    return getDelegate().keySet();
  }

  protected V delegatedPut(K key, V value)
  {
    return getDelegate().put(key, value);
  }

  protected void delegatedPutAll(Map<? extends K, ? extends V> t)
  {
    getDelegate().putAll(t);
  }

  protected V delegatedRemove(Object key)
  {
    return getDelegate().remove(key);
  }

  protected int delegatedSize()
  {
    return getDelegate().size();
  }

  protected Collection<V> delegatedValues()
  {
    return getDelegate().values();
  }

  protected boolean delegatedEquals(Object obj)
  {
    return getDelegate().equals(obj);
  }

  protected int delegatedHashCode()
  {
    return getDelegate().hashCode();
  }

  protected String delegatedToString()
  {
    return getDelegate().toString();
  }

  protected abstract Map<K, V> getDelegate();
}
