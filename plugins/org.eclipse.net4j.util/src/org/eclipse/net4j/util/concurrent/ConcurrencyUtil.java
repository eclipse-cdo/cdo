/*
 * Copyright (c) 2007-2009, 2011-2013, 2015, 2016, 2018-2020, 2025 Eike Stepper (Loehne, Germany) and others.
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

  /**
   * Restores the interrupt status of the current thread when the directly caught exception is an
   * {@link InterruptedException}.
   * <p>
   * Interruptible operations such as {@link Thread#sleep(long)}, {@link Object#wait()},
   * {@link Thread#join()}, {@code CountDownLatch.await()}, and interruptible queue or lock
   * operations clear the current thread's interrupt status before throwing {@code InterruptedException}.
   * This helper is intended for a broader catch boundary that cannot propagate that checked
   * exception and instead translates it or otherwise terminates the current operation, when the
   * surrounding thread-ownership and cancellation policy requires the status to remain observable.
   * <p>
   * Callers must not use this as a blanket action for every broad catch, as a substitute for
   * propagating {@code InterruptedException}, or without analyzing continuing loops, retry logic,
   * worker lifecycle code, ordinary-result translations, and intentional interrupt-consumption
   * policies. Those contexts may require a different policy, because retaining the status can
   * change subsequent waits, retries, shutdown behavior, or worker termination.
   * <p>
   * Only the directly caught value is inspected. Causes are deliberately not traversed: a wrapper
   * that merely contains an {@code InterruptedException} does not establish that this catch
   * consumed the current thread's interrupt status, and restoring from a nested historical cause
   * could spuriously interrupt unrelated execution.
   * <p>
   * For a non-{@code InterruptedException} value this method does nothing. For an
   * {@code InterruptedException} it sets the current thread's interrupt status and otherwise
   * leaves the supplied exception untouched. It does not throw, wrap, log, or inspect causes.
   *
   * @param ex
   *          the exception directly caught by the caller
   * @since 3.31
   */
  public static void restoreInterrupt(Throwable ex)
  {
    if (ex instanceof InterruptedException)
    {
      Thread.currentThread().interrupt();
    }
  }
}
