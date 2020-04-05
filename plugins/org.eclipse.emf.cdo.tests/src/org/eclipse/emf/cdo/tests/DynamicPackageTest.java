/*
 * Copyright (c) 2010-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.object.DynamicCDOObjectImpl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;

/**
 * @author Martin Fluegge
 */
public class DynamicPackageTest extends AbstractCDOTest
{
  public void testDynamicMaps() throws Exception
  {
    EPackage dynamicMapEPackge = createPackage();
    EFactory dynamicMapEFactoryInstance = dynamicMapEPackge.getEFactoryInstance();
    EObject mapContainer = dynamicMapEFactoryInstance.create((EClass)dynamicMapEPackge.getEClassifier("MapContainer"));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(mapContainer);
    transaction.commit();

    if (!isConfig(LEGACY))
    {
      assertEquals(true, mapContainer instanceof DynamicCDOObjectImpl);
    }
    else
    {
      assertEquals(true, mapContainer instanceof DynamicEObjectImpl);
    }
  }

  private EPackage createPackage()
  {
    EStructuralFeature name = EcoreFactory.eINSTANCE.createEAttribute();
    name.setName("name");
    name.setEType(EcorePackage.eINSTANCE.getEString());

    EClass mapContainerEClass = EcoreFactory.eINSTANCE.createEClass();
    mapContainerEClass.setName("MapContainer");
    mapContainerEClass.getEStructuralFeatures().add(name);

    EPackage dynamicMapEPackage = createUniquePackage();
    dynamicMapEPackage.getEClassifiers().add(mapContainerEClass);

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(dynamicMapEPackage);
    }

    return dynamicMapEPackage;
  }
}
