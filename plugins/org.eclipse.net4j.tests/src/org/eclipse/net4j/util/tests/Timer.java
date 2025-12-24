/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class Timer
{
  private long sum;

  private long start;

  public Timer()
  {
    start();
  }

  public void start()
  {
    start = System.nanoTime();
  }

  public void stop()
  {
    if (start > 0)
    {
      long nanos = System.nanoTime() - start;
      sum += nanos;
      start = 0;
    }
  }

  public void done()
  {
    done(TimeUnit.NANOSECONDS);
  }

  public void done(TimeUnit timeUnit)
  {
    stop();
    long converted = timeUnit.convert(sum, TimeUnit.NANOSECONDS);
    System.out.println(converted);
  }

  public static <T> T execute(Callable<T> callable) throws Exception
  {
    return execute(TimeUnit.NANOSECONDS, callable);
  }

  public static <T> T execute(TimeUnit timeUnit, Callable<T> callable) throws Exception
  {
    Timer timer = new Timer();

    try
    {
      return callable.call();
    }
    finally
    {
      timer.done(timeUnit);
    }
  }
}
