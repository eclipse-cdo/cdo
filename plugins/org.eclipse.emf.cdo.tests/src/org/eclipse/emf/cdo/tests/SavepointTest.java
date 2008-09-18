/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/

package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

/**
 * @author Simon McDuff
 */
public class SavepointTest extends AbstractCDOTest
{
  public void testRollbackWithNewObject_Collection() throws Exception
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction1 = session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");

    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    transaction1.setSavepoint();

    Category category2 = getModel1Factory().createCategory();
    company1.getCategories().add(category2);

    CDOSavepoint savePoint2 = transaction1.setSavepoint();

    Category category3 = getModel1Factory().createCategory();
    company1.getCategories().add(category3);

    transaction1.setSavepoint();
    transaction1.rollback(savePoint2, false);
    assertEquals(2, company1.getCategories().size());

    transaction1.commit();
  }

  public void testRollbackWithNewObject_Commit() throws Exception
  {
    flow1(false, true);
  }

  public void testRollbackWithNewObject_Rollback() throws Exception
  {
    flow1(false, false);
  }

  public void testRollbackWithPersistedObject_Commit() throws Exception
  {
    flow1(true, true);
  }

  public void testRollbackWithPersistedObject_Rollback() throws Exception
  {
    flow1(true, false);
  }

  public void testWrongSavePoint() throws Exception
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransactionImpl transaction1 = (CDOTransactionImpl)session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    CDOSavepoint savePoint1 = transaction1.setSavepoint();
    CDOSavepoint savePoint2 = transaction1.setSavepoint();
    transaction1.rollback(savePoint1, false);

    try
    {
      transaction1.rollback(savePoint2, false);
      fail("Should have thrown an exception");
    }
    catch (IllegalArgumentException expected)
    {
    }

    try
    {
      transaction1.rollback(null, false);
      fail("Should have thrown an exception");
    }
    catch (IllegalArgumentException expected)
    {
    }
  }

  public void testisDirty() throws Exception
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransactionImpl transaction1 = (CDOTransactionImpl)session.openTransaction();
    CDOSavepoint savePoint0 = transaction1.setSavepoint();

    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    CDOSavepoint savePoint1 = transaction1.setSavepoint();
    Category category2 = getModel1Factory().createCategory();
    company1.getCategories().add(category2);

    CDOSavepoint savePoint2 = transaction1.setSavepoint();
    CDOSavepoint savePoint3 = transaction1.setSavepoint();

    assertEquals(true, transaction1.isDirty());

    transaction1.rollback(savePoint3, false);
    assertEquals(true, transaction1.isDirty());

    transaction1.rollback(savePoint2, false);
    assertEquals(true, transaction1.isDirty());

    transaction1.rollback(savePoint1, false);
    assertEquals(true, transaction1.isDirty());

    // Didn`t make any modification prior to savepoint0
    transaction1.rollback(savePoint0, false);
    assertEquals(false, transaction1.isDirty());

    transaction1.rollback(false);
    assertEquals(false, transaction1.isDirty());
  }

  public void flow1(boolean commitBegin, boolean commitEnd) throws Exception
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransactionImpl transaction1 = (CDOTransactionImpl)session.openTransaction();

    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Category category3, category2, category4;

    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    CDOSavepoint savePoint1 = transaction1.setSavepoint();

    // Modification for savePoint1
    Company company2 = getModel1Factory().createCompany();
    resource1.getContents().add(company2);
    company1.setCity("CITY1");

    assertEquals(2, resource1.getContents().size());

    // Rollback
    transaction1.rollback(savePoint1, false);

    if (commitBegin)
    {
      transaction1.commit();
    }

    {
      assertEquals(null, company1.getCity());
      assertEquals(1, resource1.getContents().size());
      company1.setCity("CITY1");
      category2 = getModel1Factory().createCategory();
      company1.getCategories().add(category2);
    }

    CDOSavepoint savePoint2 = transaction1.setSavepoint();

    {
      company1.setCity("CITY2");
      category3 = getModel1Factory().createCategory();
      company1.getCategories().add(category3);
    }

    transaction1.setSavepoint();

    {
      company1.setCity("CITY3");
      assertEquals(3, company1.getCategories().size());
      category4 = getModel1Factory().createCategory();
      company1.getCategories().add(category4);

    }

    transaction1.rollback(savePoint2, false);
    assertEquals(true, transaction1.isDirty());

    // Test NEW TO NEW
    assertEquals(false, FSMUtil.isTransient(CDOUtil.getCDOObject(company1)));

    // Test NEW TO TRANSIENT (2 step back)
    assertEquals(true, FSMUtil.isTransient(CDOUtil.getCDOObject(category3)));
    assertEquals(false, transaction1.getNewObjects().containsKey(((CDOObject)category3).cdoID()));

    // Test NEW TO TRANSIENT (1 step back)
    assertEquals(true, FSMUtil.isTransient(CDOUtil.getCDOObject(category4)));
    assertEquals(false, transaction1.getNewObjects().containsKey(((CDOObject)category4).cdoID()));

    // Test NEW TO NEW
    assertEquals(false, FSMUtil.isTransient(CDOUtil.getCDOObject(category2)));
    assertEquals(true, transaction1.getNewObjects().containsKey(((CDOObject)category2).cdoID()));

    // Test rollback NEW
    assertEquals("CITY1", company1.getCity());
    assertEquals(2, company1.getCategories().size());
    if (commitEnd)
    {
      transaction1.commit();
      assertClean(company1, transaction1);
      assertClean(category2, transaction1);
      assertEquals("CITY1", company1.getCity());
      assertEquals(2, company1.getCategories().size());
      assertEquals(null, transaction1.getLastSavepoint().getPreviousSavepoint());
    }
    else
    {
      transaction1.rollback(false);
      assertEquals(false, transaction1.isDirty());
      assertEquals(null, transaction1.getLastSavepoint().getNextSavepoint());
      assertEquals(null, transaction1.getLastSavepoint().getPreviousSavepoint());
      assertEquals(commitBegin, !FSMUtil.isTransient(CDOUtil.getCDOObject(company1)));
      assertEquals(commitBegin, !FSMUtil.isTransient(resource1));
    }
  }
}
