/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Order;
import org.eclipse.emf.cdo.examples.company.OrderDetail;
import org.eclipse.emf.cdo.examples.company.Product;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Detail</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl#getOrder <em>Order</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl#getPrice <em>Price</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OrderDetailImpl extends CDOObjectImpl implements OrderDetail
{
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
    return CompanyPackage.Literals.ORDER_DETAIL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Order getOrder()
  {
    return (Order)eGet(CompanyPackage.Literals.ORDER_DETAIL__ORDER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setOrder(Order newOrder)
  {
    eSet(CompanyPackage.Literals.ORDER_DETAIL__ORDER, newOrder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Product getProduct()
  {
    return (Product)eGet(CompanyPackage.Literals.ORDER_DETAIL__PRODUCT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setProduct(Product newProduct)
  {
    eSet(CompanyPackage.Literals.ORDER_DETAIL__PRODUCT, newProduct);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getPrice()
  {
    return (Float)eGet(CompanyPackage.Literals.ORDER_DETAIL__PRICE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPrice(float newPrice)
  {
    eSet(CompanyPackage.Literals.ORDER_DETAIL__PRICE, newPrice);
  }

} // OrderDetailImpl
