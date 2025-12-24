/*
 * Copyright (c) 2010-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.util.TestEMFUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.signal.RemoteException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.util.Map;

/**
 * CDO not robust when using dynamic packages
 *
 * @author Caspar De Groot
 */
public class Bugzilla_303466_Test extends AbstractCDOTest
{
  @CleanRepositoriesBefore(reason = "Package counting")
  public void test_missingDependency() throws Exception
  {
    CDOSession session = openSession();

    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();
    EPackage derivedPkg = TestEMFUtil.loadEcore("../org.eclipse.emf.cdo.tests/model/derived.ecore");
    Resource resource = derivedPkg.eResource();
    resourceSet.getResources().add(resource);
    resource.load(null);

    session.getPackageRegistry().putEPackage(derivedPkg);
    assertEquals(4, session.getPackageRegistry().size());

    EClass derivedClass = (EClass)derivedPkg.getEClassifier("DerivedClass");
    EObject derived = derivedPkg.getEFactoryInstance().create(derivedClass);

    // Verify that the feature inherited from the base class is missing
    assertNull(derivedClass.getEStructuralFeature("couter"));

    CDOTransaction tx = session.openTransaction();
    CDOResource resource2 = tx.createResource(getResourcePath("/resource1"));
    resource2.getContents().add(derived);

    try
    {
      tx.commit();
      fail("Should have thrown an exception because of the missing base package");
    }
    catch (CommitException e)
    {
      if (e.getCause() instanceof IllegalStateException)
      {
        // Good
      }
      else if (e.getCause() instanceof RemoteException)
      {
        fail("Problem should have been detected on the client side");
      }
      else
      {
        throw e;
      }
    }
    finally
    {
      tx.close();
      session.close();
    }
  }

  public void test_badUris() throws IOException
  {
    CDOSession session = openSession();

    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();

    EPackage basePkg = TestEMFUtil.loadEcore("../org.eclipse.emf.cdo.tests/model/base.ecore");
    Resource resource1 = basePkg.eResource();
    resourceSet.getResources().add(resource1);
    resourceSet.getPackageRegistry().put(basePkg.getNsURI(), basePkg);

    EPackage derivedPkg = TestEMFUtil.loadEcore("../org.eclipse.emf.cdo.tests/model/derived.ecore");
    Resource resource2 = derivedPkg.eResource();
    resourceSet.getResources().add(resource2);
    resourceSet.getPackageRegistry().put(basePkg.getNsURI(), derivedPkg);

    Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();
    URI modelUri3 = URI.createFileURI("base.ecore");
    uriMap.put(modelUri3, resource1.getURI());

    EMFUtil.safeResolveAll(resourceSet);

    session.getPackageRegistry().putEPackage(basePkg);
    session.getPackageRegistry().putEPackage(derivedPkg);

    EClass derivedClass = (EClass)derivedPkg.getEClassifier("DerivedClass");
    EObject derived = derivedPkg.getEFactoryInstance().create(derivedClass);

    // Verify that the feature inherited from the base class is NOT missing on the client side
    assertNotNull(derivedClass.getEStructuralFeature("couter"));

    CDOTransaction tx = session.openTransaction();
    CDOResource resource3 = tx.createResource(getResourcePath("/resource1"));
    resource3.getContents().add(derived);

    try
    {
      tx.commit();
      fail("Should have thrown an exception because of the file URIs in the dependent package");
    }
    catch (CommitException e)
    {
      if (e.getCause() instanceof IllegalStateException)
      {
        // Good
      }
      else if (e.getCause() instanceof RemoteException)
      {
        fail("Problem should have been detected on the client side");
      }
      else
      {
        fail("Should have thrown an " + IllegalStateException.class.getName());
      }
    }

    tx.close();
    session.close();
  }
}
