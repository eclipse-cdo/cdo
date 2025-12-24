/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * Bug 419574 - NPE in CDOMergingConflictResolver.
 *
 * @author Eike Stepper
 */
public class Bugzilla_419574_Test extends AbstractCDOTest
{
  public void testConflict() throws Exception
  {
    String resourcePath = getResourcePath("/res1");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      Company company = getModel1Factory().createCompany();
      company.setName("Central");

      transaction.createResource(resourcePath).getContents().add(company);
      transaction.commit();
      session.close();
    }

    CDOSession session1 = openSession();
    session1.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addConflictResolver(new CDOMergingConflictResolver());
    // transaction1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.options().addConflictResolver(new CDOMergingConflictResolver());
    // transaction2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    Company company1 = (Company)transaction1.getResource(resourcePath).getContents().get(0);
    Customer c1 = getModel1Factory().createCustomer();
    company1.getCustomers().add(c1);

    Company company2 = (Company)transaction2.getResource(resourcePath).getContents().get(0);
    Customer c2 = getModel1Factory().createCustomer();
    company2.getCustomers().add(c2);

    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    SalesOrder s1 = getModel1Factory().createSalesOrder();
    s1.setCustomer(c1);
    company1.getSalesOrders().add(s1);

    SalesOrder s2 = getModel1Factory().createSalesOrder();
    s2.setCustomer(c2);
    company2.getSalesOrders().add(s2);

    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);
  }
}
