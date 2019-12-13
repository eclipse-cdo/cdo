/*
 * Copyright (c) 2010-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 * $Id: StringToAddressContainmentMapImpl.java,v 1.2 2011-01-01 11:01:57 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model2.Model2Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>String To Address Containment Map</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.StringToAddressContainmentMapImpl#getTypedKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.StringToAddressContainmentMapImpl#getTypedValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StringToAddressContainmentMapImpl extends CDOObjectImpl implements BasicEMap.Entry<String, Address>
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected StringToAddressContainmentMapImpl()
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
    return Model2Package.eINSTANCE.getStringToAddressContainmentMap();
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
  public String getTypedKey()
  {
    return (String)eGet(Model2Package.eINSTANCE.getStringToAddressContainmentMap_Key(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTypedKey(String newKey)
  {
    eSet(Model2Package.eINSTANCE.getStringToAddressContainmentMap_Key(), newKey);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Address getTypedValue()
  {
    return (Address)eGet(Model2Package.eINSTANCE.getStringToAddressContainmentMap_Value(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTypedValue(Address newValue)
  {
    eSet(Model2Package.eINSTANCE.getStringToAddressContainmentMap_Value(), newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected int hash = -1;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getHash()
  {
    if (hash == -1)
    {
      Object theKey = getKey();
      hash = theKey == null ? 0 : theKey.hashCode();
    }
    return hash;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHash(int hash)
  {
    this.hash = hash;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getKey()
  {
    return getTypedKey();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setKey(String key)
  {
    setTypedKey(key);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Address getValue()
  {
    return getTypedValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Address setValue(Address value)
  {
    Address oldValue = getValue();
    setTypedValue(value);
    return oldValue;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, Address> getEMap()
  {
    EObject container = eContainer();
    return container == null ? null : (EMap<String, Address>)container.eGet(eContainmentFeature());
  }

} // StringToAddressContainmentMapImpl
