/*
 * Copyright (c) 2009-2012, 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Product</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Product#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Product#getVat <em>Vat</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Product#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Product#getPrice <em>Price</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getProduct()
 * @model
 * @generated
 */
public interface Product extends EObject
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
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getProduct_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.Product#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Vat</b></em>' attribute.
   * The default value is <code>"vat15"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.examples.company.VAT}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Vat</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Vat</em>' attribute.
   * @see org.eclipse.emf.cdo.examples.company.VAT
   * @see #setVat(VAT)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getProduct_Vat()
   * @model default="vat15"
   * @generated
   */
  VAT getVat();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.Product#getVat <em>Vat</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Vat</em>' attribute.
   * @see org.eclipse.emf.cdo.examples.company.VAT
   * @see #getVat()
   * @generated
   */
  void setVat(VAT value);

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
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getProduct_Description()
   * @model transient="true"
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.Product#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>Price</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Price</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   *
   * @since 4.0 <!-- end-user-doc -->
   * @return the value of the '<em>Price</em>' attribute.
   * @see #setPrice(float)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getProduct_Price()
   * @model
   * @generated
   */
  float getPrice();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.Product#getPrice <em>Price</em>}' attribute.
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @param value the new value of the '<em>Price</em>' attribute.
   * @see #getPrice()
   * @generated
   */
  void setPrice(float value);

} // Product
