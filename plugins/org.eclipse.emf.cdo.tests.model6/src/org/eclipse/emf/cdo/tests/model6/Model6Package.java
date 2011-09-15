/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

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
 * @see org.eclipse.emf.cdo.tests.model6.Model6Factory
 * @model kind="package"
 * @generated
 */
public interface Model6Package extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model6";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model6/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "model6";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model6Package eINSTANCE = org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl <em>Root</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model6.impl.RootImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getRoot()
   * @generated
   */
  int ROOT = 0;

  /**
   * The feature id for the '<em><b>List A</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ROOT__LIST_A = 0;

  /**
   * The feature id for the '<em><b>List B</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ROOT__LIST_B = 1;

  /**
   * The feature id for the '<em><b>List C</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ROOT__LIST_C = 2;

  /**
   * The feature id for the '<em><b>List D</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ROOT__LIST_D = 3;

  /**
   * The number of structural features of the '<em>Root</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ROOT_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl <em>Base Object</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getBaseObject()
   * @generated
   */
  int BASE_OBJECT = 1;

  /**
   * The feature id for the '<em><b>Attribute Optional</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int BASE_OBJECT__ATTRIBUTE_OPTIONAL = 0;

  /**
   * The feature id for the '<em><b>Attribute Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int BASE_OBJECT__ATTRIBUTE_REQUIRED = 1;

  /**
   * The feature id for the '<em><b>Attribute List</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int BASE_OBJECT__ATTRIBUTE_LIST = 2;

  /**
   * The number of structural features of the '<em>Base Object</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int BASE_OBJECT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl
   * <em>Reference Object</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getReferenceObject()
   * @generated
   */
  int REFERENCE_OBJECT = 2;

  /**
   * The feature id for the '<em><b>Attribute Optional</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__ATTRIBUTE_OPTIONAL = BASE_OBJECT__ATTRIBUTE_OPTIONAL;

  /**
   * The feature id for the '<em><b>Attribute Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__ATTRIBUTE_REQUIRED = BASE_OBJECT__ATTRIBUTE_REQUIRED;

  /**
   * The feature id for the '<em><b>Attribute List</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__ATTRIBUTE_LIST = BASE_OBJECT__ATTRIBUTE_LIST;

  /**
   * The feature id for the '<em><b>Reference Optional</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__REFERENCE_OPTIONAL = BASE_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Reference List</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__REFERENCE_LIST = BASE_OBJECT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Reference Object</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT_FEATURE_COUNT = BASE_OBJECT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
   * <em>Containment Object</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getContainmentObject()
   * @generated
   */
  int CONTAINMENT_OBJECT = 3;

  /**
   * The feature id for the '<em><b>Attribute Optional</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__ATTRIBUTE_OPTIONAL = BASE_OBJECT__ATTRIBUTE_OPTIONAL;

  /**
   * The feature id for the '<em><b>Attribute Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__ATTRIBUTE_REQUIRED = BASE_OBJECT__ATTRIBUTE_REQUIRED;

  /**
   * The feature id for the '<em><b>Attribute List</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__ATTRIBUTE_LIST = BASE_OBJECT__ATTRIBUTE_LIST;

  /**
   * The feature id for the '<em><b>Containment Optional</b></em>' containment reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL = BASE_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Containment List</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__CONTAINMENT_LIST = BASE_OBJECT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Containment Object</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT_FEATURE_COUNT = BASE_OBJECT_FEATURE_COUNT + 2;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.Root <em>Root</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Root</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root
   * @generated
   */
  EClass getRoot();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListA
   * <em>List A</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>List A</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListA()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListA();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListB
   * <em>List B</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>List B</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListB()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListB();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListC
   * <em>List C</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>List C</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListC()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListC();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListD
   * <em>List D</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>List D</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListD()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListD();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.BaseObject <em>Base Object</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Base Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject
   * @generated
   */
  EClass getBaseObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeOptional
   * <em>Attribute Optional</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attribute Optional</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeOptional()
   * @see #getBaseObject()
   * @generated
   */
  EAttribute getBaseObject_AttributeOptional();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeRequired
   * <em>Attribute Required</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Attribute Required</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeRequired()
   * @see #getBaseObject()
   * @generated
   */
  EAttribute getBaseObject_AttributeRequired();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeList <em>Attribute List</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Attribute List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeList()
   * @see #getBaseObject()
   * @generated
   */
  EAttribute getBaseObject_AttributeList();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.ReferenceObject
   * <em>Reference Object</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Reference Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ReferenceObject
   * @generated
   */
  EClass getReferenceObject();

  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceOptional <em>Reference Optional</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Reference Optional</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceOptional()
   * @see #getReferenceObject()
   * @generated
   */
  EReference getReferenceObject_ReferenceOptional();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceList <em>Reference List</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Reference List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceList()
   * @see #getReferenceObject()
   * @generated
   */
  EReference getReferenceObject_ReferenceList();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.ContainmentObject
   * <em>Containment Object</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Containment Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ContainmentObject
   * @generated
   */
  EClass getContainmentObject();

  /**
   * Returns the meta object for the containment reference '
   * {@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentOptional <em>Containment Optional</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Containment Optional</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentOptional()
   * @see #getContainmentObject()
   * @generated
   */
  EReference getContainmentObject_ContainmentOptional();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentList <em>Containment List</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Containment List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentList()
   * @see #getContainmentObject()
   * @generated
   */
  EReference getContainmentObject_ContainmentList();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model6Factory getModel6Factory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl <em>Root</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.RootImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getRoot()
     * @generated
     */
    EClass ROOT = eINSTANCE.getRoot();

    /**
     * The meta object literal for the '<em><b>List A</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ROOT__LIST_A = eINSTANCE.getRoot_ListA();

    /**
     * The meta object literal for the '<em><b>List B</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ROOT__LIST_B = eINSTANCE.getRoot_ListB();

    /**
     * The meta object literal for the '<em><b>List C</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ROOT__LIST_C = eINSTANCE.getRoot_ListC();

    /**
     * The meta object literal for the '<em><b>List D</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ROOT__LIST_D = eINSTANCE.getRoot_ListD();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl
     * <em>Base Object</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getBaseObject()
     * @generated
     */
    EClass BASE_OBJECT = eINSTANCE.getBaseObject();

    /**
     * The meta object literal for the '<em><b>Attribute Optional</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute BASE_OBJECT__ATTRIBUTE_OPTIONAL = eINSTANCE.getBaseObject_AttributeOptional();

    /**
     * The meta object literal for the '<em><b>Attribute Required</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute BASE_OBJECT__ATTRIBUTE_REQUIRED = eINSTANCE.getBaseObject_AttributeRequired();

    /**
     * The meta object literal for the '<em><b>Attribute List</b></em>' attribute list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute BASE_OBJECT__ATTRIBUTE_LIST = eINSTANCE.getBaseObject_AttributeList();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl
     * <em>Reference Object</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getReferenceObject()
     * @generated
     */
    EClass REFERENCE_OBJECT = eINSTANCE.getReferenceObject();

    /**
     * The meta object literal for the '<em><b>Reference Optional</b></em>' reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference REFERENCE_OBJECT__REFERENCE_OPTIONAL = eINSTANCE.getReferenceObject_ReferenceOptional();

    /**
     * The meta object literal for the '<em><b>Reference List</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference REFERENCE_OBJECT__REFERENCE_LIST = eINSTANCE.getReferenceObject_ReferenceList();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
     * <em>Containment Object</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getContainmentObject()
     * @generated
     */
    EClass CONTAINMENT_OBJECT = eINSTANCE.getContainmentObject();

    /**
     * The meta object literal for the '<em><b>Containment Optional</b></em>' containment reference feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL = eINSTANCE.getContainmentObject_ContainmentOptional();

    /**
     * The meta object literal for the '<em><b>Containment List</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CONTAINMENT_OBJECT__CONTAINMENT_LIST = eINSTANCE.getContainmentObject_ContainmentList();

  }

} // Model6Package
