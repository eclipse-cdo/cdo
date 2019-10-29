/*
 * Copyright (c) 2008-2013, 2016-2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Address;
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
public class ConflictResolverTest extends AbstractCDOTest
{
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testMergeLocalChangesPerFeature_Basic() throws Exception
  {
    Address address = getModel1Factory().createAddress();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);

    address2.setCity("OTTAWA");

    address.setName("NAME1");

    // Resolver should be triggered.
    commitAndSync(transaction, transaction2);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  // Does not work in legacy as long as there is not getter interception
  public void testMergeLocalChangesPerFeature_BasicException() throws Exception
  {
    Address address = getModel1Factory().createAddress();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    final Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);

    address2.setCity("OTTAWA");

    address.setCity("NAME1");
    commitAndSync(transaction, transaction2);

    assertEquals(true, transaction2.hasConflict());
    assertEquals(true, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals("OTTAWA", address2.getCity());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testCDOMergingConflictResolver() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());

    Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);
    address2.setCity("OTTAWA");

    address.setName("NAME1");

    // Resolver should be triggered.
    commitAndSync(transaction, transaction2);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  public void testMergeLocalChangesPerFeature_Bug1() throws Exception
  {
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

  public void testMergeLocalChangesPerFeature_Bug3() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    CDOResource resource1 = transaction1.getOrCreateResource(getResourcePath("/res1"));
    EList<EObject> contents1 = resource1.getContents();
    transaction1.commit();
    contents1.add(getModel1Factory().createAddress());
    contents1.add(getModel1Factory().createAddress());

    // ----------------------------

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/res1"));
    EList<EObject> contents2 = resource2.getContents();
    contents2.add(getModel1Factory().createAddress());
    contents2.remove(0);

    // ----------------------------

    CDOTransaction transaction3 = session.openTransaction();
    transaction3.options().addConflictResolver(createConflictResolver());
    CDOResource resource3 = transaction3.getOrCreateResource(getResourcePath("/res1"));
    EList<EObject> contents3 = resource3.getContents();
    contents3.add(getModel1Factory().createAddress());
    contents3.add(getModel1Factory().createAddress());

    // Resolvers should be triggered.
    commitAndSync(transaction3, transaction2, transaction1);
    commitAndSync(transaction2, transaction1, transaction3);
    commitAndSync(transaction1, transaction2, transaction3);
  }

  public void testMerge_ManyValue() throws Exception
  {
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
    assertEquals(CDOUtil.getCDOObject(orderDetail1).cdoID(), CDOUtil.getCDOObject(orderDetails1.get(0)).cdoID());
    assertEquals(CDOUtil.getCDOObject(orderDetail2).cdoID(), CDOUtil.getCDOObject(orderDetails1.get(1)).cdoID());
  }

  protected CDOConflictResolver createConflictResolver()
  {
    return new CDOMergingConflictResolver();
  }
}
