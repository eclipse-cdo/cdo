/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - https://bugs.eclipse.org/333260
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * @author Eike Stepper
 */
public class BackupTest extends AbstractCDOTest
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
    disableConsole();
    super.doTearDown();
  }

  public void testExport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit();
    session.close();

    IRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());
  }

  public void testExportDate() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    purchaseOrder.setDate(new Date(1234567));
    resource.getContents().add(purchaseOrder);
    transaction.commit();
    session.close();

    IRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());
  }

  private Customer createCustomer(String name)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName(name);
    return customer;
  }
}
