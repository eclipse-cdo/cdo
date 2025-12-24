/*
 * Copyright (c) 2008-2012, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class SynchronizingCorrelator<CORRELATION, RESULT> implements ICorrelator<CORRELATION, ISynchronizer<RESULT>>
{
  private final ConcurrentMap<CORRELATION, ISynchronizer<RESULT>> map = new ConcurrentHashMap<>(0);

  /**
   * @since 3.0
   */
  public ISynchronizer<RESULT> getSynchronizer(CORRELATION correlation)
  {
    return map.get(correlation);
  }

  @Override
  public boolean isCorrelated(CORRELATION correlation)
  {
    return map.containsKey(correlation);
  }

  @Override
  public ISynchronizer<RESULT> correlate(CORRELATION correlation)
  {
    ISynchronizer<RESULT> synchronizer = map.get(correlation);
    if (synchronizer == null)
    {
      synchronizer = createSynchronizer(correlation);
      map.put(correlation, synchronizer);
    }

    return synchronizer;
  }

  @Override
  public ISynchronizer<RESULT> correlateUnique(CORRELATION correlation)
  {
    ISynchronizer<RESULT> synchronizer = createSynchronizer(correlation);
    if (map.putIfAbsent(correlation, synchronizer) != null)
    {
      throw new IllegalStateException("Already correlated: " + correlation); //$NON-NLS-1$
    }

    return synchronizer;
  }

  @Override
  public ISynchronizer<RESULT> uncorrelate(CORRELATION correlation)
  {
    return map.remove(correlation);
  }

  public RESULT get(CORRELATION correlation, long timeout)
  {
    return correlate(correlation).get(timeout);
  }

  public void put(CORRELATION correlation, RESULT result)
  {
    correlate(correlation).put(result);
  }

  /**
   * @since 3.0
   */
  public boolean putIfCorrelated(CORRELATION correlation, RESULT result)
  {
    ISynchronizer<RESULT> synchronizer = getSynchronizer(correlation);
    if (synchronizer != null)
    {
      synchronizer.put(result);
      return true;
    }

    return false;
  }

  public boolean put(CORRELATION correlation, RESULT result, long timeout)
  {
    return correlate(correlation).put(result, timeout);
  }

  protected ISynchronizer<RESULT> createSynchronizer(final CORRELATION correlation)
  {
    // TODO Make top level class
    return new ISynchronizer<RESULT>()
    {
      private ISynchronizer<RESULT> delegate = new ResultSynchronizer<>();

      @Override
      public RESULT get(long timeout)
      {
        RESULT result = delegate.get(timeout);
        uncorrelate(correlation);
        return result;
      }

      @Override
      public void put(RESULT result)
      {
        delegate.put(result);
      }

      @Override
      public boolean put(RESULT result, long timeout)
      {
        return delegate.put(result, timeout);
      }
    };
  }

  @Override
  public String toString()
  {
    return "SynchronizingCorrelator" + map; //$NON-NLS-1$
  }
}
