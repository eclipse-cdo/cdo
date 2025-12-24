/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Category</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CategoryImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CategoryImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CategoryImpl#getProducts <em>Products</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CategoryImpl#getMainProduct <em>Main Product</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.legacy.impl.CategoryImpl#getTopProducts <em>Top Products</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CategoryImpl extends EObjectImpl implements Category
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
   * The cached value of the '{@link #getCategories() <em>Categories</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getCategories()
   * @generated
   * @ordered
   */
  protected EList<Category> categories;

  /**
   * The cached value of the '{@link #getProducts() <em>Products</em>}' containment reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getProducts()
   * @generated
   * @ordered
   */
  protected EList<Product1> products;

  /**
   * The cached value of the '{@link #getMainProduct() <em>Main Product</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMainProduct()
   * @generated
   * @ordered
   */
  protected Product1 mainProduct;

  /**
  	 * The cached value of the '{@link #getTopProducts() <em>Top Products</em>}' reference list.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @see #getTopProducts()
  	 * @generated
  	 * @ordered
  	 */
  protected EList<Product1> topProducts;

  /**
  	 * <!-- begin-user-doc --> <!-- end-user-doc -->
  	 * @generated
  	 */
  protected CategoryImpl()
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
    return Model1Package.eINSTANCE.getCategory();
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
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.CATEGORY__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Category> getCategories()
  {
    if (categories == null)
    {
      categories = new EObjectContainmentEList.Resolving<>(Category.class, this, Model1Package.CATEGORY__CATEGORIES);
    }
    return categories;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Product1> getProducts()
  {
    if (products == null)
    {
      products = new EObjectContainmentEList.Resolving<>(Product1.class, this, Model1Package.CATEGORY__PRODUCTS);
    }
    return products;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Product1 getMainProduct()
  {
    if (mainProduct != null && mainProduct.eIsProxy())
    {
      InternalEObject oldMainProduct = (InternalEObject)mainProduct;
      mainProduct = (Product1)eResolveProxy(oldMainProduct);
      if (mainProduct != oldMainProduct)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.CATEGORY__MAIN_PRODUCT, oldMainProduct, mainProduct));
        }
      }
    }
    return mainProduct;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public Product1 basicGetMainProduct()
  {
    return mainProduct;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public void setMainProduct(Product1 newMainProduct)
  {
    Product1 oldMainProduct = mainProduct;
    mainProduct = newMainProduct;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.CATEGORY__MAIN_PRODUCT, oldMainProduct, mainProduct));
    }
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public EList<Product1> getTopProducts()
  {
    if (topProducts == null)
    {
      topProducts = new EObjectResolvingEList<>(Product1.class, this, Model1Package.CATEGORY__TOP_PRODUCTS);
    }
    return topProducts;
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
    case Model1Package.CATEGORY__CATEGORIES:
      return ((InternalEList<?>)getCategories()).basicRemove(otherEnd, msgs);
    case Model1Package.CATEGORY__PRODUCTS:
      return ((InternalEList<?>)getProducts()).basicRemove(otherEnd, msgs);
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
    case Model1Package.CATEGORY__NAME:
      return getName();
    case Model1Package.CATEGORY__CATEGORIES:
      return getCategories();
    case Model1Package.CATEGORY__PRODUCTS:
      return getProducts();
    case Model1Package.CATEGORY__MAIN_PRODUCT:
      if (resolve)
      {
        return getMainProduct();
      }
      return basicGetMainProduct();
    case Model1Package.CATEGORY__TOP_PRODUCTS:
      return getTopProducts();
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
    case Model1Package.CATEGORY__NAME:
      setName((String)newValue);
      return;
    case Model1Package.CATEGORY__CATEGORIES:
      getCategories().clear();
      getCategories().addAll((Collection<? extends Category>)newValue);
      return;
    case Model1Package.CATEGORY__PRODUCTS:
      getProducts().clear();
      getProducts().addAll((Collection<? extends Product1>)newValue);
      return;
    case Model1Package.CATEGORY__MAIN_PRODUCT:
      setMainProduct((Product1)newValue);
      return;
    case Model1Package.CATEGORY__TOP_PRODUCTS:
      getTopProducts().clear();
      getTopProducts().addAll((Collection<? extends Product1>)newValue);
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
    case Model1Package.CATEGORY__NAME:
      setName(NAME_EDEFAULT);
      return;
    case Model1Package.CATEGORY__CATEGORIES:
      getCategories().clear();
      return;
    case Model1Package.CATEGORY__PRODUCTS:
      getProducts().clear();
      return;
    case Model1Package.CATEGORY__MAIN_PRODUCT:
      setMainProduct((Product1)null);
      return;
    case Model1Package.CATEGORY__TOP_PRODUCTS:
      getTopProducts().clear();
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
    case Model1Package.CATEGORY__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case Model1Package.CATEGORY__CATEGORIES:
      return categories != null && !categories.isEmpty();
    case Model1Package.CATEGORY__PRODUCTS:
      return products != null && !products.isEmpty();
    case Model1Package.CATEGORY__MAIN_PRODUCT:
      return mainProduct != null;
    case Model1Package.CATEGORY__TOP_PRODUCTS:
      return topProducts != null && !topProducts.isEmpty();
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
    result.append(')');
    return result.toString();
  }

} // CategoryImpl
