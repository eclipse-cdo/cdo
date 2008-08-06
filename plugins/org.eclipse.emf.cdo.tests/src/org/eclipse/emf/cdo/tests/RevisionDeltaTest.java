/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

import junit.framework.Assert;

/**
 * @see http://bugs.eclipse.org/201266
 * @author Simon McDuff
 */
public abstract class RevisionDeltaTest extends AbstractCDOTest
{
  protected RevisionDeltaTest()
  {
  }

  public void testBasicRevisionDelta() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource("/test1");

    Company company1 = Model1Factory.eINSTANCE.createCompany();
    Category category1 = Model1Factory.eINSTANCE.createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    company1.setName("TEST");
    InternalCDORevision rev1 = getCopyCDORevision(company1);

    // Test simple attributes
    company1.setName("TEST3");
    InternalCDORevision rev2 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta = rev2.compare(rev1);
    assertEquals(1, revisionDelta.getFeatureDeltas().size());
    CDOSetFeatureDelta setDelta = (CDOSetFeatureDelta)revisionDelta.getFeatureDeltas().get(0);
    assertEquals("TEST3", setDelta.getValue());

    // Test List clear
    company1.getCategories().clear();
    InternalCDORevision rev3 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta3 = rev3.compare(rev2);
    assertEquals(1, revisionDelta3.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta3List = (CDOListFeatureDeltaImpl)revisionDelta3.getFeatureDeltas().get(0);

    assertEquals(1, delta3List.getListChanges().size());
    assertEquals(true, delta3List.getListChanges().get(0) instanceof CDOClearFeatureDelta);

    // Test List Add
    for (int i = 0; i < 5; i++)
    {
      Category category = Model1Factory.eINSTANCE.createCategory();
      company1.getCategories().add(category);
    }
    InternalCDORevision rev4 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta4 = rev4.compare(rev3);
    assertEquals(1, revisionDelta4.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta4List = (CDOListFeatureDeltaImpl)revisionDelta4.getFeatureDeltas().get(0);

    assertEquals(5, delta4List.getListChanges().size());
    assertEquals(true, delta4List.getListChanges().get(0) instanceof CDOAddFeatureDelta);
    transaction.rollback(true);

    transaction.close();
    session.close();
  }

  /**
   * Sending deltas doesn't adjust CDOIDs
   * 
   * @see http://bugs.eclipse.org/214374
   */
  public void testBugzilla214374() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    salesOrder.setCustomer(customer);

    resource.getContents().add(customer);
    transaction.commit();
    transaction.close();

    CDOTransaction transaction2 = session.openTransaction();
    SalesOrder salesOrder2 = (SalesOrder)transaction2.getObject(salesOrder.cdoID(), true);
    CDORevision salesRevision = salesOrder2.cdoRevision();
    CDOFeature customerFeature = session.getPackageManager().convert(Model1Package.eINSTANCE.getSalesOrder_Customer());

    Object value = salesRevision.getData().get(customerFeature, 0);
    Assert.assertEquals(true, value instanceof CDOID);
    transaction2.close();
    session.close();
  }

  /**
   * CDOView.getRevision() does not work for transactions/dirty objects (INVALID)
   * 
   * @see http://bugs.eclipse.org/214431
   */
  public void testBugzilla214431() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    salesOrder.setId(4711);
    assertNotSame(salesOrder.cdoRevision(), transaction.getRevision(salesOrder.cdoID(), true));
    assertEquals(salesOrder.cdoRevision(), transaction.getDirtyObjects().get(salesOrder.cdoID()).cdoRevision());
    transaction.close();
    session.close();
  }

  /**
   * CDOView.getRevision() does not work for transactions/dirty objects (INVALID)
   * 
   * @see http://bugs.eclipse.org/214374
   */
  public void testBugzilla214374_HandleClear() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    resource.getContents().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);
    transaction.commit();

    salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(salesOrder);
    resource.getContents().add(customer);
    salesOrder.setCustomer(customer);
    transaction.commit();
    transaction.close();
    session.close();
  }

  /**
   * java.lang.IllegalStateException with MEMStore
   * 
   * @see http://bugs.eclipse.org/243282
   */
  public void testBugzilla243282_Exception() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    resource.getContents().add(customer);

    transaction.commit();

    TestRevisionManager revisionManager = (TestRevisionManager)getRepository().getRevisionManager();
    revisionManager.removeRevision(customer.cdoRevision());

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    resource.getContents().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);

    transaction.commit();
    transaction.close();
    session.close();
  }

  private InternalCDORevision getCopyCDORevision(Object object)
  {
    return (InternalCDORevision)CDORevisionUtil.copy(((CDOObject)object).cdoRevision());
  }

  /**
   * @author Simon McDuff
   */
  protected static class TestRevisionManager extends RevisionManager
  {
    public TestRevisionManager(Repository repository)
    {
      super(repository);
    }

    public void removeRevision(CDORevision revision)
    {
      super.removeRevision(revision.getID(), revision.getVersion());
    }
  }
}
