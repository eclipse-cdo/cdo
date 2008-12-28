/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOUIDefsPackageImpl.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
 */
package org.eclipse.emf.cdo.ui.cdouidefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.ui.cdouidefs.CDOEditorDef;
import org.eclipse.emf.cdo.ui.cdouidefs.CDOUIDefsFactory;
import org.eclipse.emf.cdo.ui.cdouidefs.CDOUIDefsPackage;
import org.eclipse.emf.cdo.ui.cdouidefs.EditorDef;

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
public class CDOUIDefsPackageImpl extends EPackageImpl implements CDOUIDefsPackage {
	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	private EClass editorDefEClass = null;

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	private EClass cdoEditorDefEClass = null;

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
   * @see org.eclipse.emf.cdo.ui.cdouidefs.CDOUIDefsPackage#eNS_URI
   * @see #init()
   * @generated
   */
	private CDOUIDefsPackageImpl() {
    super(eNS_URI, CDOUIDefsFactory.eINSTANCE);
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
	public static CDOUIDefsPackage init() {
    if (isInited) return (CDOUIDefsPackage)EPackage.Registry.INSTANCE.getEPackage(CDOUIDefsPackage.eNS_URI);

    // Obtain or create and register package
    CDOUIDefsPackageImpl theCDOUIDefsPackage = (CDOUIDefsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof CDOUIDefsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new CDOUIDefsPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    CDODefsPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theCDOUIDefsPackage.createPackageContents();

    // Initialize created meta-data
    theCDOUIDefsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theCDOUIDefsPackage.freeze();

    return theCDOUIDefsPackage;
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public EClass getEditorDef() {
    return editorDefEClass;
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public EAttribute getEditorDef_EditorID() {
    return (EAttribute)editorDefEClass.getEStructuralFeatures().get(0);
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public EClass getCDOEditorDef() {
    return cdoEditorDefEClass;
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public EReference getCDOEditorDef_CdoView() {
    return (EReference)cdoEditorDefEClass.getEStructuralFeatures().get(0);
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public EAttribute getCDOEditorDef_ResourcePath() {
    return (EAttribute)cdoEditorDefEClass.getEStructuralFeatures().get(1);
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public CDOUIDefsFactory getCDOUIDefsFactory() {
    return (CDOUIDefsFactory)getEFactoryInstance();
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
    editorDefEClass = createEClass(EDITOR_DEF);
    createEAttribute(editorDefEClass, EDITOR_DEF__EDITOR_ID);

    cdoEditorDefEClass = createEClass(CDO_EDITOR_DEF);
    createEReference(cdoEditorDefEClass, CDO_EDITOR_DEF__CDO_VIEW);
    createEAttribute(cdoEditorDefEClass, CDO_EDITOR_DEF__RESOURCE_PATH);
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
    CDODefsPackage theCDODefsPackage = (CDODefsPackage)EPackage.Registry.INSTANCE.getEPackage(CDODefsPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    editorDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
    cdoEditorDefEClass.getESuperTypes().add(this.getEditorDef());

    // Initialize classes and features; add operations and parameters
    initEClass(editorDefEClass, EditorDef.class, "EditorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEditorDef_EditorID(), ecorePackage.getEString(), "editorID", null, 1, 1, EditorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cdoEditorDefEClass, CDOEditorDef.class, "CDOEditorDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCDOEditorDef_CdoView(), theCDODefsPackage.getCDOViewDef(), null, "cdoView", null, 1, 1, CDOEditorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOEditorDef_ResourcePath(), ecorePackage.getEString(), "resourcePath", null, 1, 1, CDOEditorDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //CDOUIDefsPackageImpl
