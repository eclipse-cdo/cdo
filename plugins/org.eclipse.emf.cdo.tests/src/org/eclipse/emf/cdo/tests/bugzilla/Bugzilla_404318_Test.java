/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.net.URL;

/**
 * Bug 404318: Tests that we don't get certain NPEs when processing dynamic objects whose
 * EClasses have been unloaded.
 *
 * @author Christian W. Damus (CEA)
 */
public class Bugzilla_404318_Test extends AbstractCDOTest
{
  public void testUnloadedEClass() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    // get our dynamic package
    EPackage epackage = getTestPackage(transaction.getResourceSet());

    // and the instance model
    Resource resource = getTestInstance(transaction.getResourceSet());

    session.getPackageRegistry().putEPackage(epackage);

    EObject anA = resource.getContents().get(0);
    EObject aB = resource.getContents().get(1);

    // sanity check
    assertSame(anA, aB.eGet(aB.eClass().getEStructuralFeature("a")));

    try
    {
      // unload the models. This would NPE without the fix
      epackage.eResource().unload();
      resource.unload();

      // unloaded EClass still supports the unloaded instance
      assertSame(anA, aB.eGet(aB.eClass().getEStructuralFeature("a")));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Threw exception: " + e.getLocalizedMessage());
    }
  }

  //
  // test framework

  protected EPackage getTestPackage(ResourceSet rset) throws Exception
  {
    rset.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    URL location = getClass().getClassLoader().getResource("Bug404318.ecore");
    Resource result = rset.getResource(URI.createURI(location.toExternalForm(), true), true);
    return (EPackage)result.getContents().get(0);
  }

  protected Resource getTestInstance(ResourceSet rset) throws Exception
  {
    rset.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    URL location = getClass().getClassLoader().getResource("Bug404318.xmi");
    Resource result = rset.getResource(URI.createURI(location.toExternalForm(), true), true);
    return result;
  }
}
