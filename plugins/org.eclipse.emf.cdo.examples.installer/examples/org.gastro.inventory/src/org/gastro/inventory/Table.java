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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Table</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Table#getSeats <em>Seats</em>}</li>
 * <li>{@link org.gastro.inventory.Table#getRestaurant <em>Restaurant</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getTable()
 * @model
 * @generated
 */
public interface Table extends Station
{
  /**
   * Returns the value of the '<em><b>Seats</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Seats</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Seats</em>' attribute.
   * @see #setSeats(int)
   * @see org.gastro.inventory.InventoryPackage#getTable_Seats()
   * @model
   * @generated
   */
  int getSeats();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Table#getSeats <em>Seats</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Seats</em>' attribute.
   * @see #getSeats()
   * @generated
   */
  void setSeats(int value);

  /**
   * Returns the value of the '<em><b>Restaurant</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.gastro.inventory.Restaurant#getTables <em>Tables</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restaurant</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Restaurant</em>' container reference.
   * @see #setRestaurant(Restaurant)
   * @see org.gastro.inventory.InventoryPackage#getTable_Restaurant()
   * @see org.gastro.inventory.Restaurant#getTables
   * @model opposite="tables" required="true" transient="false"
   * @generated
   */
  Restaurant getRestaurant();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Table#getRestaurant <em>Restaurant</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Restaurant</em>' container reference.
   * @see #getRestaurant()
   * @generated
   */
  void setRestaurant(Restaurant value);

} // Table
