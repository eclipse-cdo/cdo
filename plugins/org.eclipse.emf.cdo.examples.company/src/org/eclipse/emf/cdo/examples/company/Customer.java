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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Customer</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Customer#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCustomer()
 * @model
 * @generated
 */
public interface Customer extends Addressable
{
  /**
   * Returns the value of the '<em><b>Sales Orders</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.examples.company.SalesOrder}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getCustomer <em>Customer</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sales Orders</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sales Orders</em>' reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCustomer_SalesOrders()
   * @see org.eclipse.emf.cdo.examples.company.SalesOrder#getCustomer
   * @model opposite="customer"
   * @generated
   */
  EList<SalesOrder> getSalesOrders();

} // Customer
