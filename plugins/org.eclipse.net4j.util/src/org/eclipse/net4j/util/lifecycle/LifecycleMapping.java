/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.IListener;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * A canonical mapping from keys that are potentially {@link ILifecycle} to arbitrary values.
 * A mapping is automatically removed if its key is just {@link WeakReference weakly reachable},
 * or if its key is an {@link ILifecycle} and is {@link ILifecycle#deactivate() deactivated}.
 *
 * @author Eike Stepper
 * @since 3.27
 */
public class LifecycleMapping<K, V>
{
  private final Map<K, V> map = new WeakHashMap<>();

  private final IListener lifecycleListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      @SuppressWarnings("unchecked")
      K key = (K)lifecycle;

      removeMapping(key);
    }
  };

  public LifecycleMapping()
  {
  }

  public V getMapping(K lifecycle)
  {
    synchronized (map)
    {
      return map.get(lifecycle);
    }
  }

  public V getOrAddMapping(K lifecycle, Supplier<V> valueSupplier)
  {
    synchronized (map)
    {
      return map.computeIfAbsent(lifecycle, k -> {
        if (lifecycle instanceof ILifecycle)
        {
          ((ILifecycle)lifecycle).addListener(lifecycleListener);
        }

        return valueSupplier.get();
      });
    }
  }

  public boolean addMapping(K lifecycle, V value)
  {
    synchronized (map)
    {
      if (map.putIfAbsent(lifecycle, value) == null)
      {
        if (lifecycle instanceof ILifecycle)
        {
          ((ILifecycle)lifecycle).addListener(lifecycleListener);
        }

        return true;
      }

      return false;
    }
  }

  public V removeMapping(K lifecycle)
  {
    synchronized (map)
    {
      V value = map.remove(lifecycle);
      if (value != null)
      {
        if (lifecycle instanceof ILifecycle)
        {
          ((ILifecycle)lifecycle).removeListener(lifecycleListener);
        }
      }

      return value;
    }
  }
}
