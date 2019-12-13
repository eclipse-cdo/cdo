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
import org.eclipse.emf.cdo.examples.company.PurchaseOrder;
import org.eclipse.emf.cdo.examples.company.Supplier;

import org.eclipse.emf.ecore.EClass;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Purchase Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl#getSupplier <em>Supplier</em>}</li>
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
    return CompanyPackage.Literals.PURCHASE_ORDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getDate()
  {
    return (Date)eGet(CompanyPackage.Literals.PURCHASE_ORDER__DATE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDate(Date newDate)
  {
    eSet(CompanyPackage.Literals.PURCHASE_ORDER__DATE, newDate);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Supplier getSupplier()
  {
    return (Supplier)eGet(CompanyPackage.Literals.PURCHASE_ORDER__SUPPLIER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSupplier(Supplier newSupplier)
  {
    eSet(CompanyPackage.Literals.PURCHASE_ORDER__SUPPLIER, newSupplier);
  }

} // PurchaseOrderImpl
