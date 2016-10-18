/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.impl;

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClassChild;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.AParameter;
import org.eclipse.emf.cdo.dawn.examples.acore.AccessType;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class AcoreFactoryImpl extends EFactoryImpl implements AcoreFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static AcoreFactory init()
  {
    try
    {
      AcoreFactory theAcoreFactory = (AcoreFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore");
      if (theAcoreFactory != null)
      {
        return theAcoreFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new AcoreFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AcoreFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case AcorePackage.ACLASS:
      return createAClass();
    case AcorePackage.AINTERFACE:
      return createAInterface();
    case AcorePackage.ACORE_ROOT:
      return createACoreRoot();
    case AcorePackage.AATTRIBUTE:
      return createAAttribute();
    case AcorePackage.AOPERATION:
      return createAOperation();
    case AcorePackage.ABASIC_CLASS:
      return createABasicClass();
    case AcorePackage.APARAMETER:
      return createAParameter();
    case AcorePackage.ACLASS_CHILD:
      return createAClassChild();
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
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case AcorePackage.ACCESS_TYPE:
      return createAccessTypeFromString(eDataType, initialValue);
    case AcorePackage.ACCESS_TYPE_OBJECT:
      return createAccessTypeObjectFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case AcorePackage.ACCESS_TYPE:
      return convertAccessTypeToString(eDataType, instanceValue);
    case AcorePackage.ACCESS_TYPE_OBJECT:
      return convertAccessTypeObjectToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AClass createAClass()
  {
    AClassImpl aClass = new AClassImpl();
    return aClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AInterface createAInterface()
  {
    AInterfaceImpl aInterface = new AInterfaceImpl();
    return aInterface;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ACoreRoot createACoreRoot()
  {
    ACoreRootImpl aCoreRoot = new ACoreRootImpl();
    return aCoreRoot;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AAttribute createAAttribute()
  {
    AAttributeImpl aAttribute = new AAttributeImpl();
    return aAttribute;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AOperation createAOperation()
  {
    AOperationImpl aOperation = new AOperationImpl();
    return aOperation;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ABasicClass createABasicClass()
  {
    ABasicClassImpl aBasicClass = new ABasicClassImpl();
    return aBasicClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AParameter createAParameter()
  {
    AParameterImpl aParameter = new AParameterImpl();
    return aParameter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AClassChild createAClassChild()
  {
    AClassChildImpl aClassChild = new AClassChildImpl();
    return aClassChild;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AccessType createAccessTypeFromString(EDataType eDataType, String initialValue)
  {
    AccessType result = AccessType.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertAccessTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AccessType createAccessTypeObjectFromString(EDataType eDataType, String initialValue)
  {
    return createAccessTypeFromString(AcorePackage.Literals.ACCESS_TYPE, initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertAccessTypeObjectToString(EDataType eDataType, Object instanceValue)
  {
    return convertAccessTypeToString(AcorePackage.Literals.ACCESS_TYPE, instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AcorePackage getAcorePackage()
  {
    return (AcorePackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static AcorePackage getPackage()
  {
    return AcorePackage.eINSTANCE;
  }

} // AcoreFactoryImpl
