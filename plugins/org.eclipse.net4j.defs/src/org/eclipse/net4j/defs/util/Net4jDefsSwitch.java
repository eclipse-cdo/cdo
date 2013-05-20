/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.defs.util;

import org.eclipse.net4j.defs.AcceptorDef;
import org.eclipse.net4j.defs.BufferPoolDef;
import org.eclipse.net4j.defs.BufferProviderDef;
import org.eclipse.net4j.defs.ClientProtocolFactoryDef;
import org.eclipse.net4j.defs.ConnectorDef;
import org.eclipse.net4j.defs.HTTPConnectorDef;
import org.eclipse.net4j.defs.JVMAcceptorDef;
import org.eclipse.net4j.defs.JVMConnectorDef;
import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.defs.ProtocolProviderDef;
import org.eclipse.net4j.defs.ServerProtocolFactoryDef;
import org.eclipse.net4j.defs.TCPAcceptorDef;
import org.eclipse.net4j.defs.TCPConnectorDef;
import org.eclipse.net4j.defs.TCPSelectorDef;
import org.eclipse.net4j.util.defs.Def;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.defs.Net4jDefsPackage
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
