/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model1.Model1Factory
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model1.legacy.Model1Package
 * @generated
 */
public interface Model1Factory extends EFactory, org.eclipse.emf.cdo.tests.model1.Model1Factory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model1Factory eINSTANCE = org.eclipse.emf.cdo.tests.model1.legacy.impl.Model1FactoryImpl.init();

  @Override
  /**
   * Returns a new object of class '<em>Address</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Address</em>'.
   * @generated
   */
  Address createAddress();

  @Override
  /**
   * Returns a new object of class '<em>Company</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Company</em>'.
   * @generated
   */
  Company createCompany();

  @Override
  /**
   * Returns a new object of class '<em>Supplier</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Supplier</em>'.
   * @generated
   */
  Supplier createSupplier();

  @Override
  /**
   * Returns a new object of class '<em>Customer</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Customer</em>'.
   * @generated
   */
  Customer createCustomer();

  @Override
  /**
   * Returns a new object of class '<em>Order Detail</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Order Detail</em>'.
   * @generated
   */
  OrderDetail createOrderDetail();

  @Override
  /**
   * Returns a new object of class '<em>Purchase Order</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Purchase Order</em>'.
   * @generated
   */
  PurchaseOrder createPurchaseOrder();

  @Override
  /**
   * Returns a new object of class '<em>Sales Order</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Sales Order</em>'.
   * @generated
   */
  SalesOrder createSalesOrder();

  @Override
  /**
   * Returns a new object of class '<em>Category</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Category</em>'.
   * @generated
   */
  Category createCategory();

  @Override
  /**
   * Returns a new object of class '<em>Product1</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Product1</em>'.
   * @generated
   */
  Product1 createProduct1();

  @Override
  /**
   * Returns a new object of class '<em>Order Address</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Order Address</em>'.
   * @generated
   */
  OrderAddress createOrderAddress();

  @Override
  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Model1Package getModel1Package();

} // Model1Factory
