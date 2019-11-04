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

import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model5.CustomType;
import org.eclipse.emf.cdo.tests.model5.GenListOfIntArray;
import org.eclipse.emf.cdo.tests.model5.WithCustomType;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;

/**
 * Bug 503564 - Creation of CDOSetFeatureDeltaImpl in CDOStoreImp mixes EMF-Type and CDO-Type values
 *
 * @author Eike Stepper
 */
public class Bugzilla_503573_Test extends AbstractCDOTest
{
  public void testCustomDatatype_Single() throws Exception
  {
    WithCustomType object = getModel5Factory().createWithCustomType();
    object.setValue(new CustomType(1234));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(object);
    transaction.commit();

    CDOView controlView = session.openView();
    WithCustomType controlObject = controlView.getObject(object);
    assertEquals(new CustomType(1234), controlObject.getValue());

    TestAdapter adapter = new TestAdapter(controlObject);

    object.setValue(new CustomType(5678));
    commitAndSync(transaction, controlView);

    Notification notification = adapter.assertNotifications(1)[0];
    Object oldValue = notification.getOldValue();
    assertEquals(new CustomType(1234), oldValue);
  }

  /**
   * TODO Check whether the {@link CDOFeatureDelta#UNKNOWN_VALUE} can be improved.
   */
  public void _testCustomDatatype_Many() throws Exception
  {
    GenListOfIntArray object = getModel5Factory().createGenListOfIntArray();
    object.getElements().add(new int[] { 1, 2, 3 });
    object.getElements().add(new int[] { 4, 5, 6 });
    object.getElements().add(new int[] { 7, 8, 9 });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(object);
    transaction.commit();

    CDOView controlView = session.openView();
    GenListOfIntArray controlObject = controlView.getObject(object);
    assertEquals(3, controlObject.getElements().size());

    TestAdapter adapter = new TestAdapter(controlObject);

    object.getElements().remove(1);
    commitAndSync(transaction, controlView);

    Notification notification = adapter.assertNotifications(1)[0];
    Object oldValue = notification.getOldValue();
    assertEquals("Old Name", oldValue);
  }
}
