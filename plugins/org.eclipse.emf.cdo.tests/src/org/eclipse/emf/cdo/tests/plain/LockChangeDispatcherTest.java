/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.internal.server.LockChangeDispatcher;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests ordered completion and gap-free numbering of per-session lock changes.
 *
 * @author Eike Stepper
 */
public class LockChangeDispatcherTest extends PlainTest
{
  public void testReadyEntriesWaitBehindEarlierPendingEntry()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> first = dispatcher.reserve("first context");
    LockChangeDispatcher.Ticket<String, String> second = dispatcher.reserve("second context");

    second.ready("second", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));

    assertFalse(second.isDone());
    assertEquals(0L, dispatcher.getLockModCount());

    first.ready("first", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));

    assertTrue(first.isDone());
    assertTrue(second.isDone());
    assertEquals(1L, first.getLockModCount());
    assertEquals(2L, second.getLockModCount());
  }

  public void testCancelledAndIrrelevantEntriesDoNotCreateGaps()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> cancelled = dispatcher.reserve("cancelled context");
    LockChangeDispatcher.Ticket<String, String> irrelevant = dispatcher.reserve("irrelevant context");
    LockChangeDispatcher.Ticket<String, String> visible = dispatcher.reserve("visible context");

    visible.ready("visible", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));
    cancelled.cancel();
    irrelevant.ready("irrelevant", (context, payload) -> new LockChangeDispatcher.Projection<>(false, null));

    assertTrue(cancelled.isDone());
    assertTrue(irrelevant.isDone());
    assertEquals(0L, cancelled.getLockModCount());
    assertEquals(0L, irrelevant.getLockModCount());
    assertEquals(1L, visible.getLockModCount());
    assertEquals(1L, dispatcher.getLockModCount());
  }

  public void testProjectionUsesCapturedContextAndCountsArePerSession()
  {
    LockChangeDispatcher<String, String> firstSession = new LockChangeDispatcher<>();
    LockChangeDispatcher<String, String> secondSession = new LockChangeDispatcher<>();
    String[] currentMode = { "FULL" };
    LockChangeDispatcher.Ticket<String, String> first = firstSession.reserve(currentMode[0]);
    currentMode[0] = "SKIPPED";
    LockChangeDispatcher.Ticket<String, String> second = secondSession.reserve("SKIPPED");

    first.ready("change", (capturedMode, payload) -> new LockChangeDispatcher.Projection<>("FULL".equals(capturedMode), payload));
    second.ready("change", (capturedMode, payload) -> new LockChangeDispatcher.Projection<>("FULL".equals(capturedMode), payload));

    assertEquals(1L, first.getLockModCount());
    assertEquals(0L, second.getLockModCount());
    assertEquals(1L, firstSession.getLockModCount());
    assertEquals(0L, secondSession.getLockModCount());
  }

  public void testOwnerRemapReservationPrecedesUnlockWhenUnlockCompletesFirst()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> ownerRemap = dispatcher.reserve("owner remap");
    LockChangeDispatcher.Ticket<String, String> unlock = dispatcher.reserve("unlock");

    unlock.ready("unlock", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));
    assertEquals(0L, dispatcher.getLockModCount());

    ownerRemap.ready("owner remap", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));

    assertEquals(1L, ownerRemap.getLockModCount());
    assertEquals(2L, unlock.getLockModCount());
  }

  public void testTicketResultRemainsSpecificWhenCarrierIsReadAfterLaterChange()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> first = dispatcher.reserve("first");
    LockChangeDispatcher.Ticket<String, String> second = dispatcher.reserve("second");

    first.ready("A", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));
    second.ready("B", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));

    assertEquals(2L, dispatcher.getLockModCount());
    assertEquals(1L, first.awaitResult().getLockModCount());
    assertEquals("A", first.awaitResult().getProjection().getValue());
    assertEquals(2L, second.awaitResult().getLockModCount());
    assertEquals("B", second.awaitResult().getProjection().getValue());
  }

  public void testFilteredProjectionIsRetainedInTicketResult()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> ticket = dispatcher.reserve("change");
    Set<String> ids = Collections.singleton("id");

    ticket.ready("payload", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload, ids));

    LockChangeDispatcher.TicketResult<String> result = ticket.awaitResult();
    assertEquals(1L, result.getLockModCount());
    assertEquals(ids, result.getProjection().getFilteredIDs());
  }

  public void testCloseCancelsOutstandingReservations()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> pending = dispatcher.reserve("pending context");
    LockChangeDispatcher.Ticket<String, String> ready = dispatcher.reserve("ready context");

    ready.ready("ready", (context, payload) -> new LockChangeDispatcher.Projection<>(true, payload));
    dispatcher.close();

    assertTrue(pending.isDone());
    assertTrue(ready.isDone());
    assertEquals(0L, pending.getLockModCount());
    assertEquals(0L, ready.getLockModCount());
    assertEquals(0L, dispatcher.getLockModCount());
  }

  public void testQuiescenceWaitsForTheActiveProjection() throws Exception
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> ticket = dispatcher.reserve("pending"); //$NON-NLS-1$
    CountDownLatch projectionStarted = new CountDownLatch(1);
    CountDownLatch allowProjectionToFinish = new CountDownLatch(1);
    Thread completingThread = new Thread(() -> ticket.ready("complete", (context, payload) -> {
      projectionStarted.countDown();
      try
      {
        allowProjectionToFinish.await();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }

      return new LockChangeDispatcher.Projection<>(true, payload);
    }));
    completingThread.start();

    assertTrue(projectionStarted.await(5, TimeUnit.SECONDS));
    assertFalse(dispatcher.isQuiescent());
    allowProjectionToFinish.countDown();
    completingThread.join(5000L);

    assertFalse(completingThread.isAlive());
    assertTrue(dispatcher.isQuiescent());
    assertEquals(1L, dispatcher.getLockModCount());
  }

  public void testCancelledReservationDoesNotPreventQuiescence()
  {
    LockChangeDispatcher<String, String> dispatcher = new LockChangeDispatcher<>();
    LockChangeDispatcher.Ticket<String, String> ticket = dispatcher.reserve("cancelled"); //$NON-NLS-1$

    ticket.cancel();

    assertTrue(ticket.isDone());
    assertTrue(dispatcher.isQuiescent());
  }
}
