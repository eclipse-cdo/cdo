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
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.gmf.runtime.notation.DataTypeStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Type Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DataTypeStyleImpl#getInstanceType <em>Instance Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataTypeStyleImpl extends NamedStyleImpl implements DataTypeStyle
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DataTypeStyleImpl()
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
    return NotationPackage.Literals.DATA_TYPE_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getInstanceType()
  {
    return (EDataType)eDynamicGet(NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE, NotationPackage.Literals.DATA_TYPE_STYLE__INSTANCE_TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType basicGetInstanceType()
  {
    return (EDataType)eDynamicGet(NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE, NotationPackage.Literals.DATA_TYPE_STYLE__INSTANCE_TYPE, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setInstanceType(EDataType newInstanceType)
  {
    eDynamicSet(NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE, NotationPackage.Literals.DATA_TYPE_STYLE__INSTANCE_TYPE, newInstanceType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getStringFromObject(Object objectValue)
  {
    if (getInstanceType() == null && objectValue instanceof String)
    {
      return (String)objectValue;
    }
    return EcoreUtil.convertToString(getInstanceType(), objectValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getObjectFromString(String stringValue)
  {
    if (getInstanceType() == null)
    {
      return stringValue;
    }
    return EcoreUtil.createFromString(getInstanceType(), stringValue);
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
    case NotationPackage.DATA_TYPE_STYLE__NAME:
      return getName();
    case NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE:
      if (resolve)
      {
        return getInstanceType();
      }
      return basicGetInstanceType();
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
    case NotationPackage.DATA_TYPE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE:
      setInstanceType((EDataType)newValue);
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
    case NotationPackage.DATA_TYPE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE:
      setInstanceType((EDataType)null);
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
    case NotationPackage.DATA_TYPE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.DATA_TYPE_STYLE__INSTANCE_TYPE:
      return basicGetInstanceType() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // DataTypeStyleImpl
