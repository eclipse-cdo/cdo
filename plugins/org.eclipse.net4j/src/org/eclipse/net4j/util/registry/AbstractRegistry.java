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
import org.eclipse.net4j.util.registry.IRegistryDelta.Kind;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

  private boolean autoCommit;

  private Transaction transaction;

  protected AbstractRegistry(boolean autoCommit)
  {
    this.autoCommit = autoCommit;
  }

  protected AbstractRegistry()
  {
    this(true);
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
   * Requires {@link #commit()} to be called later if not
   * {@link #isAutoCommit()}.
   */
  public synchronized V put(K key, V value)
  {
    V result = register(key, value);
    autoCommit();
    return result;
  }

  /**
   * Requires {@link #commit()} to be called later if not
   * {@link #isAutoCommit()}.
   */
  public synchronized void putAll(Map<? extends K, ? extends V> t)
  {
    if (!t.isEmpty())
    {
      Iterator<? extends Entry<? extends K, ? extends V>> i = t.entrySet().iterator();
      while (i.hasNext())
      {
        Entry<? extends K, ? extends V> e = i.next();
        register(e.getKey(), e.getValue());
      }

      autoCommit();
    }
  }

  /**
   * Requires {@link #commit()} to be called later if not
   * {@link #isAutoCommit()}.
   */
  public synchronized V remove(Object key)
  {
    V result = deregister(key);
    autoCommit();
    return result;
  }

  /**
   * Requires {@link #commit()} to be called later if not
   * {@link #isAutoCommit()}.
   */
  public synchronized void clear()
  {
    if (isEmpty())
    {
      Set<K> keys = keySet();
      for (K key : keys)
      {
        deregister(key);
      }

      autoCommit();
    }
  }

  public boolean isAutoCommit()
  {
    return autoCommit;
  }

  public void setAutoCommit(boolean autoCommit)
  {
    this.autoCommit = autoCommit;
  }

  public synchronized void commit(boolean notifications)
  {
    if (transaction != null)
    {
      if (!transaction.isOwned())
      {
        Net4j.LOG.warn("Committing thread is not owner of transaction: " + Thread.currentThread());
      }

      transaction.commit(notifications);
      transaction = null;
      notifyAll();
    }
  }

  public void commit()
  {
    commit(true);
  }

  public synchronized void dispose()
  {
    listeners.clear();
  }

  protected V register(K key, V value)
  {
    Transaction transaction = getTransaction();
    V oldValue = getMap().put(key, value);
    if (oldValue != null)
    {
      transaction.rememberDeregistered(key, oldValue);
    }

    transaction.rememberRegistered(key, value);
    return oldValue;
  }

  protected V deregister(Object key)
  {
    V value = getMap().remove(key);
    if (value != null)
    {
      getTransaction().rememberDeregistered(key, value);
    }

    return value;
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

  protected void fireRegistryEvent(IRegistryDelta<K, V> delta)
  {
    fireRegistryEvent(Collections.singletonList(delta));
  }

  protected void fireRegistryEvent(List<IRegistryDelta<K, V>> deltas)
  {
    fireRegistryEvent(new RegistryEvent<K, V>(AbstractRegistry.this, deltas));
  }

  protected void fireRegistryEvent(IRegistryEvent<K, V> event)
  {
    if (TRACER.isEnabled())
    {
      for (IRegistryDelta<K, V> delta : event.getDeltas())
      {
        K key = delta.getKey();
        V value = delta.getValue();
        Kind kind = delta.getKind();
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

  protected void autoCommit()
  {
    if (autoCommit)
    {
      commit();
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

    public void commit(boolean notifications)
    {
      if (--nesting == 0)
      {
        if (notifications && !deltas.isEmpty())
        {
          fireRegistryEvent(deltas);
        }

        deltas = null;
      }
    }

    public void rememberRegistered(K key, V value)
    {
      deltas.add(new RegistryDelta(key, value, Kind.REGISTERED));
    }

    public void rememberDeregistered(Object key, V value)
    {
      deltas.add(new RegistryDelta(key, value, Kind.DEREGISTERED));
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

    private Kind kind;

    public RegistryDelta(K key, V value, Kind kind)
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

    public Kind getKind()
    {
      return kind;
    }
  }
}
