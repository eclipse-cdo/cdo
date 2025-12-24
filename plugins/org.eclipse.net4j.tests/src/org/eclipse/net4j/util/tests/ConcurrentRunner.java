/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.tests;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A class that implements a concurrent execution environment for runnables. It waits until all runnables submitted for
 * execution are terminated and returns the first throwable that occurs
 *
 * @author Andre Dietisheim
 */
public class ConcurrentRunner
{
  /**
   * Executes Runnables in concurrent manner. The first Throwable thrown by those runnables is thrown back to the
   * caller.
   *
   * @param runnables
   *          the runnables to execute
   * @param maxThreads
   *          the maximum number of threads to use
   * @param numOfExecution
   *          the number of executions per runnable
   * @throws Throwable
   *           the throwable
   */
  public static void run(Runnable[] runnables, int maxThreads, int numOfExecution) throws Throwable
  {
    ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
    Future<Throwable>[] futures = execute(numOfExecution, threadPool, runnables);
    throwOnFailure(futures, threadPool);
  }

  /**
   * Executes the runnables. The runnables are wrapped in Callables when they're submitted to the thread pool.
   *
   * @param loops
   *          the loops
   * @param threadPool
   *          the thread pool
   * @param runnables
   *          the runnables
   * @return the future<throwable>[] that allow to wait for the runnables result
   */
  @SuppressWarnings("unchecked")
  private static Future<Throwable>[] execute(int loops, ExecutorService threadPool, Runnable[] runnables)
  {
    Future<Throwable>[] futures = new Future[loops * runnables.length];
    for (int j = 0; j < loops; j++)
    {
      for (int i = 0; i < runnables.length; i++)
      {
        futures[j * runnables.length + i] = threadPool.submit(new ThrowableCatchingWrapper(runnables[i]));
      }
    }

    return futures;
  }

  /**
   * Throw a throwable if it occured while executing the runnables
   *
   * @param futures
   *          the futures
   * @param threadPool
   * @throws InterruptedException
   *           the interrupted exception
   * @throws ExecutionException
   *           the execution exception
   * @throws Throwable
   *           the throwable
   */
  private static void throwOnFailure(Future<Throwable>[] futures, ExecutorService threadPool) throws InterruptedException, ExecutionException, Throwable
  {
    for (Future<Throwable> future : futures)
    {
      Throwable e = future.get();
      if (e != null)
      {
        threadPool.shutdownNow();
        throw e;
      }
    }
  }

  /**
   * A Wrapper for runnables that catches a Throwable that occur when running the runnable
   */
  private static class ThrowableCatchingWrapper implements Callable<Throwable>
  {
    /** The runnable. */
    private Runnable runnable;

    /**
     * Instantiates a new concurrent test case.
     *
     * @param runnable
     *          the runnable
     */
    private ThrowableCatchingWrapper(Runnable runnable)
    {
      this.runnable = runnable;
    }

    /**
     * Call.
     *
     * @return the throwable
     * @throws Exception
     *           the exception
     */
    @Override
    public Throwable call() throws Exception
    {
      try
      {
        runnable.run();
        return null;
      }
      catch (Throwable t)
      {
        return t;
      }
    }
  }
}
