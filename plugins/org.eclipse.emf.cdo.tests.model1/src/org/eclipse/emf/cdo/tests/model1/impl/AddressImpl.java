/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.AddressImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.AddressImpl#getStreet <em>Street</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model1.impl.AddressImpl#getCity <em>City</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AddressImpl extends CDOObjectImpl implements Address
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AddressImpl()
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
    return Model1Package.eINSTANCE.getAddress();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(Model1Package.eINSTANCE.getAddress_Name(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(Model1Package.eINSTANCE.getAddress_Name(), newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getStreet()
  {
    return (String)eGet(Model1Package.eINSTANCE.getAddress_Street(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStreet(String newStreet)
  {
    eSet(Model1Package.eINSTANCE.getAddress_Street(), newStreet);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getCity()
  {
    return (String)eGet(Model1Package.eINSTANCE.getAddress_City(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCity(String newCity)
  {
    eSet(Model1Package.eINSTANCE.getAddress_City(), newCity);
  }

} // AddressImpl
