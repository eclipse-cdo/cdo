/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jDefsSwitch.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage
 * @generated
 */
public class Net4jDefsSwitch<T>
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static Net4jDefsPackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jDefsSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = Net4jDefsPackage.eINSTANCE;
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
    case Net4jDefsPackage.CONNECTOR_DEF:
    {
      ConnectorDef connectorDef = (ConnectorDef)theEObject;
      T result = caseConnectorDef(connectorDef);
      if (result == null)
      {
        result = caseDef(connectorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.CLIENT_PROTOCOL_FACTORY_DEF:
    {
      ClientProtocolFactoryDef clientProtocolFactoryDef = (ClientProtocolFactoryDef)theEObject;
      T result = caseClientProtocolFactoryDef(clientProtocolFactoryDef);
      if (result == null)
      {
        result = caseProtocolProviderDef(clientProtocolFactoryDef);
      }
      if (result == null)
      {
        result = caseDef(clientProtocolFactoryDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.TCP_CONNECTOR_DEF:
    {
      TCPConnectorDef tcpConnectorDef = (TCPConnectorDef)theEObject;
      T result = caseTCPConnectorDef(tcpConnectorDef);
      if (result == null)
      {
        result = caseConnectorDef(tcpConnectorDef);
      }
      if (result == null)
      {
        result = caseDef(tcpConnectorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.ACCEPTOR_DEF:
    {
      AcceptorDef acceptorDef = (AcceptorDef)theEObject;
      T result = caseAcceptorDef(acceptorDef);
      if (result == null)
      {
        result = caseDef(acceptorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF:
    {
      TCPAcceptorDef tcpAcceptorDef = (TCPAcceptorDef)theEObject;
      T result = caseTCPAcceptorDef(tcpAcceptorDef);
      if (result == null)
      {
        result = caseAcceptorDef(tcpAcceptorDef);
      }
      if (result == null)
      {
        result = caseDef(tcpAcceptorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.JVM_ACCEPTOR_DEF:
    {
      JVMAcceptorDef jvmAcceptorDef = (JVMAcceptorDef)theEObject;
      T result = caseJVMAcceptorDef(jvmAcceptorDef);
      if (result == null)
      {
        result = caseAcceptorDef(jvmAcceptorDef);
      }
      if (result == null)
      {
        result = caseDef(jvmAcceptorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.JVM_CONNECTOR_DEF:
    {
      JVMConnectorDef jvmConnectorDef = (JVMConnectorDef)theEObject;
      T result = caseJVMConnectorDef(jvmConnectorDef);
      if (result == null)
      {
        result = caseConnectorDef(jvmConnectorDef);
      }
      if (result == null)
      {
        result = caseDef(jvmConnectorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.HTTP_CONNECTOR_DEF:
    {
      HTTPConnectorDef httpConnectorDef = (HTTPConnectorDef)theEObject;
      T result = caseHTTPConnectorDef(httpConnectorDef);
      if (result == null)
      {
        result = caseConnectorDef(httpConnectorDef);
      }
      if (result == null)
      {
        result = caseDef(httpConnectorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.NEGOTIATOR_DEF:
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
    case Net4jDefsPackage.RESPONSE_NEGOTIATOR_DEF:
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
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF:
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
    case Net4jDefsPackage.TCP_SELECTOR_DEF:
    {
      TCPSelectorDef tcpSelectorDef = (TCPSelectorDef)theEObject;
      T result = caseTCPSelectorDef(tcpSelectorDef);
      if (result == null)
      {
        result = caseDef(tcpSelectorDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.SERVER_PROTOCOL_FACTORY_DEF:
    {
      ServerProtocolFactoryDef serverProtocolFactoryDef = (ServerProtocolFactoryDef)theEObject;
      T result = caseServerProtocolFactoryDef(serverProtocolFactoryDef);
      if (result == null)
      {
        result = caseProtocolProviderDef(serverProtocolFactoryDef);
      }
      if (result == null)
      {
        result = caseDef(serverProtocolFactoryDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.BUFFER_PROVIDER_DEF:
    {
      BufferProviderDef bufferProviderDef = (BufferProviderDef)theEObject;
      T result = caseBufferProviderDef(bufferProviderDef);
      if (result == null)
      {
        result = caseDef(bufferProviderDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.BUFFER_POOL_DEF:
    {
      BufferPoolDef bufferPoolDef = (BufferPoolDef)theEObject;
      T result = caseBufferPoolDef(bufferPoolDef);
      if (result == null)
      {
        result = caseBufferProviderDef(bufferPoolDef);
      }
      if (result == null)
      {
        result = caseDef(bufferPoolDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.PROTOCOL_PROVIDER_DEF:
    {
      ProtocolProviderDef protocolProviderDef = (ProtocolProviderDef)theEObject;
      T result = caseProtocolProviderDef(protocolProviderDef);
      if (result == null)
      {
        result = caseDef(protocolProviderDef);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.CREDENTIALS_PROVIDER_DEF:
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
    case Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF:
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
    case Net4jDefsPackage.USER:
    {
      User user = (User)theEObject;
      T result = caseUser(user);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Net4jDefsPackage.USER_MANAGER_DEF:
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
    case Net4jDefsPackage.RANDOMIZER_DEF:
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
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Connector Def</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Connector Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConnectorDef(ConnectorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Client Protocol Factory Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Client Protocol Factory Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClientProtocolFactoryDef(ClientProtocolFactoryDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>TCP Connector Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>TCP Connector Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTCPConnectorDef(TCPConnectorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Acceptor Def</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Acceptor Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAcceptorDef(AcceptorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>TCP Acceptor Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>TCP Acceptor Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTCPAcceptorDef(TCPAcceptorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>JVM Acceptor Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>JVM Acceptor Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJVMAcceptorDef(JVMAcceptorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>JVM Connector Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>JVM Connector Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJVMConnectorDef(JVMConnectorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>HTTP Connector Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>HTTP Connector Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseHTTPConnectorDef(HTTPConnectorDef object)
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
   * Returns the result of interpreting the object as an instance of '<em>TCP Selector Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>TCP Selector Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTCPSelectorDef(TCPSelectorDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Server Protocol Factory Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Server Protocol Factory Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseServerProtocolFactoryDef(ServerProtocolFactoryDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Buffer Provider Def</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Buffer Provider Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBufferProviderDef(BufferProviderDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Buffer Pool Def</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Buffer Pool Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBufferPoolDef(BufferPoolDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Protocol Provider Def</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Protocol Provider Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProtocolProviderDef(ProtocolProviderDef object)
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

} // Net4jDefsSwitch
