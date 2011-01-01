/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class QueueWorker<E> extends Worker
{
  private BlockingQueue<E> queue;

  private long pollMillis;

  public QueueWorker()
  {
    setPollMillis(100);
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
    if (queue != null)
    {
      queue.clear();
    }
  }

  public boolean addWork(E element)
  {
    if (queue != null)
    {
      return queue.offer(element);
    }

    return false;
  }

  @Override
  protected final void work(WorkContext context) throws Exception
  {
    if (queue == null)
    {
      context.terminate();
    }
    else
    {
      E element = queue.poll(pollMillis, TimeUnit.MILLISECONDS);
      if (element != null)
      {
        work(context, element);
      }
    }
  }

  protected abstract void work(WorkContext context, E element);

  protected BlockingQueue<E> createQueue()
  {
    return new LinkedBlockingQueue<E>();
  }

  @Override
  protected void doActivate() throws Exception
  {
    queue = createQueue();
    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    if (queue != null)
    {
      queue.clear();
      queue = null;
    }
  }
}
