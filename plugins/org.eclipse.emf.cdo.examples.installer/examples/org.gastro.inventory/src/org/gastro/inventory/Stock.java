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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Stock</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Stock#getProducts <em>Products</em>}</li>
 * <li>{@link org.gastro.inventory.Stock#getName <em>Name</em>}</li>
 * <li>{@link org.gastro.inventory.Stock#getDepartment <em>Department</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getStock()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Stock extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Products</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.StockProduct}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.StockProduct#getStock <em>Stock</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Products</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Products</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getStock_Products()
   * @see org.gastro.inventory.StockProduct#getStock
   * @model opposite="stock" containment="true"
   * @generated
   */
  EList<StockProduct> getProducts();

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.gastro.inventory.InventoryPackage#getStock_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Stock#getName <em>Name</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Department</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.gastro.inventory.Department#getStocks <em>Stocks</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Department</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Department</em>' container reference.
   * @see #setDepartment(Department)
   * @see org.gastro.inventory.InventoryPackage#getStock_Department()
   * @see org.gastro.inventory.Department#getStocks
   * @model opposite="stocks" required="true" transient="false"
   * @generated
   */
  Department getDepartment();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Stock#getDepartment <em>Department</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Department</em>' container reference.
   * @see #getDepartment()
   * @generated
   */
  void setDepartment(Department value);

} // Stock
