/*
 * Copyright (c) 2007-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model1.Model1Factory
 * @model kind="package"
 * @generated
 */
public interface Model1Package extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model1";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model1/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model1";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model1Package eINSTANCE = org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.AddressImpl <em>Address</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model1.impl.AddressImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getAddress()
   * @generated
   */
  int ADDRESS = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESS__NAME = 0;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESS__STREET = 1;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESS__CITY = 2;

  /**
   * The number of structural features of the '<em>Address</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESS_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl <em>Supplier</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getSupplier()
   * @generated
   */
  int SUPPLIER = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl <em>Purchase Order</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getPurchaseOrder()
   * @generated
   */
  int PURCHASE_ORDER = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl <em>Order Detail</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getOrderDetail()
   * @generated
   */
  int ORDER_DETAIL = 5;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Address <em>Address</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Address</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Address
   * @generated
   */
  EClass getAddress();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Address#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Address#getName()
   * @see #getAddress()
   * @generated
   */
  EAttribute getAddress_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Address#getStreet <em>Street</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Street</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Address#getStreet()
   * @see #getAddress()
   * @generated
   */
  EAttribute getAddress_Street();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Address#getCity <em>City</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>City</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Address#getCity()
   * @see #getAddress()
   * @generated
   */
  EAttribute getAddress_City();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl <em>Category</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCategory()
   * @generated
   */
  int CATEGORY = 8;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl <em>Company</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCompany()
   * @generated
   */
  int COMPANY = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__NAME = ADDRESS__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__STREET = ADDRESS__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__CITY = ADDRESS__CITY;

  /**
   * The feature id for the '<em><b>Categories</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__CATEGORIES = ADDRESS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Suppliers</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__SUPPLIERS = ADDRESS_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Customers</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__CUSTOMERS = ADDRESS_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Purchase Orders</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__PURCHASE_ORDERS = ADDRESS_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Sales Orders</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__SALES_ORDERS = ADDRESS_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Company</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY_FEATURE_COUNT = ADDRESS_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__NAME = ADDRESS__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__STREET = ADDRESS__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__CITY = ADDRESS__CITY;

  /**
   * The feature id for the '<em><b>Purchase Orders</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int SUPPLIER__PURCHASE_ORDERS = ADDRESS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Preferred</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__PREFERRED = ADDRESS_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Supplier</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER_FEATURE_COUNT = ADDRESS_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CustomerImpl <em>Customer</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.CustomerImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCustomer()
   * @generated
   */
  int CUSTOMER = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__NAME = ADDRESS__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__STREET = ADDRESS__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__CITY = ADDRESS__CITY;

  /**
   * The feature id for the '<em><b>Sales Orders</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__SALES_ORDERS = ADDRESS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Order By Product</b></em>' map.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__ORDER_BY_PRODUCT = ADDRESS_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Customer</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER_FEATURE_COUNT = ADDRESS_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.OrderImpl <em>Order</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model1.impl.OrderImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getOrder()
   * @generated
   */
  int ORDER = 4;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER__ORDER_DETAILS = 0;

  /**
   * The number of structural features of the '<em>Order</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Order</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__ORDER = 0;

  /**
   * The feature id for the '<em><b>Product</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__PRODUCT = 1;

  /**
   * The feature id for the '<em><b>Price</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__PRICE = 2;

  /**
   * The number of structural features of the '<em>Order Detail</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int ORDER_DETAIL_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__ORDER_DETAILS = ORDER__ORDER_DETAILS;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__DATE = ORDER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Supplier</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__SUPPLIER = ORDER_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Sales Orders</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__SALES_ORDERS = ORDER_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Purchase Order</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER_FEATURE_COUNT = ORDER_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl <em>Sales Order</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getSalesOrder()
   * @generated
   */
  int SALES_ORDER = 7;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int SALES_ORDER__ORDER_DETAILS = ORDER__ORDER_DETAILS;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SALES_ORDER__ID = ORDER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Customer</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SALES_ORDER__CUSTOMER = ORDER_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Purchase Orders</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SALES_ORDER__PURCHASE_ORDERS = ORDER_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Sales Order</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int SALES_ORDER_FEATURE_COUNT = ORDER_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY__NAME = 0;

  /**
   * The feature id for the '<em><b>Categories</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY__CATEGORIES = 1;

  /**
   * The feature id for the '<em><b>Products</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY__PRODUCTS = 2;

  /**
   * The feature id for the '<em><b>Main Product</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY__MAIN_PRODUCT = 3;

  /**
   * The feature id for the '<em><b>Top Products</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY__TOP_PRODUCTS = 4;

  /**
   * The number of structural features of the '<em>Category</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.Product1Impl <em>Product1</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.Product1Impl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getProduct1()
   * @generated
   */
  int PRODUCT1 = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT1__NAME = 0;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int PRODUCT1__ORDER_DETAILS = 1;

  /**
   * The feature id for the '<em><b>Vat</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT1__VAT = 2;

  /**
   * The feature id for the '<em><b>Other VA Ts</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT1__OTHER_VA_TS = 3;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT1__DESCRIPTION = 4;

  /**
   * The number of structural features of the '<em>Product1</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT1_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl <em>Order Address</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getOrderAddress()
   * @generated
   */
  int ORDER_ADDRESS = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__NAME = ADDRESS__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__STREET = ADDRESS__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__CITY = ADDRESS__CITY;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__ORDER_DETAILS = ADDRESS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Order</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__ORDER = ADDRESS_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Product</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__PRODUCT = ADDRESS_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Price</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__PRICE = ADDRESS_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Test Attribute</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS__TEST_ATTRIBUTE = ADDRESS_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Order Address</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int ORDER_ADDRESS_FEATURE_COUNT = ADDRESS_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.ProductToOrderImpl <em>Product To Order</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.ProductToOrderImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getProductToOrder()
   * @generated
   */
  int PRODUCT_TO_ORDER = 11;

  /**
   * The feature id for the '<em><b>Key</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT_TO_ORDER__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT_TO_ORDER__VALUE = 1;

  /**
   * The number of structural features of the '<em>Product To Order</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT_TO_ORDER_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.VAT <em>VAT</em>}' enum.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getVAT()
   * @generated
   */
  int VAT = 12;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Supplier <em>Supplier</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier
   * @generated
   */
  EClass getSupplier();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.Supplier#getPurchaseOrders <em>Purchase Orders</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Purchase Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier#getPurchaseOrders()
   * @see #getSupplier()
   * @generated
   */
  EReference getSupplier_PurchaseOrders();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Supplier#isPreferred <em>Preferred</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Preferred</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier#isPreferred()
   * @see #getSupplier()
   * @generated
   */
  EAttribute getSupplier_Preferred();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder <em>Purchase Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Purchase Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder
   * @generated
   */
  EClass getPurchaseOrder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate <em>Date</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate()
   * @see #getPurchaseOrder()
   * @generated
   */
  EAttribute getPurchaseOrder_Date();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier <em>Supplier</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier()
   * @see #getPurchaseOrder()
   * @generated
   */
  EReference getPurchaseOrder_Supplier();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSalesOrders <em>Sales Orders</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Sales Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSalesOrders()
   * @see #getPurchaseOrder()
   * @generated
   */
  EReference getPurchaseOrder_SalesOrders();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail <em>Order Detail</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Order Detail</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail
   * @generated
   */
  EClass getOrderDetail();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getOrder <em>Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getOrder()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Order();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct <em>Product</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Product</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Product();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getPrice <em>Price</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Price</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getPrice()
   * @see #getOrderDetail()
   * @generated
   */
  EAttribute getOrderDetail_Price();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.OrderAddress <em>Order Address</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Order Address</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderAddress
   * @generated
   */
  EClass getOrderAddress();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.OrderAddress#isTestAttribute <em>Test Attribute</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Test Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderAddress#isTestAttribute()
   * @see #getOrderAddress()
   * @generated
   */
  EAttribute getOrderAddress_TestAttribute();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>Product To Order</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Product To Order</em>'.
   * @see java.util.Map.Entry
   * @model keyType="org.eclipse.emf.cdo.tests.model1.Product1"
   *        valueType="org.eclipse.emf.cdo.tests.model1.SalesOrder"
   * @generated
   */
  EClass getProductToOrder();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the reference '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getProductToOrder()
   * @generated
   */
  EReference getProductToOrder_Key();

  /**
   * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getProductToOrder()
   * @generated
   */
  EReference getProductToOrder_Value();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.tests.model1.VAT <em>VAT</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for enum '<em>VAT</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @generated
   */
  EEnum getVAT();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Category <em>Category</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Category</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category
   * @generated
   */
  EClass getCategory();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Category#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getName()
   * @see #getCategory()
   * @generated
   */
  EAttribute getCategory_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Category#getCategories <em>Categories</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Categories</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getCategories()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_Categories();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Category#getProducts <em>Products</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Products</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getProducts()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_Products();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.Category#getMainProduct <em>Main Product</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Main Product</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getMainProduct()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_MainProduct();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.Category#getTopProducts <em>Top Products</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Top Products</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getTopProducts()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_TopProducts();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Product1 <em>Product1</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Product1</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product1
   * @generated
   */
  EClass getProduct1();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Product1#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product1#getName()
   * @see #getProduct1()
   * @generated
   */
  EAttribute getProduct1_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.Product1#getOrderDetails <em>Order Details</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Order Details</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product1#getOrderDetails()
   * @see #getProduct1()
   * @generated
   */
  EReference getProduct1_OrderDetails();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Product1#getVat <em>Vat</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Vat</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product1#getVat()
   * @see #getProduct1()
   * @generated
   */
  EAttribute getProduct1_Vat();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model1.Product1#getOtherVATs <em>Other VA Ts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Other VA Ts</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product1#getOtherVATs()
   * @see #getProduct1()
   * @generated
   */
  EAttribute getProduct1_OtherVATs();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Product1#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product1#getDescription()
   * @see #getProduct1()
   * @generated
   */
  EAttribute getProduct1_Description();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Company <em>Company</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Company</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company
   * @generated
   */
  EClass getCompany();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getCategories <em>Categories</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Categories</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getCategories()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Categories();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getSuppliers <em>Suppliers</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Suppliers</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getSuppliers()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Suppliers();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getPurchaseOrders <em>Purchase Orders</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Purchase Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getPurchaseOrders()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_PurchaseOrders();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getCustomers <em>Customers</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Customers</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getCustomers()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Customers();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getSalesOrders <em>Sales Orders</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Sales Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getSalesOrders()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_SalesOrders();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Customer <em>Customer</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Customer</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Customer
   * @generated
   */
  EClass getCustomer();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.Customer#getSalesOrders <em>Sales Orders</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Sales Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Customer#getSalesOrders()
   * @see #getCustomer()
   * @generated
   */
  EReference getCustomer_SalesOrders();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model1.Customer#getOrderByProduct <em>Order By Product</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the map '<em>Order By Product</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Customer#getOrderByProduct()
   * @see #getCustomer()
   * @generated
   */
  EReference getCustomer_OrderByProduct();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Order <em>Order</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Order
   * @generated
   */
  EClass getOrder();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Order#getOrderDetails <em>Order Details</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Order Details</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Order#getOrderDetails()
   * @see #getOrder()
   * @generated
   */
  EReference getOrder_OrderDetails();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.SalesOrder <em>Sales Order</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Sales Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.SalesOrder
   * @generated
   */
  EClass getSalesOrder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.SalesOrder#getId <em>Id</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.SalesOrder#getId()
   * @see #getSalesOrder()
   * @generated
   */
  EAttribute getSalesOrder_Id();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.SalesOrder#getCustomer <em>Customer</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Customer</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.SalesOrder#getCustomer()
   * @see #getSalesOrder()
   * @generated
   */
  EReference getSalesOrder_Customer();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.SalesOrder#getPurchaseOrders <em>Purchase Orders</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Purchase Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.SalesOrder#getPurchaseOrders()
   * @see #getSalesOrder()
   * @generated
   */
  EReference getSalesOrder_PurchaseOrders();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model1Factory getModel1Factory();

} // Model1Package
