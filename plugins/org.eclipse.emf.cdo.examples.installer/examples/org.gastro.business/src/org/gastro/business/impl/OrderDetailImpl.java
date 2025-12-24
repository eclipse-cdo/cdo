/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.gastro.business.BusinessPackage;
import org.gastro.business.Order;
import org.gastro.business.OrderDetail;
import org.gastro.business.OrderState;
import org.gastro.inventory.Offering;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Detail</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.gastro.business.impl.OrderDetailImpl#getOrder <em>Order</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderDetailImpl#getOffering <em>Offering</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderDetailImpl#getQuantity <em>Quantity</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderDetailImpl#getState <em>State</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderDetailImpl#getPrice <em>Price</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OrderDetailImpl extends CDOObjectImpl implements OrderDetail
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected OrderDetailImpl()
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
    return BusinessPackage.Literals.ORDER_DETAIL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Order getOrder()
  {
    return (Order)eGet(BusinessPackage.Literals.ORDER_DETAIL__ORDER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOrder(Order newOrder)
  {
    eSet(BusinessPackage.Literals.ORDER_DETAIL__ORDER, newOrder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Offering getOffering()
  {
    return (Offering)eGet(BusinessPackage.Literals.ORDER_DETAIL__OFFERING, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOffering(Offering newOffering)
  {
    eSet(BusinessPackage.Literals.ORDER_DETAIL__OFFERING, newOffering);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getQuantity()
  {
    return (Integer)eGet(BusinessPackage.Literals.ORDER_DETAIL__QUANTITY, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setQuantity(int newQuantity)
  {
    eSet(BusinessPackage.Literals.ORDER_DETAIL__QUANTITY, newQuantity);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public OrderState getState()
  {
    return (OrderState)eGet(BusinessPackage.Literals.ORDER_DETAIL__STATE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setState(OrderState newState)
  {
    eSet(BusinessPackage.Literals.ORDER_DETAIL__STATE, newState);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public float getPrice()
  {
    Offering offering = getOffering();
    if (offering != null)
    {
      return offering.getPrice() * getQuantity();
    }

    return 0f;
  }
} // OrderDetailImpl
