/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;

/**
 * @author Eike Stepper
 */
public abstract class Worker extends Lifecycle
{
  private boolean daemon;

  private long joinMillis;

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

  public long getJoinMillis()
  {
    return joinMillis;
  }

  public void setJoinMillis(long joinMillis)
  {
    this.joinMillis = joinMillis;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    workerThread = new WorkerThread();
    workerThread.start();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      workerThread.stopRunning();
      workerThread.interrupt();
      workerThread.join(joinMillis);
    }
    catch (Exception ignore)
    {
    }

    super.doDeactivate();
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

    public void stopRunning()
    {
      running = false;
    }

    @Override
    public void run()
    {
      WorkContext context = new WorkContext();
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

    public boolean pause()
    {
      if (pauseMillis <= 0)
      {
        return true;
      }

      try
      {
        Thread.sleep(pauseMillis);
        return true;
      }
      catch (InterruptedException ex)
      {
        return false;
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
