/**
 * <copyright>
 * </copyright>
 *
 * $Id: ChallengeNegotiatorDef.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;



/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Challenge Negotiator Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef#getUserManager <em>User Manager</em>}</li>
 *   <li>{@link org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef#getRandomizer <em>Randomizer</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getChallengeNegotiatorDef()
 * @model
 * @generated
 */
public interface ChallengeNegotiatorDef extends NegotiatorDef {

	/**
	 * Returns the value of the '<em><b>User Manager</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User Manager</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User Manager</em>' reference.
	 * @see #setUserManager(UserManagerDef)
	 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getChallengeNegotiatorDef_UserManager()
	 * @model
	 * @generated
	 */
	UserManagerDef getUserManager();

	/**
	 * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef#getUserManager <em>User Manager</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User Manager</em>' reference.
	 * @see #getUserManager()
	 * @generated
	 */
	void setUserManager(UserManagerDef value);

	/**
	 * Returns the value of the '<em><b>Randomizer</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Randomizer</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Randomizer</em>' reference.
	 * @see #setRandomizer(RandomizerDef)
	 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getChallengeNegotiatorDef_Randomizer()
	 * @model
	 * @generated
	 */
	RandomizerDef getRandomizer();

	/**
	 * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef#getRandomizer <em>Randomizer</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Randomizer</em>' reference.
	 * @see #getRandomizer()
	 * @generated
	 */
	void setRandomizer(RandomizerDef value);
} // ChallengeNegotiatorDef
