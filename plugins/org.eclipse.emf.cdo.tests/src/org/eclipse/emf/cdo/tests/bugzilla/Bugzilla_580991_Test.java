/*
 * Copyright (c) 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Bug 580991 - Outdated lock state when releasing a durable lock.
 *
 * @author Eike Stepper
 */
public class Bugzilla_580991_Test extends AbstractCDOTest
{
  public void testDurableLockRelease() throws Exception
  {
    String path = getResourcePath("someRes");

    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resFromTX = tx.createResource(path);
    tx.commit();
    resFromTX.cdoWriteLock().lock(100);

    CDOSession observerSession = openSession();
    CDOView view = observerSession.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    view.options().setLockNotificationEnabled(true);
    CDOResource resFromView = view.getResource(path);

    Session observerServerSession = (Session)serverSession(observerSession);
    long lockModCountBeforeEnable = observerServerSession.getLockModCount();
    String durableLockID = tx.enableDurableLocking();
    assertEquals(lockModCountBeforeEnable + 1, observerServerSession.getLockModCount());

    // Reopen transaction
    tx.close();
    tx = session.openTransaction(durableLockID);
    resFromTX = tx.getResource(path);
    assertTrue(resFromTX.cdoWriteLock().isLocked());
    assertNoTimeout(() -> resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().unlock();
    assertNoTimeout(() -> !resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().lock(100);
    assertNoTimeout(() -> resFromView.cdoWriteLock().isLockedByOthers());

    // Reopen session + transaction
    session.close();
    session = openSession();
    tx = session.openTransaction(durableLockID);
    resFromTX = tx.getResource(path);
    assertTrue(resFromTX.cdoWriteLock().isLocked());
    assertNoTimeout(() -> resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().unlock();
    assertNoTimeout(() -> !resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().lock(100);
    assertNoTimeout(() -> resFromView.cdoWriteLock().isLockedByOthers());

    long lockModCountBeforeDisable = observerServerSession.getLockModCount();

    tx.disableDurableLocking(true);

    assertEquals(lockModCountBeforeDisable + 2, observerServerSession.getLockModCount());
    assertNoTimeout(() -> !resFromView.cdoWriteLock().isLockedByOthers());
  }
}
