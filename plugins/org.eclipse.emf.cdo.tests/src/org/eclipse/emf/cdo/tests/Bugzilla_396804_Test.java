/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Erdal Karaca - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * Test cases for CDOMergingConflictResolver. See bug 396804.
 *
 * @author Erdal Karaca
 */
public class Bugzilla_396804_Test extends AbstractCDOTest
{
  public void testParallelADD() throws Exception
  {
    String path = getResourcePath("/res1");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(path);
      transaction.commit();
      session.close();
    }

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());

    CDOResource resource1 = transaction1.getResource(path);
    transaction1.commit();

    resource1.getContents().add(Model1Factory.eINSTANCE.createSalesOrder());

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    CDOResource resource2 = transaction2.getResource(path);
    OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();
    resource2.getContents().add(orderDetail);
    commitAndSync(transaction2, transaction1);

    transaction1.commit();
  }

  public void testParallelRemoveYieldsUnexpectedObjectState() throws Exception
  {
    String path = getResourcePath("/res1");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      EList<EObject> contents = transaction.createResource(path).getContents();
      contents.add(Model1Factory.eINSTANCE.createOrderDetail());
      contents.add(Model1Factory.eINSTANCE.createOrderDetail());

      transaction.commit();
      session.close();
    }

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());

    EList<EObject> contents1 = transaction1.getResource(path).getContents();
    OrderDetail firstOrderDetail1 = (OrderDetail)contents1.get(0);
    Order order = Model1Factory.eINSTANCE.createSalesOrder();
    order.getOrderDetails().add(firstOrderDetail1); // change2: contents1.remove(0)
    contents1.add(order); // change3

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    EList<EObject> contents2 = transaction2.getResource(path).getContents();
    contents2.remove(1); // change1: lastOrderDetail2
    commitAndSync(transaction2, transaction1);

    transaction1.commit();
  }

  public void testParallelRemoveThrowsIOOBE() throws Exception
  {
    String path = getResourcePath("/res1");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(path);

      resource.getContents().add(Model1Factory.eINSTANCE.createOrderDetail());
      resource.getContents().add(Model1Factory.eINSTANCE.createOrderDetail());

      transaction.commit();
      session.close();
    }

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());

    CDOResource resource1 = transaction1.getResource(path);
    Order order = Model1Factory.eINSTANCE.createSalesOrder();
    resource1.getContents().add(order);

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    CDOResource resource2 = transaction2.getResource(path);
    OrderDetail lastOrderDetail = (OrderDetail)resource2.getContents().get(resource2.getContents().size() - 1);
    resource2.getContents().remove(lastOrderDetail);
    commitAndSync(transaction2, transaction1);

    transaction1.commit();
  }

  private CDOMergingConflictResolver createConflictResolver()
  {
    return new CDOMergingConflictResolver();
  }
}
