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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Eike Stepper
 */
public class DurableLockReleaseTest extends AbstractCDOTest
{
  /**
   * Unfortunately this test case uses sleep() calls to make sure that asynchronous
   * notification signals have (hopefully) enough time to reach their destination
   * and be processed when respective assertions are made. It would be better to
   * implement correct waitFor() idioms, but certainly a lot more effort...
   */
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

    // view2.refreshLockStates(null);

    sleep(200);
    assertTrue("The resource should be locked!", resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().unlock();

    sleep(200);
    assertFalse("The resource should have been unlocked!", resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().lock(100);

    sleep(200);
    assertTrue(resFromView.cdoWriteLock().isLockedByOthers());

    // Reopen session
    session.close();
    session = openSession();
    tx = session.openTransaction(durableLockID);
    sleep(200);

    resFromTX = tx.getResource(path);
    resFromTX.cdoWriteLock().unlock();

    sleep(200);
    assertFalse(resFromView.cdoWriteLock().isLockedByOthers());

    resFromTX.cdoWriteLock().lock(100);

    sleep(200);
    assertTrue(resFromView.cdoWriteLock().isLockedByOthers());
  }
}
