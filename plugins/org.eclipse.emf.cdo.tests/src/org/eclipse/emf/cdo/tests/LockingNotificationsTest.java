/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018, 2020-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.tests.TestListener2;

import java.util.Collections;

/**
 * @author Caspar De Groot
 */
public class LockingNotificationsTest extends AbstractLockingTest
{
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
    transaction.commit();

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
