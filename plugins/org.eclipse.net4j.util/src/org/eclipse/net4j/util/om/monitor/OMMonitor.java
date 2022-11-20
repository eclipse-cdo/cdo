/*
 * Copyright (c) 2007-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMMonitor extends OMMonitorProgress
{
  /**
   * @since 2.0
   */
  public static final int THOUSAND = 1000;

  /**
   * @since 2.0
   */
  public static final double DEFAULT_TIME_FACTOR = THOUSAND;

  /**
   * @since 2.0
   */
  public boolean isCanceled();

  /**
   * @since 2.0
   */
  public void checkCanceled() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public boolean hasBegun() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public OMMonitor begin(double totalWork) throws MonitorCanceledException;

  /**
   * Same as calling <code>begin(ONE)</code>.
   *
   * @since 2.0
   */
  public OMMonitor begin() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public void worked(double work) throws MonitorCanceledException;

  /**
   * Same as calling <code>worked(ONE)</code>.
   *
   * @since 2.0
   */
  public void worked() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public OMMonitor fork(double work);

  /**
   * Same as calling <code>fork(ONE)</code>.
   *
   * @since 2.0
   */
  public OMMonitor fork();

  /**
   * @since 2.0
   */
  public Async forkAsync(double work);

  /**
   * Same as calling <code>forkAsync(ONE)</code>.
   *
   * @since 2.0
   */
  public Async forkAsync();

  /**
   * @since 2.0
   */
  public void done();

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface Async
  {
    public void stop();

    /**
    * @since 3.21
    */
    public static AsyncMonitor fork(OMMonitor monitor, double work)
    {
      Async async = monitor.forkAsync(work);
      return new AsyncMonitor()
      {
        @Override
        public void stop()
        {
          async.stop();
        }

        @Override
        public void close()
        {
          stop();
        }
      };
    }

    /**
    * Same as calling <code>fork(monitor, ONE)</code>.
    *
    * @since 3.21
    */
    public static AsyncMonitor fork(OMMonitor monitor)
    {
      return fork(monitor, ONE);
    }

    /**
     * @since 3.21
     */
    public static boolean exec(OMMonitor monitor, double work, Runnable runnable)
    {
      try (AsyncMonitor async = fork(monitor))
      {
        boolean[] finished = { false };
        boolean[] canceled = { false };
        RuntimeException[] exception = { null };

        Runnable finishingRunnable = () -> {
          try
          {
            runnable.run();
          }
          catch (Exception ex)
          {
            Exception unwrapped = WrappedException.unwrap(ex);
            if (unwrapped instanceof InterruptedException)
            {
              canceled[0] = true;
              return;
            }

            if (unwrapped instanceof RuntimeException)
            {
              exception[0] = (RuntimeException)unwrapped;
            }
            else if (ex instanceof RuntimeException)
            {
              exception[0] = (RuntimeException)ex;
            }
            else
            {
              exception[0] = WrappedException.wrap(ex);
            }
          }
          finally
          {
            finished[0] = true;
          }
        };

        Thread thread = new Thread(finishingRunnable, runnable.getClass().getSimpleName());
        thread.setDaemon(true);
        thread.start();

        while (!finished[0])
        {
          if (monitor.isCanceled())
          {
            thread.interrupt();
            break;
          }

          ConcurrencyUtil.sleep(10);
        }

        if (exception[0] != null)
        {
          throw exception[0];
        }

        return !canceled[0];
      }
    }

    /**
     * Same as calling <code>exec(monitor, ONE, runnable)</code>.
     *
     * @since 3.21
     */
    public static boolean exec(OMMonitor monitor, Runnable runnable)
    {
      return exec(monitor, ONE, runnable);
    }
  }

  /**
  * @author Eike Stepper
  * @since 3.21
  * @noextend This interface is not intended to be extended by clients.
  * @noimplement This interface is not intended to be implemented by clients.
  */
  public interface AsyncMonitor extends Async, AutoCloseable
  {
    @Override
    public void close();
  }
}
