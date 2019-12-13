/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.Department;
import org.gastro.inventory.Employee;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.Recipe;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Stock;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Department</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.DepartmentImpl#getRecipes <em>Recipes</em>}</li>
 * <li>{@link org.gastro.inventory.impl.DepartmentImpl#getRestaurant <em>Restaurant</em>}</li>
 * <li>{@link org.gastro.inventory.impl.DepartmentImpl#getEmployees <em>Employees</em>}</li>
 * <li>{@link org.gastro.inventory.impl.DepartmentImpl#getStocks <em>Stocks</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DepartmentImpl extends StationImpl implements Department
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected DepartmentImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return InventoryPackage.Literals.DEPARTMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Recipe> getRecipes()
  {
    return (EList<Recipe>)eGet(InventoryPackage.Literals.DEPARTMENT__RECIPES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Restaurant getRestaurant()
  {
    return (Restaurant)eGet(InventoryPackage.Literals.DEPARTMENT__RESTAURANT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setRestaurant(Restaurant newRestaurant)
  {
    eSet(InventoryPackage.Literals.DEPARTMENT__RESTAURANT, newRestaurant);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Employee> getEmployees()
  {
    return (EList<Employee>)eGet(InventoryPackage.Literals.DEPARTMENT__EMPLOYEES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Stock> getStocks()
  {
    return (EList<Stock>)eGet(InventoryPackage.Literals.DEPARTMENT__STOCKS, true);
  }

} // DepartmentImpl
