/**
 * <copyright>
 * </copyright>
 *
 * $Id: ResponseNegotiatorDef.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Response Negotiator Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef#getCredentialsProvider <em>Credentials Provider</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getResponseNegotiatorDef()
 * @model
 * @generated
 */
public interface ResponseNegotiatorDef extends NegotiatorDef {

	/**
	 * Returns the value of the '<em><b>Credentials Provider</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Credentials Provider</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Credentials Provider</em>' reference.
	 * @see #setCredentialsProvider(CredentialsProviderDef)
	 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getResponseNegotiatorDef_CredentialsProvider()
	 * @model
	 * @generated
	 */
	CredentialsProviderDef getCredentialsProvider();

	/**
	 * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef#getCredentialsProvider <em>Credentials Provider</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Credentials Provider</em>' reference.
	 * @see #getCredentialsProvider()
	 * @generated
	 */
	void setCredentialsProvider(CredentialsProviderDef value);
} // ResponseNegotiatorDef
