/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jUtilDefsPackageImpl.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs.impl;

import org.eclipse.net4j.util.net4jutildefs.Def;
import org.eclipse.net4j.util.net4jutildefs.DefsContainer;
import org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef;

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
public class Net4jUtilDefsPackageImpl extends EPackageImpl implements Net4jUtilDefsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass defsContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass defEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass executorServiceDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass threadPoolDefEClass = null;

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
	 * @see org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Net4jUtilDefsPackageImpl() {
		super(eNS_URI, Net4jUtilDefsFactory.eINSTANCE);
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
	public static Net4jUtilDefsPackage init() {
		if (isInited) return (Net4jUtilDefsPackage)EPackage.Registry.INSTANCE.getEPackage(Net4jUtilDefsPackage.eNS_URI);

		// Obtain or create and register package
		Net4jUtilDefsPackageImpl theNet4jUtilDefsPackage = (Net4jUtilDefsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof Net4jUtilDefsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new Net4jUtilDefsPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theNet4jUtilDefsPackage.createPackageContents();

		// Initialize created meta-data
		theNet4jUtilDefsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theNet4jUtilDefsPackage.freeze();

		return theNet4jUtilDefsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDefsContainer() {
		return defsContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDefsContainer_Definitions() {
		return (EReference)defsContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDefsContainer_DefaultDefinition() {
		return (EReference)defsContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDef() {
		return defEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getExecutorServiceDef() {
		return executorServiceDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getThreadPoolDef() {
		return threadPoolDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4jUtilDefsFactory getNet4jUtilDefsFactory() {
		return (Net4jUtilDefsFactory)getEFactoryInstance();
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
		defsContainerEClass = createEClass(DEFS_CONTAINER);
		createEReference(defsContainerEClass, DEFS_CONTAINER__DEFINITIONS);
		createEReference(defsContainerEClass, DEFS_CONTAINER__DEFAULT_DEFINITION);

		defEClass = createEClass(DEF);

		executorServiceDefEClass = createEClass(EXECUTOR_SERVICE_DEF);

		threadPoolDefEClass = createEClass(THREAD_POOL_DEF);
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

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		executorServiceDefEClass.getESuperTypes().add(this.getDef());
		threadPoolDefEClass.getESuperTypes().add(this.getExecutorServiceDef());

		// Initialize classes and features; add operations and parameters
		initEClass(defsContainerEClass, DefsContainer.class, "DefsContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDefsContainer_Definitions(), this.getDef(), null, "definitions", null, 1, -1, DefsContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDefsContainer_DefaultDefinition(), this.getDef(), null, "defaultDefinition", null, 0, 1, DefsContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(defEClass, Def.class, "Def", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		addEOperation(defEClass, ecorePackage.getEJavaObject(), "getInstance", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(defEClass, null, "unsetInstance", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(executorServiceDefEClass, ExecutorServiceDef.class, "ExecutorServiceDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(threadPoolDefEClass, ThreadPoolDef.class, "ThreadPoolDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //Net4jUtilDefsPackageImpl
