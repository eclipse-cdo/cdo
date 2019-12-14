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

import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderAddressImpl#getOrderDetails <em>Order Details</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderAddressImpl#getOrder <em>Order</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderAddressImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderAddressImpl#getPrice <em>Price</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.OrderAddressImpl#isTestAttribute <em>Test Attribute</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OrderAddressImpl extends AddressImpl implements OrderAddress
{
  /**
   * The cached value of the '{@link #getOrderDetails() <em>Order Details</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getOrderDetails()
   * @generated
   * @ordered
   */
  protected EList<OrderDetail> orderDetails;

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
   * The default value of the '{@link #isTestAttribute() <em>Test Attribute</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isTestAttribute()
   * @generated
   * @ordered
   */
  protected static final boolean TEST_ATTRIBUTE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isTestAttribute() <em>Test Attribute</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isTestAttribute()
   * @generated
   * @ordered
   */
  protected boolean testAttribute = TEST_ATTRIBUTE_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected OrderAddressImpl()
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
    return Model1Package.eINSTANCE.getOrderAddress();
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
      orderDetails = new EObjectContainmentWithInverseEList.Resolving<>(OrderDetail.class, this, Model1Package.ORDER_ADDRESS__ORDER_DETAILS,
          Model1Package.ORDER_DETAIL__ORDER);
    }
    return orderDetails;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Order getOrder()
  {
    if (eContainerFeatureID() != Model1Package.ORDER_ADDRESS__ORDER)
    {
      return null;
    }
    return (Order)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Order basicGetOrder()
  {
    if (eContainerFeatureID() != Model1Package.ORDER_ADDRESS__ORDER)
    {
      return null;
    }
    return (Order)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetOrder(Order newOrder, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newOrder, Model1Package.ORDER_ADDRESS__ORDER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOrder(Order newOrder)
  {
    if (newOrder != eInternalContainer() || eContainerFeatureID() != Model1Package.ORDER_ADDRESS__ORDER && newOrder != null)
    {
      if (EcoreUtil.isAncestor(this, newOrder))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newOrder != null)
      {
        msgs = ((InternalEObject)newOrder).eInverseAdd(this, Model1Package.ORDER__ORDER_DETAILS, Order.class, msgs);
      }
      msgs = basicSetOrder(newOrder, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_ADDRESS__ORDER, newOrder, newOrder));
    }
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
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.ORDER_ADDRESS__PRODUCT, oldProduct, product));
        }
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_ADDRESS__PRODUCT, oldProduct, newProduct);
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
  public void setProduct(Product1 newProduct)
  {
    if (newProduct != product)
    {
      NotificationChain msgs = null;
      if (product != null)
      {
        msgs = ((InternalEObject)product).eInverseRemove(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class, msgs);
      }
      if (newProduct != null)
      {
        msgs = ((InternalEObject)newProduct).eInverseAdd(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class, msgs);
      }
      msgs = basicSetProduct(newProduct, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_ADDRESS__PRODUCT, newProduct, newProduct));
    }
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
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_ADDRESS__PRICE, oldPrice, price));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isTestAttribute()
  {
    return testAttribute;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTestAttribute(boolean newTestAttribute)
  {
    boolean oldTestAttribute = testAttribute;
    testAttribute = newTestAttribute;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_ADDRESS__TEST_ATTRIBUTE, oldTestAttribute, testAttribute));
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
    case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOrderDetails()).basicAdd(otherEnd, msgs);
    case Model1Package.ORDER_ADDRESS__ORDER:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetOrder((Order)otherEnd, msgs);
    case Model1Package.ORDER_ADDRESS__PRODUCT:
      if (product != null)
      {
        msgs = ((InternalEObject)product).eInverseRemove(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class, msgs);
      }
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
    case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
      return ((InternalEList<?>)getOrderDetails()).basicRemove(otherEnd, msgs);
    case Model1Package.ORDER_ADDRESS__ORDER:
      return basicSetOrder(null, msgs);
    case Model1Package.ORDER_ADDRESS__PRODUCT:
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
    case Model1Package.ORDER_ADDRESS__ORDER:
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
    case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
      return getOrderDetails();
    case Model1Package.ORDER_ADDRESS__ORDER:
      if (resolve)
      {
        return getOrder();
      }
      return basicGetOrder();
    case Model1Package.ORDER_ADDRESS__PRODUCT:
      if (resolve)
      {
        return getProduct();
      }
      return basicGetProduct();
    case Model1Package.ORDER_ADDRESS__PRICE:
      return getPrice();
    case Model1Package.ORDER_ADDRESS__TEST_ATTRIBUTE:
      return isTestAttribute();
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
    case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
      getOrderDetails().clear();
      getOrderDetails().addAll((Collection<? extends OrderDetail>)newValue);
      return;
    case Model1Package.ORDER_ADDRESS__ORDER:
      setOrder((Order)newValue);
      return;
    case Model1Package.ORDER_ADDRESS__PRODUCT:
      setProduct((Product1)newValue);
      return;
    case Model1Package.ORDER_ADDRESS__PRICE:
      setPrice((Float)newValue);
      return;
    case Model1Package.ORDER_ADDRESS__TEST_ATTRIBUTE:
      setTestAttribute((Boolean)newValue);
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
    case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
      getOrderDetails().clear();
      return;
    case Model1Package.ORDER_ADDRESS__ORDER:
      setOrder((Order)null);
      return;
    case Model1Package.ORDER_ADDRESS__PRODUCT:
      setProduct((Product1)null);
      return;
    case Model1Package.ORDER_ADDRESS__PRICE:
      setPrice(PRICE_EDEFAULT);
      return;
    case Model1Package.ORDER_ADDRESS__TEST_ATTRIBUTE:
      setTestAttribute(TEST_ATTRIBUTE_EDEFAULT);
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
    case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
      return orderDetails != null && !orderDetails.isEmpty();
    case Model1Package.ORDER_ADDRESS__ORDER:
      return basicGetOrder() != null;
    case Model1Package.ORDER_ADDRESS__PRODUCT:
      return product != null;
    case Model1Package.ORDER_ADDRESS__PRICE:
      return price != PRICE_EDEFAULT;
    case Model1Package.ORDER_ADDRESS__TEST_ATTRIBUTE:
      return testAttribute != TEST_ATTRIBUTE_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == Order.class)
    {
      switch (derivedFeatureID)
      {
      case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
        return Model1Package.ORDER__ORDER_DETAILS;
      default:
        return -1;
      }
    }
    if (baseClass == OrderDetail.class)
    {
      switch (derivedFeatureID)
      {
      case Model1Package.ORDER_ADDRESS__ORDER:
        return Model1Package.ORDER_DETAIL__ORDER;
      case Model1Package.ORDER_ADDRESS__PRODUCT:
        return Model1Package.ORDER_DETAIL__PRODUCT;
      case Model1Package.ORDER_ADDRESS__PRICE:
        return Model1Package.ORDER_DETAIL__PRICE;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == Order.class)
    {
      switch (baseFeatureID)
      {
      case Model1Package.ORDER__ORDER_DETAILS:
        return Model1Package.ORDER_ADDRESS__ORDER_DETAILS;
      default:
        return -1;
      }
    }
    if (baseClass == OrderDetail.class)
    {
      switch (baseFeatureID)
      {
      case Model1Package.ORDER_DETAIL__ORDER:
        return Model1Package.ORDER_ADDRESS__ORDER;
      case Model1Package.ORDER_DETAIL__PRODUCT:
        return Model1Package.ORDER_ADDRESS__PRODUCT;
      case Model1Package.ORDER_DETAIL__PRICE:
        return Model1Package.ORDER_ADDRESS__PRICE;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
    result.append(" (price: ");
    result.append(price);
    result.append(", testAttribute: ");
    result.append(testAttribute);
    result.append(')');
    return result.toString();
  }

} // OrderAddressImpl
