/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Special Purchase Order</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getDiscountCode <em>Discount Code</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getShippingAddress <em>Shipping Address</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getSpecialPurchaseOrder()
 * @model
 * @generated
 */
public interface SpecialPurchaseOrder extends PurchaseOrder
{
  /**
   * Returns the value of the '<em><b>Discount Code</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Discount Code</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Discount Code</em>' attribute.
   * @see #setDiscountCode(String)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getSpecialPurchaseOrder_DiscountCode()
   * @model
   * @generated
   */
  String getDiscountCode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getDiscountCode <em>Discount Code</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Discount Code</em>' attribute.
   * @see #getDiscountCode()
   * @generated
   */
  void setDiscountCode(String value);

  /**
   * Returns the value of the '<em><b>Shipping Address</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Shipping Address</em>' containment reference isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Shipping Address</em>' containment reference.
   * @see #setShippingAddress(Address)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getSpecialPurchaseOrder_ShippingAddress()
   * @model containment="true"
   * @generated
   */
  Address getShippingAddress();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getShippingAddress <em>Shipping Address</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Shipping Address</em>' containment reference.
   * @see #getShippingAddress()
   * @generated
   */
  void setShippingAddress(Address value);

} // SpecialPurchaseOrder
