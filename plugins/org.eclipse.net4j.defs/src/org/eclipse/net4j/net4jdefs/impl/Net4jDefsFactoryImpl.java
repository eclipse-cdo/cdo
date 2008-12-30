/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jDefsFactoryImpl.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.net4jdefs.BufferPoolDef;
import org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef;
import org.eclipse.net4j.net4jdefs.HTTPConnectorDef;
import org.eclipse.net4j.net4jdefs.JVMAcceptorDef;
import org.eclipse.net4j.net4jdefs.JVMConnectorDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.net4jdefs.RandomizerDef;
import org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef;
import org.eclipse.net4j.net4jdefs.TCPAcceptorDef;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.net4jdefs.TCPSelectorDef;
import org.eclipse.net4j.net4jdefs.User;
import org.eclipse.net4j.net4jdefs.UserManagerDef;

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
    case Net4jDefsPackage.RESPONSE_NEGOTIATOR_DEF:
      return createResponseNegotiatorDef();
    case Net4jDefsPackage.CHALLENGE_NEGOTIATOR_DEF:
      return createChallengeNegotiatorDef();
    case Net4jDefsPackage.TCP_SELECTOR_DEF:
      return createTCPSelectorDef();
    case Net4jDefsPackage.BUFFER_POOL_DEF:
      return createBufferPoolDef();
    case Net4jDefsPackage.PASSWORD_CREDENTIALS_PROVIDER_DEF:
      return createPasswordCredentialsProviderDef();
    case Net4jDefsPackage.USER:
      return createUser();
    case Net4jDefsPackage.USER_MANAGER_DEF:
      return createUserManagerDef();
    case Net4jDefsPackage.RANDOMIZER_DEF:
      return createRandomizerDef();
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
  public ResponseNegotiatorDef createResponseNegotiatorDef()
  {
    ResponseNegotiatorDefImpl responseNegotiatorDef = new ResponseNegotiatorDefImpl();
    return responseNegotiatorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ChallengeNegotiatorDef createChallengeNegotiatorDef()
  {
    ChallengeNegotiatorDefImpl challengeNegotiatorDef = new ChallengeNegotiatorDefImpl();
    return challengeNegotiatorDef;
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
  public PasswordCredentialsProviderDef createPasswordCredentialsProviderDef()
  {
    PasswordCredentialsProviderDefImpl passwordCredentialsProviderDef = new PasswordCredentialsProviderDefImpl();
    return passwordCredentialsProviderDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public UserManagerDef createUserManagerDef()
  {
    UserManagerDefImpl userManagerDef = new UserManagerDefImpl();
    return userManagerDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RandomizerDef createRandomizerDef()
  {
    RandomizerDefImpl randomizerDef = new RandomizerDefImpl();
    return randomizerDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public User createUser()
  {
    UserImpl user = new UserImpl();
    return user;
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
} // Net4jDefsFactoryImpl
