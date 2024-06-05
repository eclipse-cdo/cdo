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
 * @see org.eclipse.emf.cdo.examples.company.CompanyFactory
 * @model kind="package"
 * @generated
 */
public interface CompanyPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "company";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/examples/company/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "company";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  CompanyPackage eINSTANCE = org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.AddressableImpl <em>Addressable</em>}' class.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.AddressableImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getAddressable()
   * @generated
   */
  int ADDRESSABLE = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE__NAME = 0;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE__STREET = 1;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE__CITY = 2;

  /**
   * The number of structural features of the '<em>Addressable</em>' class.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.SupplierImpl <em>Supplier</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.SupplierImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getSupplier()
   * @generated
   */
  int SUPPLIER = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl <em>Purchase Order</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getPurchaseOrder()
   * @generated
   */
  int PURCHASE_ORDER = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl <em>Order Detail</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getOrderDetail()
   * @generated
   */
  int ORDER_DETAIL = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.CategoryImpl <em>Category</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.CategoryImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getCategory()
   * @generated
   */
  int CATEGORY = 8;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl <em>Company</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getCompany()
   * @generated
   */
  int COMPANY = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__NAME = ADDRESSABLE__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__STREET = ADDRESSABLE__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__CITY = ADDRESSABLE__CITY;

  /**
   * The feature id for the '<em><b>Categories</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__CATEGORIES = ADDRESSABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Suppliers</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__SUPPLIERS = ADDRESSABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Customers</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__CUSTOMERS = ADDRESSABLE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Purchase Orders</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__PURCHASE_ORDERS = ADDRESSABLE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Sales Orders</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY__SALES_ORDERS = ADDRESSABLE_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Company</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPANY_FEATURE_COUNT = ADDRESSABLE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__NAME = ADDRESSABLE__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__STREET = ADDRESSABLE__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__CITY = ADDRESSABLE__CITY;

  /**
   * The feature id for the '<em><b>Purchase Orders</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int SUPPLIER__PURCHASE_ORDERS = ADDRESSABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Preferred</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__PREFERRED = ADDRESSABLE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Supplier</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER_FEATURE_COUNT = ADDRESSABLE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.CustomerImpl <em>Customer</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.CustomerImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getCustomer()
   * @generated
   */
  int CUSTOMER = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__NAME = ADDRESSABLE__NAME;

  /**
   * The feature id for the '<em><b>Street</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__STREET = ADDRESSABLE__STREET;

  /**
   * The feature id for the '<em><b>City</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__CITY = ADDRESSABLE__CITY;

  /**
   * The feature id for the '<em><b>Sales Orders</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER__SALES_ORDERS = ADDRESSABLE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Customer</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CUSTOMER_FEATURE_COUNT = ADDRESSABLE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.OrderImpl <em>Order</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.examples.company.impl.OrderImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getOrder()
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
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl <em>Sales Order</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getSalesOrder()
   * @generated
   */
  int SALES_ORDER = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.impl.ProductImpl <em>Product</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.examples.company.impl.ProductImpl
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getProduct()
   * @generated
   */
  int PRODUCT = 9;

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
   * The number of structural features of the '<em>Purchase Order</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER_FEATURE_COUNT = ORDER_FEATURE_COUNT + 2;

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
   * The number of structural features of the '<em>Sales Order</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int SALES_ORDER_FEATURE_COUNT = ORDER_FEATURE_COUNT + 2;

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
   * The number of structural features of the '<em>Category</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CATEGORY_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT__NAME = 0;

  /**
   * The feature id for the '<em><b>Vat</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT__VAT = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>Price</b></em>' attribute.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT__PRICE = 3;

  /**
   * The number of structural features of the '<em>Product</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PRODUCT_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.examples.company.VAT <em>VAT</em>}' enum. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.examples.company.VAT
   * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getVAT()
   * @generated
   */
  int VAT = 10;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Addressable <em>Addressable</em>}'.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @return the meta object for class '<em>Addressable</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Addressable
   * @generated
   */
  EClass getAddressable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Addressable#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Addressable#getName()
   * @see #getAddressable()
   * @generated
   */
  EAttribute getAddressable_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Addressable#getStreet <em>Street</em>}'.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Street</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Addressable#getStreet()
   * @see #getAddressable()
   * @generated
   */
  EAttribute getAddressable_Street();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Addressable#getCity <em>City</em>}'.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @return the meta object for the attribute '<em>City</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Addressable#getCity()
   * @see #getAddressable()
   * @generated
   */
  EAttribute getAddressable_City();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Supplier <em>Supplier</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Supplier
   * @generated
   */
  EClass getSupplier();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.examples.company.Supplier#getPurchaseOrders <em>Purchase Orders</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Purchase Orders</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Supplier#getPurchaseOrders()
   * @see #getSupplier()
   * @generated
   */
  EReference getSupplier_PurchaseOrders();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Supplier#isPreferred <em>Preferred</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Preferred</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Supplier#isPreferred()
   * @see #getSupplier()
   * @generated
   */
  EAttribute getSupplier_Preferred();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder <em>Purchase Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Purchase Order</em>'.
   * @see org.eclipse.emf.cdo.examples.company.PurchaseOrder
   * @generated
   */
  EClass getPurchaseOrder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getDate <em>Date</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Date</em>'.
   * @see org.eclipse.emf.cdo.examples.company.PurchaseOrder#getDate()
   * @see #getPurchaseOrder()
   * @generated
   */
  EAttribute getPurchaseOrder_Date();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.examples.company.PurchaseOrder#getSupplier <em>Supplier</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.examples.company.PurchaseOrder#getSupplier()
   * @see #getPurchaseOrder()
   * @generated
   */
  EReference getPurchaseOrder_Supplier();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.OrderDetail <em>Order Detail</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Order Detail</em>'.
   * @see org.eclipse.emf.cdo.examples.company.OrderDetail
   * @generated
   */
  EClass getOrderDetail();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.examples.company.OrderDetail#getOrder <em>Order</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the container reference '<em>Order</em>'.
   * @see org.eclipse.emf.cdo.examples.company.OrderDetail#getOrder()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Order();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.examples.company.OrderDetail#getProduct <em>Product</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Product</em>'.
   * @see org.eclipse.emf.cdo.examples.company.OrderDetail#getProduct()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Product();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.OrderDetail#getPrice <em>Price</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Price</em>'.
   * @see org.eclipse.emf.cdo.examples.company.OrderDetail#getPrice()
   * @see #getOrderDetail()
   * @generated
   */
  EAttribute getOrderDetail_Price();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.examples.company.VAT <em>VAT</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for enum '<em>VAT</em>'.
   * @see org.eclipse.emf.cdo.examples.company.VAT
   * @generated
   */
  EEnum getVAT();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Category <em>Category</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Category</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Category
   * @generated
   */
  EClass getCategory();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Category#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Category#getName()
   * @see #getCategory()
   * @generated
   */
  EAttribute getCategory_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Category#getCategories <em>Categories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Categories</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Category#getCategories()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_Categories();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Category#getProducts <em>Products</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Products</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Category#getProducts()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_Products();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Product <em>Product</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Product</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Product
   * @generated
   */
  EClass getProduct();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Product#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Product#getName()
   * @see #getProduct()
   * @generated
   */
  EAttribute getProduct_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Product#getVat <em>Vat</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Vat</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Product#getVat()
   * @see #getProduct()
   * @generated
   */
  EAttribute getProduct_Vat();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Product#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Product#getDescription()
   * @see #getProduct()
   * @generated
   */
  EAttribute getProduct_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.Product#getPrice <em>Price</em>}'.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Price</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Product#getPrice()
   * @see #getProduct()
   * @generated
   */
  EAttribute getProduct_Price();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Company <em>Company</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Company</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Company
   * @generated
   */
  EClass getCompany();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Company#getCategories <em>Categories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Categories</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Company#getCategories()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Categories();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Company#getSuppliers <em>Suppliers</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Suppliers</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Company#getSuppliers()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Suppliers();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.examples.company.Company#getPurchaseOrders <em>Purchase Orders</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Purchase Orders</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Company#getPurchaseOrders()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_PurchaseOrders();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Company#getCustomers <em>Customers</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Customers</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Company#getCustomers()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Customers();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Company#getSalesOrders <em>Sales Orders</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Sales Orders</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Company#getSalesOrders()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_SalesOrders();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Customer <em>Customer</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Customer</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Customer
   * @generated
   */
  EClass getCustomer();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.examples.company.Customer#getSalesOrders <em>Sales Orders</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Sales Orders</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Customer#getSalesOrders()
   * @see #getCustomer()
   * @generated
   */
  EReference getCustomer_SalesOrders();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.Order <em>Order</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Order</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Order
   * @generated
   */
  EClass getOrder();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.examples.company.Order#getOrderDetails <em>Order Details</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Order Details</em>'.
   * @see org.eclipse.emf.cdo.examples.company.Order#getOrderDetails()
   * @see #getOrder()
   * @generated
   */
  EReference getOrder_OrderDetails();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.examples.company.SalesOrder <em>Sales Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Sales Order</em>'.
   * @see org.eclipse.emf.cdo.examples.company.SalesOrder
   * @generated
   */
  EClass getSalesOrder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getId <em>Id</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.examples.company.SalesOrder#getId()
   * @see #getSalesOrder()
   * @generated
   */
  EAttribute getSalesOrder_Id();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.examples.company.SalesOrder#getCustomer <em>Customer</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Customer</em>'.
   * @see org.eclipse.emf.cdo.examples.company.SalesOrder#getCustomer()
   * @see #getSalesOrder()
   * @generated
   */
  EReference getSalesOrder_Customer();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  CompanyFactory getCompanyFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.AddressableImpl <em>Addressable</em>}' class.
     * <!-- begin-user-doc -->
     *
     * @since 4.0<!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.AddressableImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getAddressable()
     * @generated
     */
    EClass ADDRESSABLE = eINSTANCE.getAddressable();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     *
     * @since 4.0<!-- end-user-doc -->
     * @generated
     */
    EAttribute ADDRESSABLE__NAME = eINSTANCE.getAddressable_Name();

    /**
     * The meta object literal for the '<em><b>Street</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     *
     * @since 4.0<!-- end-user-doc -->
     * @generated
     */
    EAttribute ADDRESSABLE__STREET = eINSTANCE.getAddressable_Street();

    /**
     * The meta object literal for the '<em><b>City</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     *
     * @since 4.0<!-- end-user-doc -->
     * @generated
     */
    EAttribute ADDRESSABLE__CITY = eINSTANCE.getAddressable_City();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.SupplierImpl <em>Supplier</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.SupplierImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getSupplier()
     * @generated
     */
    EClass SUPPLIER = eINSTANCE.getSupplier();

    /**
     * The meta object literal for the '<em><b>Purchase Orders</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUPPLIER__PURCHASE_ORDERS = eINSTANCE.getSupplier_PurchaseOrders();

    /**
     * The meta object literal for the '<em><b>Preferred</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute SUPPLIER__PREFERRED = eINSTANCE.getSupplier_Preferred();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl <em>Purchase Order</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getPurchaseOrder()
     * @generated
     */
    EClass PURCHASE_ORDER = eINSTANCE.getPurchaseOrder();

    /**
     * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute PURCHASE_ORDER__DATE = eINSTANCE.getPurchaseOrder_Date();

    /**
     * The meta object literal for the '<em><b>Supplier</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference PURCHASE_ORDER__SUPPLIER = eINSTANCE.getPurchaseOrder_Supplier();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl <em>Order Detail</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getOrderDetail()
     * @generated
     */
    EClass ORDER_DETAIL = eINSTANCE.getOrderDetail();

    /**
     * The meta object literal for the '<em><b>Order</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference ORDER_DETAIL__ORDER = eINSTANCE.getOrderDetail_Order();

    /**
     * The meta object literal for the '<em><b>Product</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference ORDER_DETAIL__PRODUCT = eINSTANCE.getOrderDetail_Product();

    /**
     * The meta object literal for the '<em><b>Price</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute ORDER_DETAIL__PRICE = eINSTANCE.getOrderDetail_Price();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.VAT <em>VAT</em>}' enum. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.examples.company.VAT
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getVAT()
     * @generated
     */
    EEnum VAT = eINSTANCE.getVAT();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.CategoryImpl <em>Category</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.CategoryImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getCategory()
     * @generated
     */
    EClass CATEGORY = eINSTANCE.getCategory();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute CATEGORY__NAME = eINSTANCE.getCategory_Name();

    /**
     * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference CATEGORY__CATEGORIES = eINSTANCE.getCategory_Categories();

    /**
     * The meta object literal for the '<em><b>Products</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference CATEGORY__PRODUCTS = eINSTANCE.getCategory_Products();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.ProductImpl <em>Product</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.ProductImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getProduct()
     * @generated
     */
    EClass PRODUCT = eINSTANCE.getProduct();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute PRODUCT__NAME = eINSTANCE.getProduct_Name();

    /**
     * The meta object literal for the '<em><b>Vat</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute PRODUCT__VAT = eINSTANCE.getProduct_Vat();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute PRODUCT__DESCRIPTION = eINSTANCE.getProduct_Description();

    /**
     * The meta object literal for the '<em><b>Price</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     *
     * @since 4.0<!-- end-user-doc -->
     * @generated
     */
    EAttribute PRODUCT__PRICE = eINSTANCE.getProduct_Price();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.CompanyImpl <em>Company</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getCompany()
     * @generated
     */
    EClass COMPANY = eINSTANCE.getCompany();

    /**
     * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference COMPANY__CATEGORIES = eINSTANCE.getCompany_Categories();

    /**
     * The meta object literal for the '<em><b>Suppliers</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference COMPANY__SUPPLIERS = eINSTANCE.getCompany_Suppliers();

    /**
     * The meta object literal for the '<em><b>Purchase Orders</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference COMPANY__PURCHASE_ORDERS = eINSTANCE.getCompany_PurchaseOrders();

    /**
     * The meta object literal for the '<em><b>Customers</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference COMPANY__CUSTOMERS = eINSTANCE.getCompany_Customers();

    /**
     * The meta object literal for the '<em><b>Sales Orders</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference COMPANY__SALES_ORDERS = eINSTANCE.getCompany_SalesOrders();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.CustomerImpl <em>Customer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.CustomerImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getCustomer()
     * @generated
     */
    EClass CUSTOMER = eINSTANCE.getCustomer();

    /**
     * The meta object literal for the '<em><b>Sales Orders</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CUSTOMER__SALES_ORDERS = eINSTANCE.getCustomer_SalesOrders();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.OrderImpl <em>Order</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.OrderImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getOrder()
     * @generated
     */
    EClass ORDER = eINSTANCE.getOrder();

    /**
     * The meta object literal for the '<em><b>Order Details</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference ORDER__ORDER_DETAILS = eINSTANCE.getOrder_OrderDetails();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl <em>Sales Order</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl
     * @see org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl#getSalesOrder()
     * @generated
     */
    EClass SALES_ORDER = eINSTANCE.getSalesOrder();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @generated
     */
    EAttribute SALES_ORDER__ID = eINSTANCE.getSalesOrder_Id();

    /**
     * The meta object literal for the '<em><b>Customer</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference SALES_ORDER__CUSTOMER = eINSTANCE.getSalesOrder_Customer();

  }

} // CompanyPackage
