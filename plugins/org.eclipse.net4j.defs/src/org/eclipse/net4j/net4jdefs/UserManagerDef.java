/**
 * <copyright>
 * </copyright>
 *
 * $Id: UserManagerDef.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;

import org.eclipse.net4j.util.net4jutildefs.Def;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User Manager Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.net4jdefs.UserManagerDef#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getUserManagerDef()
 * @model
 * @generated
 */
public interface UserManagerDef extends Def {

	/**
	 * Returns the value of the '<em><b>User</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.net4j.net4jdefs.User}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User</em>' reference list.
	 * @see #isSetUser()
	 * @see #unsetUser()
	 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getUserManagerDef_User()
	 * @model unsettable="true" required="true"
	 * @generated
	 */
	EList<User> getUser();

	/**
	 * Unsets the value of the '{@link org.eclipse.net4j.net4jdefs.UserManagerDef#getUser <em>User</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetUser()
	 * @see #getUser()
	 * @generated
	 */
	void unsetUser();

	/**
	 * Returns whether the value of the '{@link org.eclipse.net4j.net4jdefs.UserManagerDef#getUser <em>User</em>}' reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>User</em>' reference list is set.
	 * @see #unsetUser()
	 * @see #getUser()
	 * @generated
	 */
	boolean isSetUser();
} // UserManagerDef
