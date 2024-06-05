/*
 * Copyright (c) 2007-2009, 2011-2013, 2015, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Category</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Category#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Category#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Category#getProducts <em>Products</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Category#getMainProduct <em>Main Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Category#getTopProducts <em>Top Products</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCategory()
 * @model
 * @generated
 */
public interface Category extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCategory_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Category#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Categories</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.Category}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Categories</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Categories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCategory_Categories()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Category> getCategories();

  /**
   * Returns the value of the '<em><b>Products</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.Product1}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Products</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Products</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCategory_Products()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Product1> getProducts();

  /**
   * Returns the value of the '<em><b>Main Product</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Main Product</em>' reference.
   * @see #setMainProduct(Product1)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCategory_MainProduct()
   * @model
   * @generated
   */
  Product1 getMainProduct();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Category#getMainProduct <em>Main Product</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Main Product</em>' reference.
   * @see #getMainProduct()
   * @generated
   */
  void setMainProduct(Product1 value);

  /**
   * Returns the value of the '<em><b>Top Products</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.Product1}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Top Products</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCategory_TopProducts()
   * @model
   * @generated
   */
  EList<Product1> getTopProducts();

} // Category
