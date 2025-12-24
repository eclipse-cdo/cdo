/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Torsten Scholz - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Torsten Scholz <scholz@kdo.de>
 */
public class Bugzilla_396181_Test extends AbstractCDOTest
{
  public void testCreatePersist() throws Exception
  {
    // Create an OrderDetail and add it to the resource
    final CDOSession session1 = openSession();
    final CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.getOrCreateResource(getResourcePath("/res1"));

    OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();
    resource1.getContents().add(orderDetail);
    transaction1.commit();

    // Create a SalesOrder an add it the resource in another transaction, commit the transaction
    final CDOSession session2 = openSession();
    final CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/res1"));

    Order order = Model1Factory.eINSTANCE.createSalesOrder();
    resource2.getContents().add(order);
    commitAndSync(transaction2, transaction1);

    // Query the SalesOrder in the first opened transaction, add it to the OrderDetail, commit the first transaction
    Order queriedOrder = (Order)transaction1.getObject(((CDOObject)order).cdoID());
    orderDetail.setOrder(queriedOrder);
    transaction1.commit();
  }
}
