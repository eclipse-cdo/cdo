/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package base;

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
 * @see base.BaseFactory
 * @model kind="package"
 * @generated
 */
public interface BasePackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "base";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.fernuni-hagen.de/ST/dummy/base.ecore";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "base";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  BasePackage eINSTANCE = base.impl.BasePackageImpl.init();

  /**
   * The meta object id for the '{@link base.impl.BaseClassImpl <em>Class</em>}' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see base.impl.BaseClassImpl
   * @see base.impl.BasePackageImpl#getBaseClass()
   * @generated
   */
  int BASE_CLASS = 0;

  /**
   * The feature id for the '<em><b>Couter</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BASE_CLASS__COUTER = 0;

  /**
   * The number of structural features of the '<em>Class</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BASE_CLASS_FEATURE_COUNT = 1;

  /**
   * Returns the meta object for class '{@link base.BaseClass <em>Class</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for class '<em>Class</em>'.
   * @see base.BaseClass
   * @generated
   */
  EClass getBaseClass();

  /**
   * Returns the meta object for the attribute '{@link base.BaseClass#getCouter <em>Couter</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Couter</em>'.
   * @see base.BaseClass#getCouter()
   * @see #getBaseClass()
   * @generated
   */
  EAttribute getBaseClass_Couter();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  BaseFactory getBaseFactory();

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
     * The meta object literal for the '{@link base.impl.BaseClassImpl <em>Class</em>}' class. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see base.impl.BaseClassImpl
     * @see base.impl.BasePackageImpl#getBaseClass()
     * @generated
     */
    EClass BASE_CLASS = eINSTANCE.getBaseClass();

    /**
     * The meta object literal for the '<em><b>Couter</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BASE_CLASS__COUTER = eINSTANCE.getBaseClass_Couter();

  }

} // BasePackage
