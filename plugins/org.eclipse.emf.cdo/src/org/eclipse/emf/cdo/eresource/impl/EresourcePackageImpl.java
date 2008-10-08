/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class EresourcePackageImpl extends EPackageImpl implements EresourcePackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoResourceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType resourceSetEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType uriEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType diagnosticEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EresourcePackageImpl()
  {
    super(eNS_URI, EresourceFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * Simple dependencies are satisfied by calling this method on all dependent packages before doing anything else. This
   * method drives initialization for interdependent packages directly, in parallel with this package, itself.
   * <p>
   * Of this package and its interdependencies, all packages which have not yet been registered by their URI values are
   * first created and registered. The packages are then initialized in two steps: meta-model objects for all of the
   * packages are created before any are initialized, since one package's meta-model objects may refer to those of
   * another.
   * <p>
   * Invocation of this method will not affect any packages that have already been initialized. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EresourcePackage init()
  {
    if (isInited) return (EresourcePackage)EPackage.Registry.INSTANCE.getEPackage(EresourcePackage.eNS_URI);

    // Obtain or create and register package
    EresourcePackageImpl theEresourcePackage = (EresourcePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof EresourcePackageImpl ? EPackage.Registry.INSTANCE
        .getEPackage(eNS_URI)
        : new EresourcePackageImpl());

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theEresourcePackage.createPackageContents();

    // Initialize created meta-data
    theEresourcePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEresourcePackage.freeze();

    return theEresourcePackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOResource()
  {
    return cdoResourceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_ResourceSet()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_URI()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOResource_Contents()
  {
    return (EReference)cdoResourceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Modified()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Loaded()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_TrackingModification()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Errors()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Warnings()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_TimeStamp()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Path()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getResourceSet()
  {
    return resourceSetEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getURI()
  {
    return uriEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getDiagnostic()
  {
    return diagnosticEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EresourceFactory getEresourceFactory()
  {
    return (EresourceFactory)getEFactoryInstance();
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
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    cdoResourceEClass = createEClass(CDO_RESOURCE);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__RESOURCE_SET);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__URI);
    createEReference(cdoResourceEClass, CDO_RESOURCE__CONTENTS);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__MODIFIED);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__LOADED);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__TRACKING_MODIFICATION);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__ERRORS);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__WARNINGS);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__TIME_STAMP);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__PATH);

    // Create data types
    resourceSetEDataType = createEDataType(RESOURCE_SET);
    uriEDataType = createEDataType(URI);
    diagnosticEDataType = createEDataType(DIAGNOSTIC);
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
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(cdoResourceEClass, CDOResource.class, "CDOResource", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCDOResource_ResourceSet(), this.getResourceSet(), "resourceSet", null, 0, 1, CDOResource.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_URI(), this.getURI(), "uRI", null, 0, 1, CDOResource.class, IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCDOResource_Contents(), theEcorePackage.getEObject(), null, "contents", null, 0, -1,
        CDOResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_Modified(), ecorePackage.getEBoolean(), "modified", null, 0, 1, CDOResource.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_Loaded(), ecorePackage.getEBoolean(), "loaded", "true", 0, 1, CDOResource.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_TrackingModification(), ecorePackage.getEBoolean(), "trackingModification", null, 0,
        1, CDOResource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_Errors(), this.getDiagnostic(), "errors", null, 0, -1, CDOResource.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_Warnings(), this.getDiagnostic(), "warnings", null, 0, -1, CDOResource.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_TimeStamp(), theEcorePackage.getELong(), "timeStamp", null, 0, 1, CDOResource.class,
        IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResource_Path(), ecorePackage.getEString(), "path", null, 0, 1, CDOResource.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(resourceSetEDataType, ResourceSet.class, "ResourceSet", !IS_SERIALIZABLE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(diagnosticEDataType, Diagnostic.class, "Diagnostic", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // EresourcePackageImpl
