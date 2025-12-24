/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.WrappedException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public final class ResultSynchronizer<RESULT> implements ISynchronizer<RESULT>
{
  private RESULT result;

  private Object consumerLock = new Object();

  private CountDownLatch producerLatch = new CountDownLatch(1);

  public ResultSynchronizer()
  {
  }

  @Override
  public RESULT get(long timeout)
  {
    try
    {
      final long stop = System.currentTimeMillis() + timeout;
      synchronized (consumerLock)
      {
        while (result == null)
        {
          try
          {
            final long remaining = stop - System.currentTimeMillis();
            if (remaining <= 0)
            {
              return null;
            }

            consumerLock.wait(Math.min(remaining, 100L));
          }
          catch (InterruptedException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        return result;
      }
    }
    finally
    {
      producerLatch.countDown();
    }
  }

  @Override
  public void put(RESULT result)
  {
    synchronized (consumerLock)
    {
      this.result = result;
      consumerLock.notifyAll();
    }
  }

  @Override
  public boolean put(RESULT result, long timeout)
  {
    synchronized (consumerLock)
    {
      this.result = result;
      consumerLock.notifyAll();
    }

    try
    {
      if (!producerLatch.await(timeout, TimeUnit.MILLISECONDS))
      {
        return false;
      }
    }
    catch (InterruptedException ex)
    {
      return false;
    }

    return true;
  }
}
