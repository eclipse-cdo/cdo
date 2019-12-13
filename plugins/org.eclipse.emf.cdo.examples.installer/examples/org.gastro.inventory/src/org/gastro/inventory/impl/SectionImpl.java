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
import org.gastro.inventory.Offering;
import org.gastro.inventory.Section;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Section</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.SectionImpl#getMenuCard <em>Menu Card</em>}</li>
 * <li>{@link org.gastro.inventory.impl.SectionImpl#getOfferings <em>Offerings</em>}</li>
 * <li>{@link org.gastro.inventory.impl.SectionImpl#getTitle <em>Title</em>}</li>
 * <li>{@link org.gastro.inventory.impl.SectionImpl#getText <em>Text</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SectionImpl extends CDOObjectImpl implements Section
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected SectionImpl()
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
    return InventoryPackage.Literals.SECTION;
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
  public MenuCard getMenuCard()
  {
    return (MenuCard)eGet(InventoryPackage.Literals.SECTION__MENU_CARD, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setMenuCard(MenuCard newMenuCard)
  {
    eSet(InventoryPackage.Literals.SECTION__MENU_CARD, newMenuCard);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Offering> getOfferings()
  {
    return (EList<Offering>)eGet(InventoryPackage.Literals.SECTION__OFFERINGS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getTitle()
  {
    return (String)eGet(InventoryPackage.Literals.SECTION__TITLE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setTitle(String newTitle)
  {
    eSet(InventoryPackage.Literals.SECTION__TITLE, newTitle);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getText()
  {
    return (String)eGet(InventoryPackage.Literals.SECTION__TEXT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setText(String newText)
  {
    eSet(InventoryPackage.Literals.SECTION__TEXT, newText);
  }

} // SectionImpl
