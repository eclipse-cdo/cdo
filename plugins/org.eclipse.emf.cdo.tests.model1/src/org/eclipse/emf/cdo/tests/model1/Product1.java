/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Product</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getOrderDetails <em>Order Details</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getVat <em>Vat</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getOtherVATs <em>Other VA Ts</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Product1#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1()
 * @model
 * @generated
 */
public interface Product1 extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_Name()
   * @model
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
   * Returns the value of the '<em><b>Order Details</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.OrderDetail}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct <em>Product</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Order Details</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_OrderDetails()
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct
   * @model opposite="product"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

  /**
   * Returns the value of the '<em><b>Vat</b></em>' attribute.
   * The default value is <code>"vat15"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.model1.VAT}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Vat</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
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
   * Returns the value of the '<em><b>Other VA Ts</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.VAT}.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.model1.VAT}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Other VA Ts</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Other VA Ts</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_OtherVATs()
   * @model default="vat15"
   * @generated
   */
  EList<VAT> getOtherVATs();

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct1_Description()
   * @model transient="true"
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Product1#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

} // Product
