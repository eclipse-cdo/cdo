/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Company</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getSuppliers <em>Suppliers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getCustomers <em>Customers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CompanyImpl extends AddressImpl implements Company
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
    return Model1Package.eINSTANCE.getCompany();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Category> getCategories()
  {
    return (EList<Category>)eGet(Model1Package.eINSTANCE.getCompany_Categories(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Supplier> getSuppliers()
  {
    return (EList<Supplier>)eGet(Model1Package.eINSTANCE.getCompany_Suppliers(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<PurchaseOrder> getPurchaseOrders()
  {
    return (EList<PurchaseOrder>)eGet(Model1Package.eINSTANCE.getCompany_PurchaseOrders(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Customer> getCustomers()
  {
    return (EList<Customer>)eGet(Model1Package.eINSTANCE.getCompany_Customers(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<SalesOrder> getSalesOrders()
  {
    return (EList<SalesOrder>)eGet(Model1Package.eINSTANCE.getCompany_SalesOrders(), true);
  }

} // CompanyImpl
