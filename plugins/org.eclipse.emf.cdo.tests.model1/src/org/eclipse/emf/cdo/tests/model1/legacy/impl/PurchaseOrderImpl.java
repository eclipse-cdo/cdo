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
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
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
import java.util.Date;

/**
 * @author Eike Stepper
 */
public class PurchaseOrderImpl extends OrderImpl implements PurchaseOrder
{
  /**
   * The default value of the '{@link #getDate() <em>Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getDate()
   * @generated
   * @ordered
   */
  protected static final Date DATE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDate() <em>Date</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getDate()
   * @generated
   * @ordered
   */
  protected Date date = DATE_EDEFAULT;

  /**
   * The cached value of the '{@link #getSupplier() <em>Supplier</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getSupplier()
   * @generated
   * @ordered
   */
  protected Supplier supplier;

  /**
   * The cached value of the '{@link #getSalesOrders() <em>Sales Orders</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSalesOrders()
   * @generated
   * @ordered
   */
  protected EList<SalesOrder> salesOrders;

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
    return date;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDate(Date newDate)
  {
    Date oldDate = date;
    date = newDate;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PURCHASE_ORDER__DATE, oldDate, date));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Supplier getSupplier()
  {
    if (supplier != null && supplier.eIsProxy())
    {
      InternalEObject oldSupplier = (InternalEObject)supplier;
      supplier = (Supplier)eResolveProxy(oldSupplier);
      if (supplier != oldSupplier)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.PURCHASE_ORDER__SUPPLIER, oldSupplier, supplier));
      }
    }
    return supplier;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Supplier basicGetSupplier()
  {
    return supplier;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSupplier(Supplier newSupplier, NotificationChain msgs)
  {
    Supplier oldSupplier = supplier;
    supplier = newSupplier;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model1Package.PURCHASE_ORDER__SUPPLIER, oldSupplier, newSupplier);
      if (msgs == null)
        msgs = notification;
      else
        msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSupplier(Supplier newSupplier)
  {
    if (newSupplier != supplier)
    {
      NotificationChain msgs = null;
      if (supplier != null)
        msgs = ((InternalEObject)supplier).eInverseRemove(this, Model1Package.SUPPLIER__PURCHASE_ORDERS, Supplier.class, msgs);
      if (newSupplier != null)
        msgs = ((InternalEObject)newSupplier).eInverseAdd(this, Model1Package.SUPPLIER__PURCHASE_ORDERS, Supplier.class, msgs);
      msgs = basicSetSupplier(newSupplier, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PURCHASE_ORDER__SUPPLIER, newSupplier, newSupplier));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<SalesOrder> getSalesOrders()
  {
    if (salesOrders == null)
    {
      salesOrders = new EObjectWithInverseResolvingEList.ManyInverse<SalesOrder>(SalesOrder.class, this, Model1Package.PURCHASE_ORDER__SALES_ORDERS,
          Model1Package.SALES_ORDER__PURCHASE_ORDERS);
    }
    return salesOrders;
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
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      if (supplier != null)
        msgs = ((InternalEObject)supplier).eInverseRemove(this, Model1Package.SUPPLIER__PURCHASE_ORDERS, Supplier.class, msgs);
      return basicSetSupplier((Supplier)otherEnd, msgs);
    case Model1Package.PURCHASE_ORDER__SALES_ORDERS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getSalesOrders()).basicAdd(otherEnd, msgs);
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
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      return basicSetSupplier(null, msgs);
    case Model1Package.PURCHASE_ORDER__SALES_ORDERS:
      return ((InternalEList<?>)getSalesOrders()).basicRemove(otherEnd, msgs);
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
    case Model1Package.PURCHASE_ORDER__DATE:
      return getDate();
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      if (resolve)
        return getSupplier();
      return basicGetSupplier();
    case Model1Package.PURCHASE_ORDER__SALES_ORDERS:
      return getSalesOrders();
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
    case Model1Package.PURCHASE_ORDER__DATE:
      setDate((Date)newValue);
      return;
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      setSupplier((Supplier)newValue);
      return;
    case Model1Package.PURCHASE_ORDER__SALES_ORDERS:
      getSalesOrders().clear();
      getSalesOrders().addAll((Collection<? extends SalesOrder>)newValue);
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
    case Model1Package.PURCHASE_ORDER__DATE:
      setDate(DATE_EDEFAULT);
      return;
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      setSupplier((Supplier)null);
      return;
    case Model1Package.PURCHASE_ORDER__SALES_ORDERS:
      getSalesOrders().clear();
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
    case Model1Package.PURCHASE_ORDER__DATE:
      return DATE_EDEFAULT == null ? date != null : !DATE_EDEFAULT.equals(date);
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      return supplier != null;
    case Model1Package.PURCHASE_ORDER__SALES_ORDERS:
      return salesOrders != null && !salesOrders.isEmpty();
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
    result.append(" (date: ");
    result.append(date);
    result.append(')');
    return result.toString();
  }

} // PurchaseOrderImpl
