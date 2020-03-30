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

import org.eclipse.gmf.runtime.notation.DoubleValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Double
 * Value Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DoubleValueStyleImpl#getDoubleValue <em>Double Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DoubleValueStyleImpl extends NamedStyleImpl implements DoubleValueStyle
{
  /**
   * The default value of the '{@link #getDoubleValue() <em>Double Value</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getDoubleValue()
   * @generated
   * @ordered
   */
  protected static final double DOUBLE_VALUE_EDEFAULT = 0.0;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DoubleValueStyleImpl()
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
    return NotationPackage.Literals.DOUBLE_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public double getDoubleValue()
  {
    return ((Double)eDynamicGet(NotationPackage.DOUBLE_VALUE_STYLE__DOUBLE_VALUE, NotationPackage.Literals.DOUBLE_VALUE_STYLE__DOUBLE_VALUE, true, true))
        .doubleValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDoubleValue(double newDoubleValue)
  {
    eDynamicSet(NotationPackage.DOUBLE_VALUE_STYLE__DOUBLE_VALUE, NotationPackage.Literals.DOUBLE_VALUE_STYLE__DOUBLE_VALUE, new Double(newDoubleValue));
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
    case NotationPackage.DOUBLE_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.DOUBLE_VALUE_STYLE__DOUBLE_VALUE:
      return new Double(getDoubleValue());
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
    case NotationPackage.DOUBLE_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.DOUBLE_VALUE_STYLE__DOUBLE_VALUE:
      setDoubleValue(((Double)newValue).doubleValue());
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
    case NotationPackage.DOUBLE_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.DOUBLE_VALUE_STYLE__DOUBLE_VALUE:
      setDoubleValue(DOUBLE_VALUE_EDEFAULT);
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
    case NotationPackage.DOUBLE_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.DOUBLE_VALUE_STYLE__DOUBLE_VALUE:
      return getDoubleValue() != DOUBLE_VALUE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // DoubleValueStyleImpl
