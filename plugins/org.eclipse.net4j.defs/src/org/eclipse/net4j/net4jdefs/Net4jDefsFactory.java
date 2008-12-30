/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jDefsFactory.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage
 * @generated
 */
public interface Net4jDefsFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Net4jDefsFactory eINSTANCE = org.eclipse.net4j.net4jdefs.impl.Net4jDefsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>TCP Connector Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>TCP Connector Def</em>'.
   * @generated
   */
  TCPConnectorDef createTCPConnectorDef();

  /**
   * Returns a new object of class '<em>TCP Acceptor Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>TCP Acceptor Def</em>'.
   * @generated
   */
  TCPAcceptorDef createTCPAcceptorDef();

  /**
   * Returns a new object of class '<em>JVM Acceptor Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>JVM Acceptor Def</em>'.
   * @generated
   */
  JVMAcceptorDef createJVMAcceptorDef();

  /**
   * Returns a new object of class '<em>JVM Connector Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>JVM Connector Def</em>'.
   * @generated
   */
  JVMConnectorDef createJVMConnectorDef();

  /**
   * Returns a new object of class '<em>HTTP Connector Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>HTTP Connector Def</em>'.
   * @generated
   */
  HTTPConnectorDef createHTTPConnectorDef();

  /**
   * Returns a new object of class '<em>Response Negotiator Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Response Negotiator Def</em>'.
   * @generated
   */
  ResponseNegotiatorDef createResponseNegotiatorDef();

  /**
   * Returns a new object of class '<em>Challenge Negotiator Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Challenge Negotiator Def</em>'.
   * @generated
   */
  ChallengeNegotiatorDef createChallengeNegotiatorDef();

  /**
   * Returns a new object of class '<em>TCP Selector Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>TCP Selector Def</em>'.
   * @generated
   */
  TCPSelectorDef createTCPSelectorDef();

  /**
   * Returns a new object of class '<em>Buffer Pool Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Buffer Pool Def</em>'.
   * @generated
   */
  BufferPoolDef createBufferPoolDef();

  /**
   * Returns a new object of class '<em>Password Credentials Provider Def</em>'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return a new object of class '<em>Password Credentials Provider Def</em>'.
   * @generated
   */
  PasswordCredentialsProviderDef createPasswordCredentialsProviderDef();

  /**
   * Returns a new object of class '<em>User Manager Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>User Manager Def</em>'.
   * @generated
   */
  UserManagerDef createUserManagerDef();

  /**
   * Returns a new object of class '<em>Randomizer Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Randomizer Def</em>'.
   * @generated
   */
  RandomizerDef createRandomizerDef();

  /**
   * Returns a new object of class '<em>User</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>User</em>'.
   * @generated
   */
  User createUser();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  Net4jDefsPackage getNet4jDefsPackage();

} // Net4jDefsFactory
