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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.IntListValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Int List Value Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.IntListValueStyleImpl#getIntListValue <em>Int List Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IntListValueStyleImpl extends NamedStyleImpl implements IntListValueStyle
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IntListValueStyleImpl()
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
    return NotationPackage.Literals.INT_LIST_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getIntListValue()
  {
    return (EList)eDynamicGet(NotationPackage.INT_LIST_VALUE_STYLE__INT_LIST_VALUE, NotationPackage.Literals.INT_LIST_VALUE_STYLE__INT_LIST_VALUE, true, true);
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
    case NotationPackage.INT_LIST_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.INT_LIST_VALUE_STYLE__INT_LIST_VALUE:
      return getIntListValue();
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
    case NotationPackage.INT_LIST_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.INT_LIST_VALUE_STYLE__INT_LIST_VALUE:
      getIntListValue().clear();
      getIntListValue().addAll((Collection)newValue);
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
    case NotationPackage.INT_LIST_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.INT_LIST_VALUE_STYLE__INT_LIST_VALUE:
      getIntListValue().clear();
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
    case NotationPackage.INT_LIST_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.INT_LIST_VALUE_STYLE__INT_LIST_VALUE:
      return !getIntListValue().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // IntListValueStyleImpl
