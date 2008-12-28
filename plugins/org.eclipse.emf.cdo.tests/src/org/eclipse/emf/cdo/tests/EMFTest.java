/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Eike Stepper
 */
public class EMFTest extends AbstractOMTest
{
  public EMFTest()
  {
  }

  public void testProxy()
  {
    EPackage model2 = (EPackage)EMFUtil.loadXMI("model2.ecore");
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
