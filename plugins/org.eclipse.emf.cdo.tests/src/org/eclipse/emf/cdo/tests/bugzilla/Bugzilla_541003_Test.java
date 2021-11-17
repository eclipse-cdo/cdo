/*
 * Copyright (c) 2018, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Collections;

/**
 * Bug 541003 - LockStatePrefetcher does not update other views.
 *
 * @author Eike Stepper
 */
@Requires(ISessionConfig.CAPABILITY_NET4J)
public class Bugzilla_541003_Test extends AbstractCDOTest
{
  public void testLockStateWithPrefetcher() throws Exception
  {
    run(true);
  }

  public void testLockStateWithoutPrefetcher() throws Exception
  {
    run(false);
  }

  private void run(boolean lockStatePrefetchEnabled) throws Exception
  {
    CDOLock lock = prepareLock();
    lock.lock();

    CDONet4jSession session = (CDONet4jSession)openSession();

    CDOView view1 = openView(session, lockStatePrefetchEnabled);
    CDOView view2 = openView(session, false);

    // With lockStatePrefetchEnabled the lock state is loaded here:
    CDOLock lock1 = loadLock(view1);
    CDOLock lock2 = loadLock(view2);
    assertEquals(lockStatePrefetchEnabled, hasLockState(lock1));
    assertEquals(lockStatePrefetchEnabled, hasLockState(lock2));

    // Without lockStatePrefetchEnabled the lock state is loaded here:
    assertTrue(lock1.isLockedByOthers());
    assertTrue(lock2.isLockedByOthers());
    assertEquals(true, hasLockState(lock1));
    assertEquals(true, hasLockState(lock2));
  }

  private CDOLock prepareLock() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    return CDOUtil.getCDOObject(company).cdoWriteLock();
  }

  private CDOView openView(CDOSession session, boolean lockStatePrefetchEnabled)
  {
    CDOView view = session.openView();

    if (lockStatePrefetchEnabled)
    {
      new CDOLockStatePrefetcher(view, false);
    }

    return view;
  }

  private CDOLock loadLock(CDOView view)
  {
    CDOResource resource = view.getResource(getResourcePath("res"));
    Company company = (Company)resource.getContents().get(0);
    return CDOUtil.getCDOObject(company).cdoWriteLock();
  }

  private boolean hasLockState(CDOLock lock)
  {
    CDOObject object = lock.getObject();
    InternalCDOView view = (InternalCDOView)object.cdoView();

    CDOLockState[] lockStates = view.getLockStates(Collections.singleton(object.cdoID()), false);
    return lockStates != null && lockStates.length == 1;
  }
}
