/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>TCP Connector Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.defs.TCPConnectorDef#getTcpSelectorDef <em>Tcp Selector Def</em>}</li>
 * <li>{@link org.eclipse.net4j.defs.TCPConnectorDef#getHost <em>Host</em>}</li>
 * <li>{@link org.eclipse.net4j.defs.TCPConnectorDef#getPort <em>Port</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.defs.Net4jDefsPackage#getTCPConnectorDef()
 * @model
 * @generated
 */
public interface TCPConnectorDef extends ConnectorDef
{
  /**
   * Returns the value of the '<em><b>Tcp Selector Def</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tcp Selector Def</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tcp Selector Def</em>' reference.
   * @see #setTcpSelectorDef(TCPSelectorDef)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getTCPConnectorDef_TcpSelectorDef()
   * @model
   * @generated
   */
  TCPSelectorDef getTcpSelectorDef();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.TCPConnectorDef#getTcpSelectorDef <em>Tcp Selector Def</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Tcp Selector Def</em>' reference.
   * @see #getTcpSelectorDef()
   * @generated
   */
  void setTcpSelectorDef(TCPSelectorDef value);

  /**
   * Returns the value of the '<em><b>Host</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Host</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Host</em>' attribute.
   * @see #setHost(String)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getTCPConnectorDef_Host()
   * @model required="true"
   * @generated
   */
  String getHost();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.TCPConnectorDef#getHost <em>Host</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Host</em>' attribute.
   * @see #getHost()
   * @generated
   */
  void setHost(String value);

  /**
   * Returns the value of the '<em><b>Port</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Port</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Port</em>' attribute.
   * @see #isSetPort()
   * @see #unsetPort()
   * @see #setPort(int)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getTCPConnectorDef_Port()
   * @model unsettable="true"
   * @generated
   */
  int getPort();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.TCPConnectorDef#getPort <em>Port</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Port</em>' attribute.
   * @see #isSetPort()
   * @see #unsetPort()
   * @see #getPort()
   * @generated
   */
  void setPort(int value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.defs.TCPConnectorDef#getPort <em>Port</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetPort()
   * @see #getPort()
   * @see #setPort(int)
   * @generated
   */
  void unsetPort();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.defs.TCPConnectorDef#getPort <em>Port</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Port</em>' attribute is set.
   * @see #unsetPort()
   * @see #getPort()
   * @see #setPort(int)
   * @generated
   */
  boolean isSetPort();

} // TCPConnectorDef
