/*
 * Copyright (c) 2007-2013, 2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - test case for cross-reference to container
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * @author Eike Stepper
 */
public class CrossReferenceTest extends AbstractCDOTest
{
  public void testLoadViaContainment() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

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
    CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    assertEquals(2, customerA.getSalesOrders().size());

    // ************************************************************* //

    msg("Attaching viewB");
    CDOView viewB = session.openTransaction();

    msg("Loading resource");
    CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));
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
    CDOSession session = openSession();

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
    CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    assertEquals(2, customerA.getSalesOrders().size());

    // ************************************************************* //

    msg("Attaching viewB");
    CDOView viewB = session.openTransaction();

    msg("Loading resource");
    CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));
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
    CDOSession session = openSession();

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
    CDOResource resource1A = transaction.createResource(getResourcePath("/test1"));

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
    CDOResource resource2A = transaction.createResource(getResourcePath("/test2"));

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
    CDOResource resource1B = viewB.getResource(getResourcePath("/test1"));
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

  public void testDetachXRef() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    transaction.commit();

    company.getCustomers().remove(customer);

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
    }
  }

  public void testDetachXRefReattach() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    transaction.commit();

    company.getCustomers().remove(customer);
    company.getCustomers().add(customer);
    assertEquals(1, company.getCustomers().size());
    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testDetachXRefExternal() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    transaction.commit();

    Resource externalResource = new ResourceImpl(URI.createFileURI("/x/y/z"));
    transaction.getResourceSet().getResources().add(externalResource);
    externalResource.getContents().add(customer);

    transaction.commit();
    CDORevisionData data = CDOUtil.getCDOObject(salesOrder).cdoRevision().data();
    CDOID id = (CDOID)data.get(getModel1Package().getSalesOrder_Customer(), 0);
    assertEquals(true, id.isExternal());
  }

  public void _testDetachXRefExternalReattach() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    transaction.commit();

    Resource externalResource = new ResourceImpl(URI.createFileURI("/x/y/z"));
    transaction.getResourceSet().getResources().add(externalResource);
    externalResource.getContents().add(customer);

    transaction.commit();
    CDORevisionData data = CDOUtil.getCDOObject(salesOrder).cdoRevision().data();
    CDOID id = (CDOID)data.get(getModel1Package().getSalesOrder_Customer(), 0);
    assertEquals(true, id.isExternal());

    company.getCustomers().add(customer);
    transaction.commit();
    data = CDOUtil.getCDOObject(salesOrder).cdoRevision().data();
    id = (CDOID)data.get(getModel1Package().getSalesOrder_Customer(), 0);
    assertEquals(false, id.isExternal());
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testNewMakeExternal() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    // DO NOT: transaction.commit();

    Resource externalResource = new ResourceImpl(URI.createFileURI("/x/y/z"));
    transaction.getResourceSet().getResources().add(externalResource);
    externalResource.getContents().add(customer);

    transaction.commit();
    CDORevisionData data = CDOUtil.getCDOObject(salesOrder).cdoRevision().data();
    CDOID id = (CDOID)data.get(getModel1Package().getSalesOrder_Customer(), 0);
    assertEquals(true, id.isExternal());
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testExternalMakeNew() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    // DO NOT: company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    // DO NOT: transaction.commit();

    Resource externalResource = new ResourceImpl(URI.createFileURI("/x/y/z"));
    transaction.getResourceSet().getResources().add(externalResource);
    externalResource.getContents().add(customer);

    company.getCustomers().add(customer);

    transaction.commit();
    CDORevisionData data = CDOUtil.getCDOObject(salesOrder).cdoRevision().data();
    CDOID id = (CDOID)data.get(getModel1Package().getSalesOrder_Customer(), 0);

    assertEquals(true, id.isExternal());
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testExternalMakeDangling() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);

    Company company = getModel1Factory().createCompany();
    // DO NOT: company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/company/resource"));
    resource.getContents().add(company);
    // DO NOT: transaction.commit();

    Resource externalResource = new ResourceImpl(URI.createFileURI("/x/y/z"));
    transaction.getResourceSet().getResources().add(externalResource);
    externalResource.getContents().add(customer);

    transaction.commit();
    CDORevisionData data = CDOUtil.getCDOObject(salesOrder).cdoRevision().data();
    CDOID id = (CDOID)data.get(getModel1Package().getSalesOrder_Customer(), 0);
    assertEquals(true, id.isExternal());

    externalResource.getContents().remove(customer);
    transaction.commit(); // Should be dangling reference now, but we can not detect ;-(
  }

  /**
   * Bug 369253: bidirectional cross-reference between containing and contained object that
   * is not a containment reference.
   */
  public void testCrossCreferenceBetweenContainerAndContained() throws Exception
  {
    Parent parent = getModel5Factory().createParent();
    Child blackSheep = getModel5Factory().createChild();
    Child whiteSheep = getModel5Factory().createChild();

    // children *is* a containment reference
    parent.getChildren().add(blackSheep);
    parent.getChildren().add(whiteSheep);

    // favourite *is not* a containment reference
    parent.setFavourite(whiteSheep);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(parent);
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    EList<EObject> contents = resource.getContents();
    assertEquals(1, contents.size());

    Parent newParent = (Parent)contents.get(0);
    assertEquals(2, newParent.getChildren().size());
    assertSame(newParent.getChildren().get(1), newParent.getFavourite());
    assertSame(newParent, newParent.getChildren().get(1).getPreferredBy());
  }
}
