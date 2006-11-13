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
package org.eclipse.net4j.util.registry;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractRegistry<K, V> implements IRegistry<K, V>
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_REGISTRY,
      AbstractRegistry.class);

  /**
   * Don't initialize lazily to circumvent synchronization!
   */
  private Queue<IRegistryListener> listeners = new ConcurrentLinkedQueue();

  private Transaction transaction;

  protected AbstractRegistry()
  {
  }

  public void addRegistryListener(IRegistryListener<K, V> listener)
  {
    listeners.add(listener);
  }

  public void removeRegistryListener(IRegistryListener<K, V> listener)
  {
    listeners.remove(listener);
  }

  public boolean isEmpty()
  {
    return keySet().isEmpty();
  }

  public int size()
  {
    return keySet().size();
  }

  public Set<Entry<K, V>> entrySet()
  {
    return getMap().entrySet();
  }

  public Set<K> keySet()
  {
    return getMap().keySet();
  }

  public Collection<V> values()
  {
    return getMap().values();
  }

  public boolean containsKey(Object key)
  {
    return keySet().contains(key);
  }

  public boolean containsValue(Object value)
  {
    return values().contains(value);
  }

  public V get(Object key)
  {
    return getMap().get(key);
  }

  /**
   * Requires {@link #commit()} to be called later.
   */
  public synchronized V put(K key, V value)
  {
    return getTransaction().put(key, value);
  }

  /**
   * Requires {@link #commit()} to be called later.
   */
  public synchronized void putAll(Map<? extends K, ? extends V> t)
  {
    if (!t.isEmpty())
    {
      Transaction transaction = getTransaction();
      Iterator<? extends Entry<? extends K, ? extends V>> i = t.entrySet().iterator();
      while (i.hasNext())
      {
        Entry<? extends K, ? extends V> e = i.next();
        transaction.put(e.getKey(), e.getValue());
      }
    }
  }

  /**
   * Requires {@link #commit()} to be called later.
   */
  public synchronized V remove(Object key)
  {
    return getTransaction().remove(key);
  }

  /**
   * Requires {@link #commit()} to be called later.
   */
  public synchronized void clear()
  {
    if (isEmpty())
    {
      Transaction transaction = getTransaction();
      Set<K> keys = keySet();
      for (K key : keys)
      {
        transaction.remove(key);
      }
    }
  }

  public synchronized void commit()
  {
    if (transaction != null)
    {
      if (!transaction.isOwned())
      {
        Net4j.LOG.warn("Committing thread is not owner of transaction: " + Thread.currentThread());
      }

      transaction.commit();
      transaction = null;
      notifyAll();
    }
  }

  public synchronized void dispose()
  {
    listeners.clear();
  }

  protected V register(K key, V value)
  {
    return getMap().put(key, value);
  }

  protected V deregister(Object key)
  {
    return getMap().remove(key);
  }

  protected Transaction getTransaction()
  {
    for (;;)
    {
      if (transaction == null)
      {
        transaction = new Transaction();
        return transaction;
      }

      if (transaction.isOwned())
      {
        transaction.increaseNesting();
        return transaction;
      }

      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        return null;
      }
    }
  }

  protected void fireRegistryEvent(IRegistryEvent<K, V> event)
  {
    if (TRACER.isEnabled())
    {
      for (IRegistryDelta<K, V> delta : event.getDeltas())
      {
        K key = delta.getKey();
        V value = delta.getValue();
        int kind = delta.getKind();
        TRACER.trace("Registry delta " + key + " = " + value + " (" + kind + ")");
      }
    }

    for (IRegistryListener listener : listeners)
    {
      try
      {
        listener.notifyRegistryEvent(event);
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
      }
    }
  }

  protected abstract Map<K, V> getMap();

  /**
   * @author Eike Stepper
   */
  protected class Transaction
  {
    private int nesting = 1;

    private List<IRegistryDelta<K, V>> deltas = new ArrayList();

    private Thread owner;

    public Transaction()
    {
      owner = Thread.currentThread();
    }

    public boolean isOwned()
    {
      return owner == Thread.currentThread();
    }

    public void increaseNesting()
    {
      ++nesting;
    }

    public V put(K key, V value)
    {
      V oldValue = register(key, value);
      if (oldValue != null)
      {
        rememberDeregistering(key, oldValue);
      }

      rememberRegistered(key, value);
      return oldValue;
    }

    public V remove(Object key)
    {
      V value = deregister(key);
      if (value != null)
      {
        rememberDeregistering(key, value);
      }

      return value;
    }

    public void commit()
    {
      if (--nesting == 0)
      {
        if (!deltas.isEmpty())
        {
          fireRegistryEvent(new RegistryEvent<K, V>(AbstractRegistry.this, deltas));
        }

        deltas = null;
      }
    }

    protected void rememberRegistered(K key, V value)
    {
      deltas.add(new RegistryDelta(key, value, IRegistryDelta.REGISTERED));
    }

    protected void rememberDeregistering(Object key, V value)
    {
      deltas.add(new RegistryDelta(key, value, IRegistryDelta.DEREGISTERING));
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class RegistryEvent<K, V> implements IRegistryEvent<K, V>
  {
    private IRegistry<K, V> registry;

    private List<IRegistryDelta<K, V>> deltas;

    public RegistryEvent(IRegistry<K, V> registry, List<IRegistryDelta<K, V>> deltas)
    {
      this.registry = registry;
      this.deltas = deltas;
    }

    public IRegistry<K, V> getRegistry()
    {
      return registry;
    }

    public IRegistryDelta<K, V>[] getDeltas()
    {
      return deltas.toArray(new IRegistryDelta[deltas.size()]);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class RegistryDelta<K, V> implements IRegistryDelta<K, V>
  {
    private K key;

    private V value;

    private int kind;

    public RegistryDelta(K key, V value, int kind)
    {
      this.key = key;
      this.value = value;
      this.kind = kind;
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

    public int getKind()
    {
      return kind;
    }
  }
}
