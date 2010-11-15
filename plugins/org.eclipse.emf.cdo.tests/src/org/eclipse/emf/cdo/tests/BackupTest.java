/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerBackup;
import org.eclipse.emf.cdo.server.CDOServerBackup.XML;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

  public void testBackupExport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit();
    session.close();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XML backup = new CDOServerBackup.XML(getRepository());
    backup.exportRepository(baos);
    System.out.println(baos.toString());
  }

  public void testBackupImport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit();
    session.close();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XML backup = new CDOServerBackup.XML(getRepository());
    backup.exportRepository(baos);
    System.out.println(baos.toString());

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    backup = new CDOServerBackup.XML(getRepository("repo2"));
    backup.importRepository(bais);
    sleep(10000000);
  }

  private Customer createCustomer(String name)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName(name);
    return customer;
  }
}
