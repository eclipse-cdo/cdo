/*
 * Copyright (c) 2007-2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DymamicEcoreTest extends AbstractCDOTest
{
  public void testDynamicEcore() throws Exception
  {
    {
      // Obtain model
      EPackage ecore = (EPackage)loadModel("ecore/Ecore.ecore", EPackage.Registry.INSTANCE);

      // Create resource in session 1
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(ecore);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      EObject root = loadModel("model1.ecore", session.getPackageRegistry());
      res.getContents().add(root);
      transaction.commit();
      session.close();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    EObject root = res.getContents().get(0);
    saveModel("model1X.ecore", root);
    session.close();
  }

  private static EObject loadModel(String fileName, EPackage.Registry packageRegistry) throws IOException
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    resourceSet.setPackageRegistry(packageRegistry);

    Resource resource = resourceSet.getResource(URI.createFileURI(fileName), true);
    return resource.getContents().get(0);
  }

  private static void saveModel(String fileName, EObject root) throws IOException
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    Resource resource = resourceSet.getResource(URI.createFileURI(fileName), true);
    resource.getContents().add(EcoreUtil.copy(root));
    resource.save(null);
  }
}
