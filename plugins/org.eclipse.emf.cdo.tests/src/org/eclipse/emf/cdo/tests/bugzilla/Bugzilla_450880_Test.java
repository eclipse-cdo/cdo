/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * Bug 450880 about ClassCastException on CDODeltaNotification.getNewValue or getOldValue for Enum-based attribute.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_450880_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  public void testCDODeltaNotificationGetValueWithEnumTypedAttributeOnCDOResource() throws Exception
  {
    CDOSession session1 = openSession();
    session1.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    testCDODeltaNotificationGetValueWithEnumTypedAttribute(resource1);

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource1Bis = transaction2.getResource(getResourcePath(RESOURCE_NAME));
    Product1 product1 = (Product1)resource1Bis.getContents().get(0);
    TestAdapter testAdapter = new TestAdapter(product1);
    commitAndSync(transaction1, transaction2);

    Notification notification = testAdapter.assertNotifications(1)[0];
    assertEquals(getModel1Package().getProduct1_OtherVATs().getDefaultValue(), notification.getOldValue());
    assertEquals(VAT.VAT0, notification.getNewValue());
  }

  public void testCDODeltaNotificationGetValueWithEnumTypedAttributeOnXMIResource() throws Exception
  {
    Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
    registry.getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());
    ResourceSet resourceSet = new ResourceSetImpl();
    URI localMainResourceURI = URI.createFileURI(createTempFile(getName(), ".model1").getCanonicalPath());
    Resource resource1 = resourceSet.createResource(localMainResourceURI);
    testCDODeltaNotificationGetValueWithEnumTypedAttribute(resource1);
  }

  private void testCDODeltaNotificationGetValueWithEnumTypedAttribute(Resource resource) throws Exception
  {
    Product1 product1 = getModel1Factory().createProduct1();
    resource.getContents().add(product1);
    resource.save(Collections.emptyMap());
    TestAdapter testAdapter = new TestAdapter(product1);
    VAT newValue = VAT.VAT0;
    product1.setVat(newValue);

    Notification notification = testAdapter.assertNotifications(1)[0];
    assertEquals(getModel1Package().getProduct1_OtherVATs().getDefaultValue(), notification.getOldValue());
    assertEquals(newValue, notification.getNewValue());
  }

}
