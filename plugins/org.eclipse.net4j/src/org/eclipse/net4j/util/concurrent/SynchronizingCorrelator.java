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
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class SynchronizingCorrelator<CORRELATION, RESULT> implements
    Correlator<CORRELATION, Synchronizer<RESULT>>
{
  private ConcurrentMap<CORRELATION, Synchronizer<RESULT>> map = new ConcurrentHashMap(0);

  public boolean isCorrelated(CORRELATION correlation)
  {
    return map.containsKey(correlation);
  }

  public Synchronizer<RESULT> correlate(CORRELATION correlation)
  {
    Synchronizer<RESULT> synchronizer = map.get(correlation);
    if (synchronizer == null)
    {
      synchronizer = createSynchronizer(correlation);
      map.put(correlation, synchronizer);
    }

    return synchronizer;
  }

  public Synchronizer<RESULT> correlateUnique(CORRELATION correlation)
  {
    Synchronizer<RESULT> synchronizer = createSynchronizer(correlation);
    if (map.putIfAbsent(correlation, synchronizer) != null)
    {
      throw new IllegalStateException("Already correlated: " + correlation); //$NON-NLS-1$
    }

    return synchronizer;
  }

  public Synchronizer<RESULT> uncorrelate(CORRELATION correlation)
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

  public boolean put(CORRELATION correlation, RESULT result, long timeout)
  {
    return correlate(correlation).put(result, timeout);
  }

  protected Synchronizer<RESULT> createSynchronizer(final CORRELATION correlation)
  {
    return new Synchronizer<RESULT>()
    {
      private Synchronizer<RESULT> delegate = new ResultSynchronizer<RESULT>();

      public RESULT get(long timeout)
      {
        RESULT result = delegate.get(timeout);
        uncorrelate(correlation);
        return result;
      }

      public void put(RESULT result)
      {
        delegate.put(result);
      }

      public boolean put(RESULT result, long timeout)
      {
        return delegate.put(result, timeout);
      }
    };
  }

  @Override
  public String toString()
  {
    return "SynchronizingCorrelator" + map;
  }
}
