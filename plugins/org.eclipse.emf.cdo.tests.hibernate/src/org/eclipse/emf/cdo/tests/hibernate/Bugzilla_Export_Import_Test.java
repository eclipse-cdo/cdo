/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Tests:
 * 
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=381013
 * 
 * @author Martin Taal
 */
@CleanRepositoriesBefore
public class Bugzilla_Export_Import_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
    skipStoreWithoutRawAccess();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    super.doTearDown();
  }

  public void testExport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());
  }

  private void useAfterImport(String repoName) throws CommitException
  {
    CDOSession session2 = openSession(repoName);
    CDOTransaction transaction2 = session2.openTransaction();

    // Read all repo contents
    TreeIterator<EObject> iter = transaction2.getRootResource().getAllContents();
    while (iter.hasNext())
    {
      iter.next();
    }

    // Add content from a new package
    CDOResource resource = transaction2.createResource(getResourcePath("/r1"));
    resource.getContents().add(getModel3Factory().createPolygon());
    transaction2.commit();

    session2.close();
  }

  public void testImport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    Customer eike = createCustomer("Eike");
    resource.getContents().add(eike);
    resource.getContents().add(createCustomer("Jos"));
    resource.getContents().add(createCustomer("Simon"));
    transaction.commit();
    SalesOrder salesOrder = createSalesOrder(eike);
    salesOrder.getOrderDetails().add(getModel1Factory().createOrderDetail());
    resource.getContents().add(salesOrder);
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);

    useAfterImport("repo2");
  }

  private Customer initExtResource(ResourceSet resourceSet)
  {
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    Resource extResource = resourceSet.createResource(URI.createURI("ext.xmi"));

    Customer customer = getModel1Factory().createCustomer();
    extResource.getContents().add(customer);
    return customer;
  }

  private Customer createCustomer(String name)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName(name);
    return customer;
  }

  private SalesOrder createSalesOrder(Customer customer)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);
    return salesOrder;
  }

}
