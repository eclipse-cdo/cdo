/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Supplier</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.SupplierImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.SupplierImpl#isPreferred <em>Preferred</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SupplierImpl extends AddressImpl implements Supplier
{
  /**
   * The cached value of the '{@link #getPurchaseOrders() <em>Purchase Orders</em>}' reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getPurchaseOrders()
   * @generated
   * @ordered
   */
  protected EList<PurchaseOrder> purchaseOrders;

  /**
   * The default value of the '{@link #isPreferred() <em>Preferred</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #isPreferred()
   * @generated
   * @ordered
   */
  protected static final boolean PREFERRED_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isPreferred() <em>Preferred</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #isPreferred()
   * @generated
   * @ordered
   */
  protected boolean preferred = PREFERRED_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected SupplierImpl()
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
    return Model1Package.eINSTANCE.getSupplier();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<PurchaseOrder> getPurchaseOrders()
  {
    if (purchaseOrders == null)
    {
      purchaseOrders = new EObjectWithInverseResolvingEList<PurchaseOrder>(PurchaseOrder.class, this, Model1Package.SUPPLIER__PURCHASE_ORDERS,
          Model1Package.PURCHASE_ORDER__SUPPLIER);
    }
    return purchaseOrders;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isPreferred()
  {
    return preferred;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPreferred(boolean newPreferred)
  {
    boolean oldPreferred = preferred;
    preferred = newPreferred;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.SUPPLIER__PREFERRED, oldPreferred, preferred));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.SUPPLIER__PURCHASE_ORDERS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getPurchaseOrders()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case Model1Package.SUPPLIER__PURCHASE_ORDERS:
      return ((InternalEList<?>)getPurchaseOrders()).basicRemove(otherEnd, msgs);
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
    case Model1Package.SUPPLIER__PURCHASE_ORDERS:
      return getPurchaseOrders();
    case Model1Package.SUPPLIER__PREFERRED:
      return isPreferred();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model1Package.SUPPLIER__PURCHASE_ORDERS:
      getPurchaseOrders().clear();
      getPurchaseOrders().addAll((Collection<? extends PurchaseOrder>)newValue);
      return;
    case Model1Package.SUPPLIER__PREFERRED:
      setPreferred((Boolean)newValue);
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
    case Model1Package.SUPPLIER__PURCHASE_ORDERS:
      getPurchaseOrders().clear();
      return;
    case Model1Package.SUPPLIER__PREFERRED:
      setPreferred(PREFERRED_EDEFAULT);
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
    case Model1Package.SUPPLIER__PURCHASE_ORDERS:
      return purchaseOrders != null && !purchaseOrders.isEmpty();
    case Model1Package.SUPPLIER__PREFERRED:
      return preferred != PREFERRED_EDEFAULT;
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
      return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (preferred: ");
    result.append(preferred);
    result.append(')');
    return result.toString();
  }

} // SupplierImpl
