/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.CloseableIterator;

import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * Bug 416555: Provide CDOView.queryInstances(EClass).
 * 
 * @author Eike Stepper
 */
public class Bugzilla_416555_Test extends AbstractCDOTest
{
  public void testQueryInstances() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    // TODO Remove this work-around for a bug with unmapped packages
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    int orders = transaction.queryInstances(getModel1Package().getOrder()).size();
    int purchaseOrders = transaction.queryInstances(getModel1Package().getPurchaseOrder()).size();
    int salesOrders = transaction.queryInstances(getModel1Package().getSalesOrder()).size();

    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());

    transaction.commit();
    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    assertResult(orders + 9, view.queryInstances(getModel1Package().getOrder()), Order.class);

    assertResult(purchaseOrders + 4, view.queryInstances(getModel1Package().getPurchaseOrder()), PurchaseOrder.class);

    assertResult(salesOrders + 5, view.queryInstances(getModel1Package().getSalesOrder()), SalesOrder.class);
  }

  private void assertResult(int expected, List<EObject> results, Class<?> c)
  {
    assertEquals(expected, results.size());
    for (EObject result : results)
    {
      assertInstanceOf(c, CDOUtil.getEObject(result));
    }
  }

  public void testQueryInstancesAsync() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    // TODO Remove this work-around for a bug with unmapped packages
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    int orders = transaction.queryInstances(getModel1Package().getOrder()).size();
    int purchaseOrders = transaction.queryInstances(getModel1Package().getPurchaseOrder()).size();
    int salesOrders = transaction.queryInstances(getModel1Package().getSalesOrder()).size();

    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createPurchaseOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());
    resource.getContents().add(getModel1Factory().createSalesOrder());

    transaction.commit();
    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    assertResult(orders + 9, view.queryInstancesAsync(getModel1Package().getOrder()), Order.class);

    assertResult(purchaseOrders + 4, view.queryInstancesAsync(getModel1Package().getPurchaseOrder()),
        PurchaseOrder.class);

    assertResult(salesOrders + 5, view.queryInstancesAsync(getModel1Package().getSalesOrder()), SalesOrder.class);
  }

  private void assertResult(int expected, CloseableIterator<EObject> it, Class<?> c)
  {
    int count = 0;
    while (it.hasNext())
    {
      EObject result = it.next();
      assertInstanceOf(c, CDOUtil.getEObject(result));
      ++count;
    }

    assertEquals(expected, count);
  }
}
