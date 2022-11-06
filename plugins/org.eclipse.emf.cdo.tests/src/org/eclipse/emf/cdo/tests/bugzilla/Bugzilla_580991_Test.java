/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
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

    String durableLockID = tx.enableDurableLocking();

    CDOView view = openSession().openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    view.options().setLockNotificationEnabled(true);
    CDOResource resFromView = view.getResource(path);

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

    tx.disableDurableLocking(true);
    assertNoTimeout(() -> !resFromView.cdoWriteLock().isLockedByOthers());
  }
}
