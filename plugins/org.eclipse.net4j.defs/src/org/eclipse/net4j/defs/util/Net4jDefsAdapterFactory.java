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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * @see org.eclipse.net4j.defs.Net4jDefsPackage
 * @generated
 */
public class Net4jDefsAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static Net4jDefsPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.ConnectorDef <em>Connector Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.ConnectorDef
   * @generated
   */
  public Adapter createConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.ClientProtocolFactoryDef <em>Client Protocol Factory Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.ClientProtocolFactoryDef
   * @generated
   */
  public Adapter createClientProtocolFactoryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.TCPConnectorDef <em>TCP Connector Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.TCPConnectorDef
   * @generated
   */
  public Adapter createTCPConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.AcceptorDef <em>Acceptor Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.AcceptorDef
   * @generated
   */
  public Adapter createAcceptorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.TCPAcceptorDef <em>TCP Acceptor Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.TCPAcceptorDef
   * @generated
   */
  public Adapter createTCPAcceptorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.JVMAcceptorDef <em>JVM Acceptor Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.JVMAcceptorDef
   * @generated
   */
  public Adapter createJVMAcceptorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.JVMConnectorDef <em>JVM Connector Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.JVMConnectorDef
   * @generated
   */
  public Adapter createJVMConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.HTTPConnectorDef <em>HTTP Connector Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.HTTPConnectorDef
   * @generated
   */
  public Adapter createHTTPConnectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.TCPSelectorDef <em>TCP Selector Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.TCPSelectorDef
   * @generated
   */
  public Adapter createTCPSelectorDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.ServerProtocolFactoryDef <em>Server Protocol Factory Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.ServerProtocolFactoryDef
   * @generated
   */
  public Adapter createServerProtocolFactoryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.BufferProviderDef <em>Buffer Provider Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.BufferProviderDef
   * @generated
   */
  public Adapter createBufferProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.BufferPoolDef <em>Buffer Pool Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
   * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.BufferPoolDef
   * @generated
   */
  public Adapter createBufferPoolDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.ProtocolProviderDef <em>Protocol Provider Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.ProtocolProviderDef
   * @generated
   */
  public Adapter createProtocolProviderDefAdapter()
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

} // Net4jDefsAdapterFactory
