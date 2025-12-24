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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see org.gastro.inventory.InventoryFactory
 * @model kind="package"
 * @generated
 */
public interface InventoryPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "inventory";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "http://www.gastro.org/inventory/1.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "inventory";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  InventoryPackage eINSTANCE = org.gastro.inventory.impl.InventoryPackageImpl.init();

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.StockImpl <em>Stock</em>}' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.StockImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getStock()
   * @generated
   */
  int STOCK = 0;

  /**
   * The feature id for the '<em><b>Products</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK__PRODUCTS = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK__NAME = 1;

  /**
   * The feature id for the '<em><b>Department</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int STOCK__DEPARTMENT = 2;

  /**
   * The number of structural features of the '<em>Stock</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.ProductImpl <em>Product</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.ProductImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getProduct()
   * @generated
   */
  int PRODUCT = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PRODUCT__NAME = 0;

  /**
   * The number of structural features of the '<em>Product</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PRODUCT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.StockProductImpl <em>Stock Product</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.StockProductImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getStockProduct()
   * @generated
   */
  int STOCK_PRODUCT = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK_PRODUCT__NAME = PRODUCT__NAME;

  /**
   * The feature id for the '<em><b>Stock</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK_PRODUCT__STOCK = PRODUCT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Cost</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK_PRODUCT__COST = PRODUCT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Available</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK_PRODUCT__AVAILABLE = PRODUCT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Order Limit</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STOCK_PRODUCT__ORDER_LIMIT = PRODUCT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Stock Product</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int STOCK_PRODUCT_FEATURE_COUNT = PRODUCT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.RecipeImpl <em>Recipe</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.RecipeImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getRecipe()
   * @generated
   */
  int RECIPE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RECIPE__NAME = PRODUCT__NAME;

  /**
   * The feature id for the '<em><b>Ingredients</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RECIPE__INGREDIENTS = PRODUCT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Department</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int RECIPE__DEPARTMENT = PRODUCT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Cost</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RECIPE__COST = PRODUCT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Recipe</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RECIPE_FEATURE_COUNT = PRODUCT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.IngredientImpl <em>Ingredient</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.IngredientImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getIngredient()
   * @generated
   */
  int INGREDIENT = 4;

  /**
   * The feature id for the '<em><b>Recipe</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int INGREDIENT__RECIPE = 0;

  /**
   * The feature id for the '<em><b>Product</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int INGREDIENT__PRODUCT = 1;

  /**
   * The feature id for the '<em><b>Quantity</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int INGREDIENT__QUANTITY = 2;

  /**
   * The number of structural features of the '<em>Ingredient</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int INGREDIENT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.MenuCardImpl <em>Menu Card</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.MenuCardImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getMenuCard()
   * @generated
   */
  int MENU_CARD = 5;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MENU_CARD__TITLE = 0;

  /**
   * The feature id for the '<em><b>Restaurant</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int MENU_CARD__RESTAURANT = 1;

  /**
   * The feature id for the '<em><b>Sections</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MENU_CARD__SECTIONS = 2;

  /**
   * The number of structural features of the '<em>Menu Card</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MENU_CARD_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.RestaurantImpl <em>Restaurant</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.RestaurantImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getRestaurant()
   * @generated
   */
  int RESTAURANT = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RESTAURANT__NAME = 0;

  /**
   * The feature id for the '<em><b>Departments</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RESTAURANT__DEPARTMENTS = 1;

  /**
   * The feature id for the '<em><b>Menu Cards</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RESTAURANT__MENU_CARDS = 2;

  /**
   * The feature id for the '<em><b>Tables</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RESTAURANT__TABLES = 3;

  /**
   * The feature id for the '<em><b>Stations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RESTAURANT__STATIONS = 4;

  /**
   * The number of structural features of the '<em>Restaurant</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int RESTAURANT_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.StationImpl <em>Station</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.StationImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getStation()
   * @generated
   */
  int STATION = 11;

  /**
   * The feature id for the '<em><b>Station ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STATION__STATION_ID = 0;

  /**
   * The number of structural features of the '<em>Station</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STATION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.DepartmentImpl <em>Department</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.DepartmentImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getDepartment()
   * @generated
   */
  int DEPARTMENT = 7;

  /**
   * The feature id for the '<em><b>Station ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPARTMENT__STATION_ID = STATION__STATION_ID;

  /**
   * The feature id for the '<em><b>Recipes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPARTMENT__RECIPES = STATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Restaurant</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int DEPARTMENT__RESTAURANT = STATION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Employees</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPARTMENT__EMPLOYEES = STATION_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Stocks</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPARTMENT__STOCKS = STATION_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Department</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPARTMENT_FEATURE_COUNT = STATION_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.OfferingImpl <em>Offering</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.OfferingImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getOffering()
   * @generated
   */
  int OFFERING = 8;

  /**
   * The feature id for the '<em><b>Product</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OFFERING__PRODUCT = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OFFERING__NAME = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OFFERING__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>Price</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OFFERING__PRICE = 3;

  /**
   * The feature id for the '<em><b>Section</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OFFERING__SECTION = 4;

  /**
   * The number of structural features of the '<em>Offering</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OFFERING_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.TableImpl <em>Table</em>}' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.TableImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getTable()
   * @generated
   */
  int TABLE = 9;

  /**
   * The feature id for the '<em><b>Station ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int TABLE__STATION_ID = STATION__STATION_ID;

  /**
   * The feature id for the '<em><b>Seats</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int TABLE__SEATS = STATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Restaurant</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int TABLE__RESTAURANT = STATION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Table</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int TABLE_FEATURE_COUNT = STATION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.EmployeeImpl <em>Employee</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.EmployeeImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getEmployee()
   * @generated
   */
  int EMPLOYEE = 10;

  /**
   * The feature id for the '<em><b>Department</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int EMPLOYEE__DEPARTMENT = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int EMPLOYEE__NAME = 1;

  /**
   * The number of structural features of the '<em>Employee</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int EMPLOYEE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.gastro.inventory.impl.SectionImpl <em>Section</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.inventory.impl.SectionImpl
   * @see org.gastro.inventory.impl.InventoryPackageImpl#getSection()
   * @generated
   */
  int SECTION = 12;

  /**
   * The feature id for the '<em><b>Menu Card</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int SECTION__MENU_CARD = 0;

  /**
   * The feature id for the '<em><b>Offerings</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SECTION__OFFERINGS = 1;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SECTION__TITLE = 2;

  /**
   * The feature id for the '<em><b>Text</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SECTION__TEXT = 3;

  /**
   * The number of structural features of the '<em>Section</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SECTION_FEATURE_COUNT = 4;

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Stock <em>Stock</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for class '<em>Stock</em>'.
   * @see org.gastro.inventory.Stock
   * @generated
   */
  EClass getStock();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Stock#getProducts
   * <em>Products</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Products</em>'.
   * @see org.gastro.inventory.Stock#getProducts()
   * @see #getStock()
   * @generated
   */
  EReference getStock_Products();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Stock#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.gastro.inventory.Stock#getName()
   * @see #getStock()
   * @generated
   */
  EAttribute getStock_Name();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Stock#getDepartment
   * <em>Department</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Department</em>'.
   * @see org.gastro.inventory.Stock#getDepartment()
   * @see #getStock()
   * @generated
   */
  EReference getStock_Department();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Product <em>Product</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Product</em>'.
   * @see org.gastro.inventory.Product
   * @generated
   */
  EClass getProduct();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Product#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.gastro.inventory.Product#getName()
   * @see #getProduct()
   * @generated
   */
  EAttribute getProduct_Name();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.StockProduct <em>Stock Product</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Stock Product</em>'.
   * @see org.gastro.inventory.StockProduct
   * @generated
   */
  EClass getStockProduct();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.StockProduct#getStock
   * <em>Stock</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Stock</em>'.
   * @see org.gastro.inventory.StockProduct#getStock()
   * @see #getStockProduct()
   * @generated
   */
  EReference getStockProduct_Stock();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.StockProduct#getCost <em>Cost</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Cost</em>'.
   * @see org.gastro.inventory.StockProduct#getCost()
   * @see #getStockProduct()
   * @generated
   */
  EAttribute getStockProduct_Cost();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.StockProduct#getAvailable
   * <em>Available</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Available</em>'.
   * @see org.gastro.inventory.StockProduct#getAvailable()
   * @see #getStockProduct()
   * @generated
   */
  EAttribute getStockProduct_Available();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.StockProduct#getOrderLimit
   * <em>Order Limit</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Order Limit</em>'.
   * @see org.gastro.inventory.StockProduct#getOrderLimit()
   * @see #getStockProduct()
   * @generated
   */
  EAttribute getStockProduct_OrderLimit();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Recipe <em>Recipe</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Recipe</em>'.
   * @see org.gastro.inventory.Recipe
   * @generated
   */
  EClass getRecipe();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Recipe#getIngredients
   * <em>Ingredients</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Ingredients</em>'.
   * @see org.gastro.inventory.Recipe#getIngredients()
   * @see #getRecipe()
   * @generated
   */
  EReference getRecipe_Ingredients();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Recipe#getDepartment
   * <em>Department</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Department</em>'.
   * @see org.gastro.inventory.Recipe#getDepartment()
   * @see #getRecipe()
   * @generated
   */
  EReference getRecipe_Department();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Recipe#getCost <em>Cost</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Cost</em>'.
   * @see org.gastro.inventory.Recipe#getCost()
   * @see #getRecipe()
   * @generated
   */
  EAttribute getRecipe_Cost();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Ingredient <em>Ingredient</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Ingredient</em>'.
   * @see org.gastro.inventory.Ingredient
   * @generated
   */
  EClass getIngredient();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Ingredient#getRecipe
   * <em>Recipe</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Recipe</em>'.
   * @see org.gastro.inventory.Ingredient#getRecipe()
   * @see #getIngredient()
   * @generated
   */
  EReference getIngredient_Recipe();

  /**
   * Returns the meta object for the reference '{@link org.gastro.inventory.Ingredient#getProduct <em>Product</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Product</em>'.
   * @see org.gastro.inventory.Ingredient#getProduct()
   * @see #getIngredient()
   * @generated
   */
  EReference getIngredient_Product();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Ingredient#getQuantity <em>Quantity</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Quantity</em>'.
   * @see org.gastro.inventory.Ingredient#getQuantity()
   * @see #getIngredient()
   * @generated
   */
  EAttribute getIngredient_Quantity();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.MenuCard <em>Menu Card</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Menu Card</em>'.
   * @see org.gastro.inventory.MenuCard
   * @generated
   */
  EClass getMenuCard();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.MenuCard#getTitle <em>Title</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see org.gastro.inventory.MenuCard#getTitle()
   * @see #getMenuCard()
   * @generated
   */
  EAttribute getMenuCard_Title();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.MenuCard#getRestaurant
   * <em>Restaurant</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Restaurant</em>'.
   * @see org.gastro.inventory.MenuCard#getRestaurant()
   * @see #getMenuCard()
   * @generated
   */
  EReference getMenuCard_Restaurant();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.MenuCard#getSections
   * <em>Sections</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Sections</em>'.
   * @see org.gastro.inventory.MenuCard#getSections()
   * @see #getMenuCard()
   * @generated
   */
  EReference getMenuCard_Sections();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Restaurant <em>Restaurant</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Restaurant</em>'.
   * @see org.gastro.inventory.Restaurant
   * @generated
   */
  EClass getRestaurant();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Restaurant#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.gastro.inventory.Restaurant#getName()
   * @see #getRestaurant()
   * @generated
   */
  EAttribute getRestaurant_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Restaurant#getDepartments
   * <em>Departments</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Departments</em>'.
   * @see org.gastro.inventory.Restaurant#getDepartments()
   * @see #getRestaurant()
   * @generated
   */
  EReference getRestaurant_Departments();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Restaurant#getMenuCards
   * <em>Menu Cards</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Menu Cards</em>'.
   * @see org.gastro.inventory.Restaurant#getMenuCards()
   * @see #getRestaurant()
   * @generated
   */
  EReference getRestaurant_MenuCards();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Restaurant#getTables
   * <em>Tables</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Tables</em>'.
   * @see org.gastro.inventory.Restaurant#getTables()
   * @see #getRestaurant()
   * @generated
   */
  EReference getRestaurant_Tables();

  /**
   * Returns the meta object for the reference list '{@link org.gastro.inventory.Restaurant#getStations
   * <em>Stations</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Stations</em>'.
   * @see org.gastro.inventory.Restaurant#getStations()
   * @see #getRestaurant()
   * @generated
   */
  EReference getRestaurant_Stations();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Department <em>Department</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Department</em>'.
   * @see org.gastro.inventory.Department
   * @generated
   */
  EClass getDepartment();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Department#getRecipes
   * <em>Recipes</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Recipes</em>'.
   * @see org.gastro.inventory.Department#getRecipes()
   * @see #getDepartment()
   * @generated
   */
  EReference getDepartment_Recipes();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Department#getRestaurant
   * <em>Restaurant</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Restaurant</em>'.
   * @see org.gastro.inventory.Department#getRestaurant()
   * @see #getDepartment()
   * @generated
   */
  EReference getDepartment_Restaurant();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Department#getEmployees
   * <em>Employees</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Employees</em>'.
   * @see org.gastro.inventory.Department#getEmployees()
   * @see #getDepartment()
   * @generated
   */
  EReference getDepartment_Employees();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Department#getStocks
   * <em>Stocks</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Stocks</em>'.
   * @see org.gastro.inventory.Department#getStocks()
   * @see #getDepartment()
   * @generated
   */
  EReference getDepartment_Stocks();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Offering <em>Offering</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Offering</em>'.
   * @see org.gastro.inventory.Offering
   * @generated
   */
  EClass getOffering();

  /**
   * Returns the meta object for the reference '{@link org.gastro.inventory.Offering#getProduct <em>Product</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Product</em>'.
   * @see org.gastro.inventory.Offering#getProduct()
   * @see #getOffering()
   * @generated
   */
  EReference getOffering_Product();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Offering#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.gastro.inventory.Offering#getName()
   * @see #getOffering()
   * @generated
   */
  EAttribute getOffering_Name();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Offering#getDescription
   * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.gastro.inventory.Offering#getDescription()
   * @see #getOffering()
   * @generated
   */
  EAttribute getOffering_Description();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Offering#getPrice <em>Price</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Price</em>'.
   * @see org.gastro.inventory.Offering#getPrice()
   * @see #getOffering()
   * @generated
   */
  EAttribute getOffering_Price();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Offering#getSection
   * <em>Section</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Section</em>'.
   * @see org.gastro.inventory.Offering#getSection()
   * @see #getOffering()
   * @generated
   */
  EReference getOffering_Section();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Table <em>Table</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for class '<em>Table</em>'.
   * @see org.gastro.inventory.Table
   * @generated
   */
  EClass getTable();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Table#getSeats <em>Seats</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Seats</em>'.
   * @see org.gastro.inventory.Table#getSeats()
   * @see #getTable()
   * @generated
   */
  EAttribute getTable_Seats();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Table#getRestaurant
   * <em>Restaurant</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Restaurant</em>'.
   * @see org.gastro.inventory.Table#getRestaurant()
   * @see #getTable()
   * @generated
   */
  EReference getTable_Restaurant();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Employee <em>Employee</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Employee</em>'.
   * @see org.gastro.inventory.Employee
   * @generated
   */
  EClass getEmployee();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Employee#getDepartment
   * <em>Department</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Department</em>'.
   * @see org.gastro.inventory.Employee#getDepartment()
   * @see #getEmployee()
   * @generated
   */
  EReference getEmployee_Department();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Employee#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.gastro.inventory.Employee#getName()
   * @see #getEmployee()
   * @generated
   */
  EAttribute getEmployee_Name();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Station <em>Station</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Station</em>'.
   * @see org.gastro.inventory.Station
   * @generated
   */
  EClass getStation();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Station#getStationID <em>Station ID</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Station ID</em>'.
   * @see org.gastro.inventory.Station#getStationID()
   * @see #getStation()
   * @generated
   */
  EAttribute getStation_StationID();

  /**
   * Returns the meta object for class '{@link org.gastro.inventory.Section <em>Section</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Section</em>'.
   * @see org.gastro.inventory.Section
   * @generated
   */
  EClass getSection();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.inventory.Section#getMenuCard
   * <em>Menu Card</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Menu Card</em>'.
   * @see org.gastro.inventory.Section#getMenuCard()
   * @see #getSection()
   * @generated
   */
  EReference getSection_MenuCard();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.inventory.Section#getOfferings
   * <em>Offerings</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Offerings</em>'.
   * @see org.gastro.inventory.Section#getOfferings()
   * @see #getSection()
   * @generated
   */
  EReference getSection_Offerings();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Section#getTitle <em>Title</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see org.gastro.inventory.Section#getTitle()
   * @see #getSection()
   * @generated
   */
  EAttribute getSection_Title();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.inventory.Section#getText <em>Text</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Text</em>'.
   * @see org.gastro.inventory.Section#getText()
   * @see #getSection()
   * @generated
   */
  EAttribute getSection_Text();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  InventoryFactory getInventoryFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   *
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.StockImpl <em>Stock</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.StockImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getStock()
     * @generated
     */
    EClass STOCK = eINSTANCE.getStock();

    /**
     * The meta object literal for the '<em><b>Products</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference STOCK__PRODUCTS = eINSTANCE.getStock_Products();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute STOCK__NAME = eINSTANCE.getStock_Name();

    /**
     * The meta object literal for the '<em><b>Department</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference STOCK__DEPARTMENT = eINSTANCE.getStock_Department();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.ProductImpl <em>Product</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.ProductImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getProduct()
     * @generated
     */
    EClass PRODUCT = eINSTANCE.getProduct();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute PRODUCT__NAME = eINSTANCE.getProduct_Name();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.StockProductImpl <em>Stock Product</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.StockProductImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getStockProduct()
     * @generated
     */
    EClass STOCK_PRODUCT = eINSTANCE.getStockProduct();

    /**
     * The meta object literal for the '<em><b>Stock</b></em>' container reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference STOCK_PRODUCT__STOCK = eINSTANCE.getStockProduct_Stock();

    /**
     * The meta object literal for the '<em><b>Cost</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute STOCK_PRODUCT__COST = eINSTANCE.getStockProduct_Cost();

    /**
     * The meta object literal for the '<em><b>Available</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute STOCK_PRODUCT__AVAILABLE = eINSTANCE.getStockProduct_Available();

    /**
     * The meta object literal for the '<em><b>Order Limit</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute STOCK_PRODUCT__ORDER_LIMIT = eINSTANCE.getStockProduct_OrderLimit();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.RecipeImpl <em>Recipe</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.RecipeImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getRecipe()
     * @generated
     */
    EClass RECIPE = eINSTANCE.getRecipe();

    /**
     * The meta object literal for the '<em><b>Ingredients</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference RECIPE__INGREDIENTS = eINSTANCE.getRecipe_Ingredients();

    /**
     * The meta object literal for the '<em><b>Department</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference RECIPE__DEPARTMENT = eINSTANCE.getRecipe_Department();

    /**
     * The meta object literal for the '<em><b>Cost</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute RECIPE__COST = eINSTANCE.getRecipe_Cost();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.IngredientImpl <em>Ingredient</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.IngredientImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getIngredient()
     * @generated
     */
    EClass INGREDIENT = eINSTANCE.getIngredient();

    /**
     * The meta object literal for the '<em><b>Recipe</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference INGREDIENT__RECIPE = eINSTANCE.getIngredient_Recipe();

    /**
     * The meta object literal for the '<em><b>Product</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference INGREDIENT__PRODUCT = eINSTANCE.getIngredient_Product();

    /**
     * The meta object literal for the '<em><b>Quantity</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute INGREDIENT__QUANTITY = eINSTANCE.getIngredient_Quantity();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.MenuCardImpl <em>Menu Card</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.MenuCardImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getMenuCard()
     * @generated
     */
    EClass MENU_CARD = eINSTANCE.getMenuCard();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute MENU_CARD__TITLE = eINSTANCE.getMenuCard_Title();

    /**
     * The meta object literal for the '<em><b>Restaurant</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference MENU_CARD__RESTAURANT = eINSTANCE.getMenuCard_Restaurant();

    /**
     * The meta object literal for the '<em><b>Sections</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference MENU_CARD__SECTIONS = eINSTANCE.getMenuCard_Sections();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.RestaurantImpl <em>Restaurant</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.RestaurantImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getRestaurant()
     * @generated
     */
    EClass RESTAURANT = eINSTANCE.getRestaurant();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute RESTAURANT__NAME = eINSTANCE.getRestaurant_Name();

    /**
     * The meta object literal for the '<em><b>Departments</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference RESTAURANT__DEPARTMENTS = eINSTANCE.getRestaurant_Departments();

    /**
     * The meta object literal for the '<em><b>Menu Cards</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference RESTAURANT__MENU_CARDS = eINSTANCE.getRestaurant_MenuCards();

    /**
     * The meta object literal for the '<em><b>Tables</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference RESTAURANT__TABLES = eINSTANCE.getRestaurant_Tables();

    /**
     * The meta object literal for the '<em><b>Stations</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference RESTAURANT__STATIONS = eINSTANCE.getRestaurant_Stations();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.DepartmentImpl <em>Department</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.DepartmentImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getDepartment()
     * @generated
     */
    EClass DEPARTMENT = eINSTANCE.getDepartment();

    /**
     * The meta object literal for the '<em><b>Recipes</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference DEPARTMENT__RECIPES = eINSTANCE.getDepartment_Recipes();

    /**
     * The meta object literal for the '<em><b>Restaurant</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference DEPARTMENT__RESTAURANT = eINSTANCE.getDepartment_Restaurant();

    /**
     * The meta object literal for the '<em><b>Employees</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference DEPARTMENT__EMPLOYEES = eINSTANCE.getDepartment_Employees();

    /**
     * The meta object literal for the '<em><b>Stocks</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference DEPARTMENT__STOCKS = eINSTANCE.getDepartment_Stocks();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.OfferingImpl <em>Offering</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.OfferingImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getOffering()
     * @generated
     */
    EClass OFFERING = eINSTANCE.getOffering();

    /**
     * The meta object literal for the '<em><b>Product</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference OFFERING__PRODUCT = eINSTANCE.getOffering_Product();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute OFFERING__NAME = eINSTANCE.getOffering_Name();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute OFFERING__DESCRIPTION = eINSTANCE.getOffering_Description();

    /**
     * The meta object literal for the '<em><b>Price</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute OFFERING__PRICE = eINSTANCE.getOffering_Price();

    /**
     * The meta object literal for the '<em><b>Section</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference OFFERING__SECTION = eINSTANCE.getOffering_Section();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.TableImpl <em>Table</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.TableImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getTable()
     * @generated
     */
    EClass TABLE = eINSTANCE.getTable();

    /**
     * The meta object literal for the '<em><b>Seats</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute TABLE__SEATS = eINSTANCE.getTable_Seats();

    /**
     * The meta object literal for the '<em><b>Restaurant</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference TABLE__RESTAURANT = eINSTANCE.getTable_Restaurant();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.EmployeeImpl <em>Employee</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.EmployeeImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getEmployee()
     * @generated
     */
    EClass EMPLOYEE = eINSTANCE.getEmployee();

    /**
     * The meta object literal for the '<em><b>Department</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference EMPLOYEE__DEPARTMENT = eINSTANCE.getEmployee_Department();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute EMPLOYEE__NAME = eINSTANCE.getEmployee_Name();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.StationImpl <em>Station</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.StationImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getStation()
     * @generated
     */
    EClass STATION = eINSTANCE.getStation();

    /**
     * The meta object literal for the '<em><b>Station ID</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute STATION__STATION_ID = eINSTANCE.getStation_StationID();

    /**
     * The meta object literal for the '{@link org.gastro.inventory.impl.SectionImpl <em>Section</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.inventory.impl.SectionImpl
     * @see org.gastro.inventory.impl.InventoryPackageImpl#getSection()
     * @generated
     */
    EClass SECTION = eINSTANCE.getSection();

    /**
     * The meta object literal for the '<em><b>Menu Card</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    EReference SECTION__MENU_CARD = eINSTANCE.getSection_MenuCard();

    /**
     * The meta object literal for the '<em><b>Offerings</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference SECTION__OFFERINGS = eINSTANCE.getSection_Offerings();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute SECTION__TITLE = eINSTANCE.getSection_Title();

    /**
     * The meta object literal for the '<em><b>Text</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute SECTION__TEXT = eINSTANCE.getSection_Text();

  }

} // InventoryPackage
