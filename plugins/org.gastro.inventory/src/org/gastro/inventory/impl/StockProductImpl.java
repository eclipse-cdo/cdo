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

import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.Department;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.Stock;
import org.gastro.inventory.StockProduct;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Stock Product</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.StockProductImpl#getStock <em>Stock</em>}</li>
 * <li>{@link org.gastro.inventory.impl.StockProductImpl#getCost <em>Cost</em>}</li>
 * <li>{@link org.gastro.inventory.impl.StockProductImpl#getAvailable <em>Available</em>}</li>
 * <li>{@link org.gastro.inventory.impl.StockProductImpl#getOrderLimit <em>Order Limit</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class StockProductImpl extends ProductImpl implements StockProduct
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected StockProductImpl()
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
    return InventoryPackage.Literals.STOCK_PRODUCT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Stock getStock()
  {
    return (Stock)eGet(InventoryPackage.Literals.STOCK_PRODUCT__STOCK, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setStock(Stock newStock)
  {
    eSet(InventoryPackage.Literals.STOCK_PRODUCT__STOCK, newStock);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public float getCost()
  {
    return (Float)eGet(InventoryPackage.Literals.STOCK_PRODUCT__COST, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setCost(float newCost)
  {
    eSet(InventoryPackage.Literals.STOCK_PRODUCT__COST, newCost);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int getAvailable()
  {
    return (Integer)eGet(InventoryPackage.Literals.STOCK_PRODUCT__AVAILABLE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setAvailable(int newAvailable)
  {
    eSet(InventoryPackage.Literals.STOCK_PRODUCT__AVAILABLE, newAvailable);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int getOrderLimit()
  {
    return (Integer)eGet(InventoryPackage.Literals.STOCK_PRODUCT__ORDER_LIMIT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setOrderLimit(int newOrderLimit)
  {
    eSet(InventoryPackage.Literals.STOCK_PRODUCT__ORDER_LIMIT, newOrderLimit);
  }

  /**
   * TODO Should be modeled as derived EReference!
   *
   * @ADDED
   */
  @Override
  public Department getDepartment()
  {
    Stock stock = getStock();
    if (stock != null)
    {
      return stock.getDepartment();
    }

    return null;
  }

} // StockProductImpl
