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
package org.eclipse.emf.cdo.lm.security;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Type Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#getModuleType <em>Module Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#isIncludeUntyped <em>Include Untyped</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleTypeFilter()
 * @model
 * @generated
 */
public interface ModuleTypeFilter extends LMFilter
{
  /**
   * Returns the value of the '<em><b>Module Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Module Type</em>' attribute.
   * @see #setModuleType(String)
   * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleTypeFilter_ModuleType()
   * @model
   * @generated
   */
  String getModuleType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#getModuleType <em>Module Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Module Type</em>' attribute.
   * @see #getModuleType()
   * @generated
   */
  void setModuleType(String value);

  /**
   * Returns the value of the '<em><b>Include Untyped</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Include Untyped</em>' attribute.
   * @see #setIncludeUntyped(boolean)
   * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleTypeFilter_IncludeUntyped()
   * @model
   * @generated
   */
  boolean isIncludeUntyped();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#isIncludeUntyped <em>Include Untyped</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Include Untyped</em>' attribute.
   * @see #isIncludeUntyped()
   * @generated
   */
  void setIncludeUntyped(boolean value);

} // ModuleTypeFilter
