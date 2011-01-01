/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Password Credentials Provider Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef#getPassword <em>Password</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getPasswordCredentialsProviderDef()
 * @model
 * @generated
 */
public interface PasswordCredentialsProviderDef extends CredentialsProviderDef
{
  /**
   * Returns the value of the '<em><b>Password</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Password</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Password</em>' attribute.
   * @see #setPassword(String)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getPasswordCredentialsProviderDef_Password()
   * @model
   * @generated
   */
  String getPassword();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef#getPassword
   * <em>Password</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Password</em>' attribute.
   * @see #getPassword()
   * @generated
   */
  void setPassword(String value);

} // PasswordCredentialsProviderDef
