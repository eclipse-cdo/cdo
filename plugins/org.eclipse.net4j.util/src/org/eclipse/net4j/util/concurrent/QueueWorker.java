/*
 * Copyright (c) 2008, 2010-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.lifecycle.LifecycleState;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class QueueWorker<E> extends Worker
{
  /**
   * @since 3.2
   */
  public static final int DEFAULT_POLL_MILLIS = 100;

  private BlockingQueue<E> queue = createQueue();

  private long pollMillis;

  public QueueWorker()
  {
    setPollMillis(DEFAULT_POLL_MILLIS);
  }

  public long getPollMillis()
  {
    return pollMillis;
  }

  public void setPollMillis(long pollMillis)
  {
    this.pollMillis = pollMillis;
  }

  /**
   * @since 3.0
   */
  public void clearQueue()
  {
    queue.clear();
  }

  public boolean addWork(E element)
  {
    if (getLifecycleState() != LifecycleState.DEACTIVATING)
    {
      return queue.offer(element);
    }

    return false;
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    doWork(context);
  }

  private void doWork(WorkContext context) throws InterruptedException
  {
    E element = pollQueue();
    if (element != null)
    {
      work(context, element);
    }
    else
    {
      noWork(context);
    }
  }

  /**
   * Factored out for better profiling.
   */
  private E pollQueue() throws InterruptedException
  {
    return queue.poll(pollMillis, TimeUnit.MILLISECONDS);
  }

  protected abstract void work(WorkContext context, E element);

  /**
   * @since 3.3
   */
  protected void noWork(WorkContext context)
  {
  }

  protected BlockingQueue<E> createQueue()
  {
    return new LinkedBlockingQueue<>();
  }

  /**
   * @since 3.1
   */
  protected boolean doRemainingWorkBeforeDeactivate()
  {
    return false;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    if (doRemainingWorkBeforeDeactivate())
    {
      WorkContext context = new WorkContext();
      while (!queue.isEmpty())
      {
        doWork(context);
      }
    }
    else
    {
      queue.clear();
    }
  }
}
