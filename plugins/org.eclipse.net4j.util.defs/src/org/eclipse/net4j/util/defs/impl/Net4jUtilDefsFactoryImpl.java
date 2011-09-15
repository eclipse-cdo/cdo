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
import org.eclipse.net4j.util.defs.DefContainer;
import org.eclipse.net4j.util.defs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.util.defs.RandomizerDef;
import org.eclipse.net4j.util.defs.ResponseNegotiatorDef;
import org.eclipse.net4j.util.defs.ThreadPoolDef;
import org.eclipse.net4j.util.defs.User;
import org.eclipse.net4j.util.defs.UserManagerDef;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class Net4jUtilDefsFactoryImpl extends EFactoryImpl implements Net4jUtilDefsFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Net4jUtilDefsFactory init()
  {
    try
    {
      Net4jUtilDefsFactory theNet4jUtilDefsFactory = (Net4jUtilDefsFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/NET4J/util/defs/1.0.0");
      if (theNet4jUtilDefsFactory != null)
      {
        return theNet4jUtilDefsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Net4jUtilDefsFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jUtilDefsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER:
      return createDefContainer();
    case Net4jUtilDefsPackage.THREAD_POOL_DEF:
      return createThreadPoolDef();
    case Net4jUtilDefsPackage.RANDOMIZER_DEF:
      return createRandomizerDef();
    case Net4jUtilDefsPackage.USER_MANAGER_DEF:
      return createUserManagerDef();
    case Net4jUtilDefsPackage.USER:
      return createUser();
    case Net4jUtilDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF:
      return createPasswordCredentialsProviderDef();
    case Net4jUtilDefsPackage.RESPONSE_NEGOTIATOR_DEF:
      return createResponseNegotiatorDef();
    case Net4jUtilDefsPackage.CHALLENGE_NEGOTIATOR_DEF:
      return createChallengeNegotiatorDef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DefContainer createDefContainer()
  {
    DefContainerImpl defContainer = new DefContainerImpl();
    return defContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ThreadPoolDef createThreadPoolDef()
  {
    ThreadPoolDefImpl threadPoolDef = new ThreadPoolDefImpl();
    return threadPoolDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RandomizerDef createRandomizerDef()
  {
    RandomizerDefImpl randomizerDef = new RandomizerDefImpl();
    return randomizerDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public UserManagerDef createUserManagerDef()
  {
    UserManagerDefImpl userManagerDef = new UserManagerDefImpl();
    return userManagerDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public User createUser()
  {
    UserImpl user = new UserImpl();
    return user;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public PasswordCredentialsProviderDef createPasswordCredentialsProviderDef()
  {
    PasswordCredentialsProviderDefImpl passwordCredentialsProviderDef = new PasswordCredentialsProviderDefImpl();
    return passwordCredentialsProviderDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ResponseNegotiatorDef createResponseNegotiatorDef()
  {
    ResponseNegotiatorDefImpl responseNegotiatorDef = new ResponseNegotiatorDefImpl();
    return responseNegotiatorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ChallengeNegotiatorDef createChallengeNegotiatorDef()
  {
    ChallengeNegotiatorDefImpl challengeNegotiatorDef = new ChallengeNegotiatorDefImpl();
    return challengeNegotiatorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jUtilDefsPackage getNet4jUtilDefsPackage()
  {
    return (Net4jUtilDefsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Net4jUtilDefsPackage getPackage()
  {
    return Net4jUtilDefsPackage.eINSTANCE;
  }

} // Net4jUtilDefsFactoryImpl
