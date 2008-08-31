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
package org.eclipse.net4j.util.ref;

import org.eclipse.net4j.util.lifecycle.Worker;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ReferenceQueueWorker<T> extends Worker
{
  private static final int DEFAULT_POLL_MILLIS = 1000 * 60;

  private static final int DEFAULT_MAX_WORK = 100;

  private ReferenceQueue<T> queue = new ReferenceQueue<T>();

  private long pollMillis;

  private int maxWork;

  public ReferenceQueueWorker()
  {
    setPollMillis(DEFAULT_POLL_MILLIS);
    setMaxWork(DEFAULT_MAX_WORK);
  }

  public long getPollMillis()
  {
    return pollMillis;
  }

  public void setPollMillis(long pollMillis)
  {
    this.pollMillis = pollMillis;
  }

  public int getMaxWork()
  {
    return maxWork;
  }

  public void setMaxWork(int maxWork)
  {
    this.maxWork = maxWork;
  }

  protected ReferenceQueue<T> getQueue()
  {
    return queue;
  }

  @Override
  protected final void work(WorkContext context) throws Exception
  {
    for (int i = 0; i < maxWork; i++)
    {
      Reference<? extends T> reference = queue.poll();
      if (reference == null)
      {
        break;
      }

      work(reference);
    }

    context.nextWork(pollMillis);
  }

  protected abstract void work(Reference<? extends T> reference);
}
