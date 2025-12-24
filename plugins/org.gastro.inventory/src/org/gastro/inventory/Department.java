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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Department</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Department#getRecipes <em>Recipes</em>}</li>
 * <li>{@link org.gastro.inventory.Department#getRestaurant <em>Restaurant</em>}</li>
 * <li>{@link org.gastro.inventory.Department#getEmployees <em>Employees</em>}</li>
 * <li>{@link org.gastro.inventory.Department#getStocks <em>Stocks</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getDepartment()
 * @model
 * @generated
 */
public interface Department extends Station
{
  /**
   * Returns the value of the '<em><b>Recipes</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Recipe}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Recipe#getDepartment <em>Department</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Recipes</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Recipes</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getDepartment_Recipes()
   * @see org.gastro.inventory.Recipe#getDepartment
   * @model opposite="department" containment="true"
   * @generated
   */
  EList<Recipe> getRecipes();

  /**
   * Returns the value of the '<em><b>Restaurant</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.gastro.inventory.Restaurant#getDepartments <em>Departments</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restaurant</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Restaurant</em>' container reference.
   * @see #setRestaurant(Restaurant)
   * @see org.gastro.inventory.InventoryPackage#getDepartment_Restaurant()
   * @see org.gastro.inventory.Restaurant#getDepartments
   * @model opposite="departments" required="true" transient="false"
   * @generated
   */
  Restaurant getRestaurant();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Department#getRestaurant <em>Restaurant</em>}' container
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Restaurant</em>' container reference.
   * @see #getRestaurant()
   * @generated
   */
  void setRestaurant(Restaurant value);

  /**
   * Returns the value of the '<em><b>Employees</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Employee}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Employee#getDepartment <em>Department</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Employees</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Employees</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getDepartment_Employees()
   * @see org.gastro.inventory.Employee#getDepartment
   * @model opposite="department" containment="true"
   * @generated
   */
  EList<Employee> getEmployees();

  /**
   * Returns the value of the '<em><b>Stocks</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Stock}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Stock#getDepartment <em>Department</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Stocks</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Stocks</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getDepartment_Stocks()
   * @see org.gastro.inventory.Stock#getDepartment
   * @model opposite="department" containment="true" required="true"
   * @generated
   */
  EList<Stock> getStocks();

} // Department
