/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
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
  public void sendCommitNotification(CommitNotificationInfo info)
  {
    synchronized (lock)
    {
      if (commitNotificationDelay != 0)
      {
        delayLatch.countDown();
        Thread.yield();
        ConcurrencyUtil.sleep(commitNotificationDelay);
      }
    }

    super.sendCommitNotification(info);
  }
}
