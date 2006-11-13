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

  private Object transactionLock = new Object();

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

  public void clear()
  {
    Set<K> keys = keySet();
    for (K key : keys)
    {
      deregister(key);
    }
  }

  public boolean containsKey(Object key)
  {
    return keySet().contains(key);
  }

  public boolean containsValue(Object value)
  {
    return values().contains(value);
  }

  public boolean isEmpty()
  {
    return keySet().isEmpty();
  }

  public V put(K key, V value)
  {
    return getTransaction().put(key, value);
  }

  public void putAll(Map<? extends K, ? extends V> t)
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

  public V remove(Object key)
  {
    return getTransaction().remove(key);
  }

  public int size()
  {
    return keySet().size();
  }

  public void commit()
  {
    synchronized (transactionLock)
    {
      if (transaction != null)
      {
        transaction.commit();
      }
    }
  }

  protected Transaction getTransaction()
  {
    synchronized (transactionLock)
    {
      if (transaction != null)
      {
        transaction = new Transaction();
      }

      transaction.increaseNesting();
    }

    return transaction;
  }

  public void dispose()
  {
    listeners.clear();
  }

  protected void fireRegistryEvent(IRegistryEvent<K, V> event)
  {
    for (IRegistryListener listener : listeners)
    {
      try
      {
        listener.notifyRegistryEvent(event);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
  }

  protected abstract V register(K key, V value);

  protected abstract V deregister(Object key);

  /**
   * @author Eike Stepper
   */
  protected class Transaction
  {
    private int nesting;

    private List<IRegistryDelta<K, V>> deltas = new ArrayList();

    public Transaction()
    {
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
        transaction = null;
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
}
