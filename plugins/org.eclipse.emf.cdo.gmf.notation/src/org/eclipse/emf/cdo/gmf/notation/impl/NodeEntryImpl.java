/******************************************************************************
 * Copyright (c) 2018-2020 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.Alignment;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Node
 * Entry</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.NodeEntryImpl#getTypedValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.NodeEntryImpl#getTypedKey <em>Key</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class NodeEntryImpl extends CDOObjectImpl implements BasicEMap.Entry
{
  /**
  * The default value of the '{@link #getTypedValue() <em>Value</em>}' attribute.
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @see #getTypedValue()
  * @generated
  * @ordered
  */
  protected static final Alignment VALUE_EDEFAULT = Alignment.CENTER_LITERAL;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected NodeEntryImpl()
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
    return NotationPackage.Literals.NODE_ENTRY;
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Alignment getTypedValue()
  {
    return (Alignment)eDynamicGet(NotationPackage.NODE_ENTRY__VALUE, NotationPackage.Literals.NODE_ENTRY__VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTypedValue(Alignment newValue)
  {
    eDynamicSet(NotationPackage.NODE_ENTRY__VALUE, NotationPackage.Literals.NODE_ENTRY__VALUE, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Node getTypedKey()
  {
    return (Node)eDynamicGet(NotationPackage.NODE_ENTRY__KEY, NotationPackage.Literals.NODE_ENTRY__KEY, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Node basicGetTypedKey()
  {
    return (Node)eDynamicGet(NotationPackage.NODE_ENTRY__KEY, NotationPackage.Literals.NODE_ENTRY__KEY, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTypedKey(Node newKey)
  {
    eDynamicSet(NotationPackage.NODE_ENTRY__KEY, NotationPackage.Literals.NODE_ENTRY__KEY, newKey);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case NotationPackage.NODE_ENTRY__VALUE:
      return getTypedValue();
    case NotationPackage.NODE_ENTRY__KEY:
      if (resolve)
      {
        return getTypedKey();
      }
      return basicGetTypedKey();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.NODE_ENTRY__VALUE:
      setTypedValue((Alignment)newValue);
      return;
    case NotationPackage.NODE_ENTRY__KEY:
      setTypedKey((Node)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.NODE_ENTRY__VALUE:
      setTypedValue(VALUE_EDEFAULT);
      return;
    case NotationPackage.NODE_ENTRY__KEY:
      setTypedKey((Node)null);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.NODE_ENTRY__VALUE:
      return getTypedValue() != VALUE_EDEFAULT;
    case NotationPackage.NODE_ENTRY__KEY:
      return basicGetTypedKey() != null;
    }
    return eDynamicIsSet(featureID);
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
  public Object getKey()
  {
    return getTypedKey();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setKey(Object key)
  {
    setTypedKey((Node)key);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getValue()
  {
    return getTypedValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object setValue(Object value)
  {
    Object oldValue = getValue();
    setTypedValue((Alignment)value);
    return oldValue;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EMap getEMap()
  {
    EObject container = eContainer();
    return container == null ? null : (EMap)container.eGet(eContainmentFeature());
  }

} // NodeEntryImpl
