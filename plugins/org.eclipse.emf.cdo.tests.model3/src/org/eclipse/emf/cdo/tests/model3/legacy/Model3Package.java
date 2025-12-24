/*
 * Copyright (c) 2013, 2015, 2018, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy;

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
 * @extends org.eclipse.emf.cdo.tests.model3.Model3Package
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model3.legacy.Model3Factory
 * @model kind="package"
 * @generated
 */
public interface Model3Package extends EPackage, org.eclipse.emf.cdo.tests.model3.Model3Package
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model3";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model3Package eINSTANCE = org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.Class1Impl <em>Class1</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Class1Impl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getClass1()
   * @generated
   */
  int CLASS1 = 0;

  /**
   * The feature id for the '<em><b>Class2</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS1__CLASS2 = 0;

  /**
   * The feature id for the '<em><b>Additional Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS1__ADDITIONAL_VALUE = 1;

  /**
   * The number of structural features of the '<em>Class1</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS1_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MetaRefImpl <em>Meta Ref</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.MetaRefImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getMetaRef()
   * @generated
   */
  int META_REF = 1;

  /**
   * The feature id for the '<em><b>EPackage Ref</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int META_REF__EPACKAGE_REF = 0;

  /**
   * The feature id for the '<em><b>EClass Ref</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int META_REF__ECLASS_REF = 1;

  /**
   * The feature id for the '<em><b>EReference Ref</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int META_REF__EREFERENCE_REF = 2;

  /**
   * The number of structural features of the '<em>Meta Ref</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int META_REF_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.PolygonImpl <em>Polygon</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.PolygonImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getPolygon()
   * @generated
   */
  int POLYGON = 2;

  /**
   * The feature id for the '<em><b>Points</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POLYGON__POINTS = 0;

  /**
   * The number of structural features of the '<em>Polygon</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POLYGON_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.PolygonWithDuplicatesImpl <em>Polygon With Duplicates</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.PolygonWithDuplicatesImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getPolygonWithDuplicates()
   * @generated
   */
  int POLYGON_WITH_DUPLICATES = 3;

  /**
   * The feature id for the '<em><b>Points</b></em>' attribute list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POLYGON_WITH_DUPLICATES__POINTS = 0;

  /**
   * The number of structural features of the '<em>Polygon With Duplicates</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int POLYGON_WITH_DUPLICATES_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeAImpl <em>Node A</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeAImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getNodeA()
   * @generated
   */
  int NODE_A = 4;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_A__CHILDREN = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_A__NAME = 1;

  /**
   * The feature id for the '<em><b>Other Nodes</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_A__OTHER_NODES = 2;

  /**
   * The number of structural features of the '<em>Node A</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_A_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeBImpl <em>Node B</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeBImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getNodeB()
   * @generated
   */
  int NODE_B = 5;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_B__CHILDREN = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_B__PARENT = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_B__NAME = 2;

  /**
   * The number of structural features of the '<em>Node B</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_B_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl <em>Node C</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getNodeC()
   * @generated
   */
  int NODE_C = 6;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_C__CHILDREN = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_C__PARENT = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_C__NAME = 2;

  /**
   * The feature id for the '<em><b>Other Nodes</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_C__OTHER_NODES = 3;

  /**
   * The feature id for the '<em><b>Opposite Nodes</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int NODE_C__OPPOSITE_NODES = 4;

  /**
   * The number of structural features of the '<em>Node C</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_C_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeDImpl <em>Node D</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeDImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getNodeD()
   * @generated
   */
  int NODE_D = 7;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_D__CHILDREN = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_D__PARENT = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_D__NAME = 2;

  /**
   * The feature id for the '<em><b>Other Nodes</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_D__OTHER_NODES = 3;

  /**
   * The feature id for the '<em><b>Opposite Node</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_D__OPPOSITE_NODE = 4;

  /**
   * The number of structural features of the '<em>Node D</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_D_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeEImpl <em>Node E</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeEImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getNodeE()
   * @generated
   */
  int NODE_E = 8;

  /**
   * The feature id for the '<em><b>Main Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_E__MAIN_NODE = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_E__NAME = 1;

  /**
   * The feature id for the '<em><b>Other Nodes</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_E__OTHER_NODES = 2;

  /**
   * The number of structural features of the '<em>Node E</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_E_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ImageImpl <em>Image</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.ImageImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getImage()
   * @generated
   */
  int IMAGE = 9;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMAGE__WIDTH = 0;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMAGE__HEIGHT = 1;

  /**
   * The feature id for the '<em><b>Data</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMAGE__DATA = 2;

  /**
   * The number of structural features of the '<em>Image</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMAGE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.FileImpl <em>File</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.FileImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getFile()
   * @generated
   */
  int FILE = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE__NAME = 0;

  /**
   * The feature id for the '<em><b>Data</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE__DATA = 1;

  /**
   * The number of structural features of the '<em>File</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MultiLobImpl <em>Multi Lob</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.MultiLobImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getMultiLob()
   * @generated
   */
  int MULTI_LOB = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI_LOB__NAME = 0;

  /**
   * The feature id for the '<em><b>Blobs</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI_LOB__BLOBS = 1;

  /**
   * The feature id for the '<em><b>Clobs</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI_LOB__CLOBS = 2;

  /**
   * The number of structural features of the '<em>Multi Lob</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI_LOB_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithIDAttributeImpl <em>Class With ID Attribute</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithIDAttributeImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getClassWithIDAttribute()
   * @generated
   */
  int CLASS_WITH_ID_ATTRIBUTE = 12;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_ID_ATTRIBUTE__ID = 0;

  /**
   * The number of structural features of the '<em>Class With ID Attribute</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_ID_ATTRIBUTE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithJavaClassAttributeImpl <em>Class With Java Class Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithJavaClassAttributeImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getClassWithJavaClassAttribute()
   * @generated
   */
  int CLASS_WITH_JAVA_CLASS_ATTRIBUTE = 13;

  /**
   * The feature id for the '<em><b>Java Class</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS = 0;

  /**
   * The number of structural features of the '<em>Class With Java Class Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_JAVA_CLASS_ATTRIBUTE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithJavaObjectAttributeImpl <em>Class With Java Object Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithJavaObjectAttributeImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getClassWithJavaObjectAttribute()
   * @generated
   */
  int CLASS_WITH_JAVA_OBJECT_ATTRIBUTE = 14;

  /**
   * The feature id for the '<em><b>Java Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT = 0;

  /**
   * The number of structural features of the '<em>Class With Java Object Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_JAVA_OBJECT_ATTRIBUTE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl <em>Class With Transient Containment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getClassWithTransientContainment()
   * @generated
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT = 15;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT__NAME = 0;

  /**
   * The feature id for the '<em><b>Transient Child</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD = 1;

  /**
   * The feature id for the '<em><b>Transient Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN = 2;

  /**
   * The feature id for the '<em><b>Persistent Child</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD = 3;

  /**
   * The feature id for the '<em><b>Persistent Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN = 4;

  /**
   * The number of structural features of the '<em>Class With Transient Containment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CLASS_WITH_TRANSIENT_CONTAINMENT_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeTargetImpl <em>Edge Target</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeTargetImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getEdgeTarget()
   * @generated
   */
  int EDGE_TARGET = 16;

  /**
   * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_TARGET__OUTGOING_EDGES = 0;

  /**
   * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_TARGET__INCOMING_EDGES = 1;

  /**
   * The number of structural features of the '<em>Edge Target</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_TARGET_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeFImpl <em>Node F</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeFImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getNodeF()
   * @generated
   */
  int NODE_F = 17;

  /**
   * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_F__OUTGOING_EDGES = EDGE_TARGET__OUTGOING_EDGES;

  /**
   * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_F__INCOMING_EDGES = EDGE_TARGET__INCOMING_EDGES;

  /**
   * The number of structural features of the '<em>Node F</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NODE_F_FEATURE_COUNT = EDGE_TARGET_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeImpl <em>Edge</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getEdge()
   * @generated
   */
  int EDGE = 18;

  /**
   * The feature id for the '<em><b>Source Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE__SOURCE_NODE = 0;

  /**
   * The feature id for the '<em><b>Target Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE__TARGET_NODE = 1;

  /**
   * The number of structural features of the '<em>Edge</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.DiagramImpl <em>Diagram</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.DiagramImpl
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getDiagram()
   * @generated
   */
  int DIAGRAM = 19;

  /**
   * The feature id for the '<em><b>Edges</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIAGRAM__EDGES = 0;

  /**
   * The feature id for the '<em><b>Edge Targets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIAGRAM__EDGE_TARGETS = 1;

  /**
   * The number of structural features of the '<em>Diagram</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIAGRAM_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '<em>Point</em>' data type.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model3.Point
   * @see org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3PackageImpl#getPoint()
   * @generated
   */
  int POINT = 20;

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.Class1 <em>Class1</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Class1</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Class1
   * @generated
   */
  EClass getClass1();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.Class1#getClass2 <em>Class2</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Class2</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Class1#getClass2()
   * @see #getClass1()
   * @generated
   */
  EReference getClass1_Class2();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.Class1#getAdditionalValue <em>Additional Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Additional Value</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Class1#getAdditionalValue()
   * @see #getClass1()
   * @generated
   */
  EAttribute getClass1_AdditionalValue();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.MetaRef <em>Meta Ref</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Meta Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.MetaRef
   * @generated
   */
  EClass getMetaRef();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEPackageRef <em>EPackage Ref</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>EPackage Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MetaRef#getEPackageRef()
   * @see #getMetaRef()
   * @generated
   */
  EReference getMetaRef_EPackageRef();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEClassRef <em>EClass Ref</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>EClass Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MetaRef#getEClassRef()
   * @see #getMetaRef()
   * @generated
   */
  EReference getMetaRef_EClassRef();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEReferenceRef <em>EReference Ref</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>EReference Ref</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MetaRef#getEReferenceRef()
   * @see #getMetaRef()
   * @generated
   */
  EReference getMetaRef_EReferenceRef();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.Polygon <em>Polygon</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Polygon</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Polygon
   * @generated
   */
  EClass getPolygon();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model3.Polygon#getPoints <em>Points</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Points</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Polygon#getPoints()
   * @see #getPolygon()
   * @generated
   */
  EAttribute getPolygon_Points();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates <em>Polygon With Duplicates</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Polygon With Duplicates</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates
   * @generated
   */
  EClass getPolygonWithDuplicates();

  @Override
  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.legacy.model3.PolygonWithDuplicates#getPoints <em>Points</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute list '<em>Points</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.PolygonWithDuplicates#getPoints()
   * @see #getPolygonWithDuplicates()
   * @generated
   */
  EAttribute getPolygonWithDuplicates_Points();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.NodeA <em>Node A</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Node A</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.NodeA
   * @generated
   */
  EClass getNodeA();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeA#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeA#getChildren()
   * @see #getNodeA()
   * @generated
   */
  EReference getNodeA_Children();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.NodeA#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeA#getName()
   * @see #getNodeA()
   * @generated
   */
  EAttribute getNodeA_Name();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeA#getOtherNodes <em>Other Nodes</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Other Nodes</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeA#getOtherNodes()
   * @see #getNodeA()
   * @generated
   */
  EReference getNodeA_OtherNodes();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.NodeB <em>Node B</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Node B</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.NodeB
   * @generated
   */
  EClass getNodeB();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeB#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeB#getChildren()
   * @see #getNodeB()
   * @generated
   */
  EReference getNodeB_Children();

  @Override
  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model3.NodeB#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeB#getParent()
   * @see #getNodeB()
   * @generated
   */
  EReference getNodeB_Parent();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.NodeB#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeB#getName()
   * @see #getNodeB()
   * @generated
   */
  EAttribute getNodeB_Name();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.NodeC <em>Node C</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Node C</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.NodeC
   * @generated
   */
  EClass getNodeC();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getChildren()
   * @see #getNodeC()
   * @generated
   */
  EReference getNodeC_Children();

  @Override
  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getParent()
   * @see #getNodeC()
   * @generated
   */
  EReference getNodeC_Parent();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getName()
   * @see #getNodeC()
   * @generated
   */
  EAttribute getNodeC_Name();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeC#getOtherNodes <em>Other Nodes</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Other Nodes</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeC#getOtherNodes()
   * @see #getNodeC()
   * @generated
   */
  EReference getNodeC_OtherNodes();

  @Override
  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.tests.legacy.model3.NodeC#getOppositeNodes <em>Opposite Nodes</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Opposite Nodes</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.NodeC#getOppositeNodes()
   * @see #getNodeC()
   * @generated
   */
  EReference getNodeC_OppositeNodes();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.NodeD <em>Node D</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Node D</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.NodeD
   * @generated
   */
  EClass getNodeD();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeD#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeD#getChildren()
   * @see #getNodeD()
   * @generated
   */
  EReference getNodeD_Children();

  @Override
  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model3.NodeD#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeD#getParent()
   * @see #getNodeD()
   * @generated
   */
  EReference getNodeD_Parent();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.NodeD#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeD#getName()
   * @see #getNodeD()
   * @generated
   */
  EAttribute getNodeD_Name();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeD#getOtherNodes <em>Other Nodes</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Other Nodes</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeD#getOtherNodes()
   * @see #getNodeD()
   * @generated
   */
  EReference getNodeD_OtherNodes();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.NodeD#getOppositeNode <em>Opposite Node</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Opposite Node</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeD#getOppositeNode()
   * @see #getNodeD()
   * @generated
   */
  EReference getNodeD_OppositeNode();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.NodeE <em>Node E</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Node E</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeE
   * @generated
   */
  EClass getNodeE();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.NodeE#getMainNode <em>Main Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Main Node</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeE#getMainNode()
   * @see #getNodeE()
   * @generated
   */
  EReference getNodeE_MainNode();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.NodeE#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeE#getName()
   * @see #getNodeE()
   * @generated
   */
  EAttribute getNodeE_Name();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.NodeE#getOtherNodes <em>Other Nodes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Other Nodes</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeE#getOtherNodes()
   * @see #getNodeE()
   * @generated
   */
  EReference getNodeE_OtherNodes();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.Image <em>Image</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Image</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.Image
   * @generated
   */
  EClass getImage();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.Image#getWidth <em>Width</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Width</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Image#getWidth()
   * @see #getImage()
   * @generated
   */
  EAttribute getImage_Width();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.Image#getHeight <em>Height</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Height</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Image#getHeight()
   * @see #getImage()
   * @generated
   */
  EAttribute getImage_Height();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.Image#getData <em>Data</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Data</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Image#getData()
   * @see #getImage()
   * @generated
   */
  EAttribute getImage_Data();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.legacy.model3.File <em>File</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>File</em>'.
   * @see org.eclipse.emf.cdo.tests.legacy.model3.File
   * @generated
   */
  EClass getFile();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.File#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.File#getName()
   * @see #getFile()
   * @generated
   */
  EAttribute getFile_Name();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.File#getData <em>Data</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Data</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.File#getData()
   * @see #getFile()
   * @generated
   */
  EAttribute getFile_Data();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.MultiLob <em>Multi Lob</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Multi Lob</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MultiLob
   * @generated
   */
  EClass getMultiLob();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MultiLob#getName()
   * @see #getMultiLob()
   * @generated
   */
  EAttribute getMultiLob_Name();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getBlobs <em>Blobs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Blobs</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MultiLob#getBlobs()
   * @see #getMultiLob()
   * @generated
   */
  EAttribute getMultiLob_Blobs();

  @Override
  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getClobs <em>Clobs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Clobs</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.MultiLob#getClobs()
   * @see #getMultiLob()
   * @generated
   */
  EAttribute getMultiLob_Clobs();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute <em>Class With ID Attribute</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Class With ID Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute
   * @generated
   */
  EClass getClassWithIDAttribute();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute#getId()
   * @see #getClassWithIDAttribute()
   * @generated
   */
  EAttribute getClassWithIDAttribute_Id();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute <em>Class With Java Class Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Class With Java Class Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute
   * @generated
   */
  EClass getClassWithJavaClassAttribute();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute#getJavaClass <em>Java Class</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Java Class</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute#getJavaClass()
   * @see #getClassWithJavaClassAttribute()
   * @generated
   */
  EAttribute getClassWithJavaClassAttribute_JavaClass();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute <em>Class With Java Object Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Class With Java Object Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute
   * @generated
   */
  EClass getClassWithJavaObjectAttribute();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute#getJavaObject <em>Java Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Java Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute#getJavaObject()
   * @see #getClassWithJavaObjectAttribute()
   * @generated
   */
  EAttribute getClassWithJavaObjectAttribute_JavaObject();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment <em>Class With Transient Containment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Class With Transient Containment</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment
   * @generated
   */
  EClass getClassWithTransientContainment();

  @Override
  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getName()
   * @see #getClassWithTransientContainment()
   * @generated
   */
  EAttribute getClassWithTransientContainment_Name();

  @Override
  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChild <em>Transient Child</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Transient Child</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChild()
   * @see #getClassWithTransientContainment()
   * @generated
   */
  EReference getClassWithTransientContainment_TransientChild();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChildren <em>Transient Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Transient Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChildren()
   * @see #getClassWithTransientContainment()
   * @generated
   */
  EReference getClassWithTransientContainment_TransientChildren();

  @Override
  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChild <em>Persistent Child</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Persistent Child</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChild()
   * @see #getClassWithTransientContainment()
   * @generated
   */
  EReference getClassWithTransientContainment_PersistentChild();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChildren <em>Persistent Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Persistent Children</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChildren()
   * @see #getClassWithTransientContainment()
   * @generated
   */
  EReference getClassWithTransientContainment_PersistentChildren();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget <em>Edge Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge Target</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.EdgeTarget
   * @generated
   */
  EClass getEdgeTarget();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget#getOutgoingEdges <em>Outgoing Edges</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Outgoing Edges</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.EdgeTarget#getOutgoingEdges()
   * @see #getEdgeTarget()
   * @generated
   */
  EReference getEdgeTarget_OutgoingEdges();

  @Override
  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget#getIncomingEdges <em>Incoming Edges</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Incoming Edges</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.EdgeTarget#getIncomingEdges()
   * @see #getEdgeTarget()
   * @generated
   */
  EReference getEdgeTarget_IncomingEdges();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.NodeF <em>Node F</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Node F</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.NodeF
   * @generated
   */
  EClass getNodeF();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.Edge <em>Edge</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Edge
   * @generated
   */
  EClass getEdge();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.Edge#getSourceNode <em>Source Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source Node</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Edge#getSourceNode()
   * @see #getEdge()
   * @generated
   */
  EReference getEdge_SourceNode();

  @Override
  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model3.Edge#getTargetNode <em>Target Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target Node</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Edge#getTargetNode()
   * @see #getEdge()
   * @generated
   */
  EReference getEdge_TargetNode();

  @Override
  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model3.Diagram <em>Diagram</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Diagram</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Diagram
   * @generated
   */
  EClass getDiagram();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.Diagram#getEdges <em>Edges</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Edges</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Diagram#getEdges()
   * @see #getDiagram()
   * @generated
   */
  EReference getDiagram_Edges();

  @Override
  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model3.Diagram#getEdgeTargets <em>Edge Targets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Edge Targets</em>'.
   * @see org.eclipse.emf.cdo.tests.model3.Diagram#getEdgeTargets()
   * @see #getDiagram()
   * @generated
   */
  EReference getDiagram_EdgeTargets();

  @Override
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

  @Override
  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model3Factory getModel3Factory();

} // Model3Package
