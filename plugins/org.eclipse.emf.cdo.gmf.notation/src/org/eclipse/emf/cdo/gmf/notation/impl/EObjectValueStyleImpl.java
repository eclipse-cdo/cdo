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
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.EObjectValueStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>EObject
 * Value Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.EObjectValueStyleImpl#getEObjectValue <em>EObject Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EObjectValueStyleImpl extends NamedStyleImpl implements EObjectValueStyle
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected EObjectValueStyleImpl()
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
    return NotationPackage.Literals.EOBJECT_VALUE_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getEObjectValue()
  {
    return (EObject)eDynamicGet(NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE, NotationPackage.Literals.EOBJECT_VALUE_STYLE__EOBJECT_VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EObject basicGetEObjectValue()
  {
    return (EObject)eDynamicGet(NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE, NotationPackage.Literals.EOBJECT_VALUE_STYLE__EOBJECT_VALUE, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEObjectValue(EObject newEObjectValue)
  {
    eDynamicSet(NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE, NotationPackage.Literals.EOBJECT_VALUE_STYLE__EOBJECT_VALUE, newEObjectValue);
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
    case NotationPackage.EOBJECT_VALUE_STYLE__NAME:
      return getName();
    case NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE:
      if (resolve)
      {
        return getEObjectValue();
      }
      return basicGetEObjectValue();
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
    case NotationPackage.EOBJECT_VALUE_STYLE__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE:
      setEObjectValue((EObject)newValue);
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
    case NotationPackage.EOBJECT_VALUE_STYLE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE:
      setEObjectValue((EObject)null);
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
    case NotationPackage.EOBJECT_VALUE_STYLE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.EOBJECT_VALUE_STYLE__EOBJECT_VALUE:
      return basicGetEObjectValue() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // EObjectValueStyleImpl
