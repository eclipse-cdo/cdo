/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.gastro.inventory.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.gastro.inventory.Department;
import org.gastro.inventory.Employee;
import org.gastro.inventory.Ingredient;
import org.gastro.inventory.InventoryFactory;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.MenuCard;
import org.gastro.inventory.Offering;
import org.gastro.inventory.Recipe;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Section;
import org.gastro.inventory.Stock;
import org.gastro.inventory.StockProduct;
import org.gastro.inventory.Table;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class InventoryFactoryImpl extends EFactoryImpl implements InventoryFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static InventoryFactory init()
  {
    try
    {
      InventoryFactory theInventoryFactory = (InventoryFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.gastro.org/inventory/1.0");
      if (theInventoryFactory != null)
      {
        return theInventoryFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new InventoryFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public InventoryFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case InventoryPackage.STOCK:
      return createStock();
    case InventoryPackage.STOCK_PRODUCT:
      return createStockProduct();
    case InventoryPackage.RECIPE:
      return createRecipe();
    case InventoryPackage.INGREDIENT:
      return createIngredient();
    case InventoryPackage.MENU_CARD:
      return createMenuCard();
    case InventoryPackage.RESTAURANT:
      return createRestaurant();
    case InventoryPackage.DEPARTMENT:
      return createDepartment();
    case InventoryPackage.OFFERING:
      return createOffering();
    case InventoryPackage.TABLE:
      return createTable();
    case InventoryPackage.EMPLOYEE:
      return createEmployee();
    case InventoryPackage.SECTION:
      return createSection();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Stock createStock()
  {
    StockImpl stock = new StockImpl();
    return stock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public StockProduct createStockProduct()
  {
    StockProductImpl stockProduct = new StockProductImpl();
    return stockProduct;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Recipe createRecipe()
  {
    RecipeImpl recipe = new RecipeImpl();
    return recipe;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Ingredient createIngredient()
  {
    IngredientImpl ingredient = new IngredientImpl();
    return ingredient;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public MenuCard createMenuCard()
  {
    MenuCardImpl menuCard = new MenuCardImpl();
    return menuCard;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Restaurant createRestaurant()
  {
    RestaurantImpl restaurant = new RestaurantImpl();
    return restaurant;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Department createDepartment()
  {
    DepartmentImpl department = new DepartmentImpl();
    return department;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Offering createOffering()
  {
    OfferingImpl offering = new OfferingImpl();
    return offering;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Table createTable()
  {
    TableImpl table = new TableImpl();
    return table;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Employee createEmployee()
  {
    EmployeeImpl employee = new EmployeeImpl();
    return employee;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Section createSection()
  {
    SectionImpl section = new SectionImpl();
    return section;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public InventoryPackage getInventoryPackage()
  {
    return (InventoryPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static InventoryPackage getPackage()
  {
    return InventoryPackage.eINSTANCE;
  }

} // InventoryFactoryImpl
