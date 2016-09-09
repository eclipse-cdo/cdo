/*
 * Copyright (c) 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class Bugzilla_469301_Test extends AbstractCDOTest
{
  public void testLockDetachedObject() throws Exception
  {
    Company company0 = getModel1Factory().createCompany();
    Company company1 = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(company0);
    resource.getContents().add(company1);
    transaction.commit();

    resource.getContents().remove(company0);
    resource.getContents().remove(company1);

    List<? extends CDOObject> companies = CDOUtil.getCDOObjects(company0, company1);
    transaction.lockObjects(companies, LockType.WRITE, DEFAULT_TIMEOUT);

    CDOLockState[] lockStates = transaction.getLockStatesOfObjects(companies);
    assertEquals(2, lockStates.length);

    for (CDOLockState lockState : lockStates)
    {
      assertEquals(transaction, lockState.getWriteLockOwner());

      LockState<Object, IView> serverLockState = serverLockState(session, lockState);
      assertEquals(serverTransaction(transaction), serverLockState.getWriteLockOwner());
    }
  }
}
