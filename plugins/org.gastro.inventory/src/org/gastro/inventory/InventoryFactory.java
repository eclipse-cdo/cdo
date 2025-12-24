/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 *
 * @see org.gastro.inventory.InventoryPackage
 * @generated
 */
public interface InventoryFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  InventoryFactory eINSTANCE = org.gastro.inventory.impl.InventoryFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Stock</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Stock</em>'.
   * @generated
   */
  Stock createStock();

  /**
   * Returns a new object of class '<em>Stock Product</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Stock Product</em>'.
   * @generated
   */
  StockProduct createStockProduct();

  /**
   * Returns a new object of class '<em>Recipe</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Recipe</em>'.
   * @generated
   */
  Recipe createRecipe();

  /**
   * Returns a new object of class '<em>Ingredient</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Ingredient</em>'.
   * @generated
   */
  Ingredient createIngredient();

  /**
   * Returns a new object of class '<em>Menu Card</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Menu Card</em>'.
   * @generated
   */
  MenuCard createMenuCard();

  /**
   * Returns a new object of class '<em>Restaurant</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Restaurant</em>'.
   * @generated
   */
  Restaurant createRestaurant();

  /**
   * Returns a new object of class '<em>Department</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Department</em>'.
   * @generated
   */
  Department createDepartment();

  /**
   * Returns a new object of class '<em>Offering</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Offering</em>'.
   * @generated
   */
  Offering createOffering();

  /**
   * Returns a new object of class '<em>Table</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Table</em>'.
   * @generated
   */
  Table createTable();

  /**
   * Returns a new object of class '<em>Employee</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Employee</em>'.
   * @generated
   */
  Employee createEmployee();

  /**
   * Returns a new object of class '<em>Section</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Section</em>'.
   * @generated
   */
  Section createSection();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the package supported by this factory.
   * @generated
   */
  InventoryPackage getInventoryPackage();

} // InventoryFactory
