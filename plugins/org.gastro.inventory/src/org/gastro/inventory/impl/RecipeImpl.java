/*
 * Copyright (c) 2009-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.Department;
import org.gastro.inventory.Ingredient;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.Recipe;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Recipe</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.RecipeImpl#getIngredients <em>Ingredients</em>}</li>
 * <li>{@link org.gastro.inventory.impl.RecipeImpl#getDepartment <em>Department</em>}</li>
 * <li>{@link org.gastro.inventory.impl.RecipeImpl#getCost <em>Cost</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RecipeImpl extends ProductImpl implements Recipe
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected RecipeImpl()
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
    return InventoryPackage.Literals.RECIPE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Ingredient> getIngredients()
  {
    return (EList<Ingredient>)eGet(InventoryPackage.Literals.RECIPE__INGREDIENTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Department getDepartment()
  {
    return (Department)eGet(InventoryPackage.Literals.RECIPE__DEPARTMENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setDepartment(Department newDepartment)
  {
    eSet(InventoryPackage.Literals.RECIPE__DEPARTMENT, newDepartment);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public float getCost()
  {
    float cost = 0f;
    for (Ingredient ingredient : getIngredients())
    {
      cost += ingredient.getQuantity() * ingredient.getProduct().getCost();
    }

    return cost;
  }

  // /**
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @generated
  // */
  // public float getCost()
  // {
  // return (Float)eGet(InventoryPackage.Literals.RECIPE__COST, true);
  // }

  // /**
  // * @ADDED
  // */
  // @Override
  // public float getCost()
  // {
  // float cost = 0f;
  // for (Ingredient ingredient : getIngredients())
  // {
  // cost += ingredient.getQuantity() * ingredient.getProduct().getCost();
  // }
  //
  // return cost;
  // }

} // RecipeImpl
