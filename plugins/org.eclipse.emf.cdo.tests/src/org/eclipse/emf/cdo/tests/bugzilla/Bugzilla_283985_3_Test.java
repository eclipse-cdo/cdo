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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_283985_3_Test extends AbstractCDOTest
{
  private Order order1, order2;

  private OrderDetail detail1, detail2, detail3, detail4;

  private CDOSession session;

  private CDOTransaction transaction;

  private CDOResource resource;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    Model1Factory factory = getModel1Factory();

    order1 = factory.createPurchaseOrder();
    order2 = factory.createPurchaseOrder();
    detail1 = factory.createOrderDetail();
    detail2 = factory.createOrderDetail();
    detail3 = factory.createOrderDetail();
    detail4 = factory.createOrderDetail();

    order1.getOrderDetails().add(detail1);
    order1.getOrderDetails().add(detail2);
    order1.getOrderDetails().add(detail3);
    order1.getOrderDetails().add(detail4);

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/r1"));
    resource.getContents().clear();
    resource.getContents().add(order1);
    resource.getContents().add(order2);
    transaction.commit();
  }

  @Override
  public void tearDown() throws Exception
  {
    transaction.close();
    transaction = null;

    session.close();
    session = null;

    order1 = null;
    order2 = null;

    detail1 = null;
    detail2 = null;
    detail3 = null;
    detail4 = null;
    super.tearDown();
  }

  // TODO Fix bug 344072
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void test1() throws CommitException
  {
    CDOID id = CDOUtil.getCDOObject(detail1).cdoID();

    order1.getOrderDetails().remove(detail1);
    assertTransient(detail1);

    transaction.setSavepoint();
    order1.getOrderDetails().add(detail1);
    assertClean(detail1, transaction);

    transaction.commit();
    assertEquals(id, CDOUtil.getCDOObject(detail1).cdoID());
    assertEquals(detail1, transaction.getObject(id));
    assertClean(detail1, transaction);
  }

  // TODO Fix bug 344072
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void test2() throws CommitException
  {
    InternalCDOSavepoint sp = (InternalCDOSavepoint)transaction.setSavepoint();
    order1.getOrderDetails().remove(detail1);
    assertTransient(detail1);

    assertEquals(true, sp.getDetachedObjects().containsValue(CDOUtil.getCDOObject(detail1)));

    sp = (InternalCDOSavepoint)transaction.setSavepoint();
    assertEquals(true, sp.getPreviousSavepoint().getDetachedObjects().containsValue(CDOUtil.getCDOObject(detail1)));

    order1.getOrderDetails().add(detail1);
    assertEquals(true, sp.getReattachedObjects().containsValue(CDOUtil.getCDOObject(detail1)));
    assertClean(detail1, transaction);

    sp.rollback();
    System.out.println(CDOUtil.getCDOObject(detail1).cdoState());
    assertTransient(detail1);

    transaction.commit();
    assertEquals(false, order1.getOrderDetails().contains(detail1));
  }

  // TODO Fix bug 344072
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void test3() throws CommitException
  {
    CDOID id = CDOUtil.getCDOObject(detail1).cdoID();

    transaction.setSavepoint();
    assertClean(detail1, transaction);

    order1.getOrderDetails().remove(detail1);
    transaction.setSavepoint();
    assertTransient(detail1);

    order1.getOrderDetails().add(detail1);
    transaction.setSavepoint();
    assertClean(detail1, transaction);

    order1.getOrderDetails().remove(detail1);
    assertTransient(detail1);

    transaction.getLastSavepoint().rollback();
    assertClean(detail1, transaction);

    transaction.commit();

    assertEquals(true, order1.getOrderDetails().contains(detail1));
    assertEquals(id, CDOUtil.getCDOObject(detail1).cdoID());
  }

  /**
   * Bug 312205 - After detach-reattach-rollback, object is not present in tx
   */
  public void test4()
  {
    CDOID id = CDOUtil.getCDOObject(detail1).cdoID();
    assertSame(transaction.getObject(id), CDOUtil.getCDOObject(detail1));

    // Detach
    order1.getOrderDetails().remove(detail1);

    // And re-attach
    order1.getOrderDetails().add(detail1);
    assertSame(transaction.getObject(id), CDOUtil.getCDOObject(detail1));

    transaction.rollback();
    assertSame(transaction.getObject(id), CDOUtil.getCDOObject(detail1));
  }
}
