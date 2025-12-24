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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Restaurant</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Restaurant#getName <em>Name</em>}</li>
 * <li>{@link org.gastro.inventory.Restaurant#getDepartments <em>Departments</em>}</li>
 * <li>{@link org.gastro.inventory.Restaurant#getMenuCards <em>Menu Cards</em>}</li>
 * <li>{@link org.gastro.inventory.Restaurant#getTables <em>Tables</em>}</li>
 * <li>{@link org.gastro.inventory.Restaurant#getStations <em>Stations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getRestaurant()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Restaurant extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.gastro.inventory.InventoryPackage#getRestaurant_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Restaurant#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Departments</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Department}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Department#getRestaurant <em>Restaurant</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Departments</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Departments</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getRestaurant_Departments()
   * @see org.gastro.inventory.Department#getRestaurant
   * @model opposite="restaurant" containment="true"
   * @generated
   */
  EList<Department> getDepartments();

  /**
   * Returns the value of the '<em><b>Menu Cards</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.MenuCard}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.MenuCard#getRestaurant <em>Restaurant</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Menu Cards</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Menu Cards</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getRestaurant_MenuCards()
   * @see org.gastro.inventory.MenuCard#getRestaurant
   * @model opposite="restaurant" containment="true"
   * @generated
   */
  EList<MenuCard> getMenuCards();

  /**
   * Returns the value of the '<em><b>Tables</b></em>' containment reference list. The list contents are of type
   * {@link org.gastro.inventory.Table}. It is bidirectional and its opposite is '
   * {@link org.gastro.inventory.Table#getRestaurant <em>Restaurant</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tables</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Tables</em>' containment reference list.
   * @see org.gastro.inventory.InventoryPackage#getRestaurant_Tables()
   * @see org.gastro.inventory.Table#getRestaurant
   * @model opposite="restaurant" containment="true"
   * @generated
   */
  EList<Table> getTables();

  /**
   * Returns the value of the '<em><b>Stations</b></em>' reference list. The list contents are of type
   * {@link org.gastro.inventory.Station}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Stations</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Stations</em>' reference list.
   * @see org.gastro.inventory.InventoryPackage#getRestaurant_Stations()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Station> getStations();

} // Restaurant
