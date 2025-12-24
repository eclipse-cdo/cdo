/*
 * Copyright (c) 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 *
 * Bug 464036 : NPE on CDOObject.eContainer()/eResource() called when the container/resource has been remotely deleted.
 *
 * @author Esteban Dugueperoux
 */
public class CDOStaleReferencePolicyTest extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test {@link EObject#eContainer()} call while the container has been remotely deleted.
   */
  public void testCDOStaleReferencePolicyWithCDOStoreGetContainer() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company companyFromTx1 = getModel1Factory().createCompany();
    resource1.getContents().add(companyFromTx1);
    transaction1.commit();

    // Test
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath(RESOURCE_NAME));
    Company companyFromTx2 = (Company)resource2.getContents().get(0);
    CDOObject companyCDOObjectFromTx2 = CDOUtil.getCDOObject(companyFromTx2);

    Category categoryFromTx2 = getModel1Factory().createCategory();
    // CDOObject categoryCDOObjectFromTx2 = CDOUtil.getCDOObject(categoryFromTx2);
    companyFromTx2.getCategories().add(categoryFromTx2);

    EcoreUtil.remove(companyFromTx1);

    commitAndSync(transaction1, transaction2);

    EObject eContainer = categoryFromTx2.eContainer();
    assertNull(eContainer);
    assertEquals(CDOState.INVALID_CONFLICT, companyCDOObjectFromTx2.cdoState());
    // assertEquals(CDOState.PROXY, categoryCDOObjectFromTx2.cdoState());
    // assertTrue(transaction2.hasConflict());
    // transaction2.rollback();
    // assertEquals(CDOState.INVALID, companyCDOObjectFromTx2.cdoState());
    // assertEquals(CDOState.TRANSIENT, categoryCDOObjectFromTx2.cdoState());
  }

  /**
   * Test {@link EObject#eResource()} call while the resource has been remotely deleted.
   */
  public void testCDOStaleReferencePolicyWithCDOStoreGetResource() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    transaction1.commit();

    // Test
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath(RESOURCE_NAME));

    Company companyFromTx2 = getModel1Factory().createCompany();
    // CDOObject companyCDOObjectFromTx2 = CDOUtil.getCDOObject(companyFromTx2);
    resource2.getContents().add(companyFromTx2);

    resource1.delete(null);

    commitAndSync(transaction1, transaction2);

    Resource resource = companyFromTx2.eResource();
    assertNull(resource);
    assertEquals(CDOState.INVALID_CONFLICT, resource2.cdoState());
    // assertEquals(CDOState.PROXY, companyCDOObjectFromTx2.cdoState());
    // assertTrue(transaction2.hasConflict());
    // transaction2.rollback();
    // assertEquals(CDOState.INVALID, resource2.cdoState());
    // assertEquals(CDOState.TRANSIENT, companyCDOObjectFromTx2.cdoState());
  }
}
