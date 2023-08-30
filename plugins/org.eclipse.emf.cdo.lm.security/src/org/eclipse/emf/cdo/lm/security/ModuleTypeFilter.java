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
 *   <li>{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#getModuleTypeName <em>Module Type Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleTypeFilter()
 * @model
 * @generated
 */
public interface ModuleTypeFilter extends LMFilter
{
  /**
   * Returns the value of the '<em><b>Module Type Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Module Type Name</em>' attribute.
   * @see #setModuleTypeName(String)
   * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleTypeFilter_ModuleTypeName()
   * @model
   * @generated
   */
  String getModuleTypeName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#getModuleTypeName <em>Module Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Module Type Name</em>' attribute.
   * @see #getModuleTypeName()
   * @generated
   */
  void setModuleTypeName(String value);

} // ModuleTypeFilter
