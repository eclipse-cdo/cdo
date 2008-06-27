/***************************************************************************
 * Copyright (c) 2004 - 2008 Simon McDuff, Canada.
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
import org.eclipse.emf.cdo.CDOSavePoint;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import junit.framework.Assert;

/**
 * @author Simon McDuff
 */
public class SavePointTest extends AbstractCDOTest
{
  public void testRollbackWithNewObject_Collection() throws Exception
  {

    CDOSession session = openModel1Session();

    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransaction transaction1 = session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");

    Company company1 = Model1Factory.eINSTANCE.createCompany();
    resource1.getContents().add(company1);
    Category category1 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category1);

    transaction1.createSavePoint();

    Category category2 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category2);

    CDOSavePoint savePoint2 = transaction1.createSavePoint();

    Category category3 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category3);

    transaction1.createSavePoint();

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
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransactionImpl transaction1 = (CDOTransactionImpl)session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Company company1 = Model1Factory.eINSTANCE.createCompany();
    resource1.getContents().add(company1);
    Category category1 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category1);

    CDOSavePoint savePoint1 = transaction1.createSavePoint();
    CDOSavePoint savePoint2 = transaction1.createSavePoint();
    transaction1.rollback(savePoint1, false);
    try
    {
      transaction1.rollback(savePoint2, false);
      Assert.assertEquals("Should have thrown an exception", false, true);
    }
    catch (IllegalArgumentException illegalArgumentException)
    {

    }
    try
    {
      transaction1.rollback(null, false);
      Assert.assertEquals("Should have thrown an exception", false, true);
    }
    catch (IllegalArgumentException illegalArgumentException)
    {

    }
  }

  public void testisDirty() throws Exception
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransactionImpl transaction1 = (CDOTransactionImpl)session.openTransaction();

    CDOSavePoint savePoint0 = transaction1.createSavePoint();
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Company company1 = Model1Factory.eINSTANCE.createCompany();
    resource1.getContents().add(company1);
    Category category1 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category1);

    CDOSavePoint savePoint1 = transaction1.createSavePoint();
    Category category2 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category2);

    CDOSavePoint savePoint2 = transaction1.createSavePoint();
    CDOSavePoint savePoint3 = transaction1.createSavePoint();

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
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransactionImpl transaction1 = (CDOTransactionImpl)session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Category category3, category2, category4;

    Company company1 = Model1Factory.eINSTANCE.createCompany();
    resource1.getContents().add(company1);
    Category category1 = Model1Factory.eINSTANCE.createCategory();
    company1.getCategories().add(category1);

    CDOSavePoint savePoint1 = transaction1.createSavePoint();

    // Modification for savePoint1
    Company company2 = Model1Factory.eINSTANCE.createCompany();
    resource1.getContents().add(company2);
    company1.setCity("CITY1");

    assertEquals(2, resource1.getContents().size());

    // Rollback
    transaction1.rollback(savePoint1, false);

    if (commitBegin) transaction1.commit();

    {
      assertEquals(null, company1.getCity());
      assertEquals(1, resource1.getContents().size());
      company1.setCity("CITY1");
      category2 = Model1Factory.eINSTANCE.createCategory();
      company1.getCategories().add(category2);
    }

    CDOSavePoint savePoint2 = transaction1.createSavePoint();
    {
      company1.setCity("CITY2");
      category3 = Model1Factory.eINSTANCE.createCategory();
      company1.getCategories().add(category3);
    }

    transaction1.createSavePoint();
    {
      company1.setCity("CITY3");
      assertEquals(3, company1.getCategories().size());
      category4 = Model1Factory.eINSTANCE.createCategory();
      company1.getCategories().add(category4);

    }

    transaction1.rollback(savePoint2, false);

    assertEquals(true, transaction1.isDirty());

    // Test NEW TO NEW
    assertEquals(false, FSMUtil.isTransient(company1));

    // Test NEW TO TRANSIENT (2 step back)
    assertEquals(true, FSMUtil.isTransient(category3));
    assertEquals(false, transaction1.getNewObjects().containsKey(((CDOObject)category3).cdoID()));

    // Test NEW TO TRANSIENT (1 step back)
    assertEquals(true, FSMUtil.isTransient(category4));
    assertEquals(false, transaction1.getNewObjects().containsKey(((CDOObject)category4).cdoID()));

    // Test NEW TO NEW
    assertEquals(false, FSMUtil.isTransient(category2));
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

      assertEquals(null, transaction1.getLastSavePoint().getPreviousSavePoint());
    }
    else
    {
      transaction1.rollback(false);
      assertEquals(false, transaction1.isDirty());
      assertEquals(null, transaction1.getLastSavePoint().getNextSavePoint());
      assertEquals(null, transaction1.getLastSavePoint().getPreviousSavePoint());

      assertEquals(commitBegin, !FSMUtil.isTransient(company1));

      assertEquals(commitBegin, !FSMUtil.isTransient(resource1));

    }

  }
}
