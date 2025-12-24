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

import org.gastro.inventory.Employee;
import org.gastro.inventory.Table;

import java.util.Date;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Waiter</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.gastro.business.Waiter#getBusinessDay <em>Business Day</em>}</li>
 *   <li>{@link org.gastro.business.Waiter#getTables <em>Tables</em>}</li>
 *   <li>{@link org.gastro.business.Waiter#getEmployee <em>Employee</em>}</li>
 *   <li>{@link org.gastro.business.Waiter#getFrom <em>From</em>}</li>
 *   <li>{@link org.gastro.business.Waiter#getUntil <em>Until</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.business.BusinessPackage#getWaiter()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Waiter extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Business Day</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.gastro.business.BusinessDay#getWaiters <em>Waiters</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Business Day</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Business Day</em>' container reference.
   * @see #setBusinessDay(BusinessDay)
   * @see org.gastro.business.BusinessPackage#getWaiter_BusinessDay()
   * @see org.gastro.business.BusinessDay#getWaiters
   * @model opposite="waiters" transient="false"
   * @generated
   */
  BusinessDay getBusinessDay();

  /**
   * Sets the value of the '{@link org.gastro.business.Waiter#getBusinessDay <em>Business Day</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Business Day</em>' container reference.
   * @see #getBusinessDay()
   * @generated
   */
  void setBusinessDay(BusinessDay value);

  /**
   * Returns the value of the '<em><b>Tables</b></em>' reference list.
   * The list contents are of type {@link org.gastro.inventory.Table}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tables</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tables</em>' reference list.
   * @see org.gastro.business.BusinessPackage#getWaiter_Tables()
   * @model
   * @generated
   */
  EList<Table> getTables();

  /**
   * Returns the value of the '<em><b>Employee</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Employee</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Employee</em>' reference.
   * @see #setEmployee(Employee)
   * @see org.gastro.business.BusinessPackage#getWaiter_Employee()
   * @model required="true"
   * @generated
   */
  Employee getEmployee();

  /**
   * Sets the value of the '{@link org.gastro.business.Waiter#getEmployee <em>Employee</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Employee</em>' reference.
   * @see #getEmployee()
   * @generated
   */
  void setEmployee(Employee value);

  /**
   * Returns the value of the '<em><b>From</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>From</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>From</em>' attribute.
   * @see #setFrom(Date)
   * @see org.gastro.business.BusinessPackage#getWaiter_From()
   * @model
   * @generated
   */
  Date getFrom();

  /**
   * Sets the value of the '{@link org.gastro.business.Waiter#getFrom <em>From</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>From</em>' attribute.
   * @see #getFrom()
   * @generated
   */
  void setFrom(Date value);

  /**
   * Returns the value of the '<em><b>Until</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Until</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Until</em>' attribute.
   * @see #setUntil(Date)
   * @see org.gastro.business.BusinessPackage#getWaiter_Until()
   * @model
   * @generated
   */
  Date getUntil();

  /**
   * Sets the value of the '{@link org.gastro.business.Waiter#getUntil <em>Until</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Until</em>' attribute.
   * @see #getUntil()
   * @generated
   */
  void setUntil(Date value);

} // Waiter
