/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.preferences;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.preferences.PreferencesFactory
 * @model kind="package"
 * @generated
 */
public interface PreferencesPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "preferences";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/CDO/releng/preferences/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "preferences";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PreferencesPackage eINSTANCE = org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceNodeImpl <em>Preference Node</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferenceNodeImpl
   * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl#getPreferenceNode()
   * @generated
   */
  int PREFERENCE_NODE = 0;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE__CHILDREN = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE__PARENT = 1;

  /**
   * The feature id for the '<em><b>Properties</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE__PROPERTIES = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE__NAME = 3;

  /**
   * The feature id for the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE__LOCATION = 4;

  /**
   * The number of structural features of the '<em>Preference Node</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE_FEATURE_COUNT = 5;

  /**
   * The operation id for the '<em>Get Node</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE___GET_NODE__STRING = 0;

  /**
   * The operation id for the '<em>Get Property</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE___GET_PROPERTY__STRING = 1;

  /**
   * The number of operations of the '<em>Preference Node</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_NODE_OPERATION_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.preferences.impl.PropertyImpl <em>Property</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.preferences.impl.PropertyImpl
   * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl#getProperty()
   * @generated
   */
  int PROPERTY = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY__NAME = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY__PARENT = 1;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY__VALUE = 2;

  /**
   * The number of structural features of the '<em>Property</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FEATURE_COUNT = 3;

  /**
   * The number of operations of the '<em>Property</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '<em>Escaped String</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.String
   * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl#getEscapedString()
   * @generated
   */
  int ESCAPED_STRING = 2;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode <em>Preference Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Preference Node</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode
   * @generated
   */
  EClass getPreferenceNode();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getChildren()
   * @see #getPreferenceNode()
   * @generated
   */
  EReference getPreferenceNode_Children();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getParent()
   * @see #getPreferenceNode()
   * @generated
   */
  EReference getPreferenceNode_Parent();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getProperties <em>Properties</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Properties</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getProperties()
   * @see #getPreferenceNode()
   * @generated
   */
  EReference getPreferenceNode_Properties();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getName()
   * @see #getPreferenceNode()
   * @generated
   */
  EAttribute getPreferenceNode_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getLocation <em>Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Location</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getLocation()
   * @see #getPreferenceNode()
   * @generated
   */
  EAttribute getPreferenceNode_Location();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getNode(java.lang.String) <em>Get Node</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Node</em>' operation.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getNode(java.lang.String)
   * @generated
   */
  EOperation getPreferenceNode__GetNode__String();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getProperty(java.lang.String) <em>Get Property</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Property</em>' operation.
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getProperty(java.lang.String)
   * @generated
   */
  EOperation getPreferenceNode__GetProperty__String();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.preferences.Property <em>Property</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Property</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.Property
   * @generated
   */
  EClass getProperty();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.preferences.Property#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.Property#getName()
   * @see #getProperty()
   * @generated
   */
  EAttribute getProperty_Name();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.preferences.Property#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.Property#getParent()
   * @see #getProperty()
   * @generated
   */
  EReference getProperty_Parent();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.preferences.Property#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.preferences.Property#getValue()
   * @see #getProperty()
   * @generated
   */
  EAttribute getProperty_Value();

  /**
   * Returns the meta object for data type '{@link java.lang.String <em>Escaped String</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Escaped String</em>'.
   * @see java.lang.String
   * @model instanceClass="java.lang.String"
   * @generated
   */
  EDataType getEscapedString();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  PreferencesFactory getPreferencesFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceNodeImpl <em>Preference Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferenceNodeImpl
     * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl#getPreferenceNode()
     * @generated
     */
    EClass PREFERENCE_NODE = eINSTANCE.getPreferenceNode();

    /**
     * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_NODE__CHILDREN = eINSTANCE.getPreferenceNode_Children();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_NODE__PARENT = eINSTANCE.getPreferenceNode_Parent();

    /**
     * The meta object literal for the '<em><b>Properties</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_NODE__PROPERTIES = eINSTANCE.getPreferenceNode_Properties();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCE_NODE__NAME = eINSTANCE.getPreferenceNode_Name();

    /**
     * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCE_NODE__LOCATION = eINSTANCE.getPreferenceNode_Location();

    /**
     * The meta object literal for the '<em><b>Get Node</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREFERENCE_NODE___GET_NODE__STRING = eINSTANCE.getPreferenceNode__GetNode__String();

    /**
     * The meta object literal for the '<em><b>Get Property</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREFERENCE_NODE___GET_PROPERTY__STRING = eINSTANCE.getPreferenceNode__GetProperty__String();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.preferences.impl.PropertyImpl <em>Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.preferences.impl.PropertyImpl
     * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl#getProperty()
     * @generated
     */
    EClass PROPERTY = eINSTANCE.getProperty();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTY__NAME = eINSTANCE.getProperty_Name();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTY__PARENT = eINSTANCE.getProperty_Parent();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTY__VALUE = eINSTANCE.getProperty_Value();

    /**
     * The meta object literal for the '<em>Escaped String</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.eclipse.emf.cdo.releng.preferences.impl.PreferencesPackageImpl#getEscapedString()
     * @generated
     */
    EDataType ESCAPED_STRING = eINSTANCE.getEscapedString();

  }

} // PreferencesPackage
