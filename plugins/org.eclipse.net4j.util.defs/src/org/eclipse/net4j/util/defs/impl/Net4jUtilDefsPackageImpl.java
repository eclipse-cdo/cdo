/*
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
package org.eclipse.net4j.util.defs.impl;

import org.eclipse.net4j.util.defs.ChallengeNegotiatorDef;
import org.eclipse.net4j.util.defs.CredentialsProviderDef;
import org.eclipse.net4j.util.defs.Def;
import org.eclipse.net4j.util.defs.DefContainer;
import org.eclipse.net4j.util.defs.ExecutorServiceDef;
import org.eclipse.net4j.util.defs.NegotiatorDef;
import org.eclipse.net4j.util.defs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.util.defs.RandomizerDef;
import org.eclipse.net4j.util.defs.ResponseNegotiatorDef;
import org.eclipse.net4j.util.defs.ThreadPoolDef;
import org.eclipse.net4j.util.defs.User;
import org.eclipse.net4j.util.defs.UserManagerDef;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class Net4jUtilDefsPackageImpl extends EPackageImpl implements Net4jUtilDefsPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass defContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass defEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass executorServiceDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass threadPoolDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass randomizerDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass userManagerDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass userEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass passwordCredentialsProviderDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass credentialsProviderDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass negotiatorDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass responseNegotiatorDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass challengeNegotiatorDefEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private Net4jUtilDefsPackageImpl()
  {
    super(eNS_URI, Net4jUtilDefsFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link Net4jUtilDefsPackage#eINSTANCE} when that field is accessed. Clients
   * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Net4jUtilDefsPackage init()
  {
    if (isInited)
    {
      return (Net4jUtilDefsPackage)EPackage.Registry.INSTANCE.getEPackage(Net4jUtilDefsPackage.eNS_URI);
    }

    // Obtain or create and register package
    Net4jUtilDefsPackageImpl theNet4jUtilDefsPackage = (Net4jUtilDefsPackageImpl)(EPackage.Registry.INSTANCE
        .get(eNS_URI) instanceof Net4jUtilDefsPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
        : new Net4jUtilDefsPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theNet4jUtilDefsPackage.createPackageContents();

    // Initialize created meta-data
    theNet4jUtilDefsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theNet4jUtilDefsPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Net4jUtilDefsPackage.eNS_URI, theNet4jUtilDefsPackage);
    return theNet4jUtilDefsPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getDefContainer()
  {
    return defContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDefContainer_Definitions()
  {
    return (EReference)defContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDefContainer_DefaultDefinition()
  {
    return (EReference)defContainerEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getDef()
  {
    return defEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getExecutorServiceDef()
  {
    return executorServiceDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getThreadPoolDef()
  {
    return threadPoolDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getRandomizerDef()
  {
    return randomizerDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getRandomizerDef_AlgorithmName()
  {
    return (EAttribute)randomizerDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getRandomizerDef_ProviderName()
  {
    return (EAttribute)randomizerDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getRandomizerDef_Seed()
  {
    return (EAttribute)randomizerDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getUserManagerDef()
  {
    return userManagerDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getUserManagerDef_User()
  {
    return (EReference)userManagerDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getUser()
  {
    return userEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUser_UserID()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUser_Password()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getPasswordCredentialsProviderDef()
  {
    return passwordCredentialsProviderDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getPasswordCredentialsProviderDef_Password()
  {
    return (EAttribute)passwordCredentialsProviderDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCredentialsProviderDef()
  {
    return credentialsProviderDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCredentialsProviderDef_UserID()
  {
    return (EAttribute)credentialsProviderDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getNegotiatorDef()
  {
    return negotiatorDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getResponseNegotiatorDef()
  {
    return responseNegotiatorDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getResponseNegotiatorDef_CredentialsProvider()
  {
    return (EReference)responseNegotiatorDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getChallengeNegotiatorDef()
  {
    return challengeNegotiatorDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getChallengeNegotiatorDef_UserManager()
  {
    return (EReference)challengeNegotiatorDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getChallengeNegotiatorDef_Randomizer()
  {
    return (EReference)challengeNegotiatorDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jUtilDefsFactory getNet4jUtilDefsFactory()
  {
    return (Net4jUtilDefsFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    defContainerEClass = createEClass(DEF_CONTAINER);
    createEReference(defContainerEClass, DEF_CONTAINER__DEFINITIONS);
    createEReference(defContainerEClass, DEF_CONTAINER__DEFAULT_DEFINITION);

    defEClass = createEClass(DEF);

    executorServiceDefEClass = createEClass(EXECUTOR_SERVICE_DEF);

    threadPoolDefEClass = createEClass(THREAD_POOL_DEF);

    randomizerDefEClass = createEClass(RANDOMIZER_DEF);
    createEAttribute(randomizerDefEClass, RANDOMIZER_DEF__ALGORITHM_NAME);
    createEAttribute(randomizerDefEClass, RANDOMIZER_DEF__PROVIDER_NAME);
    createEAttribute(randomizerDefEClass, RANDOMIZER_DEF__SEED);

    userManagerDefEClass = createEClass(USER_MANAGER_DEF);
    createEReference(userManagerDefEClass, USER_MANAGER_DEF__USER);

    userEClass = createEClass(USER);
    createEAttribute(userEClass, USER__USER_ID);
    createEAttribute(userEClass, USER__PASSWORD);

    passwordCredentialsProviderDefEClass = createEClass(PASSWORD_CREDENTIALS_PROVIDER_DEF);
    createEAttribute(passwordCredentialsProviderDefEClass, PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD);

    credentialsProviderDefEClass = createEClass(CREDENTIALS_PROVIDER_DEF);
    createEAttribute(credentialsProviderDefEClass, CREDENTIALS_PROVIDER_DEF__USER_ID);

    negotiatorDefEClass = createEClass(NEGOTIATOR_DEF);

    responseNegotiatorDefEClass = createEClass(RESPONSE_NEGOTIATOR_DEF);
    createEReference(responseNegotiatorDefEClass, RESPONSE_NEGOTIATOR_DEF__CREDENTIALS_PROVIDER);

    challengeNegotiatorDefEClass = createEClass(CHALLENGE_NEGOTIATOR_DEF);
    createEReference(challengeNegotiatorDefEClass, CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER);
    createEReference(challengeNegotiatorDefEClass, CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    executorServiceDefEClass.getESuperTypes().add(getDef());
    threadPoolDefEClass.getESuperTypes().add(getExecutorServiceDef());
    randomizerDefEClass.getESuperTypes().add(getDef());
    userManagerDefEClass.getESuperTypes().add(getDef());
    passwordCredentialsProviderDefEClass.getESuperTypes().add(getCredentialsProviderDef());
    credentialsProviderDefEClass.getESuperTypes().add(getDef());
    negotiatorDefEClass.getESuperTypes().add(getDef());
    responseNegotiatorDefEClass.getESuperTypes().add(getNegotiatorDef());
    challengeNegotiatorDefEClass.getESuperTypes().add(getNegotiatorDef());

    // Initialize classes and features; add operations and parameters
    initEClass(defContainerEClass, DefContainer.class, "DefContainer", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDefContainer_Definitions(), getDef(), null, "definitions", null, 1, -1, DefContainer.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getDefContainer_DefaultDefinition(), getDef(), null, "defaultDefinition", null, 0, 1,
        DefContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(defEClass, Def.class, "Def", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(defEClass, ecorePackage.getEJavaObject(), "getInstance", 0, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(defEClass, null, "unsetInstance", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(executorServiceDefEClass, ExecutorServiceDef.class, "ExecutorServiceDef", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(threadPoolDefEClass, ThreadPoolDef.class, "ThreadPoolDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(randomizerDefEClass, RandomizerDef.class, "RandomizerDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRandomizerDef_AlgorithmName(), ecorePackage.getEString(), "algorithmName", null, 0, 1,
        RandomizerDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getRandomizerDef_ProviderName(), ecorePackage.getEString(), "providerName", null, 0, 1,
        RandomizerDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getRandomizerDef_Seed(), ecorePackage.getEByteArray(), "seed", null, 0, 1, RandomizerDef.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(userManagerDefEClass, UserManagerDef.class, "UserManagerDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getUserManagerDef_User(), getUser(), null, "user", null, 1, -1, UserManagerDef.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(userEClass, User.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUser_UserID(), ecorePackage.getEString(), "userID", null, 0, 1, User.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_Password(), ecorePackage.getEString(), "password", null, 0, 1, User.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(passwordCredentialsProviderDefEClass, PasswordCredentialsProviderDef.class,
        "PasswordCredentialsProviderDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPasswordCredentialsProviderDef_Password(), ecorePackage.getEString(), "password", null, 0, 1,
        PasswordCredentialsProviderDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(credentialsProviderDefEClass, CredentialsProviderDef.class, "CredentialsProviderDef", IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCredentialsProviderDef_UserID(), ecorePackage.getEString(), "userID", null, 0, 1,
        CredentialsProviderDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(negotiatorDefEClass, NegotiatorDef.class, "NegotiatorDef", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(responseNegotiatorDefEClass, ResponseNegotiatorDef.class, "ResponseNegotiatorDef", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getResponseNegotiatorDef_CredentialsProvider(), getCredentialsProviderDef(), null,
        "credentialsProvider", null, 0, 1, ResponseNegotiatorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(challengeNegotiatorDefEClass, ChallengeNegotiatorDef.class, "ChallengeNegotiatorDef", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChallengeNegotiatorDef_UserManager(), getUserManagerDef(), null, "userManager", null, 0, 1,
        ChallengeNegotiatorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChallengeNegotiatorDef_Randomizer(), getRandomizerDef(), null, "randomizer", null, 0, 1,
        ChallengeNegotiatorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // Net4jUtilDefsPackageImpl
