/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage
 * @generated
 */
public interface AcoreFactory extends EFactory
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n \r\nContributors:\r\n    Martin Fluegge - initial API and implementation\r\n";

  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  AcoreFactory eINSTANCE = org.eclipse.emf.cdo.dawn.examples.acore.impl.AcoreFactoryImpl.init();

  /**
   * Returns a new object of class '<em>AClass</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>AClass</em>'.
   * @generated
   */
  AClass createAClass();

  /**
   * Returns a new object of class '<em>AInterface</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>AInterface</em>'.
   * @generated
   */
  AInterface createAInterface();

  /**
   * Returns a new object of class '<em>ACore Root</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>ACore Root</em>'.
   * @generated
   */
  ACoreRoot createACoreRoot();

  /**
   * Returns a new object of class '<em>AAttribute</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>AAttribute</em>'.
   * @generated
   */
  AAttribute createAAttribute();

  /**
   * Returns a new object of class '<em>AOperation</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>AOperation</em>'.
   * @generated
   */
  AOperation createAOperation();

  /**
   * Returns a new object of class '<em>ABasic Class</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>ABasic Class</em>'.
   * @generated
   */
  ABasicClass createABasicClass();

  /**
   * Returns a new object of class '<em>AParameter</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>AParameter</em>'.
   * @generated
   */
  AParameter createAParameter();

  /**
   * Returns a new object of class '<em>AClass Child</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>AClass Child</em>'.
   * @generated
   */
  AClassChild createAClassChild();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  AcorePackage getAcorePackage();

} // AcoreFactory
