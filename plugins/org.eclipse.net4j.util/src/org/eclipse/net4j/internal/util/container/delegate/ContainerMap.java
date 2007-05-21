/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.container.delegate;

import org.eclipse.net4j.internal.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.delegate.IContainerMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ContainerMap<K, V> extends AbstractDelegator<Map.Entry<K, V>> implements IContainerMap<K, V>
{
  private Map<K, V> delegate;

  public ContainerMap(Map<K, V> delegate)
  {
    this.delegate = delegate;
  }

  public Map<K, V> getDelegate()
  {
    return delegate;
  }

  /**
   * @category WRITE
   */
  public void clear()
  {
    if (!isEmpty())
    {
      ContainerEvent event = createEvent(getDelegate().entrySet(), IContainerDelta.Kind.REMOVED);
      getDelegate().clear();
      fireEvent(event);
    }
  }

  /**
   * @category READ
   */
  public boolean containsKey(Object key)
  {
    return getDelegate().containsKey(key);
  }

  /**
   * @category READ
   */
  public boolean containsValue(Object value)
  {
    return getDelegate().containsValue(value);
  }

  /**
   * @category READ
   */
  public V get(Object key)
  {
    return getDelegate().get(key);
  }

  /**
   * @category WRITE
   */
  public V put(K key, V value)
  {
    ContainerEvent event = new ContainerEvent(this);
    V removed = getDelegate().put(key, value);
    if (removed != null)
    {
      event.addDelta(new MapEntry(key, removed), IContainerDelta.Kind.REMOVED);
    }

    event.addDelta(new MapEntry(key, value), IContainerDelta.Kind.ADDED);
    fireEvent(event);
    return removed;
  }

  /**
   * @category WRITE
   */
  public void putAll(Map<? extends K, ? extends V> t)
  {
    ContainerEvent event = new ContainerEvent(this);
    Iterator<? extends Entry<? extends K, ? extends V>> i = t.entrySet().iterator();
    while (i.hasNext())
    {
      Entry<? extends K, ? extends V> e = i.next();
      K key = e.getKey();
      V value = e.getValue();
      V removed = getDelegate().put(key, value);
      if (removed != null)
      {
        event.addDelta(new MapEntry(key, removed), IContainerDelta.Kind.REMOVED);
      }

      event.addDelta(e, IContainerDelta.Kind.ADDED);
    }

    dispatchEvent(event);
  }

  /**
   * @category WRITE
   */
  public V remove(Object key)
  {
    V removed = getDelegate().remove(key);
    if (removed != null)
    {
      fireRemovedEvent(new MapEntry(key, removed));
    }

    return removed;
  }

  /**
   * @category READ
   */
  public int size()
  {
    return getDelegate().size();
  }

  /**
   * @category READ
   */
  public Map.Entry<K, V>[] getElements()
  {
    return (Entry<K, V>[])getDelegate().entrySet().toArray();
  }

  /**
   * @category READ
   */
  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  /**
   * @category READ
   */
  public Set<Map.Entry<K, V>> entrySet()
  {
    return new ContainerSet(getDelegate().entrySet());
  }

  /**
   * @category READ
   */
  public Set<K> keySet()
  {
    return new ContainerSet(getDelegate().keySet());
  }

  /**
   * @category READ
   */
  public Collection<V> values()
  {
    return new ContainerCollection(getDelegate().values());
  }

  /**
   * @author Eike Stepper
   */
  private static final class MapEntry<K, V> implements Map.Entry<K, V>
  {
    private K key;

    private V value;

    private MapEntry(K key, V value)
    {
      this.key = key;
      this.value = value;
    }

    public K getKey()
    {
      return key;
    }

    public V getValue()
    {
      return value;
    }

    public V setValue(V value)
    {
      throw new UnsupportedOperationException();
    }
  }
}
