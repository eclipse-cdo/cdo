/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.security.impl;

import org.eclipse.emf.cdo.lm.security.LMSecurityPackage;
import org.eclipse.emf.cdo.lm.security.ModuleTypeFilter;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Type Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl#getModuleTypeName <em>Module Type Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleTypeFilterImpl extends LMFilterImpl implements ModuleTypeFilter
{
  /**
   * The default value of the '{@link #getModuleTypeName() <em>Module Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModuleTypeName()
   * @generated
   * @ordered
   */
  protected static final String MODULE_TYPE_NAME_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModuleTypeFilterImpl()
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
    return LMSecurityPackage.Literals.MODULE_TYPE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getModuleTypeName()
  {
    return (String)eDynamicGet(LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE_NAME, LMSecurityPackage.Literals.MODULE_TYPE_FILTER__MODULE_TYPE_NAME, true,
        true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModuleTypeName(String newModuleTypeName)
  {
    eDynamicSet(LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE_NAME, LMSecurityPackage.Literals.MODULE_TYPE_FILTER__MODULE_TYPE_NAME, newModuleTypeName);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE_NAME:
      return getModuleTypeName();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE_NAME:
      setModuleTypeName((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE_NAME:
      setModuleTypeName(MODULE_TYPE_NAME_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE_NAME:
      return MODULE_TYPE_NAME_EDEFAULT == null ? getModuleTypeName() != null : !MODULE_TYPE_NAME_EDEFAULT.equals(getModuleTypeName());
    }
    return super.eIsSet(featureID);
  }

  @Override
  protected String getComparisonKey()
  {
    return LMSecurityPackage.Literals.MODULE_TYPE_FILTER__MODULE_TYPE_NAME.getName();
  }

  @Override
  protected String getComparisonValue()
  {
    return getModuleTypeName();
  }
} // ModuleTypeFilterImpl
