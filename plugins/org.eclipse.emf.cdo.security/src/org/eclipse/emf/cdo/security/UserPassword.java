/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User Password</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.UserPassword#getEncrypted <em>Encrypted</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getUserPassword()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface UserPassword extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Encrypted</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Encrypted</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Encrypted</em>' attribute.
   * @see #setEncrypted(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUserPassword_Encrypted()
   * @model
   * @generated
   */
  String getEncrypted();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.UserPassword#getEncrypted <em>Encrypted</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Encrypted</em>' attribute.
   * @see #getEncrypted()
   * @generated
   */
  void setEncrypted(String value);

} // UserPassword
