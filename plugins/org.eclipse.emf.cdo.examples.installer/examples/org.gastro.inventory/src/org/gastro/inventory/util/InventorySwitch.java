/*
 * Copyright (c) 2009-2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.ecore.EClass;
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

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 *
 * @see org.gastro.inventory.InventoryPackage
 * @generated
 */
public class InventorySwitch<T>
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static InventoryPackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public InventorySwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = InventoryPackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case InventoryPackage.STOCK:
    {
      Stock stock = (Stock)theEObject;
      T result = caseStock(stock);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.PRODUCT:
    {
      Product product = (Product)theEObject;
      T result = caseProduct(product);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.STOCK_PRODUCT:
    {
      StockProduct stockProduct = (StockProduct)theEObject;
      T result = caseStockProduct(stockProduct);
      if (result == null)
      {
        result = caseProduct(stockProduct);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.RECIPE:
    {
      Recipe recipe = (Recipe)theEObject;
      T result = caseRecipe(recipe);
      if (result == null)
      {
        result = caseProduct(recipe);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.INGREDIENT:
    {
      Ingredient ingredient = (Ingredient)theEObject;
      T result = caseIngredient(ingredient);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.MENU_CARD:
    {
      MenuCard menuCard = (MenuCard)theEObject;
      T result = caseMenuCard(menuCard);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.RESTAURANT:
    {
      Restaurant restaurant = (Restaurant)theEObject;
      T result = caseRestaurant(restaurant);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.DEPARTMENT:
    {
      Department department = (Department)theEObject;
      T result = caseDepartment(department);
      if (result == null)
      {
        result = caseStation(department);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.OFFERING:
    {
      Offering offering = (Offering)theEObject;
      T result = caseOffering(offering);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.TABLE:
    {
      Table table = (Table)theEObject;
      T result = caseTable(table);
      if (result == null)
      {
        result = caseStation(table);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.EMPLOYEE:
    {
      Employee employee = (Employee)theEObject;
      T result = caseEmployee(employee);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.STATION:
    {
      Station station = (Station)theEObject;
      T result = caseStation(station);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case InventoryPackage.SECTION:
    {
      Section section = (Section)theEObject;
      T result = caseSection(section);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Stock</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Stock</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStock(Stock object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Product</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Product</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProduct(Product object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Stock Product</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Stock Product</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStockProduct(StockProduct object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Recipe</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Recipe</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRecipe(Recipe object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ingredient</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ingredient</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIngredient(Ingredient object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Menu Card</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Menu Card</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMenuCard(MenuCard object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Restaurant</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Restaurant</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRestaurant(Restaurant object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Department</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Department</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDepartment(Department object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Offering</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Offering</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOffering(Offering object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Table</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Table</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTable(Table object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Employee</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Employee</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEmployee(Employee object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Station</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Station</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStation(Station object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Section</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Section</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSection(Section object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // InventorySwitch
