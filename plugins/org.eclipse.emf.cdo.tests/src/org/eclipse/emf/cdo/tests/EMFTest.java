/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.net4j.tests.AbstractOMTest;

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
    System.out.println(type.getName());
    dumpProxy(type);

    System.out.println();

    if (false)
    {
      model2.eResource().getResourceSet().getResources().get(1).unload();
    }
    else
    {
      ((InternalEObject)type).eSetProxyURI(URI.createURI("model1.ecore#//Company"));
    }

    if (true)
    {
      type = (EClass)reference.getEType();
    }

    dumpProxy(type);
    System.out.println(type.getName());
    dumpProxy(type);
  }

  private void dumpProxy(EObject object)
  {
    InternalEObject eObject = (InternalEObject)object;
    String label = eObject.eClass().getName();

    System.out.println(label + ": " + eObject.eIsProxy());
    System.out.println(label + ": " + eObject.eProxyURI());
  }
}
