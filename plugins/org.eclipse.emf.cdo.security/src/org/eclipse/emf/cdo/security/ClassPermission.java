/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Permissionermission</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.ClassPermission#getApplicableClass <em>Applicable Class</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassPermission()
 * @model
 * @generated
 */
public interface ClassPermission extends Permission
{
  /**
   * Returns the value of the '<em><b>Applicable Class</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Applicable Class</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Applicable Class</em>' reference.
   * @see #setApplicableClass(EClass)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassPermission_ApplicableClass()
   * @model required="true"
   * @generated
   */
  EClass getApplicableClass();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ClassPermission#getApplicableClass <em>Applicable Class</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Applicable Class</em>' reference.
   * @see #getApplicableClass()
   * @generated
   */
  void setApplicableClass(EClass value);

} // ClassPermission
