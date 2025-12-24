/*
 * Copyright (c) 2014, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;

/**
 * Bug 367738 - getOldValue call on Notification from CDO returns null as opposed to old value.
 *
 * @author Eike Stepper
 */
public class Bugzilla_367738_Test extends AbstractCDOTest
{
  public void testOldValue_Attribute() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    company.setName("ESC");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    TestAdapter adapter = new TestAdapter(company);
    company.setName(null);

    Notification[] notifications = adapter.getNotifications();
    Object oldValue = notifications[0].getOldValue();
    assertEquals("ESC", oldValue);
  }

  public void testOldValue_XRef() throws Exception
  {
    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(15.4f);
    orderDetail.setProduct(product);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);
    transaction.commit();

    TestAdapter adapter = new TestAdapter(orderDetail);
    orderDetail.setProduct(null);

    Notification[] notifications = adapter.getNotifications();
    Object oldValue = notifications[0].getOldValue();
    assertEquals(product, oldValue);
  }

  public void testOldValue_ContainmentMove() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Eike");

    SpecialPurchaseOrder purchaseOrder1 = getModel2Factory().createSpecialPurchaseOrder();
    purchaseOrder1.setShippingAddress(customer);

    SpecialPurchaseOrder purchaseOrder2 = getModel2Factory().createSpecialPurchaseOrder();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    transaction.commit();

    TestAdapter adapter = new TestAdapter(purchaseOrder1);
    purchaseOrder2.setShippingAddress(customer);
    assertNull(purchaseOrder1.getShippingAddress());

    Notification[] notifications = adapter.getNotifications();
    Object oldValue = notifications[0].getOldValue();
    assertEquals(customer, oldValue);
  }

  public void testOldValue_ContainmentDetach() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Eike");

    SpecialPurchaseOrder purchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
    purchaseOrder.setShippingAddress(customer);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(purchaseOrder);
    transaction.commit();

    TestAdapter adapter = new TestAdapter(purchaseOrder);
    purchaseOrder.setShippingAddress(null);

    Notification[] notifications = adapter.getNotifications();
    Object oldValue = notifications[0].getOldValue();
    assertEquals(customer, oldValue);
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testOldValue_ContainmentControl() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Eike");

    SpecialPurchaseOrder purchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
    purchaseOrder.setShippingAddress(customer);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(purchaseOrder);
    transaction.commit();

    TestAdapter adapter = new TestAdapter(purchaseOrder);
    resource.getContents().add(customer);

    // Adding the customer to resource.contents does NOT remove it from the containment ref!
    adapter.assertNotifications(0);
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testOldValue_ContainmentUncontrol() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Eike");

    SpecialPurchaseOrder purchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
    purchaseOrder.setShippingAddress(customer);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(purchaseOrder);
    transaction.commit();
    assertEquals(customer, purchaseOrder.getShippingAddress());

    resource.getContents().add(customer); // Control customer
    transaction.commit();
    assertEquals(customer, purchaseOrder.getShippingAddress());

    TestAdapter adapter = new TestAdapter(purchaseOrder);
    purchaseOrder.setShippingAddress(null);

    Notification[] notifications = adapter.getNotifications();
    Object oldValue = notifications[0].getOldValue();
    assertEquals(customer, oldValue);
  }

  @Skips(IConfig.CAPABILITY_ALL)
  public void testOldValue_ContainmentUncontrol_BROKEN() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Eike");

    SpecialPurchaseOrder purchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
    purchaseOrder.setShippingAddress(customer);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(purchaseOrder);
    resource.getContents().add(customer); // Control customer
    transaction.commit();
    assertEquals(customer, purchaseOrder.getShippingAddress());

    TestAdapter adapter = new TestAdapter(purchaseOrder);
    purchaseOrder.setShippingAddress(null);

    Notification[] notifications = adapter.getNotifications();
    Object oldValue = notifications[0].getOldValue();
    assertEquals(customer, oldValue);
  }
}
