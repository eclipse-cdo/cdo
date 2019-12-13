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
import org.gastro.inventory.Station;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Station</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.StationImpl#getStationID <em>Station ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class StationImpl extends CDOObjectImpl implements Station
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected StationImpl()
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
    return InventoryPackage.Literals.STATION;
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
  public String getStationID()
  {
    return (String)eGet(InventoryPackage.Literals.STATION__STATION_ID, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setStationID(String newStationID)
  {
    eSet(InventoryPackage.Literals.STATION__STATION_ID, newStationID);
  }

} // StationImpl
