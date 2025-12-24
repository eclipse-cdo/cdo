/*
 * Copyright (c) 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.models;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;
import org.eclipse.emf.cdo.tests.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class ReflectOnTestModels
{
  private static final EPackage[] PACKAGES = { //
      Model1Package.eINSTANCE, //
      Model2Package.eINSTANCE, //
      Model3Package.eINSTANCE, //
      model4Package.eINSTANCE, //
      model4interfacesPackage.eINSTANCE, //
      Model5Package.eINSTANCE, //
      Model6Package.eINSTANCE, //
      MangoPackage.eINSTANCE, //
  };

  public static void main(String[] args)
  {
    for (EPackage ePackage : PACKAGES)
    {
      reflectPackage(ePackage);
    }
  }

  public static String fqn(EStructuralFeature eFeature)
  {
    EClass eClass = eFeature.getEContainingClass();
    EPackage ePackage = eClass.getEPackage();
    return ePackage.getName() + "::" + eClass.getName() + "::" + eFeature.getName();
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
    for (EAttribute eAttribute : eClass.getEAllAttributes())
    {
      reflectAttribute(eAttribute);
    }

    for (EReference eReference : eClass.getEAllReferences())
    {
      reflectReference(eReference);
    }
  }

  private static void reflectAttribute(EAttribute eAttribute)
  {
    dumpLobAttribute(eAttribute);
  }

  private static void reflectReference(EReference eReference)
  {
    // dumpUniDirectionalCrossReference(eReference);
    // dumpSingleValuedContainmentReference(eReference);
    // dumpManyValuedCrossReference(eReference);
    // dumpSingleValuedCrossReference(eReference);
  }

  protected static void dumpLobAttribute(EAttribute eAttribute)
  {
    EClassifier eType = eAttribute.getEType();
    String many = eAttribute.isMany() ? "  -->  MANY" : "";

    if (eType == EtypesPackage.Literals.BLOB)
    {
      System.out.println("BLOB " + fqn(eAttribute) + many);
    }
    else if (eType == EtypesPackage.Literals.CLOB)
    {
      System.out.println("CLOB " + fqn(eAttribute) + many);
    }
  }

  protected static void dumpUniDirectionalCrossReference(EReference eReference)
  {
    if (eReference.getEOpposite() == null && !eReference.isContainer() && !eReference.isContainment())
    {
      String many = eReference.isMany() ? "  -->  MANY" : "";
      System.out.println(fqn(eReference) + many);
    }
  }

  protected static void dumpSingleValuedContainmentReference(EReference eReference)
  {
    if (eReference.isContainment() && !eReference.isMany())
    {
      System.out.println(fqn(eReference));
    }
  }

  protected static void dumpManyValuedCrossReference(EReference eReference)
  {
    if (!eReference.isContainment() && !eReference.isContainer() && eReference.isMany())
    {
      System.out.println(fqn(eReference));
    }
  }

  protected static void dumpSingleValuedCrossReference(EReference eReference)
  {
    if (!eReference.isContainment() && !eReference.isContainer() && !eReference.isMany())
    {
      System.out.println(fqn(eReference));
    }
  }
}
