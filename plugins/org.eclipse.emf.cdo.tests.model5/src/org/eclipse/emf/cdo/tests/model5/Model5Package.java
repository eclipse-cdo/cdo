/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5;

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
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model5.Model5Factory
 * @model kind="package"
 * @generated
 */
public interface Model5Package extends EPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model5";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model5/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "model5";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model5Package eINSTANCE = org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl
   * <em>Test Feature Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getTestFeatureMap()
   * @generated
   */
  int TEST_FEATURE_MAP = 0;

  /**
   * The feature id for the '<em><b>Managers</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP__MANAGERS = 0;

  /**
   * The feature id for the '<em><b>Doctors</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP__DOCTORS = 1;

  /**
   * The feature id for the '<em><b>People</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP__PEOPLE = 2;

  /**
   * The number of structural features of the '<em>Test Feature Map</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl <em>Manager</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getManager()
   * @generated
   */
  int MANAGER = 1;

  /**
   * The number of structural features of the '<em>Manager</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MANAGER_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl <em>Doctor</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getDoctor()
   * @generated
   */
  int DOCTOR = 2;

  /**
   * The number of structural features of the '<em>Doctor</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DOCTOR_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl <em>Gen List Of Int</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfInt()
   * @generated
   */
  int GEN_LIST_OF_INT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntegerImpl
   * <em>Gen List Of Integer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntegerImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfInteger()
   * @generated
   */
  int GEN_LIST_OF_INTEGER = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfLongImpl
   * <em>Gen List Of Long</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfLongImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfLong()
   * @generated
   */
  int GEN_LIST_OF_LONG = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfBooleanImpl
   * <em>Gen List Of Boolean</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfBooleanImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfBoolean()
   * @generated
   */
  int GEN_LIST_OF_BOOLEAN = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl
   * <em>Gen List Of String</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfString()
   * @generated
   */
  int GEN_LIST_OF_STRING = 3;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_STRING__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of String</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_STRING_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Int</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INTEGER__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Integer</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INTEGER_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_LONG__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Long</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_LONG_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_BOOLEAN__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Boolean</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_BOOLEAN_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfShortImpl
   * <em>Gen List Of Short</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfShortImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfShort()
   * @generated
   */
  int GEN_LIST_OF_SHORT = 8;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_SHORT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Short</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_SHORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfFloatImpl
   * <em>Gen List Of Float</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfFloatImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfFloat()
   * @generated
   */
  int GEN_LIST_OF_FLOAT = 9;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_FLOAT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Float</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_FLOAT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfDoubleImpl
   * <em>Gen List Of Double</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfDoubleImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfDouble()
   * @generated
   */
  int GEN_LIST_OF_DOUBLE = 10;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DOUBLE__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Double</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DOUBLE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfDateImpl
   * <em>Gen List Of Date</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfDateImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfDate()
   * @generated
   */
  int GEN_LIST_OF_DATE = 11;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DATE__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Date</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_DATE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfCharImpl
   * <em>Gen List Of Char</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfCharImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfChar()
   * @generated
   */
  int GEN_LIST_OF_CHAR = 12;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_CHAR__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Char</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_CHAR_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntArrayImpl
   * <em>Gen List Of Int Array</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntArrayImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfIntArray()
   * @generated
   */
  int GEN_LIST_OF_INT_ARRAY = 13;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_ARRAY__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Int Array</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_ARRAY_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '<em>Int Array</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getIntArray()
   * @generated
   */
  int INT_ARRAY = 14;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap
   * <em>Test Feature Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Test Feature Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap
   * @generated
   */
  EClass getTestFeatureMap();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getManagers
   * <em>Managers</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Managers</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getManagers()
   * @see #getTestFeatureMap()
   * @generated
   */
  EReference getTestFeatureMap_Managers();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getDoctors
   * <em>Doctors</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Doctors</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getDoctors()
   * @see #getTestFeatureMap()
   * @generated
   */
  EReference getTestFeatureMap_Doctors();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getPeople
   * <em>People</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>People</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getPeople()
   * @see #getTestFeatureMap()
   * @generated
   */
  EAttribute getTestFeatureMap_People();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.Manager <em>Manager</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Manager</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Manager
   * @generated
   */
  EClass getManager();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.Doctor <em>Doctor</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Doctor</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Doctor
   * @generated
   */
  EClass getDoctor();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInt <em>Gen List Of Int</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInt
   * @generated
   */
  EClass getGenListOfInt();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInt#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInt#getElements()
   * @see #getGenListOfInt()
   * @generated
   */
  EAttribute getGenListOfInt_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInteger
   * <em>Gen List Of Integer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Integer</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInteger
   * @generated
   */
  EClass getGenListOfInteger();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model5.GenListOfInteger#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInteger#getElements()
   * @see #getGenListOfInteger()
   * @generated
   */
  EAttribute getGenListOfInteger_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfLong <em>Gen List Of Long</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfLong
   * @generated
   */
  EClass getGenListOfLong();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfLong#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfLong#getElements()
   * @see #getGenListOfLong()
   * @generated
   */
  EAttribute getGenListOfLong_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfBoolean
   * <em>Gen List Of Boolean</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfBoolean
   * @generated
   */
  EClass getGenListOfBoolean();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model5.GenListOfBoolean#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfBoolean#getElements()
   * @see #getGenListOfBoolean()
   * @generated
   */
  EAttribute getGenListOfBoolean_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfShort
   * <em>Gen List Of Short</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfShort
   * @generated
   */
  EClass getGenListOfShort();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfShort#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfShort#getElements()
   * @see #getGenListOfShort()
   * @generated
   */
  EAttribute getGenListOfShort_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfFloat
   * <em>Gen List Of Float</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfFloat
   * @generated
   */
  EClass getGenListOfFloat();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfFloat#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfFloat#getElements()
   * @see #getGenListOfFloat()
   * @generated
   */
  EAttribute getGenListOfFloat_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDouble
   * <em>Gen List Of Double</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDouble
   * @generated
   */
  EClass getGenListOfDouble();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model5.GenListOfDouble#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDouble#getElements()
   * @see #getGenListOfDouble()
   * @generated
   */
  EAttribute getGenListOfDouble_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDate <em>Gen List Of Date</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDate
   * @generated
   */
  EClass getGenListOfDate();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfDate#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfDate#getElements()
   * @see #getGenListOfDate()
   * @generated
   */
  EAttribute getGenListOfDate_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfChar <em>Gen List Of Char</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfChar
   * @generated
   */
  EClass getGenListOfChar();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfChar#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfChar#getElements()
   * @see #getGenListOfChar()
   * @generated
   */
  EAttribute getGenListOfChar_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfIntArray
   * <em>Gen List Of Int Array</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Int Array</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfIntArray
   * @generated
   */
  EClass getGenListOfIntArray();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model5.GenListOfIntArray#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfIntArray#getElements()
   * @see #getGenListOfIntArray()
   * @generated
   */
  EAttribute getGenListOfIntArray_Elements();

  /**
   * Returns the meta object for data type '<em>Int Array</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Int Array</em>'.
   * @model instanceClass="int[]"
   * @generated
   */
  EDataType getIntArray();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfString
   * <em>Gen List Of String</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of String</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfString
   * @generated
   */
  EClass getGenListOfString();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model5.GenListOfString#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfString#getElements()
   * @see #getGenListOfString()
   * @generated
   */
  EAttribute getGenListOfString_Elements();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model5Factory getModel5Factory();

} // Model5Package
