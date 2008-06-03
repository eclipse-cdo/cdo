/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public abstract class Worker extends Lifecycle
{
  public static final int DEFAULT_TIMEOUT = 2000;

  private boolean daemon;

  private long activationTimeout = DEFAULT_TIMEOUT;

  private long deactivationTimeout = DEFAULT_TIMEOUT;

  private transient CountDownLatch activationLatch;

  private transient WorkerThread workerThread;

  public Worker()
  {
  }

  public boolean isDaemon()
  {
    return daemon;
  }

  public void setDaemon(boolean daemon)
  {
    this.daemon = daemon;
  }

  public long getActivationTimeout()
  {
    return activationTimeout;
  }

  public void setActivationTimeout(long activationTimeout)
  {
    this.activationTimeout = activationTimeout;
  }

  public long getDeactivationTimeout()
  {
    return deactivationTimeout;
  }

  public void setDeactivationTimeout(long deactivationTimeout)
  {
    this.deactivationTimeout = deactivationTimeout;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    activationLatch = new CountDownLatch(1);
    String threadName = getThreadName();
    workerThread = threadName == null ? new WorkerThread() : new WorkerThread(threadName);
    workerThread.start();
    if (!activationLatch.await(activationTimeout, TimeUnit.MILLISECONDS))
    {
      try
      {
        workerThread.stopRunning();
        workerThread.interrupt();
      }
      catch (RuntimeException ignore)
      {
      }

      throw new TimeoutException("Worker thread activation timed out after " + activationTimeout + " millis");
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      workerThread.stopRunning();
      workerThread.interrupt();
      workerThread.join(deactivationTimeout);
    }
    catch (RuntimeException ignore)
    {
    }

    super.doDeactivate();
  }

  protected String getThreadName()
  {
    return null;
  }

  protected abstract void work(WorkContext context) throws Exception;

  /**
   * @author Eike Stepper
   */
  private final class WorkerThread extends Thread
  {
    private boolean running = true;

    public WorkerThread()
    {
      setDaemon(daemon);
    }

    public WorkerThread(String threadName)
    {
      super(threadName);
      setDaemon(daemon);
    }

    public void stopRunning()
    {
      running = false;
    }

    @Override
    public void run()
    {
      WorkContext context = new WorkContext();
      activationLatch.countDown();
      while (running && !isInterrupted())
      {
        try
        {
          context.increaseCount();
          work(context);
        }
        catch (NextWork nextWork)
        {
          nextWork.pause();
        }
        catch (Terminate terminate)
        {
          break;
        }
        catch (InterruptedException ex)
        {
          break;
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          break;
        }
      }

      deactivate();
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class WorkContext
  {
    private long count;

    public WorkContext()
    {
    }

    public long getCount()
    {
      return count;
    }

    public void nextWork()
    {
      throw new NextWork();
    }

    public void nextWork(long pauseMillis)
    {
      throw new NextWork(pauseMillis);
    }

    public void terminate()
    {
      throw new Terminate();
    }

    private void increaseCount()
    {
      ++count;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class NextWork extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    private long pauseMillis;

    public NextWork()
    {
    }

    public NextWork(long pauseMillis)
    {
      this.pauseMillis = pauseMillis;
    }

    public void pause()
    {
      if (pauseMillis > 0)
      {
        ConcurrencyUtil.sleep(pauseMillis);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Terminate extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public Terminate()
    {
    }
  }
}
