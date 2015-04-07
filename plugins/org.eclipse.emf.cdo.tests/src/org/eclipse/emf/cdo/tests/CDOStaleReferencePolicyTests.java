/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 *
 * Bug 464036 : NPE on CDOObject.eContainer()/eResource() called when the container/resource has been remotely deleted.
 *
 * @author Esteban Dugueperoux
 */
public class CDOStaleReferencePolicyTests extends AbstractCDOTest
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

    Category categoryFromTx2 = getModel1Factory().createCategory();
    companyFromTx2.getCategories().add(categoryFromTx2);

    EcoreUtil.remove(companyFromTx1);

    commitAndSync(transaction1, transaction2);

    EObject eContainer = categoryFromTx2.eContainer();
    assertNull(eContainer);
    // FIXME/TODO : CDOState should be at TRANSIENT and not NEW
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
    resource2.getContents().add(companyFromTx2);

    resource1.delete(null);

    commitAndSync(transaction1, transaction2);

    Resource resource = companyFromTx2.eResource();
    assertNull(resource);
    // FIXME/TODO : CDOState should be at TRANSIENT and not NEW
  }

}
