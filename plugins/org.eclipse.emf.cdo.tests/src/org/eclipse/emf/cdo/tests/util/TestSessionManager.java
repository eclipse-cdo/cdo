/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.util.concurrent.CountDownLatch;

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
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    synchronized (lock)
    {
      if (commitNotificationDelay != 0)
      {
        delayLatch.countDown();
        ConcurrencyUtil.sleep(commitNotificationDelay);
      }
    }

    super.sendCommitNotification(sender, commitInfo);
  }
}
