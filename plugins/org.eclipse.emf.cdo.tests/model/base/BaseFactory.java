/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package base;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * @see base.BasePackage
 * @generated
 */
public interface BaseFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  BaseFactory eINSTANCE = base.impl.BaseFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Class</em>'.
   * @generated
   */
  BaseClass createBaseClass();

  /**
   * Returns a new object of class '<em>Document</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Document</em>'.
   * @generated
   */
  Document createDocument();

  /**
  	 * Returns a new object of class '<em>Element</em>'.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @return a new object of class '<em>Element</em>'.
  	 * @generated
  	 */
  Element createElement();

  /**
  	 * Returns the package supported by this factory.
  	 * <!-- begin-user-doc --> <!-- end-user-doc -->
  	 * @return the package supported by this factory.
  	 * @generated
  	 */
  BasePackage getBasePackage();

} // BaseFactory
