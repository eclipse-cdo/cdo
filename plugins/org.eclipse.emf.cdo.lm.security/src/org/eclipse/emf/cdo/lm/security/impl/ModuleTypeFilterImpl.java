/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
 *   <li>{@link org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl#getModuleType <em>Module Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl#isIncludeUntyped <em>Include Untyped</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleTypeFilterImpl extends LMFilterImpl implements ModuleTypeFilter
{
  /**
   * The default value of the '{@link #getModuleType() <em>Module Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModuleType()
   * @generated
   * @ordered
   */
  protected static final String MODULE_TYPE_EDEFAULT = null;

  /**
   * The default value of the '{@link #isIncludeUntyped() <em>Include Untyped</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isIncludeUntyped()
   * @generated
   * @ordered
   */
  protected static final boolean INCLUDE_UNTYPED_EDEFAULT = false;

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
  public String getModuleType()
  {
    return (String)eDynamicGet(LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE, LMSecurityPackage.Literals.MODULE_TYPE_FILTER__MODULE_TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModuleType(String newModuleType)
  {
    eDynamicSet(LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE, LMSecurityPackage.Literals.MODULE_TYPE_FILTER__MODULE_TYPE, newModuleType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isIncludeUntyped()
  {
    return (Boolean)eDynamicGet(LMSecurityPackage.MODULE_TYPE_FILTER__INCLUDE_UNTYPED, LMSecurityPackage.Literals.MODULE_TYPE_FILTER__INCLUDE_UNTYPED, true,
        true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setIncludeUntyped(boolean newIncludeUntyped)
  {
    eDynamicSet(LMSecurityPackage.MODULE_TYPE_FILTER__INCLUDE_UNTYPED, LMSecurityPackage.Literals.MODULE_TYPE_FILTER__INCLUDE_UNTYPED, newIncludeUntyped);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE:
      return getModuleType();
    case LMSecurityPackage.MODULE_TYPE_FILTER__INCLUDE_UNTYPED:
      return isIncludeUntyped();
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE:
      setModuleType((String)newValue);
      return;
    case LMSecurityPackage.MODULE_TYPE_FILTER__INCLUDE_UNTYPED:
      setIncludeUntyped((Boolean)newValue);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE:
      setModuleType(MODULE_TYPE_EDEFAULT);
      return;
    case LMSecurityPackage.MODULE_TYPE_FILTER__INCLUDE_UNTYPED:
      setIncludeUntyped(INCLUDE_UNTYPED_EDEFAULT);
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
    case LMSecurityPackage.MODULE_TYPE_FILTER__MODULE_TYPE:
      return MODULE_TYPE_EDEFAULT == null ? getModuleType() != null : !MODULE_TYPE_EDEFAULT.equals(getModuleType());
    case LMSecurityPackage.MODULE_TYPE_FILTER__INCLUDE_UNTYPED:
      return isIncludeUntyped() != INCLUDE_UNTYPED_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * @ADDED
   */
  @Override
  protected boolean filterMissingComparisonValue()
  {
    return isIncludeUntyped();
  }

  /**
   * @ADDED
   */
  @Override
  protected String getComparisonKey()
  {
    return LMSecurityPackage.Literals.MODULE_TYPE_FILTER__MODULE_TYPE.getName();
  }

  /**
   * @ADDED
   */
  @Override
  protected String getComparisonValue()
  {
    return getModuleType();
  }
} // ModuleTypeFilterImpl
