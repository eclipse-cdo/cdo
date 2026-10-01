/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018, 2020-2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.server.LockChangeDispatcher;
import org.eclipse.emf.cdo.internal.server.LockingManager;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractLockingTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.concurrent.Access;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.tests.TestListener2;

import org.eclipse.emf.spi.cdo.CDOLockStateCache;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.ChangeLockAreaResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockStateSnapshotResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionInvalidationEvent;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * @author Caspar De Groot
 */
public class LockingNotificationsTest extends AbstractLockingTest
{
  private volatile CountDownLatch lockNotificationSubmitted;

  @Override
  protected void afterLockNotificationSubmitted(long lockModCount)
  {
    CountDownLatch latch = lockNotificationSubmitted;
    if (lockModCount > 0L && latch != null)
    {
      latch.countDown();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testRecursiveRemoteLockPreservesPartialUnrelatedList() throws Exception
  {
    CDOSession session1 = openSession();
    session1.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setLockNotificationEnabled(true);

    CDOResource resource1 = transaction1.createResource(getResourcePath("r1"));
    Category category1 = getModel1Factory().createCategory();
    resource1.getContents().add(category1);
    for (int i = 0; i < 8; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("product" + i);
      resource1.getContents().add(product);
      category1.getTopProducts().add(product);
    }
    transaction1.commit();

    session1.close();
    session1 = openSession();
    session1.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    transaction1 = session1.openTransaction();
    transaction1.options().setLockNotificationEnabled(true);
    category1 = (Category)transaction1.getResource(getResourcePath("r1")).getContents().get(0);

    CDOObject cdoCategory1 = CDOUtil.getCDOObject(category1);
    category1.getTopProducts().get(0);
    InternalCDORevision revision = (InternalCDORevision)cdoCategory1.cdoRevision();
    CDOList topProducts = revision.getListOrNull(getModel1Package().getCategory_TopProducts());
    assertFalse(topProducts.isFullyLoaded());

    TestListener2 listener = new TestListener2(CDOViewLocksChangedEvent.class);
    transaction1.addListener(listener);

    CDOSession session2 = openSession();
    CDOView view2 = openViewWithLockNotifications(session2, transaction1.getBranch());
    CDOObject cdoCategory2 = CDOUtil.getCDOObject(view2.getObject(cdoCategory1.cdoID()));
    view2.lockObjects(Collections.singleton(cdoCategory2), LockType.WRITE, DEFAULT_TIMEOUT, true);

    listener.waitFor(1);
    assertEquals(true, cdoCategory1.cdoWriteLock().isLockedByOthers());
    assertFalse(topProducts.isFullyLoaded());

    view2.unlockObjects(Collections.singleton(cdoCategory2), LockType.WRITE, true);
    listener.waitFor(2);
    assertFalse(topProducts.isFullyLoaded());

    session2.close();
    session1.close();
  }

  public void testSameBranchDifferentSession_WithoutAutoRelease() throws Exception
  {
    sameBranchDifferentSession(false);
  }

  public void testSameBranchDifferentSession_WithAutoRelease() throws Exception
  {
    sameBranchDifferentSession(true);
  }

  public void testSameBranchSameSession_WithoutAutoRelease() throws Exception
  {
    sameBranchSameSession(false);
  }

  public void testSameBranchSameSession_WithAutoRelease() throws Exception
  {
    sameBranchSameSession(true);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchDifferentSession() throws Exception
  {
    differentBranchDifferentSession(false);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchDifferentSession_WithAutoRelease() throws Exception
  {
    differentBranchDifferentSession(true);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchSameSession() throws Exception
  {
    differentBranchSameSession(false);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchSameSession_WithAutoRelease() throws Exception
  {
    differentBranchSameSession(true);
  }

  public void testEnableDisableNotifications() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();
    CDOView controlView = session2.openView();
    withoutAutoRelease(session1, controlView, false);

    controlView.options().setLockNotificationEnabled(true);
    withoutAutoRelease(session1, controlView, true);

    controlView.options().setLockNotificationEnabled(false);
    withoutAutoRelease(session1, controlView, false);

    session1.close();
    session2.close();
  }

  public void testEnableDisableNotificationsSameSession() throws Exception
  {
    CDOSession session1 = openSession();
    CDOView controlView = session1.openView();
    withoutAutoRelease(session1, controlView, false);

    controlView.options().setLockNotificationEnabled(true);
    withoutAutoRelease(session1, controlView, true);

    controlView.options().setLockNotificationEnabled(false);
    withoutAutoRelease(session1, controlView, false);

    session1.close();
  }

  public void testLockUnlockAndDurableAreaResponsesCarryTheirExactCounts() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("lockModCount"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDORevisionKey revisionKey = cdoCompany.cdoRevision();
    CDOSessionProtocol protocol = ((InternalCDOSession)session).getSessionProtocol();
    int viewID = ((CDOViewImpl)transaction).getViewID();

    LockObjectsResult lockResult = protocol.lockObjects2(Collections.singletonList(revisionKey), viewID, transaction.getBranch(), LockType.WRITE, false,
        DEFAULT_TIMEOUT);
    assertTrue(lockResult.isSuccessful());
    assertTrue(lockResult.getLockModCount() > 0L);

    LockObjectsResult repeatedLockResult = protocol.lockObjects2(Collections.singletonList(revisionKey), viewID, transaction.getBranch(), LockType.WRITE, false,
        DEFAULT_TIMEOUT);
    assertTrue(repeatedLockResult.isSuccessful());
    assertEquals(0L, repeatedLockResult.getLockModCount());

    UnlockObjectsResult partialUnlockResult = protocol.unlockObjects2(transaction, Collections.singletonList(cdoCompany.cdoID()), LockType.WRITE, false);
    assertEquals(0L, partialUnlockResult.getLockModCount());

    UnlockObjectsResult unlockResult = protocol.unlockObjects2(transaction, Collections.singletonList(cdoCompany.cdoID()), LockType.WRITE, false);
    assertEquals(lockResult.getLockModCount() + 1L, unlockResult.getLockModCount());

    LockObjectsResult durableLockResult = protocol.lockObjects2(Collections.singletonList(revisionKey), viewID, transaction.getBranch(), LockType.WRITE, false,
        DEFAULT_TIMEOUT);
    assertTrue(durableLockResult.getLockModCount() > unlockResult.getLockModCount());

    ChangeLockAreaResult enableResult = protocol.changeLockArea2(transaction, true);
    assertNotNull(enableResult.getDurableLockingID());
    assertEquals(durableLockResult.getLockModCount() + 1L, enableResult.getLockModCount());

    ChangeLockAreaResult disableResult = protocol.changeLockArea2(transaction, false);
    assertNull(disableResult.getDurableLockingID());
    assertEquals(enableResult.getLockModCount() + 1L, disableResult.getLockModCount());

    transaction.close();
    session.close();
  }

  public void testOwnerRemapAndUnlockCarriersCanArriveInReverseOrder() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("lockSequence"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOBranch branch = transaction.getBranch();
    CDOLockOwner oldOwner = CDOLockUtil.createLockOwner(session.getSessionID(), 1, "durable");
    CDOLockOwner newOwner = CDOLockUtil.createLockOwner(session.getSessionID(), 1, null);
    CDOLockState initialState = CDOLockUtil.createLockState(cdoCompany.cdoID());
    ((InternalCDOLockState)initialState).addOwner(oldOwner, LockType.WRITE);

    CDOLockStateCache cache = ((InternalCDOSession)session).getLockStateCache();
    cache.addLockStates(branch, Collections.singletonList(initialState), null);

    CDOLockDelta unlock = CDOLockUtil.createLockDelta(cdoCompany.cdoID(), LockType.WRITE, newOwner, null);
    CDOLockChangeInfo unlockInfo = CDOLockUtil.createLockChangeInfo(branch.getHead(), newOwner, Collections.singletonList(unlock), Collections.emptyList());

    invokeDoHandleLockNotification((InternalCDOSession)session, 2L, unlockInfo);
    assertEquals(oldOwner, cache.getLockState(branch, cdoCompany.cdoID()).getWriteLockOwner());

    CDOSessionImpl internalSession = (CDOSessionImpl)session;
    internalSession.handleLockOwnerRemappedNotification(1L, branch, oldOwner, newOwner);
    internalSession.awaitLockChange(2L);

    assertEquals(2L, getCurrentLockModCount((InternalCDOSession)session));
    CDOLockState finalState = cache.getLockState(branch, cdoCompany.cdoID());
    assertTrue(finalState == null || finalState.getWriteLockOwner() == null);

    transaction.close();
    session.close();
  }

  public void testSnapshotReplacesStaleCacheAndResumesBufferedCarriers() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("lockSnapshot"));
    Company staleCompany = getModel1Factory().createCompany();
    Company lockedCompany = getModel1Factory().createCompany();
    resource.getContents().add(staleCompany);
    resource.getContents().add(lockedCompany);
    transaction.commit();

    Company newCompany = getModel1Factory().createCompany();
    resource.getContents().add(newCompany);
    CDOObject newObject = CDOUtil.getCDOObject(newCompany);
    newObject.cdoWriteLock().lock();

    CDOObject staleObject = CDOUtil.getCDOObject(staleCompany);
    CDOObject lockedObject = CDOUtil.getCDOObject(lockedCompany);
    CDOBranch branch = transaction.getBranch();
    CDOLockOwner staleOwner = CDOLockUtil.createLockOwner(session.getSessionID(), 1, "stale");
    CDOLockOwner wrongOwner = CDOLockUtil.createLockOwner(session.getSessionID(), 2, "wrong");
    CDOLockOwner durableOwner = CDOLockUtil.createLockOwner(session.getSessionID(), 3, "durable");
    CDOLockOwner normalOwner = CDOLockUtil.createLockOwner(session.getSessionID(), 3, null);
    CDOLockState staleState = CDOLockUtil.createLockState(staleObject.cdoID());
    ((InternalCDOLockState)staleState).addOwner(staleOwner, LockType.WRITE);
    CDOLockState wrongState = CDOLockUtil.createLockState(lockedObject.cdoID());
    ((InternalCDOLockState)wrongState).addOwner(wrongOwner, LockType.WRITE);
    CDOLockState authoritativeState = CDOLockUtil.createLockState(lockedObject.cdoID());
    ((InternalCDOLockState)authoritativeState).addOwner(durableOwner, LockType.WRITE);

    CDOLockStateCache cache = ((InternalCDOSession)session).getLockStateCache();
    cache.addLockStates(branch, Arrays.asList(staleState, wrongState), null);
    AtomicInteger lockEvents = new AtomicInteger();
    session.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionLocksChangedEvent)
        {
          lockEvents.incrementAndGet();
        }
      }
    });

    CDOSessionImpl implementation = (CDOSessionImpl)session;
    implementation.suspendLockChanges(0L);
    implementation.handleLockOwnerRemappedNotification(5L, branch, wrongOwner, staleOwner);

    CDOLockDelta unlock = CDOLockUtil.createLockDelta(lockedObject.cdoID(), LockType.WRITE, normalOwner, null);
    CDOLockChangeInfo unlockInfo = CDOLockUtil.createLockChangeInfo(branch.getHead(), normalOwner, Collections.singletonList(unlock), Collections.emptyList());
    invokeDoHandleLockNotification((InternalCDOSession)session, 7L, unlockInfo);
    implementation.handleLockOwnerRemappedNotification(6L, branch, durableOwner, normalOwner);

    assertEquals(staleOwner, cache.getLockState(branch, staleObject.cdoID()).getWriteLockOwner());
    assertEquals(wrongOwner, cache.getLockState(branch, lockedObject.cdoID()).getWriteLockOwner());
    assertEquals(0L, getCurrentLockModCount((InternalCDOSession)session));

    implementation.installLockStateSnapshot(new LockStateSnapshotResult(5L, Collections.singletonList(authoritativeState)));
    implementation.awaitLockChange(7L);

    assertEquals(7L, getCurrentLockModCount((InternalCDOSession)session));
    assertTrue(newObject.cdoWriteLock().isLocked());
    CDOLockState finalStaleState = cache.getLockState(branch, staleObject.cdoID());
    assertTrue(finalStaleState == null || finalStaleState.getWriteLockOwner() == null);
    CDOLockState finalLockedState = cache.getLockState(branch, lockedObject.cdoID());
    assertTrue(finalLockedState == null || finalLockedState.getWriteLockOwner() == null);
    assertEquals(1, lockEvents.get());

    newObject.cdoWriteLock().unlock();

    transaction.close();
    session.close();
  }

  public void testServerSnapshotReturnsMatchingSessionBaseline()
  {
    CDOSession session = openSession();
    LockStateSnapshotResult snapshot = ((InternalCDOSession)session).getSessionProtocol().getLockStateSnapshot();

    assertEquals(0L, snapshot.getLockModCount());
    assertNotNull(snapshot.getLockStates());

    session.close();
  }

  public void testClosingRecipientDoesNotLoseEarlierForceFullReservation() throws Exception
  {
    Session activeSession = new Session(getRepository().getSessionManager(), null, Integer.MAX_VALUE - 101, "reservation-A");
    Session closingSession = new Session(getRepository().getSessionManager(), null, Integer.MAX_VALUE - 102, "reservation-B");

    try
    {
      closingSession.close();
      assertTrue(closingSession.isClosed());

      Method reserveRecipients = LockingManager.class.getDeclaredMethod("reserveLockChange", ISession[].class, boolean.class);
      reserveRecipients.setAccessible(true);
      Object reservation = reserveRecipients.invoke(null, new ISession[] { activeSession, closingSession }, true);
      assertNotNull(reservation);

      Method reserveSession = Session.class.getDeclaredMethod("reserveLockChange", boolean.class);
      reserveSession.setAccessible(true);
      Object laterTicket = reserveSession.invoke(activeSession, true);

      CDOBranch branch = getRepository().getBranchManager().getMainBranch();
      CDOLockOwner owner = CDOLockUtil.createLockOwner(activeSession.getSessionID(), 1, "durable");
      CDOLockChangeInfo info = CDOLockUtil.createLockChangeInfo(branch.getHead(), owner, Collections.emptyList(), Collections.emptyList());

      Method completeSessionTicket = Session.class.getDeclaredMethod("completeLockChange", LockChangeDispatcher.Ticket.class, CDOLockChangeInfo.class);
      completeSessionTicket.setAccessible(true);
      completeSessionTicket.invoke(activeSession, laterTicket, info);
      assertEquals(0L, activeSession.getLockModCount());

      Method completeReservation = reservation.getClass().getDeclaredMethod("complete", CDOLockChangeInfo.class, Session.class);
      completeReservation.setAccessible(true);
      completeReservation.invoke(reservation, info, null);

      assertEquals(2L, activeSession.getLockModCount());
      Method isQuiescent = Session.class.getDeclaredMethod("isLockChangesQuiescent");
      isQuiescent.setAccessible(true);
      assertEquals(Boolean.TRUE, isQuiescent.invoke(activeSession));
    }
    finally
    {
      activeSession.close();
      closingSession.close();
    }
  }

  public void testSynchronousLockResultFailsFastWhenCallerAlreadyHoldsAccess() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("lockAccess"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    InternalCDOSession internalSession = (InternalCDOSession)session;

    try (Access access = ((InternalCDOView)transaction).access())
    {
      try
      {
        transaction.lockObjects(Collections.singleton(cdoCompany), LockType.WRITE, DEFAULT_TIMEOUT, false);
        fail("A synchronous lock result must not wait while caller-owned Access is held");
      }
      catch (IllegalStateException ex)
      {
        assertTrue(ex.getMessage().contains("holding view Access"));
      }
    }

    CDOSessionImpl implementation = (CDOSessionImpl)session;
    implementation.awaitLockChange(1L);
    assertEquals(1L, getCurrentLockModCount(internalSession));
    transaction.unlockObjects(Collections.singleton(cdoCompany), LockType.WRITE, false);

    transaction.close();
    session.close();
  }

  public void testSynchronousLockResultWaitsBehindDelayedNotificationWithoutHoldingAccess() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setLockNotificationEnabled(true);
    CDOResource resource = transaction1.createResource(getResourcePath("lockWait"));
    Company notifiedCompany = getModel1Factory().createCompany();
    Company ownCompany = getModel1Factory().createCompany();
    resource.getContents().add(notifiedCompany);
    resource.getContents().add(ownCompany);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction(transaction1.getBranch());
    CDOObject notifiedObject = CDOUtil.getCDOObject(notifiedCompany);
    CDOObject ownObject = CDOUtil.getCDOObject(ownCompany);
    CDOLockStateCache cache = ((InternalCDOSession)session1).getLockStateCache();
    CountDownLatch notificationSubmitted = new CountDownLatch(1);
    CountDownLatch ownOperationReturned = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    lockNotificationSubmitted = notificationSubmitted;

    try
    {
      synchronized (cache)
      {
        transaction2.lockObjects(Collections.singleton(notifiedObject), LockType.WRITE, DEFAULT_TIMEOUT, false);
        assertTrue("The earlier notification must reach the session handler", notificationSubmitted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

        Thread operation = new Thread(() -> {
          try
          {
            transaction1.lockObjects(Collections.singleton(ownObject), LockType.WRITE, DEFAULT_TIMEOUT, false);
          }
          catch (Throwable ex)
          {
            failure.set(ex);
          }
          finally
          {
            ownOperationReturned.countDown();
          }
        }, "lock-result-waiter"); //$NON-NLS-1$

        operation.start();

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT);

        while (operation.getState() != Thread.State.WAITING && ownOperationReturned.getCount() != 0 && System.nanoTime() < deadline)
        {
          if (operation.getState() == Thread.State.BLOCKED)
          {
            fail("The synchronous result must wait on its sequence completion, not block on the lock cache");
          }

          Thread.yield();
        }

        assertEquals(Thread.State.WAITING, operation.getState());
        assertEquals(1L, ownOperationReturned.getCount());
        assertFalse(((AbstractCDOView)transaction1).isAccessHeldBy(operation));
        assertEquals(0L, getCurrentLockModCount((InternalCDOSession)session1));

      }

      assertTrue(ownOperationReturned.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertNull(failure.get());
      assertEquals(2L, getCurrentLockModCount((InternalCDOSession)session1));
      assertEquals(transaction1.getLockOwner(), cache.getLockState(transaction1.getBranch(), ownObject.cdoID()).getWriteLockOwner());
    }
    finally
    {
      lockNotificationSubmitted = null;
      session2.close();
      session1.close();
    }
  }

  public void testCommitNotificationWaitsForTimestampAndLockCountCaseA() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOSessionImpl implementation = (CDOSessionImpl)session;
    session.options().setPassiveUpdateEnabled(true);
    List<IEvent> events = new ArrayList<>();
    session.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionInvalidationEvent || event instanceof CDOSessionLocksChangedEvent)
        {
          synchronized (events)
          {
            events.add(event);
          }
        }
      }
    });

    CDOBranch branch = transaction.getBranch();
    long baseTimeStamp = implementation.getLastUpdateTime();
    long predecessorTimeStamp = baseTimeStamp + 10L;
    long commitTimeStamp = predecessorTimeStamp + 10L;
    CommitNotificationInfo blockedCommit = createCommitNotificationInfo((InternalCDOSession)session, branch, commitTimeStamp, predecessorTimeStamp, 1L);
    implementation.handleCommitNotification(blockedCommit);

    CDOLockChangeInfo laterLock = CDOLockUtil.createLockChangeInfo(branch.getHead(), transaction.getLockOwner(), Collections.emptyList(),
        Collections.emptyList());
    invokeDoHandleLockNotification((InternalCDOSession)session, 2L, laterLock);
    assertEquals(0L, getCurrentLockModCount((InternalCDOSession)session));
    assertTrue(events.isEmpty());

    CommitNotificationInfo predecessor = createCommitNotificationInfo((InternalCDOSession)session, branch, predecessorTimeStamp, baseTimeStamp, 0L);
    implementation.handleCommitNotification(predecessor);
    implementation.awaitLockChange(2L);

    assertEquals(2L, getCurrentLockModCount((InternalCDOSession)session));
    assertEquals(3, events.size());
    assertTrue(events.get(0) instanceof CDOSessionInvalidationEvent);
    assertEquals(predecessorTimeStamp, ((CDOSessionInvalidationEvent)events.get(0)).getTimeStamp());
    assertTrue(events.get(1) instanceof CDOSessionInvalidationEvent);
    assertEquals(commitTimeStamp, ((CDOSessionInvalidationEvent)events.get(1)).getTimeStamp());
    assertEquals(1L, ((InternalCDOSessionInvalidationEvent)events.get(1)).getLockModCount());
    assertTrue(events.get(2) instanceof CDOSessionLocksChangedEvent);
    assertEquals(2L, ((CDOSessionLocksChangedEvent)events.get(2)).getLockModCount());

    transaction.close();
    session.close();
  }

  public void testCommitNotificationWaitsForLockCountCaseB() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOSessionImpl implementation = (CDOSessionImpl)session;
    session.options().setPassiveUpdateEnabled(true);
    List<IEvent> events = new ArrayList<>();
    session.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionInvalidationEvent || event instanceof CDOSessionLocksChangedEvent)
        {
          synchronized (events)
          {
            events.add(event);
          }
        }
      }
    });

    CDOBranch branch = transaction.getBranch();
    long baseTimeStamp = implementation.getLastUpdateTime();
    long commitTimeStamp = baseTimeStamp + 10L;
    CommitNotificationInfo blockedCommit = createCommitNotificationInfo((InternalCDOSession)session, branch, commitTimeStamp, baseTimeStamp, 2L);
    implementation.handleCommitNotification(blockedCommit);

    assertEquals(0L, getCurrentLockModCount((InternalCDOSession)session));
    assertTrue(events.isEmpty());

    CDOLockChangeInfo earlierLock = CDOLockUtil.createLockChangeInfo(branch.getHead(), transaction.getLockOwner(), Collections.emptyList(),
        Collections.emptyList());
    invokeDoHandleLockNotification((InternalCDOSession)session, 1L, earlierLock);
    implementation.awaitLockChange(2L);

    assertEquals(2L, getCurrentLockModCount((InternalCDOSession)session));
    assertEquals(2, events.size());
    assertTrue(events.get(0) instanceof CDOSessionLocksChangedEvent);
    assertEquals(1L, ((CDOSessionLocksChangedEvent)events.get(0)).getLockModCount());
    assertTrue(events.get(1) instanceof CDOSessionInvalidationEvent);
    assertEquals(commitTimeStamp, ((CDOSessionInvalidationEvent)events.get(1)).getTimeStamp());
    assertEquals(2L, ((InternalCDOSessionInvalidationEvent)events.get(1)).getLockModCount());

    transaction.close();
    session.close();
  }

  public void testCommitRunnableWaitsAfterItsOuterAccessIsReleased() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setAutoReleaseLocksEnabled(false);
    transaction1.options().setLockNotificationEnabled(true);
    CDOResource resource = transaction1.createResource(getResourcePath("commitLockSequence"));
    Company notifiedCompany = getModel1Factory().createCompany();
    resource.getContents().add(notifiedCompany);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction(transaction1.getBranch());
    CountDownLatch notificationSubmitted = new CountDownLatch(1);
    CountDownLatch commitReturned = new CountDownLatch(1);
    AtomicReference<CDOCommitInfo> commitInfo = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Company newCompany = getModel1Factory().createCompany();
    CDOLockStateCache cache = ((InternalCDOSession)session1).getLockStateCache();
    lockNotificationSubmitted = notificationSubmitted;

    try
    {
      synchronized (cache)
      {
        transaction2.lockObjects(Collections.singleton(CDOUtil.getCDOObject(notifiedCompany)), LockType.WRITE, DEFAULT_TIMEOUT, false);
        assertTrue("The earlier notification must reach the session handler", notificationSubmitted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

        Thread commitThread = new Thread(() -> {
          try
          {
            Predicate<Long> retry = elapsed -> false;
            commitInfo.set(transaction1.commit((Runnable)() -> {
              resource.getContents().add(newCompany);
              CDOUtil.getCDOObject(newCompany).cdoWriteLock().lock();
            }, retry, null));
          }
          catch (Throwable ex)
          {
            failure.set(ex);
          }
          finally
          {
            commitReturned.countDown();
          }
        }, "nested-commit-lock-waiter"); //$NON-NLS-1$

        commitThread.start();

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT);
        while (commitThread.getState() != Thread.State.WAITING && commitReturned.getCount() != 0 && System.nanoTime() < deadline)
        {
          if (commitThread.getState() == Thread.State.BLOCKED)
          {
            fail("The commit must wait on its sequence completion, not block on the lock cache");
          }

          Thread.yield();
        }

        assertEquals(Arrays.toString(commitThread.getStackTrace()), Thread.State.WAITING, commitThread.getState());
        assertEquals(1L, commitReturned.getCount());
        assertFalse(((AbstractCDOView)transaction1).isAccessHeldBy(commitThread));
        assertEquals(0L, getCurrentLockModCount((InternalCDOSession)session1));
      }

      assertTrue(commitReturned.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      assertNull(failure.get());
      assertNotNull(commitInfo.get());
      assertEquals(2L, getCurrentLockModCount((InternalCDOSession)session1));
      assertEquals(transaction1.getLockOwner(), cache.getLockState(transaction1.getBranch(), CDOUtil.getCDOObject(newCompany).cdoID()).getWriteLockOwner());
    }
    finally
    {
      lockNotificationSubmitted = null;
      session2.close();
      session1.close();
    }
  }

  private CommitNotificationInfo createCommitNotificationInfo(InternalCDOSession session, CDOBranch branch, long timeStamp, long previousTimeStamp,
      long lockModCount)
  {
    CommitNotificationInfo info = new CommitNotificationInfo();
    CDOCommitInfo commitInfo = session.getCommitInfoManager().createCommitInfo(branch, timeStamp, previousTimeStamp, "test", "test",
        new CDOCommitDataImpl(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
    info.setCommitInfo(commitInfo);
    info.setLockModCount(lockModCount);
    return info;
  }

  private void invokeDoHandleLockNotification(InternalCDOSession session, long lockModCount, CDOLockChangeInfo lockChangeInfo) throws Exception
  {
    Method method = CDOSessionImpl.class.getDeclaredMethod("doHandleLockNotification", long.class, CDOLockChangeInfo.class, InternalCDOView.class,
        boolean.class);
    method.setAccessible(true);
    method.invoke(session, lockModCount, lockChangeInfo, null, true);
  }

  private long getCurrentLockModCount(InternalCDOSession session) throws Exception
  {
    Method method = CDOSessionImpl.class.getDeclaredMethod("getCurrentLockModCount");
    method.setAccessible(true);
    return (long)method.invoke(session);
  }

  public void testLockStateHeldByDurableView() throws Exception
  {
    skipStoreWithoutDurableLocking();

    {
      CDOSession session1 = openSession();
      CDOTransaction tx1 = session1.openTransaction();
      tx1.enableDurableLocking();
      CDOResource res1 = tx1.createResource(getResourcePath("r1"));
      Company company1 = getModel1Factory().createCompany();
      res1.getContents().add(company1);
      tx1.commit();

      CDOUtil.getCDOObject(company1).cdoWriteLock().lock();
      waitForActiveLockNotifications();
      tx1.close();
      session1.close();
    }

    CDOSession session2 = openSession();
    CDOView controlView = session2.openView();
    CDOResource resource = controlView.getResource(getResourcePath("r1"));
    Company company1 = (Company)resource.getContents().get(0);
    CDOObject cdoObj = CDOUtil.getCDOObject(company1);
    assertEquals(true, cdoObj.cdoWriteLock().isLockedByOthers());
    assertEquals(true, cdoObj.cdoLockState().getWriteLockOwner().isDurableView());
    session2.close();
  }

  public void testLockStateTransientAndNew() throws Exception
  {
    Company company1 = getModel1Factory().createCompany();
    CDOObject cdoObj = CDOUtil.getCDOObject(company1);
    assertTransient(cdoObj);
    assertNull(cdoObj.cdoLockState());

    CDOSession session1 = openSession();
    CDOTransaction tx1 = session1.openTransaction();
    CDOResource res1 = tx1.createResource(getResourcePath("r1"));
    res1.getContents().add(company1);
    assertNew(cdoObj, tx1);
    assertNotNull(cdoObj.cdoLockState());

    res1.getContents().remove(company1);
    assertTransient(cdoObj);
    assertNull(cdoObj.cdoLockState());

    res1.getContents().add(company1);
    tx1.commit();
    assertClean(cdoObj, tx1);
    assertNotNull(cdoObj.cdoLockState());

    res1.getContents().remove(company1);
    assertTransient(cdoObj);
    assertNull(cdoObj.cdoLockState());

    session1.close();
  }

  public void testEventForNewObjects() throws Exception
  {
    CDOObject company = CDOUtil.getCDOObject(getModel1Factory().createCompany());
    assertTransient(company);
    assertNull(company.cdoLockState());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("r1"));
    resource.getContents().add(company);
    assertNew(company, transaction);

    TestListener2 listener = new TestListener2(CDOViewLocksChangedEvent.class).dump(true, false);
    transaction.addListener(listener);

    lockWrite(company);
    assertWriteLock(true, company);

    listener.waitFor(1);
    listener.clearEvents();

    unlockWrite(company);
    assertWriteLock(false, company);

    listener.waitFor(1);
  }

  public void testCloseViewSameSession() throws Exception
  {
    CDOSession session1 = openSession();
    runCloseView(session1, session1);
  }

  public void testCloseViewDifferentSession() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();
    runCloseView(session1, session2);
  }

  private void runCloseView(CDOSession session1, CDOSession session2) throws ConcurrentAccessException, CommitException, InterruptedException
  {
    CDOTransaction transaction1 = session1.openTransaction();
    CDOLockOwner lockOwner1 = transaction1.getLockOwner();
    CDOResource resource1 = transaction1.createResource(getResourcePath("r1"));

    CDOObject company1 = CDOUtil.getCDOObject(getModel1Factory().createCompany());
    resource1.getContents().add(company1);
    transaction1.commit();

    CDOView view2 = openViewWithLockNotifications(session2, transaction1.getBranch());

    @SuppressWarnings("unused")
    CDOObject company2 = view2.getObject(company1);

    TestListener2 listener2 = new TestListener2(CDOViewLocksChangedEvent.class).dump(true, false);
    view2.addListener(listener2);

    lockWrite(company1);
    listener2.waitFor(1);
    listener2.clearEvents();

    transaction1.close();
    IEvent[] events = listener2.waitFor(1);
    assertEquals(1, events.length);

    CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
    CDOLockDelta[] lockDeltas = event.getLockDeltas();
    assertEquals(1, lockDeltas.length);
    assertEquals(CDOLockDelta.Kind.REMOVED, lockDeltas[0].getKind());
    assertEquals(LockType.WRITE, lockDeltas[0].getType());
    assertEquals(lockOwner1, lockDeltas[0].getOldOwner());
    assertEquals(null, lockDeltas[0].getNewOwner());

    assertEquals(1, event.getLockStates().length);
  }

  private CDOView openViewWithLockNotifications(CDOSession session, CDOBranch branch)
  {
    CDOView view = branch != null ? session.openView(branch) : session.openView();
    view.options().setLockNotificationEnabled(true);
    return view;
  }

  private void sameBranchDifferentSession(boolean autoRelease) throws Exception
  {
    CDOSession session = openSession();
    CDOSession controlSession = openSession();
    CDOView controlView = openViewWithLockNotifications(controlSession, null);

    if (autoRelease)
    {
      withAutoRelease(session, controlView, true);
    }
    else
    {
      withoutAutoRelease(session, controlView, true);
    }

    controlSession.close();
    session.close();
  }

  private void sameBranchSameSession(boolean autoRelease) throws Exception
  {
    CDOSession session = openSession();
    CDOView controlView = openViewWithLockNotifications(session, null);

    if (autoRelease)
    {
      withAutoRelease(session, controlView, true);
    }
    else
    {
      withoutAutoRelease(session, controlView, true);
    }

    session.close();
  }

  private void differentBranchDifferentSession(boolean autoRelease) throws Exception
  {
    CDOSession session = openSession();
    CDOBranch subBranch = session.getBranchManager().getMainBranch().createBranch(getBranchName("sub1"));

    CDOSession controlSession = openSession();
    CDOView controlView = openViewWithLockNotifications(controlSession, subBranch);

    if (autoRelease)
    {
      withAutoRelease(session, controlView, false);
    }
    else
    {
      withoutAutoRelease(session, controlView, false);
    }

    controlSession.close();
    session.close();
  }

  private void differentBranchSameSession(boolean autoRelease) throws Exception
  {
    CDOSession session = openSession();
    CDOBranch subBranch = session.getBranchManager().getMainBranch().createBranch(getBranchName("sub2"));
    CDOView controlView = openViewWithLockNotifications(session, subBranch);

    if (autoRelease)
    {
      withAutoRelease(session, controlView, false);
    }
    else
    {
      withoutAutoRelease(session, controlView, false);
    }

    session.close();
  }

  private void withoutAutoRelease(CDOSession session, CDOView controlView, boolean sameBranch) throws Exception
  {
    TestListener2 controlViewListener = new TestListener2(CDOViewLocksChangedEvent.class).dump(true, false);
    controlView.addListener(controlViewListener);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(false);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("r1"));
    TestListener2 transactionListener = new TestListener2(CDOViewLocksChangedEvent.class).dump(true, false);
    transaction.addListener(transactionListener);
    resource.getContents().clear();
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompanyInControlView = null;
    if (sameBranch)
    {
      cdoCompanyInControlView = controlView.getObject(cdoCompany.cdoID());
    }

    /* Test write lock */

    cdoCompany.cdoWriteLock().lock();
    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
      assertLockOwner(transaction, event.getLockOwner());

      CDOLockDelta[] lockDeltas = event.getLockDeltas();
      assertEquals(1, lockDeltas.length);
      assertLockedObject(cdoCompany, lockDeltas[0]);
      assertLockOwner(transaction, lockDeltas[0].getNewOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), event.getLockStates()[0]);
    }
    else
    {
      sleep(100);
      assertEquals(0, controlViewListener.getEvents().size());
    }

    controlViewListener.clearEvents();
    cdoCompany.cdoWriteLock().unlock();
    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
      assertLockOwner(transaction, event.getLockOwner());

      CDOLockDelta[] lockDeltas = event.getLockDeltas();
      assertEquals(1, lockDeltas.length);
      assertLockedObject(cdoCompany, lockDeltas[0]);
      assertLockOwner(transaction, lockDeltas[0].getOldOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), event.getLockStates()[0]);
    }

    /* Test read lock */

    controlViewListener.clearEvents();
    cdoCompany.cdoReadLock().lock();
    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
      assertLockOwner(transaction, event.getLockOwner());

      CDOLockDelta[] lockDeltas = event.getLockDeltas();
      assertEquals(1, lockDeltas.length);
      assertLockedObject(cdoCompany, lockDeltas[0]);
      assertLockOwner(transaction, lockDeltas[0].getNewOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), event.getLockStates()[0]);
    }

    controlViewListener.clearEvents();
    cdoCompany.cdoReadLock().unlock();
    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
      assertLockOwner(transaction, event.getLockOwner());

      CDOLockDelta[] lockDeltas = event.getLockDeltas();
      assertEquals(1, lockDeltas.length);
      assertLockOwner(transaction, lockDeltas[0].getOldOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), event.getLockStates()[0]);
    }

    /* Test write option */

    controlViewListener.clearEvents();
    cdoCompany.cdoWriteOption().lock();
    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
      assertLockOwner(transaction, event.getLockOwner());

      CDOLockDelta[] lockDeltas = event.getLockDeltas();
      assertEquals(1, lockDeltas.length);
      assertLockedObject(cdoCompany, lockDeltas[0]);
      assertLockOwner(transaction, lockDeltas[0].getNewOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), event.getLockStates()[0]);
    }

    controlViewListener.clearEvents();
    cdoCompany.cdoWriteOption().unlock();
    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)events[0];
      assertLockOwner(transaction, event.getLockOwner());

      CDOLockDelta[] lockDeltas = event.getLockDeltas();
      assertEquals(1, lockDeltas.length);
      assertLockedObject(cdoCompany, lockDeltas[0]);
      assertLockOwner(transaction, lockDeltas[0].getOldOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), event.getLockStates()[0]);
    }
  }

  private void withAutoRelease(CDOSession session, CDOView controlView, boolean sameBranch) throws Exception
  {
    TestListener2 controlViewListener = new TestListener2(CDOViewLocksChangedEvent.class).dump(true, true);
    controlView.addListener(controlViewListener);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(true);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("r1"));
    resource.getContents().clear();

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    withAutoRelease(company, LockType.READ, transaction, controlViewListener, sameBranch);
    withAutoRelease(company, LockType.WRITE, transaction, controlViewListener, sameBranch);
    withAutoRelease(company, LockType.OPTION, transaction, controlViewListener, sameBranch);
  }

  private void withAutoRelease(Company company, LockType lockType, CDOTransaction transaction, TestListener2 controlViewListener, boolean sameBranch)
      throws Exception
  {
    CDOViewLocksChangedEvent event;
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    company.setName(company.getName() + "x"); // Make object DIRTY.

    controlViewListener.clearEvents();

    switch (lockType)
    {
    case READ:
      cdoCompany.cdoReadLock().lock();
      break;

    case WRITE:
      cdoCompany.cdoWriteLock().lock();
      break;

    case OPTION:
      cdoCompany.cdoWriteOption().lock();
      break;
    }

    waitForActiveLockNotifications();

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      event = (CDOViewLocksChangedEvent)events[0];
      assertEquals(Collections.singleton(Operation.LOCK), event.getOperations());
      assertEquals(Collections.singleton(lockType), event.getLockTypes());
    }
    else
    {
      sleep(100);
      assertEquals(0, controlViewListener.getEvents().size());
    }

    controlViewListener.clearEvents();
    Session transactionServerSession = (Session)serverSession(transaction.getSession());
    long lockModCount = transactionServerSession.getLockModCount();
    transaction.commit();
    assertEquals(lockModCount + 1, transactionServerSession.getLockModCount());

    if (sameBranch)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      event = (CDOViewLocksChangedEvent)events[0];
      assertEquals(Collections.singleton(Operation.UNLOCK), event.getOperations());
      assertEquals(Collections.singleton(lockType), event.getLockTypes());
    }
    else
    {
      sleep(100);
      assertEquals(0, controlViewListener.getEvents().size());
    }
  }

  public static void assertLockedObject(CDOObject expected, CDOLockDelta actual)
  {
    Object lockedObject = actual.getTarget();
    if (lockedObject instanceof CDOIDAndBranch)
    {
      CDOIDAndBranch idAndBranch = (CDOIDAndBranch)lockedObject;
      assertEquals(expected.cdoID(), idAndBranch.getID());
      assertEquals(expected.cdoView().getBranch().getID(), idAndBranch.getBranch().getID());
    }
    else if (lockedObject instanceof CDOID)
    {
      assertEquals(expected.cdoID(), lockedObject);
    }
  }

  public static void assertLockOwner(CDOView expected, CDOLockOwner actual)
  {
    CDOLockOwner lockOwner = expected == null ? null : expected.getLockOwner();
    assertEquals(lockOwner, actual);
  }
}
