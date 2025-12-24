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

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

import org.gastro.inventory.MenuCard;

import java.util.Date;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Day</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.gastro.business.BusinessDay#getDate <em>Date</em>}</li>
 *   <li>{@link org.gastro.business.BusinessDay#getMenuCard <em>Menu Card</em>}</li>
 *   <li>{@link org.gastro.business.BusinessDay#getOrders <em>Orders</em>}</li>
 *   <li>{@link org.gastro.business.BusinessDay#getWaiters <em>Waiters</em>}</li>
 *   <li>{@link org.gastro.business.BusinessDay#isClosed <em>Closed</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.business.BusinessPackage#getBusinessDay()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface BusinessDay extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Date</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Date</em>' attribute.
   * @see #setDate(Date)
   * @see org.gastro.business.BusinessPackage#getBusinessDay_Date()
   * @model
   * @generated
   */
  Date getDate();

  /**
   * Sets the value of the '{@link org.gastro.business.BusinessDay#getDate <em>Date</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Date</em>' attribute.
   * @see #getDate()
   * @generated
   */
  void setDate(Date value);

  /**
   * Returns the value of the '<em><b>Menu Card</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Menu Card</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Menu Card</em>' reference.
   * @see #setMenuCard(MenuCard)
   * @see org.gastro.business.BusinessPackage#getBusinessDay_MenuCard()
   * @model required="true"
   * @generated
   */
  MenuCard getMenuCard();

  /**
   * Sets the value of the '{@link org.gastro.business.BusinessDay#getMenuCard <em>Menu Card</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Menu Card</em>' reference.
   * @see #getMenuCard()
   * @generated
   */
  void setMenuCard(MenuCard value);

  /**
   * Returns the value of the '<em><b>Orders</b></em>' containment reference list.
   * The list contents are of type {@link org.gastro.business.Order}.
   * It is bidirectional and its opposite is '{@link org.gastro.business.Order#getBusinessDay <em>Business Day</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Orders</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Orders</em>' containment reference list.
   * @see org.gastro.business.BusinessPackage#getBusinessDay_Orders()
   * @see org.gastro.business.Order#getBusinessDay
   * @model opposite="businessDay" containment="true"
   * @generated
   */
  EList<Order> getOrders();

  /**
   * Returns the value of the '<em><b>Waiters</b></em>' containment reference list.
   * The list contents are of type {@link org.gastro.business.Waiter}.
   * It is bidirectional and its opposite is '{@link org.gastro.business.Waiter#getBusinessDay <em>Business Day</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Waiters</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Waiters</em>' containment reference list.
   * @see org.gastro.business.BusinessPackage#getBusinessDay_Waiters()
   * @see org.gastro.business.Waiter#getBusinessDay
   * @model opposite="businessDay" containment="true" required="true"
   * @generated
   */
  EList<Waiter> getWaiters();

  /**
   * Returns the value of the '<em><b>Closed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Closed</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Closed</em>' attribute.
   * @see #setClosed(boolean)
   * @see org.gastro.business.BusinessPackage#getBusinessDay_Closed()
   * @model
   * @generated
   */
  boolean isClosed();

  /**
   * Sets the value of the '{@link org.gastro.business.BusinessDay#isClosed <em>Closed</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Closed</em>' attribute.
   * @see #isClosed()
   * @generated
   */
  void setClosed(boolean value);

} // BusinessDay
