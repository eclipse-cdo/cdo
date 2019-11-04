/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;

/**
 * Bug 503564 - Creation of CDOSetFeatureDeltaImpl in CDOStoreImp mixes EMF-Type and CDO-Type values
 *
 * @author Eike Stepper
 */
public class Bugzilla_503564_Test extends AbstractCDOTest
{
  public void testSetFeatureDelta_String() throws Exception
  {
    Supplier object = getModel1Factory().createSupplier();
    object.setName("Old Name");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(object);
    transaction.commit();

    CDOView controlView = session.openView();
    Supplier controlObject = controlView.getObject(object);
    assertEquals("Old Name", controlObject.getName());

    TestAdapter adapter = new TestAdapter(controlObject);

    object.setName("New Name");
    commitAndSync(transaction, controlView);

    Notification[] notifications = adapter.assertNotifications(1);
    Object oldValue = notifications[0].getOldValue();
    assertEquals("Old Name", oldValue);
  }

  public void testSetFeatureDelta_CDOID() throws Exception
  {
    Supplier oldSupplier = getModel1Factory().createSupplier();
    PurchaseOrder object = getModel1Factory().createPurchaseOrder();
    object.setSupplier(oldSupplier);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(object);
    resource.getContents().add(oldSupplier);
    transaction.commit();

    CDOView controlView = session.openView();
    PurchaseOrder controlObject = controlView.getObject(object);
    Supplier controlOldSupplier = controlView.getObject(oldSupplier);
    assertEquals(controlOldSupplier, controlObject.getSupplier());

    TestAdapter adapter = new TestAdapter(controlObject);

    Supplier newSupplier = getModel1Factory().createSupplier();
    resource.getContents().add(newSupplier);
    object.setSupplier(newSupplier);
    commitAndSync(transaction, controlView);

    Notification[] notifications = adapter.assertNotifications(1);
    Object oldValue = notifications[0].getOldValue();
    assertEquals(controlOldSupplier, oldValue);
  }
}
