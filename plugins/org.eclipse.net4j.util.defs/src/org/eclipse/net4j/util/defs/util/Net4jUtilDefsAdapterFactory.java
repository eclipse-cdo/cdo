/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage
 * @generated
 */
public class Net4jUtilDefsAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static Net4jUtilDefsPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Net4jUtilDefsAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = Net4jUtilDefsPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Net4jUtilDefsSwitch<Adapter> modelSwitch = new Net4jUtilDefsSwitch<Adapter>()
  {
    @Override
    public Adapter caseDefContainer(DefContainer object)
    {
      return createDefContainerAdapter();
    }

    @Override
    public Adapter caseDef(Def object)
    {
      return createDefAdapter();
    }

    @Override
    public Adapter caseExecutorServiceDef(ExecutorServiceDef object)
    {
      return createExecutorServiceDefAdapter();
    }

    @Override
    public Adapter caseThreadPoolDef(ThreadPoolDef object)
    {
      return createThreadPoolDefAdapter();
    }

    @Override
    public Adapter caseRandomizerDef(RandomizerDef object)
    {
      return createRandomizerDefAdapter();
    }

    @Override
    public Adapter caseUserManagerDef(UserManagerDef object)
    {
      return createUserManagerDefAdapter();
    }

    @Override
    public Adapter caseUser(User object)
    {
      return createUserAdapter();
    }

    @Override
    public Adapter casePasswordCredentialsProviderDef(PasswordCredentialsProviderDef object)
    {
      return createPasswordCredentialsProviderDefAdapter();
    }

    @Override
    public Adapter caseCredentialsProviderDef(CredentialsProviderDef object)
    {
      return createCredentialsProviderDefAdapter();
    }

    @Override
    public Adapter caseNegotiatorDef(NegotiatorDef object)
    {
      return createNegotiatorDefAdapter();
    }

    @Override
    public Adapter caseResponseNegotiatorDef(ResponseNegotiatorDef object)
    {
      return createResponseNegotiatorDefAdapter();
    }

    @Override
    public Adapter caseChallengeNegotiatorDef(ChallengeNegotiatorDef object)
    {
      return createChallengeNegotiatorDefAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.DefContainer <em>Def Container</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.DefContainer
   * @generated
   */
  public Adapter createDefContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.Def <em>Def</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.Def
   * @generated
   */
  public Adapter createDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.ExecutorServiceDef <em>Executor Service Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.ExecutorServiceDef
   * @generated
   */
  public Adapter createExecutorServiceDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.ThreadPoolDef <em>Thread Pool Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.ThreadPoolDef
   * @generated
   */
  public Adapter createThreadPoolDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.RandomizerDef <em>Randomizer Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.RandomizerDef
   * @generated
   */
  public Adapter createRandomizerDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.UserManagerDef <em>User Manager Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.UserManagerDef
   * @generated
   */
  public Adapter createUserManagerDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.User <em>User</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.User
   * @generated
   */
  public Adapter createUserAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef <em>Password Credentials Provider Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef
   * @generated
   */
  public Adapter createPasswordCredentialsProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.CredentialsProviderDef <em>Credentials Provider Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.CredentialsProviderDef
   * @generated
   */
  public Adapter createCredentialsProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.NegotiatorDef <em>Negotiator Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.NegotiatorDef
   * @generated
   */
  public Adapter createNegotiatorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.ResponseNegotiatorDef <em>Response Negotiator Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.ResponseNegotiatorDef
   * @generated
   */
  public Adapter createResponseNegotiatorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.ChallengeNegotiatorDef <em>Challenge Negotiator Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.ChallengeNegotiatorDef
   * @generated
   */
  public Adapter createChallengeNegotiatorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // Net4jUtilDefsAdapterFactory
