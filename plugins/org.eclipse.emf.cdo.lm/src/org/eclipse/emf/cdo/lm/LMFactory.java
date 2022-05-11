/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.LMPackage
 * @generated
 */
public interface LMFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  LMFactory eINSTANCE = org.eclipse.emf.cdo.lm.impl.LMFactoryImpl.init();

  /**
   * Returns a new object of class '<em>System</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>System</em>'.
   * @generated
   */
  System createSystem();

  /**
   * Returns a new object of class '<em>Process</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Process</em>'.
   * @generated
   */
  Process createProcess();

  /**
   * Returns a new object of class '<em>Drop Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Drop Type</em>'.
   * @generated
   */
  DropType createDropType();

  DropType createDropType(String name, boolean release);

  /**
   * Returns a new object of class '<em>Module</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Module</em>'.
   * @generated
   */
  Module createModule();

  /**
   * Returns a new object of class '<em>Dependency</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Dependency</em>'.
   * @generated
   */
  Dependency createDependency();

  /**
   * Returns a new object of class '<em>Module Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Module Type</em>'.
   * @generated
   */
  ModuleType createModuleType();

  ModuleType createModuleType(String name);

  /**
   * Returns a new object of class '<em>Stream</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Stream</em>'.
   * @generated
   */
  Stream createStream();

  Stream createStream(int majorVersion, int minorVersion, String codeName);

  /**
   * Returns a new object of class '<em>Change</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Change</em>'.
   * @generated
   */
  Change createChange();

  /**
   * Returns a new object of class '<em>Delivery</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Delivery</em>'.
   * @generated
   */
  Delivery createDelivery();

  /**
   * Returns a new object of class '<em>Drop</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Drop</em>'.
   * @generated
   */
  Drop createDrop();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  LMPackage getLMPackage();

} // LMFactory
