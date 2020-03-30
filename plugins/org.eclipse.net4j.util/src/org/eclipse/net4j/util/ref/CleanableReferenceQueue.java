/*
 * Copyright (c) 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 */
package org.eclipse.net4j.util.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public abstract class CleanableReferenceQueue<T> extends ReferenceQueue<T>
{
  public static final int ALL_WORK_PER_POLL = ReferenceQueueWorker.ALL_WORK_PER_POLL;

  public static final int DEFAULT_MAX_WORK_PER_POLL = ReferenceQueueWorker.DEFAULT_MAX_WORK_PER_POLL;

  public static final int DEFAULT_POLL_MILLIS = ReferenceQueueWorker.DEFAULT_POLL_MILLIS;

  private final AtomicBoolean cleaning = new AtomicBoolean();

  private int maxWorkPerPoll;

  private long pollMillis;

  private long lastPoll = System.currentTimeMillis();

  public CleanableReferenceQueue()
  {
    setPollMillis(DEFAULT_POLL_MILLIS);
    setMaxWorkPerPoll(DEFAULT_MAX_WORK_PER_POLL);
  }

  public final long getPollMillis()
  {
    return pollMillis;
  }

  public final void setPollMillis(long pollMillis)
  {
    this.pollMillis = pollMillis;
  }

  public final int getMaxWorkPerPoll()
  {
    return maxWorkPerPoll;
  }

  public final void setMaxWorkPerPoll(int maxWorkPerPoll)
  {
    this.maxWorkPerPoll = maxWorkPerPoll;
  }

  public final void register(T object)
  {
    clean();
    createReference(object);
  }

  public final void clean()
  {
    if (cleaning.compareAndSet(false, true))
    {
      long now = System.currentTimeMillis();
      if (now >= lastPoll + pollMillis)
      {
        int count = maxWorkPerPoll;
        if (count == ALL_WORK_PER_POLL)
        {
          count = Integer.MAX_VALUE;
        }

        for (int i = 0; i < count; i++)
        {
          Reference<? extends T> reference = poll();
          if (reference == null)
          {
            break;
          }

          cleanReference(reference);
        }

        lastPoll = now;
      }

      cleaning.set(false);
    }
  }

  protected abstract void cleanReference(Reference<? extends T> reference);

  protected abstract Reference<T> createReference(T object);
}
