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
package org.gastro.inventory.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import org.gastro.inventory.Department;
import org.gastro.inventory.Employee;
import org.gastro.inventory.Ingredient;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.MenuCard;
import org.gastro.inventory.Offering;
import org.gastro.inventory.Product;
import org.gastro.inventory.Recipe;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Section;
import org.gastro.inventory.Station;
import org.gastro.inventory.Stock;
import org.gastro.inventory.StockProduct;
import org.gastro.inventory.Table;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 *
 * @see org.gastro.inventory.InventoryPackage
 * @generated
 */
public class InventoryAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static InventoryPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public InventoryAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = InventoryPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   *
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected InventorySwitch<Adapter> modelSwitch = new InventorySwitch<Adapter>()
  {
    @Override
    public Adapter caseStock(Stock object)
    {
      return createStockAdapter();
    }

    @Override
    public Adapter caseProduct(Product object)
    {
      return createProductAdapter();
    }

    @Override
    public Adapter caseStockProduct(StockProduct object)
    {
      return createStockProductAdapter();
    }

    @Override
    public Adapter caseRecipe(Recipe object)
    {
      return createRecipeAdapter();
    }

    @Override
    public Adapter caseIngredient(Ingredient object)
    {
      return createIngredientAdapter();
    }

    @Override
    public Adapter caseMenuCard(MenuCard object)
    {
      return createMenuCardAdapter();
    }

    @Override
    public Adapter caseRestaurant(Restaurant object)
    {
      return createRestaurantAdapter();
    }

    @Override
    public Adapter caseDepartment(Department object)
    {
      return createDepartmentAdapter();
    }

    @Override
    public Adapter caseOffering(Offering object)
    {
      return createOfferingAdapter();
    }

    @Override
    public Adapter caseTable(Table object)
    {
      return createTableAdapter();
    }

    @Override
    public Adapter caseEmployee(Employee object)
    {
      return createEmployeeAdapter();
    }

    @Override
    public Adapter caseStation(Station object)
    {
      return createStationAdapter();
    }

    @Override
    public Adapter caseSection(Section object)
    {
      return createSectionAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Stock <em>Stock</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Stock
   * @generated
   */
  public Adapter createStockAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Product <em>Product</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Product
   * @generated
   */
  public Adapter createProductAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.StockProduct <em>Stock Product</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.StockProduct
   * @generated
   */
  public Adapter createStockProductAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Recipe <em>Recipe</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Recipe
   * @generated
   */
  public Adapter createRecipeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Ingredient <em>Ingredient</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Ingredient
   * @generated
   */
  public Adapter createIngredientAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.MenuCard <em>Menu Card</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.MenuCard
   * @generated
   */
  public Adapter createMenuCardAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Restaurant <em>Restaurant</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Restaurant
   * @generated
   */
  public Adapter createRestaurantAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Department <em>Department</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Department
   * @generated
   */
  public Adapter createDepartmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Offering <em>Offering</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Offering
   * @generated
   */
  public Adapter createOfferingAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Table <em>Table</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Table
   * @generated
   */
  public Adapter createTableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Employee <em>Employee</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Employee
   * @generated
   */
  public Adapter createEmployeeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Station <em>Station</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Station
   * @generated
   */
  public Adapter createStationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.gastro.inventory.Section <em>Section</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.gastro.inventory.Section
   * @generated
   */
  public Adapter createSectionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // InventoryAdapterFactory
