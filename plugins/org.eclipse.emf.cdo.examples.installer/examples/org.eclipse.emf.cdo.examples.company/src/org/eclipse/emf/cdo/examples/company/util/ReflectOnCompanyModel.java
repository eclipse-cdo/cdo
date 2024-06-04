/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.util;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * @author Eike Stepper
 */
public class ReflectOnCompanyModel
{
  private static final EPackage[] PACKAGES = { //
      CompanyPackage.eINSTANCE, //
  };

  public static void main(String[] args)
  {
    for (EPackage ePackage : PACKAGES)
    {
      reflectPackage(ePackage);
    }
  }

  private static void reflectPackage(EPackage ePackage)
  {
    for (EClassifier eClassifier : ePackage.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        if (!eClass.isInterface() && !eClass.isAbstract())
        {
          reflectClass(eClass);
        }
      }
    }

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      reflectPackage(subPackage);
    }
  }

  private static void reflectClass(EClass eClass)
  {
    for (EReference eReference : eClass.getEAllReferences())
    {
      reflectReference(eReference);
    }
  }

  private static void reflectReference(EReference eReference)
  {
    if (eReference.getEOpposite() == null && !eReference.isContainer() && !eReference.isContainment())
    {
      EClass eClass = eReference.getEContainingClass();
      EPackage ePackage = eClass.getEPackage();
      String many = eReference.isMany() ? "  -->  MANY" : "";
      System.out.println(ePackage.getName() + "::" + eClass.getName() + "::" + eReference.getName() + many);
    }
  }
}
