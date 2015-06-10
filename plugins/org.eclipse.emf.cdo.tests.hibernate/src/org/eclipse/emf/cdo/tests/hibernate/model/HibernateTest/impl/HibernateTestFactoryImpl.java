/*
 * Copyright (c) 2012, 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A1;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class HibernateTestFactoryImpl extends EFactoryImpl implements HibernateTestFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static HibernateTestFactory init()
  {
    try
    {
      HibernateTestFactory theHibernateTestFactory = (HibernateTestFactory)EPackage.Registry.INSTANCE
          .getEFactory(HibernateTestPackage.eNS_URI);
      if (theHibernateTestFactory != null)
      {
        return theHibernateTestFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new HibernateTestFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public HibernateTestFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case HibernateTestPackage.BZ356181_MAIN:
      return createBz356181_Main();
    case HibernateTestPackage.BZ356181_TRANSIENT:
      return createBz356181_Transient();
    case HibernateTestPackage.BZ356181_NON_TRANSIENT:
      return createBz356181_NonTransient();
    case HibernateTestPackage.BZ387752_MAIN:
      return createBz387752_Main();
    case HibernateTestPackage.BZ380987_GROUP:
      return createBz380987_Group();
    case HibernateTestPackage.BZ380987_PLACE:
      return createBz380987_Place();
    case HibernateTestPackage.BZ380987_PERSON:
      return createBz380987_Person();
    case HibernateTestPackage.BZ398057_A:
      return createBz398057A();
    case HibernateTestPackage.BZ398057_A1:
      return createBz398057A1();
    case HibernateTestPackage.BZ398057_B:
      return createBz398057B();
    case HibernateTestPackage.BZ398057_B1:
      return createBz398057B1();
    case HibernateTestPackage.BZ397682_P:
      return createBz397682P();
    case HibernateTestPackage.BZ397682_C:
      return createBz397682C();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case HibernateTestPackage.BZ387752_ENUM:
      return createBz387752_EnumFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case HibernateTestPackage.BZ387752_ENUM:
      return convertBz387752_EnumToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz356181_Main createBz356181_Main()
  {
    Bz356181_MainImpl bz356181_Main = new Bz356181_MainImpl();
    return bz356181_Main;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz356181_Transient createBz356181_Transient()
  {
    Bz356181_TransientImpl bz356181_Transient = new Bz356181_TransientImpl();
    return bz356181_Transient;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz356181_NonTransient createBz356181_NonTransient()
  {
    Bz356181_NonTransientImpl bz356181_NonTransient = new Bz356181_NonTransientImpl();
    return bz356181_NonTransient;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz387752_Main createBz387752_Main()
  {
    Bz387752_MainImpl bz387752_Main = new Bz387752_MainImpl();
    return bz387752_Main;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz380987_Group createBz380987_Group()
  {
    Bz380987_GroupImpl bz380987_Group = new Bz380987_GroupImpl();
    return bz380987_Group;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz380987_Place createBz380987_Place()
  {
    Bz380987_PlaceImpl bz380987_Place = new Bz380987_PlaceImpl();
    return bz380987_Place;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz380987_Person createBz380987_Person()
  {
    Bz380987_PersonImpl bz380987_Person = new Bz380987_PersonImpl();
    return bz380987_Person;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz398057A createBz398057A()
  {
    Bz398057AImpl bz398057A = new Bz398057AImpl();
    return bz398057A;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz398057A1 createBz398057A1()
  {
    Bz398057A1Impl bz398057A1 = new Bz398057A1Impl();
    return bz398057A1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz398057B createBz398057B()
  {
    Bz398057BImpl bz398057B = new Bz398057BImpl();
    return bz398057B;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz398057B1 createBz398057B1()
  {
    Bz398057B1Impl bz398057B1 = new Bz398057B1Impl();
    return bz398057B1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz397682P createBz397682P()
  {
    Bz397682PImpl bz397682P = new Bz397682PImpl();
    return bz397682P;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz397682C createBz397682C()
  {
    Bz397682CImpl bz397682C = new Bz397682CImpl();
    return bz397682C;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz387752_Enum createBz387752_EnumFromString(EDataType eDataType, String initialValue)
  {
    Bz387752_Enum result = Bz387752_Enum.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException(
          "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertBz387752_EnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public HibernateTestPackage getHibernateTestPackage()
  {
    return (HibernateTestPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static HibernateTestPackage getPackage()
  {
    return HibernateTestPackage.eINSTANCE;
  }

} // HibernateTestFactoryImpl
