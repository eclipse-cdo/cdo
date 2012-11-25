/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

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
public class HibernateTestPackageImpl extends EPackageImpl implements HibernateTestPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bz356181_MainEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bz356181_TransientEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bz356181_NonTransientEClass = null;

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
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private HibernateTestPackageImpl() {
		super(eNS_URI, HibernateTestFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link HibernateTestPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static HibernateTestPackage init() {
		if (isInited) return (HibernateTestPackage)EPackage.Registry.INSTANCE.getEPackage(HibernateTestPackage.eNS_URI);

		// Obtain or create and register package
		HibernateTestPackageImpl theHibernateTestPackage = (HibernateTestPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof HibernateTestPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new HibernateTestPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theHibernateTestPackage.createPackageContents();

		// Initialize created meta-data
		theHibernateTestPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theHibernateTestPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(HibernateTestPackage.eNS_URI, theHibernateTestPackage);
		return theHibernateTestPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBz356181_Main() {
		return bz356181_MainEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBz356181_Main_Transient() {
		return (EAttribute)bz356181_MainEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBz356181_Main_NonTransient() {
		return (EAttribute)bz356181_MainEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBz356181_Main_TransientRef() {
		return (EReference)bz356181_MainEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBz356181_Main_TransientOtherRef() {
		return (EReference)bz356181_MainEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBz356181_Transient() {
		return bz356181_TransientEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBz356181_NonTransient() {
		return bz356181_NonTransientEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBz356181_NonTransient_Main() {
		return (EReference)bz356181_NonTransientEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HibernateTestFactory getHibernateTestFactory() {
		return (HibernateTestFactory)getEFactoryInstance();
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
		bz356181_MainEClass = createEClass(BZ356181_MAIN);
		createEAttribute(bz356181_MainEClass, BZ356181_MAIN__TRANSIENT);
		createEAttribute(bz356181_MainEClass, BZ356181_MAIN__NON_TRANSIENT);
		createEReference(bz356181_MainEClass, BZ356181_MAIN__TRANSIENT_REF);
		createEReference(bz356181_MainEClass, BZ356181_MAIN__TRANSIENT_OTHER_REF);

		bz356181_TransientEClass = createEClass(BZ356181_TRANSIENT);

		bz356181_NonTransientEClass = createEClass(BZ356181_NON_TRANSIENT);
		createEReference(bz356181_NonTransientEClass, BZ356181_NON_TRANSIENT__MAIN);
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

		// Initialize classes and features; add operations and parameters
		initEClass(bz356181_MainEClass, Bz356181_Main.class, "Bz356181_Main", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBz356181_Main_Transient(), ecorePackage.getEString(), "transient", null, 0, 1, Bz356181_Main.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBz356181_Main_NonTransient(), ecorePackage.getEString(), "nonTransient", null, 0, 1, Bz356181_Main.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBz356181_Main_TransientRef(), this.getBz356181_Transient(), null, "transientRef", null, 0, 1, Bz356181_Main.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBz356181_Main_TransientOtherRef(), this.getBz356181_NonTransient(), null, "transientOtherRef", null, 0, 1, Bz356181_Main.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(bz356181_TransientEClass, Bz356181_Transient.class, "Bz356181_Transient", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(bz356181_NonTransientEClass, Bz356181_NonTransient.class, "Bz356181_NonTransient", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBz356181_NonTransient_Main(), this.getBz356181_Main(), null, "main", null, 0, 1, Bz356181_NonTransient.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// teneo.jpa
		createTeneoAnnotations();
	}

	/**
	 * Initializes the annotations for <b>teneo.jpa</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createTeneoAnnotations() {
		String source = "teneo.jpa";		
		addAnnotation
		  (getBz356181_Main_Transient(), 
		   source, 
		   new String[] {
			 "value", "@Transient"
		   });		
		addAnnotation
		  (bz356181_TransientEClass, 
		   source, 
		   new String[] {
			 "value", "@Transient"
		   });
	}

} //HibernateTestPackageImpl
