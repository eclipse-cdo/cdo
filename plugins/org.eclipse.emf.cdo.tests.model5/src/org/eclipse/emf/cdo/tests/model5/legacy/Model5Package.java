/*
 * Copyright (c) 2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.legacy;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @extends org.eclipse.emf.cdo.tests.model5.Model5Package
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model5.legacy.Model5Factory
 * @model kind="package"
 * @generated
 */
public interface Model5Package extends EPackage, org.eclipse.emf.cdo.tests.model5.Model5Package
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model5";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/legacy/model5/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model5";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model5Package eINSTANCE = org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ManagerImpl <em>Manager</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.ManagerImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getManager()
   * @generated
   */
  int MANAGER = 0;

  /**
   * The number of structural features of the '<em>Manager</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MANAGER_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.DoctorImpl <em>Doctor</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.DoctorImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getDoctor()
   * @generated
   */
  int DOCTOR = 1;

  /**
   * The number of structural features of the '<em>Doctor</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOCTOR_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfStringImpl <em>Gen List Of String</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfStringImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfString()
   * @generated
   */
  int GEN_LIST_OF_STRING = 2;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_STRING__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of String</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_STRING_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfIntImpl <em>Gen List Of Int</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfIntImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfInt()
   * @generated
   */
  int GEN_LIST_OF_INT = 3;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Int</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfIntegerImpl <em>Gen List Of Integer</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfIntegerImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfInteger()
   * @generated
   */
  int GEN_LIST_OF_INTEGER = 4;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INTEGER__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Integer</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INTEGER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfLongImpl <em>Gen List Of Long</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfLongImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfLong()
   * @generated
   */
  int GEN_LIST_OF_LONG = 5;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_LONG__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Long</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_LONG_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfBooleanImpl <em>Gen List Of Boolean</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfBooleanImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfBoolean()
   * @generated
   */
  int GEN_LIST_OF_BOOLEAN = 6;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_BOOLEAN__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Boolean</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_BOOLEAN_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfShortImpl <em>Gen List Of Short</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfShortImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfShort()
   * @generated
   */
  int GEN_LIST_OF_SHORT = 7;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_SHORT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Short</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_SHORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfFloatImpl <em>Gen List Of Float</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfFloatImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfFloat()
   * @generated
   */
  int GEN_LIST_OF_FLOAT = 8;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_FLOAT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Float</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_FLOAT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfDoubleImpl <em>Gen List Of Double</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfDoubleImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfDouble()
   * @generated
   */
  int GEN_LIST_OF_DOUBLE = 9;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DOUBLE__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Double</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DOUBLE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfDateImpl <em>Gen List Of Date</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfDateImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfDate()
   * @generated
   */
  int GEN_LIST_OF_DATE = 10;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DATE__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Date</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DATE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfCharImpl <em>Gen List Of Char</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfCharImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfChar()
   * @generated
   */
  int GEN_LIST_OF_CHAR = 11;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_CHAR__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Char</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_CHAR_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfIntArrayImpl <em>Gen List Of Int Array</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.GenListOfIntArrayImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getGenListOfIntArray()
   * @generated
   */
  int GEN_LIST_OF_INT_ARRAY = 12;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_ARRAY__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Int Array</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_ARRAY_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ParentImpl <em>Parent</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.ParentImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getParent()
   * @generated
   */
  int PARENT = 13;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARENT__CHILDREN = 0;

  /**
   * The feature id for the '<em><b>Favourite</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARENT__FAVOURITE = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARENT__NAME = 2;

  /**
   * The number of structural features of the '<em>Parent</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARENT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ChildImpl <em>Child</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.ChildImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getChild()
   * @generated
   */
  int CHILD = 14;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHILD__PARENT = 0;

  /**
   * The feature id for the '<em><b>Preferred By</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHILD__PREFERRED_BY = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHILD__NAME = 2;

  /**
   * The number of structural features of the '<em>Child</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHILD_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.WithCustomTypeImpl <em>With Custom Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.WithCustomTypeImpl
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getWithCustomType()
   * @generated
   */
  int WITH_CUSTOM_TYPE = 15;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WITH_CUSTOM_TYPE__VALUE = 0;

  /**
   * The number of structural features of the '<em>With Custom Type</em>' class.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WITH_CUSTOM_TYPE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '<em>Int Array</em>' data type.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getIntArray()
   * @generated
   */
  int INT_ARRAY = 16;

  /**
   * The meta object id for the '<em>Custom Type</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model5.CustomType
   * @see org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5PackageImpl#getCustomType()
   * @generated
   */
  int CUSTOM_TYPE = 17;

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model5.Manager <em>Manager</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Manager</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model5.Manager
   * @generated
   */
  EClass getManager();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model5.Doctor <em>Doctor</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Doctor</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model5.Doctor
   * @generated
   */
  EClass getDoctor();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfString <em>Gen List Of String</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of String</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfString
   * @generated
   */
  EClass getGenListOfString();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfString#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfString#getElements()
   * @see #getGenListOfString()
   * @generated
   */
  EAttribute getGenListOfString_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInt <em>Gen List Of Int</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInt
   * @generated
   */
  EClass getGenListOfInt();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInt#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInt#getElements()
   * @see #getGenListOfInt()
   * @generated
   */
  EAttribute getGenListOfInt_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInteger <em>Gen List Of Integer</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Integer</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInteger
   * @generated
   */
  EClass getGenListOfInteger();

  @Override
  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.legacy.model5.GenListOfInteger#getElements <em>Elements</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model5.GenListOfInteger#getElements()
   * @see #getGenListOfInteger()
   * @generated
   */
  EAttribute getGenListOfInteger_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfLong <em>Gen List Of Long</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfLong
   * @generated
   */
  EClass getGenListOfLong();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfLong#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfLong#getElements()
   * @see #getGenListOfLong()
   * @generated
   */
  EAttribute getGenListOfLong_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfBoolean <em>Gen List Of Boolean</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfBoolean
   * @generated
   */
  EClass getGenListOfBoolean();

  @Override
  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.legacy.model5.GenListOfBoolean#getElements <em>Elements</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model5.GenListOfBoolean#getElements()
   * @see #getGenListOfBoolean()
   * @generated
   */
  EAttribute getGenListOfBoolean_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfShort <em>Gen List Of Short</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfShort
   * @generated
   */
  EClass getGenListOfShort();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfShort#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfShort#getElements()
   * @see #getGenListOfShort()
   * @generated
   */
  EAttribute getGenListOfShort_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfFloat <em>Gen List Of Float</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfFloat
   * @generated
   */
  EClass getGenListOfFloat();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfFloat#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfFloat#getElements()
   * @see #getGenListOfFloat()
   * @generated
   */
  EAttribute getGenListOfFloat_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDouble <em>Gen List Of Double</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDouble
   * @generated
   */
  EClass getGenListOfDouble();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDouble#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDouble#getElements()
   * @see #getGenListOfDouble()
   * @generated
   */
  EAttribute getGenListOfDouble_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDate <em>Gen List Of Date</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDate
   * @generated
   */
  EClass getGenListOfDate();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDate#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDate#getElements()
   * @see #getGenListOfDate()
   * @generated
   */
  EAttribute getGenListOfDate_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfChar <em>Gen List Of Char</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfChar
   * @generated
   */
  EClass getGenListOfChar();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfChar#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfChar#getElements()
   * @see #getGenListOfChar()
   * @generated
   */
  EAttribute getGenListOfChar_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfIntArray <em>Gen List Of Int Array</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Gen List Of Int Array</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfIntArray
   * @generated
   */
  EClass getGenListOfIntArray();

  @Override
  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.legacy.model5.GenListOfIntArray#getElements <em>Elements</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model5.GenListOfIntArray#getElements()
   * @see #getGenListOfIntArray()
   * @generated
   */
  EAttribute getGenListOfIntArray_Elements();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.Parent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Parent
   * @generated
   */
  EClass getParent();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model5.Parent#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Parent#getChildren()
   * @see #getParent()
   * @generated
   */
  EReference getParent_Children();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model5.Parent#getFavourite <em>Favourite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Favourite</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Parent#getFavourite()
   * @see #getParent()
   * @generated
   */
  EReference getParent_Favourite();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model5.Parent#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Parent#getName()
   * @see #getParent()
   * @generated
   */
  EAttribute getParent_Name();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.Child <em>Child</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Child</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Child
   * @generated
   */
  EClass getChild();

  @Override
  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model5.Child#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Child#getParent()
   * @see #getChild()
   * @generated
   */
  EReference getChild_Parent();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model5.Child#getPreferredBy <em>Preferred By</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Preferred By</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Child#getPreferredBy()
   * @see #getChild()
   * @generated
   */
  EReference getChild_PreferredBy();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model5.Child#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Child#getName()
   * @see #getChild()
   * @generated
   */
  EAttribute getChild_Name();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.WithCustomType <em>With Custom Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>With Custom Type</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.WithCustomType
   * @generated
   */
  EClass getWithCustomType();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model5.WithCustomType#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.WithCustomType#getValue()
   * @see #getWithCustomType()
   * @generated
   */
  EAttribute getWithCustomType_Value();

  @Override
  /**
   * Returns the meta object for data type '<em>Int Array</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for data type '<em>Int Array</em>'.
   * @model instanceClass="int[]"
   * @generated
   */
  EDataType getIntArray();

  @Override
  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.tests.model5.CustomType <em>Custom Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Custom Type</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.CustomType
   * @model instanceClass="org.eclipse.emf.cdo.tests.model5.CustomType"
   * @generated
   */
  EDataType getCustomType();

  @Override
  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model5Factory getModel5Factory();

} // Model5Package
