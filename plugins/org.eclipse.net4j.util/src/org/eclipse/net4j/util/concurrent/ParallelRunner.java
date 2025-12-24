/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A {@link ArrayList list} of {@link Runnable runnables} that can be run in parallel.
 *
 * @since 3.2
 * @author Eike Stepper
 */
public class ParallelRunner extends ArrayList<Runnable>
{
  private static final long serialVersionUID = 1L;

  public ParallelRunner()
  {
  }

  public ParallelRunner(Collection<? extends Runnable> c)
  {
    super(c);
  }

  public ParallelRunner(int initialCapacity)
  {
    super(initialCapacity);
  }

  public void run(ExecutorService executorService, long timeout) throws InterruptedException
  {
    final CountDownLatch latch = new CountDownLatch(size());

    for (final Runnable runnable : this)
    {
      executorService.submit(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            runnable.run();
          }
          finally
          {
            latch.countDown();
          }
        }
      });
    }

    latch.await(timeout, TimeUnit.MILLISECONDS);
  }
}
