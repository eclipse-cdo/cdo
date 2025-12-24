/*
 * Copyright (c) 2010-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 293283 / Bug 314387 - Failed writes on CDOObjects leave bad featureDeltas in transaction
 *
 * @author Caspar De Groot
 */
public class Bugzilla_293283_Test extends AbstractCDOTest
{
  private CDOSession session;

  private CDOTransaction tx;

  private Order order1;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    session = openSession();
    tx = session.openTransaction();
    CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1")); //$NON-NLS-1$
    r1.getContents().clear();

    order1 = getModel1Factory().createPurchaseOrder();
    OrderDetail detail1 = getModel1Factory().createOrderDetail();
    OrderDetail detail2 = getModel1Factory().createOrderDetail();
    order1.getOrderDetails().add(detail1);
    order1.getOrderDetails().add(detail2);
    r1.getContents().add(order1);
    tx.commit();
  }

  @Override
  public void tearDown() throws Exception
  {
    tx.close();
    tx = null;

    session.close();
    session = null;

    order1 = null;
    super.tearDown();
  }

  public void test1()
  {
    test(Action.ADD);
  }

  public void test2()
  {
    test(Action.SET);
  }

  public void test3()
  {
    test(Action.MOVE1);
  }

  public void test4()
  {
    test(Action.MOVE2);
  }

  public void test5()
  {
    test(Action.REMOVE1);
  }

  public void test6()
  {
    test(Action.REMOVE2);
  }

  private void test(Action action)
  {
    try
    {
      switch (action)
      {
      case ADD:
      {
        OrderDetail newDetail = getModel1Factory().createOrderDetail();
        order1.getOrderDetails().add(3, newDetail);
        break;
      }

      case MOVE1:
        order1.getOrderDetails().move(0, 3);
        break;

      case MOVE2:
        order1.getOrderDetails().move(3, 0);
        break;

      case REMOVE1:
        order1.getOrderDetails().remove(3);
        break;

      case REMOVE2:
        order1.getOrderDetails().remove(-1);
        break;

      case SET:
      {
        OrderDetail newDetail = getModel1Factory().createOrderDetail();
        order1.getOrderDetails().set(3, newDetail);
        break;
      }
      }

      fail("Should have thrown " + IndexOutOfBoundsException.class.getSimpleName()); //$NON-NLS-1$
    }
    catch (IndexOutOfBoundsException ex)
    {
      // Good
    }

    try
    {
      tx.commit();
    }
    catch (Exception e)
    {
      fail("Should have committed cleanly, but failed with " + e.getClass().getName() + "\n" + e.getMessage());
    }
  }

  private enum Action
  {
    ADD, MOVE1, MOVE2, REMOVE1, REMOVE2, SET
  }
}
