/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.legacy.model3;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see org.eclipse.emf.cdo.tests.legacy.model3.Model3Factory
 * @model kind="package"
 * @generated
 */
public interface Model3Package extends org.eclipse.emf.cdo.tests.model3.Model3Package
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model3";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/legacy/model3/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "model3";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model3Package eINSTANCE = org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.Class1Impl <em>Class1</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Class1Impl
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getClass1()
   * @generated
   */
  int CLASS1 = 0;

  /**
   * The feature id for the '<em><b>Class2</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CLASS1__CLASS2 = 0;

  /**
   * The number of structural features of the '<em>Class1</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CLASS1_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.MetaRefImpl <em>Meta Ref</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.MetaRefImpl
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getMetaRef()
   * @generated
   */
  int META_REF = 1;

  /**
   * The feature id for the '<em><b>EPackage Ref</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int META_REF__EPACKAGE_REF = 0;

  /**
   * The number of structural features of the '<em>Meta Ref</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int META_REF_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.PolygonImpl <em>Polygon</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.PolygonImpl
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getPolygon()
   * @generated
   */
  int POLYGON = 2;

  /**
   * The feature id for the '<em><b>Points</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int POLYGON__POINTS = 0;

  /**
   * The number of structural features of the '<em>Polygon</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int POLYGON_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '<em>Point</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model3.Point
   * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getPoint()
   * @generated
   */
  int POINT = 3;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.Class1 <em>Class1</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Class1</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Class1
   * @generated
   */
  EClass getClass1();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.legacy.model3.Class1#getClass2
   * <em>Class2</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Class2</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Class1#getClass2()
   * @see #getClass1()
   * @generated
   */
  EReference getClass1_Class2();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.MetaRef <em>Meta Ref</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Meta Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.MetaRef
   * @generated
   */
  EClass getMetaRef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.legacy.model3.MetaRef#getEPackageRef
   * <em>EPackage Ref</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>EPackage Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.MetaRef#getEPackageRef()
   * @see #getMetaRef()
   * @generated
   */
  EReference getMetaRef_EPackageRef();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.Polygon <em>Polygon</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Polygon</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Polygon
   * @generated
   */
  EClass getPolygon();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.legacy.model3.Polygon#getPoints
   * <em>Points</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Points</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Polygon#getPoints()
   * @see #getPolygon()
   * @generated
   */
  EAttribute getPolygon_Points();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.tests.model3.Point <em>Point</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Point</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Point
   * @model instanceClass="org.eclipse.emf.cdo.tests.model3.Point"
   * @generated
   */
  EDataType getPoint();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model3Factory getModel3Factory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.Class1Impl <em>Class1</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Class1Impl
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getClass1()
     * @generated
     */
    EClass CLASS1 = eINSTANCE.getClass1();

    /**
     * The meta object literal for the '<em><b>Class2</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CLASS1__CLASS2 = eINSTANCE.getClass1_Class2();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.MetaRefImpl
     * <em>Meta Ref</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.MetaRefImpl
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getMetaRef()
     * @generated
     */
    EClass META_REF = eINSTANCE.getMetaRef();

    /**
     * The meta object literal for the '<em><b>EPackage Ref</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference META_REF__EPACKAGE_REF = eINSTANCE.getMetaRef_EPackageRef();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.PolygonImpl
     * <em>Polygon</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.PolygonImpl
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getPolygon()
     * @generated
     */
    EClass POLYGON = eINSTANCE.getPolygon();

    /**
     * The meta object literal for the '<em><b>Points</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute POLYGON__POINTS = eINSTANCE.getPolygon_Points();

    /**
     * The meta object literal for the '<em>Point</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model3.Point
     * @see org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl#getPoint()
     * @generated
     */
    EDataType POINT = eINSTANCE.getPoint();

  }

} // Model3Package
