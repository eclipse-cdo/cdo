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

import org.gastro.inventory.Offering;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order Detail</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.gastro.business.OrderDetail#getOrder <em>Order</em>}</li>
 *   <li>{@link org.gastro.business.OrderDetail#getOffering <em>Offering</em>}</li>
 *   <li>{@link org.gastro.business.OrderDetail#getQuantity <em>Quantity</em>}</li>
 *   <li>{@link org.gastro.business.OrderDetail#getState <em>State</em>}</li>
 *   <li>{@link org.gastro.business.OrderDetail#getPrice <em>Price</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.business.BusinessPackage#getOrderDetail()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface OrderDetail extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Order</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.gastro.business.Order#getOrderDetails <em>Order Details</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Order</em>' container reference.
   * @see #setOrder(Order)
   * @see org.gastro.business.BusinessPackage#getOrderDetail_Order()
   * @see org.gastro.business.Order#getOrderDetails
   * @model opposite="orderDetails" required="true" transient="false"
   * @generated
   */
  Order getOrder();

  /**
   * Sets the value of the '{@link org.gastro.business.OrderDetail#getOrder <em>Order</em>}' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Order</em>' container reference.
   * @see #getOrder()
   * @generated
   */
  void setOrder(Order value);

  /**
   * Returns the value of the '<em><b>Offering</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Offering</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Offering</em>' reference.
   * @see #setOffering(Offering)
   * @see org.gastro.business.BusinessPackage#getOrderDetail_Offering()
   * @model required="true"
   * @generated
   */
  Offering getOffering();

  /**
   * Sets the value of the '{@link org.gastro.business.OrderDetail#getOffering <em>Offering</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Offering</em>' reference.
   * @see #getOffering()
   * @generated
   */
  void setOffering(Offering value);

  /**
   * Returns the value of the '<em><b>Quantity</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Quantity</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Quantity</em>' attribute.
   * @see #setQuantity(int)
   * @see org.gastro.business.BusinessPackage#getOrderDetail_Quantity()
   * @model
   * @generated
   */
  int getQuantity();

  /**
   * Sets the value of the '{@link org.gastro.business.OrderDetail#getQuantity <em>Quantity</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Quantity</em>' attribute.
   * @see #getQuantity()
   * @generated
   */
  void setQuantity(int value);

  /**
   * Returns the value of the '<em><b>State</b></em>' attribute.
   * The literals are from the enumeration {@link org.gastro.business.OrderState}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>State</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>State</em>' attribute.
   * @see org.gastro.business.OrderState
   * @see #setState(OrderState)
   * @see org.gastro.business.BusinessPackage#getOrderDetail_State()
   * @model
   * @generated
   */
  OrderState getState();

  /**
   * Sets the value of the '{@link org.gastro.business.OrderDetail#getState <em>State</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>State</em>' attribute.
   * @see org.gastro.business.OrderState
   * @see #getState()
   * @generated
   */
  void setState(OrderState value);

  /**
   * Returns the value of the '<em><b>Price</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Price</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Price</em>' attribute.
   * @see org.gastro.business.BusinessPackage#getOrderDetail_Price()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  float getPrice();

} // OrderDetail
