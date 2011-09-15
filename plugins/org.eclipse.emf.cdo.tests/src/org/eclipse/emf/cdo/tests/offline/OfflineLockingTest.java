/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.syncing.OfflineClone;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestListener2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Caspar De Groot
 */
public class OfflineLockingTest extends AbstractSyncingTest
{
  public void testLockAndUnlockThrough() throws Exception
  {
    assertEquals(true, getRepository("repo1") instanceof OfflineClone);

    CDOSession masterSession = openSession("master");
    CDOSession cloneSession = openSession("repo1");
    waitForOnline(cloneSession.getRepositoryInfo());

    CDOTransaction cloneTx = cloneSession.openTransaction();
    cloneTx.enableDurableLocking(true);

    CDOResource res = cloneTx.createResource(getResourcePath("test"));
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    cloneTx.commit();
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    CDOView masterView = masterSession.openView();
    masterView.options().setLockNotificationEnabled(true);
    TestListener2 masterViewListener = new TestListener2(CDOViewLocksChangedEvent.class);
    masterView.addListener(masterViewListener);
    CDOObject cdoCompanyOnMaster = masterView.getObject(cdoCompany.cdoID());

    cdoCompany.cdoWriteLock().lock();
    masterViewListener.waitFor(1);
    assertEquals(true, cdoCompanyOnMaster.cdoWriteLock().isLockedByOthers());

    cdoCompany.cdoWriteLock().unlock();
    masterViewListener.waitFor(2);
    assertEquals(false, cdoCompanyOnMaster.cdoWriteLock().isLockedByOthers());

    cloneSession.close();
    masterSession.close();
  }

  public void testCloneLocks_arrivalInOtherClone() throws Exception
  {
    // Create a 2nd clone repository
    assertEquals(true, getRepository("repo1") instanceof OfflineClone);
    assertEquals(true, getRepository("repo2") instanceof OfflineClone);

    CDOSession clone1Session = openSession("repo1");
    TestListener2 session1lockListener = new TestListener2(CDOSessionLocksChangedEvent.class, "session1lockListener");
    clone1Session.addListener(session1lockListener);
    waitForOnline(clone1Session.getRepositoryInfo());

    CDOSession clone2Session = openSession("repo2");
    TestListener2 session2invalidationListener = new TestListener2(CDOSessionInvalidationEvent.class,
        "session2invalidationListener");
    clone2Session.addListener(session2invalidationListener);
    TestListener2 session2lockListener = new TestListener2(CDOSessionLocksChangedEvent.class, "session2lockListener");
    clone2Session.addListener(session2lockListener);
    waitForOnline(clone2Session.getRepositoryInfo());

    CDOTransaction clone1Tx = openTransaction(clone1Session);

    CDOResource resourceInSession1 = clone1Tx.createResource(getResourcePath("test"));
    Company companyA = getModel1Factory().createCompany();
    Company companyB = getModel1Factory().createCompany();
    Company companyC = getModel1Factory().createCompany();
    resourceInSession1.getContents().add(companyA);
    resourceInSession1.getContents().add(companyB);
    resourceInSession1.getContents().add(companyC);
    clone1Tx.commit();

    CDOObject companyA_session1 = CDOUtil.getCDOObject(companyA);
    CDOObject companyB_session1 = CDOUtil.getCDOObject(companyB);
    CDOObject companyC_session1 = CDOUtil.getCDOObject(companyC);

    session2invalidationListener.setTimeout(Integer.MAX_VALUE);
    session2invalidationListener.waitFor(1); // Wait for the commit notification

    CDOTransaction clone2Tx = openTransaction(clone2Session);

    CDOResource resourceInSession2 = clone2Tx.getResource(getResourcePath("test"));
    CDOObject companyA_session2 = CDOUtil.getCDOObject(resourceInSession2.getContents().get(0));
    CDOObject companyB_session2 = CDOUtil.getCDOObject(resourceInSession2.getContents().get(1));
    CDOObject companyC_session2 = CDOUtil.getCDOObject(resourceInSession2.getContents().get(2));

    CDOSessionLocksChangedEvent e;

    // Verify that thusfar we haven't received any locking events in session 1
    assertEquals(0, session1lockListener.getEvents().size());

    // Perform the lock in session 2, connected to clone 2
    companyA_session2.cdoWriteLock().lock();

    // Wait for the lock notification in session 1, which is connected to clone 1
    session1lockListener.waitFor(1);

    e = (CDOSessionLocksChangedEvent)session1lockListener.getEvents().get(0);
    assertSame(LockType.WRITE, e.getLockType());
    assertSame(Operation.LOCK, e.getOperation());
    assertEquals(true, companyA_session1.cdoWriteLock().isLockedByOthers());

    // Perform the unlock in session 2, connected to clone 2
    companyA_session2.cdoWriteLock().unlock();

    // Wait for the lock notification in session 1, which is connected to clone 1
    session1lockListener.waitFor(2);

    e = (CDOSessionLocksChangedEvent)session1lockListener.getEvents().get(1);
    assertSame(LockType.WRITE, e.getLockType());
    assertSame(Operation.UNLOCK, e.getOperation());
    assertEquals(false, companyA_session1.cdoWriteLock().isLockedByOthers());

    // Now vice versa . . .

    session2lockListener.getEvents().clear();

    // Perform the lock in session 1, connected to clone 1
    companyA_session1.cdoWriteLock().lock();

    // Wait for the lock notification in session 2, which is connected to clone 2
    session2lockListener.waitFor(1);

    e = (CDOSessionLocksChangedEvent)session2lockListener.getEvents().get(0);
    assertSame(LockType.WRITE, e.getLockType());
    assertSame(Operation.LOCK, e.getOperation());
    assertEquals(true, companyA_session2.cdoWriteLock().isLockedByOthers());

    // Perform the unlock in session 1, connected to clone 1
    companyA_session1.cdoWriteLock().unlock();

    // Wait for the lock notification in session 1, which is connected to clone 1
    session2lockListener.waitFor(2);

    e = (CDOSessionLocksChangedEvent)session2lockListener.getEvents().get(1);
    assertSame(LockType.WRITE, e.getLockType());
    assertSame(Operation.UNLOCK, e.getOperation());
    assertEquals(false, companyA_session2.cdoWriteLock().isLockedByOthers());

    // Now try an unlock-all . . .

    session1lockListener.getEvents().clear();

    companyA_session2.cdoReadLock().lock();
    companyB_session2.cdoWriteLock().lock();
    companyC_session2.cdoWriteOption().lock();

    session1lockListener.waitFor(3);

    assertEquals(true, companyA_session1.cdoReadLock().isLockedByOthers());
    assertEquals(true, companyB_session1.cdoWriteLock().isLockedByOthers());
    assertEquals(true, companyC_session1.cdoWriteOption().isLockedByOthers());

    clone2Tx.unlockObjects();

    session1lockListener.waitFor(4);

    assertEquals(false, companyA_session1.cdoReadLock().isLockedByOthers());
    assertEquals(false, companyA_session1.cdoWriteLock().isLockedByOthers());
    assertEquals(false, companyA_session1.cdoWriteOption().isLockedByOthers());

    clone1Session.close();
    clone2Session.close();
  }

  public void testCloneLocks_replicationToOtherClone() throws CommitException
  {
    InternalRepository repo1 = getRepository("repo1");
    assertEquals(true, repo1 instanceof OfflineClone);
    InternalRepository repo2 = getRepository("repo2");
    assertEquals(true, repo2 instanceof OfflineClone);

    OfflineClone clone2 = (OfflineClone)repo2;

    waitForOnline(getRepository("repo1"));
    waitForOnline(getRepository("repo2"));

    CDOSession clone1session = openSession("repo1");

    // Store 3 objects in repo1
    CDOTransaction tx1_sess1 = openTransaction(clone1session);
    CDOResource resource_tx1_sess1 = tx1_sess1.createResource(getResourcePath("test"));
    Company companyA = getModel1Factory().createCompany();
    Company companyB = getModel1Factory().createCompany();
    Company companyC = getModel1Factory().createCompany();
    resource_tx1_sess1.getContents().add(companyA);
    resource_tx1_sess1.getContents().add(companyB);
    resource_tx1_sess1.getContents().add(companyC);
    tx1_sess1.commit();

    {
      // Verify that they're visible in repo2
      CDOSession clone2session = openSession("repo2");
      CDOTransaction tx1_sess2 = openTransaction(clone2session);
      CDOResource resource_tx1_sess2 = tx1_sess2.getResource(getResourcePath("test"));
      assertEquals(3, resource_tx1_sess2.getContents().size());
      tx1_sess2.close();
      clone2session.close();
    }

    clone2.goOffline();
    waitForOffline(clone2);

    // Lock the objects in repo1. Since repo1 is ONLINE, this will also lock them
    // in the master.
    CDOUtil.getCDOObject(companyA).cdoReadLock().lock();
    CDOUtil.getCDOObject(companyB).cdoWriteLock().lock();
    CDOUtil.getCDOObject(companyC).cdoWriteOption().lock();

    clone2.goOnline();
    waitForOnline(clone2);

    {
      // Verify that the locks are visible in repo2
      CDOSession clone2session = openSession("repo2");
      CDOTransaction tx1_sess2 = openTransaction(clone2session);
      CDOResource resource_tx1_sess2 = tx1_sess2.getResource(getResourcePath("test"));
      EList<EObject> contents = resource_tx1_sess2.getContents();
      CDOObject companyA_in_sess2 = CDOUtil.getCDOObject(contents.get(0));
      CDOObject companyB_in_sess2 = CDOUtil.getCDOObject(contents.get(1));
      CDOObject companyC_in_sess2 = CDOUtil.getCDOObject(contents.get(2));

      assertEquals(true, companyA_in_sess2.cdoReadLock().isLockedByOthers());
      assertEquals(true, companyB_in_sess2.cdoWriteLock().isLockedByOthers());
      assertEquals(true, companyC_in_sess2.cdoWriteOption().isLockedByOthers());

      tx1_sess2.close();
      clone2session.close();
    }

    clone2.goOffline();
    waitForOffline(clone2);

    // Unlock the objects in repo1. Since repo1 is ONLINE, this will also lock them
    // in the master.
    CDOUtil.getCDOObject(companyA).cdoReadLock().unlock();
    CDOUtil.getCDOObject(companyB).cdoWriteLock().unlock();
    CDOUtil.getCDOObject(companyC).cdoWriteOption().unlock();

    clone2.goOnline();
    waitForOnline(clone2);

    {
      // Verify in repo2
      CDOSession clone2session = openSession("repo2");
      CDOTransaction tx1_sess2 = openTransaction(clone2session);
      CDOResource resource_tx1_sess2 = tx1_sess2.getResource(getResourcePath("test"));
      EList<EObject> contents = resource_tx1_sess2.getContents();
      CDOObject companyA_in_sess2 = CDOUtil.getCDOObject(contents.get(0));
      CDOObject companyB_in_sess2 = CDOUtil.getCDOObject(contents.get(1));
      CDOObject companyC_in_sess2 = CDOUtil.getCDOObject(contents.get(2));

      assertEquals(false, companyA_in_sess2.cdoReadLock().isLockedByOthers());
      assertEquals(false, companyB_in_sess2.cdoWriteLock().isLockedByOthers());
      assertEquals(false, companyC_in_sess2.cdoWriteOption().isLockedByOthers());

      tx1_sess2.close();
      clone2session.close();
    }

    clone1session.close();
  }

  private static CDOTransaction openTransaction(CDOSession session)
  {
    CDOTransaction tx = session.openTransaction();
    tx.options().setLockNotificationEnabled(true);
    tx.enableDurableLocking(true);
    return tx;
  }
}
