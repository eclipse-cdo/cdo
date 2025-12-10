/*
 * Copyright (c) 2012, 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Permission</b></em>'.
 * @deprecated As of 4.3 use {@link FilterPermission} and {@link PackageFilter}.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.PackagePermission#getApplicablePackage <em>Applicable Package</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPackagePermission()
 * @model
 * @generated
 */
@Deprecated
public interface PackagePermission extends Permission
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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getPackagePermission_ApplicablePackage()
   * @model required="true"
   * @generated
   */
  @Deprecated
  EPackage getApplicablePackage();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.PackagePermission#getApplicablePackage <em>Applicable Package</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Applicable Package</em>' reference.
   * @see #getApplicablePackage()
   * @generated
   */
  @Deprecated
  void setApplicablePackage(EPackage value);

} // PackagePermission
