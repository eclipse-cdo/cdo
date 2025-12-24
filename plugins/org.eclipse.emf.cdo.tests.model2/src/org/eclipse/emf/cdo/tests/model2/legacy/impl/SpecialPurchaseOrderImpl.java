/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.legacy.impl.PurchaseOrderImpl;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Special Purchase Order</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.SpecialPurchaseOrderImpl#getDiscountCode <em>Discount Code</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.SpecialPurchaseOrderImpl#getShippingAddress <em>Shipping Address</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SpecialPurchaseOrderImpl extends PurchaseOrderImpl implements SpecialPurchaseOrder
{
  /**
   * The default value of the '{@link #getDiscountCode() <em>Discount Code</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDiscountCode()
   * @generated
   * @ordered
   */
  protected static final String DISCOUNT_CODE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDiscountCode() <em>Discount Code</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getDiscountCode()
   * @generated
   * @ordered
   */
  protected String discountCode = DISCOUNT_CODE_EDEFAULT;

  /**
   * The cached value of the '{@link #getShippingAddress() <em>Shipping Address</em>}' containment reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getShippingAddress()
   * @generated
   * @ordered
   */
  protected Address shippingAddress;

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
    return discountCode;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDiscountCode(String newDiscountCode)
  {
    String oldDiscountCode = discountCode;
    discountCode = newDiscountCode;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE, oldDiscountCode, discountCode));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Address getShippingAddress()
  {
    return shippingAddress;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetShippingAddress(Address newShippingAddress, NotificationChain msgs)
  {
    Address oldShippingAddress = shippingAddress;
    shippingAddress = newShippingAddress;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS, oldShippingAddress,
          newShippingAddress);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setShippingAddress(Address newShippingAddress)
  {
    if (newShippingAddress != shippingAddress)
    {
      NotificationChain msgs = null;
      if (shippingAddress != null)
      {
        msgs = ((InternalEObject)shippingAddress).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS, null,
            msgs);
      }
      if (newShippingAddress != null)
      {
        msgs = ((InternalEObject)newShippingAddress).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS, null,
            msgs);
      }
      msgs = basicSetShippingAddress(newShippingAddress, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS, newShippingAddress, newShippingAddress));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS:
      return basicSetShippingAddress(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE:
      return getDiscountCode();
    case Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS:
      return getShippingAddress();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE:
      setDiscountCode((String)newValue);
      return;
    case Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS:
      setShippingAddress((Address)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE:
      setDiscountCode(DISCOUNT_CODE_EDEFAULT);
      return;
    case Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS:
      setShippingAddress((Address)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE:
      return DISCOUNT_CODE_EDEFAULT == null ? discountCode != null : !DISCOUNT_CODE_EDEFAULT.equals(discountCode);
    case Model2Package.SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS:
      return shippingAddress != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (discountCode: ");
    result.append(discountCode);
    result.append(')');
    return result.toString();
  }

} // SpecialPurchaseOrderImpl
