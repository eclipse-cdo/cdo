/*
 * Copyright (c) 2011, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.syncing.OfflineClone;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.tests.TestListener2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Caspar De Groot
 */
public class OfflineLockReplicationTest extends AbstractSyncingTest
{
  public void testCloneLocks_replicationToOtherClone_newSession() throws CommitException
  {
    testCloneLocks_replicationToOtherClone(false);
  }

  public void testCloneLocks_replicationToOtherClone_sameSession() throws CommitException
  {
    testCloneLocks_replicationToOtherClone(true);
  }

  private void testCloneLocks_replicationToOtherClone(boolean keepSession) throws CommitException
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

    CDOTransaction tx1_sess2;
    {
      // Verify that they're visible in repo2
      tx1_sess2 = openTransactionWithLockListener("repo2");
      CDOResource resource_tx1_sess2 = tx1_sess2.getResource(getResourcePath("test"));
      assertEquals(3, resource_tx1_sess2.getContents().size());

      if (!keepSession)
      {
        tx1_sess2.getSession().close();
        tx1_sess2 = null;
      }
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
      if (!keepSession)
      {
        tx1_sess2 = openTransactionWithLockListener("repo2");
      }
      else
      {
        ((TestListener2)tx1_sess2.getListeners()[0]).waitFor(1);
      }

      CDOResource resource_tx1_sess2 = tx1_sess2.getResource(getResourcePath("test"));
      EList<EObject> contents = resource_tx1_sess2.getContents();
      CDOObject companyA_in_sess2 = CDOUtil.getCDOObject(contents.get(0));
      CDOObject companyB_in_sess2 = CDOUtil.getCDOObject(contents.get(1));
      CDOObject companyC_in_sess2 = CDOUtil.getCDOObject(contents.get(2));

      assertEquals(true, companyA_in_sess2.cdoReadLock().isLockedByOthers());
      assertEquals(true, companyB_in_sess2.cdoWriteLock().isLockedByOthers());
      assertEquals(true, companyC_in_sess2.cdoWriteOption().isLockedByOthers());

      if (!keepSession)
      {
        tx1_sess2.getSession().close();
        tx1_sess2 = null;
      }
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
      if (!keepSession)
      {
        tx1_sess2 = openTransactionWithLockListener("repo2");
      }
      else
      {
        ((TestListener2)tx1_sess2.getListeners()[0]).waitFor(2);
      }

      CDOResource resource_tx1_sess2 = tx1_sess2.getResource(getResourcePath("test"));
      EList<EObject> contents = resource_tx1_sess2.getContents();
      CDOObject companyA_in_sess2 = CDOUtil.getCDOObject(contents.get(0));
      CDOObject companyB_in_sess2 = CDOUtil.getCDOObject(contents.get(1));
      CDOObject companyC_in_sess2 = CDOUtil.getCDOObject(contents.get(2));

      assertEquals(false, companyA_in_sess2.cdoReadLock().isLockedByOthers());
      assertEquals(false, companyB_in_sess2.cdoWriteLock().isLockedByOthers());
      assertEquals(false, companyC_in_sess2.cdoWriteOption().isLockedByOthers());

      tx1_sess2.getSession().close();
    }

    clone1session.close();
  }

  private CDOTransaction openTransactionWithLockListener(String repoName)
  {
    CDOSession session = openSession(repoName);
    CDOTransaction tx = openTransaction(session);
    tx.addListener(new TestListener2(CDOViewLocksChangedEvent.class));
    return tx;
  }
}
