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
import org.gastro.business.Waiter;
import org.gastro.inventory.Employee;
import org.gastro.inventory.Table;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Waiter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.gastro.business.impl.WaiterImpl#getBusinessDay <em>Business Day</em>}</li>
 *   <li>{@link org.gastro.business.impl.WaiterImpl#getTables <em>Tables</em>}</li>
 *   <li>{@link org.gastro.business.impl.WaiterImpl#getEmployee <em>Employee</em>}</li>
 *   <li>{@link org.gastro.business.impl.WaiterImpl#getFrom <em>From</em>}</li>
 *   <li>{@link org.gastro.business.impl.WaiterImpl#getUntil <em>Until</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaiterImpl extends CDOObjectImpl implements Waiter
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected WaiterImpl()
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
    return BusinessPackage.Literals.WAITER;
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
    return (BusinessDay)eGet(BusinessPackage.Literals.WAITER__BUSINESS_DAY, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBusinessDay(BusinessDay newBusinessDay)
  {
    eSet(BusinessPackage.Literals.WAITER__BUSINESS_DAY, newBusinessDay);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Table> getTables()
  {
    return (EList<Table>)eGet(BusinessPackage.Literals.WAITER__TABLES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Employee getEmployee()
  {
    return (Employee)eGet(BusinessPackage.Literals.WAITER__EMPLOYEE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEmployee(Employee newEmployee)
  {
    eSet(BusinessPackage.Literals.WAITER__EMPLOYEE, newEmployee);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getFrom()
  {
    return (Date)eGet(BusinessPackage.Literals.WAITER__FROM, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFrom(Date newFrom)
  {
    eSet(BusinessPackage.Literals.WAITER__FROM, newFrom);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getUntil()
  {
    return (Date)eGet(BusinessPackage.Literals.WAITER__UNTIL, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUntil(Date newUntil)
  {
    eSet(BusinessPackage.Literals.WAITER__UNTIL, newUntil);
  }

} // WaiterImpl
