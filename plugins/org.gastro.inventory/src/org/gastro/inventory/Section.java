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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Section</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Section#getMenuCard <em>Menu Card</em>}</li>
 * <li>{@link org.gastro.inventory.Section#getOfferings <em>Offerings</em>}</li>
 * <li>{@link org.gastro.inventory.Section#getTitle <em>Title</em>}</li>
 * <li>{@link org.gastro.inventory.Section#getText <em>Text</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getSection()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Section extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Menu Card</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.MenuCard#getSections <em>Sections</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Menu Card</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Menu Card</em>' container reference.
   * @see #setMenuCard(MenuCard)
   * @see org.gastro.inventory.InventoryPackage#getSection_MenuCard()
   * @see org.gastro.inventory.MenuCard#getSections
   * @model opposite="sections" required="true" transient="false"
   * @generated
   */
  MenuCard getMenuCard();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Section#getMenuCard <em>Menu Card</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Menu Card</em>' container reference.
   * @see #getMenuCard()
   * @generated
   */
  void setMenuCard(MenuCard value);

  /**
   * Returns the value of the '<em><b>Offerings</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Offering}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Offering#getSection <em>Section</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Offerings</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Offerings</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getSection_Offerings()
   * @see org.gastro.inventory.Offering#getSection
   * @model opposite="section" containment="true" required="true"
   * @generated
   */
  EList<Offering> getOfferings();

  /**
   * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Title</em>' attribute.
   * @see #setTitle(String)
   * @see org.gastro.inventory.InventoryPackage#getSection_Title()
   * @model
   * @generated
   */
  String getTitle();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Section#getTitle <em>Title</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Title</em>' attribute.
   * @see #getTitle()
   * @generated
   */
  void setTitle(String value);

  /**
   * Returns the value of the '<em><b>Text</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Text</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Text</em>' attribute.
   * @see #setText(String)
   * @see org.gastro.inventory.InventoryPackage#getSection_Text()
   * @model
   * @generated
   */
  String getText();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Section#getText <em>Text</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Text</em>' attribute.
   * @see #getText()
   * @generated
   */
  void setText(String value);

} // Section
