/*
 * Copyright (c) 2011-2013, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.internal.server.SessionManager;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Caspar De Groot
 */
public class TestSessionManager extends SessionManager
{
  /**
   * Prevents other threads from changing/resetting the commitNotificationDelay while we are just about to execute the
   * delay.
   */
  private Object lock = new Object();

  private long commitNotificationDelay;

  /**
   * Allows tests to wait until the delay is about to commence
   */
  private CountDownLatch delayLatch;

  private CountDownLatch commitNotificationEntered;

  private CountDownLatch commitNotificationRelease;

  /**
   * Arranges for the next commit notification to stop immediately before it is sent. Tests can wait for the notification
   * to reach that point and then release it without depending on elapsed time.
   */
  public void blockCommitNotifications()
  {
    synchronized (lock)
    {
      commitNotificationEntered = new CountDownLatch(1);
      commitNotificationRelease = new CountDownLatch(1);
    }
  }

  /**
   * Returns the latch that signals that a commit notification is waiting to be sent after a call to
   * {@link #blockCommitNotifications()}.
   */
  public CountDownLatch getCommitNotificationEntered()
  {
    return commitNotificationEntered;
  }

  /**
   * Allows a commit notification blocked by {@link #blockCommitNotifications()} to be sent.
   */
  public void releaseCommitNotifications()
  {
    CountDownLatch release = commitNotificationRelease;
    if (release != null)
    {
      release.countDown();
    }
  }

  public void setCommitNotificationDelay(long millis)
  {
    synchronized (lock)
    {
      commitNotificationDelay = millis;
      if (commitNotificationDelay > 0)
      {
        delayLatch = new CountDownLatch(1);
      }
    }
  }

  public CountDownLatch getDelayLatch()
  {
    return delayLatch;
  }

  @Override
  public void sendCommitNotification(CommitNotificationInfo info)
  {
    CountDownLatch entered;
    CountDownLatch release;
    long delay;
    synchronized (lock)
    {
      entered = commitNotificationEntered;
      release = commitNotificationRelease;
      delay = commitNotificationDelay;

      if (delay != 0)
      {
        delayLatch.countDown();
      }
    }

    if (delay != 0)
    {
      ConcurrencyUtil.sleep(delay);
    }

    if (entered != null)
    {
      entered.countDown();

      try
      {
        if (!release.await(10, TimeUnit.SECONDS))
        {
          throw new IllegalStateException("Commit notification was not released");
        }
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }
    }

    super.sendCommitNotification(info);
  }
}
