/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * @author Simon McDuff
 */
public class ConflictResolverMergingTest extends ConflictResolverTest
{
  public void testMergeLocalChangesPerFeature_Bug1() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    contents1.add(getModel1Factory().createAddress());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);
  }

  public void testMergeLocalChangesPerFeature_Bug2() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    contents1.add(getModel1Factory().createAddress());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction2, transaction1);
    commitAndSync(transaction1, transaction2);
  }

  public void testMerge_ManyValue() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    // CLIENT-1 creates sales order
    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    SalesOrder salesOrder1 = getModel1Factory().createSalesOrder();
    EList<OrderDetail> orderDetails1 = salesOrder1.getOrderDetails();

    contents1.add(salesOrder1);
    transaction1.commit();

    // CLIENT-2 loads sales order
    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    SalesOrder salesOrder2 = (SalesOrder)contents2.get(0);
    EList<OrderDetail> orderDetails2 = salesOrder2.getOrderDetails();

    // CLIENT-1 adds order detail
    OrderDetail orderDetail1 = getModel1Factory().createOrderDetail();
    orderDetails1.add(orderDetail1);

    // CLIENT-2 adds order detail
    OrderDetail orderDetail2 = getModel1Factory().createOrderDetail();
    orderDetails2.add(orderDetail2);

    // CLIENT-1 commits and waits for CLIENT-2's conflict resolver
    commitAndSync(transaction1, transaction2);

    // CLIENT-2 commits and waits for CLIENT-1's conflict resolver (nothing to do there)
    commitAndSync(transaction2, transaction1);

    assertEquals(2, orderDetails1.size());
    assertEquals(CDOUtil.getCDOObject(orderDetail2).cdoID(), CDOUtil.getCDOObject(orderDetails1.get(0)).cdoID());
    assertEquals(CDOUtil.getCDOObject(orderDetail1).cdoID(), CDOUtil.getCDOObject(orderDetails1.get(1)).cdoID());
  }

  @Override
  protected CDOConflictResolver createConflictResolver()
  {
    return new CDOMergingConflictResolver();
  }
}
