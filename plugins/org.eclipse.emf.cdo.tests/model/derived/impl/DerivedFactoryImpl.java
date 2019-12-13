/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package derived.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import derived.DerivedClass;
import derived.DerivedFactory;
import derived.DerivedPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class DerivedFactoryImpl extends EFactoryImpl implements DerivedFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static DerivedFactory init()
  {
    try
    {
      DerivedFactory theDerivedFactory = (DerivedFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.fernuni-hagen.de/ST/dummy/derived.ecore");
      if (theDerivedFactory != null)
      {
        return theDerivedFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DerivedFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DerivedFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @SuppressWarnings("cast")
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case DerivedPackage.DERIVED_CLASS:
      return createDerivedClass();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DerivedClass createDerivedClass()
  {
    DerivedClassImpl derivedClass = new DerivedClassImpl();
    return derivedClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DerivedPackage getDerivedPackage()
  {
    return (DerivedPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static DerivedPackage getPackage()
  {
    return DerivedPackage.eINSTANCE;
  }

} // DerivedFactoryImpl
