/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.Addressable;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Addressable</b></em>'.
 *
 * @since 4.0 <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.AddressableImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.AddressableImpl#getStreet <em>Street</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.examples.company.impl.AddressableImpl#getCity <em>City</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AddressableImpl extends CDOObjectImpl implements Addressable
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AddressableImpl()
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
    return CompanyPackage.Literals.ADDRESSABLE;
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
    return (String)eGet(CompanyPackage.Literals.ADDRESSABLE__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(CompanyPackage.Literals.ADDRESSABLE__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getStreet()
  {
    return (String)eGet(CompanyPackage.Literals.ADDRESSABLE__STREET, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStreet(String newStreet)
  {
    eSet(CompanyPackage.Literals.ADDRESSABLE__STREET, newStreet);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getCity()
  {
    return (String)eGet(CompanyPackage.Literals.ADDRESSABLE__CITY, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCity(String newCity)
  {
    eSet(CompanyPackage.Literals.ADDRESSABLE__CITY, newCity);
  }

} // AddressableImpl
