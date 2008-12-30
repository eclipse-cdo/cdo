/**
 * <copyright>
 * </copyright>
 *
 * $Id: AcceptorDef.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;

import org.eclipse.net4j.util.net4jutildefs.Def;
import org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Acceptor Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getBufferProvider <em>Buffer Provider</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getExecutorService <em>Executor Service</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getNegotiator <em>Negotiator</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getServerProtocolProvider <em>Server Protocol Provider</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getAcceptorDef()
 * @model abstract="true"
 * @generated
 */
public interface AcceptorDef extends Def
{
  /**
   * Returns the value of the '<em><b>Buffer Provider</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Buffer Provider</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Buffer Provider</em>' reference.
   * @see #setBufferProvider(BufferPoolDef)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getAcceptorDef_BufferProvider()
   * @model
   * @generated
   */
  BufferPoolDef getBufferProvider();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getBufferProvider <em>Buffer Provider</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Buffer Provider</em>' reference.
   * @see #getBufferProvider()
   * @generated
   */
  void setBufferProvider(BufferPoolDef value);

  /**
   * Returns the value of the '<em><b>Executor Service</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Executor Service</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Executor Service</em>' reference.
   * @see #setExecutorService(ThreadPoolDef)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getAcceptorDef_ExecutorService()
   * @model
   * @generated
   */
  ThreadPoolDef getExecutorService();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getExecutorService <em>Executor Service</em>}
   * ' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Executor Service</em>' reference.
   * @see #getExecutorService()
   * @generated
   */
  void setExecutorService(ThreadPoolDef value);

  /**
   * Returns the value of the '<em><b>Negotiator</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Negotiator</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Negotiator</em>' reference.
   * @see #isSetNegotiator()
   * @see #unsetNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getAcceptorDef_Negotiator()
   * @model unsettable="true"
   * @generated
   */
  NegotiatorDef getNegotiator();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getNegotiator <em>Negotiator</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Negotiator</em>' reference.
   * @see #isSetNegotiator()
   * @see #unsetNegotiator()
   * @see #getNegotiator()
   * @generated
   */
  void setNegotiator(NegotiatorDef value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getNegotiator <em>Negotiator</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetNegotiator()
   * @see #getNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @generated
   */
  void unsetNegotiator();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getNegotiator <em>Negotiator</em>}
   * ' reference is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return whether the value of the '<em>Negotiator</em>' reference is set.
   * @see #unsetNegotiator()
   * @see #getNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @generated
   */
  boolean isSetNegotiator();

  /**
   * Returns the value of the '<em><b>Server Protocol Provider</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Server Protocol Provider</em>' reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Server Protocol Provider</em>' reference list.
   * @see #isSetServerProtocolProvider()
   * @see #unsetServerProtocolProvider()
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getAcceptorDef_ServerProtocolProvider()
   * @model unsettable="true" required="true"
   * @generated
   */
  EList<ServerProtocolFactoryDef> getServerProtocolProvider();

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getServerProtocolProvider
   * <em>Server Protocol Provider</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetServerProtocolProvider()
   * @see #getServerProtocolProvider()
   * @generated
   */
  void unsetServerProtocolProvider();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.net4jdefs.AcceptorDef#getServerProtocolProvider
   * <em>Server Protocol Provider</em>}' reference list is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return whether the value of the '<em>Server Protocol Provider</em>' reference list is set.
   * @see #unsetServerProtocolProvider()
   * @see #getServerProtocolProvider()
   * @generated
   */
  boolean isSetServerProtocolProvider();

} // AcceptorDef
