/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.tests.TestListener2;

import java.util.List;

/**
 * @author Caspar De Groot
 */
public class LockingNotificationsTest extends AbstractLockingTest
{
  /**
   * FIXME Disabled until bug 358603 is addressed.
   */
  public void _testSameBranchDifferentSession_explicitRelease() throws Exception
  {
    sameBranchDifferentSession(LockReleaseMode.EXPLICIT);
  }

  public void testSameBranchDifferentSession_autoRelease() throws Exception
  {
    sameBranchDifferentSession(LockReleaseMode.AUTO);
  }

  public void testSameBranchSameSession_explicitRelease() throws Exception
  {
    sameBranchSameSession(LockReleaseMode.EXPLICIT);
  }

  public void testSameBranchSameSession_autoRelease() throws Exception
  {
    sameBranchSameSession(LockReleaseMode.AUTO);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchDifferentSession() throws Exception
  {
    differentBranchDifferentSession(LockReleaseMode.EXPLICIT);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchDifferentSession_autoRelease() throws Exception
  {
    differentBranchDifferentSession(LockReleaseMode.AUTO);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchSameSession() throws Exception
  {
    differentBranchSameSession(LockReleaseMode.EXPLICIT);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchSameSession_autoRelease() throws Exception
  {
    differentBranchSameSession(LockReleaseMode.AUTO);
  }

  public void testEnableDisableNotifications() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();
    CDOView controlView = session2.openView();
    withExplicitRelease(session1, controlView, false);

    controlView.options().setLockNotificationEnabled(true);
    withExplicitRelease(session1, controlView, true);

    controlView.options().setLockNotificationEnabled(false);
    withExplicitRelease(session1, controlView, false);

    session1.close();
    session2.close();
  }

  public void testEnableDisableNotificationsSameSession() throws Exception
  {
    CDOSession session1 = openSession();
    CDOView controlView = session1.openView();
    withExplicitRelease(session1, controlView, false);

    controlView.options().setLockNotificationEnabled(true);
    withExplicitRelease(session1, controlView, true);

    controlView.options().setLockNotificationEnabled(false);
    withExplicitRelease(session1, controlView, false);

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

  public void testLockStateNewAndTransient() throws Exception
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

  private CDOView openViewWithLockNotifications(CDOSession session, CDOBranch branch)
  {
    CDOView view = branch != null ? session.openView(branch) : session.openView();
    view.options().setLockNotificationEnabled(true);
    return view;
  }

  private void sameBranchDifferentSession(LockReleaseMode mode) throws Exception
  {
    CDOSession session = openSession();
    CDOSession controlSession = openSession();
    CDOView controlView = openViewWithLockNotifications(controlSession, null);

    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session, controlView, true);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session, controlView, true);
    }

    controlSession.close();
    session.close();
  }

  private void sameBranchSameSession(LockReleaseMode mode) throws Exception
  {
    CDOSession session = openSession();
    CDOView controlView = openViewWithLockNotifications(session, null);

    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session, controlView, true);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session, controlView, true);
    }

    session.close();
  }

  private void differentBranchDifferentSession(LockReleaseMode mode) throws Exception
  {
    CDOSession session = openSession();
    CDOBranch subBranch = session.getBranchManager().getMainBranch().createBranch("sub1");

    CDOSession controlSession = openSession();
    CDOView controlView = openViewWithLockNotifications(controlSession, subBranch);

    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session, controlView, false);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session, controlView, false);
    }

    controlSession.close();
    session.close();
  }

  private void differentBranchSameSession(LockReleaseMode mode) throws Exception
  {
    CDOSession session = openSession();
    CDOBranch subBranch = session.getBranchManager().getMainBranch().createBranch("sub2");
    CDOView controlView = openViewWithLockNotifications(session, subBranch);

    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session, controlView, false);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session, controlView, false);
    }

    session.close();
  }

  private void withExplicitRelease(CDOSession session1, CDOView controlView, boolean mustReceiveNotifications) throws Exception
  {
    TestListener2 controlViewListener = new TestListener2(CDOViewLocksChangedEvent.class);
    controlView.addListener(controlViewListener);

    CDOTransaction tx1 = session1.openTransaction();
    CDOResource res1 = tx1.getOrCreateResource(getResourcePath("r1"));
    TestListener2 transactionListener = new TestListener2(CDOViewLocksChangedEvent.class);
    tx1.addListener(transactionListener);
    res1.getContents().clear();
    Company company = getModel1Factory().createCompany();
    res1.getContents().add(company);
    tx1.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompanyInControlView = null;
    if (mustReceiveNotifications)
    {
      cdoCompanyInControlView = controlView.getObject(cdoCompany.cdoID());
    }

    /* Test write lock */

    cdoCompany.cdoWriteLock().lock();
    waitForActiveLockNotifications();
    if (mustReceiveNotifications)
    {
      IEvent[] events = controlViewListener.waitFor(1);
      assertEquals(1, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(0);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0]);
      assertLockOwner(tx1, lockStates[0].getWriteLockOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    cdoCompany.cdoWriteLock().unlock();
    waitForActiveLockNotifications();
    if (mustReceiveNotifications)
    {
      IEvent[] events = controlViewListener.waitFor(2);
      assertEquals(2, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(1);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0]);
      assertNull(lockStates[0].getWriteLockOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    /* Test read lock */

    cdoCompany.cdoReadLock().lock();
    waitForActiveLockNotifications();
    if (mustReceiveNotifications)
    {
      IEvent[] events = controlViewListener.waitFor(3);
      assertEquals(3, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(2);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0]);
      assertEquals(1, lockStates[0].getReadLockOwners().size());
      CDOLockOwner tx1Lo = CDOLockUtil.createLockOwner(tx1);
      assertEquals(true, lockStates[0].getReadLockOwners().contains(tx1Lo));
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    cdoCompany.cdoReadLock().unlock();
    waitForActiveLockNotifications();
    if (mustReceiveNotifications)
    {
      IEvent[] events = controlViewListener.waitFor(4);
      assertEquals(4, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(3);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertEquals(0, lockStates[0].getReadLockOwners().size());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    /* Test write option */

    cdoCompany.cdoWriteOption().lock();
    waitForActiveLockNotifications();
    if (mustReceiveNotifications)
    {
      IEvent[] events = controlViewListener.waitFor(5);
      assertEquals(5, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(4);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0]);
      assertLockOwner(tx1, lockStates[0].getWriteOptionOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    cdoCompany.cdoWriteOption().unlock();
    waitForActiveLockNotifications();
    if (mustReceiveNotifications)
    {
      IEvent[] events = controlViewListener.waitFor(6);
      assertEquals(6, events.length);

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(5);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0]);
      assertNull(lockStates[0].getWriteOptionOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    List<IEvent> events = transactionListener.getEvents();
    assertEquals(6, events.size());

    if (!mustReceiveNotifications)
    {
      assertEquals(0, controlViewListener.getEvents().size());
    }
  }

  private void withAutoRelease(CDOSession session, CDOView controlView, boolean mustReceiveNotifications) throws Exception
  {
    TestListener2 controlViewListener = new TestListener2(CDOViewLocksChangedEvent.class);
    controlView.addListener(controlViewListener);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(true);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("r1"));
    resource.getContents().clear();

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    implicitRelease(company, LockType.WRITE, transaction, controlViewListener, mustReceiveNotifications);
    implicitRelease(company, LockType.READ, transaction, controlViewListener, mustReceiveNotifications);
    implicitRelease(company, LockType.OPTION, transaction, controlViewListener, mustReceiveNotifications);
  }

  private void implicitRelease(Company company, LockType type, CDOTransaction transaction, TestListener2 controlViewListener, boolean mustReceiveNotifications)
      throws Exception
  {
    CDOViewLocksChangedEvent event;
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    company.setName(company.getName() + "x"); // Make object DIRTY.
    cdoCompany.cdoWriteLock().lock();
    waitForActiveLockNotifications();

    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(1);
      event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(0);
      assertEquals(Operation.LOCK, event.getOperation());
      assertEquals(LockType.WRITE, event.getLockType());
    }

    transaction.commit();

    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(2);
      event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(1);
      assertEquals(Operation.UNLOCK, event.getOperation());
      assertNull(event.getLockType());
    }
    else
    {
      sleep(1000);
      assertEquals(0, controlViewListener.getEvents().size());
    }
  }

  public static void assertLockedObject(CDOObject expected, CDOLockState actual)
  {
    Object lockedObject = actual.getLockedObject();
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
    CDOLockOwner lockOwner = CDOLockUtil.createLockOwner(expected);
    assertEquals(lockOwner, actual);
  }

  /**
   * @author Caspar De Groot
   */
  private static enum LockReleaseMode
  {
    EXPLICIT, AUTO
  }
}
