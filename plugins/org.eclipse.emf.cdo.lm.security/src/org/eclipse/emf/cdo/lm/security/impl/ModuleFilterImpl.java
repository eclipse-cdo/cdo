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
import org.eclipse.emf.cdo.lm.security.ModuleFilter;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.impl.ModuleFilterImpl#getModuleName <em>Module Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleFilterImpl extends LMFilterImpl implements ModuleFilter
{
  /**
   * The default value of the '{@link #getModuleName() <em>Module Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModuleName()
   * @generated
   * @ordered
   */
  protected static final String MODULE_NAME_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModuleFilterImpl()
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
    return LMSecurityPackage.Literals.MODULE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getModuleName()
  {
    return (String)eDynamicGet(LMSecurityPackage.MODULE_FILTER__MODULE_NAME, LMSecurityPackage.Literals.MODULE_FILTER__MODULE_NAME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModuleName(String newModuleName)
  {
    eDynamicSet(LMSecurityPackage.MODULE_FILTER__MODULE_NAME, LMSecurityPackage.Literals.MODULE_FILTER__MODULE_NAME, newModuleName);
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
    case LMSecurityPackage.MODULE_FILTER__MODULE_NAME:
      return getModuleName();
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
    case LMSecurityPackage.MODULE_FILTER__MODULE_NAME:
      setModuleName((String)newValue);
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
    case LMSecurityPackage.MODULE_FILTER__MODULE_NAME:
      setModuleName(MODULE_NAME_EDEFAULT);
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
    case LMSecurityPackage.MODULE_FILTER__MODULE_NAME:
      return MODULE_NAME_EDEFAULT == null ? getModuleName() != null : !MODULE_NAME_EDEFAULT.equals(getModuleName());
    }
    return super.eIsSet(featureID);
  }

  @Override
  protected String getComparisonKey()
  {
    return LMSecurityPackage.Literals.MODULE_FILTER__MODULE_NAME.getName();
  }

  @Override
  protected String getComparisonValue()
  {
    return getModuleName();
  }
} // ModuleFilterImpl
