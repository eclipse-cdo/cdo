/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.examples.company.SalesOrder;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Sales Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl#getCustomer <em>Customer</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SalesOrderImpl extends OrderImpl implements SalesOrder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected SalesOrderImpl()
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
    return CompanyPackage.Literals.SALES_ORDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getId()
  {
    return (Integer)eGet(CompanyPackage.Literals.SALES_ORDER__ID, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(int newId)
  {
    eSet(CompanyPackage.Literals.SALES_ORDER__ID, newId);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Customer getCustomer()
  {
    return (Customer)eGet(CompanyPackage.Literals.SALES_ORDER__CUSTOMER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCustomer(Customer newCustomer)
  {
    eSet(CompanyPackage.Literals.SALES_ORDER__CUSTOMER, newCustomer);
  }

} // SalesOrderImpl
