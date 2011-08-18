/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOLocksChangedEvent;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.LinkedList;
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

  public void testSameBranchDifferentSession() throws CommitException
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();
    CDOView controlView = openViewWithLockNotifications(session2, null);
    test(session1, controlView, true);
    session1.close();
    session2.close();
  }

  public void testSameBranchSameSession() throws CommitException
  {
    CDOSession session1 = openSession();
    CDOView controlView = openViewWithLockNotifications(session1, null);
    test(session1, controlView, true);
    session1.close();
  }

  public void testDifferentBranchDifferentSession() throws CommitException
  {
    skipUnlessBranching();

    CDOSession session1 = openSession();
    CDOBranch subBranch = session1.getBranchManager().getMainBranch().createBranch("sub1");
    CDOSession session2 = openSession();
    CDOView controlView = openViewWithLockNotifications(session2, subBranch);
    test(session1, controlView, false);
    session1.close();
    session2.close();
  }

  public void testDifferentBranchSameSession() throws CommitException
  {
    skipUnlessBranching();

    CDOSession session1 = openSession();
    CDOBranch subBranch = session1.getBranchManager().getMainBranch().createBranch("sub2");
    CDOView controlView = openViewWithLockNotifications(session1, subBranch);
    test(session1, controlView, false);
    session1.close();
  }

  private void test(CDOSession session1, CDOView controlView, boolean mustReceiveNotifications) throws CommitException
  {
    CDOTransaction tx1 = session1.openTransaction();
    CDOResource res1 = tx1.getOrCreateResource(getResourcePath("r1"));
    LockEventListener listener1 = new LockEventListener();
    tx1.addListener(listener1);
    res1.getContents().clear();
    Company company1 = getModel1Factory().createCompany();
    res1.getContents().add(company1);
    tx1.commit();

    LockEventListener listener2 = new LockEventListener();
    controlView.addListener(listener2);

    CDOObject cdoCompany1 = CDOUtil.getCDOObject(company1);

    /* Test write lock */

    cdoCompany1.cdoWriteLock().lock();
    if (mustReceiveNotifications)
    {
      listener2.waitFor(1);
      assertEquals(1, listener2.getLockEvents().size());

      CDOLocksChangedEvent event = listener2.getLockEvents().get(0);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany1, lockStates[0].getLockedObject());
      assertLockOwner(tx1, lockStates[0].getWriteLockOwner());
    }

    cdoCompany1.cdoWriteLock().unlock();
    if (mustReceiveNotifications)
    {
      listener2.waitFor(2);

      assertEquals(2, listener2.getLockEvents().size());

      CDOLocksChangedEvent event = listener2.getLockEvents().get(1);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany1, lockStates[0].getLockedObject());
      assertNull(lockStates[0].getWriteLockOwner());
    }

    /* Test read lock */

    cdoCompany1.cdoReadLock().lock();
    if (mustReceiveNotifications)
    {
      listener2.waitFor(3);
      assertEquals(3, listener2.getLockEvents().size());

      CDOLocksChangedEvent event = listener2.getLockEvents().get(2);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany1, lockStates[0].getLockedObject());
      assertEquals(1, lockStates[0].getReadLockOwners().size());
      CDOLockOwner tx1Lo = CDOLockUtil.createLockOwner(tx1);
      assertEquals(true, lockStates[0].getReadLockOwners().contains(tx1Lo));
    }

    cdoCompany1.cdoReadLock().unlock();
    if (mustReceiveNotifications)
    {
      listener2.waitFor(4);

      assertEquals(4, listener2.getLockEvents().size());

      CDOLocksChangedEvent event = listener2.getLockEvents().get(3);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertEquals(0, lockStates[0].getReadLockOwners().size());
    }

    /* Test write option */

    cdoCompany1.cdoWriteOption().lock();
    if (mustReceiveNotifications)
    {
      listener2.waitFor(5);
      assertEquals(5, listener2.getLockEvents().size());

      CDOLocksChangedEvent event = listener2.getLockEvents().get(4);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany1, lockStates[0].getLockedObject());
      assertLockOwner(tx1, lockStates[0].getWriteOptionOwner());
    }

    cdoCompany1.cdoWriteOption().unlock();
    if (mustReceiveNotifications)
    {
      listener2.waitFor(6);

      assertEquals(6, listener2.getLockEvents().size());

      CDOLocksChangedEvent event = listener2.getLockEvents().get(5);
      assertLockOwner(tx1, event.getLockOwner());

      CDOLockState[] lockStates = event.getLockStates();
      assertEquals(1, lockStates.length);
      assertLockedObject(cdoCompany1, lockStates[0].getLockedObject());
      assertNull(lockStates[0].getWriteOptionOwner());
    }

    assertEquals(0, listener1.getLockEvents().size());

    if (!mustReceiveNotifications)
    {
      assertEquals(0, listener2.getLockEvents().size());
    }
  }

  private void assertLockedObject(CDOObject obj, Object lockedObject)
  {
    if (lockedObject instanceof CDOIDAndBranch)
    {
      CDOIDAndBranch idAndBranch = CDOIDUtil.createIDAndBranch(obj.cdoID(), obj.cdoView().getBranch());
      assertEquals(idAndBranch, lockedObject);
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

  /**
   * @author Caspar De Groot
   */
  private final class LockEventListener implements IListener
  {
    private List<CDOLocksChangedEvent> lockEvents = new LinkedList<CDOLocksChangedEvent>();

    public synchronized void notifyEvent(IEvent event)
    {
      if (event instanceof CDOLocksChangedEvent)
      {
        lockEvents.add((CDOLocksChangedEvent)event);
        notify();
      }
    }

    public List<CDOLocksChangedEvent> getLockEvents()
    {
      return lockEvents;
    }

    public synchronized void waitFor(int n)
    {
      long timeout = 2000;
      long t = 0;
      while (lockEvents.size() < n)
      {
        if (timeout <= 0)
        {
          fail("Timed out");
        }
        try
        {
          t = System.currentTimeMillis();
          wait(timeout);
        }
        catch (InterruptedException ex)
        {
        }
        timeout -= System.currentTimeMillis() - t;
      }
    }
  }

  public void testEnableDisableNotifications() throws CommitException
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();
    CDOView controlView = session2.openView();
    test(session1, controlView, false);

    controlView.options().setLockNotificationEnabled(true);
    test(session1, controlView, true);

    controlView.options().setLockNotificationEnabled(false);
    test(session1, controlView, false);

    session1.close();
    session2.close();
  }

  public void testLockStateHeldByDurableView() throws CommitException
  {
    {
      CDOSession session1 = openSession();
      CDOTransaction tx1 = session1.openTransaction();
      tx1.enableDurableLocking(true);
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
    assertSame(CDOLockOwner.UNKNOWN, cdoObj.cdoLockState().getWriteLockOwner());
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
    assertNull(cdoObj.cdoLockState());

    tx1.commit();
    assertClean(cdoObj, tx1);
    assertNotNull(cdoObj.cdoLockState());

    res1.getContents().add(company1);
    tx1.commit();
  }
}
