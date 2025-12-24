/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Eike Stepper
 */
public class RWLock extends ReentrantReadWriteLock
{
  private static final long serialVersionUID = 1L;

  private long timeoutMillis;

  public RWLock(long timeoutMillis)
  {
    this.timeoutMillis = timeoutMillis;
  }

  public RWLock(long timeoutMillis, boolean fair)
  {
    super(fair);
    this.timeoutMillis = timeoutMillis;
  }

  public <V> V read(Callable<V> callable)
  {
    return call(callable, readLock(), timeoutMillis);
  }

  public void read(Runnable runnable)
  {
    run(runnable, readLock(), timeoutMillis);
  }

  public <V> V write(Callable<V> callable)
  {
    return call(callable, writeLock(), timeoutMillis);
  }

  public void write(Runnable runnable)
  {
    run(runnable, writeLock(), timeoutMillis);
  }

  public static <V> V call(Callable<V> callable, Lock lock, long timeoutMillis)
  {
    try
    {
      boolean locked = lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
      if (locked)
      {
        try
        {
          return callable.call();
        }
        finally
        {
          lock.unlock();
        }
      }

      throw new TimeoutException("Acquisition of lock timed out after " + timeoutMillis + " millis"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void run(Runnable runnable, Lock lock, long timeoutMillis)
  {
    try
    {
      boolean locked = lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
      if (locked)
      {
        try
        {
          runnable.run();
        }
        finally
        {
          lock.unlock();
        }
      }
      else
      {
        throw new TimeoutException("Acquisition of lock timed out after " + timeoutMillis + " millis"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
