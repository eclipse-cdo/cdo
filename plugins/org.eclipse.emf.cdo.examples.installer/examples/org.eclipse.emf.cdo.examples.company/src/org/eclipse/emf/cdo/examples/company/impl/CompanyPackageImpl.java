/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.Addressable;
import org.eclipse.emf.cdo.examples.company.Category;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.examples.company.Order;
import org.eclipse.emf.cdo.examples.company.OrderDetail;
import org.eclipse.emf.cdo.examples.company.Product;
import org.eclipse.emf.cdo.examples.company.PurchaseOrder;
import org.eclipse.emf.cdo.examples.company.SalesOrder;
import org.eclipse.emf.cdo.examples.company.Supplier;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class CompanyPackageImpl extends EPackageImpl implements CompanyPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass addressableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass supplierEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass purchaseOrderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass orderDetailEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EEnum vatEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass categoryEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass productEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass companyEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass customerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass orderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass salesOrderEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.examples.company.CompanyPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private CompanyPackageImpl()
  {
    super(eNS_URI, CompanyFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link CompanyPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static CompanyPackage init()
  {
    if (isInited)
      return (CompanyPackage)EPackage.Registry.INSTANCE.getEPackage(CompanyPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredCompanyPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    CompanyPackageImpl theCompanyPackage = registeredCompanyPackage instanceof CompanyPackageImpl ? (CompanyPackageImpl)registeredCompanyPackage
        : new CompanyPackageImpl();

    isInited = true;

    // Create package meta-data objects
    theCompanyPackage.createPackageContents();

    // Initialize created meta-data
    theCompanyPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theCompanyPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(CompanyPackage.eNS_URI, theCompanyPackage);
    return theCompanyPackage;
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAddressable()
  {
    return addressableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAddressable_Name()
  {
    return (EAttribute)addressableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAddressable_Street()
  {
    return (EAttribute)addressableEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAddressable_City()
  {
    return (EAttribute)addressableEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSupplier()
  {
    return supplierEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSupplier_PurchaseOrders()
  {
    return (EReference)supplierEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSupplier_Preferred()
  {
    return (EAttribute)supplierEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPurchaseOrder()
  {
    return purchaseOrderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPurchaseOrder_Date()
  {
    return (EAttribute)purchaseOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPurchaseOrder_Supplier()
  {
    return (EReference)purchaseOrderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getOrderDetail()
  {
    return orderDetailEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getOrderDetail_Order()
  {
    return (EReference)orderDetailEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getOrderDetail_Product()
  {
    return (EReference)orderDetailEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getOrderDetail_Price()
  {
    return (EAttribute)orderDetailEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getVAT()
  {
    return vatEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCategory()
  {
    return categoryEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getCategory_Name()
  {
    return (EAttribute)categoryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCategory_Categories()
  {
    return (EReference)categoryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCategory_Products()
  {
    return (EReference)categoryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getProduct()
  {
    return productEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getProduct_Name()
  {
    return (EAttribute)productEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getProduct_Vat()
  {
    return (EAttribute)productEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getProduct_Description()
  {
    return (EAttribute)productEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getProduct_Price()
  {
    return (EAttribute)productEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCompany()
  {
    return companyEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCompany_Categories()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCompany_Suppliers()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCompany_PurchaseOrders()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCompany_Customers()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCompany_SalesOrders()
  {
    return (EReference)companyEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCustomer()
  {
    return customerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCustomer_SalesOrders()
  {
    return (EReference)customerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getOrder()
  {
    return orderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getOrder_OrderDetails()
  {
    return (EReference)orderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSalesOrder()
  {
    return salesOrderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSalesOrder_Id()
  {
    return (EAttribute)salesOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSalesOrder_Customer()
  {
    return (EReference)salesOrderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CompanyFactory getCompanyFactory()
  {
    return (CompanyFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    addressableEClass = createEClass(ADDRESSABLE);
    createEAttribute(addressableEClass, ADDRESSABLE__NAME);
    createEAttribute(addressableEClass, ADDRESSABLE__STREET);
    createEAttribute(addressableEClass, ADDRESSABLE__CITY);

    companyEClass = createEClass(COMPANY);
    createEReference(companyEClass, COMPANY__CATEGORIES);
    createEReference(companyEClass, COMPANY__SUPPLIERS);
    createEReference(companyEClass, COMPANY__CUSTOMERS);
    createEReference(companyEClass, COMPANY__PURCHASE_ORDERS);
    createEReference(companyEClass, COMPANY__SALES_ORDERS);

    supplierEClass = createEClass(SUPPLIER);
    createEReference(supplierEClass, SUPPLIER__PURCHASE_ORDERS);
    createEAttribute(supplierEClass, SUPPLIER__PREFERRED);

    customerEClass = createEClass(CUSTOMER);
    createEReference(customerEClass, CUSTOMER__SALES_ORDERS);

    orderEClass = createEClass(ORDER);
    createEReference(orderEClass, ORDER__ORDER_DETAILS);

    orderDetailEClass = createEClass(ORDER_DETAIL);
    createEReference(orderDetailEClass, ORDER_DETAIL__ORDER);
    createEReference(orderDetailEClass, ORDER_DETAIL__PRODUCT);
    createEAttribute(orderDetailEClass, ORDER_DETAIL__PRICE);

    purchaseOrderEClass = createEClass(PURCHASE_ORDER);
    createEAttribute(purchaseOrderEClass, PURCHASE_ORDER__DATE);
    createEReference(purchaseOrderEClass, PURCHASE_ORDER__SUPPLIER);

    salesOrderEClass = createEClass(SALES_ORDER);
    createEAttribute(salesOrderEClass, SALES_ORDER__ID);
    createEReference(salesOrderEClass, SALES_ORDER__CUSTOMER);

    categoryEClass = createEClass(CATEGORY);
    createEAttribute(categoryEClass, CATEGORY__NAME);
    createEReference(categoryEClass, CATEGORY__CATEGORIES);
    createEReference(categoryEClass, CATEGORY__PRODUCTS);

    productEClass = createEClass(PRODUCT);
    createEAttribute(productEClass, PRODUCT__NAME);
    createEAttribute(productEClass, PRODUCT__VAT);
    createEAttribute(productEClass, PRODUCT__DESCRIPTION);
    createEAttribute(productEClass, PRODUCT__PRICE);

    // Create enums
    vatEEnum = createEEnum(VAT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    companyEClass.getESuperTypes().add(this.getAddressable());
    supplierEClass.getESuperTypes().add(this.getAddressable());
    customerEClass.getESuperTypes().add(this.getAddressable());
    purchaseOrderEClass.getESuperTypes().add(this.getOrder());
    salesOrderEClass.getESuperTypes().add(this.getOrder());

    // Initialize classes and features; add operations and parameters
    initEClass(addressableEClass, Addressable.class, "Addressable", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAddressable_Name(), ecorePackage.getEString(), "name", null, 0, 1, Addressable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddressable_Street(), ecorePackage.getEString(), "street", null, 0, 1, Addressable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddressable_City(), ecorePackage.getEString(), "city", null, 0, 1, Addressable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(companyEClass, Company.class, "Company", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCompany_Categories(), this.getCategory(), null, "categories", null, 0, -1, Company.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_Suppliers(), this.getSupplier(), null, "suppliers", null, 0, -1, Company.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_Customers(), this.getCustomer(), null, "customers", null, 0, -1, Company.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_PurchaseOrders(), this.getPurchaseOrder(), null, "purchaseOrders", null, 0, -1, Company.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCompany_SalesOrders(), this.getSalesOrder(), null, "salesOrders", null, 0, -1, Company.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(supplierEClass, Supplier.class, "Supplier", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSupplier_PurchaseOrders(), this.getPurchaseOrder(), this.getPurchaseOrder_Supplier(), "purchaseOrders", null, 0, -1, Supplier.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSupplier_Preferred(), ecorePackage.getEBoolean(), "preferred", "true", 0, 1, Supplier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(customerEClass, Customer.class, "Customer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCustomer_SalesOrders(), this.getSalesOrder(), this.getSalesOrder_Customer(), "salesOrders", null, 0, -1, Customer.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderEClass, Order.class, "Order", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrder_OrderDetails(), this.getOrderDetail(), this.getOrderDetail_Order(), "orderDetails", null, 0, -1, Order.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderDetailEClass, OrderDetail.class, "OrderDetail", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrderDetail_Order(), this.getOrder(), this.getOrder_OrderDetails(), "order", null, 1, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrderDetail_Product(), this.getProduct(), null, "product", null, 0, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOrderDetail_Price(), ecorePackage.getEFloat(), "price", null, 0, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(purchaseOrderEClass, PurchaseOrder.class, "PurchaseOrder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPurchaseOrder_Date(), ecorePackage.getEDate(), "date", null, 0, 1, PurchaseOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPurchaseOrder_Supplier(), this.getSupplier(), this.getSupplier_PurchaseOrders(), "supplier", null, 1, 1, PurchaseOrder.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(salesOrderEClass, SalesOrder.class, "SalesOrder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSalesOrder_Id(), ecorePackage.getEInt(), "id", null, 0, 1, SalesOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSalesOrder_Customer(), this.getCustomer(), this.getCustomer_SalesOrders(), "customer", null, 1, 1, SalesOrder.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(categoryEClass, Category.class, "Category", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCategory_Name(), ecorePackage.getEString(), "name", null, 0, 1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCategory_Categories(), this.getCategory(), null, "categories", null, 0, -1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCategory_Products(), this.getProduct(), null, "products", null, 0, -1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(productEClass, Product.class, "Product", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProduct_Name(), ecorePackage.getEString(), "name", null, 0, 1, Product.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProduct_Vat(), this.getVAT(), "vat", "vat15", 0, 1, Product.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProduct_Description(), ecorePackage.getEString(), "description", null, 0, 1, Product.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProduct_Price(), ecorePackage.getEFloat(), "price", null, 0, 1, Product.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(vatEEnum, org.eclipse.emf.cdo.examples.company.VAT.class, "VAT");
    addEEnumLiteral(vatEEnum, org.eclipse.emf.cdo.examples.company.VAT.VAT0);
    addEEnumLiteral(vatEEnum, org.eclipse.emf.cdo.examples.company.VAT.VAT7);
    addEEnumLiteral(vatEEnum, org.eclipse.emf.cdo.examples.company.VAT.VAT15);

    // Create resource
    createResource(eNS_URI);
  }

} // CompanyPackageImpl
