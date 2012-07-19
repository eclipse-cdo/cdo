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
package org.eclipse.net4j.util.tests.defs;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;

import org.eclipse.emf.ecore.EAttribute;
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
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.util.tests.defs.DefsFactory
 * @model kind="package"
 * @generated
 */
public interface DefsPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "defs"; //$NON-NLS-1$

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/NET4J/defs/tests/1.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "net4j.defs.tests"; //$NON-NLS-1$

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  DefsPackage eINSTANCE = org.eclipse.net4j.util.tests.defs.impl.DefsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.tests.defs.impl.TestDefImpl <em>Test Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.tests.defs.impl.TestDefImpl
   * @see org.eclipse.net4j.util.tests.defs.impl.DefsPackageImpl#getTestDef()
   * @generated
   */
  int TEST_DEF = 0;

  /**
   * The feature id for the '<em><b>References</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_DEF__REFERENCES = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_DEF__ATTRIBUTE = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Test Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 2;

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.tests.defs.TestDef <em>Test Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Test Def</em>'.
   * @see org.eclipse.net4j.util.tests.defs.TestDef
   * @generated
   */
  EClass getTestDef();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.net4j.util.tests.defs.TestDef#getReferences
   * <em>References</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>References</em>'.
   * @see org.eclipse.net4j.util.tests.defs.TestDef#getReferences()
   * @see #getTestDef()
   * @generated
   */
  EReference getTestDef_References();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.tests.defs.TestDef#getAttribute
   * <em>Attribute</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attribute</em>'.
   * @see org.eclipse.net4j.util.tests.defs.TestDef#getAttribute()
   * @see #getTestDef()
   * @generated
   */
  EAttribute getTestDef_Attribute();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DefsFactory getDefsFactory();

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
     * The meta object literal for the '{@link org.eclipse.net4j.util.tests.defs.impl.TestDefImpl <em>Test Def</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.tests.defs.impl.TestDefImpl
     * @see org.eclipse.net4j.util.tests.defs.impl.DefsPackageImpl#getTestDef()
     * @generated
     */
    EClass TEST_DEF = eINSTANCE.getTestDef();

    /**
     * The meta object literal for the '<em><b>References</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference TEST_DEF__REFERENCES = eINSTANCE.getTestDef_References();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute TEST_DEF__ATTRIBUTE = eINSTANCE.getTestDef_Attribute();

  }

} // DefsPackage
