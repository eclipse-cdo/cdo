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

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    SalesOrder c1 = getModel1Factory().createSalesOrder();
    contents1.add(c1);
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    SalesOrder c2 = (SalesOrder)contents2.get(0);

    OrderDetail s1 = getModel1Factory().createOrderDetail();
    c1.getOrderDetails().add(s1);

    OrderDetail s2 = getModel1Factory().createOrderDetail();
    c2.getOrderDetails().add(s2);

    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    EList<OrderDetail> list = c1.getOrderDetails();
    System.out.println(list);
    assertEquals(2, list.size());
    assertEquals(CDOUtil.getCDOObject(s2).cdoID(), CDOUtil.getCDOObject(list.get(0)).cdoID());
    assertEquals(CDOUtil.getCDOObject(s1).cdoID(), CDOUtil.getCDOObject(list.get(1)).cdoID());
  }

  @SuppressWarnings("deprecation")
  @Override
  protected CDOConflictResolver createConflictResolver()
  {
    return new org.eclipse.emf.spi.cdo.CDOMergingConflictResolver();
  }
}
