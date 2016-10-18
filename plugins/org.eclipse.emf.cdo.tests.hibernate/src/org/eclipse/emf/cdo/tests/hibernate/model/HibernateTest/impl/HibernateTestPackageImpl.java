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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class HibernateTestPackageImpl extends EPackageImpl implements HibernateTestPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz356181_MainEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz356181_TransientEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz356181_NonTransientEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz387752_MainEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz380987_GroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz380987_PlaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz380987_PersonEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz398057AEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz398057A1EClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz398057BEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz398057B1EClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz397682PEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bz397682CEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum bz387752_EnumEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private HibernateTestPackageImpl()
  {
    super(eNS_URI, HibernateTestFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link HibernateTestPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static HibernateTestPackage init()
  {
    if (isInited)
    {
      return (HibernateTestPackage)EPackage.Registry.INSTANCE.getEPackage(HibernateTestPackage.eNS_URI);
    }

    // Obtain or create and register package
    HibernateTestPackageImpl theHibernateTestPackage = (HibernateTestPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof HibernateTestPackageImpl
        ? EPackage.Registry.INSTANCE.get(eNS_URI) : new HibernateTestPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theHibernateTestPackage.createPackageContents();

    // Initialize created meta-data
    theHibernateTestPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theHibernateTestPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(HibernateTestPackage.eNS_URI, theHibernateTestPackage);
    return theHibernateTestPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz356181_Main()
  {
    return bz356181_MainEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz356181_Main_Transient()
  {
    return (EAttribute)bz356181_MainEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz356181_Main_NonTransient()
  {
    return (EAttribute)bz356181_MainEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz356181_Main_TransientRef()
  {
    return (EReference)bz356181_MainEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz356181_Main_TransientOtherRef()
  {
    return (EReference)bz356181_MainEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz356181_Transient()
  {
    return bz356181_TransientEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz356181_NonTransient()
  {
    return bz356181_NonTransientEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz356181_NonTransient_Main()
  {
    return (EReference)bz356181_NonTransientEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz387752_Main()
  {
    return bz387752_MainEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz387752_Main_StrUnsettable()
  {
    return (EAttribute)bz387752_MainEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz387752_Main_StrSettable()
  {
    return (EAttribute)bz387752_MainEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz387752_Main_EnumSettable()
  {
    return (EAttribute)bz387752_MainEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz387752_Main_EnumUnsettable()
  {
    return (EAttribute)bz387752_MainEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz380987_Group()
  {
    return bz380987_GroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz380987_Group_People()
  {
    return (EReference)bz380987_GroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz380987_Place()
  {
    return bz380987_PlaceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz380987_Place_Name()
  {
    return (EAttribute)bz380987_PlaceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz380987_Place_People()
  {
    return (EReference)bz380987_PlaceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz380987_Person()
  {
    return bz380987_PersonEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz380987_Person_Name()
  {
    return (EAttribute)bz380987_PersonEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz380987_Person_Group()
  {
    return (EReference)bz380987_PersonEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz380987_Person_Places()
  {
    return (EReference)bz380987_PersonEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz398057A()
  {
    return bz398057AEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz398057A_ListOfB()
  {
    return (EReference)bz398057AEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz398057A_DbId()
  {
    return (EAttribute)bz398057AEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz398057A1()
  {
    return bz398057A1EClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz398057B()
  {
    return bz398057BEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz398057B_RefToClassA()
  {
    return (EReference)bz398057BEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz398057B_Value()
  {
    return (EAttribute)bz398057BEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz398057B_DbId()
  {
    return (EAttribute)bz398057BEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz398057B1()
  {
    return bz398057B1EClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz398057B1_ValueStr()
  {
    return (EAttribute)bz398057B1EClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz397682P()
  {
    return bz397682PEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz397682P_DbId()
  {
    return (EAttribute)bz397682PEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz397682P_ListOfC()
  {
    return (EReference)bz397682PEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBz397682C()
  {
    return bz397682CEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz397682C_RefToP()
  {
    return (EReference)bz397682CEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBz397682C_RefToC()
  {
    return (EReference)bz397682CEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBz397682C_DbId()
  {
    return (EAttribute)bz397682CEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getBz387752_Enum()
  {
    return bz387752_EnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public HibernateTestFactory getHibernateTestFactory()
  {
    return (HibernateTestFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
    bz356181_MainEClass = createEClass(BZ356181_MAIN);
    createEAttribute(bz356181_MainEClass, BZ356181_MAIN__TRANSIENT);
    createEAttribute(bz356181_MainEClass, BZ356181_MAIN__NON_TRANSIENT);
    createEReference(bz356181_MainEClass, BZ356181_MAIN__TRANSIENT_REF);
    createEReference(bz356181_MainEClass, BZ356181_MAIN__TRANSIENT_OTHER_REF);

    bz356181_TransientEClass = createEClass(BZ356181_TRANSIENT);

    bz356181_NonTransientEClass = createEClass(BZ356181_NON_TRANSIENT);
    createEReference(bz356181_NonTransientEClass, BZ356181_NON_TRANSIENT__MAIN);

    bz387752_MainEClass = createEClass(BZ387752_MAIN);
    createEAttribute(bz387752_MainEClass, BZ387752_MAIN__STR_UNSETTABLE);
    createEAttribute(bz387752_MainEClass, BZ387752_MAIN__STR_SETTABLE);
    createEAttribute(bz387752_MainEClass, BZ387752_MAIN__ENUM_SETTABLE);
    createEAttribute(bz387752_MainEClass, BZ387752_MAIN__ENUM_UNSETTABLE);

    bz380987_GroupEClass = createEClass(BZ380987_GROUP);
    createEReference(bz380987_GroupEClass, BZ380987_GROUP__PEOPLE);

    bz380987_PlaceEClass = createEClass(BZ380987_PLACE);
    createEAttribute(bz380987_PlaceEClass, BZ380987_PLACE__NAME);
    createEReference(bz380987_PlaceEClass, BZ380987_PLACE__PEOPLE);

    bz380987_PersonEClass = createEClass(BZ380987_PERSON);
    createEAttribute(bz380987_PersonEClass, BZ380987_PERSON__NAME);
    createEReference(bz380987_PersonEClass, BZ380987_PERSON__GROUP);
    createEReference(bz380987_PersonEClass, BZ380987_PERSON__PLACES);

    bz398057AEClass = createEClass(BZ398057_A);
    createEReference(bz398057AEClass, BZ398057_A__LIST_OF_B);
    createEAttribute(bz398057AEClass, BZ398057_A__DB_ID);

    bz398057A1EClass = createEClass(BZ398057_A1);

    bz398057BEClass = createEClass(BZ398057_B);
    createEReference(bz398057BEClass, BZ398057_B__REF_TO_CLASS_A);
    createEAttribute(bz398057BEClass, BZ398057_B__VALUE);
    createEAttribute(bz398057BEClass, BZ398057_B__DB_ID);

    bz398057B1EClass = createEClass(BZ398057_B1);
    createEAttribute(bz398057B1EClass, BZ398057_B1__VALUE_STR);

    bz397682PEClass = createEClass(BZ397682_P);
    createEAttribute(bz397682PEClass, BZ397682_P__DB_ID);
    createEReference(bz397682PEClass, BZ397682_P__LIST_OF_C);

    bz397682CEClass = createEClass(BZ397682_C);
    createEReference(bz397682CEClass, BZ397682_C__REF_TO_P);
    createEReference(bz397682CEClass, BZ397682_C__REF_TO_C);
    createEAttribute(bz397682CEClass, BZ397682_C__DB_ID);

    // Create enums
    bz387752_EnumEEnum = createEEnum(BZ387752_ENUM);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
    bz398057A1EClass.getESuperTypes().add(getBz398057A());
    bz398057B1EClass.getESuperTypes().add(getBz398057B());

    // Initialize classes and features; add operations and parameters
    initEClass(bz356181_MainEClass, Bz356181_Main.class, "Bz356181_Main", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBz356181_Main_Transient(), ecorePackage.getEString(), "transient", null, 0, 1, Bz356181_Main.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz356181_Main_NonTransient(), ecorePackage.getEString(), "nonTransient", null, 0, 1, Bz356181_Main.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz356181_Main_TransientRef(), getBz356181_Transient(), null, "transientRef", null, 0, 1, Bz356181_Main.class, IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz356181_Main_TransientOtherRef(), getBz356181_NonTransient(), null, "transientOtherRef", null, 0, 1, Bz356181_Main.class, IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz356181_TransientEClass, Bz356181_Transient.class, "Bz356181_Transient", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(bz356181_NonTransientEClass, Bz356181_NonTransient.class, "Bz356181_NonTransient", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBz356181_NonTransient_Main(), getBz356181_Main(), null, "main", null, 0, 1, Bz356181_NonTransient.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz387752_MainEClass, Bz387752_Main.class, "Bz387752_Main", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBz387752_Main_StrUnsettable(), ecorePackage.getEString(), "strUnsettable", "def_value", 0, 1, Bz387752_Main.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz387752_Main_StrSettable(), ecorePackage.getEString(), "strSettable", "value", 0, 1, Bz387752_Main.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz387752_Main_EnumSettable(), getBz387752_Enum(), "enumSettable", null, 0, 1, Bz387752_Main.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz387752_Main_EnumUnsettable(), getBz387752_Enum(), "enumUnsettable", "VAL1", 0, 1, Bz387752_Main.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz380987_GroupEClass, Bz380987_Group.class, "Bz380987_Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBz380987_Group_People(), getBz380987_Person(), getBz380987_Person_Group(), "people", null, 0, -1, Bz380987_Group.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz380987_PlaceEClass, Bz380987_Place.class, "Bz380987_Place", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBz380987_Place_Name(), ecorePackage.getEString(), "name", null, 0, 1, Bz380987_Place.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz380987_Place_People(), getBz380987_Person(), getBz380987_Person_Places(), "people", null, 0, -1, Bz380987_Place.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz380987_PersonEClass, Bz380987_Person.class, "Bz380987_Person", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBz380987_Person_Name(), ecorePackage.getEString(), "name", null, 0, 1, Bz380987_Person.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz380987_Person_Group(), getBz380987_Group(), getBz380987_Group_People(), "group", null, 0, -1, Bz380987_Person.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz380987_Person_Places(), getBz380987_Place(), getBz380987_Place_People(), "places", null, 0, -1, Bz380987_Person.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz398057AEClass, Bz398057A.class, "Bz398057A", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBz398057A_ListOfB(), getBz398057B(), getBz398057B_RefToClassA(), "listOfB", null, 0, -1, Bz398057A.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz398057A_DbId(), ecorePackage.getEString(), "dbId", null, 1, 1, Bz398057A.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz398057A1EClass, Bz398057A1.class, "Bz398057A1", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(bz398057BEClass, Bz398057B.class, "Bz398057B", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBz398057B_RefToClassA(), getBz398057A(), getBz398057A_ListOfB(), "refToClassA", null, 0, 1, Bz398057B.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz398057B_Value(), ecorePackage.getEDouble(), "value", null, 0, 1, Bz398057B.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz398057B_DbId(), ecorePackage.getEString(), "dbId", null, 1, 1, Bz398057B.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz398057B1EClass, Bz398057B1.class, "Bz398057B1", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBz398057B1_ValueStr(), ecorePackage.getEString(), "valueStr", null, 0, 1, Bz398057B1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz397682PEClass, Bz397682P.class, "Bz397682P", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBz397682P_DbId(), ecorePackage.getEString(), "dbId", null, 1, 1, Bz397682P.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz397682P_ListOfC(), getBz397682C(), getBz397682C_RefToP(), "listOfC", null, 0, -1, Bz397682P.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bz397682CEClass, Bz397682C.class, "Bz397682C", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBz397682C_RefToP(), getBz397682P(), getBz397682P_ListOfC(), "refToP", null, 0, 1, Bz397682C.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBz397682C_RefToC(), getBz397682C(), null, "refToC", null, 0, 1, Bz397682C.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBz397682C_DbId(), ecorePackage.getEString(), "dbId", null, 1, 1, Bz397682C.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(bz387752_EnumEEnum, Bz387752_Enum.class, "Bz387752_Enum");
    addEEnumLiteral(bz387752_EnumEEnum, Bz387752_Enum.VAL0);
    addEEnumLiteral(bz387752_EnumEEnum, Bz387752_Enum.VAL1);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // teneo.jpa
    createTeneoAnnotations();
  }

  /**
   * Initializes the annotations for <b>teneo.jpa</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createTeneoAnnotations()
  {
    String source = "teneo.jpa";
    addAnnotation(getBz356181_Main_Transient(), source, new String[] { "value", "@Transient" });
    addAnnotation(bz356181_TransientEClass, source, new String[] { "value", "@Transient" });
    addAnnotation(bz398057AEClass, source, new String[] { "value", "@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)" });
    addAnnotation(getBz398057A_DbId(), source, new String[] { "value", "@Id" });
    addAnnotation(bz398057BEClass, source, new String[] { "value", "@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)" });
    addAnnotation(getBz398057B_DbId(), source, new String[] { "value", "@Id" });
    addAnnotation(bz397682PEClass, source, new String[] { "value", "@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)" });
    addAnnotation(getBz397682P_DbId(), source, new String[] { "value", "@Id" });
    addAnnotation(getBz397682C_DbId(), source, new String[] { "value", "@Id" });
  }

} // HibernateTestPackageImpl
