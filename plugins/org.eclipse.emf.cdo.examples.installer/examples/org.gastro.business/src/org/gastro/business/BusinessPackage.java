/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
package org.gastro.business;

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
 * @see org.gastro.business.BusinessFactory
 * @model kind="package"
 * @generated
 */
public interface BusinessPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "business";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.gastro.org/business/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "business";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  BusinessPackage eINSTANCE = org.gastro.business.impl.BusinessPackageImpl.init();

  /**
   * The meta object id for the '{@link org.gastro.business.impl.BusinessDayImpl <em>Day</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.business.impl.BusinessDayImpl
   * @see org.gastro.business.impl.BusinessPackageImpl#getBusinessDay()
   * @generated
   */
  int BUSINESS_DAY = 0;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUSINESS_DAY__DATE = 0;

  /**
   * The feature id for the '<em><b>Menu Card</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUSINESS_DAY__MENU_CARD = 1;

  /**
   * The feature id for the '<em><b>Orders</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int BUSINESS_DAY__ORDERS = 2;

  /**
   * The feature id for the '<em><b>Waiters</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int BUSINESS_DAY__WAITERS = 3;

  /**
   * The feature id for the '<em><b>Closed</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUSINESS_DAY__CLOSED = 4;

  /**
   * The number of structural features of the '<em>Day</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUSINESS_DAY_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.gastro.business.impl.OrderImpl <em>Order</em>}' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.gastro.business.impl.OrderImpl
   * @see org.gastro.business.impl.BusinessPackageImpl#getOrder()
   * @generated
   */
  int ORDER = 1;

  /**
   * The feature id for the '<em><b>Business Day</b></em>' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER__BUSINESS_DAY = 0;

  /**
   * The feature id for the '<em><b>Table</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER__TABLE = 1;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER__ORDER_DETAILS = 2;

  /**
   * The feature id for the '<em><b>Number</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER__NUMBER = 3;

  /**
   * The number of structural features of the '<em>Order</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.gastro.business.impl.OrderDetailImpl <em>Order Detail</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.gastro.business.impl.OrderDetailImpl
   * @see org.gastro.business.impl.BusinessPackageImpl#getOrderDetail()
   * @generated
   */
  int ORDER_DETAIL = 2;

  /**
   * The feature id for the '<em><b>Order</b></em>' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__ORDER = 0;

  /**
   * The feature id for the '<em><b>Offering</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__OFFERING = 1;

  /**
   * The feature id for the '<em><b>Quantity</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__QUANTITY = 2;

  /**
   * The feature id for the '<em><b>State</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__STATE = 3;

  /**
   * The feature id for the '<em><b>Price</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__PRICE = 4;

  /**
   * The number of structural features of the '<em>Order Detail</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int ORDER_DETAIL_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.gastro.business.impl.WaiterImpl <em>Waiter</em>}' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.gastro.business.impl.WaiterImpl
   * @see org.gastro.business.impl.BusinessPackageImpl#getWaiter()
   * @generated
   */
  int WAITER = 3;

  /**
   * The feature id for the '<em><b>Business Day</b></em>' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int WAITER__BUSINESS_DAY = 0;

  /**
   * The feature id for the '<em><b>Tables</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WAITER__TABLES = 1;

  /**
   * The feature id for the '<em><b>Employee</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WAITER__EMPLOYEE = 2;

  /**
   * The feature id for the '<em><b>From</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WAITER__FROM = 3;

  /**
   * The feature id for the '<em><b>Until</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WAITER__UNTIL = 4;

  /**
   * The number of structural features of the '<em>Waiter</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WAITER_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.gastro.business.OrderState <em>Order State</em>}' enum.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.gastro.business.OrderState
   * @see org.gastro.business.impl.BusinessPackageImpl#getOrderState()
   * @generated
   */
  int ORDER_STATE = 4;

  /**
   * Returns the meta object for class '{@link org.gastro.business.BusinessDay <em>Day</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Day</em>'.
   * @see org.gastro.business.BusinessDay
   * @generated
   */
  EClass getBusinessDay();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.BusinessDay#getDate <em>Date</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Date</em>'.
   * @see org.gastro.business.BusinessDay#getDate()
   * @see #getBusinessDay()
   * @generated
   */
  EAttribute getBusinessDay_Date();

  /**
   * Returns the meta object for the reference '{@link org.gastro.business.BusinessDay#getMenuCard <em>Menu Card</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Menu Card</em>'.
   * @see org.gastro.business.BusinessDay#getMenuCard()
   * @see #getBusinessDay()
   * @generated
   */
  EReference getBusinessDay_MenuCard();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.business.BusinessDay#getOrders <em>Orders</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Orders</em>'.
   * @see org.gastro.business.BusinessDay#getOrders()
   * @see #getBusinessDay()
   * @generated
   */
  EReference getBusinessDay_Orders();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.business.BusinessDay#getWaiters <em>Waiters</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Waiters</em>'.
   * @see org.gastro.business.BusinessDay#getWaiters()
   * @see #getBusinessDay()
   * @generated
   */
  EReference getBusinessDay_Waiters();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.BusinessDay#isClosed <em>Closed</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Closed</em>'.
   * @see org.gastro.business.BusinessDay#isClosed()
   * @see #getBusinessDay()
   * @generated
   */
  EAttribute getBusinessDay_Closed();

  /**
   * Returns the meta object for class '{@link org.gastro.business.Order <em>Order</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for class '<em>Order</em>'.
   * @see org.gastro.business.Order
   * @generated
   */
  EClass getOrder();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.business.Order#getBusinessDay <em>Business Day</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Business Day</em>'.
   * @see org.gastro.business.Order#getBusinessDay()
   * @see #getOrder()
   * @generated
   */
  EReference getOrder_BusinessDay();

  /**
   * Returns the meta object for the reference '{@link org.gastro.business.Order#getTable <em>Table</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Table</em>'.
   * @see org.gastro.business.Order#getTable()
   * @see #getOrder()
   * @generated
   */
  EReference getOrder_Table();

  /**
   * Returns the meta object for the containment reference list '{@link org.gastro.business.Order#getOrderDetails <em>Order Details</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Order Details</em>'.
   * @see org.gastro.business.Order#getOrderDetails()
   * @see #getOrder()
   * @generated
   */
  EReference getOrder_OrderDetails();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.Order#getNumber <em>Number</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Number</em>'.
   * @see org.gastro.business.Order#getNumber()
   * @see #getOrder()
   * @generated
   */
  EAttribute getOrder_Number();

  /**
   * Returns the meta object for class '{@link org.gastro.business.OrderDetail <em>Order Detail</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Order Detail</em>'.
   * @see org.gastro.business.OrderDetail
   * @generated
   */
  EClass getOrderDetail();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.business.OrderDetail#getOrder <em>Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Order</em>'.
   * @see org.gastro.business.OrderDetail#getOrder()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Order();

  /**
   * Returns the meta object for the reference '{@link org.gastro.business.OrderDetail#getOffering <em>Offering</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Offering</em>'.
   * @see org.gastro.business.OrderDetail#getOffering()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Offering();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.OrderDetail#getQuantity <em>Quantity</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Quantity</em>'.
   * @see org.gastro.business.OrderDetail#getQuantity()
   * @see #getOrderDetail()
   * @generated
   */
  EAttribute getOrderDetail_Quantity();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.OrderDetail#getState <em>State</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>State</em>'.
   * @see org.gastro.business.OrderDetail#getState()
   * @see #getOrderDetail()
   * @generated
   */
  EAttribute getOrderDetail_State();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.OrderDetail#getPrice <em>Price</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Price</em>'.
   * @see org.gastro.business.OrderDetail#getPrice()
   * @see #getOrderDetail()
   * @generated
   */
  EAttribute getOrderDetail_Price();

  /**
   * Returns the meta object for class '{@link org.gastro.business.Waiter <em>Waiter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Waiter</em>'.
   * @see org.gastro.business.Waiter
   * @generated
   */
  EClass getWaiter();

  /**
   * Returns the meta object for the container reference '{@link org.gastro.business.Waiter#getBusinessDay <em>Business Day</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Business Day</em>'.
   * @see org.gastro.business.Waiter#getBusinessDay()
   * @see #getWaiter()
   * @generated
   */
  EReference getWaiter_BusinessDay();

  /**
   * Returns the meta object for the reference list '{@link org.gastro.business.Waiter#getTables <em>Tables</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Tables</em>'.
   * @see org.gastro.business.Waiter#getTables()
   * @see #getWaiter()
   * @generated
   */
  EReference getWaiter_Tables();

  /**
   * Returns the meta object for the reference '{@link org.gastro.business.Waiter#getEmployee <em>Employee</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Employee</em>'.
   * @see org.gastro.business.Waiter#getEmployee()
   * @see #getWaiter()
   * @generated
   */
  EReference getWaiter_Employee();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.Waiter#getFrom <em>From</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>From</em>'.
   * @see org.gastro.business.Waiter#getFrom()
   * @see #getWaiter()
   * @generated
   */
  EAttribute getWaiter_From();

  /**
   * Returns the meta object for the attribute '{@link org.gastro.business.Waiter#getUntil <em>Until</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Until</em>'.
   * @see org.gastro.business.Waiter#getUntil()
   * @see #getWaiter()
   * @generated
   */
  EAttribute getWaiter_Until();

  /**
   * Returns the meta object for enum '{@link org.gastro.business.OrderState <em>Order State</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for enum '<em>Order State</em>'.
   * @see org.gastro.business.OrderState
   * @generated
   */
  EEnum getOrderState();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  BusinessFactory getBusinessFactory();

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
     * The meta object literal for the '{@link org.gastro.business.impl.BusinessDayImpl <em>Day</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.business.impl.BusinessDayImpl
     * @see org.gastro.business.impl.BusinessPackageImpl#getBusinessDay()
     * @generated
     */
    EClass BUSINESS_DAY = eINSTANCE.getBusinessDay();

    /**
     * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute BUSINESS_DAY__DATE = eINSTANCE.getBusinessDay_Date();

    /**
     * The meta object literal for the '<em><b>Menu Card</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference BUSINESS_DAY__MENU_CARD = eINSTANCE.getBusinessDay_MenuCard();

    /**
     * The meta object literal for the '<em><b>Orders</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference BUSINESS_DAY__ORDERS = eINSTANCE.getBusinessDay_Orders();

    /**
     * The meta object literal for the '<em><b>Waiters</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference BUSINESS_DAY__WAITERS = eINSTANCE.getBusinessDay_Waiters();

    /**
     * The meta object literal for the '<em><b>Closed</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute BUSINESS_DAY__CLOSED = eINSTANCE.getBusinessDay_Closed();

    /**
     * The meta object literal for the '{@link org.gastro.business.impl.OrderImpl <em>Order</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.business.impl.OrderImpl
     * @see org.gastro.business.impl.BusinessPackageImpl#getOrder()
     * @generated
     */
    EClass ORDER = eINSTANCE.getOrder();

    /**
     * The meta object literal for the '<em><b>Business Day</b></em>' container reference feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference ORDER__BUSINESS_DAY = eINSTANCE.getOrder_BusinessDay();

    /**
     * The meta object literal for the '<em><b>Table</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference ORDER__TABLE = eINSTANCE.getOrder_Table();

    /**
     * The meta object literal for the '<em><b>Order Details</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference ORDER__ORDER_DETAILS = eINSTANCE.getOrder_OrderDetails();

    /**
     * The meta object literal for the '<em><b>Number</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute ORDER__NUMBER = eINSTANCE.getOrder_Number();

    /**
     * The meta object literal for the '{@link org.gastro.business.impl.OrderDetailImpl <em>Order Detail</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.gastro.business.impl.OrderDetailImpl
     * @see org.gastro.business.impl.BusinessPackageImpl#getOrderDetail()
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
     * The meta object literal for the '<em><b>Offering</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference ORDER_DETAIL__OFFERING = eINSTANCE.getOrderDetail_Offering();

    /**
     * The meta object literal for the '<em><b>Quantity</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute ORDER_DETAIL__QUANTITY = eINSTANCE.getOrderDetail_Quantity();

    /**
     * The meta object literal for the '<em><b>State</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute ORDER_DETAIL__STATE = eINSTANCE.getOrderDetail_State();

    /**
     * The meta object literal for the '<em><b>Price</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute ORDER_DETAIL__PRICE = eINSTANCE.getOrderDetail_Price();

    /**
     * The meta object literal for the '{@link org.gastro.business.impl.WaiterImpl <em>Waiter</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.business.impl.WaiterImpl
     * @see org.gastro.business.impl.BusinessPackageImpl#getWaiter()
     * @generated
     */
    EClass WAITER = eINSTANCE.getWaiter();

    /**
     * The meta object literal for the '<em><b>Business Day</b></em>' container reference feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference WAITER__BUSINESS_DAY = eINSTANCE.getWaiter_BusinessDay();

    /**
     * The meta object literal for the '<em><b>Tables</b></em>' reference list feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference WAITER__TABLES = eINSTANCE.getWaiter_Tables();

    /**
     * The meta object literal for the '<em><b>Employee</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference WAITER__EMPLOYEE = eINSTANCE.getWaiter_Employee();

    /**
     * The meta object literal for the '<em><b>From</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute WAITER__FROM = eINSTANCE.getWaiter_From();

    /**
     * The meta object literal for the '<em><b>Until</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute WAITER__UNTIL = eINSTANCE.getWaiter_Until();

    /**
     * The meta object literal for the '{@link org.gastro.business.OrderState <em>Order State</em>}' enum. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.gastro.business.OrderState
     * @see org.gastro.business.impl.BusinessPackageImpl#getOrderState()
     * @generated
     */
    EEnum ORDER_STATE = eINSTANCE.getOrderState();

  }

} // BusinessPackage
