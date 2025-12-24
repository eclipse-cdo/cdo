/*
 * Copyright (c) 2007-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class RollbackTest extends AbstractCDOTest
{
  public void testRollbackSameSession() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    CDOTransaction transaction2 = session.openTransaction();
    flow1(transaction1, transaction2);
  }

  public void testRollbackSeparateSession() throws Exception
  {
    // Client1
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    // Client2
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    flow1(transaction1, transaction2);
  }

  protected void flow1(CDOTransaction transaction1, CDOTransaction transaction2) throws CommitException
  {
    // Client1
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);
    long commitTime = transaction1.commit().getTimeStamp();

    // Client2
    transaction2.waitForUpdate(commitTime, DEFAULT_TIMEOUT);
    CDOResource resource2 = transaction2.getResource(getResourcePath("/test1"));
    Company company2 = (Company)resource2.getContents().get(0); // Infrequent exceptions in legacy mode
    Category category2 = company2.getCategories().get(0);
    category2.setName("client2");
    Product1 product2 = getModel1Factory().createProduct1();
    product2.setName("product2");
    category2.getProducts().add(product2);

    // Client1
    category1.setName("client1");
    Product1 product1 = getModel1Factory().createProduct1();
    product1.setName("product1");
    category1.getProducts().add(product1);

    msg("Checking state of category");
    CDOObject cdoObjectCategory1 = CDOUtil.getCDOObject(category1);
    CDOObject cdoObjectProduct1 = CDOUtil.getCDOObject(product1);

    msg("Object should contain internalEObject");
    EStructuralFeature category_Products1 = getModel1Package().getCategory_Products();
    Object testObject = cdoObjectCategory1.cdoRevision().data().get(category_Products1, 0);
    assertEquals(product1, testObject);

    commitTime = transaction1.commit().getTimeStamp();

    msg("Object should contain CDOID");
    testObject = cdoObjectCategory1.cdoRevision().data().get(category_Products1, 0);
    assertEquals(cdoObjectProduct1.cdoID(), testObject);

    transaction2.waitForUpdate(commitTime, DEFAULT_TIMEOUT);

    // Client2
    assertEquals(true, transaction2.isDirty());
    assertEquals(true, transaction2.hasConflict());

    try
    {
      commitTime = transaction2.commit().getTimeStamp();
      fail("CommitException expected");
    }
    catch (CommitException ex)
    {
      // Commit process should no have changed state of the object
      CDOObject cdoObjectCategory2 = CDOUtil.getCDOObject(category2);
      EStructuralFeature category_Products2 = getModel1Package().getCategory_Products();
      testObject = cdoObjectCategory2.cdoRevision().data().get(category_Products2, 0);
      assertEquals(product2, testObject);
      transaction2.rollback();
    }

    assertEquals(false, transaction2.isDirty());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("client1", category2.getName());
    category2.setName("client2");

    commitTime = transaction2.commit().getTimeStamp();

    assertEquals(false, transaction2.isDirty());
    assertEquals(false, transaction2.hasConflict());
    assertEquals("client2", category2.getName());

    transaction1.waitForUpdate(commitTime, DEFAULT_TIMEOUT);

    // Client1
    assertEquals(false, transaction1.isDirty());
    assertEquals(false, transaction1.hasConflict());
    assertEquals("client2", category1.getName());
  }

  /**
   * Bug 296680.
   */
  public void test_Bugzilla_296680() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res"));
      resource.getContents().add(getModel1Factory().createCompany());
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res"));

    Company company = (Company)resource.getContents().get(0);
    company.setName("MyCompany");

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.setSavepoint();
    resource.getContents().remove(1);

    transaction.rollback();
    session.close();
  }
}
