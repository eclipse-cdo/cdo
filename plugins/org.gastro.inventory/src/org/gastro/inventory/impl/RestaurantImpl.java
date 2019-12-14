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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.gastro.inventory.Department;
import org.gastro.inventory.InventoryPackage;
import org.gastro.inventory.MenuCard;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Station;
import org.gastro.inventory.Table;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Restaurant</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.gastro.inventory.impl.RestaurantImpl#getName <em>Name</em>}</li>
 * <li>{@link org.gastro.inventory.impl.RestaurantImpl#getDepartments <em>Departments</em>}</li>
 * <li>{@link org.gastro.inventory.impl.RestaurantImpl#getMenuCards <em>Menu Cards</em>}</li>
 * <li>{@link org.gastro.inventory.impl.RestaurantImpl#getTables <em>Tables</em>}</li>
 * <li>{@link org.gastro.inventory.impl.RestaurantImpl#getStations <em>Stations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RestaurantImpl extends CDOObjectImpl implements Restaurant
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected RestaurantImpl()
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
    return InventoryPackage.Literals.RESTAURANT;
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
  public String getName()
  {
    return (String)eGet(InventoryPackage.Literals.RESTAURANT__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(InventoryPackage.Literals.RESTAURANT__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Department> getDepartments()
  {
    return (EList<Department>)eGet(InventoryPackage.Literals.RESTAURANT__DEPARTMENTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<MenuCard> getMenuCards()
  {
    return (EList<MenuCard>)eGet(InventoryPackage.Literals.RESTAURANT__MENU_CARDS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Table> getTables()
  {
    return (EList<Table>)eGet(InventoryPackage.Literals.RESTAURANT__TABLES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public EList<Station> getStations()
  {
    EList<Station> stations = new BasicEList<>();
    for (Department department : getDepartments())
    {
      stations.add(department);
    }

    for (Table table : getTables())
    {
      stations.add(table);
    }

    return stations;
  }

} // RestaurantImpl
