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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Stock Product</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.StockProduct#getStock <em>Stock</em>}</li>
 * <li>{@link org.gastro.inventory.StockProduct#getCost <em>Cost</em>}</li>
 * <li>{@link org.gastro.inventory.StockProduct#getAvailable <em>Available</em>}</li>
 * <li>{@link org.gastro.inventory.StockProduct#getOrderLimit <em>Order Limit</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getStockProduct()
 * @model
 * @generated
 */
public interface StockProduct extends Product
{
  /**
   * Returns the value of the '<em><b>Stock</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Stock#getProducts <em>Products</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Stock</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Stock</em>' container reference.
   * @see #setStock(Stock)
   * @see org.gastro.inventory.InventoryPackage#getStockProduct_Stock()
   * @see org.gastro.inventory.Stock#getProducts
   * @model opposite="products" required="true" transient="false"
   * @generated
   */
  Stock getStock();

  /**
   * Sets the value of the '{@link org.gastro.inventory.StockProduct#getStock <em>Stock</em>}' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Stock</em>' container reference.
   * @see #getStock()
   * @generated
   */
  void setStock(Stock value);

  @Override
  /**
   * Returns the value of the '<em><b>Cost</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cost</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Cost</em>' attribute.
   * @see #setCost(float)
   * @see org.gastro.inventory.InventoryPackage#getStockProduct_Cost()
   * @model
   * @generated
   */
  float getCost();

  /**
   * Sets the value of the '{@link org.gastro.inventory.StockProduct#getCost <em>Cost</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Cost</em>' attribute.
   * @see #getCost()
   * @generated
   */
  void setCost(float value);

  /**
   * Returns the value of the '<em><b>Available</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Available</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Available</em>' attribute.
   * @see #setAvailable(int)
   * @see org.gastro.inventory.InventoryPackage#getStockProduct_Available()
   * @model
   * @generated
   */
  int getAvailable();

  /**
   * Sets the value of the '{@link org.gastro.inventory.StockProduct#getAvailable <em>Available</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Available</em>' attribute.
   * @see #getAvailable()
   * @generated
   */
  void setAvailable(int value);

  /**
   * Returns the value of the '<em><b>Order Limit</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Limit</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Order Limit</em>' attribute.
   * @see #setOrderLimit(int)
   * @see org.gastro.inventory.InventoryPackage#getStockProduct_OrderLimit()
   * @model
   * @generated
   */
  int getOrderLimit();

  /**
   * Sets the value of the '{@link org.gastro.inventory.StockProduct#getOrderLimit <em>Order Limit</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Order Limit</em>' attribute.
   * @see #getOrderLimit()
   * @generated
   */
  void setOrderLimit(int value);

} // StockProduct
