/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.security.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.lm.security.LMFilter;
import org.eclipse.emf.cdo.lm.security.LMSecurityFactory;
import org.eclipse.emf.cdo.lm.security.LMSecurityPackage;
import org.eclipse.emf.cdo.lm.security.ModuleFilter;
import org.eclipse.emf.cdo.lm.security.ModuleTypeFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LMSecurityPackageImpl extends EPackageImpl implements LMSecurityPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass lmFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleTypeFilterEClass = null;

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
   * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private LMSecurityPackageImpl()
  {
    super(eNS_URI, LMSecurityFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link LMSecurityPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static LMSecurityPackage init()
  {
    if (isInited)
    {
      return (LMSecurityPackage)EPackage.Registry.INSTANCE.getEPackage(LMSecurityPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredLMSecurityPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    LMSecurityPackageImpl theLMSecurityPackage = registeredLMSecurityPackage instanceof LMSecurityPackageImpl
        ? (LMSecurityPackageImpl)registeredLMSecurityPackage
        : new LMSecurityPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();
    EtypesPackage.eINSTANCE.eClass();
    ExpressionsPackage.eINSTANCE.eClass();
    SecurityPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theLMSecurityPackage.createPackageContents();

    // Initialize created meta-data
    theLMSecurityPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theLMSecurityPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(LMSecurityPackage.eNS_URI, theLMSecurityPackage);
    return theLMSecurityPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLMFilter()
  {
    return lmFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getLMFilter_Regex()
  {
    return (EAttribute)lmFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModuleFilter()
  {
    return moduleFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleFilter_Module()
  {
    return (EAttribute)moduleFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModuleTypeFilter()
  {
    return moduleTypeFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleTypeFilter_ModuleType()
  {
    return (EAttribute)moduleTypeFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleTypeFilter_IncludeUntyped()
  {
    return (EAttribute)moduleTypeFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public LMSecurityFactory getLMSecurityFactory()
  {
    return (LMSecurityFactory)getEFactoryInstance();
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
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    lmFilterEClass = createEClass(LM_FILTER);
    createEAttribute(lmFilterEClass, LM_FILTER__REGEX);

    moduleFilterEClass = createEClass(MODULE_FILTER);
    createEAttribute(moduleFilterEClass, MODULE_FILTER__MODULE);

    moduleTypeFilterEClass = createEClass(MODULE_TYPE_FILTER);
    createEAttribute(moduleTypeFilterEClass, MODULE_TYPE_FILTER__MODULE_TYPE);
    createEAttribute(moduleTypeFilterEClass, MODULE_TYPE_FILTER__INCLUDE_UNTYPED);
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
    SecurityPackage theSecurityPackage = (SecurityPackage)EPackage.Registry.INSTANCE.getEPackage(SecurityPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    lmFilterEClass.getESuperTypes().add(theSecurityPackage.getPermissionFilter());
    moduleFilterEClass.getESuperTypes().add(getLMFilter());
    moduleTypeFilterEClass.getESuperTypes().add(getLMFilter());

    // Initialize classes, features, and operations; add parameters
    initEClass(lmFilterEClass, LMFilter.class, "LMFilter", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLMFilter_Regex(), ecorePackage.getEBoolean(), "regex", null, 0, 1, LMFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleFilterEClass, ModuleFilter.class, "ModuleFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModuleFilter_Module(), ecorePackage.getEString(), "module", null, 0, 1, ModuleFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleTypeFilterEClass, ModuleTypeFilter.class, "ModuleTypeFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModuleTypeFilter_ModuleType(), ecorePackage.getEString(), "moduleType", null, 0, 1, ModuleTypeFilter.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModuleTypeFilter_IncludeUntyped(), ecorePackage.getEBoolean(), "includeUntyped", null, 0, 1, ModuleTypeFilter.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // LMSecurityPackageImpl
