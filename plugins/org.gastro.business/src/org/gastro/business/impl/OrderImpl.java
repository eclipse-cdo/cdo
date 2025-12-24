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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.gastro.business.BusinessDay;
import org.gastro.business.BusinessPackage;
import org.gastro.business.Order;
import org.gastro.business.OrderDetail;
import org.gastro.inventory.Table;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.gastro.business.impl.OrderImpl#getBusinessDay <em>Business Day</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderImpl#getTable <em>Table</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderImpl#getOrderDetails <em>Order Details</em>}</li>
 *   <li>{@link org.gastro.business.impl.OrderImpl#getNumber <em>Number</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OrderImpl extends CDOObjectImpl implements Order
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected OrderImpl()
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
    return BusinessPackage.Literals.ORDER;
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
  public BusinessDay getBusinessDay()
  {
    return (BusinessDay)eGet(BusinessPackage.Literals.ORDER__BUSINESS_DAY, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBusinessDay(BusinessDay newBusinessDay)
  {
    eSet(BusinessPackage.Literals.ORDER__BUSINESS_DAY, newBusinessDay);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Table getTable()
  {
    return (Table)eGet(BusinessPackage.Literals.ORDER__TABLE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTable(Table newTable)
  {
    eSet(BusinessPackage.Literals.ORDER__TABLE, newTable);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<OrderDetail> getOrderDetails()
  {
    return (EList<OrderDetail>)eGet(BusinessPackage.Literals.ORDER__ORDER_DETAILS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getNumber()
  {
    return (Integer)eGet(BusinessPackage.Literals.ORDER__NUMBER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNumber(int newNumber)
  {
    eSet(BusinessPackage.Literals.ORDER__NUMBER, newNumber);
  }

} // OrderImpl
