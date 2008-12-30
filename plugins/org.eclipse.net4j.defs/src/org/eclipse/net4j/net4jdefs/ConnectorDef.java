/**
 * <copyright>
 * </copyright>
 *
 * $Id: ConnectorDef.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;

import org.eclipse.net4j.util.net4jutildefs.Def;
import org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Connector Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getUserID <em>User ID</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getBufferProvider <em>Buffer Provider</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getExecutorService <em>Executor Service</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getNegotiator <em>Negotiator</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getClientProtocolProvider <em>Client Protocol Provider</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getConnectorDef()
 * @model abstract="true"
 * @generated
 */
public interface ConnectorDef extends Def
{
  /**
   * Returns the value of the '<em><b>User ID</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User ID</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>User ID</em>' attribute.
   * @see #isSetUserID()
   * @see #unsetUserID()
   * @see #setUserID(String)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getConnectorDef_UserID()
   * @model unsettable="true"
   * @generated
   */
  String getUserID();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getUserID <em>User ID</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>User ID</em>' attribute.
   * @see #isSetUserID()
   * @see #unsetUserID()
   * @see #getUserID()
   * @generated
   */
  void setUserID(String value);

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getUserID <em>User ID</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetUserID()
   * @see #getUserID()
   * @see #setUserID(String)
   * @generated
   */
  void unsetUserID();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getUserID <em>User ID</em>}'
   * attribute is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return whether the value of the '<em>User ID</em>' attribute is set.
   * @see #unsetUserID()
   * @see #getUserID()
   * @see #setUserID(String)
   * @generated
   */
  boolean isSetUserID();

  /**
   * Returns the value of the '<em><b>Buffer Provider</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Buffer Provider</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Buffer Provider</em>' reference.
   * @see #setBufferProvider(BufferProviderDef)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getConnectorDef_BufferProvider()
   * @model required="true"
   * @generated
   */
  BufferProviderDef getBufferProvider();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getBufferProvider <em>Buffer Provider</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Buffer Provider</em>' reference.
   * @see #getBufferProvider()
   * @generated
   */
  void setBufferProvider(BufferProviderDef value);

  /**
   * Returns the value of the '<em><b>Executor Service</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Executor Service</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Executor Service</em>' reference.
   * @see #setExecutorService(ExecutorServiceDef)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getConnectorDef_ExecutorService()
   * @model required="true"
   * @generated
   */
  ExecutorServiceDef getExecutorService();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getExecutorService
   * <em>Executor Service</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Executor Service</em>' reference.
   * @see #getExecutorService()
   * @generated
   */
  void setExecutorService(ExecutorServiceDef value);

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
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getConnectorDef_Negotiator()
   * @model unsettable="true"
   * @generated
   */
  NegotiatorDef getNegotiator();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getNegotiator <em>Negotiator</em>}'
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
   * Unsets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getNegotiator <em>Negotiator</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetNegotiator()
   * @see #getNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @generated
   */
  void unsetNegotiator();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getNegotiator
   * <em>Negotiator</em>}' reference is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return whether the value of the '<em>Negotiator</em>' reference is set.
   * @see #unsetNegotiator()
   * @see #getNegotiator()
   * @see #setNegotiator(NegotiatorDef)
   * @generated
   */
  boolean isSetNegotiator();

  /**
   * Returns the value of the '<em><b>Client Protocol Provider</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Client Protocol Provider</em>' reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Client Protocol Provider</em>' reference list.
   * @see #isSetClientProtocolProvider()
   * @see #unsetClientProtocolProvider()
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getConnectorDef_ClientProtocolProvider()
   * @model unsettable="true" required="true"
   * @generated
   */
  EList<ClientProtocolFactoryDef> getClientProtocolProvider();

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getClientProtocolProvider
   * <em>Client Protocol Provider</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetClientProtocolProvider()
   * @see #getClientProtocolProvider()
   * @generated
   */
  void unsetClientProtocolProvider();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.net4jdefs.ConnectorDef#getClientProtocolProvider
   * <em>Client Protocol Provider</em>}' reference list is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return whether the value of the '<em>Client Protocol Provider</em>' reference list is set.
   * @see #unsetClientProtocolProvider()
   * @see #getClientProtocolProvider()
   * @generated
   */
  boolean isSetClientProtocolProvider();

} // ConnectorDef
