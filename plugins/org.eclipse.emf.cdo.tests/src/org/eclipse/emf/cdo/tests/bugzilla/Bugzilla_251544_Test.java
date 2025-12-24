/*
 * Copyright (c) 2008-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * Resources fetched using CDOViewImpl.getResource(getResourcePath(CDOID)) not added to ResourceSet
 * <p>
 * See bug 251544
 *
 * @author Simon McDuff
 */
public class Bugzilla_251544_Test extends AbstractCDOTest
{
  public void testFromPersistedToTransient() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Order order1 = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    Product1 product = getModel1Factory().createProduct1();
    product.setName("product");

    EList<EObject> contentList = resource.getContents();
    resource.getContents().add(order1);
    order1.getOrderDetails().add(orderDetail);
    orderDetail.setProduct(product);

    EList<OrderDetail> list = order1.getOrderDetails();

    resource.getContents().remove(0); // remove object by index
    list.remove(orderDetail);
    transaction.commit();

    resource.delete(null);

    assertSame(contentList, resource.getContents());
  }

  public void testFromTransientToPersisted() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Order order1 = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    EList<OrderDetail> orderDetails = order1.getOrderDetails();
    order1.getOrderDetails().add(orderDetail);

    msg("Persist the graph");
    resource.getContents().add(order1);
    assertSame(orderDetails, order1.getOrderDetails());
    resource.getContents().remove(0); // remove object by index
    assertSame(orderDetails, order1.getOrderDetails());
    assertEquals(true, orderDetails.remove(orderDetail));
    transaction.commit();
  }
}
