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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Challenge Negotiator Def</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getUserManager <em>User Manager</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getRandomizer <em>Randomizer</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getChallengeNegotiatorDef()
 * @model
 * @generated
 */
public interface ChallengeNegotiatorDef extends NegotiatorDef
{
  /**
   * Returns the value of the '<em><b>User Manager</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User Manager</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User Manager</em>' reference.
   * @see #setUserManager(UserManagerDef)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getChallengeNegotiatorDef_UserManager()
   * @model
   * @generated
   */
  UserManagerDef getUserManager();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getUserManager <em>User Manager</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>User Manager</em>' reference.
   * @see #getUserManager()
   * @generated
   */
  void setUserManager(UserManagerDef value);

  /**
   * Returns the value of the '<em><b>Randomizer</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Randomizer</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Randomizer</em>' reference.
   * @see #setRandomizer(RandomizerDef)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getChallengeNegotiatorDef_Randomizer()
   * @model
   * @generated
   */
  RandomizerDef getRandomizer();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getRandomizer <em>Randomizer</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Randomizer</em>' reference.
   * @see #getRandomizer()
   * @generated
   */
  void setRandomizer(RandomizerDef value);

} // ChallengeNegotiatorDef
