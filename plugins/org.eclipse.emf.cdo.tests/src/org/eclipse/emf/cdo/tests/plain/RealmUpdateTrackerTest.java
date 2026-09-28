/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.server.internal.security.RealmUpdateTracker;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Deterministic concurrency tests for pending Realm update timestamps.
 *
 * @author Eike Stepper
 */
public class RealmUpdateTrackerTest extends PlainTest
{
  public void testConcurrentWaitersBothObservePendingUpdate() throws Exception
  {
    RealmUpdateTracker tracker = new RealmUpdateTracker();
    tracker.remember(10);
    CountDownLatch enteredWait = new CountDownLatch(2);
    CountDownLatch release = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(2);
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread first = waiter(tracker, enteredWait, release, completed, failure);
    Thread second = waiter(tracker, enteredWait, release, completed, failure);
    first.start();
    second.start();

    enteredWait.await();
    assertEquals(2L, completed.getCount());
    release.countDown();
    completed.await();

    assertNull(failure.get());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, tracker.getPendingUpdate());
  }

  public void testNewerTimestampSurvivesOlderWaiterAcknowledgement() throws Exception
  {
    RealmUpdateTracker tracker = new RealmUpdateTracker();
    tracker.remember(10);
    CountDownLatch waitingForT1 = new CountDownLatch(1);
    CountDownLatch releaseT1 = new CountDownLatch(1);
    CountDownLatch waitingForT2 = new CountDownLatch(1);
    CountDownLatch releaseT2 = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread waiter = new Thread(() -> {
      try
      {
        tracker.waitForUpdate(CDOBranchPoint.UNSPECIFIED_DATE, timeStamp -> {
          if (timeStamp == 10)
          {
            waitingForT1.countDown();
            awaitLatch(releaseT1);
            return true;
          }

          if (timeStamp == 20)
          {
            waitingForT2.countDown();
            awaitLatch(releaseT2);
            return true;
          }

          throw new AssertionError("Unexpected Realm update timestamp " + timeStamp);
        });
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        completed.countDown();
      }
    });
    waiter.start();

    waitingForT1.await();
    tracker.remember(20);
    releaseT1.countDown();
    waitingForT2.await();
    assertEquals(20L, tracker.getPendingUpdate());
    releaseT2.countDown();
    completed.await();

    assertNull(failure.get());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, tracker.getPendingUpdate());
  }

  public void testContextAtOrAfterUpdateDoesNotWait()
  {
    RealmUpdateTracker tracker = new RealmUpdateTracker();
    tracker.remember(10);

    tracker.waitForUpdate(10, timeStamp -> {
      fail("A current-enough context must not wait");
      return false;
    });

    assertEquals(10L, tracker.getPendingUpdate());
  }

  private static Thread waiter(RealmUpdateTracker tracker, CountDownLatch enteredWait, CountDownLatch release, CountDownLatch completed,
      AtomicReference<Throwable> failure)
  {
    return new Thread(() -> {
      try
      {
        tracker.waitForUpdate(CDOBranchPoint.UNSPECIFIED_DATE, timeStamp -> {
          assertEquals(10L, timeStamp);
          enteredWait.countDown();
          awaitLatch(release);
          return true;
        });
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        completed.countDown();
      }
    });
  }

  private static void awaitLatch(CountDownLatch latch)
  {
    try
    {
      latch.await();
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }
}
