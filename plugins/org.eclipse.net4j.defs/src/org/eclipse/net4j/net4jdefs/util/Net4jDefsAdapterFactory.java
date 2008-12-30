/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jDefsAdapterFactory.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.util;

import org.eclipse.net4j.net4jdefs.AcceptorDef;
import org.eclipse.net4j.net4jdefs.BufferPoolDef;
import org.eclipse.net4j.net4jdefs.BufferProviderDef;
import org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef;
import org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.ConnectorDef;
import org.eclipse.net4j.net4jdefs.CredentialsProviderDef;
import org.eclipse.net4j.net4jdefs.HTTPConnectorDef;
import org.eclipse.net4j.net4jdefs.JVMAcceptorDef;
import org.eclipse.net4j.net4jdefs.JVMConnectorDef;
import org.eclipse.net4j.net4jdefs.NegotiatorDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.net4jdefs.ProtocolProviderDef;
import org.eclipse.net4j.net4jdefs.RandomizerDef;
import org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef;
import org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.TCPAcceptorDef;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.net4jdefs.TCPSelectorDef;
import org.eclipse.net4j.net4jdefs.User;
import org.eclipse.net4j.net4jdefs.UserManagerDef;
import org.eclipse.net4j.util.net4jutildefs.Def;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage
 * @generated
 */
public class Net4jDefsAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static Net4jDefsPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jDefsAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = Net4jDefsPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   * 
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
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Net4jDefsSwitch<Adapter> modelSwitch = new Net4jDefsSwitch<Adapter>()
  {
    @Override
    public Adapter caseConnectorDef(ConnectorDef object)
    {
      return createConnectorDefAdapter();
    }

    @Override
    public Adapter caseClientProtocolFactoryDef(ClientProtocolFactoryDef object)
    {
      return createClientProtocolFactoryDefAdapter();
    }

    @Override
    public Adapter caseTCPConnectorDef(TCPConnectorDef object)
    {
      return createTCPConnectorDefAdapter();
    }

    @Override
    public Adapter caseAcceptorDef(AcceptorDef object)
    {
      return createAcceptorDefAdapter();
    }

    @Override
    public Adapter caseTCPAcceptorDef(TCPAcceptorDef object)
    {
      return createTCPAcceptorDefAdapter();
    }

    @Override
    public Adapter caseJVMAcceptorDef(JVMAcceptorDef object)
    {
      return createJVMAcceptorDefAdapter();
    }

    @Override
    public Adapter caseJVMConnectorDef(JVMConnectorDef object)
    {
      return createJVMConnectorDefAdapter();
    }

    @Override
    public Adapter caseHTTPConnectorDef(HTTPConnectorDef object)
    {
      return createHTTPConnectorDefAdapter();
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
    public Adapter caseTCPSelectorDef(TCPSelectorDef object)
    {
      return createTCPSelectorDefAdapter();
    }

    @Override
    public Adapter caseServerProtocolFactoryDef(ServerProtocolFactoryDef object)
    {
      return createServerProtocolFactoryDefAdapter();
    }

    @Override
    public Adapter caseBufferProviderDef(BufferProviderDef object)
    {
      return createBufferProviderDefAdapter();
    }

    @Override
    public Adapter caseBufferPoolDef(BufferPoolDef object)
    {
      return createBufferPoolDefAdapter();
    }

    @Override
    public Adapter caseProtocolProviderDef(ProtocolProviderDef object)
    {
      return createProtocolProviderDefAdapter();
    }

    @Override
    public Adapter caseCredentialsProviderDef(CredentialsProviderDef object)
    {
      return createCredentialsProviderDefAdapter();
    }

    @Override
    public Adapter casePasswordCredentialsProviderDef(PasswordCredentialsProviderDef object)
    {
      return createPasswordCredentialsProviderDefAdapter();
    }

    @Override
    public Adapter caseUser(User object)
    {
      return createUserAdapter();
    }

    @Override
    public Adapter caseUserManagerDef(UserManagerDef object)
    {
      return createUserManagerDefAdapter();
    }

    @Override
    public Adapter caseRandomizerDef(RandomizerDef object)
    {
      return createRandomizerDefAdapter();
    }

    @Override
    public Adapter caseDef(Def object)
    {
      return createDefAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ConnectorDef
   * <em>Connector Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.ConnectorDef
   * @generated
   */
  public Adapter createConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef
   * <em>Client Protocol Factory Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef
   * @generated
   */
  public Adapter createClientProtocolFactoryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.TCPConnectorDef
   * <em>TCP Connector Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.TCPConnectorDef
   * @generated
   */
  public Adapter createTCPConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.AcceptorDef <em>Acceptor Def</em>}
   * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
   * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.AcceptorDef
   * @generated
   */
  public Adapter createAcceptorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.TCPAcceptorDef
   * <em>TCP Acceptor Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.TCPAcceptorDef
   * @generated
   */
  public Adapter createTCPAcceptorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.JVMAcceptorDef
   * <em>JVM Acceptor Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.JVMAcceptorDef
   * @generated
   */
  public Adapter createJVMAcceptorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.JVMConnectorDef
   * <em>JVM Connector Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.JVMConnectorDef
   * @generated
   */
  public Adapter createJVMConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.HTTPConnectorDef
   * <em>HTTP Connector Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.HTTPConnectorDef
   * @generated
   */
  public Adapter createHTTPConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.NegotiatorDef
   * <em>Negotiator Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.NegotiatorDef
   * @generated
   */
  public Adapter createNegotiatorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef
   * <em>Response Negotiator Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef
   * @generated
   */
  public Adapter createResponseNegotiatorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef
   * <em>Challenge Negotiator Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef
   * @generated
   */
  public Adapter createChallengeNegotiatorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.TCPSelectorDef
   * <em>TCP Selector Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.TCPSelectorDef
   * @generated
   */
  public Adapter createTCPSelectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef
   * <em>Server Protocol Factory Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef
   * @generated
   */
  public Adapter createServerProtocolFactoryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.BufferProviderDef
   * <em>Buffer Provider Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.BufferProviderDef
   * @generated
   */
  public Adapter createBufferProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.BufferPoolDef
   * <em>Buffer Pool Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.BufferPoolDef
   * @generated
   */
  public Adapter createBufferPoolDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ProtocolProviderDef
   * <em>Protocol Provider Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.ProtocolProviderDef
   * @generated
   */
  public Adapter createProtocolProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.CredentialsProviderDef
   * <em>Credentials Provider Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.CredentialsProviderDef
   * @generated
   */
  public Adapter createCredentialsProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef
   * <em>Password Credentials Provider Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef
   * @generated
   */
  public Adapter createPasswordCredentialsProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.UserManagerDef
   * <em>User Manager Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.UserManagerDef
   * @generated
   */
  public Adapter createUserManagerDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.RandomizerDef
   * <em>Randomizer Def</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.RandomizerDef
   * @generated
   */
  public Adapter createRandomizerDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.User <em>User</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.net4jdefs.User
   * @generated
   */
  public Adapter createUserAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.net4jutildefs.Def <em>Def</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.net4j.util.net4jutildefs.Def
   * @generated
   */
  public Adapter createDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // Net4jDefsAdapterFactory
