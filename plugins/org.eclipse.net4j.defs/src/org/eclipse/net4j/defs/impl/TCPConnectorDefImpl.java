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
import org.eclipse.net4j.defs.TCPConnectorDef;
import org.eclipse.net4j.defs.TCPSelectorDef;
import org.eclipse.net4j.internal.tcp.TCPClientConnector;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>TCP Connector Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.TCPConnectorDefImpl#getTcpSelectorDef <em>Tcp Selector Def</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.TCPConnectorDefImpl#getHost <em>Host</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.TCPConnectorDefImpl#getPort <em>Port</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TCPConnectorDefImpl extends ConnectorDefImpl implements TCPConnectorDef
{
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
   * This is true if the Port attribute has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  protected boolean portESet;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TCPConnectorDefImpl()
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
    return Net4jDefsPackage.Literals.TCP_CONNECTOR_DEF;
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
              Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF, oldTcpSelectorDef, tcpSelectorDef));
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
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF,
          oldTcpSelectorDef, tcpSelectorDef));
    }
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
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.TCP_CONNECTOR_DEF__HOST, oldHost, host));
    }
  }

  /**
   * <!-- begin-user-doc -->gets the <b>port</b> of this connector. If the port was not set, the
   * {@link ITCPConnector#DEFAULT_PORT} is returned<!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public int getPort()
  {
    if (isSetPort())
    {
      return getPortGen();
    }
    else
    {
      return ITCPConnector.DEFAULT_PORT;
    }
  }

  /**
   * @generated
   */
  public int getPortGen()
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
    boolean oldPortESet = portESet;
    portESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.TCP_CONNECTOR_DEF__PORT, oldPort, port,
          !oldPortESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetPort()
  {
    int oldPort = port;
    boolean oldPortESet = portESet;
    port = PORT_EDEFAULT;
    portESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jDefsPackage.TCP_CONNECTOR_DEF__PORT, oldPort,
          PORT_EDEFAULT, oldPortESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetPort()
  {
    return portESet;
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
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF:
      if (resolve)
      {
        return getTcpSelectorDef();
      }
      return basicGetTcpSelectorDef();
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__HOST:
      return getHost();
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__PORT:
      return getPort();
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
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF:
      setTcpSelectorDef((TCPSelectorDef)newValue);
      return;
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__HOST:
      setHost((String)newValue);
      return;
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__PORT:
      setPort((Integer)newValue);
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
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF:
      setTcpSelectorDef((TCPSelectorDef)null);
      return;
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__HOST:
      setHost(HOST_EDEFAULT);
      return;
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__PORT:
      unsetPort();
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
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF:
      return tcpSelectorDef != null;
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__HOST:
      return HOST_EDEFAULT == null ? host != null : !HOST_EDEFAULT.equals(host);
    case Net4jDefsPackage.TCP_CONNECTOR_DEF__PORT:
      return isSetPort();
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
    if (portESet)
    {
      result.append(port);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(')');
    return result.toString();
  }

  /**
   * @ADDED
   */
  @Override
  protected ITCPConnector createInstance()
  {
    TCPClientConnector connector = new TCPClientConnector();

    configure(connector);
    connector.setSelector((ITCPSelector)getTcpSelectorDef().getInstance());
    connector.setHost(getHost());
    connector.setPort(getPort());

    return connector;
  }

  /**
   * @ADDED
   */
  @Override
  protected void validateDefinition()
  {
    super.validateDefinition();
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF), "tcp selector not set!");
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.TCP_CONNECTOR_DEF__HOST), "host not set!"); //
  }
} // TCPConnectorDefImpl
