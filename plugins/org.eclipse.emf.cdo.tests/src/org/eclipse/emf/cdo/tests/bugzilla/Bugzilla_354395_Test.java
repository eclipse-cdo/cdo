/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * ContainsAll with detached objects
 * <p>
 * See bug
 *
 * @author Stefan Schedl
 */
public class Bugzilla_354395_Test extends AbstractCDOTest
{
  public void testContains() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      SalesOrder salesOrder = getModel1Factory().createSalesOrder();

      Company company = getModel1Factory().createCompany();
      company.getSalesOrders().add(salesOrder);

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      customer.getSalesOrders().add(salesOrder);

      company.getCustomers().add(customer);
      resource.getContents().add(company);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    Company company = (Company)resource.getContents().get(0);
    SalesOrder salesOrder = company.getSalesOrders().get(0);

    // remove salesOrder from its containment list, salesOrder now detached
    company.getSalesOrders().remove(salesOrder);
    assertTransient(salesOrder);

    Customer customer = company.getCustomers().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();

    List<SalesOrder> javaList = new ArrayList<>();
    for (SalesOrder order : salesOrders)
    {
      javaList.add(order);
    }

    Set<SalesOrder> lookFor = Collections.singleton(salesOrder);

    // Test contains & containsAll with "normal" java list
    assertEquals(true, javaList.contains(salesOrder));
    assertEquals(true, javaList.containsAll(lookFor));

    // Test contains & containsAll direct with the elist
    assertEquals(true, salesOrders.contains(salesOrder));
    assertEquals(true, salesOrders.containsAll(lookFor));
  }

  public void testIndexOf() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      SalesOrder salesOrder = getModel1Factory().createSalesOrder();

      Company company = getModel1Factory().createCompany();
      company.getSalesOrders().add(salesOrder);

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      customer.getSalesOrders().add(salesOrder);

      company.getCustomers().add(customer);
      resource.getContents().add(company);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    Company company = (Company)resource.getContents().get(0);
    SalesOrder salesOrder = company.getSalesOrders().get(0);

    // remove salesOrder from its containment list, salesOrder now detached
    company.getSalesOrders().remove(salesOrder);
    assertTransient(salesOrder);

    Customer customer = company.getCustomers().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();

    List<SalesOrder> javaList = new ArrayList<>();
    for (SalesOrder order : salesOrders)
    {
      javaList.add(order);
    }

    // Test indexOf & lastIndexOf with "normal" java list
    assertEquals(0, javaList.indexOf(salesOrder));
    assertEquals(0, javaList.lastIndexOf(salesOrder));

    // Test indexOf & lastIndexOf direct with the elist
    assertEquals(0, salesOrders.indexOf(salesOrder));
    assertEquals(0, salesOrders.lastIndexOf(salesOrder));
  }
}
