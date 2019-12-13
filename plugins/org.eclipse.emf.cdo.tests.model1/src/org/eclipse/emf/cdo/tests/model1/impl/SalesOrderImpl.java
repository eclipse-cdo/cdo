/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Sales Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl#getCustomer <em>Customer</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
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
    return Model1Package.eINSTANCE.getSalesOrder();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getId()
  {
    return (Integer)eGet(Model1Package.eINSTANCE.getSalesOrder_Id(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(int newId)
  {
    eSet(Model1Package.eINSTANCE.getSalesOrder_Id(), newId);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Customer getCustomer()
  {
    return (Customer)eGet(Model1Package.eINSTANCE.getSalesOrder_Customer(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCustomer(Customer newCustomer)
  {
    eSet(Model1Package.eINSTANCE.getSalesOrder_Customer(), newCustomer);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<PurchaseOrder> getPurchaseOrders()
  {
    return (EList<PurchaseOrder>)eGet(Model1Package.eINSTANCE.getSalesOrder_PurchaseOrders(), true);
  }

} // SalesOrderImpl
