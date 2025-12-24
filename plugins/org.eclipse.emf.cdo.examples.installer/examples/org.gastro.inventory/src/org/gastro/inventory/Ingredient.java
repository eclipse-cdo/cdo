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

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ingredient</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Ingredient#getRecipe <em>Recipe</em>}</li>
 * <li>{@link org.gastro.inventory.Ingredient#getProduct <em>Product</em>}</li>
 * <li>{@link org.gastro.inventory.Ingredient#getQuantity <em>Quantity</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getIngredient()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Ingredient extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Recipe</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Recipe#getIngredients <em>Ingredients</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Recipe</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Recipe</em>' container reference.
   * @see #setRecipe(Recipe)
   * @see org.gastro.inventory.InventoryPackage#getIngredient_Recipe()
   * @see org.gastro.inventory.Recipe#getIngredients
   * @model opposite="ingredients" required="true" transient="false"
   * @generated
   */
  Recipe getRecipe();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Ingredient#getRecipe <em>Recipe</em>}' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Recipe</em>' container reference.
   * @see #getRecipe()
   * @generated
   */
  void setRecipe(Recipe value);

  /**
   * Returns the value of the '<em><b>Product</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Product</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Product</em>' reference.
   * @see #setProduct(Product)
   * @see org.gastro.inventory.InventoryPackage#getIngredient_Product()
   * @model required="true"
   * @generated
   */
  Product getProduct();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Ingredient#getProduct <em>Product</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Product</em>' reference.
   * @see #getProduct()
   * @generated
   */
  void setProduct(Product value);

  /**
   * Returns the value of the '<em><b>Quantity</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Quantity</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Quantity</em>' attribute.
   * @see #setQuantity(int)
   * @see org.gastro.inventory.InventoryPackage#getIngredient_Quantity()
   * @model
   * @generated
   */
  int getQuantity();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Ingredient#getQuantity <em>Quantity</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Quantity</em>' attribute.
   * @see #getQuantity()
   * @generated
   */
  void setQuantity(int value);

} // Ingredient
