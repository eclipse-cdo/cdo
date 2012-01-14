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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage
 * @generated
 */
public interface Net4jUtilDefsFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Net4jUtilDefsFactory eINSTANCE = org.eclipse.net4j.util.defs.impl.Net4jUtilDefsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Def Container</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Def Container</em>'.
   * @generated
   */
  DefContainer createDefContainer();

  /**
   * Returns a new object of class '<em>Thread Pool Def</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Thread Pool Def</em>'.
   * @generated
   */
  ThreadPoolDef createThreadPoolDef();

  /**
   * Returns a new object of class '<em>Randomizer Def</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Randomizer Def</em>'.
   * @generated
   */
  RandomizerDef createRandomizerDef();

  /**
   * Returns a new object of class '<em>User Manager Def</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>User Manager Def</em>'.
   * @generated
   */
  UserManagerDef createUserManagerDef();

  /**
   * Returns a new object of class '<em>User</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>User</em>'.
   * @generated
   */
  User createUser();

  /**
   * Returns a new object of class '<em>Password Credentials Provider Def</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Password Credentials Provider Def</em>'.
   * @generated
   */
  PasswordCredentialsProviderDef createPasswordCredentialsProviderDef();

  /**
   * Returns a new object of class '<em>Response Negotiator Def</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Response Negotiator Def</em>'.
   * @generated
   */
  ResponseNegotiatorDef createResponseNegotiatorDef();

  /**
   * Returns a new object of class '<em>Challenge Negotiator Def</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Challenge Negotiator Def</em>'.
   * @generated
   */
  ChallengeNegotiatorDef createChallengeNegotiatorDef();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Net4jUtilDefsPackage getNet4jUtilDefsPackage();

} // Net4jUtilDefsFactory
