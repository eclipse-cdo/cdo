/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.registry;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A logical registry whose entries share one physical backing map with other registries created by the same
 * {@link Store}. Entries belonging to different registries are isolated by scope identity.
 * <p>
 * Operations that inspect all entries of a scoped registry, such as {@link #size()},
 * may require scanning the entire shared backing map.
 *
 * @param <K> the logical key type
 * @param <V> the value type
 * @since 3.31
 */
public class ScopedRegistry<K, V> extends Registry<K, V>
{
  private final Store<K, V> store;

  private final Map<K, V> map;

  private ScopedRegistry(Store<K, V> store)
  {
    this.store = store;
    map = new ScopedMap();
  }

  @Override
  protected Map<K, V> getMap()
  {
    return map;
  }

  /**
   * Owns the single physical map shared by all registries it creates.
   *
   * @param <K> the logical key type
   * @param <V> the value type
   */
  public static class Store<K, V>
  {
    private final Map<ScopedKey<K>, V> entries = new HashMap<>();

    public Store()
    {
    }

    /**
     * Creates a new registry with an independent scope in this store.
     *
     * @return a new scoped registry
     */
    public ScopedRegistry<K, V> createRegistry()
    {
      return new ScopedRegistry<>(this);
    }

    /**
     * Provides a {@link Store} that creates logical {@link ScopedRegistry} instances.
     *
     * @author Eike Stepper
     */
    public interface Provider<K, V>
    {
      public Store<K, V> getRegistryStore();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ScopedKey<K>
  {
    private final Object scope;

    private final K key;

    public ScopedKey(Object scope, K key)
    {
      this.scope = scope;
      this.key = key;
    }

    @Override
    public int hashCode()
    {
      return System.identityHashCode(scope) * 31 + Objects.hashCode(key);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (!(obj instanceof ScopedKey<?>))
      {
        return false;
      }

      ScopedKey<?> other = (ScopedKey<?>)obj;
      return scope == other.scope && Objects.equals(key, other.key);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ScopedMap extends AbstractMap<K, V>
  {
    public ScopedMap()
    {
    }

    @Override
    public V get(Object key)
    {
      synchronized (store.entries)
      {
        return store.entries.get(scopedKey(key));
      }
    }

    @Override
    public boolean containsKey(Object key)
    {
      synchronized (store.entries)
      {
        return store.entries.containsKey(scopedKey(key));
      }
    }

    @Override
    public V put(K key, V value)
    {
      synchronized (store.entries)
      {
        return store.entries.put(scopedKey(key), value);
      }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
      synchronized (store.entries)
      {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
        {
          store.entries.put(scopedKey(entry.getKey()), entry.getValue());
        }
      }
    }

    @Override
    public V remove(Object key)
    {
      synchronized (store.entries)
      {
        return store.entries.remove(scopedKey(key));
      }
    }

    @Override
    public void clear()
    {
      synchronized (store.entries)
      {
        for (ScopedKey<K> key : scopedKeys())
        {
          store.entries.remove(key);
        }
      }
    }

    @Override
    public boolean containsValue(Object value)
    {
      synchronized (store.entries)
      {
        for (ScopedKey<K> key : scopedKeys())
        {
          if (Objects.equals(store.entries.get(key), value))
          {
            return true;
          }
        }

        return false;
      }
    }

    @Override
    public int size()
    {
      synchronized (store.entries)
      {
        return scopedKeys().size();
      }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
      return new AbstractSet<>()
      {
        @Override
        public int size()
        {
          return ScopedMap.this.size();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
          return new ScopedIterator<>()
          {
            @Override
            Map.Entry<K, V> element(K key)
            {
              return new ScopedEntry(key);
            }
          };
        }

        @Override
        public boolean contains(Object obj)
        {
          if (!(obj instanceof Map.Entry<?, ?>))
          {
            return false;
          }

          Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
          synchronized (store.entries)
          {
            ScopedKey<K> scopedKey = scopedKey(entry.getKey());
            return store.entries.containsKey(scopedKey) && Objects.equals(store.entries.get(scopedKey), entry.getValue());
          }
        }

        @Override
        public boolean remove(Object obj)
        {
          if (!(obj instanceof Map.Entry<?, ?>))
          {
            return false;
          }

          Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
          synchronized (store.entries)
          {
            ScopedKey<K> scopedKey = scopedKey(entry.getKey());
            if (!store.entries.containsKey(scopedKey) || !Objects.equals(store.entries.get(scopedKey), entry.getValue()))
            {
              return false;
            }

            store.entries.remove(scopedKey);
            return true;
          }
        }
      };
    }

    @Override
    public Set<K> keySet()
    {
      return new AbstractSet<>()
      {
        @Override
        public int size()
        {
          return ScopedMap.this.size();
        }

        @Override
        public boolean contains(Object key)
        {
          return ScopedMap.this.containsKey(key);
        }

        @Override
        public boolean remove(Object key)
        {
          synchronized (store.entries)
          {
            ScopedKey<K> scopedKey = scopedKey(key);
            if (!store.entries.containsKey(scopedKey))
            {
              return false;
            }

            store.entries.remove(scopedKey);
            return true;
          }
        }

        @Override
        public Iterator<K> iterator()
        {
          return new ScopedIterator<>()
          {
            @Override
            K element(K key)
            {
              return key;
            }
          };
        }
      };
    }

    @Override
    public Collection<V> values()
    {
      return new AbstractCollection<>()
      {
        @Override
        public int size()
        {
          return ScopedMap.this.size();
        }

        @Override
        public boolean contains(Object value)
        {
          return ScopedMap.this.containsValue(value);
        }

        @Override
        public boolean remove(Object value)
        {
          synchronized (store.entries)
          {
            for (ScopedKey<K> key : scopedKeys())
            {
              if (Objects.equals(store.entries.get(key), value))
              {
                store.entries.remove(key);
                return true;
              }
            }

            return false;
          }
        }

        @Override
        public Iterator<V> iterator()
        {
          return new ScopedIterator<>()
          {
            @Override
            V element(K key)
            {
              return ScopedMap.this.get(key);
            }
          };
        }
      };
    }

    private ScopedKey<K> scopedKey(Object key)
    {
      @SuppressWarnings("unchecked")
      K castKey = (K)key;
      return new ScopedKey<>(ScopedRegistry.this, castKey);
    }

    private List<ScopedKey<K>> scopedKeys()
    {
      List<ScopedKey<K>> keys = new ArrayList<>();
      for (ScopedKey<K> key : store.entries.keySet())
      {
        if (key.scope == ScopedRegistry.this)
        {
          keys.add(key);
        }
      }

      return keys;
    }

    /**
     * @author Eike Stepper
     */
    private abstract class ScopedIterator<T> implements Iterator<T>
    {
      private final Iterator<K> keys = snapshotKeys().iterator();

      private K current;

      private boolean canRemove;

      public ScopedIterator()
      {
      }

      @Override
      public boolean hasNext()
      {
        return keys.hasNext();
      }

      @Override
      public T next()
      {
        current = keys.next();
        canRemove = true;
        return element(current);
      }

      @Override
      public void remove()
      {
        if (!canRemove)
        {
          throw new IllegalStateException();
        }

        ScopedMap.this.remove(current);
        canRemove = false;
      }

      abstract T element(K key);

      private List<K> snapshotKeys()
      {
        synchronized (store.entries)
        {
          List<K> result = new ArrayList<>();
          for (ScopedKey<K> key : scopedKeys())
          {
            result.add(key.key);
          }

          return result;
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ScopedEntry implements Map.Entry<K, V>
    {
      private final K key;

      public ScopedEntry(K key)
      {
        this.key = key;
      }

      @Override
      public K getKey()
      {
        return key;
      }

      @Override
      public V getValue()
      {
        return ScopedMap.this.get(key);
      }

      @Override
      public V setValue(V value)
      {
        return ScopedMap.this.put(key, value);
      }

      @Override
      public int hashCode()
      {
        return Objects.hashCode(key) ^ Objects.hashCode(getValue());
      }

      @Override
      public boolean equals(Object obj)
      {
        if (!(obj instanceof Map.Entry<?, ?>))
        {
          return false;
        }

        Map.Entry<?, ?> other = (Map.Entry<?, ?>)obj;
        return Objects.equals(key, other.getKey()) && Objects.equals(getValue(), other.getValue());
      }

      @Override
      public String toString()
      {
        return key + "=" + getValue(); //$NON-NLS-1$
      }
    }
  }
}
