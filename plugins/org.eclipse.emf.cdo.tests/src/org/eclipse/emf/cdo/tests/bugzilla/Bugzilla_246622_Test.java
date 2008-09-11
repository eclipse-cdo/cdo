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
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

/**
 * 246622: CDOStore.set doesn't affect variable correctly - Could cause memory retention
 * <p>
 * See https://bugs.eclipse.org/243310
 * 
 * @author Simon McDuff
 */
public class Bugzilla_246622_Test extends AbstractCDOTest
{
  public void testContainerAndMany() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    CDOResource res = transaction1.createResource("/test1");
    Order order = Model1Factory.eINSTANCE.createOrder();
    OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();
    
    res.getContents().add(order);
    order.getOrderDetails().add(orderDetail);
    CDOFeature order_OrderDetailFeature = session.getPackageManager().convert(Model1Package.eINSTANCE.getOrder_OrderDetails());
    assertEquals(orderDetail, order.cdoRevision().getData().get(order_OrderDetailFeature, 0));

    assertEquals(order, orderDetail.cdoRevision().getData().getContainerID());

    Order order2 = Model1Factory.eINSTANCE.createOrder();
    OrderDetail orderDetail2 = Model1Factory.eINSTANCE.createOrderDetail();

    order2.getOrderDetails().add(orderDetail2);
    res.getContents().add(order2);

    assertEquals(orderDetail2, order2.cdoRevision().getData().get(order_OrderDetailFeature, 0));
    assertEquals(order2, orderDetail2.cdoRevision().getData().getContainerID());

    msg("Committing");
    transaction1.commit();
    
    assertEquals(orderDetail.cdoID(), order.cdoRevision().getData().get(order_OrderDetailFeature, 0));
    assertEquals(orderDetail2.cdoID(), order2.cdoRevision().getData().get(order_OrderDetailFeature, 0));
    
    assertEquals(order.cdoID(), orderDetail.cdoRevision().getData().getContainerID());
    assertEquals(order2.cdoID(), orderDetail2.cdoRevision().getData().getContainerID());
    
    Order order3 = Model1Factory.eINSTANCE.createOrder();
    
    res.getContents().add(order3);
    order3.getOrderDetails().add(orderDetail2);
    
    assertEquals(orderDetail2.cdoID(), order3.cdoRevision().getData().get(order_OrderDetailFeature, 0));
    assertEquals(order3, orderDetail2.cdoRevision().getData().getContainerID());
    
    msg("Committing");
    transaction1.commit();
  }
  public void testSet() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    CDOResource res = transaction1.createResource("/test1");
    
    
    msg("Test set with link before");
    PurchaseOrder purchaseOrder = Model1Factory.eINSTANCE.createPurchaseOrder();
    Supplier supplier  = Model1Factory.eINSTANCE.createSupplier();
    
    purchaseOrder.setSupplier(supplier);
    
    res.getContents().add(purchaseOrder);
    res.getContents().add(supplier);
    
    CDOFeature supplier_PurchaseOrder = session.getPackageManager().convert(Model1Package.eINSTANCE.getSupplier_PurchaseOrders());
    CDOFeature purchaseOrder_Supplier = session.getPackageManager().convert(Model1Package.eINSTANCE.getPurchaseOrder_Supplier());

    
    assertEquals(supplier, purchaseOrder.cdoRevision().getData().get(purchaseOrder_Supplier, 0));
    assertEquals(purchaseOrder, supplier.cdoRevision().getData().get(supplier_PurchaseOrder, 0));

    msg("Test set with link after");
    PurchaseOrder purchaseOrder2 = Model1Factory.eINSTANCE.createPurchaseOrder();
    Supplier supplier2  = Model1Factory.eINSTANCE.createSupplier();

    res.getContents().add(purchaseOrder2);
    res.getContents().add(supplier2);
    purchaseOrder2.setSupplier(supplier2);

    assertEquals(supplier2, purchaseOrder2.cdoRevision().getData().get(purchaseOrder_Supplier, 0));
    assertEquals(purchaseOrder2, supplier2.cdoRevision().getData().get(supplier_PurchaseOrder, 0));

    msg("Committing");
    transaction1.commit();
    
    
    assertEquals(supplier2.cdoID(), purchaseOrder2.cdoRevision().getData().get(purchaseOrder_Supplier, 0));
    assertEquals(purchaseOrder2.cdoID(), supplier2.cdoRevision().getData().get(supplier_PurchaseOrder, 0));
    
    assertEquals(supplier.cdoID(), purchaseOrder.cdoRevision().getData().get(purchaseOrder_Supplier, 0));
    assertEquals(purchaseOrder.cdoID(), supplier.cdoRevision().getData().get(supplier_PurchaseOrder, 0));
    
    msg("Test set with persisted CDOID");
    PurchaseOrder purchaseOrder3 = Model1Factory.eINSTANCE.createPurchaseOrder();
    purchaseOrder3.setSupplier(supplier2);
    res.getContents().add(purchaseOrder3);
    
    assertEquals(supplier2.cdoID(), purchaseOrder3.cdoRevision().getData().get(purchaseOrder_Supplier, 0));
    assertEquals(purchaseOrder3, supplier2.cdoRevision().getData().get(supplier_PurchaseOrder, 1));

    msg("Committing");
    transaction1.commit();
  }
}
