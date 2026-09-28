/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Coordinates authorizations with committed Realm updates.
 *
 * @author Eike Stepper
 */
public final class RealmUpdateTracker
{
  private final AtomicLong pendingUpdate = new AtomicLong();

  public RealmUpdateTracker()
  {
  }

  public void remember(long timeStamp)
  {
    pendingUpdate.accumulateAndGet(timeStamp, Math::max);
  }

  public long getPendingUpdate()
  {
    return pendingUpdate.get();
  }

  public void waitForUpdate(long contextTime, UpdateWaiter waiter)
  {
    for (;;)
    {
      long updateTime = pendingUpdate.get();
      if (updateTime == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        return;
      }

      if (contextTime != CDOBranchPoint.UNSPECIFIED_DATE && contextTime >= updateTime)
      {
        return;
      }

      if (!waiter.waitForUpdate(updateTime))
      {
        throw new TimeoutRuntimeException();
      }

      pendingUpdate.compareAndSet(updateTime, CDOBranchPoint.UNSPECIFIED_DATE);
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface UpdateWaiter
  {
    public boolean waitForUpdate(long timeStamp);
  }
}
