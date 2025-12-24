/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Product;
import org.eclipse.emf.cdo.examples.company.VAT;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Product</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.ProductImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.ProductImpl#getVat <em>Vat</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.ProductImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.ProductImpl#getPrice <em>Price</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProductImpl extends CDOObjectImpl implements Product
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ProductImpl()
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
    return CompanyPackage.Literals.PRODUCT;
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
  public String getName()
  {
    return (String)eGet(CompanyPackage.Literals.PRODUCT__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(CompanyPackage.Literals.PRODUCT__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public VAT getVat()
  {
    return (VAT)eGet(CompanyPackage.Literals.PRODUCT__VAT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVat(VAT newVat)
  {
    eSet(CompanyPackage.Literals.PRODUCT__VAT, newVat);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDescription()
  {
    return (String)eGet(CompanyPackage.Literals.PRODUCT__DESCRIPTION, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDescription(String newDescription)
  {
    eSet(CompanyPackage.Literals.PRODUCT__DESCRIPTION, newDescription);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getPrice()
  {
    return (Float)eGet(CompanyPackage.Literals.PRODUCT__PRICE, true);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPrice(float newPrice)
  {
    eSet(CompanyPackage.Literals.PRODUCT__PRICE, newPrice);
  }

} // ProductImpl
