/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4JUIDefsPackageImpl.java,v 1.1 2008-12-28 18:07:30 estepper Exp $
 */
package org.eclipse.net4j.net4juidefs.impl;

import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4juidefs.InteractiveCredentialsProviderDef;
import org.eclipse.net4j.net4juidefs.Net4JUIDefsFactory;
import org.eclipse.net4j.net4juidefs.Net4JUIDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Net4JUIDefsPackageImpl extends EPackageImpl implements Net4JUIDefsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interactiveCredentialsProviderDefEClass = null;

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
	 * @see org.eclipse.net4j.net4juidefs.Net4JUIDefsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Net4JUIDefsPackageImpl() {
		super(eNS_URI, Net4JUIDefsFactory.eINSTANCE);
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
	public static Net4JUIDefsPackage init() {
		if (isInited) return (Net4JUIDefsPackage)EPackage.Registry.INSTANCE.getEPackage(Net4JUIDefsPackage.eNS_URI);

		// Obtain or create and register package
		Net4JUIDefsPackageImpl theNet4JUIDefsPackage = (Net4JUIDefsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof Net4JUIDefsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new Net4JUIDefsPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		Net4jDefsPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theNet4JUIDefsPackage.createPackageContents();

		// Initialize created meta-data
		theNet4JUIDefsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theNet4JUIDefsPackage.freeze();

		return theNet4JUIDefsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInteractiveCredentialsProviderDef() {
		return interactiveCredentialsProviderDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4JUIDefsFactory getNet4JUIDefsFactory() {
		return (Net4JUIDefsFactory)getEFactoryInstance();
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
		interactiveCredentialsProviderDefEClass = createEClass(INTERACTIVE_CREDENTIALS_PROVIDER_DEF);
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
		interactiveCredentialsProviderDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());

		// Initialize classes and features; add operations and parameters
		initEClass(interactiveCredentialsProviderDefEClass, InteractiveCredentialsProviderDef.class, "InteractiveCredentialsProviderDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //Net4JUIDefsPackageImpl
