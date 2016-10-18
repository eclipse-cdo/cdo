/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory
 * @model kind="package"
 * @generated
 */
public interface HibernateTestPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "HibernateTest";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://org.eclipse.emf.cdo.tests.hibernate";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "hibernatetests";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  HibernateTestPackage eINSTANCE = org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl <em>Bz356181 Main</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Main()
   * @generated
   */
  int BZ356181_MAIN = 0;

  /**
   * The feature id for the '<em><b>Transient</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_MAIN__TRANSIENT = 0;

  /**
   * The feature id for the '<em><b>Non Transient</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_MAIN__NON_TRANSIENT = 1;

  /**
   * The feature id for the '<em><b>Transient Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_MAIN__TRANSIENT_REF = 2;

  /**
   * The feature id for the '<em><b>Transient Other Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_MAIN__TRANSIENT_OTHER_REF = 3;

  /**
   * The number of structural features of the '<em>Bz356181 Main</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_MAIN_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl <em>Bz356181 Transient</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Transient()
   * @generated
   */
  int BZ356181_TRANSIENT = 1;

  /**
   * The number of structural features of the '<em>Bz356181 Transient</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_TRANSIENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl <em>Bz356181 Non Transient</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_NonTransient()
   * @generated
   */
  int BZ356181_NON_TRANSIENT = 2;

  /**
   * The feature id for the '<em><b>Main</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_NON_TRANSIENT__MAIN = 0;

  /**
   * The number of structural features of the '<em>Bz356181 Non Transient</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ356181_NON_TRANSIENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl <em>Bz387752 Main</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz387752_Main()
   * @generated
   */
  int BZ387752_MAIN = 3;

  /**
   * The feature id for the '<em><b>Str Unsettable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ387752_MAIN__STR_UNSETTABLE = 0;

  /**
   * The feature id for the '<em><b>Str Settable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ387752_MAIN__STR_SETTABLE = 1;

  /**
   * The feature id for the '<em><b>Enum Settable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ387752_MAIN__ENUM_SETTABLE = 2;

  /**
   * The feature id for the '<em><b>Enum Unsettable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ387752_MAIN__ENUM_UNSETTABLE = 3;

  /**
   * The number of structural features of the '<em>Bz387752 Main</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ387752_MAIN_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_GroupImpl <em>Bz380987 Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_GroupImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz380987_Group()
   * @generated
   */
  int BZ380987_GROUP = 4;

  /**
   * The feature id for the '<em><b>People</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_GROUP__PEOPLE = 0;

  /**
   * The number of structural features of the '<em>Bz380987 Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_GROUP_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PlaceImpl <em>Bz380987 Place</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PlaceImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz380987_Place()
   * @generated
   */
  int BZ380987_PLACE = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PLACE__NAME = 0;

  /**
   * The feature id for the '<em><b>People</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PLACE__PEOPLE = 1;

  /**
   * The number of structural features of the '<em>Bz380987 Place</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PLACE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl <em>Bz380987 Person</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz380987_Person()
   * @generated
   */
  int BZ380987_PERSON = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PERSON__NAME = 0;

  /**
   * The feature id for the '<em><b>Group</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PERSON__GROUP = 1;

  /**
   * The feature id for the '<em><b>Places</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PERSON__PLACES = 2;

  /**
   * The number of structural features of the '<em>Bz380987 Person</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ380987_PERSON_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057AImpl <em>Bz398057 A</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057AImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057A()
   * @generated
   */
  int BZ398057_A = 7;

  /**
   * The feature id for the '<em><b>List Of B</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_A__LIST_OF_B = 0;

  /**
   * The feature id for the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_A__DB_ID = 1;

  /**
   * The number of structural features of the '<em>Bz398057 A</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_A_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057A1Impl <em>Bz398057 A1</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057A1Impl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057A1()
   * @generated
   */
  int BZ398057_A1 = 8;

  /**
   * The feature id for the '<em><b>List Of B</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_A1__LIST_OF_B = BZ398057_A__LIST_OF_B;

  /**
   * The feature id for the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_A1__DB_ID = BZ398057_A__DB_ID;

  /**
   * The number of structural features of the '<em>Bz398057 A1</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_A1_FEATURE_COUNT = BZ398057_A_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl <em>Bz398057 B</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057B()
   * @generated
   */
  int BZ398057_B = 9;

  /**
   * The feature id for the '<em><b>Ref To Class A</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B__REF_TO_CLASS_A = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B__VALUE = 1;

  /**
   * The feature id for the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B__DB_ID = 2;

  /**
   * The number of structural features of the '<em>Bz398057 B</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057B1Impl <em>Bz398057 B1</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057B1Impl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057B1()
   * @generated
   */
  int BZ398057_B1 = 10;

  /**
   * The feature id for the '<em><b>Ref To Class A</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B1__REF_TO_CLASS_A = BZ398057_B__REF_TO_CLASS_A;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B1__VALUE = BZ398057_B__VALUE;

  /**
   * The feature id for the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B1__DB_ID = BZ398057_B__DB_ID;

  /**
   * The feature id for the '<em><b>Value Str</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B1__VALUE_STR = BZ398057_B_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Bz398057 B1</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ398057_B1_FEATURE_COUNT = BZ398057_B_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682PImpl <em>Bz397682 P</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682PImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz397682P()
   * @generated
   */
  int BZ397682_P = 11;

  /**
   * The feature id for the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_P__DB_ID = 0;

  /**
   * The feature id for the '<em><b>List Of C</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_P__LIST_OF_C = 1;

  /**
   * The number of structural features of the '<em>Bz397682 P</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_P_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl <em>Bz397682 C</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz397682C()
   * @generated
   */
  int BZ397682_C = 12;

  /**
   * The feature id for the '<em><b>Ref To P</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_C__REF_TO_P = 0;

  /**
   * The feature id for the '<em><b>Ref To C</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_C__REF_TO_C = 1;

  /**
   * The feature id for the '<em><b>Db Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_C__DB_ID = 2;

  /**
   * The number of structural features of the '<em>Bz397682 C</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BZ397682_C_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum <em>Bz387752 Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz387752_Enum()
   * @generated
   */
  int BZ387752_ENUM = 13;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main <em>Bz356181 Main</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz356181 Main</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main
   * @generated
   */
  EClass getBz356181_Main();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransient <em>Transient</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Transient</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransient()
   * @see #getBz356181_Main()
   * @generated
   */
  EAttribute getBz356181_Main_Transient();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getNonTransient <em>Non Transient</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Non Transient</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getNonTransient()
   * @see #getBz356181_Main()
   * @generated
   */
  EAttribute getBz356181_Main_NonTransient();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientRef <em>Transient Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Transient Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientRef()
   * @see #getBz356181_Main()
   * @generated
   */
  EReference getBz356181_Main_TransientRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientOtherRef <em>Transient Other Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Transient Other Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientOtherRef()
   * @see #getBz356181_Main()
   * @generated
   */
  EReference getBz356181_Main_TransientOtherRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient <em>Bz356181 Transient</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz356181 Transient</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient
   * @generated
   */
  EClass getBz356181_Transient();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient <em>Bz356181 Non Transient</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz356181 Non Transient</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient
   * @generated
   */
  EClass getBz356181_NonTransient();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient#getMain <em>Main</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Main</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient#getMain()
   * @see #getBz356181_NonTransient()
   * @generated
   */
  EReference getBz356181_NonTransient_Main();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main <em>Bz387752 Main</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz387752 Main</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main
   * @generated
   */
  EClass getBz387752_Main();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrUnsettable <em>Str Unsettable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Str Unsettable</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrUnsettable()
   * @see #getBz387752_Main()
   * @generated
   */
  EAttribute getBz387752_Main_StrUnsettable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrSettable <em>Str Settable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Str Settable</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getStrSettable()
   * @see #getBz387752_Main()
   * @generated
   */
  EAttribute getBz387752_Main_StrSettable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumSettable <em>Enum Settable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enum Settable</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumSettable()
   * @see #getBz387752_Main()
   * @generated
   */
  EAttribute getBz387752_Main_EnumSettable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumUnsettable <em>Enum Unsettable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enum Unsettable</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main#getEnumUnsettable()
   * @see #getBz387752_Main()
   * @generated
   */
  EAttribute getBz387752_Main_EnumUnsettable();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group <em>Bz380987 Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz380987 Group</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group
   * @generated
   */
  EClass getBz380987_Group();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group#getPeople <em>People</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>People</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group#getPeople()
   * @see #getBz380987_Group()
   * @generated
   */
  EReference getBz380987_Group_People();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place <em>Bz380987 Place</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz380987 Place</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place
   * @generated
   */
  EClass getBz380987_Place();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place#getName()
   * @see #getBz380987_Place()
   * @generated
   */
  EAttribute getBz380987_Place_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place#getPeople <em>People</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>People</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place#getPeople()
   * @see #getBz380987_Place()
   * @generated
   */
  EReference getBz380987_Place_People();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person <em>Bz380987 Person</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz380987 Person</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person
   * @generated
   */
  EClass getBz380987_Person();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getName()
   * @see #getBz380987_Person()
   * @generated
   */
  EAttribute getBz380987_Person_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getGroup <em>Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Group</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getGroup()
   * @see #getBz380987_Person()
   * @generated
   */
  EReference getBz380987_Person_Group();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getPlaces <em>Places</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Places</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getPlaces()
   * @see #getBz380987_Person()
   * @generated
   */
  EReference getBz380987_Person_Places();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A <em>Bz398057 A</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz398057 A</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A
   * @generated
   */
  EClass getBz398057A();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getListOfB <em>List Of B</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>List Of B</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getListOfB()
   * @see #getBz398057A()
   * @generated
   */
  EReference getBz398057A_ListOfB();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getDbId <em>Db Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Db Id</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A#getDbId()
   * @see #getBz398057A()
   * @generated
   */
  EAttribute getBz398057A_DbId();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A1 <em>Bz398057 A1</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz398057 A1</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A1
   * @generated
   */
  EClass getBz398057A1();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B <em>Bz398057 B</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz398057 B</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B
   * @generated
   */
  EClass getBz398057B();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getRefToClassA <em>Ref To Class A</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Ref To Class A</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getRefToClassA()
   * @see #getBz398057B()
   * @generated
   */
  EReference getBz398057B_RefToClassA();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getValue()
   * @see #getBz398057B()
   * @generated
   */
  EAttribute getBz398057B_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getDbId <em>Db Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Db Id</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B#getDbId()
   * @see #getBz398057B()
   * @generated
   */
  EAttribute getBz398057B_DbId();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1 <em>Bz398057 B1</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz398057 B1</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1
   * @generated
   */
  EClass getBz398057B1();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1#getValueStr <em>Value Str</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value Str</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1#getValueStr()
   * @see #getBz398057B1()
   * @generated
   */
  EAttribute getBz398057B1_ValueStr();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P <em>Bz397682 P</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz397682 P</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P
   * @generated
   */
  EClass getBz397682P();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getDbId <em>Db Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Db Id</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getDbId()
   * @see #getBz397682P()
   * @generated
   */
  EAttribute getBz397682P_DbId();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getListOfC <em>List Of C</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>List Of C</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P#getListOfC()
   * @see #getBz397682P()
   * @generated
   */
  EReference getBz397682P_ListOfC();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C <em>Bz397682 C</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Bz397682 C</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C
   * @generated
   */
  EClass getBz397682C();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToP <em>Ref To P</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Ref To P</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToP()
   * @see #getBz397682C()
   * @generated
   */
  EReference getBz397682C_RefToP();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToC <em>Ref To C</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref To C</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getRefToC()
   * @see #getBz397682C()
   * @generated
   */
  EReference getBz397682C_RefToC();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getDbId <em>Db Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Db Id</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C#getDbId()
   * @see #getBz397682C()
   * @generated
   */
  EAttribute getBz397682C_DbId();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum <em>Bz387752 Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Bz387752 Enum</em>'.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
   * @generated
   */
  EEnum getBz387752_Enum();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  HibernateTestFactory getHibernateTestFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl <em>Bz356181 Main</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Main()
     * @generated
     */
    EClass BZ356181_MAIN = eINSTANCE.getBz356181_Main();

    /**
     * The meta object literal for the '<em><b>Transient</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ356181_MAIN__TRANSIENT = eINSTANCE.getBz356181_Main_Transient();

    /**
     * The meta object literal for the '<em><b>Non Transient</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ356181_MAIN__NON_TRANSIENT = eINSTANCE.getBz356181_Main_NonTransient();

    /**
     * The meta object literal for the '<em><b>Transient Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ356181_MAIN__TRANSIENT_REF = eINSTANCE.getBz356181_Main_TransientRef();

    /**
     * The meta object literal for the '<em><b>Transient Other Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ356181_MAIN__TRANSIENT_OTHER_REF = eINSTANCE.getBz356181_Main_TransientOtherRef();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl <em>Bz356181 Transient</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Transient()
     * @generated
     */
    EClass BZ356181_TRANSIENT = eINSTANCE.getBz356181_Transient();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl <em>Bz356181 Non Transient</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_NonTransient()
     * @generated
     */
    EClass BZ356181_NON_TRANSIENT = eINSTANCE.getBz356181_NonTransient();

    /**
     * The meta object literal for the '<em><b>Main</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ356181_NON_TRANSIENT__MAIN = eINSTANCE.getBz356181_NonTransient_Main();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl <em>Bz387752 Main</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz387752_Main()
     * @generated
     */
    EClass BZ387752_MAIN = eINSTANCE.getBz387752_Main();

    /**
     * The meta object literal for the '<em><b>Str Unsettable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ387752_MAIN__STR_UNSETTABLE = eINSTANCE.getBz387752_Main_StrUnsettable();

    /**
     * The meta object literal for the '<em><b>Str Settable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ387752_MAIN__STR_SETTABLE = eINSTANCE.getBz387752_Main_StrSettable();

    /**
     * The meta object literal for the '<em><b>Enum Settable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ387752_MAIN__ENUM_SETTABLE = eINSTANCE.getBz387752_Main_EnumSettable();

    /**
     * The meta object literal for the '<em><b>Enum Unsettable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ387752_MAIN__ENUM_UNSETTABLE = eINSTANCE.getBz387752_Main_EnumUnsettable();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_GroupImpl <em>Bz380987 Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_GroupImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz380987_Group()
     * @generated
     */
    EClass BZ380987_GROUP = eINSTANCE.getBz380987_Group();

    /**
     * The meta object literal for the '<em><b>People</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ380987_GROUP__PEOPLE = eINSTANCE.getBz380987_Group_People();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PlaceImpl <em>Bz380987 Place</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PlaceImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz380987_Place()
     * @generated
     */
    EClass BZ380987_PLACE = eINSTANCE.getBz380987_Place();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ380987_PLACE__NAME = eINSTANCE.getBz380987_Place_Name();

    /**
     * The meta object literal for the '<em><b>People</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ380987_PLACE__PEOPLE = eINSTANCE.getBz380987_Place_People();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl <em>Bz380987 Person</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz380987_Person()
     * @generated
     */
    EClass BZ380987_PERSON = eINSTANCE.getBz380987_Person();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ380987_PERSON__NAME = eINSTANCE.getBz380987_Person_Name();

    /**
     * The meta object literal for the '<em><b>Group</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ380987_PERSON__GROUP = eINSTANCE.getBz380987_Person_Group();

    /**
     * The meta object literal for the '<em><b>Places</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ380987_PERSON__PLACES = eINSTANCE.getBz380987_Person_Places();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057AImpl <em>Bz398057 A</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057AImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057A()
     * @generated
     */
    EClass BZ398057_A = eINSTANCE.getBz398057A();

    /**
     * The meta object literal for the '<em><b>List Of B</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ398057_A__LIST_OF_B = eINSTANCE.getBz398057A_ListOfB();

    /**
     * The meta object literal for the '<em><b>Db Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ398057_A__DB_ID = eINSTANCE.getBz398057A_DbId();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057A1Impl <em>Bz398057 A1</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057A1Impl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057A1()
     * @generated
     */
    EClass BZ398057_A1 = eINSTANCE.getBz398057A1();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl <em>Bz398057 B</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057B()
     * @generated
     */
    EClass BZ398057_B = eINSTANCE.getBz398057B();

    /**
     * The meta object literal for the '<em><b>Ref To Class A</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ398057_B__REF_TO_CLASS_A = eINSTANCE.getBz398057B_RefToClassA();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ398057_B__VALUE = eINSTANCE.getBz398057B_Value();

    /**
     * The meta object literal for the '<em><b>Db Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ398057_B__DB_ID = eINSTANCE.getBz398057B_DbId();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057B1Impl <em>Bz398057 B1</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057B1Impl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz398057B1()
     * @generated
     */
    EClass BZ398057_B1 = eINSTANCE.getBz398057B1();

    /**
     * The meta object literal for the '<em><b>Value Str</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ398057_B1__VALUE_STR = eINSTANCE.getBz398057B1_ValueStr();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682PImpl <em>Bz397682 P</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682PImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz397682P()
     * @generated
     */
    EClass BZ397682_P = eINSTANCE.getBz397682P();

    /**
     * The meta object literal for the '<em><b>Db Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ397682_P__DB_ID = eINSTANCE.getBz397682P_DbId();

    /**
     * The meta object literal for the '<em><b>List Of C</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ397682_P__LIST_OF_C = eINSTANCE.getBz397682P_ListOfC();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl <em>Bz397682 C</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz397682C()
     * @generated
     */
    EClass BZ397682_C = eINSTANCE.getBz397682C();

    /**
     * The meta object literal for the '<em><b>Ref To P</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ397682_C__REF_TO_P = eINSTANCE.getBz397682C_RefToP();

    /**
     * The meta object literal for the '<em><b>Ref To C</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BZ397682_C__REF_TO_C = eINSTANCE.getBz397682C_RefToC();

    /**
     * The meta object literal for the '<em><b>Db Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BZ397682_C__DB_ID = eINSTANCE.getBz397682C_DbId();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum <em>Bz387752 Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum
     * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz387752_Enum()
     * @generated
     */
    EEnum BZ387752_ENUM = eINSTANCE.getBz387752_Enum();

  }

} // HibernateTestPackage
