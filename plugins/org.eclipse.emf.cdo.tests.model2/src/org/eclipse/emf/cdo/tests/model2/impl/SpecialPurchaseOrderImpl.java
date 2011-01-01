/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl#getDiscountCode <em>Discount Code</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl#getShippingAddress <em>Shipping Address
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class SpecialPurchaseOrderImpl extends PurchaseOrderImpl implements SpecialPurchaseOrder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected SpecialPurchaseOrderImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model2Package.Literals.SPECIAL_PURCHASE_ORDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDiscountCode()
  {
    return (String)eGet(Model2Package.Literals.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDiscountCode(String newDiscountCode)
  {
    eSet(Model2Package.Literals.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE, newDiscountCode);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Address getShippingAddress()
  {
    return (Address)eGet(Model2Package.Literals.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setShippingAddress(Address newShippingAddress)
  {
    eSet(Model2Package.Literals.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS, newShippingAddress);
  }

} // SpecialPurchaseOrderImpl
