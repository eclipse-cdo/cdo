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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Product</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getOrderDetails <em>Order Details</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getVat <em>Vat</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1()
 * @model
 * @generated
 */
public interface Product1 extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_Name()
   * @model annotation="teneo.jpa value='@Id'"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Product1#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Order Details</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct <em>Product</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Order Details</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_OrderDetails()
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct
   * @model opposite="product"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

  /**
   * Returns the value of the '<em><b>Vat</b></em>' attribute. The default value is <code>"vat15"</code>. The literals
   * are from the enumeration {@link org.eclipse.emf.cdo.tests.model1.VAT}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Vat</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Vat</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see #setVat(VAT)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_Vat()
   * @model default="vat15"
   * @generated
   */
  VAT getVat();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Product1#getVat <em>Vat</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Vat</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see #getVat()
   * @generated
   */
  void setVat(VAT value);

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_Description()
   * @model transient="true"
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Product1#getDescription <em>Description</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

} // Product
