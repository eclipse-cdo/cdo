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
package org.eclipse.emf.cdo.examples.company;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Company</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.examples.company.Company#getCategories <em>Categories</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.examples.company.Company#getSuppliers <em>Suppliers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.examples.company.Company#getCustomers <em>Customers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.examples.company.Company#getPurchaseOrders <em>Purchase Orders</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.examples.company.Company#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCompany()
 * @model
 * @generated
 */
public interface Company extends Address
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Categories</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.examples.company.Category}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Categories</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Categories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCompany_Categories()
   * @model containment="true"
   * @generated
   */
  EList<Category> getCategories();

  /**
   * Returns the value of the '<em><b>Suppliers</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.examples.company.Supplier}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Suppliers</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Suppliers</em>' containment reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCompany_Suppliers()
   * @model containment="true"
   * @generated
   */
  EList<Supplier> getSuppliers();

  /**
   * Returns the value of the '<em><b>Purchase Orders</b></em>' containment reference list. The list contents are of
   * type {@link org.eclipse.emf.cdo.examples.company.PurchaseOrder}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Purchase Orders</em>' containment reference list isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Purchase Orders</em>' containment reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCompany_PurchaseOrders()
   * @model containment="true"
   * @generated
   */
  EList<PurchaseOrder> getPurchaseOrders();

  /**
   * Returns the value of the '<em><b>Customers</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.examples.company.Customer}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Customers</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Customers</em>' containment reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCompany_Customers()
   * @model containment="true"
   * @generated
   */
  EList<Customer> getCustomers();

  /**
   * Returns the value of the '<em><b>Sales Orders</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.examples.company.SalesOrder}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sales Orders</em>' containment reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Sales Orders</em>' containment reference list.
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#getCompany_SalesOrders()
   * @model containment="true"
   * @generated
   */
  EList<SalesOrder> getSalesOrders();

} // Company
