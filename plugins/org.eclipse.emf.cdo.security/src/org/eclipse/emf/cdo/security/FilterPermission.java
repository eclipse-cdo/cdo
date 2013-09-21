/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Filter Permission</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.FilterPermission#getFilters <em>Filters</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getFilterPermission()
 * @model
 * @generated
 */
public interface FilterPermission extends Permission
{
  /**
   * Returns the value of the '<em><b>Filters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.PermissionFilter}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Filters</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Filters</em>' containment reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getFilterPermission_Filters()
   * @model containment="true" required="true"
   * @generated
   */
  EList<PermissionFilter> getFilters();

} // FilterPermission
