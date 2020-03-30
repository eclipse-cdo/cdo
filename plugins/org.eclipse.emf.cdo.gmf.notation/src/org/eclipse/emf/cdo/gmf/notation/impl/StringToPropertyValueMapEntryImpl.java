/******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.PropertyValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>String To Property Value Map Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StringToPropertyValueMapEntryImpl#getTypedKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StringToPropertyValueMapEntryImpl#getTypedValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StringToPropertyValueMapEntryImpl extends CDOObjectImpl implements BasicEMap.Entry
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

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StringToPropertyValueMapEntryImpl()
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
    return NotationPackage.Literals.STRING_TO_PROPERTY_VALUE_MAP_ENTRY;
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
    return (String)eDynamicGet(NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY, NotationPackage.Literals.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY, true,
        true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTypedKey(String newKey)
  {
    eDynamicSet(NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY, NotationPackage.Literals.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY, newKey);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertyValue getTypedValue()
  {
    return (PropertyValue)eDynamicGet(NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE,
        NotationPackage.Literals.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTypedValue(PropertyValue newValue, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newValue, NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTypedValue(PropertyValue newValue)
  {
    eDynamicSet(NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE, NotationPackage.Literals.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE:
      return basicSetTypedValue(null, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY:
      return getTypedKey();
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE:
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
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY:
      setTypedKey((String)newValue);
      return;
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE:
      setTypedValue((PropertyValue)newValue);
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
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY:
      setTypedKey(KEY_EDEFAULT);
      return;
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE:
      setTypedValue((PropertyValue)null);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY:
      return KEY_EDEFAULT == null ? getTypedKey() != null : !KEY_EDEFAULT.equals(getTypedKey());
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE:
      return getTypedValue() != null;
    }
    return eDynamicIsSet(featureID);
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
  public Object getKey()
  {
    return getTypedKey();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setKey(Object key)
  {
    setTypedKey((String)key);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getValue()
  {
    return getTypedValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object setValue(Object value)
  {
    Object oldValue = getValue();
    setTypedValue((PropertyValue)value);
    return oldValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EMap getEMap()
  {
    EObject container = eContainer();
    return container == null ? null : (EMap)container.eGet(eContainmentFeature());
  }

} // StringToPropertyValueMapEntryImpl
