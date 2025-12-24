/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Purchase Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl#getSupplier <em>Supplier</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PurchaseOrderImpl extends OrderImpl implements PurchaseOrder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected PurchaseOrderImpl()
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
    return Model1Package.eINSTANCE.getPurchaseOrder();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getDate()
  {
    return (Date)eGet(Model1Package.eINSTANCE.getPurchaseOrder_Date(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDate(Date newDate)
  {
    eSet(Model1Package.eINSTANCE.getPurchaseOrder_Date(), newDate);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Supplier getSupplier()
  {
    return (Supplier)eGet(Model1Package.eINSTANCE.getPurchaseOrder_Supplier(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSupplier(Supplier newSupplier)
  {
    eSet(Model1Package.eINSTANCE.getPurchaseOrder_Supplier(), newSupplier);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<SalesOrder> getSalesOrders()
  {
    return (EList<SalesOrder>)eGet(Model1Package.eINSTANCE.getPurchaseOrder_SalesOrders(), true);
  }

} // PurchaseOrderImpl
