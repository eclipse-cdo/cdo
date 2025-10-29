/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ReferentialIntegrityException;

import java.util.Map;

/**
 * Bug 485394: Referential integrity check does not detect stale containment proxies
 *
 * @author Eike Stepper
 */
public class Bugzilla_485394_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
  }

  // With inverse list mappings there is no referential integrity violation in this case.
  @Skips("DB.inverse.lists")
  public void testReferentialIntegrityWithContainmentProxy() throws Exception
  {
    skipStoreWithoutQueryXRefs();
    disableConsole();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));
    CDOResource resource2 = transaction.createResource(getResourcePath("/test2"));

    Company company = getModel1Factory().createCompany();
    company.setName("Company");
    resource1.getContents().add(company);

    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Customer");
    company.getCustomers().add(customer);

    // Create the cross-resource containment proxy.
    resource2.getContents().add(customer);
    transaction.commit();

    resource2.delete(null);

    try
    {
      transaction.commit();
      fail("ReferentialIntegrityException expected");
    }
    catch (ReferentialIntegrityException expected)
    {
      // SUCCESS
    }
  }
}
