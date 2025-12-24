/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package interface_;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

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
 * @see interface_.InterfaceFactory
 * @model kind="package"
 * @generated
 */
public interface InterfacePackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "interface";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "uuid://interface";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "interface";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  InterfacePackage eINSTANCE = interface_.impl.InterfacePackageImpl.init();

  /**
   * The meta object id for the '{@link interface_.IInterface <em>IInterface</em>}' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see interface_.IInterface
   * @see interface_.impl.InterfacePackageImpl#getIInterface()
   * @generated
   */
  int IINTERFACE = 0;

  /**
   * The feature id for the '<em><b>Test</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int IINTERFACE__TEST = 0;

  /**
   * The number of structural features of the '<em>IInterface</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int IINTERFACE_FEATURE_COUNT = 1;

  /**
   * Returns the meta object for class '{@link interface_.IInterface <em>IInterface</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for class '<em>IInterface</em>'.
   * @see interface_.IInterface
   * @generated
   */
  EClass getIInterface();

  /**
   * Returns the meta object for the attribute '{@link interface_.IInterface#getTest <em>Test</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Test</em>'.
   * @see interface_.IInterface#getTest()
   * @see #getIInterface()
   * @generated
   */
  EAttribute getIInterface_Test();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  InterfaceFactory getInterfaceFactory();

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
     * The meta object literal for the '{@link interface_.IInterface <em>IInterface</em>}' class. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see interface_.IInterface
     * @see interface_.impl.InterfacePackageImpl#getIInterface()
     * @generated
     */
    EClass IINTERFACE = eINSTANCE.getIInterface();

    /**
     * The meta object literal for the '<em><b>Test</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute IINTERFACE__TEST = eINSTANCE.getIInterface_Test();

  }

} // InterfacePackage
