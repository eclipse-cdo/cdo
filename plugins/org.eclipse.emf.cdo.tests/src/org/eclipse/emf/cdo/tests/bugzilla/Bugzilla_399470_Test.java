/*
 * Copyright (c) 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.File;

/**
 * Bug 399470 about NPE on local resource unload.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_399470_Test extends AbstractCDOTest
{
  public void testUnload() throws Exception
  {
    String folder = new File("./ecore/").getCanonicalPath();
    URI metamodelResourceURI = URI.createFileURI(new File(folder + "/component.ecore").getCanonicalPath());
    URI modelResourceURI = URI.createFileURI(new File(folder + "/component.xmi").getCanonicalPath());

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

    Resource metamodelResource = resourceSet.createResource(metamodelResourceURI);
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName("component");
    ePackage.setNsPrefix("component");
    ePackage.setNsURI("http://eclipse.org/cdo/tests/component");
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Component");
    ePackage.getEClassifiers().add(eClass);
    metamodelResource.getContents().add(ePackage);
    Resource modelResource = resourceSet.createResource(modelResourceURI);
    EObject eObject = EcoreUtil.create(eClass);
    modelResource.getContents().add(eObject);

    Copier copier = new Copier();
    copier.copyAll(metamodelResource.getContents());
    copier.copyAll(modelResource.getContents());
    copier.copyReferences();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
        new XMIResourceFactoryImpl());

    CDOResource sharedMetamodelResource = transaction.createResource(getResourcePath(metamodelResourceURI.lastSegment()));
    for (EObject content : metamodelResource.getContents())
    {
      EObject copy = copier.get(content);
      sharedMetamodelResource.getContents().add(copy);
    }

    CDOResource sharedModelResource = transaction.createResource(getResourcePath(modelResourceURI.lastSegment()));
    for (EObject content : modelResource.getContents())
    {
      EObject copy = copier.get(content);
      sharedModelResource.getContents().add(copy);
    }

    transaction.commit();
    metamodelResource.unload();
  }
}
