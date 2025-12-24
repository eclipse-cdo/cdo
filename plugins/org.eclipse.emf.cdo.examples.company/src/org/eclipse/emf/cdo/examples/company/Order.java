/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Order#getOrderDetails <em>Order Details</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getOrder()
 * @model
 * @generated
 */
public interface Order extends EObject
{
  /**
   * Returns the value of the '<em><b>Order Details</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.examples.company.OrderDetail}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.examples.company.OrderDetail#getOrder <em>Order</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' containment reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Order Details</em>' containment reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getOrder_OrderDetails()
   * @see org.eclipse.emf.cdo.examples.company.OrderDetail#getOrder
   * @model opposite="order" containment="true"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

} // Order
