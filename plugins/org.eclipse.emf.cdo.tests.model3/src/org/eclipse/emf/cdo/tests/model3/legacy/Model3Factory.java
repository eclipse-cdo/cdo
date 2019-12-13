/*
 * Copyright (c) 2013, 2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy;

import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment;
import org.eclipse.emf.cdo.tests.model3.Diagram;
import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.EdgeTarget;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.tests.model3.NodeB;
import org.eclipse.emf.cdo.tests.model3.NodeC;
import org.eclipse.emf.cdo.tests.model3.NodeD;
import org.eclipse.emf.cdo.tests.model3.NodeE;
import org.eclipse.emf.cdo.tests.model3.NodeF;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model3.Model3Factory
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model3.legacy.Model3Package
 * @generated
 */
public interface Model3Factory extends EFactory, org.eclipse.emf.cdo.tests.model3.Model3Factory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model3Factory eINSTANCE = org.eclipse.emf.cdo.tests.model3.legacy.impl.Model3FactoryImpl.init();

  @Override
  /**
   * Returns a new object of class '<em>Class1</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Class1</em>'.
   * @generated
   */
  Class1 createClass1();

  @Override
  /**
   * Returns a new object of class '<em>Meta Ref</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Meta Ref</em>'.
   * @generated
   */
  MetaRef createMetaRef();

  @Override
  /**
   * Returns a new object of class '<em>Polygon</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Polygon</em>'.
   * @generated
   */
  Polygon createPolygon();

  @Override
  /**
   * Returns a new object of class '<em>Polygon With Duplicates</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Polygon With Duplicates</em>'.
   * @generated
   */
  PolygonWithDuplicates createPolygonWithDuplicates();

  @Override
  /**
   * Returns a new object of class '<em>Node A</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node A</em>'.
   * @generated
   */
  NodeA createNodeA();

  @Override
  /**
   * Returns a new object of class '<em>Node B</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node B</em>'.
   * @generated
   */
  NodeB createNodeB();

  @Override
  /**
   * Returns a new object of class '<em>Node C</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node C</em>'.
   * @generated
   */
  NodeC createNodeC();

  @Override
  /**
   * Returns a new object of class '<em>Node D</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node D</em>'.
   * @generated
   */
  NodeD createNodeD();

  @Override
  /**
   * Returns a new object of class '<em>Node E</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Node E</em>'.
   * @generated
   */
  NodeE createNodeE();

  @Override
  /**
   * Returns a new object of class '<em>Image</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Image</em>'.
   * @generated
   */
  Image createImage();

  @Override
  /**
   * Returns a new object of class '<em>File</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>File</em>'.
   * @generated
   */
  File createFile();

  @Override
  /**
   * Returns a new object of class '<em>Class With ID Attribute</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Class With ID Attribute</em>'.
   * @generated
   */
  ClassWithIDAttribute createClassWithIDAttribute();

  @Override
  /**
   * Returns a new object of class '<em>Class With Java Class Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class With Java Class Attribute</em>'.
   * @generated
   */
  ClassWithJavaClassAttribute createClassWithJavaClassAttribute();

  @Override
  /**
   * Returns a new object of class '<em>Class With Java Object Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class With Java Object Attribute</em>'.
   * @generated
   */
  ClassWithJavaObjectAttribute createClassWithJavaObjectAttribute();

  @Override
  /**
   * Returns a new object of class '<em>Class With Transient Containment</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class With Transient Containment</em>'.
   * @generated
   */
  ClassWithTransientContainment createClassWithTransientContainment();

  @Override
  /**
   * Returns a new object of class '<em>Edge Target</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Edge Target</em>'.
   * @generated
   */
  EdgeTarget createEdgeTarget();

  @Override
  /**
   * Returns a new object of class '<em>Node F</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Node F</em>'.
   * @generated
   */
  NodeF createNodeF();

  @Override
  /**
   * Returns a new object of class '<em>Edge</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Edge</em>'.
   * @generated
   */
  Edge createEdge();

  @Override
  /**
   * Returns a new object of class '<em>Diagram</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Diagram</em>'.
   * @generated
   */
  Diagram createDiagram();

  @Override
  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Model3Package getModel3Package();

} // Model3Factory
