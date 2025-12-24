/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reference Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.LinkedFilter#getFilter <em>Filter</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getLinkedFilter()
 * @model
 * @generated
 */
public interface LinkedFilter extends PermissionFilter
{
  /**
   * Returns the value of the '<em><b>Filter</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Filter</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Filter</em>' reference.
   * @see #setFilter(PermissionFilter)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getLinkedFilter_Filter()
   * @model required="true"
   * @generated
   */
  PermissionFilter getFilter();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.LinkedFilter#getFilter <em>Filter</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Filter</em>' reference.
   * @see #getFilter()
   * @generated
   */
  void setFilter(PermissionFilter value);

} // ReferenceFilter
