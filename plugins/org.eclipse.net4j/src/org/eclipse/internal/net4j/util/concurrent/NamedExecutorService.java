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
package org.eclipse.internal.net4j.util.concurrent;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public class NamedExecutorService implements ExecutorService
{
  public static final ThreadFactory THREAD_FACTORY = new ThreadFactory()
  {
    public Thread newThread(Runnable r)
    {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    }
  };

  private String name;

  private ExecutorService delegate;

  public NamedExecutorService(String name, ExecutorService delegate)
  {
    this.name = name;
    this.delegate = delegate;
  }

  public NamedExecutorService(String name, ThreadFactory threadFactory)
  {
    this(name, Executors.newCachedThreadPool(threadFactory));
  }

  public NamedExecutorService(String name)
  {
    this(name, THREAD_FACTORY);
  }

  public NamedExecutorService()
  {
    this("DaemonThreadPool");
  }

  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
  {
    return delegate.awaitTermination(timeout, unit);
  }

  public void execute(Runnable command)
  {
    delegate.execute(command);
  }

  public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException
  {
    return delegate.invokeAll(tasks, timeout, unit);
  }

  public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks) throws InterruptedException
  {
    return delegate.invokeAll(tasks);
  }

  public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException,
      ExecutionException, TimeoutException
  {
    return delegate.invokeAny(tasks, timeout, unit);
  }

  public <T> T invokeAny(Collection<Callable<T>> tasks) throws InterruptedException, ExecutionException
  {
    return delegate.invokeAny(tasks);
  }

  public boolean isShutdown()
  {
    return delegate.isShutdown();
  }

  public boolean isTerminated()
  {
    return delegate.isTerminated();
  }

  public void shutdown()
  {
    delegate.shutdown();
  }

  public List<Runnable> shutdownNow()
  {
    return delegate.shutdownNow();
  }

  public <T> Future<T> submit(Callable<T> task)
  {
    return delegate.submit(task);
  }

  public <T> Future<T> submit(Runnable task, T result)
  {
    return delegate.submit(task, result);
  }

  public Future<?> submit(Runnable task)
  {
    return delegate.submit(task);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("ExecutorService[{0}]", name); //$NON-NLS-1$ 
  }
}