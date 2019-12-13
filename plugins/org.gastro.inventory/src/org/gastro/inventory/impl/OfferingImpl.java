/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.Offering;
import org.gastro.inventory.Product;
import org.gastro.inventory.Section;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Offering</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.OfferingImpl#getProduct <em>Product</em>}</li>
 * <li>{@link org.gastro.inventory.impl.OfferingImpl#getName <em>Name</em>}</li>
 * <li>{@link org.gastro.inventory.impl.OfferingImpl#getDescription <em>Description</em>}</li>
 * <li>{@link org.gastro.inventory.impl.OfferingImpl#getPrice <em>Price</em>}</li>
 * <li>{@link org.gastro.inventory.impl.OfferingImpl#getSection <em>Section</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OfferingImpl extends CDOObjectImpl implements Offering
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected OfferingImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return InventoryPackage.Literals.OFFERING;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Product getProduct()
  {
    return (Product)eGet(InventoryPackage.Literals.OFFERING__PRODUCT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setProduct(Product newProduct)
  {
    eSet(InventoryPackage.Literals.OFFERING__PRODUCT, newProduct);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(InventoryPackage.Literals.OFFERING__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(InventoryPackage.Literals.OFFERING__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getDescription()
  {
    return (String)eGet(InventoryPackage.Literals.OFFERING__DESCRIPTION, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setDescription(String newDescription)
  {
    eSet(InventoryPackage.Literals.OFFERING__DESCRIPTION, newDescription);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public float getPrice()
  {
    return (Float)eGet(InventoryPackage.Literals.OFFERING__PRICE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setPrice(float newPrice)
  {
    eSet(InventoryPackage.Literals.OFFERING__PRICE, newPrice);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Section getSection()
  {
    return (Section)eGet(InventoryPackage.Literals.OFFERING__SECTION, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setSection(Section newSection)
  {
    eSet(InventoryPackage.Literals.OFFERING__SECTION, newSection);
  }

} // OfferingImpl
