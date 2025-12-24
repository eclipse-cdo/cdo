/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.business.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.gastro.business.BusinessDay;
import org.gastro.business.BusinessFactory;
import org.gastro.business.BusinessPackage;
import org.gastro.business.Order;
import org.gastro.business.OrderDetail;
import org.gastro.business.OrderState;
import org.gastro.business.Waiter;
import org.gastro.inventory.InventoryPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class BusinessPackageImpl extends EPackageImpl implements BusinessPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass businessDayEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass orderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass orderDetailEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass waiterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EEnum orderStateEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.gastro.business.BusinessPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private BusinessPackageImpl()
  {
    super(eNS_URI, BusinessFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link BusinessPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static BusinessPackage init()
  {
    if (isInited)
    {
      return (BusinessPackage)EPackage.Registry.INSTANCE.getEPackage(BusinessPackage.eNS_URI);
    }

    // Obtain or create and register package
    BusinessPackageImpl theBusinessPackage = (BusinessPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof BusinessPackageImpl
        ? EPackage.Registry.INSTANCE.get(eNS_URI)
        : new BusinessPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    InventoryPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theBusinessPackage.createPackageContents();

    // Initialize created meta-data
    theBusinessPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theBusinessPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(BusinessPackage.eNS_URI, theBusinessPackage);
    return theBusinessPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getBusinessDay()
  {
    return businessDayEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBusinessDay_Date()
  {
    return (EAttribute)businessDayEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getBusinessDay_MenuCard()
  {
    return (EReference)businessDayEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getBusinessDay_Orders()
  {
    return (EReference)businessDayEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getBusinessDay_Waiters()
  {
    return (EReference)businessDayEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBusinessDay_Closed()
  {
    return (EAttribute)businessDayEClass.getEStructuralFeatures().get(4);
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
  public EReference getOrder_BusinessDay()
  {
    return (EReference)orderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getOrder_Table()
  {
    return (EReference)orderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getOrder_OrderDetails()
  {
    return (EReference)orderEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getOrder_Number()
  {
    return (EAttribute)orderEClass.getEStructuralFeatures().get(3);
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
  public EReference getOrderDetail_Offering()
  {
    return (EReference)orderDetailEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getOrderDetail_Quantity()
  {
    return (EAttribute)orderDetailEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getOrderDetail_State()
  {
    return (EAttribute)orderDetailEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getOrderDetail_Price()
  {
    return (EAttribute)orderDetailEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getWaiter()
  {
    return waiterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getWaiter_BusinessDay()
  {
    return (EReference)waiterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getWaiter_Tables()
  {
    return (EReference)waiterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getWaiter_Employee()
  {
    return (EReference)waiterEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getWaiter_From()
  {
    return (EAttribute)waiterEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getWaiter_Until()
  {
    return (EAttribute)waiterEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getOrderState()
  {
    return orderStateEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BusinessFactory getBusinessFactory()
  {
    return (BusinessFactory)getEFactoryInstance();
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
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    businessDayEClass = createEClass(BUSINESS_DAY);
    createEAttribute(businessDayEClass, BUSINESS_DAY__DATE);
    createEReference(businessDayEClass, BUSINESS_DAY__MENU_CARD);
    createEReference(businessDayEClass, BUSINESS_DAY__ORDERS);
    createEReference(businessDayEClass, BUSINESS_DAY__WAITERS);
    createEAttribute(businessDayEClass, BUSINESS_DAY__CLOSED);

    orderEClass = createEClass(ORDER);
    createEReference(orderEClass, ORDER__BUSINESS_DAY);
    createEReference(orderEClass, ORDER__TABLE);
    createEReference(orderEClass, ORDER__ORDER_DETAILS);
    createEAttribute(orderEClass, ORDER__NUMBER);

    orderDetailEClass = createEClass(ORDER_DETAIL);
    createEReference(orderDetailEClass, ORDER_DETAIL__ORDER);
    createEReference(orderDetailEClass, ORDER_DETAIL__OFFERING);
    createEAttribute(orderDetailEClass, ORDER_DETAIL__QUANTITY);
    createEAttribute(orderDetailEClass, ORDER_DETAIL__STATE);
    createEAttribute(orderDetailEClass, ORDER_DETAIL__PRICE);

    waiterEClass = createEClass(WAITER);
    createEReference(waiterEClass, WAITER__BUSINESS_DAY);
    createEReference(waiterEClass, WAITER__TABLES);
    createEReference(waiterEClass, WAITER__EMPLOYEE);
    createEAttribute(waiterEClass, WAITER__FROM);
    createEAttribute(waiterEClass, WAITER__UNTIL);

    // Create enums
    orderStateEEnum = createEEnum(ORDER_STATE);
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
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    InventoryPackage theInventoryPackage = (InventoryPackage)EPackage.Registry.INSTANCE.getEPackage(InventoryPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(businessDayEClass, BusinessDay.class, "BusinessDay", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBusinessDay_Date(), ecorePackage.getEDate(), "date", null, 0, 1, BusinessDay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBusinessDay_MenuCard(), theInventoryPackage.getMenuCard(), null, "menuCard", null, 1, 1, BusinessDay.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBusinessDay_Orders(), getOrder(), getOrder_BusinessDay(), "orders", null, 0, -1, BusinessDay.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBusinessDay_Waiters(), getWaiter(), getWaiter_BusinessDay(), "waiters", null, 1, -1, BusinessDay.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBusinessDay_Closed(), ecorePackage.getEBoolean(), "closed", null, 0, 1, BusinessDay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderEClass, Order.class, "Order", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrder_BusinessDay(), getBusinessDay(), getBusinessDay_Orders(), "businessDay", null, 1, 1, Order.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrder_Table(), theInventoryPackage.getTable(), null, "table", null, 1, 1, Order.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrder_OrderDetails(), getOrderDetail(), getOrderDetail_Order(), "orderDetails", null, 1, -1, Order.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOrder_Number(), ecorePackage.getEInt(), "number", null, 0, 1, Order.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orderDetailEClass, OrderDetail.class, "OrderDetail", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrderDetail_Order(), getOrder(), getOrder_OrderDetails(), "order", null, 1, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrderDetail_Offering(), theInventoryPackage.getOffering(), null, "offering", null, 1, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOrderDetail_Quantity(), ecorePackage.getEInt(), "quantity", null, 0, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOrderDetail_State(), getOrderState(), "state", null, 0, 1, OrderDetail.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOrderDetail_Price(), ecorePackage.getEFloat(), "price", null, 0, 1, OrderDetail.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(waiterEClass, Waiter.class, "Waiter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWaiter_BusinessDay(), getBusinessDay(), getBusinessDay_Waiters(), "businessDay", null, 0, 1, Waiter.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWaiter_Tables(), theInventoryPackage.getTable(), null, "tables", null, 0, -1, Waiter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWaiter_Employee(), theInventoryPackage.getEmployee(), null, "employee", null, 1, 1, Waiter.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWaiter_From(), ecorePackage.getEDate(), "from", null, 0, 1, Waiter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWaiter_Until(), ecorePackage.getEDate(), "until", null, 0, 1, Waiter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(orderStateEEnum, OrderState.class, "OrderState");
    addEEnumLiteral(orderStateEEnum, OrderState.ORDERED);
    addEEnumLiteral(orderStateEEnum, OrderState.ACKNOWLEDGED);
    addEEnumLiteral(orderStateEEnum, OrderState.PREPARED);
    addEEnumLiteral(orderStateEEnum, OrderState.SERVED);
    addEEnumLiteral(orderStateEEnum, OrderState.PAID);

    // Create resource
    createResource(eNS_URI);
  }

} // BusinessPackageImpl
