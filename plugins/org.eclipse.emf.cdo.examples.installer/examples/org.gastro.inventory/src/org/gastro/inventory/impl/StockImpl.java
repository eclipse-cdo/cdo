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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.Department;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.Stock;
import org.gastro.inventory.StockProduct;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Stock</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.StockImpl#getProducts <em>Products</em>}</li>
 * <li>{@link org.gastro.inventory.impl.StockImpl#getName <em>Name</em>}</li>
 * <li>{@link org.gastro.inventory.impl.StockImpl#getDepartment <em>Department</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class StockImpl extends CDOObjectImpl implements Stock
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected StockImpl()
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
    return InventoryPackage.Literals.STOCK;
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
  @SuppressWarnings("unchecked")
  public EList<StockProduct> getProducts()
  {
    return (EList<StockProduct>)eGet(InventoryPackage.Literals.STOCK__PRODUCTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(InventoryPackage.Literals.STOCK__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(InventoryPackage.Literals.STOCK__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Department getDepartment()
  {
    return (Department)eGet(InventoryPackage.Literals.STOCK__DEPARTMENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setDepartment(Department newDepartment)
  {
    eSet(InventoryPackage.Literals.STOCK__DEPARTMENT, newDepartment);
  }

} // StockImpl
