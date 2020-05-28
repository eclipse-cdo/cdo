/*
 * Copyright (c) 2013, 2015, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Detail</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderDetailImpl#getOrder <em>Order</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderDetailImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderDetailImpl#getPrice <em>Price</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OrderDetailImpl extends EObjectImpl implements OrderDetail
{
  /**
   * The cached value of the '{@link #getProduct() <em>Product</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getProduct()
   * @generated
   * @ordered
   */
  protected Product1 product;

  /**
   * The default value of the '{@link #getPrice() <em>Price</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getPrice()
   * @generated
   * @ordered
   */
  protected static final float PRICE_EDEFAULT = 0.0F;

  /**
   * The cached value of the '{@link #getPrice() <em>Price</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getPrice()
   * @generated
   * @ordered
   */
  protected float price = PRICE_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected OrderDetailImpl()
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
    return Model1Package.eINSTANCE.getOrderDetail();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Order getOrder()
  {
    if (eContainerFeatureID() != Model1Package.ORDER_DETAIL__ORDER)
      return null;
    return (Order)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Order basicGetOrder()
  {
    if (eContainerFeatureID() != Model1Package.ORDER_DETAIL__ORDER)
      return null;
    return (Order)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetOrder(Order newOrder, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newOrder, Model1Package.ORDER_DETAIL__ORDER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOrder(Order newOrder)
  {
    if (newOrder != eInternalContainer() || (eContainerFeatureID() != Model1Package.ORDER_DETAIL__ORDER && newOrder != null))
    {
      if (EcoreUtil.isAncestor(this, newOrder))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newOrder != null)
        msgs = ((InternalEObject)newOrder).eInverseAdd(this, Model1Package.ORDER__ORDER_DETAILS, Order.class, msgs);
      msgs = basicSetOrder(newOrder, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__ORDER, newOrder, newOrder));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Product1 getProduct()
  {
    if (product != null && product.eIsProxy())
    {
      InternalEObject oldProduct = (InternalEObject)product;
      product = (Product1)eResolveProxy(oldProduct);
      if (product != oldProduct)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.ORDER_DETAIL__PRODUCT, oldProduct, product));
      }
    }
    return product;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Product1 basicGetProduct()
  {
    return product;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetProduct(Product1 newProduct, NotificationChain msgs)
  {
    Product1 oldProduct = product;
    product = newProduct;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__PRODUCT, oldProduct, newProduct);
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
  public void setProduct(Product1 newProduct)
  {
    if (newProduct != product)
    {
      NotificationChain msgs = null;
      if (product != null)
        msgs = ((InternalEObject)product).eInverseRemove(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class, msgs);
      if (newProduct != null)
        msgs = ((InternalEObject)newProduct).eInverseAdd(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class, msgs);
      msgs = basicSetProduct(newProduct, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__PRODUCT, newProduct, newProduct));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getPrice()
  {
    return price;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPrice(float newPrice)
  {
    float oldPrice = price;
    price = newPrice;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__PRICE, oldPrice, price));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      return basicSetOrder((Order)otherEnd, msgs);
    case Model1Package.ORDER_DETAIL__PRODUCT:
      if (product != null)
        msgs = ((InternalEObject)product).eInverseRemove(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class, msgs);
      return basicSetProduct((Product1)otherEnd, msgs);
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
    case Model1Package.ORDER_DETAIL__ORDER:
      return basicSetOrder(null, msgs);
    case Model1Package.ORDER_DETAIL__PRODUCT:
      return basicSetProduct(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      return eInternalContainer().eInverseRemove(this, Model1Package.ORDER__ORDER_DETAILS, Order.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case Model1Package.ORDER_DETAIL__ORDER:
      if (resolve)
        return getOrder();
      return basicGetOrder();
    case Model1Package.ORDER_DETAIL__PRODUCT:
      if (resolve)
        return getProduct();
      return basicGetProduct();
    case Model1Package.ORDER_DETAIL__PRICE:
      return getPrice();
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
    case Model1Package.ORDER_DETAIL__ORDER:
      setOrder((Order)newValue);
      return;
    case Model1Package.ORDER_DETAIL__PRODUCT:
      setProduct((Product1)newValue);
      return;
    case Model1Package.ORDER_DETAIL__PRICE:
      setPrice((Float)newValue);
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
    case Model1Package.ORDER_DETAIL__ORDER:
      setOrder((Order)null);
      return;
    case Model1Package.ORDER_DETAIL__PRODUCT:
      setProduct((Product1)null);
      return;
    case Model1Package.ORDER_DETAIL__PRICE:
      setPrice(PRICE_EDEFAULT);
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
    case Model1Package.ORDER_DETAIL__ORDER:
      return basicGetOrder() != null;
    case Model1Package.ORDER_DETAIL__PRODUCT:
      return product != null;
    case Model1Package.ORDER_DETAIL__PRICE:
      return price != PRICE_EDEFAULT;
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
    result.append(" (price: ");
    result.append(price);
    result.append(')');
    return result.toString();
  }

} // OrderDetailImpl
