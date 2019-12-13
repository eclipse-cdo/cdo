/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.subpackage.legacy;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * @extends org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model3.subpackage.legacy.SubpackageFactory
 * @model kind="package"
 * @generated
 */
public interface SubpackagePackage extends EPackage, org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "subpackage";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/legacy/subpackage/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "subpackage";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  SubpackagePackage eINSTANCE = org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.SubpackagePackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.Class2Impl <em>Class2</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.Class2Impl
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.SubpackagePackageImpl#getClass2()
   * @generated
   */
  int CLASS2 = 0;

  /**
   * The feature id for the '<em><b>Class1</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS2__CLASS1 = 0;

  /**
   * The number of structural features of the '<em>Class2</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS2_FEATURE_COUNT = 1;

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.subpackage.Class2 <em>Class2</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Class2</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.Class2
   * @generated
   */
  EClass getClass2();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.subpackage.Class2#getClass1 <em>Class1</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Class1</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.Class2#getClass1()
   * @see #getClass2()
   * @generated
   */
  EReference getClass2_Class1();

  @Override
  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  SubpackageFactory getSubpackageFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.Class2Impl <em>Class2</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.Class2Impl
     * @see org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.SubpackagePackageImpl#getClass2()
     * @generated
     */
    EClass CLASS2 = eINSTANCE.getClass2();

    /**
     * The meta object literal for the '<em><b>Class1</b></em>' reference list feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference CLASS2__CLASS1 = eINSTANCE.getClass2_Class1();

  }

} // SubpackagePackage
