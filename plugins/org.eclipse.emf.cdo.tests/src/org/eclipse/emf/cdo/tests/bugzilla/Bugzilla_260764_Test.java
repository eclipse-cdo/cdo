/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.util.ArrayList;
import java.util.List;

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
    final CDOSession session = openModel1Session();
    session.options().setPassiveUpdateEnabled(false);

    // ************************************************************* //
    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();
    final CDOResource resourceA = transaction.createResource("/test1");

    final OrderAddress orderAddress = getModel1Factory().createOrderAddress();
    resourceA.getContents().add(orderAddress);
    transaction.commit();

    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    final TestAdapter adapter = new TestAdapter();
    orderAddress.eAdapters().add(adapter);

    // ************************************************************* //

    msg("Opening view");
    final CDOSession session2 = openModel1Session();
    session2.options().setPassiveUpdateEnabled(false);

    final CDOTransaction transaction2 = session2.openTransaction();
    final OrderAddress orderAddress2 = (OrderAddress)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(
        orderAddress).cdoID(), true));

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderAddress2.getOrderDetails().add(orderDetail);

    assertEquals(0, adapter.getNotifications().size());
    transaction2.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().size() == 1;
      }
    }.assertNoTimeOut();

    CDODeltaNotification cdoNotification = (CDODeltaNotification)adapter.getNotifications().get(0);
    assertEquals(false, cdoNotification.hasNext());
    assertEquals(getModel1Package().getOrder_OrderDetails(), cdoNotification.getFeature());
  }

  /**
   * @author Simon McDuff
   */
  private static class TestAdapter implements Adapter
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    private Notifier notifier;

    public TestAdapter()
    {
    }

    public Notifier getTarget()
    {
      return notifier;
    }

    public List<Notification> getNotifications()
    {
      return notifications;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      notifications.add(notification);
    }

    public void setTarget(Notifier newTarget)
    {
      notifier = newTarget;
    }
  }
}
