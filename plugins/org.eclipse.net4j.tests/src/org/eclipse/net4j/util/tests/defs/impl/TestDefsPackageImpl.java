/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.tests.defs.impl;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.tests.defs.TestDef;
import org.eclipse.net4j.util.tests.defs.TestDefsFactory;
import org.eclipse.net4j.util.tests.defs.TestDefsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class TestDefsPackageImpl extends EPackageImpl implements TestDefsPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass testDefEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.net4j.util.tests.defs.TestDefsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private TestDefsPackageImpl()
  {
    super(eNS_URI, TestDefsFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link TestDefsPackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static TestDefsPackage init()
  {
    if (isInited)
    {
      return (TestDefsPackage)EPackage.Registry.INSTANCE.getEPackage(TestDefsPackage.eNS_URI);
    }

    // Obtain or create and register package
    TestDefsPackageImpl theTestDefsPackage = (TestDefsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof TestDefsPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new TestDefsPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    Net4jUtilDefsPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theTestDefsPackage.createPackageContents();

    // Initialize created meta-data
    theTestDefsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theTestDefsPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(TestDefsPackage.eNS_URI, theTestDefsPackage);
    return theTestDefsPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getTestDef()
  {
    return testDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getTestDef_References()
  {
    return (EReference)testDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getTestDef_Attribute()
  {
    return (EAttribute)testDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TestDefsFactory getTestDefsFactory()
  {
    return (TestDefsFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
    testDefEClass = createEClass(TEST_DEF);
    createEReference(testDefEClass, TEST_DEF__REFERENCES);
    createEAttribute(testDefEClass, TEST_DEF__ATTRIBUTE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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

    // Obtain other dependent packages
    Net4jUtilDefsPackage theNet4jUtilDefsPackage = (Net4jUtilDefsPackage)EPackage.Registry.INSTANCE
        .getEPackage(Net4jUtilDefsPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    testDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());

    // Initialize classes and features; add operations and parameters
    initEClass(testDefEClass, TestDef.class, "TestDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTestDef_References(), theNet4jUtilDefsPackage.getDef(), null, "references", null, 0, -1,
        TestDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTestDef_Attribute(), ecorePackage.getEString(), "attribute", null, 0, 1, TestDef.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // TestDefsPackageImpl
