/*
 * Copyright (c) 2007-2009, 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ref;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A {@link ConcurrentMap} implementation that uses {@link KeyedReference} instances ({@link KeyedStrongReference},
 * {@link KeyedSoftReference}, {@link KeyedWeakReference} or {@link KeyedPhantomReference}) as its values.
 * <p>
 * A <code>ReferenceValueMap</code> can be used to cache mappings until the <em>value</em> of the mapping is no longer
 * reachable from outside of the map
 * <p>
 * <b>Note:</b> This map is not synchronized. If it is to be used by multiple threads concurrently the user is
 * responsible for applying proper external synchronization!
 *
 * @author Eike Stepper
 */
public abstract class ReferenceValueMap<K, V> extends ReferenceValueMap2<K, V> implements ConcurrentMap<K, V>
{
  public ReferenceValueMap()
  {
    this(new ConcurrentHashMap<K, KeyedReference<K, V>>());
  }

  public ReferenceValueMap(ConcurrentMap<K, KeyedReference<K, V>> map)
  {
    super(map);
  }

  @Override
  public V putIfAbsent(K key, V value)
  {
    try
    {
      KeyedReference<K, V> ref = createReference(key, value, queue);
      KeyedReference<K, V> oldRef = ((ConcurrentMap<K, KeyedReference<K, V>>)map).putIfAbsent(key, ref);
      return dereference(oldRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  @Override
  public V replace(K key, V value)
  {
    try
    {
      KeyedReference<K, V> ref = createReference(key, value, queue);
      KeyedReference<K, V> oldRef = ((ConcurrentMap<K, KeyedReference<K, V>>)map).replace(key, ref);
      return dereference(oldRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue)
  {
    try
    {
      // TODO Consider a dummy KeyedReference class for oldRef
      KeyedReference<K, V> oldRef = createReference(key, oldValue, queue);
      KeyedReference<K, V> newRef = createReference(key, newValue, queue);
      return ((ConcurrentMap<K, KeyedReference<K, V>>)map).replace(key, oldRef, newRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  @Override
  public boolean remove(Object key, Object value)
  {
    // TODO Consider a dummy KeyedReference class for value
    return ((ConcurrentMap<K, KeyedReference<K, V>>)map).remove(key, value);
  }

  /**
   * @author Eike Stepper
   */
  public static class Strong<K, V> extends ReferenceValueMap<K, V>
  {
    public Strong()
    {
    }

    public Strong(ConcurrentMap<K, KeyedReference<K, V>> map)
    {
      super(map);
    }

    @Override
    protected KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue)
    {
      return new KeyedStrongReference<>(key, value);
    }

    @Override
    protected ReferenceQueue<V> createQueue()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Soft<K, V> extends ReferenceValueMap<K, V>
  {
    public Soft()
    {
    }

    public Soft(ConcurrentMap<K, KeyedReference<K, V>> map)
    {
      super(map);
    }

    @Override
    protected KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue)
    {
      return new KeyedSoftReference<>(key, value, queue);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Weak<K, V> extends ReferenceValueMap<K, V>
  {
    public Weak()
    {
    }

    public Weak(ConcurrentMap<K, KeyedReference<K, V>> map)
    {
      super(map);
    }

    @Override
    protected KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue)
    {
      return new KeyedWeakReference<>(key, value, queue);
    }
  }
}
