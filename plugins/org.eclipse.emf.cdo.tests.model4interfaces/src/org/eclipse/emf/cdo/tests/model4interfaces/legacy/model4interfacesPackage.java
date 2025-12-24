/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4interfaces.legacy;

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
 * @extends org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesFactory
 * @model kind="package"
 * @generated
 */
public interface model4interfacesPackage extends EPackage, org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model4interfaces";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/legacy/model4interfaces/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model4interfaces";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  model4interfacesPackage eINSTANCE = org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer <em>ISingle Ref Container</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getISingleRefContainer()
   * @generated
   */
  int ISINGLE_REF_CONTAINER = 0;

  /**
   * The feature id for the '<em><b>Element</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int ISINGLE_REF_CONTAINER__ELEMENT = 0;

  /**
   * The number of structural features of the '<em>ISingle Ref Container</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement <em>ISingle Ref Contained Element</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getISingleRefContainedElement()
   * @generated
   */
  int ISINGLE_REF_CONTAINED_ELEMENT = 1;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_CONTAINED_ELEMENT__PARENT = 0;

  /**
   * The number of structural features of the '<em>ISingle Ref Contained Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_CONTAINED_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer <em>ISingle Ref Non Container</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getISingleRefNonContainer()
   * @generated
   */
  int ISINGLE_REF_NON_CONTAINER = 2;

  /**
   * The feature id for the '<em><b>Element</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_NON_CONTAINER__ELEMENT = 0;

  /**
   * The number of structural features of the '<em>ISingle Ref Non Container</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_NON_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement <em>ISingle Ref Non Contained Element</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getISingleRefNonContainedElement()
   * @generated
   */
  int ISINGLE_REF_NON_CONTAINED_ELEMENT = 3;

  /**
   * The feature id for the '<em><b>Parent</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_NON_CONTAINED_ELEMENT__PARENT = 0;

  /**
   * The number of structural features of the '<em>ISingle Ref Non Contained Element</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_NON_CONTAINED_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer <em>IMulti Ref Container</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIMultiRefContainer()
   * @generated
   */
  int IMULTI_REF_CONTAINER = 4;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_CONTAINER__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>IMulti Ref Container</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement <em>IMulti Ref Contained Element</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIMultiRefContainedElement()
   * @generated
   */
  int IMULTI_REF_CONTAINED_ELEMENT = 5;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_CONTAINED_ELEMENT__PARENT = 0;

  /**
   * The number of structural features of the '<em>IMulti Ref Contained Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_CONTAINED_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer <em>IMulti Ref Non Container</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIMultiRefNonContainer()
   * @generated
   */
  int IMULTI_REF_NON_CONTAINER = 6;

  /**
   * The feature id for the '<em><b>Elements</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_NON_CONTAINER__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>IMulti Ref Non Container</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_NON_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement <em>IMulti Ref Non Contained Element</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIMultiRefNonContainedElement()
   * @generated
   */
  int IMULTI_REF_NON_CONTAINED_ELEMENT = 7;

  /**
   * The feature id for the '<em><b>Parent</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_NON_CONTAINED_ELEMENT__PARENT = 0;

  /**
   * The number of structural features of the '<em>IMulti Ref Non Contained Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_NON_CONTAINED_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.INamedElement <em>INamed Element</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.INamedElement
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getINamedElement()
   * @generated
   */
  int INAMED_ELEMENT = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INAMED_ELEMENT__NAME = 0;

  /**
   * The number of structural features of the '<em>INamed Element</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int INAMED_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink <em>IContained Element No Parent Link</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIContainedElementNoParentLink()
   * @generated
   */
  int ICONTAINED_ELEMENT_NO_PARENT_LINK = 9;

  /**
   * The number of structural features of the '<em>IContained Element No Parent Link</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ICONTAINED_ELEMENT_NO_PARENT_LINK_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL <em>ISingle Ref Container NPL</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getISingleRefContainerNPL()
   * @generated
   */
  int ISINGLE_REF_CONTAINER_NPL = 10;

  /**
   * The feature id for the '<em><b>Element</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int ISINGLE_REF_CONTAINER_NPL__ELEMENT = 0;

  /**
   * The number of structural features of the '<em>ISingle Ref Container NPL</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_CONTAINER_NPL_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL <em>ISingle Ref Non Container NPL</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getISingleRefNonContainerNPL()
   * @generated
   */
  int ISINGLE_REF_NON_CONTAINER_NPL = 11;

  /**
   * The feature id for the '<em><b>Element</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_NON_CONTAINER_NPL__ELEMENT = 0;

  /**
   * The number of structural features of the '<em>ISingle Ref Non Container NPL</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ISINGLE_REF_NON_CONTAINER_NPL_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL <em>IMulti Ref Container NPL</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIMultiRefContainerNPL()
   * @generated
   */
  int IMULTI_REF_CONTAINER_NPL = 12;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_CONTAINER_NPL__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>IMulti Ref Container NPL</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_CONTAINER_NPL_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL <em>IMulti Ref Non Container NPL</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL
   * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesPackageImpl#getIMultiRefNonContainerNPL()
   * @generated
   */
  int IMULTI_REF_NON_CONTAINER_NPL = 13;

  /**
   * The feature id for the '<em><b>Elements</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_NON_CONTAINER_NPL__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>IMulti Ref Non Container NPL</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMULTI_REF_NON_CONTAINER_NPL_FEATURE_COUNT = 1;

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer <em>ISingle Ref Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>ISingle Ref Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer
   * @generated
   */
  EClass getISingleRefContainer();

  @Override
  /**
   * Returns the meta object for the containment reference '
   * {@link org.eclipse.emf.cdo.tests.legacy.model4interfaces.ISingleRefContainer#getElement <em>Element</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference '<em>Element</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.ISingleRefContainer#getElement()
   * @see #getISingleRefContainer()
   * @generated
   */
  EReference getISingleRefContainer_Element();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement <em>ISingle Ref Contained Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>ISingle Ref Contained Element</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement
   * @generated
   */
  EClass getISingleRefContainedElement();

  @Override
  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement#getParent()
   * @see #getISingleRefContainedElement()
   * @generated
   */
  EReference getISingleRefContainedElement_Parent();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer <em>ISingle Ref Non Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>ISingle Ref Non Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer
   * @generated
   */
  EClass getISingleRefNonContainer();

  @Override
  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.emf.cdo.tests.legacy.model4interfaces.ISingleRefNonContainer#getElement <em>Element</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Element</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.ISingleRefNonContainer#getElement()
   * @see #getISingleRefNonContainer()
   * @generated
   */
  EReference getISingleRefNonContainer_Element();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement <em>ISingle Ref Non Contained Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>ISingle Ref Non Contained Element</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement
   * @generated
   */
  EClass getISingleRefNonContainedElement();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement#getParent()
   * @see #getISingleRefNonContainedElement()
   * @generated
   */
  EReference getISingleRefNonContainedElement_Parent();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer <em>IMulti Ref Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IMulti Ref Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer
   * @generated
   */
  EClass getIMultiRefContainer();

  @Override
  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.tests.legacy.model4interfaces.IMultiRefContainer#getElements <em>Elements</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.IMultiRefContainer#getElements()
   * @see #getIMultiRefContainer()
   * @generated
   */
  EReference getIMultiRefContainer_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement <em>IMulti Ref Contained Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IMulti Ref Contained Element</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement
   * @generated
   */
  EClass getIMultiRefContainedElement();

  @Override
  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement#getParent()
   * @see #getIMultiRefContainedElement()
   * @generated
   */
  EReference getIMultiRefContainedElement_Parent();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer <em>IMulti Ref Non Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IMulti Ref Non Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer
   * @generated
   */
  EClass getIMultiRefNonContainer();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer#getElements()
   * @see #getIMultiRefNonContainer()
   * @generated
   */
  EReference getIMultiRefNonContainer_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement <em>IMulti Ref Non Contained Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IMulti Ref Non Contained Element</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement
   * @generated
   */
  EClass getIMultiRefNonContainedElement();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement#getParent()
   * @see #getIMultiRefNonContainedElement()
   * @generated
   */
  EReference getIMultiRefNonContainedElement_Parent();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.INamedElement <em>INamed Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>INamed Element</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.INamedElement
   * @generated
   */
  EClass getINamedElement();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model4interfaces.INamedElement#getName <em>Name</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.INamedElement#getName()
   * @see #getINamedElement()
   * @generated
   */
  EAttribute getINamedElement_Name();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink <em>IContained Element No Parent Link</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IContained Element No Parent Link</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink
   * @generated
   */
  EClass getIContainedElementNoParentLink();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL <em>ISingle Ref Container NPL</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>ISingle Ref Container NPL</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL
   * @generated
   */
  EClass getISingleRefContainerNPL();

  @Override
  /**
   * Returns the meta object for the containment reference '
   * {@link org.eclipse.emf.cdo.tests.legacy.model4interfaces.ISingleRefContainerNPL#getElement <em>Element</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference '<em>Element</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.ISingleRefContainerNPL#getElement()
   * @see #getISingleRefContainerNPL()
   * @generated
   */
  EReference getISingleRefContainerNPL_Element();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL <em>ISingle Ref Non Container NPL</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>ISingle Ref Non Container NPL</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL
   * @generated
   */
  EClass getISingleRefNonContainerNPL();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL#getElement <em>Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Element</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL#getElement()
   * @see #getISingleRefNonContainerNPL()
   * @generated
   */
  EReference getISingleRefNonContainerNPL_Element();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL <em>IMulti Ref Container NPL</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IMulti Ref Container NPL</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL
   * @generated
   */
  EClass getIMultiRefContainerNPL();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL#getElements()
   * @see #getIMultiRefContainerNPL()
   * @generated
   */
  EReference getIMultiRefContainerNPL_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL <em>IMulti Ref Non Container NPL</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>IMulti Ref Non Container NPL</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL
   * @generated
   */
  EClass getIMultiRefNonContainerNPL();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL#getElements()
   * @see #getIMultiRefNonContainerNPL()
   * @generated
   */
  EReference getIMultiRefNonContainerNPL_Elements();

  @Override
  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  model4interfacesFactory getmodel4interfacesFactory();

} // model4interfacesPackage
