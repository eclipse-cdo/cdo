/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.mango.MangoParameter;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.mango.MangoValueList;
import org.eclipse.emf.cdo.tests.mango.ParameterPassing;
import org.eclipse.emf.cdo.tests.mango.legacy.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.legacy.MangoPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class MangoPackageImpl extends EPackageImpl implements MangoPackage
{
  /**
   * @ADDED
   */
  public static final MangoPackage eINSTANCE = org.eclipse.emf.cdo.tests.mango.legacy.impl.MangoPackageImpl.init();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass mangoValueListEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass mangoValueEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass mangoParameterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EEnum parameterPassingEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.legacy.mango.MangoPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private MangoPackageImpl()
  {
    super(eNS_URI, MangoFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link MangoPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static MangoPackage init()
  {
    if (isInited)
    {
      return (MangoPackage)EPackage.Registry.INSTANCE.getEPackage(MangoPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredMangoPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    MangoPackageImpl theMangoPackage = registeredMangoPackage instanceof MangoPackageImpl ? (MangoPackageImpl)registeredMangoPackage : new MangoPackageImpl();

    isInited = true;

    // Create package meta-data objects
    theMangoPackage.createPackageContents();

    // Initialize created meta-data
    theMangoPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theMangoPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(MangoPackage.eNS_URI, theMangoPackage);
    return theMangoPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMangoValueList()
  {
    return mangoValueListEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMangoValueList_Name()
  {
    return (EAttribute)mangoValueListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMangoValueList_Values()
  {
    return (EReference)mangoValueListEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMangoValue()
  {
    return mangoValueEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMangoValue_Name()
  {
    return (EAttribute)mangoValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMangoParameter()
  {
    return mangoParameterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMangoParameter_Name()
  {
    return (EAttribute)mangoParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMangoParameter_Passing()
  {
    return (EAttribute)mangoParameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getParameterPassing()
  {
    return parameterPassingEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MangoFactory getMangoFactory()
  {
    return (MangoFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    mangoValueListEClass = createEClass(MANGO_VALUE_LIST);
    createEAttribute(mangoValueListEClass, MANGO_VALUE_LIST__NAME);
    createEReference(mangoValueListEClass, MANGO_VALUE_LIST__VALUES);

    mangoValueEClass = createEClass(MANGO_VALUE);
    createEAttribute(mangoValueEClass, MANGO_VALUE__NAME);

    mangoParameterEClass = createEClass(MANGO_PARAMETER);
    createEAttribute(mangoParameterEClass, MANGO_PARAMETER__NAME);
    createEAttribute(mangoParameterEClass, MANGO_PARAMETER__PASSING);

    // Create enums
    parameterPassingEEnum = createEEnum(PARAMETER_PASSING);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(mangoValueListEClass, MangoValueList.class, "MangoValueList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMangoValueList_Name(), ecorePackage.getEString(), "name", null, 0, 1, MangoValueList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMangoValueList_Values(), getMangoValue(), null, "values", null, 0, -1, MangoValueList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mangoValueEClass, MangoValue.class, "MangoValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMangoValue_Name(), ecorePackage.getEString(), "name", null, 0, 1, MangoValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mangoParameterEClass, MangoParameter.class, "MangoParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMangoParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, MangoParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMangoParameter_Passing(), getParameterPassing(), "passing", null, 0, 1, MangoParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(parameterPassingEEnum, ParameterPassing.class, "ParameterPassing");
    addEEnumLiteral(parameterPassingEEnum, ParameterPassing.BY_VALUE);
    addEEnumLiteral(parameterPassingEEnum, ParameterPassing.BY_REFERENCE);

    // Create resource
    createResource(eNS_URI);
  }

} // MangoPackageImpl
