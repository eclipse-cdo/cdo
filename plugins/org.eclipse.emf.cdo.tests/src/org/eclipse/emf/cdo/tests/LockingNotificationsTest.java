/*
 * Copyright (c) 2011-2013, 2015 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.util.TestListener2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;

import java.util.List;

/**
 * @author Caspar De Groot
 */
public class LockingNotificationsTest extends AbstractLockingTest
{
  private CDOView openViewWithLockNotifications(CDOSession session, CDOBranch branch)
  {
    CDOView view = branch != null ? session.openView(branch) : session.openView();
    view.options().setLockNotificationEnabled(true);
    return view;
  }

  /**
   * FIXME Disabled until bug 358603 is addressed.
   */
  public void _testSameBranchDifferentSession_explicitRelease() throws CommitException
  {
    sameBranchDifferentSession(LockReleaseMode.EXPLICIT);
  }

  public void testSameBranchDifferentSession_autoRelease() throws CommitException
  {
    sameBranchDifferentSession(LockReleaseMode.AUTO);
  }

  private void sameBranchDifferentSession(LockReleaseMode mode) throws CommitException
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();
    CDOView controlView = openViewWithLockNotifications(session2, null);
    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session1, controlView, true);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session1, controlView, true);
    }

    session1.close();
    session2.close();
  }

  public void testSameBranchSameSession_explicitRelease() throws CommitException
  {
    sameBranchSameSession(LockReleaseMode.EXPLICIT);
  }

  public void testSameBranchSameSession_autoRelease() throws CommitException
  {
    sameBranchSameSession(LockReleaseMode.AUTO);
  }

  private void sameBranchSameSession(LockReleaseMode mode) throws CommitException
  {
    CDOSession session1 = openSession();
    CDOView controlView = openViewWithLockNotifications(session1, null);
    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session1, controlView, true);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session1, controlView, true);
    }

    session1.close();
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchDifferentSession() throws CommitException
  {
    differentBranchDifferentSession(LockReleaseMode.EXPLICIT);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchDifferentSession_autoRelease() throws CommitException
  {
    differentBranchDifferentSession(LockReleaseMode.AUTO);
  }

  private void differentBranchDifferentSession(LockReleaseMode mode) throws CommitException
  {
    CDOSession session1 = openSession();
    CDOBranch subBranch = session1.getBranchManager().getMainBranch().createBranch("sub1");
    CDOSession session2 = openSession();
    CDOView controlView = openViewWithLockNotifications(session2, subBranch);
    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session1, controlView, false);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session1, controlView, false);
    }

    session1.close();
    session2.close();
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchSameSession() throws CommitException
  {
    differentBranchSameSession(LockReleaseMode.EXPLICIT);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDifferentBranchSameSession_autoRelease() throws CommitException
  {
    differentBranchSameSession(LockReleaseMode.AUTO);
  }

  private void differentBranchSameSession(LockReleaseMode mode) throws CommitException
  {
    CDOSession session1 = openSession();
    CDOBranch subBranch = session1.getBranchManager().getMainBranch().createBranch("sub2");
    CDOView controlView = openViewWithLockNotifications(session1, subBranch);
    if (mode == LockReleaseMode.EXPLICIT)
    {
      withExplicitRelease(session1, controlView, false);
    }
    else if (mode == LockReleaseMode.AUTO)
    {
      withAutoRelease(session1, controlView, false);
    }

    session1.close();
  }

  private void withExplicitRelease(CDOSession session1, CDOView controlView, boolean mustReceiveNotifications)
      throws CommitException
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
    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(1);
      assertEquals(1, controlViewListener.getEvents().size());

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(0);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0].getLockedObject());
      assertLockOwner(tx1, lockStates[0].getWriteLockOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    cdoCompany.cdoWriteLock().unlock();
    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(2);

      assertEquals(2, controlViewListener.getEvents().size());

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(1);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0].getLockedObject());
      assertNull(lockStates[0].getWriteLockOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    /* Test read lock */

    cdoCompany.cdoReadLock().lock();
    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(3);
      assertEquals(3, controlViewListener.getEvents().size());

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(2);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0].getLockedObject());
      assertEquals(1, lockStates[0].getReadLockOwners().size());
      CDOLockOwner tx1Lo = CDOLockUtil.createLockOwner(tx1);
      assertEquals(true, lockStates[0].getReadLockOwners().contains(tx1Lo));
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    cdoCompany.cdoReadLock().unlock();
    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(4);

      assertEquals(4, controlViewListener.getEvents().size());

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(3);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertEquals(0, lockStates[0].getReadLockOwners().size());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    /* Test write option */

    cdoCompany.cdoWriteOption().lock();
    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(5);
      assertEquals(5, controlViewListener.getEvents().size());

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(4);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0].getLockedObject());
      assertLockOwner(tx1, lockStates[0].getWriteOptionOwner());
      assertEquals(cdoCompanyInControlView.cdoLockState(), lockStates[0]);
    }

    cdoCompany.cdoWriteOption().unlock();
    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(6);

      assertEquals(6, controlViewListener.getEvents().size());

      CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(5);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany, lockStates[0].getLockedObject());
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

  private void withAutoRelease(CDOSession session1, CDOView controlView, boolean mustReceiveNotifications)
      throws CommitException
  {
    TestListener2 controlViewListener = new TestListener2(CDOViewLocksChangedEvent.class);
    controlView.addListener(controlViewListener);

    CDOTransaction tx1 = session1.openTransaction();
    tx1.options().setAutoReleaseLocksEnabled(true);

    CDOResource res1 = tx1.getOrCreateResource(getResourcePath("r1"));
    res1.getContents().clear();

    Company company = getModel1Factory().createCompany();
    res1.getContents().add(company);
    tx1.commit();

    implicitRelease(company, LockType.WRITE, tx1, controlViewListener, mustReceiveNotifications);
    implicitRelease(company, LockType.READ, tx1, controlViewListener, mustReceiveNotifications);
    implicitRelease(company, LockType.OPTION, tx1, controlViewListener, mustReceiveNotifications);
  }

  private void implicitRelease(Company company, LockType type, CDOTransaction tx, TestListener2 controlViewListener,
      boolean mustReceiveNotifications) throws CommitException
  {
    CDOViewLocksChangedEvent e;
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    company.setName(company.getName() + "x"); // Make name field dirty
    cdoCompany.cdoWriteLock().lock();

    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(1);
      e = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(0);
      assertEquals(Operation.LOCK, e.getOperation());
      assertEquals(LockType.WRITE, e.getLockType());
    }

    tx.commit();

    if (mustReceiveNotifications)
    {
      controlViewListener.waitFor(2);
      e = (CDOViewLocksChangedEvent)controlViewListener.getEvents().get(1);
      assertEquals(Operation.UNLOCK, e.getOperation());
      assertNull(e.getLockType());
    }

    if (!mustReceiveNotifications)
    {
      assertEquals(0, controlViewListener.getEvents().size());
    }
  }

  private void assertLockedObject(CDOObject obj, Object lockedObject)
  {
    if (lockedObject instanceof CDOIDAndBranch)
    {
      CDOIDAndBranch idAndBranch = (CDOIDAndBranch)lockedObject;
      assertEquals(obj.cdoID(), idAndBranch.getID());
      assertEquals(obj.cdoView().getBranch().getID(), idAndBranch.getBranch().getID());
    }
    else if (lockedObject instanceof CDOID)
    {
      assertEquals(obj.cdoID(), lockedObject);
    }
  }

  private void assertLockOwner(CDOView view, CDOLockOwner lockOwner)
  {
    CDOLockOwner lo = CDOLockUtil.createLockOwner(view);
    assertEquals(lo, lockOwner);
  }

  public void testEnableDisableNotifications() throws CommitException
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

  public void testLockStateHeldByDurableView() throws CommitException
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

  public void testLockStateNewAndTransient() throws CommitException
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

  /**
   * @author Caspar De Groot
   */
  private static enum LockReleaseMode
  {
    EXPLICIT, AUTO
  }
}
