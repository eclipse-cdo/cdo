/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelFactory;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.gmf.codegen.gmfgen.GMFGenPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 * @author Martin Fluegge
 */
public class DawnGmfGenmodelPackageImpl extends EPackageImpl implements DawnGmfGenmodelPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass dawnGMFGeneratorEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private DawnGmfGenmodelPackageImpl()
  {
    super(eNS_URI, DawnGmfGenmodelFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link DawnGmfGenmodelPackage#eINSTANCE} when that field is accessed. Clients
   * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static DawnGmfGenmodelPackage init()
  {
    if (isInited)
    {
      return (DawnGmfGenmodelPackage)EPackage.Registry.INSTANCE.getEPackage(DawnGmfGenmodelPackage.eNS_URI);
    }

    // Obtain or create and register package
    DawnGmfGenmodelPackageImpl theDawnGmfGenmodelPackage = (DawnGmfGenmodelPackageImpl)(EPackage.Registry.INSTANCE
        .get(eNS_URI) instanceof DawnGmfGenmodelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
        : new DawnGmfGenmodelPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    DawngenmodelPackage.eINSTANCE.eClass();
    GMFGenPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theDawnGmfGenmodelPackage.createPackageContents();

    // Initialize created meta-data
    theDawnGmfGenmodelPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theDawnGmfGenmodelPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(DawnGmfGenmodelPackage.eNS_URI, theDawnGmfGenmodelPackage);
    return theDawnGmfGenmodelPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getDawnGMFGenerator()
  {
    return dawnGMFGeneratorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnDocumentProviderClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnEditorUtilClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnCreationWizardClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnCanonicalEditingPolicyClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnDiagramEditPartClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnEditPartFactoryClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnEditPartProviderClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDawnGMFGenerator_DawnEditPolicyProviderClassName()
  {
    return (EAttribute)dawnGMFGeneratorEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDawnGMFGenerator_GMFGenEditorGenerator()
  {
    return (EReference)dawnGMFGeneratorEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnGmfGenmodelFactory getDawnGmfGenmodelFactory()
  {
    return (DawnGmfGenmodelFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    dawnGMFGeneratorEClass = createEClass(DAWN_GMF_GENERATOR);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME);
    createEAttribute(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME);
    createEReference(dawnGMFGeneratorEClass, DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    DawngenmodelPackage theDawngenmodelPackage = (DawngenmodelPackage)EPackage.Registry.INSTANCE
        .getEPackage(DawngenmodelPackage.eNS_URI);
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
    GMFGenPackage theGMFGenPackage = (GMFGenPackage)EPackage.Registry.INSTANCE.getEPackage(GMFGenPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    dawnGMFGeneratorEClass.getESuperTypes().add(theDawngenmodelPackage.getDawnFragmentGenerator());

    // Initialize classes and features; add operations and parameters
    initEClass(dawnGMFGeneratorEClass, DawnGMFGenerator.class, "DawnGMFGenerator", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDawnGMFGenerator_DawnDocumentProviderClassName(), theEcorePackage.getEString(),
        "dawnDocumentProviderClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnEditorUtilClassName(), theEcorePackage.getEString(),
        "dawnEditorUtilClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnCreationWizardClassName(), theEcorePackage.getEString(),
        "dawnCreationWizardClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnCanonicalEditingPolicyClassName(), theEcorePackage.getEString(),
        "dawnCanonicalEditingPolicyClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnDiagramEditPartClassName(), theEcorePackage.getEString(),
        "dawnDiagramEditPartClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnEditPartFactoryClassName(), theEcorePackage.getEString(),
        "dawnEditPartFactoryClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnEditPartProviderClassName(), theEcorePackage.getEString(),
        "dawnEditPartProviderClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDawnGMFGenerator_DawnEditPolicyProviderClassName(), theEcorePackage.getEString(),
        "dawnEditPolicyProviderClassName", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDawnGMFGenerator_GMFGenEditorGenerator(), theGMFGenPackage.getGenEditorGenerator(), null,
        "GMFGenEditorGenerator", null, 0, 1, DawnGMFGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // DawnGmfGenmodelPackageImpl
