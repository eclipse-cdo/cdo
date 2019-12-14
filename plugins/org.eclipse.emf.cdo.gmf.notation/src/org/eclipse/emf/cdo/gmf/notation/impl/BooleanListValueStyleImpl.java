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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.BooleanListValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Boolean List Value Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.BooleanListValueStyleImpl#getBooleanListValue <em>Boolean List Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BooleanListValueStyleImpl extends NamedStyleImpl implements BooleanListValueStyle
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BooleanListValueStyleImpl()
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
    return NotationPackage.Literals.BOOLEAN_LIST_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getBooleanListValue()
  {
    return (EList)eDynamicGet(NotationPackage.BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE,
        NotationPackage.Literals.BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE, true, true);
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
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE:
      return getBooleanListValue();
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
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE:
      getBooleanListValue().clear();
      getBooleanListValue().addAll((Collection)newValue);
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
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE:
      getBooleanListValue().clear();
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
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE:
      return !getBooleanListValue().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // BooleanListValueStyleImpl
