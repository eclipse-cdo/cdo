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

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.BooleanValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Boolean
 * Value Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.BooleanValueStyleImpl#isBooleanValue <em>Boolean Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BooleanValueStyleImpl extends NamedStyleImpl implements BooleanValueStyle
{
  /**
  * The default value of the '{@link #isBooleanValue() <em>Boolean Value</em>}' attribute.
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @see #isBooleanValue()
  * @generated
  * @ordered
  */
  protected static final boolean BOOLEAN_VALUE_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BooleanValueStyleImpl()
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
    return NotationPackage.Literals.BOOLEAN_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isBooleanValue()
  {
    return ((Boolean)eDynamicGet(NotationPackage.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE, NotationPackage.Literals.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE, true, true))
        .booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBooleanValue(boolean newBooleanValue)
  {
    eDynamicSet(NotationPackage.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE, NotationPackage.Literals.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE, new Boolean(newBooleanValue));
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
    case NotationPackage.BOOLEAN_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE:
      return isBooleanValue() ? Boolean.TRUE : Boolean.FALSE;
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
    case NotationPackage.BOOLEAN_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE:
      setBooleanValue(((Boolean)newValue).booleanValue());
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
    case NotationPackage.BOOLEAN_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE:
      setBooleanValue(BOOLEAN_VALUE_EDEFAULT);
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
    case NotationPackage.BOOLEAN_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE:
      return isBooleanValue() != BOOLEAN_VALUE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // BooleanValueStyleImpl
