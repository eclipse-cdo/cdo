/*
 * Copyright (c) 2014-2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Laurent Fasani - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Bug 429659 - check notifications when unsetting EStructuralFeature.
 * test TransientStore/CDOStoreImpl.unset() with
 * single-valued/multi-valued/settable/unsettable feature in CDO
 * and XMI mode.
 *
 * @author <a href="mailto:laurent.fasani@obeo.fr">Laurent Fasani</a>
 */
public class Bugzilla_429659_Test extends AbstractCDOTest
{
  public void testUnsetOnUnsettableMultiValuedFeatureXMIResource() throws Exception
  {
    List<EObject> objectsToAdd = new ArrayList<>();
    objectsToAdd.add(getModel3SubpackageFactory().createClass2());
    objectsToAdd.add(getModel3SubpackageFactory().createClass2());
    performUnsetOnMultiValuedFeature(getXMIResource(), getModel3Factory().createClass1(), getModel3Package().getClass1_Class2(), true, objectsToAdd);
  }

  public void testUnsetOnUnsettableMultiValuedFeatureCDOResource() throws Exception
  {
    List<EObject> objectsToAdd = new ArrayList<>();
    objectsToAdd.add(getModel3SubpackageFactory().createClass2());
    objectsToAdd.add(getModel3SubpackageFactory().createClass2());
    performUnsetOnMultiValuedFeature(getCDOResource(), getModel3Factory().createClass1(), getModel3Package().getClass1_Class2(), true, objectsToAdd);
  }

  public void testUnsetOnNonUnsettableMultiValuedFeatureXMIResource() throws Exception
  {
    List<EObject> objectsToAdd = new ArrayList<>();
    objectsToAdd.add(getModel6Factory().createBaseObject());
    objectsToAdd.add(getModel6Factory().createBaseObject());
    performUnsetOnMultiValuedFeature(getXMIResource(), getModel6Factory().createReferenceObject(), getModel6Package().getReferenceObject_ReferenceList(), false,
        objectsToAdd);
  }

  public void testUnsetOnNonUnsettableMultiValuedFeatureCDOResource() throws Exception
  {
    List<EObject> objectsToAdd = new ArrayList<>();
    objectsToAdd.add(getModel6Factory().createBaseObject());
    objectsToAdd.add(getModel6Factory().createBaseObject());
    performUnsetOnMultiValuedFeature(getCDOResource(), getModel6Factory().createReferenceObject(), getModel6Package().getReferenceObject_ReferenceList(), false,
        objectsToAdd);
  }

  public void testUnsetOnUnsettableSingleValuedAttributeCDOResource() throws Exception
  {
    performUnsetOnSingleValuedFeature(getCDOResource(), getModel6Factory().createEmptyStringDefaultUnsettable(),
        getModel6Package().getEmptyStringDefaultUnsettable_Attribute(), true, "test");
  }

  public void testUnsetOnUnsettableSingleValuedAttributeXMIResource() throws Exception
  {
    performUnsetOnSingleValuedFeature(getXMIResource(), getModel6Factory().createEmptyStringDefaultUnsettable(),
        getModel6Package().getEmptyStringDefaultUnsettable_Attribute(), true, "test");
  }

  public void testUnsetOnNonUnsettableSingleValuedAttributeCDOResource() throws Exception
  {
    performUnsetOnSingleValuedFeature(getCDOResource(), getModel6Factory().createEmptyStringDefault(), getModel6Package().getEmptyStringDefault_Attribute(),
        false, "test");
  }

  public void testUnsetOnNonUnsettableSingleValuedAttributeXMIResource() throws Exception
  {
    performUnsetOnSingleValuedFeature(getXMIResource(), getModel6Factory().createEmptyStringDefault(), getModel6Package().getEmptyStringDefault_Attribute(),
        false, "test");
  }

  private Resource getCDOResource()
  {
    CDOSession openSession = openSession();
    CDOTransaction transaction = openSession.openTransaction();
    Resource resourceCDO = transaction.createResource(getResourcePath("resource"));

    return resourceCDO;
  }

  private Resource getXMIResource() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

    URI localURI = URI.createFileURI(createTempFile().getCanonicalPath());
    Resource localResource = resourceSet.createResource(localURI);

    return localResource;
  }

  private void performUnsetOnSingleValuedFeature(Resource resource, EObject root, EStructuralFeature feature, boolean unsettable, Object addedObject)
      throws Exception
  {
    // To have CDOLegacyAdapter even in local
    CDOUtil.getCDOObject(root);

    // check that Feature is unsettable or not
    assertEquals(unsettable, feature.isUnsettable());

    resource.getContents().add(root);
    resource.save(Collections.emptyMap());

    // allow notifications checking
    NotificationAsserter notificationAsserter = new NotificationAsserter();
    resource.eAdapters().add(notificationAsserter);
    List<Notification> notifications = notificationAsserter.getNotifications();

    root.eSet(feature, addedObject);

    // unset
    notifications.clear();
    root.eUnset(feature);

    // check
    assertEquals("Incorrect number of expected notifications: ", 1, notifications.size());
    assertEquals("Incorrect notification: ", unsettable ? Notification.UNSET : Notification.SET, notifications.get(0).getEventType());
  }

  @SuppressWarnings("unchecked")
  private void performUnsetOnMultiValuedFeature(Resource resource, EObject root, EStructuralFeature feature, boolean unsettable, List<EObject> objectstoAdd)
      throws Exception
  {
    // To have CDOLegacyAdapter even in local
    CDOUtil.getCDOObject(root);
    for (EObject objectToAdd : objectstoAdd)
    {
      CDOUtil.getCDOObject(objectToAdd);
    }

    // check that feature is unsettable or not
    assertEquals(unsettable, feature.isUnsettable());

    resource.getContents().add(root);
    resource.save(Collections.emptyMap());

    // allow notifications checking
    NotificationAsserter notificationAsserter = new NotificationAsserter();
    root.eAdapters().add(notificationAsserter);
    List<Notification> notifications = notificationAsserter.getNotifications();

    // -----------------------------
    // Step 1: unset many elements
    ((Collection<EObject>)root.eGet(feature)).addAll(objectstoAdd);

    // unset
    notifications.clear();
    root.eUnset(feature);

    // check
    assertEquals("Incorrect number of expected notifications: ", unsettable ? 2 : 1, notifications.size());
    assertEquals("Incorrect notification: ", Notification.REMOVE_MANY, notifications.get(0).getEventType());
    if (unsettable)
    {
      assertEquals("Incorrect notification: ", Notification.UNSET, notifications.get(1).getEventType());
    }

    // -------------------------------------
    // Step 2: unset single element
    ((Collection<EObject>)root.eGet(feature)).add(objectstoAdd.get(0));

    // unset
    notifications.clear();
    root.eUnset(feature);

    // check
    assertEquals("Incorrect number of expected notifications: ", unsettable ? 2 : 1, notifications.size());
    assertEquals("Incorrect notification: ", Notification.REMOVE, notifications.get(0).getEventType());
    if (unsettable)
    {
      assertEquals("Incorrect notification: ", Notification.UNSET, notifications.get(1).getEventType());
    }

    notifications.clear();
    root.eUnset(feature);
    assertEquals("Incorrect number of expected notifications: ", unsettable ? 2 : 1, notifications.size());
  }

  /**
   * @author Eike Stepper
   */
  private static final class NotificationAsserter extends EContentAdapter
  {
    private final List<Notification> notifications = new ArrayList<>();

    @Override
    public void notifyChanged(Notification notification)
    {
      super.notifyChanged(notification);
      notifications.add(notification);
    }

    public List<Notification> getNotifications()
    {
      return notifications;
    }
  }
}
