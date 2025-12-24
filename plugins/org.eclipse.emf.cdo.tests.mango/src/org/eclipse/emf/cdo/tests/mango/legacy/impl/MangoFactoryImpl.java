/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.mango.legacy.impl;

//import org.eclipse.emf.cdo.tests.mango.*;
import org.eclipse.emf.cdo.tests.mango.MangoParameter;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.mango.MangoValueList;
import org.eclipse.emf.cdo.tests.mango.ParameterPassing;
import org.eclipse.emf.cdo.tests.mango.legacy.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.legacy.MangoPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class MangoFactoryImpl extends EFactoryImpl implements MangoFactory
{
  /**
   * @ADDED
   */
  public static final MangoFactory eINSTANCE = org.eclipse.emf.cdo.tests.mango.legacy.impl.MangoFactoryImpl.init();

  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public static MangoFactory init()
  {
    try
    {
      MangoFactory theMangoFactory = (MangoFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/CDO/tests/legacy/mango");
      if (theMangoFactory instanceof MangoFactoryImpl)
      {
        return theMangoFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }

    MangoFactoryImpl factory = new MangoFactoryImpl();
    // EPackage.Registry.INSTANCE.put(factory.getEPackage().getNsURI(), factory);
    EPackage.Registry.INSTANCE.put("http://www.eclipse.org/emf/CDO/tests/legacy/mango", factory);
    return factory;
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public MangoFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case MangoPackage.MANGO_VALUE_LIST:
      return createMangoValueList();
    case MangoPackage.MANGO_VALUE:
      return createMangoValue();
    case MangoPackage.MANGO_PARAMETER:
      return createMangoParameter();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case MangoPackage.PARAMETER_PASSING:
      return createParameterPassingFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case MangoPackage.PARAMETER_PASSING:
      return convertParameterPassingToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MangoValueList createMangoValueList()
  {
    MangoValueListImpl mangoValueList = new MangoValueListImpl();
    return mangoValueList;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MangoValue createMangoValue()
  {
    MangoValueImpl mangoValue = new MangoValueImpl();
    return mangoValue;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MangoParameter createMangoParameter()
  {
    MangoParameterImpl mangoParameter = new MangoParameterImpl();
    return mangoParameter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ParameterPassing createParameterPassingFromString(EDataType eDataType, String initialValue)
  {
    ParameterPassing result = ParameterPassing.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertParameterPassingToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MangoPackage getMangoPackage()
  {
    return (MangoPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static MangoPackage getPackage()
  {
    return MangoPackage.eINSTANCE;
  }

} // MangoFactoryImpl
