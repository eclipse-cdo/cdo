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
package base;

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
 * @see base.BaseFactory
 * @model kind="package"
 * @generated
 */
public interface BasePackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "base";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.fernuni-hagen.de/ST/dummy/base.ecore";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "base";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  BasePackage eINSTANCE = base.impl.BasePackageImpl.init();

  /**
   * The meta object id for the '{@link base.impl.BaseClassImpl <em>Class</em>}' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see base.impl.BaseClassImpl
   * @see base.impl.BasePackageImpl#getBaseClass()
   * @generated
   */
  int BASE_CLASS = 0;

  /**
   * The feature id for the '<em><b>Couter</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASE_CLASS__COUTER = 0;

  /**
   * The number of structural features of the '<em>Class</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASE_CLASS_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link base.impl.DocumentImpl <em>Document</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see base.impl.DocumentImpl
   * @see base.impl.BasePackageImpl#getDocument()
   * @generated
   */
  int DOCUMENT = 1;

  /**
  	 * The feature id for the '<em><b>Root</b></em>' containment reference.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 * @ordered
  	 */
  int DOCUMENT__ROOT = 0;

  /**
  	 * The number of structural features of the '<em>Document</em>' class.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 * @ordered
  	 */
  int DOCUMENT_FEATURE_COUNT = 1;

  /**
  	 * The meta object id for the '{@link base.impl.ElementImpl <em>Element</em>}' class.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @see base.impl.ElementImpl
  	 * @see base.impl.BasePackageImpl#getElement()
  	 * @generated
  	 */
  int ELEMENT = 2;

  /**
  	 * The feature id for the '<em><b>Subelements</b></em>' containment reference list.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 * @ordered
  	 */
  int ELEMENT__SUBELEMENTS = 0;

  /**
  	 * The feature id for the '<em><b>Parent</b></em>' container reference.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 * @ordered
  	 */
  int ELEMENT__PARENT = 1;

  /**
  	 * The number of structural features of the '<em>Element</em>' class.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 * @ordered
  	 */
  int ELEMENT_FEATURE_COUNT = 2;

  /**
  	 * Returns the meta object for class '{@link base.BaseClass <em>Class</em>}'.
  	 * <!-- begin-user-doc --> <!--
   * end-user-doc -->
  	 * @return the meta object for class '<em>Class</em>'.
  	 * @see base.BaseClass
  	 * @generated
  	 */
  EClass getBaseClass();

  /**
   * Returns the meta object for the attribute '{@link base.BaseClass#getCouter <em>Couter</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Couter</em>'.
   * @see base.BaseClass#getCouter()
   * @see #getBaseClass()
   * @generated
   */
  EAttribute getBaseClass_Couter();

  /**
   * Returns the meta object for class '{@link base.Document <em>Document</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Document</em>'.
   * @see base.Document
   * @generated
   */
  EClass getDocument();

  /**
  	 * Returns the meta object for the containment reference '{@link base.Document#getRoot <em>Root</em>}'.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @return the meta object for the containment reference '<em>Root</em>'.
  	 * @see base.Document#getRoot()
  	 * @see #getDocument()
  	 * @generated
  	 */
  EReference getDocument_Root();

  /**
  	 * Returns the meta object for class '{@link base.Element <em>Element</em>}'.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @return the meta object for class '<em>Element</em>'.
  	 * @see base.Element
  	 * @generated
  	 */
  EClass getElement();

  /**
  	 * Returns the meta object for the containment reference list '{@link base.Element#getSubelements <em>Subelements</em>}'.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @return the meta object for the containment reference list '<em>Subelements</em>'.
  	 * @see base.Element#getSubelements()
  	 * @see #getElement()
  	 * @generated
  	 */
  EReference getElement_Subelements();

  /**
  	 * Returns the meta object for the container reference '{@link base.Element#getParent <em>Parent</em>}'.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @return the meta object for the container reference '<em>Parent</em>'.
  	 * @see base.Element#getParent()
  	 * @see #getElement()
  	 * @generated
  	 */
  EReference getElement_Parent();

  /**
  	 * Returns the factory that creates the instances of the model.
  	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link base.impl.BaseClassImpl <em>Class</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see base.impl.BaseClassImpl
     * @see base.impl.BasePackageImpl#getBaseClass()
     * @generated
     */
    EClass BASE_CLASS = eINSTANCE.getBaseClass();

    /**
     * The meta object literal for the '<em><b>Couter</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute BASE_CLASS__COUTER = eINSTANCE.getBaseClass_Couter();

    /**
    	 * The meta object literal for the '{@link base.impl.DocumentImpl <em>Document</em>}' class.
    	 * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
    	 * @see base.impl.DocumentImpl
    	 * @see base.impl.BasePackageImpl#getDocument()
    	 * @generated
    	 */
    EClass DOCUMENT = eINSTANCE.getDocument();

    /**
    	 * The meta object literal for the '<em><b>Root</b></em>' containment reference feature.
    	 * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
    	 * @generated
    	 */
    EReference DOCUMENT__ROOT = eINSTANCE.getDocument_Root();

    /**
    	 * The meta object literal for the '{@link base.impl.ElementImpl <em>Element</em>}' class.
    	 * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
    	 * @see base.impl.ElementImpl
    	 * @see base.impl.BasePackageImpl#getElement()
    	 * @generated
    	 */
    EClass ELEMENT = eINSTANCE.getElement();

    /**
    	 * The meta object literal for the '<em><b>Subelements</b></em>' containment reference list feature.
    	 * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
    	 * @generated
    	 */
    EReference ELEMENT__SUBELEMENTS = eINSTANCE.getElement_Subelements();

    /**
    	 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
    	 * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
    	 * @generated
    	 */
    EReference ELEMENT__PARENT = eINSTANCE.getElement_Parent();

  }

} // BasePackage
