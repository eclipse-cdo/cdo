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
package org.eclipse.net4j.util.defs.util;

import org.eclipse.net4j.util.defs.ChallengeNegotiatorDef;
import org.eclipse.net4j.util.defs.CredentialsProviderDef;
import org.eclipse.net4j.util.defs.Def;
import org.eclipse.net4j.util.defs.DefContainer;
import org.eclipse.net4j.util.defs.ExecutorServiceDef;
import org.eclipse.net4j.util.defs.NegotiatorDef;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.util.defs.RandomizerDef;
import org.eclipse.net4j.util.defs.ResponseNegotiatorDef;
import org.eclipse.net4j.util.defs.ThreadPoolDef;
import org.eclipse.net4j.util.defs.User;
import org.eclipse.net4j.util.defs.UserManagerDef;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage
 * @generated
 */
public class Net4jUtilDefsSwitch<T>
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static Net4jUtilDefsPackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jUtilDefsSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = Net4jUtilDefsPackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER:
    {
      DefContainer defContainer = (DefContainer)theEObject;
      T result = caseDefContainer(defContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.DEF:
    {
      Def def = (Def)theEObject;
      T result = caseDef(def);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.EXECUTOR_SERVICE_DEF:
    {
      ExecutorServiceDef executorServiceDef = (ExecutorServiceDef)theEObject;
      T result = caseExecutorServiceDef(executorServiceDef);
      if (result == null)
      {
        result = caseDef(executorServiceDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.THREAD_POOL_DEF:
    {
      ThreadPoolDef threadPoolDef = (ThreadPoolDef)theEObject;
      T result = caseThreadPoolDef(threadPoolDef);
      if (result == null)
      {
        result = caseExecutorServiceDef(threadPoolDef);
      }
      if (result == null)
      {
        result = caseDef(threadPoolDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.RANDOMIZER_DEF:
    {
      RandomizerDef randomizerDef = (RandomizerDef)theEObject;
      T result = caseRandomizerDef(randomizerDef);
      if (result == null)
      {
        result = caseDef(randomizerDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.USER_MANAGER_DEF:
    {
      UserManagerDef userManagerDef = (UserManagerDef)theEObject;
      T result = caseUserManagerDef(userManagerDef);
      if (result == null)
      {
        result = caseDef(userManagerDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.USER:
    {
      User user = (User)theEObject;
      T result = caseUser(user);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF:
    {
      PasswordCredentialsProviderDef passwordCredentialsProviderDef = (PasswordCredentialsProviderDef)theEObject;
      T result = casePasswordCredentialsProviderDef(passwordCredentialsProviderDef);
      if (result == null)
      {
        result = caseCredentialsProviderDef(passwordCredentialsProviderDef);
      }
      if (result == null)
      {
        result = caseDef(passwordCredentialsProviderDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.CREDENTIALS_PROVIDER_DEF:
    {
      CredentialsProviderDef credentialsProviderDef = (CredentialsProviderDef)theEObject;
      T result = caseCredentialsProviderDef(credentialsProviderDef);
      if (result == null)
      {
        result = caseDef(credentialsProviderDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.NEGOTIATOR_DEF:
    {
      NegotiatorDef negotiatorDef = (NegotiatorDef)theEObject;
      T result = caseNegotiatorDef(negotiatorDef);
      if (result == null)
      {
        result = caseDef(negotiatorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.RESPONSE_NEGOTIATOR_DEF:
    {
      ResponseNegotiatorDef responseNegotiatorDef = (ResponseNegotiatorDef)theEObject;
      T result = caseResponseNegotiatorDef(responseNegotiatorDef);
      if (result == null)
      {
        result = caseNegotiatorDef(responseNegotiatorDef);
      }
      if (result == null)
      {
        result = caseDef(responseNegotiatorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jUtilDefsPackage.CHALLENGE_NEGOTIATOR_DEF:
    {
      ChallengeNegotiatorDef challengeNegotiatorDef = (ChallengeNegotiatorDef)theEObject;
      T result = caseChallengeNegotiatorDef(challengeNegotiatorDef);
      if (result == null)
      {
        result = caseNegotiatorDef(challengeNegotiatorDef);
      }
      if (result == null)
      {
        result = caseDef(challengeNegotiatorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Def Container</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Def Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDefContainer(DefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Def</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDef(Def object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Executor Service Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Executor Service Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExecutorServiceDef(ExecutorServiceDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Thread Pool Def</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Thread Pool Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseThreadPoolDef(ThreadPoolDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Randomizer Def</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Randomizer Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRandomizerDef(RandomizerDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>User Manager Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>User Manager Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUserManagerDef(UserManagerDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>User</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>User</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUser(User object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Password Credentials Provider Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Password Credentials Provider Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePasswordCredentialsProviderDef(PasswordCredentialsProviderDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Credentials Provider Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Credentials Provider Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCredentialsProviderDef(CredentialsProviderDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Negotiator Def</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Negotiator Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNegotiatorDef(NegotiatorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Response Negotiator Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Response Negotiator Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseResponseNegotiatorDef(ResponseNegotiatorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Challenge Negotiator Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Challenge Negotiator Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChallengeNegotiatorDef(ChallengeNegotiatorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // Net4jUtilDefsSwitch
