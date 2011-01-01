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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Credentials Provider Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.util.defs.CredentialsProviderDef#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getCredentialsProviderDef()
 * @model abstract="true"
 * @generated
 */
public interface CredentialsProviderDef extends Def
{
  /**
   * Returns the value of the '<em><b>User ID</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User ID</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>User ID</em>' attribute.
   * @see #setUserID(String)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getCredentialsProviderDef_UserID()
   * @model
   * @generated
   */
  String getUserID();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.CredentialsProviderDef#getUserID <em>User ID</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>User ID</em>' attribute.
   * @see #getUserID()
   * @generated
   */
  void setUserID(String value);

} // CredentialsProviderDef
