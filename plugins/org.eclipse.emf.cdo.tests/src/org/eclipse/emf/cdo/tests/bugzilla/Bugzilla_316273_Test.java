/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Problem with CDOStore.contains()
 * <p>
 * See bug 316273
 *
 * @author Eike Stepper
 */
public class Bugzilla_316273_Test extends AbstractCDOTest
{
  public void testXRef_Single() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");
    resource.getContents().add(customer);

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);
    resource.getContents().add(salesOrder);

    transaction.commit();

    resource.getContents().remove(customer);
    salesOrder.setCustomer(null);

    transaction.commit();
  }

  public void testXRef_Many() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Product1 product1 = getModel1Factory().createProduct1();
    product1.setName("product1");
    resource.getContents().add(product1);

    OrderDetail orderDetail1 = getModel1Factory().createOrderDetail();
    orderDetail1.setProduct(product1);
    resource.getContents().add(orderDetail1);

    OrderDetail orderDetail2 = getModel1Factory().createOrderDetail();
    orderDetail2.setProduct(product1);
    resource.getContents().add(orderDetail2);

    OrderDetail orderDetail3 = getModel1Factory().createOrderDetail();
    orderDetail3.setProduct(product1);
    resource.getContents().add(orderDetail3);

    transaction.commit();

    resource.getContents().remove(orderDetail2);
    product1.getOrderDetails().remove(orderDetail2);

    transaction.commit();
  }
}
