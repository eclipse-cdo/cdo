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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.MenuCard;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Section;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Menu Card</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.MenuCardImpl#getTitle <em>Title</em>}</li>
 * <li>{@link org.gastro.inventory.impl.MenuCardImpl#getRestaurant <em>Restaurant</em>}</li>
 * <li>{@link org.gastro.inventory.impl.MenuCardImpl#getSections <em>Sections</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MenuCardImpl extends CDOObjectImpl implements MenuCard
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected MenuCardImpl()
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
    return InventoryPackage.Literals.MENU_CARD;
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
  public String getTitle()
  {
    return (String)eGet(InventoryPackage.Literals.MENU_CARD__TITLE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setTitle(String newTitle)
  {
    eSet(InventoryPackage.Literals.MENU_CARD__TITLE, newTitle);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Restaurant getRestaurant()
  {
    return (Restaurant)eGet(InventoryPackage.Literals.MENU_CARD__RESTAURANT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setRestaurant(Restaurant newRestaurant)
  {
    eSet(InventoryPackage.Literals.MENU_CARD__RESTAURANT, newRestaurant);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Section> getSections()
  {
    return (EList<Section>)eGet(InventoryPackage.Literals.MENU_CARD__SECTIONS, true);
  }

} // MenuCardImpl
