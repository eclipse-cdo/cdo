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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Sales Order</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getCustomer <em>Customer</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getSalesOrder()
 * @model
 * @generated
 */
public interface SalesOrder extends Order
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(int)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getSalesOrder_Id()
   * @model
   * @generated
   */
  int getId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getId <em>Id</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(int value);

  /**
   * Returns the value of the '<em><b>Customer</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.examples.company.Customer#getSalesOrders <em>Sales Orders</em>}'.
   * <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Customer</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Customer</em>' reference.
   * @see #setCustomer(Customer)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getSalesOrder_Customer()
   * @see org.eclipse.emf.cdo.examples.company.Customer#getSalesOrders
   * @model opposite="salesOrders" required="true"
   * @generated
   */
  Customer getCustomer();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getCustomer <em>Customer</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Customer</em>' reference.
   * @see #getCustomer()
   * @generated
   */
  void setCustomer(Customer value);

} // SalesOrder
