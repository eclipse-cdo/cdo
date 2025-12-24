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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Supplier</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Supplier#getPurchaseOrders <em>Purchase Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.Supplier#isPreferred <em>Preferred</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getSupplier()
 * @model
 * @generated
 */
public interface Supplier extends Addressable
{
  /**
   * Returns the value of the '<em><b>Purchase Orders</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.examples.company.PurchaseOrder}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getSupplier <em>Supplier</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Purchase Orders</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Purchase Orders</em>' reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getSupplier_PurchaseOrders()
   * @see org.eclipse.emf.cdo.examples.company.PurchaseOrder#getSupplier
   * @model opposite="supplier"
   * @generated
   */
  EList<PurchaseOrder> getPurchaseOrders();

  /**
   * Returns the value of the '<em><b>Preferred</b></em>' attribute. The default value is <code>"true"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Preferred</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Preferred</em>' attribute.
   * @see #setPreferred(boolean)
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getSupplier_Preferred()
   * @model default="true"
   * @generated
   */
  boolean isPreferred();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.examples.company.Supplier#isPreferred <em>Preferred</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Preferred</em>' attribute.
   * @see #isPreferred()
   * @generated
   */
  void setPreferred(boolean value);

} // Supplier
