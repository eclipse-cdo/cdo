/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class OldValueNotificationTest extends AbstractCDOTest
{
  public void testSameSession() throws Exception
  {
    final CDOSession session = openSession();
    final CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    Address address1 = getModel1Factory().createAddress();
    address1.setCity("Basel");
    address1.setStreet("Turnerstrasse 39");
    address1.setName("Michael Szediwy");

    SpecialPurchaseOrder order = getModel2Factory().createSpecialPurchaseOrder();
    order.setDate(new Date());
    order.setShippingAddress(address1);

    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(order);
    transaction.commit();

    final CDOView view = session.openView();
    SpecialPurchaseOrder roOrder = view.getObject(order);
    msg(CDOUtil.getCDOObject(roOrder).cdoState());

    final TestAdapter adapter = new TestAdapter(roOrder);

    Address address2 = getModel1Factory().createAddress();
    address2.setCity("Basel2");
    address2.setStreet("Turnerstrasse 392");
    address2.setName("Michael Szediwy2");

    order.setShippingAddress(address2);

    transaction.commit();

    Notification notification = adapter.assertNoTimeout(1)[0];
    notification.getOldValue();
  }
}
