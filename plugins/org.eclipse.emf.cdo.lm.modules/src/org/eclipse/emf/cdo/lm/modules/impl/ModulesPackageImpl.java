/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.modules.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModulesPackageImpl extends EPackageImpl implements ModulesPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleDefinitionEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass dependencyDefinitionEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EDataType versionEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EDataType versionRangeEDataType = null;

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
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private ModulesPackageImpl()
  {
    super(eNS_URI, ModulesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link ModulesPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static ModulesPackage init()
  {
    if (isInited)
    {
      return (ModulesPackage)EPackage.Registry.INSTANCE.getEPackage(ModulesPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredModulesPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    ModulesPackageImpl theModulesPackage = registeredModulesPackage instanceof ModulesPackageImpl ? (ModulesPackageImpl)registeredModulesPackage
        : new ModulesPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theModulesPackage.createPackageContents();

    // Initialize created meta-data
    theModulesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModulesPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(ModulesPackage.eNS_URI, theModulesPackage);
    return theModulesPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModuleDefinition()
  {
    return moduleDefinitionEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleDefinition_Name()
  {
    return (EAttribute)moduleDefinitionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleDefinition_Version()
  {
    return (EAttribute)moduleDefinitionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getModuleDefinition_Dependencies()
  {
    return (EReference)moduleDefinitionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDependencyDefinition()
  {
    return dependencyDefinitionEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDependencyDefinition_Source()
  {
    return (EReference)dependencyDefinitionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDependencyDefinition_TargetName()
  {
    return (EAttribute)dependencyDefinitionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDependencyDefinition_VersionRange()
  {
    return (EAttribute)dependencyDefinitionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getVersion()
  {
    return versionEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getVersionRange()
  {
    return versionRangeEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModulesFactory getModulesFactory()
  {
    return (ModulesFactory)getEFactoryInstance();
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
    moduleDefinitionEClass = createEClass(MODULE_DEFINITION);
    createEAttribute(moduleDefinitionEClass, MODULE_DEFINITION__NAME);
    createEAttribute(moduleDefinitionEClass, MODULE_DEFINITION__VERSION);
    createEReference(moduleDefinitionEClass, MODULE_DEFINITION__DEPENDENCIES);

    dependencyDefinitionEClass = createEClass(DEPENDENCY_DEFINITION);
    createEReference(dependencyDefinitionEClass, DEPENDENCY_DEFINITION__SOURCE);
    createEAttribute(dependencyDefinitionEClass, DEPENDENCY_DEFINITION__TARGET_NAME);
    createEAttribute(dependencyDefinitionEClass, DEPENDENCY_DEFINITION__VERSION_RANGE);

    // Create data types
    versionEDataType = createEDataType(VERSION);
    versionRangeEDataType = createEDataType(VERSION_RANGE);
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

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    moduleDefinitionEClass.getESuperTypes().add(theEtypesPackage.getModelElement());
    dependencyDefinitionEClass.getESuperTypes().add(theEtypesPackage.getModelElement());

    // Initialize classes, features, and operations; add parameters
    initEClass(moduleDefinitionEClass, ModuleDefinition.class, "ModuleDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModuleDefinition_Name(), ecorePackage.getEString(), "name", null, 1, 1, ModuleDefinition.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleDefinition_Version(), getVersion(), "version", null, 1, 1, ModuleDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModuleDefinition_Dependencies(), getDependencyDefinition(), getDependencyDefinition_Source(), "dependencies", null, 0, -1,
        ModuleDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(dependencyDefinitionEClass, DependencyDefinition.class, "DependencyDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDependencyDefinition_Source(), getModuleDefinition(), getModuleDefinition_Dependencies(), "source", null, 1, 1,
        DependencyDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getDependencyDefinition_TargetName(), ecorePackage.getEString(), "targetName", null, 1, 1, DependencyDefinition.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDependencyDefinition_VersionRange(), getVersionRange(), "versionRange", null, 1, 1, DependencyDefinition.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(versionEDataType, Version.class, "Version", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(versionRangeEDataType, VersionRange.class, "VersionRange", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

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
    addAnnotation(getModuleDefinition_Dependencies(), source, new String[] { "name", "dependency", "kind", "element" });
  }

} // ModulesPackageImpl
