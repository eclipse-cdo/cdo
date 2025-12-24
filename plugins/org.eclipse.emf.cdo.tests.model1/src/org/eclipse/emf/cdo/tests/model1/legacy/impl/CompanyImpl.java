/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Company</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CompanyImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CompanyImpl#getSuppliers <em>Suppliers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CompanyImpl#getCustomers <em>Customers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CompanyImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CompanyImpl#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CompanyImpl extends AddressImpl implements Company
{
  /**
   * The cached value of the '{@link #getCategories() <em>Categories</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getCategories()
   * @generated
   * @ordered
   */
  protected EList<Category> categories;

  /**
   * The cached value of the '{@link #getSuppliers() <em>Suppliers</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSuppliers()
   * @generated
   * @ordered
   */
  protected EList<Supplier> suppliers;

  /**
   * The cached value of the '{@link #getCustomers() <em>Customers</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getCustomers()
   * @generated
   * @ordered
   */
  protected EList<Customer> customers;

  /**
   * The cached value of the '{@link #getPurchaseOrders() <em>Purchase Orders</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getPurchaseOrders()
   * @generated
   * @ordered
   */
  protected EList<PurchaseOrder> purchaseOrders;

  /**
   * The cached value of the '{@link #getSalesOrders() <em>Sales Orders</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSalesOrders()
   * @generated
   * @ordered
   */
  protected EList<SalesOrder> salesOrders;

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
  public EList<Category> getCategories()
  {
    if (categories == null)
    {
      categories = new EObjectContainmentEList.Resolving<>(Category.class, this, Model1Package.COMPANY__CATEGORIES);
    }
    return categories;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Supplier> getSuppliers()
  {
    if (suppliers == null)
    {
      suppliers = new EObjectContainmentEList.Resolving<>(Supplier.class, this, Model1Package.COMPANY__SUPPLIERS);
    }
    return suppliers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Customer> getCustomers()
  {
    if (customers == null)
    {
      customers = new EObjectContainmentEList.Resolving<>(Customer.class, this, Model1Package.COMPANY__CUSTOMERS);
    }
    return customers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<PurchaseOrder> getPurchaseOrders()
  {
    if (purchaseOrders == null)
    {
      purchaseOrders = new EObjectContainmentEList.Resolving<>(PurchaseOrder.class, this, Model1Package.COMPANY__PURCHASE_ORDERS);
    }
    return purchaseOrders;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<SalesOrder> getSalesOrders()
  {
    if (salesOrders == null)
    {
      salesOrders = new EObjectContainmentEList.Resolving<>(SalesOrder.class, this, Model1Package.COMPANY__SALES_ORDERS);
    }
    return salesOrders;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.COMPANY__CATEGORIES:
      return ((InternalEList<?>)getCategories()).basicRemove(otherEnd, msgs);
    case Model1Package.COMPANY__SUPPLIERS:
      return ((InternalEList<?>)getSuppliers()).basicRemove(otherEnd, msgs);
    case Model1Package.COMPANY__CUSTOMERS:
      return ((InternalEList<?>)getCustomers()).basicRemove(otherEnd, msgs);
    case Model1Package.COMPANY__PURCHASE_ORDERS:
      return ((InternalEList<?>)getPurchaseOrders()).basicRemove(otherEnd, msgs);
    case Model1Package.COMPANY__SALES_ORDERS:
      return ((InternalEList<?>)getSalesOrders()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model1Package.COMPANY__CATEGORIES:
      return getCategories();
    case Model1Package.COMPANY__SUPPLIERS:
      return getSuppliers();
    case Model1Package.COMPANY__CUSTOMERS:
      return getCustomers();
    case Model1Package.COMPANY__PURCHASE_ORDERS:
      return getPurchaseOrders();
    case Model1Package.COMPANY__SALES_ORDERS:
      return getSalesOrders();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model1Package.COMPANY__CATEGORIES:
      getCategories().clear();
      getCategories().addAll((Collection<? extends Category>)newValue);
      return;
    case Model1Package.COMPANY__SUPPLIERS:
      getSuppliers().clear();
      getSuppliers().addAll((Collection<? extends Supplier>)newValue);
      return;
    case Model1Package.COMPANY__CUSTOMERS:
      getCustomers().clear();
      getCustomers().addAll((Collection<? extends Customer>)newValue);
      return;
    case Model1Package.COMPANY__PURCHASE_ORDERS:
      getPurchaseOrders().clear();
      getPurchaseOrders().addAll((Collection<? extends PurchaseOrder>)newValue);
      return;
    case Model1Package.COMPANY__SALES_ORDERS:
      getSalesOrders().clear();
      getSalesOrders().addAll((Collection<? extends SalesOrder>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model1Package.COMPANY__CATEGORIES:
      getCategories().clear();
      return;
    case Model1Package.COMPANY__SUPPLIERS:
      getSuppliers().clear();
      return;
    case Model1Package.COMPANY__CUSTOMERS:
      getCustomers().clear();
      return;
    case Model1Package.COMPANY__PURCHASE_ORDERS:
      getPurchaseOrders().clear();
      return;
    case Model1Package.COMPANY__SALES_ORDERS:
      getSalesOrders().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model1Package.COMPANY__CATEGORIES:
      return categories != null && !categories.isEmpty();
    case Model1Package.COMPANY__SUPPLIERS:
      return suppliers != null && !suppliers.isEmpty();
    case Model1Package.COMPANY__CUSTOMERS:
      return customers != null && !customers.isEmpty();
    case Model1Package.COMPANY__PURCHASE_ORDERS:
      return purchaseOrders != null && !purchaseOrders.isEmpty();
    case Model1Package.COMPANY__SALES_ORDERS:
      return salesOrders != null && !salesOrders.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // CompanyImpl
