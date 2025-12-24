/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.Ingredient;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.Product;
import org.gastro.inventory.Recipe;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Ingredient</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.IngredientImpl#getRecipe <em>Recipe</em>}</li>
 * <li>{@link org.gastro.inventory.impl.IngredientImpl#getProduct <em>Product</em>}</li>
 * <li>{@link org.gastro.inventory.impl.IngredientImpl#getQuantity <em>Quantity</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IngredientImpl extends CDOObjectImpl implements Ingredient
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected IngredientImpl()
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
    return InventoryPackage.Literals.INGREDIENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Recipe getRecipe()
  {
    return (Recipe)eGet(InventoryPackage.Literals.INGREDIENT__RECIPE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setRecipe(Recipe newRecipe)
  {
    eSet(InventoryPackage.Literals.INGREDIENT__RECIPE, newRecipe);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Product getProduct()
  {
    return (Product)eGet(InventoryPackage.Literals.INGREDIENT__PRODUCT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setProduct(Product newProduct)
  {
    eSet(InventoryPackage.Literals.INGREDIENT__PRODUCT, newProduct);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int getQuantity()
  {
    return (Integer)eGet(InventoryPackage.Literals.INGREDIENT__QUANTITY, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setQuantity(int newQuantity)
  {
    eSet(InventoryPackage.Literals.INGREDIENT__QUANTITY, newQuantity);
  }

} // IngredientImpl
