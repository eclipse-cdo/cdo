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

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.ClassFilter#getApplicableClass <em>Applicable Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ClassFilter#isSubTypes <em>Sub Types</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassFilter()
 * @model
 * @generated
 */
public interface ClassFilter extends PermissionFilter
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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassFilter_ApplicableClass()
   * @model required="true"
   * @generated
   */
  EClass getApplicableClass();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ClassFilter#getApplicableClass <em>Applicable Class</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Applicable Class</em>' reference.
   * @see #getApplicableClass()
   * @generated
   */
  void setApplicableClass(EClass value);

  /**
   * Returns the value of the '<em><b>Sub Types</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sub Types</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sub Types</em>' attribute.
   * @see #setSubTypes(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassFilter_SubTypes()
   * @model default="true"
   * @generated
   */
  boolean isSubTypes();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ClassFilter#isSubTypes <em>Sub Types</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Sub Types</em>' attribute.
   * @see #isSubTypes()
   * @generated
   */
  void setSubTypes(boolean value);

} // ClassFilter
