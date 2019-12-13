/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.gastro.business.Waiter;
import org.gastro.inventory.MenuCard;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Day</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.gastro.business.impl.BusinessDayImpl#getDate <em>Date</em>}</li>
 *   <li>{@link org.gastro.business.impl.BusinessDayImpl#getMenuCard <em>Menu Card</em>}</li>
 *   <li>{@link org.gastro.business.impl.BusinessDayImpl#getOrders <em>Orders</em>}</li>
 *   <li>{@link org.gastro.business.impl.BusinessDayImpl#getWaiters <em>Waiters</em>}</li>
 *   <li>{@link org.gastro.business.impl.BusinessDayImpl#isClosed <em>Closed</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BusinessDayImpl extends CDOObjectImpl implements BusinessDay
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BusinessDayImpl()
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
    return BusinessPackage.Literals.BUSINESS_DAY;
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
  public Date getDate()
  {
    return (Date)eGet(BusinessPackage.Literals.BUSINESS_DAY__DATE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDate(Date newDate)
  {
    eSet(BusinessPackage.Literals.BUSINESS_DAY__DATE, newDate);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MenuCard getMenuCard()
  {
    return (MenuCard)eGet(BusinessPackage.Literals.BUSINESS_DAY__MENU_CARD, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMenuCard(MenuCard newMenuCard)
  {
    eSet(BusinessPackage.Literals.BUSINESS_DAY__MENU_CARD, newMenuCard);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Order> getOrders()
  {
    return (EList<Order>)eGet(BusinessPackage.Literals.BUSINESS_DAY__ORDERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Waiter> getWaiters()
  {
    return (EList<Waiter>)eGet(BusinessPackage.Literals.BUSINESS_DAY__WAITERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isClosed()
  {
    return (Boolean)eGet(BusinessPackage.Literals.BUSINESS_DAY__CLOSED, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setClosed(boolean newClosed)
  {
    eSet(BusinessPackage.Literals.BUSINESS_DAY__CLOSED, newClosed);
  }

} // BusinessDayImpl
