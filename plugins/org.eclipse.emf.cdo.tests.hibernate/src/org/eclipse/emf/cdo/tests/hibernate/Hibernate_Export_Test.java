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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.legacy.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.io.ByteArrayOutputStream;

/**
 * @author Martin Taal
 */
@CleanRepositoriesBefore
public class Hibernate_Export_Test extends AbstractCDOTest
{
  public void testExport() throws Exception
  {

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Customer customer = Model1Factory.eINSTANCE.createCustomer();
      customer.setName("1");
      customer.setCity("1");
      customer.setStreet("1");
      resource.getContents().add(customer);
      assertNotNull(resource);
      transaction.commit();
      session.close();
    }
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      Customer customer = (Customer)resource.getContents().get(0);
      customer.setStreet("2");
      transaction.createResource(getResourcePath("/res2"));
      transaction.commit();
      session.close();
    }

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    final String result = baos.toString();
    System.out.println(result);
  }
}
