/*
 * Copyright (c) 2014, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * Bug 449665 about {@link CDOLockState} on {@link CDOResource} in {@link CDOState#PROXY} state.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_449665_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test {@link CDOObject#cdoLockState()} on a simple {@link CDOObject} in {@link CDOState#PROXY} state.
   */
  public void testCDOObject_GetLockState() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.getResourceSet().eAdapters().add(new EContentAdapterQueringCDOLockState());
    CDOResource resource2 = transaction2.getResource(getResourcePath(RESOURCE_NAME));

    // Make company in CDOState.PROXY by remote change
    company.setName("oneChange");
    resource1.getContents().add(getModel1Factory().createCategory());
    commitAndSync(transaction1, transaction2);

    company = (Company)resource2.getContents().get(0);
    CDOObject companyCDOObject = CDOUtil.getCDOObject(company);
    CDOLockState lockState = companyCDOObject.cdoLockState();
    Object expectedLockedObject = companyCDOObject.cdoID();
    if (session1.getRepositoryInfo().isSupportingBranches())
    {
      expectedLockedObject = CDOIDUtil.createIDAndBranch(companyCDOObject.cdoID(), transaction2.getBranch());
    }

    assertEquals(expectedLockedObject, lockState.getLockedObject());
    assertTrue(lockState.getReadLockOwners().isEmpty());
    assertNull(lockState.getWriteLockOwner());
    assertNull(lockState.getWriteOptionOwner());
  }

  /**
   * Test {@link CDOObject#cdoLockState()} on a {@link CDOResource} in {@link CDOState#PROXY} state.
   */
  public void testCDOResource_GetLockState() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.getResourceSet().eAdapters().add(new EContentAdapterQueringCDOLockState());
    CDOResource resource2 = transaction2.getResource(getResourcePath(RESOURCE_NAME));

    // Make CDOResouce in CDOState.PROXY by remote change
    resource1.getContents().add(getModel1Factory().createCategory());
    commitAndSync(transaction1, transaction2);

    CDOLockState lockState = resource2.cdoLockState();
    Object expectedLockedObject = resource2.cdoID();
    if (session1.getRepositoryInfo().isSupportingBranches())
    {
      expectedLockedObject = CDOIDUtil.createIDAndBranch(resource2.cdoID(), transaction2.getBranch());
    }

    assertEquals(expectedLockedObject, lockState.getLockedObject());
    assertTrue(lockState.getReadLockOwners().isEmpty());
    assertNull(lockState.getWriteLockOwner());
    assertNull(lockState.getWriteOptionOwner());
  }

  /**
   * A {@link EContentAdapter} to get {@link CDOLockState} for each loaded object.
   */
  private static class EContentAdapterQueringCDOLockState extends EContentAdapter
  {
    @Override
    protected void addAdapter(Notifier notifier)
    {
      if (notifier instanceof EObject)
      {
        EObject eObject = (EObject)notifier;
        CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
        if (cdoObject != null)
        {
          cdoObject.cdoLockState();
        }
      }

      super.addAdapter(notifier);
    }
  }
}
