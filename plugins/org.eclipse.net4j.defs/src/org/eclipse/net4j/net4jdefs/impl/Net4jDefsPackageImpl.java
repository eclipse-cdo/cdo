/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jDefsPackageImpl.java,v 1.1 2008-12-28 18:07:28 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.net4jdefs.AcceptorDef;
import org.eclipse.net4j.net4jdefs.BufferPoolDef;
import org.eclipse.net4j.net4jdefs.BufferProviderDef;
import org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef;
import org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.ConnectorDef;
import org.eclipse.net4j.net4jdefs.CredentialsProviderDef;
import org.eclipse.net4j.net4jdefs.HTTPConnectorDef;
import org.eclipse.net4j.net4jdefs.JVMAcceptorDef;
import org.eclipse.net4j.net4jdefs.JVMConnectorDef;
import org.eclipse.net4j.net4jdefs.NegotiatorDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.net4jdefs.ProtocolProviderDef;
import org.eclipse.net4j.net4jdefs.RandomizerDef;
import org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef;
import org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.TCPAcceptorDef;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.net4jdefs.TCPSelectorDef;
import org.eclipse.net4j.net4jdefs.User;
import org.eclipse.net4j.net4jdefs.UserManagerDef;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Net4jDefsPackageImpl extends EPackageImpl implements Net4jDefsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass connectorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass clientProtocolFactoryDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tcpConnectorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass acceptorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tcpAcceptorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass jvmAcceptorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass jvmConnectorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass httpConnectorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass negotiatorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass responseNegotiatorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass challengeNegotiatorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tcpSelectorDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serverProtocolFactoryDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bufferProviderDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bufferPoolDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass protocolProviderDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass credentialsProviderDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass passwordCredentialsProviderDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass userManagerDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass randomizerDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass userEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Net4jDefsPackageImpl() {
		super(eNS_URI, Net4jDefsFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Net4jDefsPackage init() {
		if (isInited) return (Net4jDefsPackage)EPackage.Registry.INSTANCE.getEPackage(Net4jDefsPackage.eNS_URI);

		// Obtain or create and register package
		Net4jDefsPackageImpl theNet4jDefsPackage = (Net4jDefsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof Net4jDefsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new Net4jDefsPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		Net4jUtilDefsPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theNet4jDefsPackage.createPackageContents();

		// Initialize created meta-data
		theNet4jDefsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theNet4jDefsPackage.freeze();

		return theNet4jDefsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConnectorDef() {
		return connectorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConnectorDef_UserID() {
		return (EAttribute)connectorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getConnectorDef_BufferProvider() {
		return (EReference)connectorDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getConnectorDef_ExecutorService() {
		return (EReference)connectorDefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getConnectorDef_Negotiator() {
		return (EReference)connectorDefEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getConnectorDef_ClientProtocolProvider() {
		return (EReference)connectorDefEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getClientProtocolFactoryDef() {
		return clientProtocolFactoryDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTCPConnectorDef() {
		return tcpConnectorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getTCPConnectorDef_TcpSelectorDef() {
		return (EReference)tcpConnectorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTCPConnectorDef_Host() {
		return (EAttribute)tcpConnectorDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTCPConnectorDef_Port() {
		return (EAttribute)tcpConnectorDefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAcceptorDef() {
		return acceptorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAcceptorDef_BufferProvider() {
		return (EReference)acceptorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAcceptorDef_ExecutorService() {
		return (EReference)acceptorDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAcceptorDef_Negotiator() {
		return (EReference)acceptorDefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAcceptorDef_ServerProtocolProvider() {
		return (EReference)acceptorDefEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTCPAcceptorDef() {
		return tcpAcceptorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTCPAcceptorDef_Host() {
		return (EAttribute)tcpAcceptorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTCPAcceptorDef_Port() {
		return (EAttribute)tcpAcceptorDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getTCPAcceptorDef_TcpSelectorDef() {
		return (EReference)tcpAcceptorDefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getJVMAcceptorDef() {
		return jvmAcceptorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJVMAcceptorDef_Name() {
		return (EAttribute)jvmAcceptorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getJVMConnectorDef() {
		return jvmConnectorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJVMConnectorDef_Name() {
		return (EAttribute)jvmConnectorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getHTTPConnectorDef() {
		return httpConnectorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHTTPConnectorDef_Url() {
		return (EAttribute)httpConnectorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNegotiatorDef() {
		return negotiatorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResponseNegotiatorDef() {
		return responseNegotiatorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getResponseNegotiatorDef_CredentialsProvider() {
		return (EReference)responseNegotiatorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getChallengeNegotiatorDef() {
		return challengeNegotiatorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChallengeNegotiatorDef_UserManager() {
		return (EReference)challengeNegotiatorDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChallengeNegotiatorDef_Randomizer() {
		return (EReference)challengeNegotiatorDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTCPSelectorDef() {
		return tcpSelectorDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getServerProtocolFactoryDef() {
		return serverProtocolFactoryDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBufferProviderDef() {
		return bufferProviderDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBufferPoolDef() {
		return bufferPoolDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProtocolProviderDef() {
		return protocolProviderDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCredentialsProviderDef() {
		return credentialsProviderDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCredentialsProviderDef_UserID() {
		return (EAttribute)credentialsProviderDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPasswordCredentialsProviderDef() {
		return passwordCredentialsProviderDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPasswordCredentialsProviderDef_Password() {
		return (EAttribute)passwordCredentialsProviderDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUserManagerDef() {
		return userManagerDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUserManagerDef_User() {
		return (EReference)userManagerDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRandomizerDef() {
		return randomizerDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUser() {
		return userEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUser_UserID() {
		return (EAttribute)userEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUser_Password() {
		return (EAttribute)userEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4jDefsFactory getNet4jDefsFactory() {
		return (Net4jDefsFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		connectorDefEClass = createEClass(CONNECTOR_DEF);
		createEAttribute(connectorDefEClass, CONNECTOR_DEF__USER_ID);
		createEReference(connectorDefEClass, CONNECTOR_DEF__BUFFER_PROVIDER);
		createEReference(connectorDefEClass, CONNECTOR_DEF__EXECUTOR_SERVICE);
		createEReference(connectorDefEClass, CONNECTOR_DEF__NEGOTIATOR);
		createEReference(connectorDefEClass, CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER);

		clientProtocolFactoryDefEClass = createEClass(CLIENT_PROTOCOL_FACTORY_DEF);

		tcpConnectorDefEClass = createEClass(TCP_CONNECTOR_DEF);
		createEReference(tcpConnectorDefEClass, TCP_CONNECTOR_DEF__TCP_SELECTOR_DEF);
		createEAttribute(tcpConnectorDefEClass, TCP_CONNECTOR_DEF__HOST);
		createEAttribute(tcpConnectorDefEClass, TCP_CONNECTOR_DEF__PORT);

		acceptorDefEClass = createEClass(ACCEPTOR_DEF);
		createEReference(acceptorDefEClass, ACCEPTOR_DEF__BUFFER_PROVIDER);
		createEReference(acceptorDefEClass, ACCEPTOR_DEF__EXECUTOR_SERVICE);
		createEReference(acceptorDefEClass, ACCEPTOR_DEF__NEGOTIATOR);
		createEReference(acceptorDefEClass, ACCEPTOR_DEF__SERVER_PROTOCOL_PROVIDER);

		tcpAcceptorDefEClass = createEClass(TCP_ACCEPTOR_DEF);
		createEAttribute(tcpAcceptorDefEClass, TCP_ACCEPTOR_DEF__HOST);
		createEAttribute(tcpAcceptorDefEClass, TCP_ACCEPTOR_DEF__PORT);
		createEReference(tcpAcceptorDefEClass, TCP_ACCEPTOR_DEF__TCP_SELECTOR_DEF);

		jvmAcceptorDefEClass = createEClass(JVM_ACCEPTOR_DEF);
		createEAttribute(jvmAcceptorDefEClass, JVM_ACCEPTOR_DEF__NAME);

		jvmConnectorDefEClass = createEClass(JVM_CONNECTOR_DEF);
		createEAttribute(jvmConnectorDefEClass, JVM_CONNECTOR_DEF__NAME);

		httpConnectorDefEClass = createEClass(HTTP_CONNECTOR_DEF);
		createEAttribute(httpConnectorDefEClass, HTTP_CONNECTOR_DEF__URL);

		negotiatorDefEClass = createEClass(NEGOTIATOR_DEF);

		responseNegotiatorDefEClass = createEClass(RESPONSE_NEGOTIATOR_DEF);
		createEReference(responseNegotiatorDefEClass, RESPONSE_NEGOTIATOR_DEF__CREDENTIALS_PROVIDER);

		challengeNegotiatorDefEClass = createEClass(CHALLENGE_NEGOTIATOR_DEF);
		createEReference(challengeNegotiatorDefEClass, CHALLENGE_NEGOTIATOR_DEF__USER_MANAGER);
		createEReference(challengeNegotiatorDefEClass, CHALLENGE_NEGOTIATOR_DEF__RANDOMIZER);

		tcpSelectorDefEClass = createEClass(TCP_SELECTOR_DEF);

		serverProtocolFactoryDefEClass = createEClass(SERVER_PROTOCOL_FACTORY_DEF);

		bufferProviderDefEClass = createEClass(BUFFER_PROVIDER_DEF);

		bufferPoolDefEClass = createEClass(BUFFER_POOL_DEF);

		protocolProviderDefEClass = createEClass(PROTOCOL_PROVIDER_DEF);

		credentialsProviderDefEClass = createEClass(CREDENTIALS_PROVIDER_DEF);
		createEAttribute(credentialsProviderDefEClass, CREDENTIALS_PROVIDER_DEF__USER_ID);

		passwordCredentialsProviderDefEClass = createEClass(PASSWORD_CREDENTIALS_PROVIDER_DEF);
		createEAttribute(passwordCredentialsProviderDefEClass, PASSWORD_CREDENTIALS_PROVIDER_DEF__PASSWORD);

		userEClass = createEClass(USER);
		createEAttribute(userEClass, USER__USER_ID);
		createEAttribute(userEClass, USER__PASSWORD);

		userManagerDefEClass = createEClass(USER_MANAGER_DEF);
		createEReference(userManagerDefEClass, USER_MANAGER_DEF__USER);

		randomizerDefEClass = createEClass(RANDOMIZER_DEF);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		Net4jUtilDefsPackage theNet4jUtilDefsPackage = (Net4jUtilDefsPackage)EPackage.Registry.INSTANCE.getEPackage(Net4jUtilDefsPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		connectorDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		clientProtocolFactoryDefEClass.getESuperTypes().add(this.getProtocolProviderDef());
		tcpConnectorDefEClass.getESuperTypes().add(this.getConnectorDef());
		acceptorDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		tcpAcceptorDefEClass.getESuperTypes().add(this.getAcceptorDef());
		jvmAcceptorDefEClass.getESuperTypes().add(this.getAcceptorDef());
		jvmConnectorDefEClass.getESuperTypes().add(this.getConnectorDef());
		httpConnectorDefEClass.getESuperTypes().add(this.getConnectorDef());
		negotiatorDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		responseNegotiatorDefEClass.getESuperTypes().add(this.getNegotiatorDef());
		challengeNegotiatorDefEClass.getESuperTypes().add(this.getNegotiatorDef());
		tcpSelectorDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		serverProtocolFactoryDefEClass.getESuperTypes().add(this.getProtocolProviderDef());
		bufferProviderDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		bufferPoolDefEClass.getESuperTypes().add(this.getBufferProviderDef());
		protocolProviderDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		credentialsProviderDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		passwordCredentialsProviderDefEClass.getESuperTypes().add(this.getCredentialsProviderDef());
		userManagerDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
		randomizerDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());

		// Initialize classes and features; add operations and parameters
		initEClass(connectorDefEClass, ConnectorDef.class, "ConnectorDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConnectorDef_UserID(), ecorePackage.getEString(), "userID", null, 0, 1, ConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConnectorDef_BufferProvider(), this.getBufferProviderDef(), null, "bufferProvider", null, 1, 1, ConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConnectorDef_ExecutorService(), theNet4jUtilDefsPackage.getExecutorServiceDef(), null, "executorService", null, 1, 1, ConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConnectorDef_Negotiator(), this.getNegotiatorDef(), null, "negotiator", null, 0, 1, ConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConnectorDef_ClientProtocolProvider(), this.getClientProtocolFactoryDef(), null, "clientProtocolProvider", null, 1, -1, ConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(clientProtocolFactoryDefEClass, ClientProtocolFactoryDef.class, "ClientProtocolFactoryDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(tcpConnectorDefEClass, TCPConnectorDef.class, "TCPConnectorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTCPConnectorDef_TcpSelectorDef(), this.getTCPSelectorDef(), null, "tcpSelectorDef", null, 0, 1, TCPConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTCPConnectorDef_Host(), ecorePackage.getEString(), "host", null, 1, 1, TCPConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTCPConnectorDef_Port(), ecorePackage.getEInt(), "port", null, 0, 1, TCPConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(acceptorDefEClass, AcceptorDef.class, "AcceptorDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAcceptorDef_BufferProvider(), this.getBufferPoolDef(), null, "bufferProvider", null, 0, 1, AcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAcceptorDef_ExecutorService(), theNet4jUtilDefsPackage.getThreadPoolDef(), null, "executorService", null, 0, 1, AcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAcceptorDef_Negotiator(), this.getNegotiatorDef(), null, "negotiator", null, 0, 1, AcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAcceptorDef_ServerProtocolProvider(), this.getServerProtocolFactoryDef(), null, "serverProtocolProvider", null, 1, -1, AcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tcpAcceptorDefEClass, TCPAcceptorDef.class, "TCPAcceptorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTCPAcceptorDef_Host(), ecorePackage.getEString(), "host", null, 1, 1, TCPAcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTCPAcceptorDef_Port(), ecorePackage.getEInt(), "port", null, 1, 1, TCPAcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTCPAcceptorDef_TcpSelectorDef(), this.getTCPSelectorDef(), null, "tcpSelectorDef", null, 1, 1, TCPAcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(jvmAcceptorDefEClass, JVMAcceptorDef.class, "JVMAcceptorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJVMAcceptorDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, JVMAcceptorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(jvmConnectorDefEClass, JVMConnectorDef.class, "JVMConnectorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJVMConnectorDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, JVMConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(httpConnectorDefEClass, HTTPConnectorDef.class, "HTTPConnectorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHTTPConnectorDef_Url(), ecorePackage.getEString(), "url", null, 0, 1, HTTPConnectorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(negotiatorDefEClass, NegotiatorDef.class, "NegotiatorDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(responseNegotiatorDefEClass, ResponseNegotiatorDef.class, "ResponseNegotiatorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResponseNegotiatorDef_CredentialsProvider(), this.getCredentialsProviderDef(), null, "credentialsProvider", null, 0, 1, ResponseNegotiatorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(challengeNegotiatorDefEClass, ChallengeNegotiatorDef.class, "ChallengeNegotiatorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getChallengeNegotiatorDef_UserManager(), this.getUserManagerDef(), null, "userManager", null, 0, 1, ChallengeNegotiatorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChallengeNegotiatorDef_Randomizer(), this.getRandomizerDef(), null, "randomizer", null, 0, 1, ChallengeNegotiatorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tcpSelectorDefEClass, TCPSelectorDef.class, "TCPSelectorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(serverProtocolFactoryDefEClass, ServerProtocolFactoryDef.class, "ServerProtocolFactoryDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(bufferProviderDefEClass, BufferProviderDef.class, "BufferProviderDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(bufferPoolDefEClass, BufferPoolDef.class, "BufferPoolDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(protocolProviderDefEClass, ProtocolProviderDef.class, "ProtocolProviderDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(credentialsProviderDefEClass, CredentialsProviderDef.class, "CredentialsProviderDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCredentialsProviderDef_UserID(), ecorePackage.getEString(), "userID", null, 0, 1, CredentialsProviderDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(passwordCredentialsProviderDefEClass, PasswordCredentialsProviderDef.class, "PasswordCredentialsProviderDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPasswordCredentialsProviderDef_Password(), ecorePackage.getEString(), "password", null, 0, 1, PasswordCredentialsProviderDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(userEClass, User.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUser_UserID(), ecorePackage.getEString(), "userID", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUser_Password(), ecorePackage.getEString(), "password", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(userManagerDefEClass, UserManagerDef.class, "UserManagerDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUserManagerDef_User(), this.getUser(), null, "user", null, 1, -1, UserManagerDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(randomizerDefEClass, RandomizerDef.class, "RandomizerDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //Net4jDefsPackageImpl
