/**
 * <copyright>
 * </copyright>
 *
 * $Id: CredentialsProviderDef.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;

import org.eclipse.net4j.util.net4jutildefs.Def;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Credentials Provider Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.CredentialsProviderDef#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getCredentialsProviderDef()
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
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getCredentialsProviderDef_UserID()
   * @model
   * @generated
   */
  String getUserID();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.CredentialsProviderDef#getUserID <em>User ID</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>User ID</em>' attribute.
   * @see #getUserID()
   * @generated
   */
  void setUserID(String value);

} // CredentialsProviderDef
