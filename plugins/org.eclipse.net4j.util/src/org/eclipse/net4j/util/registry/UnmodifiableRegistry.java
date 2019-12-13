/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.registry;

import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UnmodifiableRegistry<K, V> implements IRegistry<K, V>
{
  private IRegistry<K, V> delegate;

  public UnmodifiableRegistry(IRegistry<K, V> delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public void addListener(IListener listener)
  {
    delegate.addListener(listener);
  }

  @Override
  public void removeListener(IListener listener)
  {
    delegate.removeListener(listener);
  }

  /**
   * @since 3.0
   */
  @Override
  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  /**
   * @since 3.0
   */
  @Override
  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  @Override
  public V put(K key, V value)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> t)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void commit()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void commit(boolean notifications)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setAutoCommit(boolean on)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAutoCommit()
  {
    return delegate.isAutoCommit();
  }

  @Override
  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }

  @Override
  public int size()
  {
    return delegate.size();
  }

  @Override
  public Entry<K, V>[] getElements()
  {
    return delegate.getElements();
  }

  @Override
  public V get(Object key)
  {
    return delegate.get(key);
  }

  @Override
  public boolean containsKey(Object key)
  {
    return delegate.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value)
  {
    return delegate.containsValue(value);
  }

  @Override
  public Set<Entry<K, V>> entrySet()
  {
    return delegate.entrySet();
  }

  @Override
  public Set<K> keySet()
  {
    return delegate.keySet();
  }

  @Override
  public Collection<V> values()
  {
    return delegate.values();
  }

  @Override
  public boolean equals(Object o)
  {
    return delegate.equals(o);
  }

  @Override
  public int hashCode()
  {
    return delegate.hashCode();
  }
}
