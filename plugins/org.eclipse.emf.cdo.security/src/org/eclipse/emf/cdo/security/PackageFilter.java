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

import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.PackageFilter#getApplicablePackage <em>Applicable Package</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPackageFilter()
 * @model
 * @generated
 */
public interface PackageFilter extends PermissionFilter
{
  /**
   * Returns the value of the '<em><b>Applicable Package</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Applicable Package</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Applicable Package</em>' reference.
   * @see #setApplicablePackage(EPackage)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getPackageFilter_ApplicablePackage()
   * @model required="true"
   * @generated
   */
  EPackage getApplicablePackage();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.PackageFilter#getApplicablePackage <em>Applicable Package</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Applicable Package</em>' reference.
   * @see #getApplicablePackage()
   * @generated
   */
  void setApplicablePackage(EPackage value);

} // PackageFilter
