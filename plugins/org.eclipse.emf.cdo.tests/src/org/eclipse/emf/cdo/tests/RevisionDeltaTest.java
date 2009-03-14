/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

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

    Company company1 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();

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
      Category category = getModel1Factory().createCategory();
      company1.getCategories().add(category);
    }

    InternalCDORevision rev4 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta4 = rev4.compare(rev3);
    assertEquals(1, revisionDelta4.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta4List = (CDOListFeatureDeltaImpl)revisionDelta4.getFeatureDeltas().get(0);

    assertEquals(5, delta4List.getListChanges().size());
    assertEquals(true, delta4List.getListChanges().get(0) instanceof CDOAddFeatureDelta);
    transaction.rollback();
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

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    Customer customer = getModel1Factory().createCustomer();
    salesOrder.setCustomer(customer);

    resource.getContents().add(customer);
    transaction.commit();
    transaction.close();

    CDOTransaction transaction2 = session.openTransaction();
    SalesOrder salesOrder2 = (SalesOrder)transaction2.getObject(CDOUtil.getCDOObject(salesOrder).cdoID(), true);
    CDORevision salesRevision = CDOUtil.getCDOObject(salesOrder2).cdoRevision();
    EStructuralFeature customerFeature = getModel1Package().getSalesOrder_Customer();

    Object value = salesRevision.data().get(customerFeature, 0);
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
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    salesOrder.setId(4711);
    assertNotSame(CDOUtil.getCDOObject(salesOrder).cdoRevision(), ((InternalCDOTransaction)transaction).getRevision(
        CDOUtil.getCDOObject(salesOrder).cdoID(), true));
    assertEquals(CDOUtil.getCDOObject(salesOrder).cdoRevision(), transaction.getDirtyObjects().get(
        CDOUtil.getCDOObject(salesOrder).cdoID()).cdoRevision());
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

    Customer customer = getModel1Factory().createCustomer();
    resource.getContents().add(customer);

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);
    transaction.commit();

    salesOrder = getModel1Factory().createSalesOrder();
    customer = getModel1Factory().createCustomer();
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

    Customer customer = getModel1Factory().createCustomer();
    resource.getContents().add(customer);

    transaction.commit();

    TestRevisionManager revisionManager = (TestRevisionManager)getRepository().getRevisionManager();
    revisionManager.removeCachedRevision(CDOUtil.getCDOObject(customer).cdoRevision());

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);

    transaction.commit();
    transaction.close();
    session.close();
  }

  private InternalCDORevision getCopyCDORevision(Object object)
  {
    return (InternalCDORevision)((CDOObject)object).cdoRevision().copy();
  }

  /**
   * @author Simon McDuff
   */
  public static class TestRevisionManager extends RevisionManager
  {
    public void removeCachedRevision(CDORevision revision)
    {
      super.removeCachedRevision(revision.getID(), revision.getVersion());
    }
  }
}
