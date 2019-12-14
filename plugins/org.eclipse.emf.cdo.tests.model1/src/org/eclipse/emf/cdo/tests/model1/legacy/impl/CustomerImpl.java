/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Customer</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CustomerImpl#getSalesOrders <em>Sales Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CustomerImpl#getOrderByProduct <em>Order By Product</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CustomerImpl extends AddressImpl implements Customer
{
  /**
   * The cached value of the '{@link #getSalesOrders() <em>Sales Orders</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSalesOrders()
   * @generated
   * @ordered
   */
  protected EList<SalesOrder> salesOrders;

  /**
   * The cached value of the '{@link #getOrderByProduct() <em>Order By Product</em>}' map.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getOrderByProduct()
   * @generated
   * @ordered
   */
  protected EMap<Product1, SalesOrder> orderByProduct;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CustomerImpl()
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
    return Model1Package.eINSTANCE.getCustomer();
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
      salesOrders = new EObjectWithInverseResolvingEList<>(SalesOrder.class, this, Model1Package.CUSTOMER__SALES_ORDERS,
          Model1Package.SALES_ORDER__CUSTOMER);
    }
    return salesOrders;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<Product1, SalesOrder> getOrderByProduct()
  {
    if (orderByProduct == null)
    {
      orderByProduct = new EcoreEMap<>(Model1Package.eINSTANCE.getProductToOrder(), ProductToOrderImpl.class, this,
          Model1Package.CUSTOMER__ORDER_BY_PRODUCT);
    }
    return orderByProduct;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.CUSTOMER__SALES_ORDERS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getSalesOrders()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case Model1Package.CUSTOMER__SALES_ORDERS:
      return ((InternalEList<?>)getSalesOrders()).basicRemove(otherEnd, msgs);
    case Model1Package.CUSTOMER__ORDER_BY_PRODUCT:
      return ((InternalEList<?>)getOrderByProduct()).basicRemove(otherEnd, msgs);
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
    case Model1Package.CUSTOMER__SALES_ORDERS:
      return getSalesOrders();
    case Model1Package.CUSTOMER__ORDER_BY_PRODUCT:
      if (coreType)
      {
        return getOrderByProduct();
      }
      else
      {
        return getOrderByProduct().map();
      }
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
    case Model1Package.CUSTOMER__SALES_ORDERS:
      getSalesOrders().clear();
      getSalesOrders().addAll((Collection<? extends SalesOrder>)newValue);
      return;
    case Model1Package.CUSTOMER__ORDER_BY_PRODUCT:
      ((EStructuralFeature.Setting)getOrderByProduct()).set(newValue);
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
    case Model1Package.CUSTOMER__SALES_ORDERS:
      getSalesOrders().clear();
      return;
    case Model1Package.CUSTOMER__ORDER_BY_PRODUCT:
      getOrderByProduct().clear();
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
    case Model1Package.CUSTOMER__SALES_ORDERS:
      return salesOrders != null && !salesOrders.isEmpty();
    case Model1Package.CUSTOMER__ORDER_BY_PRODUCT:
      return orderByProduct != null && !orderByProduct.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // CustomerImpl
