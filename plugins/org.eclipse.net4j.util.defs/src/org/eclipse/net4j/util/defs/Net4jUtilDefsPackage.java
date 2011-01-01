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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsFactory
 * @model kind="package"
 * @generated
 */
public interface Net4jUtilDefsPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "defs"; //$NON-NLS-1$

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/NET4J/util/defs/1.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "net4j.util.defs"; //$NON-NLS-1$

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Net4jUtilDefsPackage eINSTANCE = org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.DefContainerImpl <em>Def Container</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.DefContainerImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getDefContainer()
   * @generated
   */
  int DEF_CONTAINER = 0;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEF_CONTAINER__DEFINITIONS = 0;

  /**
   * The feature id for the '<em><b>Default Definition</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int DEF_CONTAINER__DEFAULT_DEFINITION = 1;

  /**
   * The number of structural features of the '<em>Def Container</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int DEF_CONTAINER_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.DefImpl <em>Def</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.DefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getDef()
   * @generated
   */
  int DEF = 1;

  /**
   * The number of structural features of the '<em>Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEF_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.ExecutorServiceDefImpl
   * <em>Executor Service Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.ExecutorServiceDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getExecutorServiceDef()
   * @generated
   */
  int EXECUTOR_SERVICE_DEF = 2;

  /**
   * The number of structural features of the '<em>Executor Service Def</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int EXECUTOR_SERVICE_DEF_FEATURE_COUNT = DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.ThreadPoolDefImpl <em>Thread Pool Def</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.ThreadPoolDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getThreadPoolDef()
   * @generated
   */
  int THREAD_POOL_DEF = 3;

  /**
   * The number of structural features of the '<em>Thread Pool Def</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int THREAD_POOL_DEF_FEATURE_COUNT = EXECUTOR_SERVICE_DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.RandomizerDefImpl <em>Randomizer Def</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.RandomizerDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getRandomizerDef()
   * @generated
   */
  int RANDOMIZER_DEF = 4;

  /**
   * The feature id for the '<em><b>Algorithm Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int RANDOMIZER_DEF__ALGORITHM_NAME = DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Provider Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int RANDOMIZER_DEF__PROVIDER_NAME = DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Seed</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int RANDOMIZER_DEF__SEED = DEF_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Randomizer Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int RANDOMIZER_DEF_FEATURE_COUNT = DEF_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.UserManagerDefImpl <em>User Manager Def</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.UserManagerDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getUserManagerDef()
   * @generated
   */
  int USER_MANAGER_DEF = 5;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.UserImpl <em>User</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.UserImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getUser()
   * @generated
   */
  int USER = 6;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.CredentialsProviderDefImpl
   * <em>Credentials Provider Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.CredentialsProviderDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getCredentialsProviderDef()
   * @generated
   */
  int CREDENTIALS_PROVIDER_DEF = 8;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.PasswordCredentialsProviderDefImpl
   * <em>Password Credentials Provider Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.PasswordCredentialsProviderDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getPasswordCredentialsProviderDef()
   * @generated
   */
  int PASSWORD_CREDENTIALS_PROVIDER_DEF = 7;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.NegotiatorDefImpl <em>Negotiator Def</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.NegotiatorDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getNegotiatorDef()
   * @generated
   */
  int NEGOTIATOR_DEF = 9;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.ResponseNegotiatorDefImpl
   * <em>Response Negotiator Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.ResponseNegotiatorDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getResponseNegotiatorDef()
   * @generated
   */
  int RESPONSE_NEGOTIATOR_DEF = 10;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.util.defs.impl.ChallengeNegotiatorDefImpl
   * <em>Challenge Negotiator Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.net4j.util.defs.impl.ChallengeNegotiatorDefImpl
   * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getChallengeNegotiatorDef()
   * @generated
   */
  int CHALLENGE_NEGOTIATOR_DEF = 11;

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.DefContainer <em>Def Container</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Def Container</em>'.
   * @see org.eclipse.net4j.util.defs.DefContainer
   * @generated
   */
  EClass getDefContainer();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.net4j.util.defs.DefContainer#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Definitions</em>'.
   * @see org.eclipse.net4j.util.defs.DefContainer#getDefinitions()
   * @see #getDefContainer()
   * @generated
   */
  EReference getDefContainer_Definitions();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.util.defs.DefContainer#getDefaultDefinition
   * <em>Default Definition</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Default Definition</em>'.
   * @see org.eclipse.net4j.util.defs.DefContainer#getDefaultDefinition()
   * @see #getDefContainer()
   * @generated
   */
  EReference getDefContainer_DefaultDefinition();

  /**
   * The feature id for the '<em><b>User</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int USER_MANAGER_DEF__USER = DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>User Manager Def</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int USER_MANAGER_DEF_FEATURE_COUNT = DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int USER__USER_ID = 0;

  /**
   * The feature id for the '<em><b>Password</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int USER__PASSWORD = 1;

  /**
   * The number of structural features of the '<em>User</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int USER_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CREDENTIALS_PROVIDER_DEF__USER_ID = DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Credentials Provider Def</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CREDENTIALS_PROVIDER_DEF_FEATURE_COUNT = DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PASSWORD_CREDENTIALS_PROVIDER_DEF__USER_ID = CREDENTIALS_PROVIDER_DEF__USER_ID;

  /**
   * The feature id for the '<em><b>Password</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD = CREDENTIALS_PROVIDER_DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Password Credentials Provider Def</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PASSWORD_CREDENTIALS_PROVIDER_DEF_FEATURE_COUNT = CREDENTIALS_PROVIDER_DEF_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Negotiator Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NEGOTIATOR_DEF_FEATURE_COUNT = DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Credentials Provider</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int RESPONSE_NEGOTIATOR_DEF__CREDENTIALS_PROVIDER = NEGOTIATOR_DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Response Negotiator Def</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int RESPONSE_NEGOTIATOR_DEF_FEATURE_COUNT = NEGOTIATOR_DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>User Manager</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER = NEGOTIATOR_DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Randomizer</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER = NEGOTIATOR_DEF_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Challenge Negotiator Def</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHALLENGE_NEGOTIATOR_DEF_FEATURE_COUNT = NEGOTIATOR_DEF_FEATURE_COUNT + 2;

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.Def <em>Def</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Def</em>'.
   * @see org.eclipse.net4j.util.defs.Def
   * @generated
   */
  EClass getDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.ExecutorServiceDef
   * <em>Executor Service Def</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Executor Service Def</em>'.
   * @see org.eclipse.net4j.util.defs.ExecutorServiceDef
   * @generated
   */
  EClass getExecutorServiceDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.ThreadPoolDef <em>Thread Pool Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Thread Pool Def</em>'.
   * @see org.eclipse.net4j.util.defs.ThreadPoolDef
   * @generated
   */
  EClass getThreadPoolDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.RandomizerDef <em>Randomizer Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Randomizer Def</em>'.
   * @see org.eclipse.net4j.util.defs.RandomizerDef
   * @generated
   */
  EClass getRandomizerDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.defs.RandomizerDef#getAlgorithmName
   * <em>Algorithm Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Algorithm Name</em>'.
   * @see org.eclipse.net4j.util.defs.RandomizerDef#getAlgorithmName()
   * @see #getRandomizerDef()
   * @generated
   */
  EAttribute getRandomizerDef_AlgorithmName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.defs.RandomizerDef#getProviderName
   * <em>Provider Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Provider Name</em>'.
   * @see org.eclipse.net4j.util.defs.RandomizerDef#getProviderName()
   * @see #getRandomizerDef()
   * @generated
   */
  EAttribute getRandomizerDef_ProviderName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.defs.RandomizerDef#getSeed <em>Seed</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Seed</em>'.
   * @see org.eclipse.net4j.util.defs.RandomizerDef#getSeed()
   * @see #getRandomizerDef()
   * @generated
   */
  EAttribute getRandomizerDef_Seed();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.UserManagerDef <em>User Manager Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>User Manager Def</em>'.
   * @see org.eclipse.net4j.util.defs.UserManagerDef
   * @generated
   */
  EClass getUserManagerDef();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.net4j.util.defs.UserManagerDef#getUser
   * <em>User</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>User</em>'.
   * @see org.eclipse.net4j.util.defs.UserManagerDef#getUser()
   * @see #getUserManagerDef()
   * @generated
   */
  EReference getUserManagerDef_User();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.User <em>User</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>User</em>'.
   * @see org.eclipse.net4j.util.defs.User
   * @generated
   */
  EClass getUser();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.defs.User#getUserID <em>User ID</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>User ID</em>'.
   * @see org.eclipse.net4j.util.defs.User#getUserID()
   * @see #getUser()
   * @generated
   */
  EAttribute getUser_UserID();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.defs.User#getPassword <em>Password</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Password</em>'.
   * @see org.eclipse.net4j.util.defs.User#getPassword()
   * @see #getUser()
   * @generated
   */
  EAttribute getUser_Password();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef
   * <em>Password Credentials Provider Def</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Password Credentials Provider Def</em>'.
   * @see org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef
   * @generated
   */
  EClass getPasswordCredentialsProviderDef();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef#getPassword <em>Password</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Password</em>'.
   * @see org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef#getPassword()
   * @see #getPasswordCredentialsProviderDef()
   * @generated
   */
  EAttribute getPasswordCredentialsProviderDef_Password();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.CredentialsProviderDef
   * <em>Credentials Provider Def</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Credentials Provider Def</em>'.
   * @see org.eclipse.net4j.util.defs.CredentialsProviderDef
   * @generated
   */
  EClass getCredentialsProviderDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.util.defs.CredentialsProviderDef#getUserID
   * <em>User ID</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>User ID</em>'.
   * @see org.eclipse.net4j.util.defs.CredentialsProviderDef#getUserID()
   * @see #getCredentialsProviderDef()
   * @generated
   */
  EAttribute getCredentialsProviderDef_UserID();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.NegotiatorDef <em>Negotiator Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Negotiator Def</em>'.
   * @see org.eclipse.net4j.util.defs.NegotiatorDef
   * @generated
   */
  EClass getNegotiatorDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.ResponseNegotiatorDef
   * <em>Response Negotiator Def</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Response Negotiator Def</em>'.
   * @see org.eclipse.net4j.util.defs.ResponseNegotiatorDef
   * @generated
   */
  EClass getResponseNegotiatorDef();

  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.net4j.util.defs.ResponseNegotiatorDef#getCredentialsProvider <em>Credentials Provider</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Credentials Provider</em>'.
   * @see org.eclipse.net4j.util.defs.ResponseNegotiatorDef#getCredentialsProvider()
   * @see #getResponseNegotiatorDef()
   * @generated
   */
  EReference getResponseNegotiatorDef_CredentialsProvider();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef
   * <em>Challenge Negotiator Def</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Challenge Negotiator Def</em>'.
   * @see org.eclipse.net4j.util.defs.ChallengeNegotiatorDef
   * @generated
   */
  EClass getChallengeNegotiatorDef();

  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getUserManager <em>User Manager</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>User Manager</em>'.
   * @see org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getUserManager()
   * @see #getChallengeNegotiatorDef()
   * @generated
   */
  EReference getChallengeNegotiatorDef_UserManager();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getRandomizer
   * <em>Randomizer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Randomizer</em>'.
   * @see org.eclipse.net4j.util.defs.ChallengeNegotiatorDef#getRandomizer()
   * @see #getChallengeNegotiatorDef()
   * @generated
   */
  EReference getChallengeNegotiatorDef_Randomizer();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Net4jUtilDefsFactory getNet4jUtilDefsFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.DefContainerImpl <em>Def Container</em>}
     * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.DefContainerImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getDefContainer()
     * @generated
     */
    EClass DEF_CONTAINER = eINSTANCE.getDefContainer();

    /**
     * The meta object literal for the '<em><b>Definitions</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference DEF_CONTAINER__DEFINITIONS = eINSTANCE.getDefContainer_Definitions();

    /**
     * The meta object literal for the '<em><b>Default Definition</b></em>' reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference DEF_CONTAINER__DEFAULT_DEFINITION = eINSTANCE.getDefContainer_DefaultDefinition();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.DefImpl <em>Def</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.DefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getDef()
     * @generated
     */
    EClass DEF = eINSTANCE.getDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.ExecutorServiceDefImpl
     * <em>Executor Service Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.ExecutorServiceDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getExecutorServiceDef()
     * @generated
     */
    EClass EXECUTOR_SERVICE_DEF = eINSTANCE.getExecutorServiceDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.ThreadPoolDefImpl
     * <em>Thread Pool Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.ThreadPoolDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getThreadPoolDef()
     * @generated
     */
    EClass THREAD_POOL_DEF = eINSTANCE.getThreadPoolDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.RandomizerDefImpl
     * <em>Randomizer Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.RandomizerDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getRandomizerDef()
     * @generated
     */
    EClass RANDOMIZER_DEF = eINSTANCE.getRandomizerDef();

    /**
     * The meta object literal for the '<em><b>Algorithm Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute RANDOMIZER_DEF__ALGORITHM_NAME = eINSTANCE.getRandomizerDef_AlgorithmName();

    /**
     * The meta object literal for the '<em><b>Provider Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute RANDOMIZER_DEF__PROVIDER_NAME = eINSTANCE.getRandomizerDef_ProviderName();

    /**
     * The meta object literal for the '<em><b>Seed</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute RANDOMIZER_DEF__SEED = eINSTANCE.getRandomizerDef_Seed();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.UserManagerDefImpl
     * <em>User Manager Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.UserManagerDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getUserManagerDef()
     * @generated
     */
    EClass USER_MANAGER_DEF = eINSTANCE.getUserManagerDef();

    /**
     * The meta object literal for the '<em><b>User</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference USER_MANAGER_DEF__USER = eINSTANCE.getUserManagerDef_User();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.UserImpl <em>User</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.UserImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getUser()
     * @generated
     */
    EClass USER = eINSTANCE.getUser();

    /**
     * The meta object literal for the '<em><b>User ID</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute USER__USER_ID = eINSTANCE.getUser_UserID();

    /**
     * The meta object literal for the '<em><b>Password</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute USER__PASSWORD = eINSTANCE.getUser_Password();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.PasswordCredentialsProviderDefImpl
     * <em>Password Credentials Provider Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.PasswordCredentialsProviderDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getPasswordCredentialsProviderDef()
     * @generated
     */
    EClass PASSWORD_CREDENTIALS_PROVIDER_DEF = eINSTANCE.getPasswordCredentialsProviderDef();

    /**
     * The meta object literal for the '<em><b>Password</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD = eINSTANCE.getPasswordCredentialsProviderDef_Password();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.CredentialsProviderDefImpl
     * <em>Credentials Provider Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.CredentialsProviderDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getCredentialsProviderDef()
     * @generated
     */
    EClass CREDENTIALS_PROVIDER_DEF = eINSTANCE.getCredentialsProviderDef();

    /**
     * The meta object literal for the '<em><b>User ID</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CREDENTIALS_PROVIDER_DEF__USER_ID = eINSTANCE.getCredentialsProviderDef_UserID();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.NegotiatorDefImpl
     * <em>Negotiator Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.NegotiatorDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getNegotiatorDef()
     * @generated
     */
    EClass NEGOTIATOR_DEF = eINSTANCE.getNegotiatorDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.ResponseNegotiatorDefImpl
     * <em>Response Negotiator Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.ResponseNegotiatorDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getResponseNegotiatorDef()
     * @generated
     */
    EClass RESPONSE_NEGOTIATOR_DEF = eINSTANCE.getResponseNegotiatorDef();

    /**
     * The meta object literal for the '<em><b>Credentials Provider</b></em>' reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference RESPONSE_NEGOTIATOR_DEF__CREDENTIALS_PROVIDER = eINSTANCE.getResponseNegotiatorDef_CredentialsProvider();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.util.defs.impl.ChallengeNegotiatorDefImpl
     * <em>Challenge Negotiator Def</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.net4j.util.defs.impl.ChallengeNegotiatorDefImpl
     * @see org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl#getChallengeNegotiatorDef()
     * @generated
     */
    EClass CHALLENGE_NEGOTIATOR_DEF = eINSTANCE.getChallengeNegotiatorDef();

    /**
     * The meta object literal for the '<em><b>User Manager</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER = eINSTANCE.getChallengeNegotiatorDef_UserManager();

    /**
     * The meta object literal for the '<em><b>Randomizer</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER = eINSTANCE.getChallengeNegotiatorDef_Randomizer();

  }

} // Net4jUtilDefsPackage
