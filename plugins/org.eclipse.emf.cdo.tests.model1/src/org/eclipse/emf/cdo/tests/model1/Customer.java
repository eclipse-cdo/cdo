/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Customer</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Customer#getSalesOrders <em>Sales Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Customer#getOrderByProduct <em>Order By Product</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCustomer()
 * @model
 * @generated
 */
public interface Customer extends Address
{
  /**
   * Returns the value of the '<em><b>Sales Orders</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.SalesOrder}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model1.SalesOrder#getCustomer <em>Customer</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sales Orders</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sales Orders</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCustomer_SalesOrders()
   * @see org.eclipse.emf.cdo.tests.model1.SalesOrder#getCustomer
   * @model opposite="customer"
   * @generated
   */
  EList<SalesOrder> getSalesOrders();

  /**
   * Returns the value of the '<em><b>Order By Product</b></em>' map.
   * The key is of type {@link org.eclipse.emf.cdo.tests.model1.Product1},
   * and the value is of type {@link org.eclipse.emf.cdo.tests.model1.SalesOrder},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order By Product</em>' map isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Order By Product</em>' map.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCustomer_OrderByProduct()
   * @model mapType="org.eclipse.emf.cdo.tests.model1.ProductToOrder&lt;org.eclipse.emf.cdo.tests.model1.Product1, org.eclipse.emf.cdo.tests.model1.SalesOrder&gt;"
   * @generated
   */
  EMap<Product1, SalesOrder> getOrderByProduct();

} // Customer
