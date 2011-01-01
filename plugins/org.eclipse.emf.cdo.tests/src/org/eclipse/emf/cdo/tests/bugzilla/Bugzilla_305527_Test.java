/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.mango.MangoValueList;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;

/**
 * @author Martin Fluegge
 * @since 4.0
 */
public class Bugzilla_305527_Test extends AbstractCDOTest
{
  public void testAvoidReferencingDifferentViews() throws CommitException
  {
    skipUnlessAuditing();
    final CDOSession session = openSession();
    long commitTime;

    {
      MangoValue mangoValue = getMangoFactory().createMangoValue();
      mangoValue.setName("1");

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1"); //$NON-NLS-1$
      resource.getContents().add(mangoValue);
      commitTime = transaction.commit().getTimeStamp();

      mangoValue.setName("2");
      transaction.commit();
      transaction.close();
    }

    CDOView audit = session.openView(commitTime);
    CDOResource auditResource = audit.getResource("/res1");
    MangoValue mangoValue = (MangoValue)auditResource.getContents().get(0);
    assertEquals("1", mangoValue.getName());

    MangoValueList mangoList = getMangoFactory().createMangoValueList();
    mangoList.getValues().add(mangoValue);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/res1"); //$NON-NLS-1$

    try
    {
      resource.getContents().add(mangoList);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  /**
   * Martin: What's this test supposed to test?
   */
  public void _testOppositeSettingValidation() throws CommitException
  {
    CDOSession openSession = openSession();
    CDOTransaction transaction1 = openSession.openTransaction();
    CDOResource resource1 = transaction1.createResource("test");

    Customer customer1 = getModel1Factory().createCustomer();
    Customer customer2 = getModel1Factory().createCustomer();
    SalesOrder salesOrder1 = getModel1Factory().createSalesOrder();
    salesOrder1.setCustomer(customer1);

    resource1.getContents().add(customer1);
    resource1.getContents().add(customer2);
    resource1.getContents().add(salesOrder1);

    Category createCategory = getModel1Factory().createCategory();

    resource1.getContents().add(createCategory);
    resource1.getContents().add(getModel1Factory().createProduct1());
    transaction1.commit();

    CDOTransaction transaction2 = openSession.openTransaction();
    CDOResource resource2 = transaction2.getResource("test");

    SalesOrder salesOrderFound = (SalesOrder)resource2.getContents().get(2);
    resource2.getContents().add(salesOrderFound);

    assertEquals(CDOUtil.getCDOObject(salesOrder1).cdoID(), CDOUtil.getCDOObject(salesOrderFound).cdoID());
    assertEquals(CDOUtil.getCDOObject(salesOrder1).cdoID(),
        CDOUtil.getCDOObject(salesOrderFound.getCustomer().getSalesOrders().get(0)).cdoID());

    assertNotSame(CDOUtil.getCDOObject(salesOrderFound).cdoView(), CDOUtil.getCDOObject(customer2).cdoView());

    // Expecting exception here
    salesOrderFound.setCustomer(customer2);

    EList<SalesOrder> salesOrders = salesOrderFound.getCustomer().getSalesOrders();

    // Model consistency is broken
    assertEquals(true, salesOrders.size() == 1);
    assertEquals(CDOUtil.getCDOObject(salesOrderFound).cdoID(), CDOUtil.getCDOObject(salesOrders.get(0)).cdoID());
    fail("Should not allow to mix elements from the different view");
  }
}
