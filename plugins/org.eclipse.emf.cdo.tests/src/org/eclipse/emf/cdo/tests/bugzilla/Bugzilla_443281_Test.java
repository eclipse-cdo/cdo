/*
 * Copyright (c) 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.File;

/**
 * Test that {@link CDOResource#setURI(org.eclipse.emf.common.util.URI)} notify adapters of Set {@link Notification} on Resource.RESOURCE__URI.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_443281_Test extends AbstractCDOTest
{
  private ResourceSet resourceSet;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
  }

  @Override
  public void tearDown() throws Exception
  {
    resourceSet = null;
    super.tearDown();
  }

  public void testCDOResource_setURI() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction(resourceSet);
    String resourceName = "resource1.model1";
    CDOResource resource1 = tx.createResource(getResourcePath(resourceName));

    // Test
    TestAdapter testAdapter = new TestAdapter(resource1);
    URI uri = resource1.getURI();
    String newResourceName = "resource2.model1";
    URI newURI = uri.trimSegments(1).appendSegment(newResourceName);
    resource1.setURI(newURI);

    Notification[] notifications = testAdapter.assertNotifications(2);

    Notification notification1 = notifications[0];
    assertEquals(resource1, notification1.getNotifier());
    assertEquals(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, notification1.getFeature());
    assertEquals(Notification.SET, notification1.getEventType());
    assertEquals(resourceName, notification1.getOldValue());
    assertEquals(newResourceName, notification1.getNewValue());

    Notification notification2 = notifications[1];
    assertEquals(resource1, notification2.getNotifier());
    assertEquals(Resource.RESOURCE__URI, notification2.getFeatureID(null));
    assertEquals(Notification.SET, notification2.getEventType());
    assertEquals(uri, notification2.getOldValue());
    assertEquals(newURI, notification2.getNewValue());

  }

  public void testXMIResource_setURI() throws Exception
  {
    String path = new File("./localResource.xmi").getCanonicalPath();
    URI localResourceURI = URI.createFileURI(path);
    Resource resource1 = resourceSet.createResource(localResourceURI);

    // Test
    TestAdapter testAdapter = new TestAdapter(resource1);
    URI uri = resource1.getURI();
    URI newURI = uri.trimSegments(1).appendSegment("resource2.model1");
    resource1.setURI(newURI);
    Notification notification = testAdapter.assertNotifications(1)[0];
    assertEquals(resource1, notification.getNotifier());
    assertEquals(Resource.RESOURCE__URI, notification.getFeatureID(null));
    assertEquals(Notification.SET, notification.getEventType());
    assertEquals(uri, notification.getOldValue());
    assertEquals(newURI, notification.getNewValue());
  }
}
