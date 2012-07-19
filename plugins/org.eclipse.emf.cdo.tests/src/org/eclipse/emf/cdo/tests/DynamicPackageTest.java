/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
  protected static EClass mapContainerEClass;

  public void testDynamicMaps() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    EPackage dynamicMapEPackge = createPackage();
    EFactory dynamicMapEFactoryInstance = dynamicMapEPackge.getEFactoryInstance();

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    EObject mapContainer = dynamicMapEFactoryInstance.create((EClass)dynamicMapEPackge.getEClassifier("MapContainer"));

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

  public EPackage createPackage()
  {
    EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
    EcorePackage theCorePackage = EcorePackage.eINSTANCE;

    mapContainerEClass = theCoreFactory.createEClass();
    mapContainerEClass.setName("MapContainer");

    EPackage dynamicMapEPackage = theCoreFactory.createEPackage();
    dynamicMapEPackage.setName("DynamicMapPackage");
    dynamicMapEPackage.setNsPrefix("dynamicmap");
    dynamicMapEPackage.setNsURI("http:///org.mftech.examples.emf.dynamic.map");

    dynamicMapEPackage.getEClassifiers().add(mapContainerEClass);

    EStructuralFeature name = theCoreFactory.createEAttribute();
    name.setName("name");
    name.setEType(theCorePackage.getEString());

    mapContainerEClass.getEStructuralFeatures().add(name);

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(dynamicMapEPackage);
    }

    return dynamicMapEPackage;
  }
}
