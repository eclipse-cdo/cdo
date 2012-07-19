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
package org.eclipse.emf.cdo.tests.legacy.model3.subpackage;

import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.legacy.model3.subpackage.SubpackagePackage
 * @generated
 */
public interface SubpackageFactory extends org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  SubpackageFactory eINSTANCE = org.eclipse.emf.cdo.tests.legacy.model3.subpackage.impl.SubpackageFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class2</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Class2</em>'.
   * @generated
   */
  Class2 createClass2();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  SubpackagePackage getSubpackagePackage();

} // SubpackageFactory
