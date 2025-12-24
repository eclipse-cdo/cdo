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
package org.gastro.business;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

import org.gastro.inventory.Table;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.gastro.business.Order#getBusinessDay <em>Business Day</em>}</li>
 *   <li>{@link org.gastro.business.Order#getTable <em>Table</em>}</li>
 *   <li>{@link org.gastro.business.Order#getOrderDetails <em>Order Details</em>}</li>
 *   <li>{@link org.gastro.business.Order#getNumber <em>Number</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.business.BusinessPackage#getOrder()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Order extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Business Day</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.gastro.business.BusinessDay#getOrders <em>Orders</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Business Day</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Business Day</em>' container reference.
   * @see #setBusinessDay(BusinessDay)
   * @see org.gastro.business.BusinessPackage#getOrder_BusinessDay()
   * @see org.gastro.business.BusinessDay#getOrders
   * @model opposite="orders" required="true" transient="false"
   * @generated
   */
  BusinessDay getBusinessDay();

  /**
   * Sets the value of the '{@link org.gastro.business.Order#getBusinessDay <em>Business Day</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Business Day</em>' container reference.
   * @see #getBusinessDay()
   * @generated
   */
  void setBusinessDay(BusinessDay value);

  /**
   * Returns the value of the '<em><b>Table</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Table</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Table</em>' reference.
   * @see #setTable(Table)
   * @see org.gastro.business.BusinessPackage#getOrder_Table()
   * @model required="true"
   * @generated
   */
  Table getTable();

  /**
   * Sets the value of the '{@link org.gastro.business.Order#getTable <em>Table</em>}' reference.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Table</em>' reference.
   * @see #getTable()
   * @generated
   */
  void setTable(Table value);

  /**
   * Returns the value of the '<em><b>Order Details</b></em>' containment reference list.
   * The list contents are of type {@link org.gastro.business.OrderDetail}.
   * It is bidirectional and its opposite is '{@link org.gastro.business.OrderDetail#getOrder <em>Order</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' containment reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Order Details</em>' containment reference list.
   * @see org.gastro.business.BusinessPackage#getOrder_OrderDetails()
   * @see org.gastro.business.OrderDetail#getOrder
   * @model opposite="order" containment="true" required="true"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

  /**
   * Returns the value of the '<em><b>Number</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Number</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Number</em>' attribute.
   * @see #setNumber(int)
   * @see org.gastro.business.BusinessPackage#getOrder_Number()
   * @model
   * @generated
   */
  int getNumber();

  /**
   * Sets the value of the '{@link org.gastro.business.Order#getNumber <em>Number</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Number</em>' attribute.
   * @see #getNumber()
   * @generated
   */
  void setNumber(int value);

} // Order
