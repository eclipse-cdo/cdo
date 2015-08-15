/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.internal.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A test for {@link ThreadPool}.
 *
 * @author Eike Stepper
 */
public class ThreadPoolTest extends AbstractOMTest
{
  public void testThreadPool() throws Exception
  {
    final ThreadPool pool = ThreadPool.create("test", 100, 200, 60);

    try
    {
      final int tasks = pool.getMaximumPoolSize() + 100;
      final CountDownLatch latch = new CountDownLatch(tasks);

      for (int i = 0; i < tasks; i++)
      {
        final int n = i;
        msg("scheduling " + n);
        pool.submit(new Runnable()
        {
          public void run()
          {
            msg("started " + n + " (wc=" + pool.getPoolSize() + ")");
            ConcurrencyUtil.sleep(1000);
            latch.countDown();
          }
        });
      }

      latch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      msg("FINISHED");
    }
    finally
    {
      pool.shutdownNow();
    }
  }
}
