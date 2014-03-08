/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Platter, Bestsolution.at - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;

/**
 * Bug 426463 - ArrayIndexOutOfBoundsException on rollback due to contains optimization in DelegatingEcoreEList
 *
 * @author Martin Platter, Bestsolution.at
 */
public class Bugzilla_426463_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    enableConsole();
    super.doTearDown();
  }

  public void testRollback() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    msg("Create test customer");
    Company company = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();
    EList<SalesOrder> customerOrders = customer.getSalesOrders();

    // We must add at least 5
    for (int i = 1; i <= 5; i++)
    {
      SalesOrder createSalesOrder = getModel1Factory().createSalesOrder();
      company.getSalesOrders().add(createSalesOrder);
      customerOrders.add(createSalesOrder);
    }

    company.getCustomers().add(customer);
    resource.getContents().add(company);
    transaction.commit();

    msg("Create test sales order and add to customer sales list");
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    company.getSalesOrders().add(salesOrder);
    customerOrders.add(salesOrder);

    msg("Rollback");
    transaction.rollback();

    // Shouldn't fail:
    customerOrders.contains(salesOrder);
  }

  public void testRollbackWithAdapter() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    msg("Create test customer");
    Company company = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();
    final EList<SalesOrder> customerOrders = customer.getSalesOrders();

    // We must add at least 5
    for (int i = 1; i <= 5; i++)
    {
      SalesOrder createSalesOrder = getModel1Factory().createSalesOrder();
      company.getSalesOrders().add(createSalesOrder);
      customerOrders.add(createSalesOrder);
    }

    company.getCustomers().add(customer);
    resource.getContents().add(company);
    transaction.commit();

    msg("Create test sales order and add to customer sales list");
    final SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    company.getSalesOrders().add(salesOrder);
    customerOrders.add(salesOrder);

    msg("Add listener and rollback");
    company.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        if (notification.getEventType() == Notification.REMOVE)
        {
          // Must not crash here on rollback
          customerOrders.contains(salesOrder);
        }
      }
    });

    transaction.rollback();
  }
}
