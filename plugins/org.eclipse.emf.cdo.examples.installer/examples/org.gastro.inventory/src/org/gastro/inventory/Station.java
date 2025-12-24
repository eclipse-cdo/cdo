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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Station</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Station#getStationID <em>Station ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getStation()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Station extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Station ID</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Station ID</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Station ID</em>' attribute.
   * @see #setStationID(String)
   * @see org.gastro.inventory.InventoryPackage#getStation_StationID()
   * @model
   * @generated
   */
  String getStationID();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Station#getStationID <em>Station ID</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Station ID</em>' attribute.
   * @see #getStationID()
   * @generated
   */
  void setStationID(String value);

} // Station
