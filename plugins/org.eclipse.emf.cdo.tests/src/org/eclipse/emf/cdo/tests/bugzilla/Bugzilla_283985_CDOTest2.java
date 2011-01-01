/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_283985_CDOTest2 extends AbstractCDOTest
{
  private Order order1, order2;

  private OrderDetail detail1, detail2, detail3, detail4;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    Model1Factory factory = Model1Factory.eINSTANCE;

    order1 = factory.createOrder();
    order2 = factory.createOrder();
    detail1 = factory.createOrderDetail();
    detail2 = factory.createOrderDetail();
    detail3 = factory.createOrderDetail();
    detail4 = factory.createOrderDetail();

    order1.getOrderDetails().add(detail1);
    order1.getOrderDetails().add(detail2);
    order1.getOrderDetails().add(detail3);
    order1.getOrderDetails().add(detail4);
  }

  public void testMoveOnce() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getOrCreateResource("/r1");
      r1.getContents().clear();
      r1.getContents().add(order1);
      r1.getContents().add(order2);

      boolean contains = order1.getOrderDetails().contains(detail1);
      assertTrue(contains);

      // Commit so that everything gets a CDOID
      tx.commit();

      contains = order1.getOrderDetails().contains(detail1);
      assertTrue(contains);

      tx.commit();

      order1.getOrderDetails().remove(detail1);
      order2.getOrderDetails().add(detail1);
      contains = order2.getOrderDetails().contains(detail1);
      assertTrue(contains);

      tx.commit();

      tx.close();
      session.close();
    }

    {
      // Check if all OK if we reload in a new session
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getResource("/r1");
      order1 = (Order)r1.getContents().get(0);
      order2 = (Order)r1.getContents().get(1);

      // lookup detail object in new transaction
      detail1 = (OrderDetail)tx.getObject(CDOUtil.getCDOObject(detail1).cdoID());

      boolean contains1 = order1.getOrderDetails().contains(detail1);
      boolean contains2 = order2.getOrderDetails().contains(detail1);
      assertFalse(contains1);
      assertTrue(contains2);

      tx.close();
      session.close();
    }
  }

  public void testMoveOnceAndBack() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getOrCreateResource("/r1");
      r1.getContents().clear();
      r1.getContents().add(order1);
      r1.getContents().add(order2);

      boolean contains = order1.getOrderDetails().contains(detail1);
      assertTrue(contains);

      // Commit so that everything gets a CDOID
      tx.commit();

      contains = order1.getOrderDetails().contains(detail1);
      assertTrue(contains);

      tx.commit();

      order1.getOrderDetails().remove(detail1);
      order2.getOrderDetails().add(detail1);
      contains = order2.getOrderDetails().contains(detail1);
      assertTrue(contains);

      order2.getOrderDetails().remove(detail1);
      order1.getOrderDetails().add(detail1);
      contains = order1.getOrderDetails().contains(detail1);
      assertTrue(contains);

      tx.commit();

      tx.close();
      session.close();
    }

    {
      // Check if all OK if we reload in a new session
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getResource("/r1");
      order1 = (Order)r1.getContents().get(0);
      order2 = (Order)r1.getContents().get(1);
      detail1 = (OrderDetail)tx.getObject(CDOUtil.getCDOObject(detail1).cdoID());

      boolean contains1 = order1.getOrderDetails().contains(detail1);
      boolean contains2 = order2.getOrderDetails().contains(detail1);
      assertFalse(contains2);
      assertTrue(contains1);

      tx.close();
      session.close();
    }
  }
}
