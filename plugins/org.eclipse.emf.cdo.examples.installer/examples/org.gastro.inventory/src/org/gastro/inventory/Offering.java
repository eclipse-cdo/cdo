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
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Offering</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Offering#getProduct <em>Product</em>}</li>
 * <li>{@link org.gastro.inventory.Offering#getName <em>Name</em>}</li>
 * <li>{@link org.gastro.inventory.Offering#getDescription <em>Description</em>}</li>
 * <li>{@link org.gastro.inventory.Offering#getPrice <em>Price</em>}</li>
 * <li>{@link org.gastro.inventory.Offering#getSection <em>Section</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getOffering()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Offering extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Product</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Product</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Product</em>' reference.
   * @see #setProduct(Product)
   * @see org.gastro.inventory.InventoryPackage#getOffering_Product()
   * @model required="true"
   * @generated
   */
  Product getProduct();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Offering#getProduct <em>Product</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Product</em>' reference.
   * @see #getProduct()
   * @generated
   */
  void setProduct(Product value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.gastro.inventory.InventoryPackage#getOffering_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Offering#getName <em>Name</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

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
   * @see org.gastro.inventory.InventoryPackage#getOffering_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Offering#getDescription <em>Description</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>Price</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Price</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Price</em>' attribute.
   * @see #setPrice(float)
   * @see org.gastro.inventory.InventoryPackage#getOffering_Price()
   * @model
   * @generated
   */
  float getPrice();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Offering#getPrice <em>Price</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Price</em>' attribute.
   * @see #getPrice()
   * @generated
   */
  void setPrice(float value);

  /**
   * Returns the value of the '<em><b>Section</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Section#getOfferings <em>Offerings</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Section</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Section</em>' container reference.
   * @see #setSection(Section)
   * @see org.gastro.inventory.InventoryPackage#getOffering_Section()
   * @see org.gastro.inventory.Section#getOfferings
   * @model opposite="offerings" required="true" transient="false"
   * @generated
   */
  Section getSection();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Offering#getSection <em>Section</em>}' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Section</em>' container reference.
   * @see #getSection()
   * @generated
   */
  void setSection(Section value);

} // Offering
