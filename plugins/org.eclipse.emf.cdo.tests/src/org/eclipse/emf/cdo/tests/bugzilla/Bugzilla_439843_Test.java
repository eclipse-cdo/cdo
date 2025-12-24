/*
 * Copyright (c) 2014, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Leonid Ripeynih
 */
@Requires(ModelConfig.CAPABILITY_LEGACY)
public class Bugzilla_439843_Test extends AbstractCDOTest
{
  public void testLegacyOpposite_Multiple() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    String path = getResourcePath("test");
    CDOResource resource = tx.createResource(path);

    Order order = getModel1Factory().createSalesOrder();
    OrderDetail detail = getModel1Factory().createOrderDetail();

    order.getOrderDetails().add(detail);
    resource.getContents().add(order);

    tx.commit();

    // We need to detach an object and attach it back
    //
    order.getOrderDetails().remove(detail);
    order.getOrderDetails().add(detail);

    tx.commit();
    tx.close();
    session.close();

    // To reproduce the bug we need to load object from revision.
    // In local cache everything is fine...
    //
    session = openSession();
    tx = session.openTransaction();

    resource = tx.getResource(path);

    order = (Order)resource.getContents().get(0);
    detail = order.getOrderDetails().get(0);

    // Basic assertions on bidirectional reference contract
    //
    assertSame(order, detail.eContainer());
    assertEquals(true, order.getOrderDetails().contains(detail));
    assertSame(order, detail.getOrder());
  }
}
