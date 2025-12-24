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

import org.eclipse.emf.cdo.tests.util.TestEMFUtil;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Eike Stepper
 */
public class EMFTest extends AbstractOMTest
{
  public EMFTest()
  {
  }

  public void testProxy() throws Exception
  {
    EPackage model2 = (EPackage)TestEMFUtil.loadXMI("model2.ecore");
    EClass companyProxy = (EClass)model2.getEClassifier("CompanyProxy");
    EReference reference = (EReference)companyProxy.getEStructuralFeature("company");

    EClass type = (EClass)reference.getEType();
    dumpProxy(type);
    IOUtil.OUT().println(type.getName());
    dumpProxy(type);

    IOUtil.OUT().println();

    // if (false)
    // {
    // model2.eResource().getResourceSet().getResources().get(1).unload();
    // }
    // else
    {
      ((InternalEObject)type).eSetProxyURI(URI.createURI("model1.ecore#//Company"));
    }

    if (true)
    {
      type = (EClass)reference.getEType();
    }

    dumpProxy(type);
    IOUtil.OUT().println(type.getName());
    dumpProxy(type);
  }

  public void testDefaultValue() throws Exception
  {
    final String DEFAULT = "Simon";

    EAttribute assignee = EcoreFactory.eINSTANCE.createEAttribute();
    assignee.setName("assignee");
    assignee.setEType(EcorePackage.eINSTANCE.getEString());
    assignee.setDefaultValueLiteral(DEFAULT);
    assignee.setUnsettable(true);

    EClass bugzilla = EcoreFactory.eINSTANCE.createEClass();
    bugzilla.setName("Bugzilla");
    bugzilla.getEStructuralFeatures().add(assignee);

    EPackage p = EcoreFactory.eINSTANCE.createEPackage();
    p.setName("p");
    p.getEClassifiers().add(bugzilla);

    EFactory f = p.getEFactoryInstance();
    EObject object = f.create(bugzilla);
    assertEquals(DEFAULT, object.eGet(assignee));
    assertEquals(false, object.eIsSet(assignee));

    object.eSet(assignee, DEFAULT);
    assertEquals(DEFAULT, object.eGet(assignee));
    assertEquals(true, object.eIsSet(assignee));

    object.eUnset(assignee);
    assertEquals(DEFAULT, object.eGet(assignee));
    assertEquals(false, object.eIsSet(assignee));
  }

  private void dumpProxy(EObject object)
  {
    InternalEObject eObject = (InternalEObject)object;
    String label = eObject.eClass().getName();

    IOUtil.OUT().println(label + ": " + eObject.eIsProxy());
    IOUtil.OUT().println(label + ": " + eObject.eProxyURI());
  }

  public static void main(String[] args)
  {
    dump(URI.createURI("cdo:///a/b/c"));
    dump(URI.createURI("cdo://a/b/c"));
    dump(URI.createURI("cdo:/a/b/c"));
    dump(URI.createURI("cdo:a/b/c"));
  }

  private static void dump(URI uri)
  {
    System.out.println(uri);
    System.out.println();
    System.out.println("    isHierarchical: " + uri.isHierarchical());
    System.out.println("    isPrefix:       " + uri.isPrefix());
    System.out.println("    isRelative:     " + uri.isRelative());
    System.out.println("    authority:      " + uri.authority());
    System.out.println("    path:           " + uri.path());
    System.out.println("    devicePath:     " + uri.devicePath());
    System.out.println("    segmentsList:   " + uri.segmentsList());
    System.out.println("    opaquePart:     " + uri.opaquePart());
    System.out.println();
  }
}
