/**
 * <copyright>
 * </copyright>
 *
 * $Id: ServerConnector.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Server Connector</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.net4jdefs.ServerConnector#getAcceptorDef <em>Acceptor Def</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.net4jdefs.Net4jdefsPackage#getServerConnector()
 * @model abstract="true"
 * @generated
 */
public interface ServerConnector extends ConnectorDef {
	/**
	 * Returns the value of the '<em><b>Acceptor Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Acceptor Def</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Acceptor Def</em>' reference.
	 * @see #setAcceptorDef(AcceptorDef)
	 * @see org.eclipse.net4j.net4jdefs.Net4jdefsPackage#getServerConnector_AcceptorDef()
	 * @model
	 * @generated
	 */
	AcceptorDef getAcceptorDef();

	/**
	 * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ServerConnector#getAcceptorDef <em>Acceptor Def</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Acceptor Def</em>' reference.
	 * @see #getAcceptorDef()
	 * @generated
	 */
	void setAcceptorDef(AcceptorDef value);

} // ServerConnector
