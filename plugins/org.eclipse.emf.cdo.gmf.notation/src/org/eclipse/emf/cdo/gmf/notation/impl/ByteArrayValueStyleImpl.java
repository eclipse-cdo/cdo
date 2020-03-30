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

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.ByteArrayValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Byte Array Value Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ByteArrayValueStyleImpl#getByteArrayValue <em>Byte Array Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ByteArrayValueStyleImpl extends NamedStyleImpl implements ByteArrayValueStyle
{
  /**
   * The default value of the '{@link #getByteArrayValue() <em>Byte Array Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getByteArrayValue()
   * @generated
   * @ordered
   */
  protected static final byte[] BYTE_ARRAY_VALUE_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ByteArrayValueStyleImpl()
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
    return NotationPackage.Literals.BYTE_ARRAY_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte[] getByteArrayValue()
  {
    return (byte[])eDynamicGet(NotationPackage.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE, NotationPackage.Literals.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE,
        true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setByteArrayValue(byte[] newByteArrayValue)
  {
    eDynamicSet(NotationPackage.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE, NotationPackage.Literals.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE, newByteArrayValue);
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
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE:
      return getByteArrayValue();
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
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE:
      setByteArrayValue((byte[])newValue);
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
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE:
      setByteArrayValue(BYTE_ARRAY_VALUE_EDEFAULT);
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
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE:
      return BYTE_ARRAY_VALUE_EDEFAULT == null ? getByteArrayValue() != null : !BYTE_ARRAY_VALUE_EDEFAULT.equals(getByteArrayValue());
    }
    return eDynamicIsSet(featureID);
  }

} // ByteArrayValueStyleImpl
