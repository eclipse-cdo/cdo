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

import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Product1</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.Product1Impl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.Product1Impl#getOrderDetails <em>Order Details</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.Product1Impl#getVat <em>Vat</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.Product1Impl#getOtherVATs <em>Other VA Ts</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.Product1Impl#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Product1Impl extends EObjectImpl implements Product1
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getOrderDetails() <em>Order Details</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrderDetails()
   * @generated
   * @ordered
   */
  protected EList<OrderDetail> orderDetails;

  /**
   * The default value of the '{@link #getVat() <em>Vat</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getVat()
   * @generated
   * @ordered
   */
  protected static final VAT VAT_EDEFAULT = VAT.VAT15;

  /**
   * The cached value of the '{@link #getVat() <em>Vat</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getVat()
   * @generated
   * @ordered
   */
  protected VAT vat = VAT_EDEFAULT;

  /**
   * The cached value of the '{@link #getOtherVATs() <em>Other VA Ts</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOtherVATs()
   * @generated
   * @ordered
   */
  protected EList<VAT> otherVATs;

  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected String description = DESCRIPTION_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Product1Impl()
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
    return Model1Package.eINSTANCE.getProduct1();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PRODUCT1__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<OrderDetail> getOrderDetails()
  {
    if (orderDetails == null)
    {
      orderDetails = new EObjectWithInverseResolvingEList<>(OrderDetail.class, this, Model1Package.PRODUCT1__ORDER_DETAILS,
          Model1Package.ORDER_DETAIL__PRODUCT);
    }
    return orderDetails;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public VAT getVat()
  {
    return vat;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVat(VAT newVat)
  {
    VAT oldVat = vat;
    vat = newVat == null ? VAT_EDEFAULT : newVat;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PRODUCT1__VAT, oldVat, vat));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<VAT> getOtherVATs()
  {
    if (otherVATs == null)
    {
      otherVATs = new EDataTypeUniqueEList<>(VAT.class, this, Model1Package.PRODUCT1__OTHER_VA_TS);
    }
    return otherVATs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDescription()
  {
    return description;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDescription(String newDescription)
  {
    String oldDescription = description;
    description = newDescription;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PRODUCT1__DESCRIPTION, oldDescription, description));
    }
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
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOrderDetails()).basicAdd(otherEnd, msgs);
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
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return ((InternalEList<?>)getOrderDetails()).basicRemove(otherEnd, msgs);
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
    case Model1Package.PRODUCT1__NAME:
      return getName();
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return getOrderDetails();
    case Model1Package.PRODUCT1__VAT:
      return getVat();
    case Model1Package.PRODUCT1__OTHER_VA_TS:
      return getOtherVATs();
    case Model1Package.PRODUCT1__DESCRIPTION:
      return getDescription();
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
    case Model1Package.PRODUCT1__NAME:
      setName((String)newValue);
      return;
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      getOrderDetails().clear();
      getOrderDetails().addAll((Collection<? extends OrderDetail>)newValue);
      return;
    case Model1Package.PRODUCT1__VAT:
      setVat((VAT)newValue);
      return;
    case Model1Package.PRODUCT1__OTHER_VA_TS:
      getOtherVATs().clear();
      getOtherVATs().addAll((Collection<? extends VAT>)newValue);
      return;
    case Model1Package.PRODUCT1__DESCRIPTION:
      setDescription((String)newValue);
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
    case Model1Package.PRODUCT1__NAME:
      setName(NAME_EDEFAULT);
      return;
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      getOrderDetails().clear();
      return;
    case Model1Package.PRODUCT1__VAT:
      setVat(VAT_EDEFAULT);
      return;
    case Model1Package.PRODUCT1__OTHER_VA_TS:
      getOtherVATs().clear();
      return;
    case Model1Package.PRODUCT1__DESCRIPTION:
      setDescription(DESCRIPTION_EDEFAULT);
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
    case Model1Package.PRODUCT1__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return orderDetails != null && !orderDetails.isEmpty();
    case Model1Package.PRODUCT1__VAT:
      return vat != VAT_EDEFAULT;
    case Model1Package.PRODUCT1__OTHER_VA_TS:
      return otherVATs != null && !otherVATs.isEmpty();
    case Model1Package.PRODUCT1__DESCRIPTION:
      return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
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
    result.append(" (name: ");
    result.append(name);
    result.append(", vat: ");
    result.append(vat);
    result.append(", otherVATs: ");
    result.append(otherVATs);
    result.append(", description: ");
    result.append(description);
    result.append(')');
    return result.toString();
  }

} // Product1Impl
