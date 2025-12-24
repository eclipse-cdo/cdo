/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_283985_2_Test extends AbstractCDOTest
{
  private Order order1, order2;

  private OrderDetail detail1, detail2, detail3, detail4;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    order1 = getModel1Factory().createPurchaseOrder();
    order2 = getModel1Factory().createPurchaseOrder();
    detail1 = getModel1Factory().createOrderDetail();
    detail2 = getModel1Factory().createOrderDetail();
    detail3 = getModel1Factory().createOrderDetail();
    detail4 = getModel1Factory().createOrderDetail();

    order1.getOrderDetails().add(detail1);
    order1.getOrderDetails().add(detail2);
    order1.getOrderDetails().add(detail3);
    order1.getOrderDetails().add(detail4);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    order1 = null;
    order2 = null;
    detail1 = null;
    detail2 = null;
    detail3 = null;
    detail4 = null;
    super.doTearDown();
  }

  public void testMoveOnce() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1"));
      r1.getContents().clear();
      r1.getContents().add(order1);
      r1.getContents().add(order2);

      boolean contains = order1.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      // Commit so that everything gets a CDOID
      tx.commit();

      contains = order1.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      tx.commit();

      order1.getOrderDetails().remove(detail1);
      order2.getOrderDetails().add(detail1);
      contains = order2.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      tx.commit();

      tx.close();
      session.close();
    }

    {
      // Check if all OK if we reload in a new session
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getResource(getResourcePath("/r1"));
      order1 = (Order)r1.getContents().get(0);
      order2 = (Order)r1.getContents().get(1);

      // lookup detail object in new transaction
      detail1 = (OrderDetail)CDOUtil.getEObject(tx.getObject(CDOUtil.getCDOObject(detail1).cdoID()));

      boolean contains1 = order1.getOrderDetails().contains(detail1);
      boolean contains2 = order2.getOrderDetails().contains(detail1);
      assertEquals(false, contains1);
      assertEquals(true, contains2);

      tx.close();
      session.close();
    }
  }

  public void testMoveOnceAndBack() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1"));
      r1.getContents().clear();
      r1.getContents().add(order1);
      r1.getContents().add(order2);

      boolean contains = order1.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      // Commit so that everything gets a CDOID
      tx.commit();

      contains = order1.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      tx.commit();

      order1.getOrderDetails().remove(detail1);
      order2.getOrderDetails().add(detail1);
      contains = order2.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      order2.getOrderDetails().remove(detail1);
      order1.getOrderDetails().add(detail1);
      contains = order1.getOrderDetails().contains(detail1);
      assertEquals(true, contains);

      tx.commit();

      tx.close();
      session.close();
    }

    {
      // Check if all OK if we reload in a new session
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource r1 = tx.getResource(getResourcePath("/r1"));
      order1 = (Order)r1.getContents().get(0);
      order2 = (Order)r1.getContents().get(1);
      detail1 = (OrderDetail)CDOUtil.getEObject(tx.getObject(CDOUtil.getCDOObject(detail1).cdoID()));

      boolean contains1 = order1.getOrderDetails().contains(detail1);
      boolean contains2 = order2.getOrderDetails().contains(detail1);
      assertEquals(false, contains2);
      assertEquals(true, contains1);

      tx.close();
      session.close();
    }
  }
}
