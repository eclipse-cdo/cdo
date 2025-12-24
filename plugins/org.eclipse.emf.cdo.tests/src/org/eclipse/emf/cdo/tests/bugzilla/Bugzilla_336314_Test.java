/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Bug 336314 - Partial commits choke on CDOMoveFeatureDeltas
 *
 * @author Caspar De Groot
 */
public class Bugzilla_336314_Test extends AbstractCDOTest
{
  public void test1() throws CommitException
  {
    test(new MoveIt()
    {
      @Override
      public void move(SalesOrder order, OrderDetail detail)
      {
        order.getOrderDetails().remove(detail);
        order.getOrderDetails().add(detail);
      }
    });
  }

  public void test2() throws CommitException
  {
    test(new MoveIt()
    {
      @Override
      public void move(SalesOrder order, OrderDetail detail)
      {
        order.getOrderDetails().move(0, 1);
      }
    });
  }

  private static interface MoveIt
  {
    void move(SalesOrder order, OrderDetail detail);
  }

  private void test(MoveIt moveIt) throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    assertEquals(true, getModel1Package().getOrder_OrderDetails().isOrdered());

    Model1Factory mf = getModel1Factory();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    OrderDetail detailY = mf.createOrderDetail();
    detailY.setPrice(1.0f);

    OrderDetail detailZ = mf.createOrderDetail();
    detailY.setPrice(2.0f);

    SalesOrder orderX = mf.createSalesOrder();
    orderX.getOrderDetails().add(detailY);
    orderX.getOrderDetails().add(detailZ);

    resource.getContents().add(orderX);

    // We need another object to make the later commit partial
    Product1 product = mf.createProduct1();
    product.setName("abc");
    resource.getContents().add(product);

    tx.commit();

    // Remove then add back, so as to change ordering
    moveIt.move(orderX, detailY);

    // Partial commit
    Set<EObject> committables = new HashSet<>(1);
    committables.add(orderX);
    tx.setCommittables(committables);

    // Make other object dirty too, so that commit becomes 'partial'
    product.setName("def");

    tx.commit();

    session.close();
  }
}
