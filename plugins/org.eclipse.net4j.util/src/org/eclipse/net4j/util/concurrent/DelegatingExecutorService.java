/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class DelegatingExecutorService implements ExecutorService
{
  private final ExecutorService delegate;

  public DelegatingExecutorService(ExecutorService delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public void execute(Runnable command)
  {
    delegate.execute(command);
  }

  @Override
  public void shutdown()
  {
    // Do nothing.
  }

  @Override
  public List<Runnable> shutdownNow()
  {
    return Collections.emptyList();
  }

  @Override
  public boolean isShutdown()
  {
    return delegate.isShutdown();
  }

  @Override
  public boolean isTerminated()
  {
    return delegate.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
  {
    return delegate.awaitTermination(timeout, unit);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task)
  {
    return delegate.submit(task);
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result)
  {
    return delegate.submit(task, result);
  }

  @Override
  public Future<?> submit(Runnable task)
  {
    return delegate.submit(task);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
  {
    return delegate.invokeAll(tasks);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException
  {
    return delegate.invokeAll(tasks, timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
  {
    return delegate.invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
  {
    return delegate.invokeAny(tasks, timeout, unit);
  }
}
