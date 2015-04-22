/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andre Dietisheim - initial API and implementation
 *   Eike Stepper - maintenance
 *
 */
package org.eclipse.net4j.defs.impl;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.defs.AcceptorDef;
import org.eclipse.net4j.defs.BufferPoolDef;
import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.defs.ServerProtocolFactoryDef;
import org.eclipse.net4j.defs.util.Net4jDefsUtil;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.defs.NegotiatorDef;
import org.eclipse.net4j.util.defs.ThreadPoolDef;
import org.eclipse.net4j.util.defs.impl.DefImpl;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Acceptor Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.net4j.defs.impl.AcceptorDefImpl#getBufferProvider <em>Buffer Provider</em>}</li>
 *   <li>{@link org.eclipse.net4j.defs.impl.AcceptorDefImpl#getExecutorService <em>Executor Service</em>}</li>
 *   <li>{@link org.eclipse.net4j.defs.impl.AcceptorDefImpl#getNegotiator <em>Negotiator</em>}</li>
 *   <li>{@link org.eclipse.net4j.defs.impl.AcceptorDefImpl#getServerProtocolProvider <em>Server Protocol Provider</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AcceptorDefImpl extends DefImpl implements AcceptorDef
{
  /**
   * The cached value of the '{@link #getBufferProvider() <em>Buffer Provider</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBufferProvider()
   * @generated
   * @ordered
   */
  protected BufferPoolDef bufferProvider;

  /**
   * The cached value of the '{@link #getExecutorService() <em>Executor Service</em>}' reference.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getExecutorService()
   * @generated
   * @ordered
   */
  protected ThreadPoolDef executorService;

  /**
   * The cached value of the '{@link #getNegotiator() <em>Negotiator</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getNegotiator()
   * @generated
   * @ordered
   */
  protected NegotiatorDef negotiator;

  /**
   * This is true if the Negotiator reference has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean negotiatorESet;

  /**
   * The cached value of the '{@link #getServerProtocolProvider() <em>Server Protocol Provider</em>}' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getServerProtocolProvider()
   * @generated
   * @ordered
   */
  protected EList<ServerProtocolFactoryDef> serverProtocolProvider;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AcceptorDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jDefsPackage.Literals.ACCEPTOR_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public BufferPoolDef getBufferProvider()
  {
    if (bufferProvider != null && bufferProvider.eIsProxy())
    {
      InternalEObject oldBufferProvider = (InternalEObject)bufferProvider;
      bufferProvider = (BufferPoolDef)eResolveProxy(oldBufferProvider);
      if (bufferProvider != oldBufferProvider)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER,
              oldBufferProvider, bufferProvider));
        }
      }
    }
    return bufferProvider;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public BufferPoolDef basicGetBufferProvider()
  {
    return bufferProvider;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setBufferProvider(BufferPoolDef newBufferProvider)
  {
    BufferPoolDef oldBufferProvider = bufferProvider;
    bufferProvider = newBufferProvider;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER,
          oldBufferProvider, bufferProvider));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ThreadPoolDef getExecutorService()
  {
    if (executorService != null && executorService.eIsProxy())
    {
      InternalEObject oldExecutorService = (InternalEObject)executorService;
      executorService = (ThreadPoolDef)eResolveProxy(oldExecutorService);
      if (executorService != oldExecutorService)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE,
              oldExecutorService, executorService));
        }
      }
    }
    return executorService;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ThreadPoolDef basicGetExecutorService()
  {
    return executorService;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setExecutorService(ThreadPoolDef newExecutorService)
  {
    ThreadPoolDef oldExecutorService = executorService;
    executorService = newExecutorService;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE,
          oldExecutorService, executorService));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NegotiatorDef getNegotiator()
  {
    if (negotiator != null && negotiator.eIsProxy())
    {
      InternalEObject oldNegotiator = (InternalEObject)negotiator;
      negotiator = (NegotiatorDef)eResolveProxy(oldNegotiator);
      if (negotiator != oldNegotiator)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR,
              oldNegotiator, negotiator));
        }
      }
    }
    return negotiator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NegotiatorDef basicGetNegotiator()
  {
    return negotiator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setNegotiator(NegotiatorDef newNegotiator)
  {
    NegotiatorDef oldNegotiator = negotiator;
    negotiator = newNegotiator;
    boolean oldNegotiatorESet = negotiatorESet;
    negotiatorESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR, oldNegotiator,
          negotiator, !oldNegotiatorESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void unsetNegotiator()
  {
    NegotiatorDef oldNegotiator = negotiator;
    boolean oldNegotiatorESet = negotiatorESet;
    negotiator = null;
    negotiatorESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR, oldNegotiator,
          null, oldNegotiatorESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetNegotiator()
  {
    return negotiatorESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EList<ServerProtocolFactoryDef> getServerProtocolProvider()
  {
    if (serverProtocolProvider == null)
    {
      serverProtocolProvider = new EObjectResolvingEList.Unsettable<ServerProtocolFactoryDef>(
          ServerProtocolFactoryDef.class, this, Net4jDefsPackage.ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER);
    }
    return serverProtocolProvider;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void unsetServerProtocolProvider()
  {
    if (serverProtocolProvider != null)
    {
      ((InternalEList.Unsettable<?>)serverProtocolProvider).unset();
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetServerProtocolProvider()
  {
    return serverProtocolProvider != null && ((InternalEList.Unsettable<?>)serverProtocolProvider).isSet();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER:
      if (resolve)
      {
        return getBufferProvider();
      }
      return basicGetBufferProvider();
    case Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE:
      if (resolve)
      {
        return getExecutorService();
      }
      return basicGetExecutorService();
    case Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR:
      if (resolve)
      {
        return getNegotiator();
      }
      return basicGetNegotiator();
    case Net4jDefsPackage.ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER:
      return getServerProtocolProvider();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER:
      setBufferProvider((BufferPoolDef)newValue);
      return;
    case Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE:
      setExecutorService((ThreadPoolDef)newValue);
      return;
    case Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR:
      setNegotiator((NegotiatorDef)newValue);
      return;
    case Net4jDefsPackage.ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER:
      getServerProtocolProvider().clear();
      getServerProtocolProvider().addAll((Collection<? extends ServerProtocolFactoryDef>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER:
      setBufferProvider((BufferPoolDef)null);
      return;
    case Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE:
      setExecutorService((ThreadPoolDef)null);
      return;
    case Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR:
      unsetNegotiator();
      return;
    case Net4jDefsPackage.ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER:
      unsetServerProtocolProvider();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER:
      return bufferProvider != null;
    case Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE:
      return executorService != null;
    case Net4jDefsPackage.ACCEPTOR_DEF__NEGOTIATOR:
      return isSetNegotiator();
    case Net4jDefsPackage.ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER:
      return isSetServerProtocolProvider();
    }
    return super.eIsSet(featureID);
  }

  /**
   * @ADDED
   */
  @Override
  protected void validateDefinition()
  {
    super.validateDefinition();
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.ACCEPTOR_DEF__EXECUTOR_SERVICE), "thread pool not set!");
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.ACCEPTOR_DEF__BUFFER_PROVIDER), "buffer pool not set!");
    if (isSetServerProtocolProvider())
    {
      CheckUtil.checkState(getServerProtocolProvider().size() >= 1,
          "server protocol provider is set but has no protocol factories!");
    }
  }

  /**
   * @ADDED
   */
  protected void configure(ITransportConfig config)
  {

    config.setBufferProvider((IBufferProvider)getBufferProvider().getInstance());
    config.setReceiveExecutor((ExecutorService)getExecutorService().getInstance());
    if (isSetServerProtocolProvider() && getServerProtocolProvider().size() > 0)
    {
      config.setProtocolProvider(Net4jDefsUtil.createFactoriesProtocolProvider(getServerProtocolProvider()));
    }
    if (isSetNegotiator())
    {
      config.setNegotiator((INegotiator)getNegotiator().getInstance());
    }
  }

} // AcceptorDefImpl
