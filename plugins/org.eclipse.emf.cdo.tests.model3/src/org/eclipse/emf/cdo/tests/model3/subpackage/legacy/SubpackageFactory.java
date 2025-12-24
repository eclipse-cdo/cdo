/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.subpackage.legacy;

import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model3.subpackage.legacy.SubpackagePackage
 * @generated
 */
public interface SubpackageFactory extends EFactory, org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  SubpackageFactory eINSTANCE = org.eclipse.emf.cdo.tests.model3.subpackage.legacy.impl.SubpackageFactoryImpl.init();

  @Override
  /**
   * Returns a new object of class '<em>Class2</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Class2</em>'.
   * @generated
   */
  Class2 createClass2();

  @Override
  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  SubpackagePackage getSubpackagePackage();

} // SubpackageFactory
