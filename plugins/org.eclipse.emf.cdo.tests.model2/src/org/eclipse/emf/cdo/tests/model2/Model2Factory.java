/*
 * Copyright (c) 2008-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package
 * @generated
 */
public interface Model2Factory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model2Factory eINSTANCE = org.eclipse.emf.cdo.tests.model2.impl.Model2FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Special Purchase Order</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Special Purchase Order</em>'.
   * @generated
   */
  SpecialPurchaseOrder createSpecialPurchaseOrder();

  /**
   * Returns a new object of class '<em>Task Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Task Container</em>'.
   * @generated
   */
  TaskContainer createTaskContainer();

  /**
   * Returns a new object of class '<em>Task</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Task</em>'.
   * @generated
   */
  Task createTask();

  /**
   * Returns a new object of class '<em>Unsettable1</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Unsettable1</em>'.
   * @generated
   */
  Unsettable1 createUnsettable1();

  /**
   * Returns a new object of class '<em>Unsettable2 With Default</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Unsettable2 With Default</em>'.
   * @generated
   */
  Unsettable2WithDefault createUnsettable2WithDefault();

  /**
   * Returns a new object of class '<em>Persistent Containment</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Persistent Containment</em>'.
   * @generated
   */
  PersistentContainment createPersistentContainment();

  /**
   * Returns a new object of class '<em>Transient Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Transient Container</em>'.
   * @generated
   */
  TransientContainer createTransientContainer();

  /**
   * Returns a new object of class '<em>Not Unsettable</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Not Unsettable</em>'.
   * @generated
   */
  NotUnsettable createNotUnsettable();

  /**
   * Returns a new object of class '<em>Not Unsettable With Default</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Not Unsettable With Default</em>'.
   * @generated
   */
  NotUnsettableWithDefault createNotUnsettableWithDefault();

  /**
   * Returns a new object of class '<em>Map Holder</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Map Holder</em>'.
   * @generated
   */
  MapHolder createMapHolder();

  /**
   * Returns a new object of class '<em>Enum List Holder</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Enum List Holder</em>'.
   * @generated
   */
  EnumListHolder createEnumListHolder();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Model2Package getModel2Package();

} // Model2Factory
