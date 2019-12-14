/******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
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

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.StringValueStyle;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>String Value Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StringValueStyleImpl#getStringValue <em>String Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StringValueStyleImpl extends NamedStyleImpl implements StringValueStyle
{
  /**
   * The default value of the '{@link #getStringValue() <em>String Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStringValue()
   * @generated
   * @ordered
   */
  protected static final String STRING_VALUE_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StringValueStyleImpl()
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
    return NotationPackage.Literals.STRING_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getStringValue()
  {
    return (String)eDynamicGet(NotationPackage.STRING_VALUE_STYLE__STRING_VALUE, NotationPackage.Literals.STRING_VALUE_STYLE__STRING_VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStringValue(String newStringValue)
  {
    eDynamicSet(NotationPackage.STRING_VALUE_STYLE__STRING_VALUE, NotationPackage.Literals.STRING_VALUE_STYLE__STRING_VALUE, newStringValue);
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
    case NotationPackage.STRING_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.STRING_VALUE_STYLE__STRING_VALUE:
      return getStringValue();
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
    case NotationPackage.STRING_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.STRING_VALUE_STYLE__STRING_VALUE:
      setStringValue((String)newValue);
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
    case NotationPackage.STRING_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.STRING_VALUE_STYLE__STRING_VALUE:
      setStringValue(STRING_VALUE_EDEFAULT);
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
    case NotationPackage.STRING_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.STRING_VALUE_STYLE__STRING_VALUE:
      return STRING_VALUE_EDEFAULT == null ? getStringValue() != null : !STRING_VALUE_EDEFAULT.equals(getStringValue());
    }
    return eDynamicIsSet(featureID);
  }

} // StringValueStyleImpl
