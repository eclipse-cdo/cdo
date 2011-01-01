/**
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

import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.defs.TCPAcceptorDef;
import org.eclipse.net4j.defs.TCPSelectorDef;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>TCP Acceptor Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.TCPAcceptorDefImpl#getHost <em>Host</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.TCPAcceptorDefImpl#getPort <em>Port</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.TCPAcceptorDefImpl#getTcpSelectorDef <em>Tcp Selector Def</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TCPAcceptorDefImpl extends AcceptorDefImpl implements TCPAcceptorDef
{
  /**
   * The default value of the '{@link #getHost() <em>Host</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getHost()
   * @generated
   * @ordered
   */
  protected static final String HOST_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getHost() <em>Host</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getHost()
   * @generated
   * @ordered
   */
  protected String host = HOST_EDEFAULT;

  /**
   * The default value of the '{@link #getPort() <em>Port</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getPort()
   * @generated
   * @ordered
   */
  protected static final int PORT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getPort() <em>Port</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getPort()
   * @generated
   * @ordered
   */
  protected int port = PORT_EDEFAULT;

  /**
   * The cached value of the '{@link #getTcpSelectorDef() <em>Tcp Selector Def</em>}' reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getTcpSelectorDef()
   * @generated
   * @ordered
   */
  protected TCPSelectorDef tcpSelectorDef;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TCPAcceptorDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jDefsPackage.Literals.TCP_ACCEPTOR_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getHost()
  {
    return host;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setHost(String newHost)
  {
    String oldHost = host;
    host = newHost;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.TCP_ACCEPTOR_DEF__HOST, oldHost, host));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public int getPort()
  {
    return port;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPort(int newPort)
  {
    int oldPort = port;
    port = newPort;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.TCP_ACCEPTOR_DEF__PORT, oldPort, port));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TCPSelectorDef getTcpSelectorDef()
  {
    if (tcpSelectorDef != null && tcpSelectorDef.eIsProxy())
    {
      InternalEObject oldTcpSelectorDef = (InternalEObject)tcpSelectorDef;
      tcpSelectorDef = (TCPSelectorDef)eResolveProxy(oldTcpSelectorDef);
      if (tcpSelectorDef != oldTcpSelectorDef)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              Net4jDefsPackage.TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF, oldTcpSelectorDef, tcpSelectorDef));
        }
      }
    }
    return tcpSelectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TCPSelectorDef basicGetTcpSelectorDef()
  {
    return tcpSelectorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setTcpSelectorDef(TCPSelectorDef newTcpSelectorDef)
  {
    TCPSelectorDef oldTcpSelectorDef = tcpSelectorDef;
    tcpSelectorDef = newTcpSelectorDef;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF,
          oldTcpSelectorDef, tcpSelectorDef));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__HOST:
      return getHost();
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__PORT:
      return getPort();
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF:
      if (resolve)
      {
        return getTcpSelectorDef();
      }
      return basicGetTcpSelectorDef();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__HOST:
      setHost((String)newValue);
      return;
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__PORT:
      setPort((Integer)newValue);
      return;
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF:
      setTcpSelectorDef((TCPSelectorDef)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__HOST:
      setHost(HOST_EDEFAULT);
      return;
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__PORT:
      setPort(PORT_EDEFAULT);
      return;
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF:
      setTcpSelectorDef((TCPSelectorDef)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__HOST:
      return HOST_EDEFAULT == null ? host != null : !HOST_EDEFAULT.equals(host);
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__PORT:
      return port != PORT_EDEFAULT;
    case Net4jDefsPackage.TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF:
      return tcpSelectorDef != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (host: ");
    result.append(host);
    result.append(", port: ");
    result.append(port);
    result.append(')');
    return result.toString();
  }

  /**
   * @ADDED
   */
  @Override
  protected ITCPAcceptor createInstance()
  {
    TCPSelector tcpSelector = (TCPSelector)getTcpSelectorDef().getInstance();

    TCPAcceptor tcpAcceptor = new TCPAcceptor();

    tcpAcceptor.setStartSynchronously(true);
    tcpAcceptor.setSynchronousStartTimeout(500l);
    configure(tcpAcceptor.getConfig());
    tcpAcceptor.setSelector(tcpSelector);

    tcpAcceptor.setAddress(getHost());
    tcpAcceptor.setPort(getPort());

    return tcpAcceptor;
  }

  /**
   * @ADDED
   */
  @Override
  protected void validateDefinition()
  {
    super.validateDefinition();
    CheckUtil.checkState(getTcpSelectorDef() != null, "tcp selector not set!");
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.TCP_ACCEPTOR_DEF__HOST), "host not set!");
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.TCP_ACCEPTOR_DEF__PORT), "port not set!");
  }
}
