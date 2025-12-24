/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Menu Card</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.MenuCard#getTitle <em>Title</em>}</li>
 * <li>{@link org.gastro.inventory.MenuCard#getRestaurant <em>Restaurant</em>}</li>
 * <li>{@link org.gastro.inventory.MenuCard#getSections <em>Sections</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getMenuCard()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface MenuCard extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Title</em>' attribute.
   * @see #setTitle(String)
   * @see org.gastro.inventory.InventoryPackage#getMenuCard_Title()
   * @model
   * @generated
   */
  String getTitle();

  /**
   * Sets the value of the '{@link org.gastro.inventory.MenuCard#getTitle <em>Title</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Title</em>' attribute.
   * @see #getTitle()
   * @generated
   */
  void setTitle(String value);

  /**
   * Returns the value of the '<em><b>Restaurant</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.gastro.inventory.Restaurant#getMenuCards <em>Menu Cards</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restaurant</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Restaurant</em>' container reference.
   * @see #setRestaurant(Restaurant)
   * @see org.gastro.inventory.InventoryPackage#getMenuCard_Restaurant()
   * @see org.gastro.inventory.Restaurant#getMenuCards
   * @model opposite="menuCards" required="true" transient="false"
   * @generated
   */
  Restaurant getRestaurant();

  /**
   * Sets the value of the '{@link org.gastro.inventory.MenuCard#getRestaurant <em>Restaurant</em>}' container
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Restaurant</em>' container reference.
   * @see #getRestaurant()
   * @generated
   */
  void setRestaurant(Restaurant value);

  /**
   * Returns the value of the '<em><b>Sections</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Section}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Section#getMenuCard <em>Menu Card</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sections</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Sections</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getMenuCard_Sections()
   * @see org.gastro.inventory.Section#getMenuCard
   * @model opposite="menuCard" containment="true" required="true"
   * @generated
   */
  EList<Section> getSections();

} // MenuCard
