/******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
import org.eclipse.emf.ecore.EDataType;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.SingleValueStyle;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Single
 * Value Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.SingleValueStyleImpl#getRawValue <em>Raw Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SingleValueStyleImpl extends DataTypeStyleImpl implements SingleValueStyle
{
  /**
   * The default value of the '{@link #getRawValue() <em>Raw Value</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getRawValue()
   * @generated
   * @ordered
   */
  protected static final String RAW_VALUE_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected SingleValueStyleImpl()
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
    return NotationPackage.Literals.SINGLE_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getRawValue()
  {
    return (String)eDynamicGet(NotationPackage.SINGLE_VALUE_STYLE__RAW_VALUE, NotationPackage.Literals.SINGLE_VALUE_STYLE__RAW_VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void setRawValue(String newRawValue)
  {
    if (getInstanceType() != null)
    {
      try
      {
        getObjectFromString(newRawValue);
      }
      catch (Exception e)
      {
        throw new IllegalArgumentException("Value <" + newRawValue//$NON-NLS-1$
            + "> cannot be associated with Data type <"//$NON-NLS-1$
            + getInstanceType().toString() + ">: " + e.getMessage());//$NON-NLS-1$
      }
    }
    setRawValueGen(newRawValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setRawValueGen(String newRawValue)
  {
    eDynamicSet(NotationPackage.SINGLE_VALUE_STYLE__RAW_VALUE, NotationPackage.Literals.SINGLE_VALUE_STYLE__RAW_VALUE, newRawValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public Object getValue()
  {
    return getObjectFromString(getRawValue());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void setValue(Object newValue)
  {
    setRawValue(getStringFromObject(newValue));
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
    case NotationPackage.SINGLE_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.SINGLE_VALUE_STYLE__INSTANCE_TYPE:
      if (resolve)
      {
        return getInstanceType();
      }
      return basicGetInstanceType();
    case NotationPackage.SINGLE_VALUE_STYLE__RAW_VALUE:
      return getRawValue();
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
    case NotationPackage.SINGLE_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.SINGLE_VALUE_STYLE__INSTANCE_TYPE:
      setInstanceType((EDataType)newValue);
      return;
    case NotationPackage.SINGLE_VALUE_STYLE__RAW_VALUE:
      setRawValue((String)newValue);
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
    case NotationPackage.SINGLE_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.SINGLE_VALUE_STYLE__INSTANCE_TYPE:
      setInstanceType((EDataType)null);
      return;
    case NotationPackage.SINGLE_VALUE_STYLE__RAW_VALUE:
      setRawValue(RAW_VALUE_EDEFAULT);
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
    case NotationPackage.SINGLE_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.SINGLE_VALUE_STYLE__INSTANCE_TYPE:
      return basicGetInstanceType() != null;
    case NotationPackage.SINGLE_VALUE_STYLE__RAW_VALUE:
      return RAW_VALUE_EDEFAULT == null ? getRawValue() != null : !RAW_VALUE_EDEFAULT.equals(getRawValue());
    }
    return eDynamicIsSet(featureID);
  }

} // SingleValueStyleImpl
