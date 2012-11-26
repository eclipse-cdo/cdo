/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Collections;

/**
 * @author Michael Tkacz
 */
public class HibernateBugzilla_333473_Test extends AbstractCDOTest
{
  @CleanRepositoriesBefore
  public void testRemovalFirstAttempt() throws Exception
  {
    testRemoval(false);
  }

  @CleanRepositoriesBefore
  public void testRemovalSecondAttempt() throws Exception
  {
    testRemoval(true);
  }

  private void testRemoval(boolean removeOrderDetailFromResourceBeforeRemoval) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource orderDetailResource = transaction.getOrCreateResource(getResourcePath("/orderDetail")); //$NON-NLS-1$
    orderDetailResource.getContents().clear();

    final OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1.0f);
    orderDetailResource.getContents().add(orderDetail);

    CDOResource orderResource = transaction.getOrCreateResource(getResourcePath("/order")); //$NON-NLS-1$
    orderResource.getContents().clear();

    SalesOrder order = getModel1Factory().createSalesOrder();
    order.getOrderDetails().add(orderDetail);
    orderResource.getContents().add(order);

    transaction.commit();
    transaction.close();
    session.close();

    // so far so good

    session = openSession();
    transaction = session.openTransaction();
    orderResource = transaction.getResource(getResourcePath("/order")); //$NON-NLS-1$
    orderDetailResource = transaction.getResource(getResourcePath("/orderDetail")); //$NON-NLS-1$

    order = (SalesOrder)orderResource.getContents().get(0);
    order.getOrderDetails().clear();

    if (removeOrderDetailFromResourceBeforeRemoval)
    {
      orderDetailResource.getContents().clear();
    }

    orderDetailResource.delete(Collections.EMPTY_MAP);
    transaction.commit();
    transaction.close();
    session.close();
  }
}
