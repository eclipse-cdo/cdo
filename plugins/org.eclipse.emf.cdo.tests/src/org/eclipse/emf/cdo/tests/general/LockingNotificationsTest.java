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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.net4j.protocol.LockStateRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.LockStateSnapshotRequest;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
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
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.session.DelegatingSessionProtocol;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;
import org.eclipse.net4j.util.concurrent.Access;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.tests.TestListener2;

import org.eclipse.emf.spi.cdo.CDOLockStateCache;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.ChangeLockAreaResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockStateQueryResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockStateSnapshotResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionInvalidationEvent;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

  private volatile CountDownLatch lockNotificationBlocked;

  private volatile CountDownLatch lockNotificationRelease;

  @Override
  protected void beforeLockNotificationSubmitted(long lockModCount)
  {
    CountDownLatch blocked = lockNotificationBlocked;
    CountDownLatch release = lockNotificationRelease;

    if (lockModCount > 0L && blocked != null && release != null)
    {
      blocked.countDown();

      try
      {
        if (!release.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
        {
          throw new IllegalStateException("Lock notification was not released"); //$NON-NLS-1$
        }
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }
    }
  }

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

  public void testLazyLockStateQueryDefersFutureBaselineCachePopulation() throws Exception
  {
    CDOSession observer = openSession();
    CDOTransaction observerTransaction = observer.openTransaction();
    observerTransaction.options().setLockNotificationEnabled(true);
    CDOResource resource = observerTransaction.createResource(getResourcePath("futureLazyLockQuery"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    observerTransaction.commit();

    CDOSession locker = openSession();
    CDOTransaction lockerTransaction = locker.openTransaction(observerTransaction.getBranch());
    CDOObject object = CDOUtil.getCDOObject(company);
    CDOLockStateCache cache = ((InternalCDOSession)observer).getLockStateCache();
    CountDownLatch notificationBlocked = new CountDownLatch(1);
    CountDownLatch notificationRelease = new CountDownLatch(1);
    lockNotificationBlocked = notificationBlocked;
    lockNotificationRelease = notificationRelease;

    try
    {
      lockerTransaction.lockObjects(Collections.singleton(object), LockType.WRITE, DEFAULT_TIMEOUT, false);
      assertTrue("The real server change must reach the observer notification gate", notificationBlocked.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      assertEquals(0L, getCurrentLockModCount((InternalCDOSession)observer));

      CDOLockState queriedState = object.cdoLockState();
      assertNotNull(queriedState);
      assertEquals(lockerTransaction.getLockOwner(), queriedState.getWriteLockOwner());
      assertNull("The future query result must not reach the shared cache before its carrier", //$NON-NLS-1$
          cache.getLockState(observerTransaction.getBranch(), object.cdoID()).getWriteLockOwner());

      notificationRelease.countDown();
      ((CDOSessionImpl)observer).awaitLockChange(1L);
      assertEquals(lockerTransaction.getLockOwner(), cache.getLockState(observerTransaction.getBranch(), object.cdoID()).getWriteLockOwner());
    }
    finally
    {
      notificationRelease.countDown();
      lockNotificationBlocked = null;
      lockNotificationRelease = null;
      lockerTransaction.unlockObjects(Collections.singleton(object), LockType.WRITE, false);
      locker.close();
      observer.close();
    }
  }

  public void testRevisionLockStatePrefetchDefersFutureBaselineCachePopulation() throws Exception
  {
    CDOSession locker = openSession();
    CDOTransaction lockerTransaction = locker.openTransaction();
    CDOResource resource = lockerTransaction.createResource(getResourcePath("revisionPiggybackLockState"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    lockerTransaction.commit();
    CDOObject lockerObject = CDOUtil.getCDOObject(company);

    CDOSession observer = openSession();
    CDOTransaction observerTransaction = observer.openTransaction(lockerTransaction.getBranch());
    observerTransaction.options().setLockNotificationEnabled(true);
    InternalCDOSession internalObserver = (InternalCDOSession)observer;
    CDOLockStateCache cache = internalObserver.getLockStateCache();
    CDOID objectID = lockerObject.cdoID();
    CDOBranch branch = observerTransaction.getBranch();
    CountDownLatch notificationBlocked = new CountDownLatch(1);
    CountDownLatch notificationRelease = new CountDownLatch(1);
    lockNotificationBlocked = notificationBlocked;
    lockNotificationRelease = notificationRelease;
    SignalCounter signalCounter = new SignalCounter(((CDONet4jSession)observer).options().getNet4jProtocol());

    try
    {
      lockerTransaction.lockObjects(Collections.singleton(lockerObject), LockType.WRITE, DEFAULT_TIMEOUT, false);
      assertTrue("The real server change must reach the observer notification gate", notificationBlocked.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      assertEquals(0L, getCurrentLockModCount(internalObserver));

      CDORevision revision = observer.getRevisionManager().request().lookupMode(LookupMode.CACHE_THEN_LOADER).prefetchDepth(CDORevision.DEPTH_NONE)
          .prefetchLockStates(true).getRevision(objectID, observerTransaction);
      assertNotNull("The revision response must complete while its future lock baseline is pending", revision); //$NON-NLS-1$
      assertNull("The piggyback state must not reach the shared cache before its carrier", getCachedLockState(cache, branch, objectID)); //$NON-NLS-1$
      assertEquals(0L, getCurrentLockModCount(internalObserver));

      notificationRelease.countDown();
      ((CDOSessionImpl)observer).awaitLockChange(1L);
      CDOLockState finalState = getCachedLockState(cache, branch, objectID);
      assertNotNull(finalState);
      assertEquals(lockerTransaction.getLockOwner(), finalState.getWriteLockOwner());
      assertEquals(0, signalCounter.getCountFor(LockStateSnapshotRequest.class));
    }
    finally
    {
      notificationRelease.countDown();
      lockNotificationBlocked = null;
      lockNotificationRelease = null;
      signalCounter.dispose();
      lockerTransaction.unlockObjects(Collections.singleton(lockerObject), LockType.WRITE, false);
      observer.close();
      locker.close();
    }
  }

  public void testPrefetcherDefersQueryDerivedUnlockedStateUntilCarrier() throws Exception
  {
    CDOSession locker = openSession();
    CDOTransaction lockerTransaction = locker.openTransaction();
    CDOResource resource = lockerTransaction.createResource(getResourcePath("prefetchOmittedLockState"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    lockerTransaction.commit();
    CDOObject lockerObject = CDOUtil.getCDOObject(company);
    lockerTransaction.lockObjects(Collections.singleton(lockerObject), LockType.WRITE, DEFAULT_TIMEOUT, false);

    CDOSession observer = openSession();
    CDOTransaction observerTransaction = observer.openTransaction(lockerTransaction.getBranch());
    observerTransaction.options().setLockNotificationEnabled(true);
    new CDOLockStatePrefetcher(observerTransaction, false);

    InternalCDOSession internalObserver = (InternalCDOSession)observer;
    CDOSessionImpl observerImpl = (CDOSessionImpl)observer;
    CDOLockStateCache cache = internalObserver.getLockStateCache();
    CDOID objectID = lockerObject.cdoID();
    CDOBranch branch = observerTransaction.getBranch();
    CountDownLatch notificationBlocked = new CountDownLatch(1);
    CountDownLatch notificationRelease = new CountDownLatch(1);
    lockNotificationBlocked = notificationBlocked;
    lockNotificationRelease = notificationRelease;

    CDOSessionProtocol originalProtocol = internalObserver.getSessionProtocol();
    CountDownLatch queryCaptured = new CountDownLatch(1);
    CountDownLatch releaseQuery = new CountDownLatch(1);
    DelayingLockStateQueryProtocol delayedProtocol = new DelayingLockStateQueryProtocol(originalProtocol, queryCaptured, releaseQuery);
    internalObserver.setSessionProtocol(delayedProtocol);
    ISignalProtocol<?> net4jProtocol = ((CDONet4jSession)observer).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(net4jProtocol);
    AtomicReference<Throwable> loadFailure = new AtomicReference<>();
    Thread loadThread = new Thread(() -> {
      try
      {
        observerTransaction.getResource(getResourcePath("prefetchOmittedLockState")).getContents().get(0);
      }
      catch (Throwable ex)
      {
        loadFailure.set(ex);
      }
    }, "prefetch-omitted-lock-state-load"); //$NON-NLS-1$
    loadThread.setDaemon(true);

    try
    {
      lockerTransaction.unlockObjects(Collections.singleton(lockerObject), LockType.WRITE, false);
      assertTrue("The real unlock notification did not reach its gate", notificationBlocked.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      long observerBaseline = getCurrentLockModCount(internalObserver);
      assertEquals(0L, observerBaseline);
      assertNull(getCachedLockState(cache, branch, objectID));

      loadThread.start();
      assertTrue("The prefetcher did not issue its real lock-state query", queryCaptured.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      LockStateQueryResult result = delayedProtocol.getFirstResult();
      assertNotNull(result);
      assertTrue("The query must cover the primary loaded ID", delayedProtocol.getQueryIDs().contains(objectID)); //$NON-NLS-1$
      assertEquals(1L, result.getLockModCount());
      assertTrue("The server must omit the queried unlocked ID", result.getLockStates().stream().noneMatch(state -> objectID.equals(state.getID()))); //$NON-NLS-1$
      assertEquals(1, signalCounter.getCountFor(LockStateRequest.class));

      releaseQuery.countDown();
      loadThread.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("Revision loading did not finish after the query returned", loadThread.isAlive()); //$NON-NLS-1$
      assertNull(loadFailure.get());
      assertNull("The synthesized query default must wait for the future baseline", getCachedLockState(cache, branch, objectID)); //$NON-NLS-1$
      assertEquals(observerBaseline, getCurrentLockModCount(internalObserver));

      notificationRelease.countDown();
      observerImpl.awaitLockChange(result.getLockModCount());
      assertEquals(result.getLockModCount(), getCurrentLockModCount(internalObserver));
      CDOLockState finalState = getCachedLockState(cache, branch, objectID);
      assertNotNull(finalState);
      assertNull(finalState.getWriteLockOwner());
      assertEquals(0, signalCounter.getCountFor(LockStateSnapshotRequest.class));
    }
    finally
    {
      releaseQuery.countDown();
      notificationRelease.countDown();
      lockNotificationBlocked = null;
      lockNotificationRelease = null;
      signalCounter.dispose();
      internalObserver.setSessionProtocol(originalProtocol);
      loadThread.join(TimeUnit.SECONDS.toMillis(5));
      lockerTransaction.unlockObjects(Collections.singleton(lockerObject), LockType.WRITE, false);
      observer.close();
      locker.close();
    }
  }

  private static CDOLockState getCachedLockState(CDOLockStateCache cache, CDOBranch branch, CDOID id)
  {
    AtomicReference<CDOLockState> result = new AtomicReference<>();
    cache.getLockStates(branch, Collections.singleton(id), false, result::set);
    return result.get();
  }

  public void testStaleLazyLockStateQueryIsRetriedBeforeReturning() throws Exception
  {
    CDOSession locker = openSession();
    CDOTransaction lockerTransaction = locker.openTransaction();
    CDOResource resource = lockerTransaction.createResource(getResourcePath("staleLazyLockQuery"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    lockerTransaction.commit();
    CDOObject lockerObject = CDOUtil.getCDOObject(company);
    lockerTransaction.lockObjects(Collections.singleton(lockerObject), LockType.WRITE, DEFAULT_TIMEOUT, false);

    CDOSession observer = openSession();
    CDOTransaction observerTransaction = observer.openTransaction(lockerTransaction.getBranch());
    observerTransaction.options().setLockNotificationEnabled(true);
    Company observedCompany = (Company)observerTransaction.getResource(getResourcePath("staleLazyLockQuery")).getContents().get(0);
    CDOObject observedObject = CDOUtil.getCDOObject(observedCompany);
    InternalCDOSession internalObserver = (InternalCDOSession)observer;
    CDOSessionImpl sessionImpl = (CDOSessionImpl)observer;
    CDOLockStateCache cache = internalObserver.getLockStateCache();
    assertEquals(0L, getCurrentLockModCount(internalObserver));

    CDOSessionProtocol originalProtocol = internalObserver.getSessionProtocol();
    CountDownLatch firstResponseCaptured = new CountDownLatch(1);
    CountDownLatch releaseFirstResponse = new CountDownLatch(1);
    DelayingLockStateQueryProtocol delayedProtocol = new DelayingLockStateQueryProtocol(originalProtocol, firstResponseCaptured, releaseFirstResponse);
    internalObserver.setSessionProtocol(delayedProtocol);

    ISignalProtocol<?> net4jProtocol = ((CDONet4jSession)observer).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(net4jProtocol);
    AtomicReference<CDOLockState> returnedState = new AtomicReference<>();
    AtomicReference<Throwable> queryFailure = new AtomicReference<>();
    CountDownLatch queryReturned = new CountDownLatch(1);
    Thread queryThread = new Thread(() -> {
      try
      {
        cache.getLockStates(observerTransaction.getBranch(), Collections.singleton(observedObject.cdoID()), true, returnedState::set);
      }
      catch (Throwable ex)
      {
        queryFailure.set(ex);
      }
      finally
      {
        queryReturned.countDown();
      }
    }, "stale-lock-state-query"); //$NON-NLS-1$
    queryThread.setDaemon(true);

    CountDownLatch notificationSubmitted = new CountDownLatch(1);
    lockNotificationSubmitted = notificationSubmitted;

    try
    {
      queryThread.start();
      assertTrue("The real server query response was not captured", firstResponseCaptured.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      LockStateQueryResult oldResult = delayedProtocol.getFirstResult();
      assertNotNull(oldResult);
      assertEquals(0L, oldResult.getLockModCount());
      assertEquals(1, oldResult.getLockStates().size());
      assertEquals(lockerTransaction.getLockOwner(), oldResult.getLockStates().get(0).getWriteLockOwner());
      assertEquals(1, signalCounter.getCountFor(LockStateRequest.class));

      lockerTransaction.unlockObjects(Collections.singleton(lockerObject), LockType.WRITE, false);
      assertTrue("The real N+1 unlock carrier was not submitted", notificationSubmitted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      sessionImpl.awaitLockChange(1L);
      assertEquals(1L, getCurrentLockModCount(internalObserver));
      assertEquals(1L, getRepository().getSessionManager().getSession(observer.getSessionID()).getLockModCount());
      assertNull(cache.getLockState(observerTransaction.getBranch(), observedObject.cdoID()).getWriteLockOwner());
      assertEquals("The caller must remain blocked until the captured response is released", 1L, queryReturned.getCount()); //$NON-NLS-1$

      releaseFirstResponse.countDown();
      assertTrue("The stale query and its retry did not complete", queryReturned.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      assertNull(queryFailure.get());
      assertNotNull(returnedState.get());
      assertNull("The stale OLD state must not escape to the caller", returnedState.get().getWriteLockOwner()); //$NON-NLS-1$
      assertEquals("The stale baseline must cause a second real LockStateRequest", 2, signalCounter.getCountFor(LockStateRequest.class)); //$NON-NLS-1$
      assertEquals(2, delayedProtocol.getQueryResults().size());
      assertEquals(0L, delayedProtocol.getQueryResults().get(0).getLockModCount());
      assertTrue(delayedProtocol.getQueryResults().get(1).getLockModCount() >= 1L);
      assertTrue(delayedProtocol.getQueryResults().get(1).getLockStates().isEmpty());
      assertNull(cache.getLockState(observerTransaction.getBranch(), observedObject.cdoID()).getWriteLockOwner());
      assertEquals(0, signalCounter.getCountFor(LockStateSnapshotRequest.class));
    }
    finally
    {
      releaseFirstResponse.countDown();
      lockNotificationSubmitted = null;
      signalCounter.dispose();
      internalObserver.setSessionProtocol(originalProtocol);
      lockerTransaction.unlockObjects(Collections.singleton(lockerObject), LockType.WRITE, false);
      observer.close();
      locker.close();
      queryThread.join(TimeUnit.SECONDS.toMillis(5));
    }
  }

  public void testServerSelectiveQueryReturnsBaselineAndOmitsUnlockedIDs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("selectiveLockQuery"));
    Company lockedCompany = getModel1Factory().createCompany();
    Company unlockedCompany = getModel1Factory().createCompany();
    resource.getContents().add(lockedCompany);
    resource.getContents().add(unlockedCompany);
    transaction.commit();

    CDOSessionProtocol protocol = ((InternalCDOSession)session).getSessionProtocol();
    CDOBranch branch = transaction.getBranch();
    LockStateQueryResult beforeLock = protocol.getLockStates3(branch.getID(), Collections.singleton(CDOUtil.getCDOObject(lockedCompany).cdoID()),
        CDOLockState.DEPTH_NONE);
    assertEquals(0L, beforeLock.getLockModCount());
    assertTrue(beforeLock.getLockStates().isEmpty());

    transaction.lockObjects(Collections.singleton(CDOUtil.getCDOObject(lockedCompany)), LockType.WRITE, DEFAULT_TIMEOUT);
    waitForActiveLockNotifications();

    List<CDOID> ids = Arrays.asList(CDOUtil.getCDOObject(lockedCompany).cdoID(), CDOUtil.getCDOObject(unlockedCompany).cdoID());
    LockStateQueryResult afterLock = protocol.getLockStates3(branch.getID(), ids, CDOLockState.DEPTH_NONE);
    assertEquals(1L, afterLock.getLockModCount());
    assertEquals(1, afterLock.getLockStates().size());
    assertEquals(ids.get(0), afterLock.getLockStates().get(0).getID());
    assertNotNull(afterLock.getLockStates().get(0).getWriteLockOwner());

    transaction.unlockObjects(Collections.singleton(CDOUtil.getCDOObject(lockedCompany)), LockType.WRITE);
    transaction.close();
    session.close();
  }

  public void testSelectiveServerCaptureWaitsForLockChangeReservation() throws Exception
  {
    CDOSession session = openSession();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalLockManager lockManager = getRepository().getLockingManager();
    CDOBranch branch = getRepository().getBranchManager().getMainBranch();
    InternalSession.LockChangeReservation reservation = serverSession.reserveLockChange(true);
    AtomicReference<InternalLockManager.LockStateQuery> result = new AtomicReference<>();
    CountDownLatch started = new CountDownLatch(1);
    Thread queryThread = new Thread(() -> {
      started.countDown();
      result.set(lockManager.snapshotLockStates(serverSession, branch, Collections.emptyList()));
    }, "selective-lock-state-query-test"); //$NON-NLS-1$
    queryThread.setDaemon(true);

    try
    {
      queryThread.start();
      assertTrue(started.await(5, TimeUnit.SECONDS));

      long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
      boolean waitingForQuiescence = false;

      while (!waitingForQuiescence && System.nanoTime() < deadline)
      {
        for (StackTraceElement frame : queryThread.getStackTrace())
        {
          if ("awaitLockChangesQuiescent".equals(frame.getMethodName())) //$NON-NLS-1$
          {
            waitingForQuiescence = true;
            break;
          }
        }

        Thread.yield();
      }

      assertTrue("Selective query did not wait for the pending reservation", waitingForQuiescence); //$NON-NLS-1$
      assertNull(result.get());

      CDOLockOwner owner = CDOLockUtil.createLockOwner(serverSession.getSessionID(), 1, null);
      CDOLockChangeInfo info = CDOLockUtil.createLockChangeInfo(branch.getHead(), owner, Collections.emptyList(), Collections.emptyList());
      reservation.complete(info);

      queryThread.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("Selective query did not finish after reservation completion", queryThread.isAlive()); //$NON-NLS-1$
      assertNotNull(result.get());
      assertEquals(serverSession.getLockModCount(), result.get().getLockModCount());
    }
    finally
    {
      if (!serverSession.isLockChangesQuiescent())
      {
        reservation.cancel();
      }

      queryThread.join(TimeUnit.SECONDS.toMillis(5));
      session.close();
    }
  }

  public void testClosingRecipientDoesNotLoseEarlierForceFullReservation() throws Exception
  {
    Session activeSession = new Session(getRepository().getSessionManager(), null, Integer.MAX_VALUE - 101, "reservation-A");
    Session closingSession = new Session(getRepository().getSessionManager(), null, Integer.MAX_VALUE - 102, "reservation-B");

    try
    {
      closingSession.close();
      assertTrue(closingSession.isClosed());

      InternalLockManager lockManager = getRepository().getLockingManager();
      InternalLockManager.LockChangeReservationSet reservation = lockManager.reserveLockChange(new InternalSession[] { activeSession, closingSession }, true);
      assertNotNull(reservation);

      InternalSession.LockChangeReservation laterTicket = activeSession.reserveLockChange(true);

      CDOBranch branch = getRepository().getBranchManager().getMainBranch();
      CDOLockOwner owner = CDOLockUtil.createLockOwner(activeSession.getSessionID(), 1, "durable");
      CDOLockChangeInfo info = CDOLockUtil.createLockChangeInfo(branch.getHead(), owner, Collections.emptyList(), Collections.emptyList());

      laterTicket.complete(info);
      assertEquals(0L, activeSession.getLockModCount());

      reservation.complete(info, null);

      assertEquals(2L, activeSession.getLockModCount());
      assertTrue(activeSession.isLockChangesQuiescent());
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
    InternalSession transactionServerSession = serverSession(transaction.getSession());
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

  /**
   * @author Eike Stepper
   */
  private static final class DelayingLockStateQueryProtocol extends DelegatingSessionProtocol
  {
    private final CountDownLatch captured;

    private final CountDownLatch release;

    private final List<LockStateQueryResult> queryResults = new ArrayList<>();

    private Collection<CDOID> queryIDs;

    private LockStateQueryResult firstResult;

    public DelayingLockStateQueryProtocol(CDOSessionProtocol delegate, CountDownLatch captured, CountDownLatch release)
    {
      super(delegate, null);
      this.captured = captured;
      this.release = release;
    }

    @Override
    public LockStateQueryResult getLockStates3(int branchID, Collection<CDOID> ids, int depth)
    {
      queryIDs = new ArrayList<>(ids);
      LockStateQueryResult result = super.getLockStates3(branchID, ids, depth);
      synchronized (queryResults)
      {
        queryResults.add(result);
        if (firstResult == null)
        {
          firstResult = result;
        }
      }

      if (firstResult == result)
      {
        captured.countDown();
        try
        {
          if (!release.await(1, TimeUnit.MINUTES))
          {
            throw new IllegalStateException("The captured query response was not released"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      return result;
    }

    private LockStateQueryResult getFirstResult()
    {
      return firstResult;
    }

    private List<LockStateQueryResult> getQueryResults()
    {
      synchronized (queryResults)
      {
        return new ArrayList<>(queryResults);
      }
    }

    private Collection<CDOID> getQueryIDs()
    {
      return queryIDs;
    }
  }
}
