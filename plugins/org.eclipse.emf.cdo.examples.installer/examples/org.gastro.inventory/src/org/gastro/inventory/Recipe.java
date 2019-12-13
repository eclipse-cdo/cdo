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
package org.gastro.inventory;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Recipe</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Recipe#getIngredients <em>Ingredients</em>}</li>
 * <li>{@link org.gastro.inventory.Recipe#getDepartment <em>Department</em>}</li>
 * <li>{@link org.gastro.inventory.Recipe#getCost <em>Cost</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getRecipe()
 * @model
 * @generated
 */
public interface Recipe extends Product
{
  /**
   * Returns the value of the '<em><b>Ingredients</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Ingredient}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Ingredient#getRecipe <em>Recipe</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ingredients</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Ingredients</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getRecipe_Ingredients()
   * @see org.gastro.inventory.Ingredient#getRecipe
   * @model opposite="recipe" containment="true"
   * @generated
   */
  EList<Ingredient> getIngredients();

  @Override
  /**
   * Returns the value of the '<em><b>Department</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.gastro.inventory.Department#getRecipes <em>Recipes</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Department</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Department</em>' container reference.
   * @see #setDepartment(Department)
   * @see org.gastro.inventory.InventoryPackage#getRecipe_Department()
   * @see org.gastro.inventory.Department#getRecipes
   * @model opposite="recipes" transient="false"
   * @generated
   */
  Department getDepartment();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Recipe#getDepartment <em>Department</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Department</em>' reference.
   * @see #getDepartment()
   * @generated
   */
  void setDepartment(Department value);

  @Override
  /**
   * Returns the value of the '<em><b>Cost</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cost</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Cost</em>' attribute.
   * @see org.gastro.inventory.InventoryPackage#getRecipe_Cost()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  float getCost();

} // Recipe
