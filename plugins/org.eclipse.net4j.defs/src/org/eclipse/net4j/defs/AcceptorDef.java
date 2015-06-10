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

import org.eclipse.net4j.util.defs.Def;
import org.eclipse.net4j.util.defs.NegotiatorDef;
import org.eclipse.net4j.util.defs.ThreadPoolDef;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Acceptor Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.defs.AcceptorDef#getBufferProvider <em>Buffer Provider</em>}</li>
 *   <li>{@link org.eclipse.net4j.defs.AcceptorDef#getExecutorService <em>Executor Service</em>}</li>
 *   <li>{@link org.eclipse.net4j.defs.AcceptorDef#getNegotiator <em>Negotiator</em>}</li>
 *   <li>{@link org.eclipse.net4j.defs.AcceptorDef#getServerProtocolProvider <em>Server Protocol Provider</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.defs.Net4jDefsPackage#getAcceptorDef()
 * @model abstract="true"
 * @generated
 */
public interface AcceptorDef extends Def
{
  /**
   * Returns the value of the '<em><b>Buffer Provider</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Buffer Provider</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Buffer Provider</em>' reference.
   * @see #setBufferProvider(BufferPoolDef)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getAcceptorDef_BufferProvider()
   * @model
   * @generated
   */
  BufferPoolDef getBufferProvider();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getBufferProvider <em>Buffer Provider</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Buffer Provider</em>' reference.
   * @see #getBufferProvider()
   * @generated
   */
  void setBufferProvider(BufferPoolDef value);

  /**
   * Returns the value of the '<em><b>Executor Service</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Executor Service</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Executor Service</em>' reference.
   * @see #setExecutorService(ThreadPoolDef)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getAcceptorDef_ExecutorService()
   * @model
   * @generated
   */
  ThreadPoolDef getExecutorService();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getExecutorService <em>Executor Service</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Executor Service</em>' reference.
   * @see #getExecutorService()
   * @generated
   */
  void setExecutorService(ThreadPoolDef value);

  /**
   * Returns the value of the '<em><b>Negotiator</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Negotiator</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Negotiator</em>' reference.
   * @see #isSetNegotiator()
   * @see #unsetNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getAcceptorDef_Negotiator()
   * @model unsettable="true"
   * @generated
   */
  NegotiatorDef getNegotiator();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getNegotiator <em>Negotiator</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Negotiator</em>' reference.
   * @see #isSetNegotiator()
   * @see #unsetNegotiator()
   * @see #getNegotiator()
   * @generated
   */
  void setNegotiator(NegotiatorDef value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getNegotiator <em>Negotiator</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetNegotiator()
   * @see #getNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @generated
   */
  void unsetNegotiator();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getNegotiator <em>Negotiator</em>}' reference is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Negotiator</em>' reference is set.
   * @see #unsetNegotiator()
   * @see #getNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @generated
   */
  boolean isSetNegotiator();

  /**
   * Returns the value of the '<em><b>Server Protocol Provider</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.net4j.defs.ServerProtocolFactoryDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Server Protocol Provider</em>' reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Server Protocol Provider</em>' reference list.
   * @see #isSetServerProtocolProvider()
   * @see #unsetServerProtocolProvider()
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getAcceptorDef_ServerProtocolProvider()
   * @model unsettable="true" required="true"
   * @generated
   */
  EList<ServerProtocolFactoryDef> getServerProtocolProvider();

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getServerProtocolProvider <em>Server Protocol Provider</em>}' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetServerProtocolProvider()
   * @see #getServerProtocolProvider()
   * @generated
   */
  void unsetServerProtocolProvider();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.defs.AcceptorDef#getServerProtocolProvider <em>Server Protocol Provider</em>}' reference list is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Server Protocol Provider</em>' reference list is set.
   * @see #unsetServerProtocolProvider()
   * @see #getServerProtocolProvider()
   * @generated
   */
  boolean isSetServerProtocolProvider();

} // AcceptorDef
