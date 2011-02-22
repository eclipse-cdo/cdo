/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.OrderAddress#isTestAttribute <em>Test Attribute</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrderAddress()
 * @model annotation=
 *        "teneo.jpa value='@AssociationOverride(name=\"orderDetails\", joinColumns=@JoinColumn(name=\"orderdetails_orderaddressid\"))'"
 * @generated
 */
public interface OrderAddress extends Address, Order, OrderDetail
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Test Attribute</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Test Attribute</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Test Attribute</em>' attribute.
   * @see #setTestAttribute(boolean)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrderAddress_TestAttribute()
   * @model
   * @generated
   */
  boolean isTestAttribute();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.OrderAddress#isTestAttribute
   * <em>Test Attribute</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Test Attribute</em>' attribute.
   * @see #isTestAttribute()
   * @generated
   */
  void setTestAttribute(boolean value);

} // OrderAddress
