/*
 * Copyright (c) 2007-2009, 2011-2013, 2015, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public final class ConcurrencyUtil
{
  private ConcurrencyUtil()
  {
  }

  public static void sleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException ex)
    {
      return;
    }
  }

  public static void sleep(long millis, int nanos)
  {
    try
    {
      Thread.sleep(millis, nanos);
    }
    catch (InterruptedException ex)
    {
      return;
    }
  }

  /**
   * @since 3.5
   */
  public static ExecutorService getExecutorService(IManagedContainer container)
  {
    return ExecutorServiceFactory.get(container);
  }

  /**
   * @since 3.6
   */
  public static ExecutorService getExecutorService(Object object)
  {
    if (object instanceof IExecutorServiceProvider)
    {
      try
      {
        return ((IExecutorServiceProvider)object).getExecutorService();
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }

    if (object instanceof IManagedContainer)
    {
      try
      {
        return getExecutorService((IManagedContainer)object);
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }

    return null;
  }

  /**
   * @since 3.6
   */
  public static void execute(Object executor, Runnable runnable)
  {
    if (executor instanceof Executor)
    {
      ((Executor)executor).execute(runnable);
      return;
    }

    ExecutorService executorService = getExecutorService(executor);
    if (executorService == null && executor instanceof IManagedContainer)
    {
      executorService = getExecutorService((IManagedContainer)executor);
    }

    if (executorService != null)
    {
      executorService.execute(runnable);
      return;
    }

    Thread thread = new Thread(runnable, runnable.getClass().getSimpleName());
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * @since 3.8
   */
  public static void setThreadName(Thread thread, String name)
  {
    thread.setName(name);
  }

  /**
   * @since 3.9
   */
  public static void setThreadName(String name)
  {
    setThreadName(Thread.currentThread(), name);
  }

  /**
   * @since 3.9
   */
  public static void log(String msg)
  {
    IOUtil.OUT().println(Thread.currentThread().getName() + ": " + msg);
  }

  /**
   * @since 3.13
   */
  public static void checkCancelation(IProgressMonitor monitor) throws OperationCanceledException
  {
    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }
  }
}
