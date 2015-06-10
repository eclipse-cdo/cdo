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
package org.eclipse.net4j.defs;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;

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
 * @see org.eclipse.net4j.defs.Net4jDefsFactory
 * @model kind="package"
 * @generated
 */
public interface Net4jDefsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "defs"; //$NON-NLS-1$

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/NET4J/defs/1.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "net4j.defs"; //$NON-NLS-1$

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Net4jDefsPackage eINSTANCE = org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.ConnectorDefImpl <em>Connector Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.ConnectorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getConnectorDef()
   * @generated
   */
  int CONNECTOR_DEF = 0;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTOR_DEF__USER_ID = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTOR_DEF__BUFFER_PROVIDER = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTOR_DEF__EXECUTOR_SERVICE = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTOR_DEF__NEGOTIATOR = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Client Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Connector Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int CONNECTOR_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.ProtocolProviderDefImpl <em>Protocol Provider Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.ProtocolProviderDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getProtocolProviderDef()
   * @generated
   */
  int PROTOCOL_PROVIDER_DEF = 12;

  /**
   * The number of structural features of the '<em>Protocol Provider Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int PROTOCOL_PROVIDER_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.ClientProtocolFactoryDefImpl <em>Client Protocol Factory Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.ClientProtocolFactoryDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getClientProtocolFactoryDef()
   * @generated
   */
  int CLIENT_PROTOCOL_FACTORY_DEF = 1;

  /**
   * The number of structural features of the '<em>Client Protocol Factory Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CLIENT_PROTOCOL_FACTORY_DEF_FEATURE_COUNT = PROTOCOL_PROVIDER_DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.TCPConnectorDefImpl <em>TCP Connector Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.TCPConnectorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getTCPConnectorDef()
   * @generated
   */
  int TCP_CONNECTOR_DEF = 2;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__USER_ID = CONNECTOR_DEF__USER_ID;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__BUFFER_PROVIDER = CONNECTOR_DEF__BUFFER_PROVIDER;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__EXECUTOR_SERVICE = CONNECTOR_DEF__EXECUTOR_SERVICE;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__NEGOTIATOR = CONNECTOR_DEF__NEGOTIATOR;

  /**
   * The feature id for the '<em><b>Client Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER = CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER;

  /**
   * The feature id for the '<em><b>Tcp Selector Def</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF = CONNECTOR_DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Host</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__HOST = CONNECTOR_DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Port</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF__PORT = CONNECTOR_DEF_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>TCP Connector Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_CONNECTOR_DEF_FEATURE_COUNT = CONNECTOR_DEF_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.AcceptorDefImpl <em>Acceptor Def</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.net4j.defs.impl.AcceptorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getAcceptorDef()
   * @generated
   */
  int ACCEPTOR_DEF = 3;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCEPTOR_DEF__BUFFER_PROVIDER = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCEPTOR_DEF__EXECUTOR_SERVICE = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCEPTOR_DEF__NEGOTIATOR = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Server Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Acceptor Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int ACCEPTOR_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.TCPAcceptorDefImpl <em>TCP Acceptor Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.TCPAcceptorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getTCPAcceptorDef()
   * @generated
   */
  int TCP_ACCEPTOR_DEF = 4;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__BUFFER_PROVIDER = ACCEPTOR_DEF__BUFFER_PROVIDER;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__EXECUTOR_SERVICE = ACCEPTOR_DEF__EXECUTOR_SERVICE;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__NEGOTIATOR = ACCEPTOR_DEF__NEGOTIATOR;

  /**
   * The feature id for the '<em><b>Server Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER = ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER;

  /**
   * The feature id for the '<em><b>Host</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__HOST = ACCEPTOR_DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Port</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__PORT = ACCEPTOR_DEF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Tcp Selector Def</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF = ACCEPTOR_DEF_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>TCP Acceptor Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_ACCEPTOR_DEF_FEATURE_COUNT = ACCEPTOR_DEF_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.JVMAcceptorDefImpl <em>JVM Acceptor Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.JVMAcceptorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getJVMAcceptorDef()
   * @generated
   */
  int JVM_ACCEPTOR_DEF = 5;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_ACCEPTOR_DEF__BUFFER_PROVIDER = ACCEPTOR_DEF__BUFFER_PROVIDER;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_ACCEPTOR_DEF__EXECUTOR_SERVICE = ACCEPTOR_DEF__EXECUTOR_SERVICE;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_ACCEPTOR_DEF__NEGOTIATOR = ACCEPTOR_DEF__NEGOTIATOR;

  /**
   * The feature id for the '<em><b>Server Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER = ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_ACCEPTOR_DEF__NAME = ACCEPTOR_DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>JVM Acceptor Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_ACCEPTOR_DEF_FEATURE_COUNT = ACCEPTOR_DEF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.JVMConnectorDefImpl <em>JVM Connector Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.JVMConnectorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getJVMConnectorDef()
   * @generated
   */
  int JVM_CONNECTOR_DEF = 6;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF__USER_ID = CONNECTOR_DEF__USER_ID;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF__BUFFER_PROVIDER = CONNECTOR_DEF__BUFFER_PROVIDER;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF__EXECUTOR_SERVICE = CONNECTOR_DEF__EXECUTOR_SERVICE;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF__NEGOTIATOR = CONNECTOR_DEF__NEGOTIATOR;

  /**
   * The feature id for the '<em><b>Client Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER = CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF__NAME = CONNECTOR_DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>JVM Connector Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int JVM_CONNECTOR_DEF_FEATURE_COUNT = CONNECTOR_DEF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.HTTPConnectorDefImpl <em>HTTP Connector Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.HTTPConnectorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getHTTPConnectorDef()
   * @generated
   */
  int HTTP_CONNECTOR_DEF = 7;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF__USER_ID = CONNECTOR_DEF__USER_ID;

  /**
   * The feature id for the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF__BUFFER_PROVIDER = CONNECTOR_DEF__BUFFER_PROVIDER;

  /**
   * The feature id for the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF__EXECUTOR_SERVICE = CONNECTOR_DEF__EXECUTOR_SERVICE;

  /**
   * The feature id for the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF__NEGOTIATOR = CONNECTOR_DEF__NEGOTIATOR;

  /**
   * The feature id for the '<em><b>Client Protocol Provider</b></em>' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER = CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER;

  /**
   * The feature id for the '<em><b>Url</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF__URL = CONNECTOR_DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>HTTP Connector Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int HTTP_CONNECTOR_DEF_FEATURE_COUNT = CONNECTOR_DEF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.TCPSelectorDefImpl <em>TCP Selector Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.TCPSelectorDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getTCPSelectorDef()
   * @generated
   */
  int TCP_SELECTOR_DEF = 8;

  /**
   * The number of structural features of the '<em>TCP Selector Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TCP_SELECTOR_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.ServerProtocolFactoryDefImpl <em>Server Protocol Factory Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.ServerProtocolFactoryDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getServerProtocolFactoryDef()
   * @generated
   */
  int SERVER_PROTOCOL_FACTORY_DEF = 9;

  /**
   * The number of structural features of the '<em>Server Protocol Factory Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int SERVER_PROTOCOL_FACTORY_DEF_FEATURE_COUNT = PROTOCOL_PROVIDER_DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.BufferProviderDefImpl <em>Buffer Provider Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.BufferProviderDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getBufferProviderDef()
   * @generated
   */
  int BUFFER_PROVIDER_DEF = 10;

  /**
   * The number of structural features of the '<em>Buffer Provider Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int BUFFER_PROVIDER_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.net4j.defs.impl.BufferPoolDefImpl <em>Buffer Pool Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.defs.impl.BufferPoolDefImpl
   * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getBufferPoolDef()
   * @generated
   */
  int BUFFER_POOL_DEF = 11;

  /**
   * The number of structural features of the '<em>Buffer Pool Def</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int BUFFER_POOL_DEF_FEATURE_COUNT = BUFFER_PROVIDER_DEF_FEATURE_COUNT + 0;

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.ConnectorDef <em>Connector Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Connector Def</em>'.
   * @see org.eclipse.net4j.defs.ConnectorDef
   * @generated
   */
  EClass getConnectorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.ConnectorDef#getUserID <em>User ID</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User ID</em>'.
   * @see org.eclipse.net4j.defs.ConnectorDef#getUserID()
   * @see #getConnectorDef()
   * @generated
   */
  EAttribute getConnectorDef_UserID();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.ConnectorDef#getBufferProvider <em>Buffer Provider</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Buffer Provider</em>'.
   * @see org.eclipse.net4j.defs.ConnectorDef#getBufferProvider()
   * @see #getConnectorDef()
   * @generated
   */
  EReference getConnectorDef_BufferProvider();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.ConnectorDef#getExecutorService <em>Executor Service</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Executor Service</em>'.
   * @see org.eclipse.net4j.defs.ConnectorDef#getExecutorService()
   * @see #getConnectorDef()
   * @generated
   */
  EReference getConnectorDef_ExecutorService();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.ConnectorDef#getNegotiator <em>Negotiator</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Negotiator</em>'.
   * @see org.eclipse.net4j.defs.ConnectorDef#getNegotiator()
   * @see #getConnectorDef()
   * @generated
   */
  EReference getConnectorDef_Negotiator();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.net4j.defs.ConnectorDef#getClientProtocolProvider <em>Client Protocol Provider</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Client Protocol Provider</em>'.
   * @see org.eclipse.net4j.defs.ConnectorDef#getClientProtocolProvider()
   * @see #getConnectorDef()
   * @generated
   */
  EReference getConnectorDef_ClientProtocolProvider();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.ClientProtocolFactoryDef <em>Client Protocol Factory Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Client Protocol Factory Def</em>'.
   * @see org.eclipse.net4j.defs.ClientProtocolFactoryDef
   * @generated
   */
  EClass getClientProtocolFactoryDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.TCPConnectorDef <em>TCP Connector Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>TCP Connector Def</em>'.
   * @see org.eclipse.net4j.defs.TCPConnectorDef
   * @generated
   */
  EClass getTCPConnectorDef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.TCPConnectorDef#getTcpSelectorDef <em>Tcp Selector Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Tcp Selector Def</em>'.
   * @see org.eclipse.net4j.defs.TCPConnectorDef#getTcpSelectorDef()
   * @see #getTCPConnectorDef()
   * @generated
   */
  EReference getTCPConnectorDef_TcpSelectorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.TCPConnectorDef#getHost <em>Host</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Host</em>'.
   * @see org.eclipse.net4j.defs.TCPConnectorDef#getHost()
   * @see #getTCPConnectorDef()
   * @generated
   */
  EAttribute getTCPConnectorDef_Host();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.TCPConnectorDef#getPort <em>Port</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Port</em>'.
   * @see org.eclipse.net4j.defs.TCPConnectorDef#getPort()
   * @see #getTCPConnectorDef()
   * @generated
   */
  EAttribute getTCPConnectorDef_Port();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.AcceptorDef <em>Acceptor Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Acceptor Def</em>'.
   * @see org.eclipse.net4j.defs.AcceptorDef
   * @generated
   */
  EClass getAcceptorDef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.AcceptorDef#getBufferProvider <em>Buffer Provider</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Buffer Provider</em>'.
   * @see org.eclipse.net4j.defs.AcceptorDef#getBufferProvider()
   * @see #getAcceptorDef()
   * @generated
   */
  EReference getAcceptorDef_BufferProvider();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.AcceptorDef#getExecutorService <em>Executor Service</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Executor Service</em>'.
   * @see org.eclipse.net4j.defs.AcceptorDef#getExecutorService()
   * @see #getAcceptorDef()
   * @generated
   */
  EReference getAcceptorDef_ExecutorService();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.AcceptorDef#getNegotiator <em>Negotiator</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Negotiator</em>'.
   * @see org.eclipse.net4j.defs.AcceptorDef#getNegotiator()
   * @see #getAcceptorDef()
   * @generated
   */
  EReference getAcceptorDef_Negotiator();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.net4j.defs.AcceptorDef#getServerProtocolProvider <em>Server Protocol Provider</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Server Protocol Provider</em>'.
   * @see org.eclipse.net4j.defs.AcceptorDef#getServerProtocolProvider()
   * @see #getAcceptorDef()
   * @generated
   */
  EReference getAcceptorDef_ServerProtocolProvider();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.TCPAcceptorDef <em>TCP Acceptor Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>TCP Acceptor Def</em>'.
   * @see org.eclipse.net4j.defs.TCPAcceptorDef
   * @generated
   */
  EClass getTCPAcceptorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.TCPAcceptorDef#getHost <em>Host</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Host</em>'.
   * @see org.eclipse.net4j.defs.TCPAcceptorDef#getHost()
   * @see #getTCPAcceptorDef()
   * @generated
   */
  EAttribute getTCPAcceptorDef_Host();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.TCPAcceptorDef#getPort <em>Port</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Port</em>'.
   * @see org.eclipse.net4j.defs.TCPAcceptorDef#getPort()
   * @see #getTCPAcceptorDef()
   * @generated
   */
  EAttribute getTCPAcceptorDef_Port();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.net4j.defs.TCPAcceptorDef#getTcpSelectorDef <em>Tcp Selector Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Tcp Selector Def</em>'.
   * @see org.eclipse.net4j.defs.TCPAcceptorDef#getTcpSelectorDef()
   * @see #getTCPAcceptorDef()
   * @generated
   */
  EReference getTCPAcceptorDef_TcpSelectorDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.JVMAcceptorDef <em>JVM Acceptor Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>JVM Acceptor Def</em>'.
   * @see org.eclipse.net4j.defs.JVMAcceptorDef
   * @generated
   */
  EClass getJVMAcceptorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.JVMAcceptorDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.net4j.defs.JVMAcceptorDef#getName()
   * @see #getJVMAcceptorDef()
   * @generated
   */
  EAttribute getJVMAcceptorDef_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.JVMConnectorDef <em>JVM Connector Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>JVM Connector Def</em>'.
   * @see org.eclipse.net4j.defs.JVMConnectorDef
   * @generated
   */
  EClass getJVMConnectorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.JVMConnectorDef#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.net4j.defs.JVMConnectorDef#getName()
   * @see #getJVMConnectorDef()
   * @generated
   */
  EAttribute getJVMConnectorDef_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.HTTPConnectorDef <em>HTTP Connector Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>HTTP Connector Def</em>'.
   * @see org.eclipse.net4j.defs.HTTPConnectorDef
   * @generated
   */
  EClass getHTTPConnectorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.net4j.defs.HTTPConnectorDef#getUrl <em>Url</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Url</em>'.
   * @see org.eclipse.net4j.defs.HTTPConnectorDef#getUrl()
   * @see #getHTTPConnectorDef()
   * @generated
   */
  EAttribute getHTTPConnectorDef_Url();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.TCPSelectorDef <em>TCP Selector Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>TCP Selector Def</em>'.
   * @see org.eclipse.net4j.defs.TCPSelectorDef
   * @generated
   */
  EClass getTCPSelectorDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.ServerProtocolFactoryDef <em>Server Protocol Factory Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Server Protocol Factory Def</em>'.
   * @see org.eclipse.net4j.defs.ServerProtocolFactoryDef
   * @generated
   */
  EClass getServerProtocolFactoryDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.BufferProviderDef <em>Buffer Provider Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Buffer Provider Def</em>'.
   * @see org.eclipse.net4j.defs.BufferProviderDef
   * @generated
   */
  EClass getBufferProviderDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.BufferPoolDef <em>Buffer Pool Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Buffer Pool Def</em>'.
   * @see org.eclipse.net4j.defs.BufferPoolDef
   * @generated
   */
  EClass getBufferPoolDef();

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.defs.ProtocolProviderDef <em>Protocol Provider Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Protocol Provider Def</em>'.
   * @see org.eclipse.net4j.defs.ProtocolProviderDef
   * @generated
   */
  EClass getProtocolProviderDef();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Net4jDefsFactory getNet4jDefsFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.ConnectorDefImpl <em>Connector Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.ConnectorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getConnectorDef()
     * @generated
     */
    EClass CONNECTOR_DEF = eINSTANCE.getConnectorDef();

    /**
     * The meta object literal for the '<em><b>User ID</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute CONNECTOR_DEF__USER_ID = eINSTANCE.getConnectorDef_UserID();

    /**
     * The meta object literal for the '<em><b>Buffer Provider</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference CONNECTOR_DEF__BUFFER_PROVIDER = eINSTANCE.getConnectorDef_BufferProvider();

    /**
     * The meta object literal for the '<em><b>Executor Service</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTOR_DEF__EXECUTOR_SERVICE = eINSTANCE.getConnectorDef_ExecutorService();

    /**
     * The meta object literal for the '<em><b>Negotiator</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference CONNECTOR_DEF__NEGOTIATOR = eINSTANCE.getConnectorDef_Negotiator();

    /**
     * The meta object literal for the '<em><b>Client Protocol Provider</b></em>' reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER = eINSTANCE.getConnectorDef_ClientProtocolProvider();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.ClientProtocolFactoryDefImpl <em>Client Protocol Factory Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.ClientProtocolFactoryDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getClientProtocolFactoryDef()
     * @generated
     */
    EClass CLIENT_PROTOCOL_FACTORY_DEF = eINSTANCE.getClientProtocolFactoryDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.TCPConnectorDefImpl <em>TCP Connector Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.TCPConnectorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getTCPConnectorDef()
     * @generated
     */
    EClass TCP_CONNECTOR_DEF = eINSTANCE.getTCPConnectorDef();

    /**
     * The meta object literal for the '<em><b>Tcp Selector Def</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF = eINSTANCE.getTCPConnectorDef_TcpSelectorDef();

    /**
     * The meta object literal for the '<em><b>Host</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute TCP_CONNECTOR_DEF__HOST = eINSTANCE.getTCPConnectorDef_Host();

    /**
     * The meta object literal for the '<em><b>Port</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute TCP_CONNECTOR_DEF__PORT = eINSTANCE.getTCPConnectorDef_Port();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.AcceptorDefImpl <em>Acceptor Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.AcceptorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getAcceptorDef()
     * @generated
     */
    EClass ACCEPTOR_DEF = eINSTANCE.getAcceptorDef();

    /**
     * The meta object literal for the '<em><b>Buffer Provider</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference ACCEPTOR_DEF__BUFFER_PROVIDER = eINSTANCE.getAcceptorDef_BufferProvider();

    /**
     * The meta object literal for the '<em><b>Executor Service</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCEPTOR_DEF__EXECUTOR_SERVICE = eINSTANCE.getAcceptorDef_ExecutorService();

    /**
     * The meta object literal for the '<em><b>Negotiator</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference ACCEPTOR_DEF__NEGOTIATOR = eINSTANCE.getAcceptorDef_Negotiator();

    /**
     * The meta object literal for the '<em><b>Server Protocol Provider</b></em>' reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER = eINSTANCE.getAcceptorDef_ServerProtocolProvider();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.TCPAcceptorDefImpl <em>TCP Acceptor Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.TCPAcceptorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getTCPAcceptorDef()
     * @generated
     */
    EClass TCP_ACCEPTOR_DEF = eINSTANCE.getTCPAcceptorDef();

    /**
     * The meta object literal for the '<em><b>Host</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute TCP_ACCEPTOR_DEF__HOST = eINSTANCE.getTCPAcceptorDef_Host();

    /**
     * The meta object literal for the '<em><b>Port</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute TCP_ACCEPTOR_DEF__PORT = eINSTANCE.getTCPAcceptorDef_Port();

    /**
     * The meta object literal for the '<em><b>Tcp Selector Def</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF = eINSTANCE.getTCPAcceptorDef_TcpSelectorDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.JVMAcceptorDefImpl <em>JVM Acceptor Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.JVMAcceptorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getJVMAcceptorDef()
     * @generated
     */
    EClass JVM_ACCEPTOR_DEF = eINSTANCE.getJVMAcceptorDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute JVM_ACCEPTOR_DEF__NAME = eINSTANCE.getJVMAcceptorDef_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.JVMConnectorDefImpl <em>JVM Connector Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.JVMConnectorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getJVMConnectorDef()
     * @generated
     */
    EClass JVM_CONNECTOR_DEF = eINSTANCE.getJVMConnectorDef();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute JVM_CONNECTOR_DEF__NAME = eINSTANCE.getJVMConnectorDef_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.HTTPConnectorDefImpl <em>HTTP Connector Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.HTTPConnectorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getHTTPConnectorDef()
     * @generated
     */
    EClass HTTP_CONNECTOR_DEF = eINSTANCE.getHTTPConnectorDef();

    /**
     * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute HTTP_CONNECTOR_DEF__URL = eINSTANCE.getHTTPConnectorDef_Url();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.TCPSelectorDefImpl <em>TCP Selector Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.TCPSelectorDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getTCPSelectorDef()
     * @generated
     */
    EClass TCP_SELECTOR_DEF = eINSTANCE.getTCPSelectorDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.ServerProtocolFactoryDefImpl <em>Server Protocol Factory Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.ServerProtocolFactoryDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getServerProtocolFactoryDef()
     * @generated
     */
    EClass SERVER_PROTOCOL_FACTORY_DEF = eINSTANCE.getServerProtocolFactoryDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.BufferProviderDefImpl <em>Buffer Provider Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.BufferProviderDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getBufferProviderDef()
     * @generated
     */
    EClass BUFFER_PROVIDER_DEF = eINSTANCE.getBufferProviderDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.BufferPoolDefImpl <em>Buffer Pool Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.BufferPoolDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getBufferPoolDef()
     * @generated
     */
    EClass BUFFER_POOL_DEF = eINSTANCE.getBufferPoolDef();

    /**
     * The meta object literal for the '{@link org.eclipse.net4j.defs.impl.ProtocolProviderDefImpl <em>Protocol Provider Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.defs.impl.ProtocolProviderDefImpl
     * @see org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl#getProtocolProviderDef()
     * @generated
     */
    EClass PROTOCOL_PROVIDER_DEF = eINSTANCE.getProtocolProviderDef();

  }

} // Net4jDefsPackage
