/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Schedl - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractLockingTest;
import org.eclipse.emf.cdo.tests.LockingNotificationsTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * Bug 415077 - Transaction could not be commited if a new object is locked.
 * <p>
 * Testcase adapted from {@link LockingNotificationsTest#testLockStateNewAndTransient()}.
 * 
 * @author Stefan Schedl
 */
public class Bugzilla_415077_Test extends AbstractLockingTest
{
  public void testReadLockOnNewAndTransientObject() throws CommitException
  {
    CDOObject company = CDOUtil.getCDOObject(getModel1Factory().createCompany());
    assertTransient(company);
    assertNull(company.cdoLockState());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("r1"));
    resource.getContents().add(company);
    assertNew(company, transaction);
    assertNotNull(company.cdoLockState());

    company.cdoReadLock().lock();

    resource.getContents().remove(company);
    assertTransient(company);
    assertNull(company.cdoLockState());

    resource.getContents().add(company);
    transaction.commit();
    assertClean(company, transaction);
    assertNotNull(company.cdoLockState());

    resource.getContents().remove(company);
    assertTransient(company);
    assertNull(company.cdoLockState());
  }
}
