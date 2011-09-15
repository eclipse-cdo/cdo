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
package org.eclipse.net4j.defs.impl;

import org.eclipse.net4j.defs.BufferPoolDef;
import org.eclipse.net4j.defs.HTTPConnectorDef;
import org.eclipse.net4j.defs.JVMAcceptorDef;
import org.eclipse.net4j.defs.JVMConnectorDef;
import org.eclipse.net4j.defs.Net4jDefsFactory;
import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.defs.TCPAcceptorDef;
import org.eclipse.net4j.defs.TCPConnectorDef;
import org.eclipse.net4j.defs.TCPSelectorDef;

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
public class Net4jDefsFactoryImpl extends EFactoryImpl implements Net4jDefsFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Net4jDefsFactory init()
  {
    try
    {
      Net4jDefsFactory theNet4jDefsFactory = (Net4jDefsFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/NET4J/defs/1.0.0");
      if (theNet4jDefsFactory != null)
      {
        return theNet4jDefsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Net4jDefsFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jDefsFactoryImpl()
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
    case Net4jDefsPackage.TCP_CONNECTOR_DEF:
      return createTCPConnectorDef();
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF:
      return createTCPAcceptorDef();
    case Net4jDefsPackage.JVM_ACCEPTOR_DEF:
      return createJVMAcceptorDef();
    case Net4jDefsPackage.JVM_CONNECTOR_DEF:
      return createJVMConnectorDef();
    case Net4jDefsPackage.HTTP_CONNECTOR_DEF:
      return createHTTPConnectorDef();
    case Net4jDefsPackage.TCP_SELECTOR_DEF:
      return createTCPSelectorDef();
    case Net4jDefsPackage.BUFFER_POOL_DEF:
      return createBufferPoolDef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TCPConnectorDef createTCPConnectorDef()
  {
    TCPConnectorDefImpl tcpConnectorDef = new TCPConnectorDefImpl();
    return tcpConnectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TCPAcceptorDef createTCPAcceptorDef()
  {
    TCPAcceptorDefImpl tcpAcceptorDef = new TCPAcceptorDefImpl();
    return tcpAcceptorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public JVMAcceptorDef createJVMAcceptorDef()
  {
    JVMAcceptorDefImpl jvmAcceptorDef = new JVMAcceptorDefImpl();
    return jvmAcceptorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public JVMConnectorDef createJVMConnectorDef()
  {
    JVMConnectorDefImpl jvmConnectorDef = new JVMConnectorDefImpl();
    return jvmConnectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public HTTPConnectorDef createHTTPConnectorDef()
  {
    HTTPConnectorDefImpl httpConnectorDef = new HTTPConnectorDefImpl();
    return httpConnectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TCPSelectorDef createTCPSelectorDef()
  {
    TCPSelectorDefImpl tcpSelectorDef = new TCPSelectorDefImpl();
    return tcpSelectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public BufferPoolDef createBufferPoolDef()
  {
    BufferPoolDefImpl bufferPoolDef = new BufferPoolDefImpl();
    return bufferPoolDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Net4jDefsPackage getNet4jDefsPackage()
  {
    return (Net4jDefsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Net4jDefsPackage getPackage()
  {
    return Net4jDefsPackage.eINSTANCE;
  }

} // Net4jDefsFactoryImpl
