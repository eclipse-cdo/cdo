/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.evolution.util;

import org.eclipse.emf.cdo.evolution.ModelSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class ElementHandler
{
  private static ElementHandler PACKAGE = new PackageHandler();

  private static ElementHandler CLASS = new ClassHandler();

  private static ElementHandler ATTRIBUTE = new AttributeHandler();

  private static ElementHandler REFERENCE = new ReferenceHandler();

  private static ElementHandler DATA_TYPE = new DataTypeHandler();

  private static ElementHandler ENUM = new EnumHandler();

  private static ElementHandler ENUM_LITERAL = new EnumLiteralHandler();

  private static final EReference[] NO_CHILD_FEATURES = {};

  private ElementHandler()
  {
  }

  public abstract EClass getEClass();

  public EReference[] getChildFeatures()
  {
    return NO_CHILD_FEATURES;
  }

  public boolean hasChildFeatures()
  {
    return getChildFeatures().length != 0;
  }

  protected abstract String computeLabel(EModelElement element);

  public static ElementHandler getHandler(EClass eClass)
  {
    if (eClass.getEPackage() == EcorePackage.eINSTANCE)
    {
      switch (eClass.getClassifierID())
      {
      case EcorePackage.EPACKAGE:
        return PACKAGE;

      case EcorePackage.ECLASS:
        return CLASS;

      case EcorePackage.EATTRIBUTE:
        return ATTRIBUTE;

      case EcorePackage.EREFERENCE:
        return REFERENCE;

      case EcorePackage.EDATA_TYPE:
        return DATA_TYPE;

      case EcorePackage.EENUM:
        return ENUM;

      case EcorePackage.EENUM_LITERAL:
        return ENUM_LITERAL;
      }
    }

    return null;
  }

  public static String getLabel(EModelElement element)
  {
    ElementHandler handler = getHandler(element.eClass());
    if (handler != null)
    {
      return handler.computeLabel(element);
    }

    return String.valueOf(element);
  }

  public static ModelSet getModelSet(EModelElement element)
  {
    EObject eContainer;
    while ((eContainer = element.eContainer()) != null)
    {
      if (eContainer instanceof ModelSet)
      {
        return (ModelSet)eContainer;
      }
    }

    return null;
  }

  public static void execute(EModelElement element, ElementRunnable runnable)
  {
    runnable.run(element);

    EClass eClass = element.eClass();
    ElementHandler handler = getHandler(eClass);
    if (handler != null)
    {
      EReference[] childFeatures = handler.getChildFeatures();
      for (int i = 0; i < childFeatures.length; i++)
      {
        EReference childFeature = childFeatures[i];

        List<?> children = (List<?>)element.eGet(childFeature);
        for (Object child : children)
        {
          if (child instanceof EModelElement)
          {
            execute((EModelElement)child, runnable);
          }
        }
      }
    }
  }

  public static void execute(List<? extends EModelElement> elements, ElementRunnable runnable)
  {
    for (EModelElement element : elements)
    {
      execute(element, runnable);
    }
  }

  public static void main(String[] args)
  {
    // dumpRelevantContainments();
    dumpManyValuedNonContainments();
  }

  private static void dumpRelevantContainments()
  {
    for (EClassifier eClassifier : EcorePackage.eINSTANCE.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;

        if (eClass == EcorePackage.Literals.EOPERATION)
        {
          continue;
        }

        if (eClass == EcorePackage.Literals.EGENERIC_TYPE)
        {
          continue;
        }

        if (eClass == EcorePackage.Literals.ETYPE_PARAMETER)
        {
          continue;
        }

        boolean first = true;
        for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures())
        {
          if (eStructuralFeature instanceof EReference)
          {
            EReference eReference = (EReference)eStructuralFeature;
            if (eReference.isContainment())
            {
              if (eReference == EcorePackage.Literals.ECLASS__EOPERATIONS)
              {
                continue;
              }

              if (eReference == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES)
              {
                continue;
              }

              if (eReference == EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS)
              {
                continue;
              }

              if (eReference == EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE)
              {
                continue;
              }

              if (first)
              {
                System.out.println(eClass.getName());
                first = false;
              }

              System.out.println("   " + eReference.getName() + (eReference.isDerived() ? "  DERIVED" : ""));
            }
          }
        }
      }
    }
  }

  private static void dumpManyValuedNonContainments()
  {
    for (EClassifier eClassifier : EcorePackage.eINSTANCE.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;

        boolean first = true;
        for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures())
        {
          if (!eStructuralFeature.isMany())
          {
            continue;
          }

          if (eStructuralFeature instanceof EReference)
          {
            EReference eReference = (EReference)eStructuralFeature;
            if (eReference.isContainment())
            {
              continue;
            }
          }

          if (first)
          {
            System.out.println(eClass.getName());
            first = false;
          }

          System.out.println("   " + eStructuralFeature.getName() + (eStructuralFeature.isDerived() ? "  DERIVED" : ""));
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class PackageHandler extends ElementHandler
  {
    private static final EReference[] CHILDREN = { EcorePackage.Literals.EPACKAGE__ECLASSIFIERS, EcorePackage.Literals.EPACKAGE__ESUBPACKAGES };

    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.EPACKAGE;
    }

    @Override
    public EReference[] getChildFeatures()
    {
      return CHILDREN;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EPackage ePackage = (EPackage)element;
      EPackage eSuperPackage = ePackage.getESuperPackage();
      if (eSuperPackage != null)
      {
        return computeLabel(eSuperPackage) + "." + ePackage.getName();
      }

      return ePackage.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ClassHandler extends ElementHandler
  {
    private static final EReference[] CHILDREN = { EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES };

    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.ECLASS;
    }

    @Override
    public EReference[] getChildFeatures()
    {
      return CHILDREN;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EClass eClass = (EClass)element;
      EPackage ePackage = eClass.getEPackage();
      if (ePackage != null)
      {
        return getLabel(ePackage) + "." + eClass.getName();
      }

      return eClass.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class AttributeHandler extends ElementHandler
  {
    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.EATTRIBUTE;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EAttribute eAttribute = (EAttribute)element;
      EClass eClass = eAttribute.getEContainingClass();
      if (eClass != null)
      {
        return getLabel(eClass) + "." + eAttribute.getName();
      }

      return eAttribute.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ReferenceHandler extends ElementHandler
  {
    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.EREFERENCE;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EReference eReference = (EReference)element;
      EClass eClass = eReference.getEContainingClass();
      if (eClass != null)
      {
        return getLabel(eClass) + "." + eReference.getName();
      }

      return eReference.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DataTypeHandler extends ElementHandler
  {
    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.EDATA_TYPE;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EDataType eDataType = (EDataType)element;
      EPackage ePackage = eDataType.getEPackage();
      if (ePackage != null)
      {
        return getLabel(ePackage) + "." + eDataType.getName();
      }

      return eDataType.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class EnumHandler extends ElementHandler
  {
    private static final EReference[] CHILDREN = { EcorePackage.Literals.EENUM__ELITERALS };

    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.EENUM;
    }

    @Override
    public EReference[] getChildFeatures()
    {
      return CHILDREN;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EEnum eEnum = (EEnum)element;
      EPackage ePackage = eEnum.getEPackage();
      if (ePackage != null)
      {
        return getLabel(ePackage) + "." + eEnum.getName();
      }

      return eEnum.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class EnumLiteralHandler extends ElementHandler
  {
    @Override
    public EClass getEClass()
    {
      return EcorePackage.Literals.EENUM_LITERAL;
    }

    @Override
    protected String computeLabel(EModelElement element)
    {
      EEnumLiteral eLiteral = (EEnumLiteral)element;
      EEnum eEnum = eLiteral.getEEnum();
      if (eEnum != null)
      {
        return getLabel(eEnum) + "." + eLiteral.getName();
      }

      return eLiteral.getName();
    }
  }
}
