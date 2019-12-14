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

import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
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
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Sales Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.SalesOrderImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.SalesOrderImpl#getCustomer <em>Customer</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.SalesOrderImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SalesOrderImpl extends OrderImpl implements SalesOrder
{
  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final int ID_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected int id = ID_EDEFAULT;

  /**
   * The cached value of the '{@link #getCustomer() <em>Customer</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getCustomer()
   * @generated
   * @ordered
   */
  protected Customer customer;

  /**
   * The cached value of the '{@link #getPurchaseOrders() <em>Purchase Orders</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPurchaseOrders()
   * @generated
   * @ordered
   */
  protected EList<PurchaseOrder> purchaseOrders;

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
    return id;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(int newId)
  {
    int oldId = id;
    id = newId;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.SALES_ORDER__ID, oldId, id));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Customer getCustomer()
  {
    if (customer != null && customer.eIsProxy())
    {
      InternalEObject oldCustomer = (InternalEObject)customer;
      customer = (Customer)eResolveProxy(oldCustomer);
      if (customer != oldCustomer)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.SALES_ORDER__CUSTOMER, oldCustomer, customer));
        }
      }
    }
    return customer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Customer basicGetCustomer()
  {
    return customer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCustomer(Customer newCustomer, NotificationChain msgs)
  {
    Customer oldCustomer = customer;
    customer = newCustomer;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model1Package.SALES_ORDER__CUSTOMER, oldCustomer, newCustomer);
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
  public void setCustomer(Customer newCustomer)
  {
    if (newCustomer != customer)
    {
      NotificationChain msgs = null;
      if (customer != null)
      {
        msgs = ((InternalEObject)customer).eInverseRemove(this, Model1Package.CUSTOMER__SALES_ORDERS, Customer.class, msgs);
      }
      if (newCustomer != null)
      {
        msgs = ((InternalEObject)newCustomer).eInverseAdd(this, Model1Package.CUSTOMER__SALES_ORDERS, Customer.class, msgs);
      }
      msgs = basicSetCustomer(newCustomer, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.SALES_ORDER__CUSTOMER, newCustomer, newCustomer));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<PurchaseOrder> getPurchaseOrders()
  {
    if (purchaseOrders == null)
    {
      purchaseOrders = new EObjectWithInverseResolvingEList.ManyInverse<>(PurchaseOrder.class, this, Model1Package.SALES_ORDER__PURCHASE_ORDERS,
          Model1Package.PURCHASE_ORDER__SALES_ORDERS);
    }
    return purchaseOrders;
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
    case Model1Package.SALES_ORDER__CUSTOMER:
      if (customer != null)
      {
        msgs = ((InternalEObject)customer).eInverseRemove(this, Model1Package.CUSTOMER__SALES_ORDERS, Customer.class, msgs);
      }
      return basicSetCustomer((Customer)otherEnd, msgs);
    case Model1Package.SALES_ORDER__PURCHASE_ORDERS:
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
    case Model1Package.SALES_ORDER__CUSTOMER:
      return basicSetCustomer(null, msgs);
    case Model1Package.SALES_ORDER__PURCHASE_ORDERS:
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
    case Model1Package.SALES_ORDER__ID:
      return getId();
    case Model1Package.SALES_ORDER__CUSTOMER:
      if (resolve)
      {
        return getCustomer();
      }
      return basicGetCustomer();
    case Model1Package.SALES_ORDER__PURCHASE_ORDERS:
      return getPurchaseOrders();
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
    case Model1Package.SALES_ORDER__ID:
      setId((Integer)newValue);
      return;
    case Model1Package.SALES_ORDER__CUSTOMER:
      setCustomer((Customer)newValue);
      return;
    case Model1Package.SALES_ORDER__PURCHASE_ORDERS:
      getPurchaseOrders().clear();
      getPurchaseOrders().addAll((Collection<? extends PurchaseOrder>)newValue);
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
    case Model1Package.SALES_ORDER__ID:
      setId(ID_EDEFAULT);
      return;
    case Model1Package.SALES_ORDER__CUSTOMER:
      setCustomer((Customer)null);
      return;
    case Model1Package.SALES_ORDER__PURCHASE_ORDERS:
      getPurchaseOrders().clear();
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
    case Model1Package.SALES_ORDER__ID:
      return id != ID_EDEFAULT;
    case Model1Package.SALES_ORDER__CUSTOMER:
      return customer != null;
    case Model1Package.SALES_ORDER__PURCHASE_ORDERS:
      return purchaseOrders != null && !purchaseOrders.isEmpty();
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
    result.append(" (id: ");
    result.append(id);
    result.append(')');
    return result.toString();
  }

} // SalesOrderImpl
