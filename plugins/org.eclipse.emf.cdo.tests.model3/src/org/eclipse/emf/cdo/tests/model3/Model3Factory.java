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
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package
 * @generated
 */
public interface Model3Factory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model3Factory eINSTANCE = org.eclipse.emf.cdo.tests.model3.impl.Model3FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class1</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Class1</em>'.
   * @generated
   */
  Class1 createClass1();

  /**
   * Returns a new object of class '<em>Meta Ref</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Meta Ref</em>'.
   * @generated
   */
  MetaRef createMetaRef();

  /**
   * Returns a new object of class '<em>Polygon</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Polygon</em>'.
   * @generated
   */
  Polygon createPolygon();

  /**
   * Returns a new object of class '<em>Polygon With Duplicates</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Polygon With Duplicates</em>'.
   * @generated
   */
  PolygonWithDuplicates createPolygonWithDuplicates();

  /**
   * Returns a new object of class '<em>Node A</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node A</em>'.
   * @generated
   */
  NodeA createNodeA();

  /**
   * Returns a new object of class '<em>Node B</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node B</em>'.
   * @generated
   */
  NodeB createNodeB();

  /**
   * Returns a new object of class '<em>Node C</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node C</em>'.
   * @generated
   */
  NodeC createNodeC();

  /**
   * Returns a new object of class '<em>Node D</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Node D</em>'.
   * @generated
   */
  NodeD createNodeD();

  /**
   * Returns a new object of class '<em>Image</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Image</em>'.
   * @generated
   */
  Image createImage();

  /**
   * Returns a new object of class '<em>File</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>File</em>'.
   * @generated
   */
  File createFile();

  /**
   * Returns a new object of class '<em>Class With ID Attribute</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Class With ID Attribute</em>'.
   * @generated
   */
  ClassWithIDAttribute createClassWithIDAttribute();

  /**
   * Returns a new object of class '<em>Class With Java Class Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class With Java Class Attribute</em>'.
   * @generated
   */
  ClassWithJavaClassAttribute createClassWithJavaClassAttribute();

  /**
   * Returns a new object of class '<em>Class With Java Object Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class With Java Object Attribute</em>'.
   * @generated
   */
  ClassWithJavaObjectAttribute createClassWithJavaObjectAttribute();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Model3Package getModel3Package();

} // Model3Factory
