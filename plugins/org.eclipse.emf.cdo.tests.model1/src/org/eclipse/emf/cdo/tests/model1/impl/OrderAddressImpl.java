/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getOrderDetails <em>Order Details</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getOrder <em>Order</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getPrice <em>Price</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#isTestAttribute <em>Test Attribute</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OrderAddressImpl extends AddressImpl implements OrderAddress
{
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
  @SuppressWarnings("unchecked")
  public EList<OrderDetail> getOrderDetails()
  {
    return (EList<OrderDetail>)eGet(Model1Package.eINSTANCE.getOrder_OrderDetails(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Order getOrder()
  {
    return (Order)eGet(Model1Package.eINSTANCE.getOrderDetail_Order(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOrder(Order newOrder)
  {
    eSet(Model1Package.eINSTANCE.getOrderDetail_Order(), newOrder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Product1 getProduct()
  {
    return (Product1)eGet(Model1Package.eINSTANCE.getOrderDetail_Product(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setProduct(Product1 newProduct)
  {
    eSet(Model1Package.eINSTANCE.getOrderDetail_Product(), newProduct);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getPrice()
  {
    return (Float)eGet(Model1Package.eINSTANCE.getOrderDetail_Price(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPrice(float newPrice)
  {
    eSet(Model1Package.eINSTANCE.getOrderDetail_Price(), newPrice);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isTestAttribute()
  {
    return (Boolean)eGet(Model1Package.eINSTANCE.getOrderAddress_TestAttribute(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTestAttribute(boolean newTestAttribute)
  {
    eSet(Model1Package.eINSTANCE.getOrderAddress_TestAttribute(), newTestAttribute);
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

} // OrderAddressImpl
