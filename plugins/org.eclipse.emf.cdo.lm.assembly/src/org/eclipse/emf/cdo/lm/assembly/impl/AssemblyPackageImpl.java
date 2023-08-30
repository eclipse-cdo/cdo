/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.assembly.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyFactory;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.assembly.AssemblyPackage;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class AssemblyPackageImpl extends EPackageImpl implements AssemblyPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass assemblyEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass assemblyModuleEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private AssemblyPackageImpl()
  {
    super(eNS_URI, AssemblyFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link AssemblyPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static AssemblyPackage init()
  {
    if (isInited)
    {
      return (AssemblyPackage)EPackage.Registry.INSTANCE.getEPackage(AssemblyPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredAssemblyPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    AssemblyPackageImpl theAssemblyPackage = registeredAssemblyPackage instanceof AssemblyPackageImpl ? (AssemblyPackageImpl)registeredAssemblyPackage
        : new AssemblyPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();
    ModulesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theAssemblyPackage.createPackageContents();

    // Initialize created meta-data
    theAssemblyPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theAssemblyPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(AssemblyPackage.eNS_URI, theAssemblyPackage);
    return theAssemblyPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAssembly()
  {
    return assemblyEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAssembly_SystemName()
  {
    return (EAttribute)assemblyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAssembly_Modules()
  {
    return (EReference)assemblyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAssemblyModule()
  {
    return assemblyModuleEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAssemblyModule_Assembly()
  {
    return (EReference)assemblyModuleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAssemblyModule_Name()
  {
    return (EAttribute)assemblyModuleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAssemblyModule_Version()
  {
    return (EAttribute)assemblyModuleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAssemblyModule_BranchPoint()
  {
    return (EAttribute)assemblyModuleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAssemblyModule_Root()
  {
    return (EAttribute)assemblyModuleEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public AssemblyFactory getAssemblyFactory()
  {
    return (AssemblyFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
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
    assemblyEClass = createEClass(ASSEMBLY);
    createEAttribute(assemblyEClass, ASSEMBLY__SYSTEM_NAME);
    createEReference(assemblyEClass, ASSEMBLY__MODULES);

    assemblyModuleEClass = createEClass(ASSEMBLY_MODULE);
    createEReference(assemblyModuleEClass, ASSEMBLY_MODULE__ASSEMBLY);
    createEAttribute(assemblyModuleEClass, ASSEMBLY_MODULE__NAME);
    createEAttribute(assemblyModuleEClass, ASSEMBLY_MODULE__VERSION);
    createEAttribute(assemblyModuleEClass, ASSEMBLY_MODULE__BRANCH_POINT);
    createEAttribute(assemblyModuleEClass, ASSEMBLY_MODULE__ROOT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is
   * guarded to have no affect on any invocation but its first. <!--
   * begin-user-doc --> <!-- end-user-doc -->
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
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);
    ModulesPackage theModulesPackage = (ModulesPackage)EPackage.Registry.INSTANCE.getEPackage(ModulesPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    assemblyEClass.getESuperTypes().add(theEtypesPackage.getModelElement());
    assemblyModuleEClass.getESuperTypes().add(theEtypesPackage.getModelElement());

    // Initialize classes, features, and operations; add parameters
    initEClass(assemblyEClass, Assembly.class, "Assembly", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAssembly_SystemName(), ecorePackage.getEString(), "systemName", null, 1, 1, Assembly.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAssembly_Modules(), getAssemblyModule(), getAssemblyModule_Assembly(), "modules", null, 0, -1, Assembly.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(assemblyModuleEClass, AssemblyModule.class, "AssemblyModule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAssemblyModule_Assembly(), getAssembly(), getAssembly_Modules(), "assembly", null, 1, 1, AssemblyModule.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAssemblyModule_Name(), ecorePackage.getEString(), "name", null, 1, 1, AssemblyModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAssemblyModule_Version(), theModulesPackage.getVersion(), "version", null, 1, 1, AssemblyModule.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAssemblyModule_BranchPoint(), theEtypesPackage.getBranchPointRef(), "branchPoint", null, 1, 1, AssemblyModule.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAssemblyModule_Root(), ecorePackage.getEBoolean(), "root", null, 0, 1, AssemblyModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for
   * <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    addAnnotation(getAssembly_Modules(), source, new String[] { "name", "module", "kind", "element" });
  }

} // AssemblyPackageImpl
