/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Order#getOrderDetails <em>Order Details</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrder()
 * @model abstract="true" annotation="teneo.jpa value='@Entity(name=\"BaseOrder\")'"
 * @generated
 */
public interface Order extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Order Details</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getOrder <em>Order</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' containment reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Order Details</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrder_OrderDetails()
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getOrder
   * @model opposite="order" containment="true"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

} // Order
