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
package org.eclipse.emf.cdo.examples.hibernate.client;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * A quick test to show creating and persisting some data in the repository. To run these tests the CDO server has to be
 * started on tcp port 2036. This can be accomplished by using the CDOHibernateServer.launch configuration which makes
 * use of the cdo-server.xml config file in the config directory.
 * <p/>
 * The connection logic resides in the {@link BaseTest} parent class.
 *
 * @author Martin Taal
 */
public class QuickStartTest extends BaseTest
{

  /**
   * Simple test that opens a connection gets a resource and creates an address
   */
  public void testCreatePersist() throws Exception
  {
    // first create an address and persist it
    final String addressName = "name " + System.currentTimeMillis(); //$NON-NLS-1$
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      // get/create a resource
      CDOResource resource = transaction.getOrCreateResource("/res1"); //$NON-NLS-1$

      // clear any previous data
      resource.getContents().clear();

      final Company address = CompanyFactory.eINSTANCE.createCompany();
      address.setCity("test"); //$NON-NLS-1$
      address.setName(addressName);
      address.setStreet("test"); //$NON-NLS-1$
      resource.getContents().add(address);

      transaction.commit();
    }

    // read back and do some tests
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/res1"); //$NON-NLS-1$
      assertEquals(true, resource.getContents().get(0) instanceof Company);
      assertEquals(1, resource.getContents().size());
      final Company address = (Company)resource.getContents().get(0);
      assertEquals(addressName, address.getName());
      transaction.commit();
    }
  }
}
