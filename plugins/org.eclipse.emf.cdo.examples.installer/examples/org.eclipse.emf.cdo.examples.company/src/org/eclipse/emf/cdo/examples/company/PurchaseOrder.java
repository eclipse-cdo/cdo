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
 */
package org.eclipse.emf.cdo.examples.company;

import java.util.Date;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Purchase Order</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getSupplier <em>Supplier</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getPurchaseOrder()
 * @model
 * @generated
 */
public interface PurchaseOrder extends Order
{
  /**
   * Returns the value of the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Date</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Date</em>' attribute.
   * @see #setDate(Date)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getPurchaseOrder_Date()
   * @model
   * @generated
   */
  Date getDate();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getDate <em>Date</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Date</em>' attribute.
   * @see #getDate()
   * @generated
   */
  void setDate(Date value);

  /**
   * Returns the value of the '<em><b>Supplier</b></em>' reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.examples.company.Supplier#getPurchaseOrders <em>Purchase Orders</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Supplier</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Supplier</em>' reference.
   * @see #setSupplier(Supplier)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getPurchaseOrder_Supplier()
   * @see org.eclipse.emf.cdo.examples.company.Supplier#getPurchaseOrders
   * @model opposite="purchaseOrders" required="true"
   * @generated
   */
  Supplier getSupplier();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getSupplier <em>Supplier</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Supplier</em>' reference.
   * @see #getSupplier()
   * @generated
   */
  void setSupplier(Supplier value);

} // PurchaseOrder
