/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

import java.util.Date;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Purchase Order</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier <em>Supplier</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getPurchaseOrder()
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
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getPurchaseOrder_Date()
   * @model
   * @generated
   */
  Date getDate();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate <em>Date</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Date</em>' attribute.
   * @see #getDate()
   * @generated
   */
  void setDate(Date value);

  /**
   * Returns the value of the '<em><b>Supplier</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model1.Supplier#getPurchaseOrders <em>Purchase Orders</em>}'.
   * <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Supplier</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Supplier</em>' reference.
   * @see #setSupplier(Supplier)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getPurchaseOrder_Supplier()
   * @see org.eclipse.emf.cdo.tests.model1.Supplier#getPurchaseOrders
   * @model opposite="purchaseOrders" required="true"
   * @generated
   */
  Supplier getSupplier();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier <em>Supplier</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Supplier</em>' reference.
   * @see #getSupplier()
   * @generated
   */
  void setSupplier(Supplier value);

} // PurchaseOrder
