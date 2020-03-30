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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.DoubleListValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Double
 * List Value Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DoubleListValueStyleImpl#getDoubleListValue <em>Double List Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DoubleListValueStyleImpl extends NamedStyleImpl implements DoubleListValueStyle
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DoubleListValueStyleImpl()
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
    return NotationPackage.Literals.DOUBLE_LIST_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getDoubleListValue()
  {
    return (EList)eDynamicGet(NotationPackage.DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE, NotationPackage.Literals.DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE,
        true, true);
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
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE:
      return getDoubleListValue();
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
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE:
      getDoubleListValue().clear();
      getDoubleListValue().addAll((Collection)newValue);
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
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE:
      getDoubleListValue().clear();
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
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE:
      return !getDoubleListValue().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // DoubleListValueStyleImpl
