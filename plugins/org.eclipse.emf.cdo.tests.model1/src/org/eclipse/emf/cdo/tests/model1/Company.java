/*
 * Copyright (c) 2007-2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Company</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getSuppliers <em>Suppliers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getCustomers <em>Customers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getPurchaseOrders <em>Purchase Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany()
 * @model
 * @generated
 */
public interface Company extends Address
{
  /**
   * Returns the value of the '<em><b>Categories</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.Category}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Categories</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Categories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_Categories()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Category> getCategories();

  /**
   * Returns the value of the '<em><b>Suppliers</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.Supplier}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Suppliers</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Suppliers</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_Suppliers()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Supplier> getSuppliers();

  /**
   * Returns the value of the '<em><b>Purchase Orders</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Purchase Orders</em>' containment reference list isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Purchase Orders</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_PurchaseOrders()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<PurchaseOrder> getPurchaseOrders();

  /**
   * Returns the value of the '<em><b>Customers</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.Customer}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Customers</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Customers</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_Customers()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Customer> getCustomers();

  /**
   * Returns the value of the '<em><b>Sales Orders</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.SalesOrder}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sales Orders</em>' containment reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sales Orders</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_SalesOrders()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<SalesOrder> getSalesOrders();

} // Company
