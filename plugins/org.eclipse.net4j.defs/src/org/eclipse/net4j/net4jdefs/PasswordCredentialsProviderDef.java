/**
 * <copyright>
 * </copyright>
 *
 * $Id: PasswordCredentialsProviderDef.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Password Credentials Provider Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef#getPassword <em>Password</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getPasswordCredentialsProviderDef()
 * @model
 * @generated
 */
public interface PasswordCredentialsProviderDef extends CredentialsProviderDef {
	/**
	 * Returns the value of the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Password</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Password</em>' attribute.
	 * @see #setPassword(String)
	 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getPasswordCredentialsProviderDef_Password()
	 * @model
	 * @generated
	 */
	String getPassword();

	/**
	 * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef#getPassword <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Password</em>' attribute.
	 * @see #getPassword()
	 * @generated
	 */
	void setPassword(String value);

} // PasswordCredentialsProviderDef
