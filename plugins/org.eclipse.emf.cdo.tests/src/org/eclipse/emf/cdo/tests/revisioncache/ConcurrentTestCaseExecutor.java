/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Andre Dietisheim
 */
public class ConcurrentTestCaseExecutor
{

  public static void execute(Runnable[] runnables, int maxThreads, int loops) throws Throwable
  {
    ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
    Future<Throwable>[] futures = executeTestCases(loops, threadPool, runnables);
    throwOnFailure(futures);
  }

  @SuppressWarnings("unchecked")
  private static Future<Throwable>[] executeTestCases(int loops, ExecutorService threadPool, Runnable[] runnables)
  {
    Future<Throwable>[] futures = new Future[loops * runnables.length];
    for (int j = 0; j < loops; j++)
    {
      for (int i = 0; i < runnables.length; i++)
      {
        futures[j * runnables.length + i] = threadPool.submit(new ConcurrentTestCase(runnables[i]));
      }
    }
    return futures;
  }

  private static void throwOnFailure(Future<Throwable>[] futures) throws InterruptedException, ExecutionException,
      Throwable
  {
    for (Future<Throwable> future : futures)
    {
      Throwable e = future.get();
      if (e != null)
      {
        throw e;
      }
    }
  }

  private static class ConcurrentTestCase implements Callable<Throwable>
  {
    private Runnable runnable;

    private ConcurrentTestCase(Runnable runnable)
    {
      this.runnable = runnable;
    }

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
