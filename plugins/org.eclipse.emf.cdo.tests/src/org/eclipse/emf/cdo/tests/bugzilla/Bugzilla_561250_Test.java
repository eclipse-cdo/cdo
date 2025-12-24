/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Robert Schulk - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EMap;

/**
 * Bug 561250 - First element in EMap is not always visible from another session.
 *
 * @author Robert Schulk
 */
public class Bugzilla_561250_Test extends AbstractCDOTest
{
  public void testMapInitialization() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    Customer customer1 = getModel1Factory().createCustomer();
    Product1 product1 = getModel1Factory().createProduct1();
    SalesOrder salesOrder1 = getModel1Factory().createSalesOrder();

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test"));
    resource1.getContents().add(customer1);
    resource1.getContents().add(product1);
    resource1.getContents().add(salesOrder1);
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOView view2 = session2.openView();
    Customer customer2 = view2.getObject(customer1);

    // Load customer2. Necessary before the next transaction1.commit() !
    EMap<Product1, SalesOrder> orderByProduct2 = customer2.getOrderByProduct();

    customer1.getOrderByProduct().put(product1, salesOrder1);
    commitAndSync(transaction1, view2); // Transitions customer2 from CLEAN to PROXY.

    Product1 product2 = view2.getObject(product1);
    assertNotNull("Could not find the recently added map entry.", orderByProduct2.get(product2));
  }
}
