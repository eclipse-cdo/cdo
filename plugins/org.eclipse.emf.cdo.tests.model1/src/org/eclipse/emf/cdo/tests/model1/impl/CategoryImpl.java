/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Product1;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Category</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl#getProducts <em>Products</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl#getMainProduct <em>Main Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl#getTopProducts <em>Top Products</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CategoryImpl extends CDOObjectImpl implements Category
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CategoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model1Package.eINSTANCE.getCategory();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(Model1Package.eINSTANCE.getCategory_Name(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(Model1Package.eINSTANCE.getCategory_Name(), newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Category> getCategories()
  {
    return (EList<Category>)eGet(Model1Package.eINSTANCE.getCategory_Categories(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Product1> getProducts()
  {
    return (EList<Product1>)eGet(Model1Package.eINSTANCE.getCategory_Products(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Product1 getMainProduct()
  {
    return (Product1)eGet(Model1Package.eINSTANCE.getCategory_MainProduct(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMainProduct(Product1 newMainProduct)
  {
    eSet(Model1Package.eINSTANCE.getCategory_MainProduct(), newMainProduct);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Product1> getTopProducts()
  {
    return (EList<Product1>)eGet(Model1Package.eINSTANCE.getCategory_TopProducts(), true);
  }

} // CategoryImpl
