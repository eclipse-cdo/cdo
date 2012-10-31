/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.legacy.model5.impl;

import org.eclipse.emf.cdo.tests.legacy.model5.Model5Factory;
import org.eclipse.emf.cdo.tests.legacy.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.GenListOfBoolean;
import org.eclipse.emf.cdo.tests.model5.GenListOfChar;
import org.eclipse.emf.cdo.tests.model5.GenListOfDate;
import org.eclipse.emf.cdo.tests.model5.GenListOfDouble;
import org.eclipse.emf.cdo.tests.model5.GenListOfFloat;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.GenListOfIntArray;
import org.eclipse.emf.cdo.tests.model5.GenListOfInteger;
import org.eclipse.emf.cdo.tests.model5.GenListOfLong;
import org.eclipse.emf.cdo.tests.model5.GenListOfShort;
import org.eclipse.emf.cdo.tests.model5.GenListOfString;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model5PackageImpl extends EPackageImpl implements Model5Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass testFeatureMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass managerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass doctorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfStringEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfIntEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfIntegerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfLongEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfBooleanEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfShortEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfFloatEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfDoubleEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfDateEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfCharEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass genListOfIntArrayEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass parentEClass = null;

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  private EClass childEClass = null;

  /**
  	 * <!-- begin-user-doc --> <!-- end-user-doc -->
  	 * @generated
  	 */
  private EDataType intArrayEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.legacy.model5.Model5Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model5PackageImpl()
  {
    super(eNS_URI, Model5Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link Model5Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model5Package init()
  {
    if (isInited)
      return (Model5Package)EPackage.Registry.INSTANCE.getEPackage(Model5Package.eNS_URI);

    // Obtain or create and register package
    Model5PackageImpl theModel5Package = (Model5PackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof Model5PackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new Model5PackageImpl());

    isInited = true;

    // Create package meta-data objects
    theModel5Package.createPackageContents();

    // Initialize created meta-data
    theModel5Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel5Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Model5Package.eNS_URI, theModel5Package);
    return theModel5Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getTestFeatureMap()
  {
    return testFeatureMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getTestFeatureMap_Managers()
  {
    return (EReference)testFeatureMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getTestFeatureMap_Doctors()
  {
    return (EReference)testFeatureMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTestFeatureMap_People()
  {
    return (EAttribute)testFeatureMapEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getManager()
  {
    return managerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getDoctor()
  {
    return doctorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfString()
  {
    return genListOfStringEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfString_Elements()
  {
    return (EAttribute)genListOfStringEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfInt()
  {
    return genListOfIntEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfInt_Elements()
  {
    return (EAttribute)genListOfIntEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfInteger()
  {
    return genListOfIntegerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfInteger_Elements()
  {
    return (EAttribute)genListOfIntegerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfLong()
  {
    return genListOfLongEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfLong_Elements()
  {
    return (EAttribute)genListOfLongEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfBoolean()
  {
    return genListOfBooleanEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfBoolean_Elements()
  {
    return (EAttribute)genListOfBooleanEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfShort()
  {
    return genListOfShortEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfShort_Elements()
  {
    return (EAttribute)genListOfShortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfFloat()
  {
    return genListOfFloatEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfFloat_Elements()
  {
    return (EAttribute)genListOfFloatEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfDouble()
  {
    return genListOfDoubleEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfDouble_Elements()
  {
    return (EAttribute)genListOfDoubleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfDate()
  {
    return genListOfDateEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfDate_Elements()
  {
    return (EAttribute)genListOfDateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfChar()
  {
    return genListOfCharEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfChar_Elements()
  {
    return (EAttribute)genListOfCharEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenListOfIntArray()
  {
    return genListOfIntArrayEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGenListOfIntArray_Elements()
  {
    return (EAttribute)genListOfIntArrayEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getParent()
  {
    return parentEClass;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public EReference getParent_Children()
  {
    return (EReference)parentEClass.getEStructuralFeatures().get(0);
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public EReference getParent_Favourite()
  {
    return (EReference)parentEClass.getEStructuralFeatures().get(1);
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public EClass getChild()
  {
    return childEClass;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public EReference getChild_Parent()
  {
    return (EReference)childEClass.getEStructuralFeatures().get(0);
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public EReference getChild_PreferredBy()
  {
    return (EReference)childEClass.getEStructuralFeatures().get(1);
  }

  /**
  	 * <!-- begin-user-doc --> <!-- end-user-doc -->
  	 * @generated
  	 */
  public EDataType getIntArray()
  {
    return intArrayEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model5Factory getModel5Factory()
  {
    return (Model5Factory)getEFactoryInstance();
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
      return;
    isCreated = true;

    // Create classes and their features
    testFeatureMapEClass = createEClass(TEST_FEATURE_MAP);
    createEReference(testFeatureMapEClass, TEST_FEATURE_MAP__MANAGERS);
    createEReference(testFeatureMapEClass, TEST_FEATURE_MAP__DOCTORS);
    createEAttribute(testFeatureMapEClass, TEST_FEATURE_MAP__PEOPLE);

    managerEClass = createEClass(MANAGER);

    doctorEClass = createEClass(DOCTOR);

    genListOfStringEClass = createEClass(GEN_LIST_OF_STRING);
    createEAttribute(genListOfStringEClass, GEN_LIST_OF_STRING__ELEMENTS);

    genListOfIntEClass = createEClass(GEN_LIST_OF_INT);
    createEAttribute(genListOfIntEClass, GEN_LIST_OF_INT__ELEMENTS);

    genListOfIntegerEClass = createEClass(GEN_LIST_OF_INTEGER);
    createEAttribute(genListOfIntegerEClass, GEN_LIST_OF_INTEGER__ELEMENTS);

    genListOfLongEClass = createEClass(GEN_LIST_OF_LONG);
    createEAttribute(genListOfLongEClass, GEN_LIST_OF_LONG__ELEMENTS);

    genListOfBooleanEClass = createEClass(GEN_LIST_OF_BOOLEAN);
    createEAttribute(genListOfBooleanEClass, GEN_LIST_OF_BOOLEAN__ELEMENTS);

    genListOfShortEClass = createEClass(GEN_LIST_OF_SHORT);
    createEAttribute(genListOfShortEClass, GEN_LIST_OF_SHORT__ELEMENTS);

    genListOfFloatEClass = createEClass(GEN_LIST_OF_FLOAT);
    createEAttribute(genListOfFloatEClass, GEN_LIST_OF_FLOAT__ELEMENTS);

    genListOfDoubleEClass = createEClass(GEN_LIST_OF_DOUBLE);
    createEAttribute(genListOfDoubleEClass, GEN_LIST_OF_DOUBLE__ELEMENTS);

    genListOfDateEClass = createEClass(GEN_LIST_OF_DATE);
    createEAttribute(genListOfDateEClass, GEN_LIST_OF_DATE__ELEMENTS);

    genListOfCharEClass = createEClass(GEN_LIST_OF_CHAR);
    createEAttribute(genListOfCharEClass, GEN_LIST_OF_CHAR__ELEMENTS);

    genListOfIntArrayEClass = createEClass(GEN_LIST_OF_INT_ARRAY);
    createEAttribute(genListOfIntArrayEClass, GEN_LIST_OF_INT_ARRAY__ELEMENTS);

    parentEClass = createEClass(PARENT);
    createEReference(parentEClass, PARENT__CHILDREN);
    createEReference(parentEClass, PARENT__FAVOURITE);

    childEClass = createEClass(CHILD);
    createEReference(childEClass, CHILD__PARENT);
    createEReference(childEClass, CHILD__PREFERRED_BY);

    // Create data types
    intArrayEDataType = createEDataType(INT_ARRAY);
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
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(testFeatureMapEClass, TestFeatureMap.class, "TestFeatureMap", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTestFeatureMap_Managers(), this.getManager(), null, "managers", null, 0, -1,
        TestFeatureMap.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTestFeatureMap_Doctors(), this.getDoctor(), null, "doctors", null, 0, -1, TestFeatureMap.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTestFeatureMap_People(), ecorePackage.getEFeatureMapEntry(), "people", null, 0, -1,
        TestFeatureMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(managerEClass, Manager.class, "Manager", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(doctorEClass, Doctor.class, "Doctor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(genListOfStringEClass, GenListOfString.class, "GenListOfString", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfString_Elements(), ecorePackage.getEString(), "elements", null, 0, -1,
        GenListOfString.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfIntEClass, GenListOfInt.class, "GenListOfInt", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfInt_Elements(), ecorePackage.getEInt(), "elements", null, 0, -1, GenListOfInt.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfIntegerEClass, GenListOfInteger.class, "GenListOfInteger", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfInteger_Elements(), ecorePackage.getEIntegerObject(), "elements", null, 0, -1,
        GenListOfInteger.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfLongEClass, GenListOfLong.class, "GenListOfLong", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfLong_Elements(), ecorePackage.getELong(), "elements", null, 0, -1, GenListOfLong.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfBooleanEClass, GenListOfBoolean.class, "GenListOfBoolean", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfBoolean_Elements(), ecorePackage.getEBoolean(), "elements", null, 0, -1,
        GenListOfBoolean.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfShortEClass, GenListOfShort.class, "GenListOfShort", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfShort_Elements(), ecorePackage.getEShort(), "elements", null, 0, -1,
        GenListOfShort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfFloatEClass, GenListOfFloat.class, "GenListOfFloat", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfFloat_Elements(), ecorePackage.getEFloat(), "elements", null, 0, -1,
        GenListOfFloat.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfDoubleEClass, GenListOfDouble.class, "GenListOfDouble", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfDouble_Elements(), ecorePackage.getEDouble(), "elements", null, 0, -1,
        GenListOfDouble.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfDateEClass, GenListOfDate.class, "GenListOfDate", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfDate_Elements(), ecorePackage.getEDate(), "elements", null, 0, -1, GenListOfDate.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfCharEClass, GenListOfChar.class, "GenListOfChar", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfChar_Elements(), ecorePackage.getEChar(), "elements", null, 0, -1, GenListOfChar.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genListOfIntArrayEClass, GenListOfIntArray.class, "GenListOfIntArray", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGenListOfIntArray_Elements(), this.getIntArray(), "elements", null, 0, -1,
        GenListOfIntArray.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(parentEClass, Parent.class, "Parent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getParent_Children(), this.getChild(), this.getChild_Parent(), "children", null, 0, -1,
        Parent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getParent_Favourite(), this.getChild(), this.getChild_PreferredBy(), "favourite", null, 0, 1,
        Parent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(childEClass, Child.class, "Child", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChild_Parent(), this.getParent(), this.getParent_Children(), "parent", null, 0, 1, Child.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getChild_PreferredBy(), this.getParent(), this.getParent_Favourite(), "preferredBy", null, 0, 1,
        Child.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(intArrayEDataType, int[].class, "IntArray", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    addAnnotation(getTestFeatureMap_Managers(), source, new String[] { "group", "#people" });
    addAnnotation(getTestFeatureMap_Doctors(), source, new String[] { "group", "#people" });
    addAnnotation(getTestFeatureMap_People(), source, new String[] { "kind", "group" });
  }

} // Model5PackageImpl
