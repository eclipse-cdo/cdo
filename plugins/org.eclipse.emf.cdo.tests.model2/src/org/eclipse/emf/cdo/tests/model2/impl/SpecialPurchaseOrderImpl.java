/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Special Purchase Order</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl#getDiscountCode <em>Discount Code</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl#getShippingAddress <em>Shipping Address</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SpecialPurchaseOrderImpl extends PurchaseOrderImpl implements SpecialPurchaseOrder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected SpecialPurchaseOrderImpl()
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
    return Model2Package.eINSTANCE.getSpecialPurchaseOrder();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDiscountCode()
  {
    return (String)eGet(Model2Package.eINSTANCE.getSpecialPurchaseOrder_DiscountCode(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDiscountCode(String newDiscountCode)
  {
    eSet(Model2Package.eINSTANCE.getSpecialPurchaseOrder_DiscountCode(), newDiscountCode);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Address getShippingAddress()
  {
    return (Address)eGet(Model2Package.eINSTANCE.getSpecialPurchaseOrder_ShippingAddress(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setShippingAddress(Address newShippingAddress)
  {
    eSet(Model2Package.eINSTANCE.getSpecialPurchaseOrder_ShippingAddress(), newShippingAddress);
  }

} // SpecialPurchaseOrderImpl
