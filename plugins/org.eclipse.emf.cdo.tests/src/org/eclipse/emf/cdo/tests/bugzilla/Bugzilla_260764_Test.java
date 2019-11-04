/*
 * Copyright (c) 2009-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

/**
 * CDO wrong feature on notification when multiple super types
 * <p>
 * See bug 260764
 *
 * @author Simon McDuff
 */
public class Bugzilla_260764_Test extends AbstractCDOTest
{
  public void testBugzilla_260764() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openSession();

    // ************************************************************* //
    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    final OrderAddress orderAddress = getModel1Factory().createOrderAddress();
    resourceA.getContents().add(orderAddress);
    transaction.commit();

    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    final TestAdapter adapter = new TestAdapter(orderAddress);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openSession();

    final CDOTransaction transaction2 = session2.openTransaction();
    final OrderAddress orderAddress2 = (OrderAddress)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(orderAddress).cdoID(), true));

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderAddress2.getOrderDetails().add(orderDetail);

    adapter.assertNotifications(0);
    transaction2.commit();

    CDODeltaNotification notification = (CDODeltaNotification)adapter.assertNoTimeout(1)[0];
    assertEquals(false, notification.hasNext());
    assertEquals(getModel1Package().getOrder_OrderDetails(), notification.getFeature());
  }
}
