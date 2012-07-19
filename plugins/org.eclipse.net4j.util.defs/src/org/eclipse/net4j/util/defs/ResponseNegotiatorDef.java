/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Response Negotiator Def</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.ResponseNegotiatorDef#getCredentialsProvider <em>Credentials Provider</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getResponseNegotiatorDef()
 * @model
 * @generated
 */
public interface ResponseNegotiatorDef extends NegotiatorDef
{
  /**
   * Returns the value of the '<em><b>Credentials Provider</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Credentials Provider</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Credentials Provider</em>' reference.
   * @see #setCredentialsProvider(CredentialsProviderDef)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getResponseNegotiatorDef_CredentialsProvider()
   * @model
   * @generated
   */
  CredentialsProviderDef getCredentialsProvider();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.ResponseNegotiatorDef#getCredentialsProvider <em>Credentials Provider</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Credentials Provider</em>' reference.
   * @see #getCredentialsProvider()
   * @generated
   */
  void setCredentialsProvider(CredentialsProviderDef value);

} // ResponseNegotiatorDef
