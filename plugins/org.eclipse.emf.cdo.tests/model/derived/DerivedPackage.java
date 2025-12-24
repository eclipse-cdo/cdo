/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package derived;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import base.BasePackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see derived.DerivedFactory
 * @model kind="package"
 * @generated
 */
public interface DerivedPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "derived";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "http://www.fernuni-hagen.de/ST/dummy/derived.ecore";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "derived";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  DerivedPackage eINSTANCE = derived.impl.DerivedPackageImpl.init();

  /**
   * The meta object id for the '{@link derived.impl.DerivedClassImpl <em>Class</em>}' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @see derived.impl.DerivedClassImpl
   * @see derived.impl.DerivedPackageImpl#getDerivedClass()
   * @generated
   */
  int DERIVED_CLASS = 0;

  /**
   * The feature id for the '<em><b>Couter</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DERIVED_CLASS__COUTER = BasePackage.BASE_CLASS__COUTER;

  /**
   * The number of structural features of the '<em>Class</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DERIVED_CLASS_FEATURE_COUNT = BasePackage.BASE_CLASS_FEATURE_COUNT + 0;

  /**
   * Returns the meta object for class '{@link derived.DerivedClass <em>Class</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for class '<em>Class</em>'.
   * @see derived.DerivedClass
   * @generated
   */
  EClass getDerivedClass();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DerivedFactory getDerivedFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   *
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link derived.impl.DerivedClassImpl <em>Class</em>}' class. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see derived.impl.DerivedClassImpl
     * @see derived.impl.DerivedPackageImpl#getDerivedClass()
     * @generated
     */
    EClass DERIVED_CLASS = eINSTANCE.getDerivedClass();

  }

} // DerivedPackage
