/*
 * Copyright (c) 2016, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractLockingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * @author Eike Stepper
 */
public class Bugzilla_387563b_Test extends AbstractLockingTest
{
  public void testNoImplicitLockingOfNewObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(true);

    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    resource.getContents().add(company);

    CDOObject cdoObject = CDOUtil.getCDOObject(company);
    CDOLockState lockState = cdoObject.cdoLockState();
    assertNull(lockState.getWriteLockOwner());

    transaction.commit();
    assertWriteLock(false, company);
  }

  public void testExplicitLockingOfNewObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(true);

    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    resource.getContents().add(company);

    CDOObject cdoObject = CDOUtil.getCDOObject(company);
    cdoObject.cdoWriteLock().lock();

    CDOLockState lockState = cdoObject.cdoLockState();
    assertEquals(((InternalCDOTransaction)transaction).getLockOwner(), lockState.getWriteLockOwner());

    transaction.commit();
    assertWriteLock(false, company);
  }

  public void testExplicitLockingOfNewObjectExemption() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.options().addAutoReleaseLocksExemptions(false, company);

    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    resource.getContents().add(company);

    CDOObject cdoObject = CDOUtil.getCDOObject(company);
    cdoObject.cdoWriteLock().lock();

    CDOLockState lockState = cdoObject.cdoLockState();
    assertEquals(((InternalCDOTransaction)transaction).getLockOwner(), lockState.getWriteLockOwner());

    transaction.commit();
    assertWriteLock(true, company);
  }

  public void testExplicitLockingAndUnlockingOfNewObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.options().addAutoReleaseLocksExemptions(false, company);

    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    resource.getContents().add(company);

    CDOObject cdoObject = CDOUtil.getCDOObject(company);
    cdoObject.cdoWriteLock().lock();

    CDOLockState lockState = cdoObject.cdoLockState();
    assertEquals(((InternalCDOTransaction)transaction).getLockOwner(), lockState.getWriteLockOwner());

    cdoObject.cdoWriteLock().unlock();
    lockState = cdoObject.cdoLockState();
    assertNull(lockState.getWriteLockOwner());

    transaction.commit();
    assertWriteLock(false, company);
  }
}
