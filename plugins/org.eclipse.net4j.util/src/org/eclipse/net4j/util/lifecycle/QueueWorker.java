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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
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
    E element = queue.poll(pollMillis, TimeUnit.MILLISECONDS);
    if (element != null)
    {
      work(context, element);
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
