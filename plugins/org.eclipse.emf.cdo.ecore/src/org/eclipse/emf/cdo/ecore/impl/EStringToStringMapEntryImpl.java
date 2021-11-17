/*
 * Copyright (c) 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EString To String Map Map.Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl#getTypedKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl#getTypedValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated not
 */
public class EStringToStringMapEntryImpl extends CDOObjectImpl implements BasicEMap.Entry<String, String>
{
  /**
   * The default value of the '{@link #getTypedKey() <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTypedKey()
   * @generated
   * @ordered
   */
  protected static final String KEY_EDEFAULT = null;

  // /**
  // * The cached value of the '{@link #getTypedKey() <em>Key</em>}' attribute.
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @see #getTypedKey()
  // * @generated
  // * @ordered
  // */
  // protected String key = KEY_EDEFAULT;

  /**
   * The default value of the '{@link #getTypedValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTypedValue()
   * @generated
   * @ordered
   */
  protected static final String VALUE_EDEFAULT = null;

  // /**
  // * The cached value of the '{@link #getTypedValue() <em>Value</em>}' attribute.
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @see #getTypedValue()
  // * @generated
  // * @ordered
  // */
  // protected String value = VALUE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EStringToStringMapEntryImpl()
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
    return EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getTypedKey()
  {
    return (String)eDynamicGet(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY__KEY, true);
  }

  public void setTypedKey(String newKey)
  {
    setTypedKeyGen(newKey == null ? null : newKey.intern());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setTypedKeyGen(String newKey)
  {
    eDynamicSet(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY__KEY, newKey);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getTypedValue()
  {
    return (String)eDynamicGet(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY__VALUE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setTypedValue(String newValue)
  {
    eDynamicSet(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY__VALUE, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__KEY:
      return getTypedKey();
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__VALUE:
      return getTypedValue();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__KEY:
      setTypedKey((String)newValue);
      return;
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__VALUE:
      setTypedValue((String)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__KEY:
      setTypedKey(KEY_EDEFAULT);
      return;
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__VALUE:
      setTypedValue(VALUE_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__KEY:
      return getTypedKey() != null;
    case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY__VALUE:
      return getTypedValue() != null;
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (key: ");
    result.append(getTypedKey());
    result.append(", value: ");
    result.append(getTypedValue());
    result.append(')');
    return result.toString();
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
  public String getValue()
  {
    return getTypedValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String setValue(String value)
  {
    String oldValue = getValue();
    setTypedValue(value);
    return oldValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, String> getEMap()
  {
    EObject container = eContainer();
    return container == null ? null : (EMap<String, String>)container.eGet(eContainmentFeature());
  }

} // EStringToStringMapEntryImpl
