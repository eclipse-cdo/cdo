/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.Category;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.examples.company.PurchaseOrder;
import org.eclipse.emf.cdo.examples.company.SalesOrder;
import org.eclipse.emf.cdo.examples.company.Supplier;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Company</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl#getSuppliers <em>Suppliers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl#getCustomers <em>Customers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CompanyImpl extends AddressableImpl implements Company
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CompanyImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return CompanyPackage.Literals.COMPANY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Category> getCategories()
  {
    return (EList<Category>)eGet(CompanyPackage.Literals.COMPANY__CATEGORIES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Supplier> getSuppliers()
  {
    return (EList<Supplier>)eGet(CompanyPackage.Literals.COMPANY__SUPPLIERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<PurchaseOrder> getPurchaseOrders()
  {
    return (EList<PurchaseOrder>)eGet(CompanyPackage.Literals.COMPANY__PURCHASE_ORDERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Customer> getCustomers()
  {
    return (EList<Customer>)eGet(CompanyPackage.Literals.COMPANY__CUSTOMERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<SalesOrder> getSalesOrders()
  {
    return (EList<SalesOrder>)eGet(CompanyPackage.Literals.COMPANY__SALES_ORDERS, true);
  }

} // CompanyImpl
