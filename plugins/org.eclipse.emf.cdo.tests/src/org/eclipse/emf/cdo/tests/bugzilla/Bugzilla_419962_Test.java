/*
 * Copyright (c) 2013, 2014 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * Bug 419962 - CommitConflictException after CDOMergingConflictResolver.resolveConflicts().
 *
 * @author Eike Stepper
 */
public class Bugzilla_419962_Test extends AbstractCDOTest
{
  private CDOSession sessionA;

  private CDOSession sessionB;

  private CDOTransaction transactionA;

  private CDOTransaction transactionB;

  private Company companyA;

  private Company companyB;

  public Bugzilla_419962_Test()
  {
  }

  @Override
  public CDOSession openSession()
  {
    CDOSession session = super.openSession();
    session.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    return session;
  }

  @Override
  protected void doSetUp() throws Exception
  {
    OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
    super.doSetUp();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Company company = getModel1Factory().createCompany();
    company.setName("Central");

    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(company);
    transaction.commit();

    transaction.close();
    session.close();

    sessionA = openSession();
    sessionB = openSession();

    transactionA = sessionA.openTransaction();
    transactionA.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    transactionB = sessionB.openTransaction();
    transactionB.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    transactionA.options().addConflictResolver(createConflictResolver());
    transactionB.options().addConflictResolver(createConflictResolver());

    companyA = (Company)transactionA.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);
    companyB = (Company)transactionB.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);
  }

  @Override
  public void tearDown() throws Exception
  {
    transactionA.close();
    transactionB.close();
    sessionA.close();
    sessionB.close();

    super.tearDown();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testConflict() throws Exception
  {
    Customer customer5A = getModel1Factory().createCustomer();
    companyA.getCustomers().add(customer5A);

    Customer customer6B = getModel1Factory().createCustomer();
    companyB.getCustomers().add(customer6B);

    commitAndSync(transactionA, transactionB);
    commitAndSync(transactionB, transactionA);

    Customer customer5B = transactionB.getObject(customer5A);
    Customer customer6A = transactionA.getObject(customer6B);

    SalesOrder s1 = getModel1Factory().createSalesOrder();
    companyA.getSalesOrders().add(s1);
    customer5A.getSalesOrders().add(s1);

    SalesOrder s2 = getModel1Factory().createSalesOrder();
    companyB.getSalesOrders().add(s2);
    customer6B.getSalesOrders().add(s2);

    commitAndSync(transactionA, transactionB);
    assertEquals(1, companyA.getSalesOrders().size()); // Not yet committed
    assertEquals(2, companyB.getSalesOrders().size());
    checkCustomer(customer5A, 1, 2, CDOState.CLEAN);
    checkCustomer(customer6B, 1, 1, CDOState.DIRTY);
    checkCustomer(customer5B, 1, 2, CDOState.CLEAN);
    checkCustomer(customer6A, 0, 1, CDOState.CLEAN); // Not yet committed

    commitAndSync(transactionB, transactionA);
    assertEquals(2, companyA.getSalesOrders().size());
    assertEquals(2, companyB.getSalesOrders().size());
    checkCustomer(customer5A, 1, 2, CDOState.CLEAN);
    checkCustomer(customer6B, 1, 2, CDOState.CLEAN);
    checkCustomer(customer5B, 1, 2, CDOState.CLEAN);
    checkCustomer(customer6A, 1, 2, CDOState.CLEAN);

    SalesOrder s3 = getModel1Factory().createSalesOrder();
    companyA.getSalesOrders().add(s3);
    customer5A.getSalesOrders().add(s3);

    SalesOrder s4 = getModel1Factory().createSalesOrder();
    companyB.getSalesOrders().add(s4);
    customer6B.getSalesOrders().add(s4);

    commitAndSync(transactionA, transactionB);
    assertEquals(3, companyA.getSalesOrders().size()); // Not yet committed
    assertEquals(4, companyB.getSalesOrders().size());
    checkCustomer(customer5A, 2, 3, CDOState.CLEAN);
    checkCustomer(customer6B, 2, 2, CDOState.DIRTY);
    checkCustomer(customer5B, 2, 3, CDOState.CLEAN);
    checkCustomer(customer6A, 1, 2, CDOState.CLEAN); // Not yet committed

    commitAndSync(transactionB, transactionA);
    assertEquals(4, companyA.getSalesOrders().size());
    assertEquals(4, companyB.getSalesOrders().size());
    checkCustomer(customer5A, 2, 3, CDOState.CLEAN);
    checkCustomer(customer6B, 2, 3, CDOState.CLEAN);
    checkCustomer(customer5B, 2, 3, CDOState.CLEAN);
    checkCustomer(customer6A, 2, 3, CDOState.CLEAN);
  }

  private static void checkCustomer(Customer customer, int salesOrders, int version, CDOState state)
  {
    assertEquals("Wrong number of sales orders", salesOrders, customer.getSalesOrders().size());

    CDOObject object = CDOUtil.getCDOObject(customer);
    assertEquals("Wrong version", version, object.cdoRevision(true).getVersion());
    assertEquals("Wrong state", state, object.cdoState());
  }

  private CDOConflictResolver createConflictResolver()
  {
    return new CDOMergingConflictResolver(new DefaultCDOMerger.PerFeature.ManyValued());
  }
}
