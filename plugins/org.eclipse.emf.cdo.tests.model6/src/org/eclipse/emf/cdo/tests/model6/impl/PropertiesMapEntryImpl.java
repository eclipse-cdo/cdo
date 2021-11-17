/*
 * Copyright (c) 2012, 2013, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Properties Map Map.Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl#getTypedKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl#getTypedValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PropertiesMapEntryImpl extends CDOObjectImpl implements BasicEMap.Entry<String, PropertiesMapEntryValue>
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertiesMapEntryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model6Package.Literals.PROPERTIES_MAP_ENTRY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTypedKey()
  {
    return (String)eGet(Model6Package.Literals.PROPERTIES_MAP_ENTRY__KEY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTypedKey(String newKey)
  {
    eSet(Model6Package.Literals.PROPERTIES_MAP_ENTRY__KEY, newKey);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertiesMapEntryValue getTypedValue()
  {
    return (PropertiesMapEntryValue)eGet(Model6Package.Literals.PROPERTIES_MAP_ENTRY__VALUE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTypedValue(PropertiesMapEntryValue newValue)
  {
    eSet(Model6Package.Literals.PROPERTIES_MAP_ENTRY__VALUE, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected int hash = -1;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHash(int hash)
  {
    this.hash = hash;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getKey()
  {
    return getTypedKey();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setKey(String key)
  {
    setTypedKey(key);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PropertiesMapEntryValue getValue()
  {
    return getTypedValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PropertiesMapEntryValue setValue(PropertiesMapEntryValue value)
  {
    PropertiesMapEntryValue oldValue = getValue();
    setTypedValue(value);
    return oldValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, PropertiesMapEntryValue> getEMap()
  {
    EObject container = eContainer();
    return container == null ? null : (EMap<String, PropertiesMapEntryValue>)container.eGet(eContainmentFeature());
  }

} // PropertiesMapEntryImpl
