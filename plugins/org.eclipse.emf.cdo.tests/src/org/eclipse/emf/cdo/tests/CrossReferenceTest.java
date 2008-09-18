/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class CrossReferenceTest extends AbstractCDOTest
{
  public void testLoadViaContainment() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    // ************************************************************* //

    msg("Creating customer");
    Customer customerA = getModel1Factory().createCustomer();
    customerA.setName("customer");

    msg("Creating salesOrder1");
    SalesOrder salesOrder1A = getModel1Factory().createSalesOrder();
    salesOrder1A.setId(1);
    salesOrder1A.setCustomer(customerA);

    msg("Creating salesOrder2");
    SalesOrder salesOrder2A = getModel1Factory().createSalesOrder();
    salesOrder2A.setId(2);
    salesOrder2A.setCustomer(customerA);

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    companyA.getCustomers().add(customerA);
    companyA.getSalesOrders().add(salesOrder1A);
    companyA.getSalesOrders().add(salesOrder2A);

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    assertEquals(2, customerA.getSalesOrders().size());

    // ************************************************************* //

    msg("Attaching viewB");
    CDOView viewB = session.openTransaction();

    msg("Loading resource");
    CDOResource resourceB = viewB.getResource("/test1");
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    Company companyB = (Company)contents.get(0);
    assertClean(companyB, viewB);
    assertClean(resourceB, viewB);
    assertContent(resourceB, companyB);

    Customer customerB = companyB.getCustomers().get(0);
    assertClean(customerB, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, customerB);

    SalesOrder salesOrder1B = companyB.getSalesOrders().get(0);
    assertClean(salesOrder1B, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, salesOrder1B);

    SalesOrder salesOrder2B = companyB.getSalesOrders().get(1);
    assertClean(salesOrder2B, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, salesOrder2B);
    assertClean(salesOrder2B, viewB);

    assertEquals(2, customerB.getSalesOrders().size());
  }

  public void testLoadViaXRef() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    // ************************************************************* //

    msg("Creating customer");
    Customer customerA = getModel1Factory().createCustomer();
    customerA.setName("customer");

    msg("Creating salesOrder1");
    SalesOrder salesOrder1A = getModel1Factory().createSalesOrder();
    salesOrder1A.setId(1);
    salesOrder1A.setCustomer(customerA);

    msg("Creating salesOrder2");
    SalesOrder salesOrder2A = getModel1Factory().createSalesOrder();
    salesOrder2A.setId(2);
    salesOrder2A.setCustomer(customerA);

    msg("Creating company");
    Company companyA = getModel1Factory().createCompany();
    companyA.getCustomers().add(customerA);
    companyA.getSalesOrders().add(salesOrder1A);
    companyA.getSalesOrders().add(salesOrder2A);

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    assertEquals(2, customerA.getSalesOrders().size());

    // ************************************************************* //

    msg("Attaching viewB");
    CDOView viewB = session.openTransaction();

    msg("Loading resource");
    CDOResource resourceB = viewB.getResource("/test1");
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    Company companyB = (Company)contents.get(0);
    assertClean(companyB, viewB);
    assertClean(resourceB, viewB);
    assertContent(resourceB, companyB);

    Customer customerB = companyB.getCustomers().get(0);
    assertClean(customerB, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, customerB);

    SalesOrder salesOrder1B = customerB.getSalesOrders().get(0);
    assertClean(salesOrder1B, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, salesOrder1B);

    SalesOrder salesOrder2B = customerB.getSalesOrders().get(1);
    assertClean(salesOrder2B, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, salesOrder2B);
    assertClean(salesOrder2B, viewB);
  }

  public void testTwoResources() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();

    // ************************************************************* //

    msg("Creating customer");
    Customer customerA = getModel1Factory().createCustomer();
    customerA.setName("customer");

    msg("Creating company1");
    Company company1A = getModel1Factory().createCompany();
    company1A.getCustomers().add(customerA);

    msg("Creating resource1");
    CDOResource resource1A = transaction.createResource("/test1");

    msg("Adding company1");
    resource1A.getContents().add(company1A);

    // ************************************************************* //

    msg("Creating salesOrder1");
    SalesOrder salesOrder1A = getModel1Factory().createSalesOrder();
    assertTransient(salesOrder1A);
    salesOrder1A.setId(1);
    salesOrder1A.setCustomer(customerA);
    assertTransient(salesOrder1A);

    msg("Creating salesOrder2");
    SalesOrder salesOrder2A = getModel1Factory().createSalesOrder();
    assertTransient(salesOrder2A);
    salesOrder2A.setId(2);
    salesOrder2A.setCustomer(customerA);
    assertTransient(salesOrder2A);

    msg("Creating company2");
    Company company2A = getModel1Factory().createCompany();
    company2A.getSalesOrders().add(salesOrder1A);
    company2A.getSalesOrders().add(salesOrder2A);

    msg("Creating resource2");
    CDOResource resource2A = transaction.createResource("/test2");

    msg("Adding company");
    resource2A.getContents().add(company2A);

    // ************************************************************* //

    msg("Committing");
    transaction.commit();

    assertEquals(2, customerA.getSalesOrders().size());

    // ************************************************************* //

    msg("Attaching viewB");
    CDOView viewB = session.openTransaction();

    msg("Loading resource1");
    CDOResource resource1B = viewB.getResource("/test1");
    assertProxy(resource1B);

    EList<EObject> contents = resource1B.getContents();
    Company company1B = (Company)contents.get(0);
    assertClean(company1B, viewB);
    assertClean(resource1B, viewB);
    assertContent(resource1B, company1B);

    Customer customerB = company1B.getCustomers().get(0);
    assertClean(customerB, viewB);
    assertClean(company1B, viewB);
    assertContent(company1B, customerB);

    SalesOrder salesOrder1B = customerB.getSalesOrders().get(0);
    assertClean(salesOrder1B, viewB);

    SalesOrder salesOrder2B = customerB.getSalesOrders().get(1);
    assertClean(salesOrder2B, viewB);
  }
}
