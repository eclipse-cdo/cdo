/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.impl;

import org.eclipse.emf.cdo.defs.CDOAuditDef;
import org.eclipse.emf.cdo.defs.CDOClientProtocolFactoryDef;
import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.CDOEagerPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOLazyPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOResourceDef;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.CDOTransactionDef;
import org.eclipse.emf.cdo.defs.CDOViewDef;
import org.eclipse.emf.cdo.defs.EDynamicPackageDef;
import org.eclipse.emf.cdo.defs.EGlobalPackageDef;
import org.eclipse.emf.cdo.defs.EPackageDef;
import org.eclipse.emf.cdo.defs.ResourceMode;

import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class CDODefsPackageImpl extends EPackageImpl implements CDODefsPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoViewDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoTransactionDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoAuditDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoSessionDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoPackageRegistryDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoEagerPackageRegistryDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoLazyPackageRegistryDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass ePackageDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass eDynamicPackageDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass eGlobalPackageDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoClientProtocolFactoryDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoResourceDefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EEnum resourceModeEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private CDODefsPackageImpl()
  {
    super(eNS_URI, CDODefsFactory.eINSTANCE);
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
   * This method is used to initialize {@link CDODefsPackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static CDODefsPackage init()
  {
    if (isInited)
      return (CDODefsPackage)EPackage.Registry.INSTANCE.getEPackage(CDODefsPackage.eNS_URI);

    // Obtain or create and register package
    CDODefsPackageImpl theCDODefsPackage = (CDODefsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof CDODefsPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new CDODefsPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    Net4jDefsPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theCDODefsPackage.createPackageContents();

    // Initialize created meta-data
    theCDODefsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theCDODefsPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(CDODefsPackage.eNS_URI, theCDODefsPackage);
    return theCDODefsPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOViewDef()
  {
    return cdoViewDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOViewDef_CdoSessionDef()
  {
    return (EReference)cdoViewDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOTransactionDef()
  {
    return cdoTransactionDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOAuditDef()
  {
    return cdoAuditDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOAuditDef_TimeStamp()
  {
    return (EAttribute)cdoAuditDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOSessionDef()
  {
    return cdoSessionDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOSessionDef_ConnectorDef()
  {
    return (EReference)cdoSessionDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOSessionDef_RepositoryName()
  {
    return (EAttribute)cdoSessionDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOSessionDef_CdoPackageRegistryDef()
  {
    return (EReference)cdoSessionDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOSessionDef_LegacySupportEnabled()
  {
    return (EAttribute)cdoSessionDefEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOPackageRegistryDef()
  {
    return cdoPackageRegistryDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOPackageRegistryDef_Packages()
  {
    return (EReference)cdoPackageRegistryDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOEagerPackageRegistryDef()
  {
    return cdoEagerPackageRegistryDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOLazyPackageRegistryDef()
  {
    return cdoLazyPackageRegistryDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getEPackageDef()
  {
    return ePackageDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getEPackageDef_NsURI()
  {
    return (EAttribute)ePackageDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getEDynamicPackageDef()
  {
    return eDynamicPackageDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getEDynamicPackageDef_ResourceURI()
  {
    return (EAttribute)eDynamicPackageDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getEGlobalPackageDef()
  {
    return eGlobalPackageDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOClientProtocolFactoryDef()
  {
    return cdoClientProtocolFactoryDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOResourceDef()
  {
    return cdoResourceDefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOResourceDef_CdoTransaction()
  {
    return (EReference)cdoResourceDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResourceDef_ResourceMode()
  {
    return (EAttribute)cdoResourceDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResourceDef_Path()
  {
    return (EAttribute)cdoResourceDefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EEnum getResourceMode()
  {
    return resourceModeEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDODefsFactory getCDODefsFactory()
  {
    return (CDODefsFactory)getEFactoryInstance();
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
      return;
    isCreated = true;

    // Create classes and their features
    cdoViewDefEClass = createEClass(CDO_VIEW_DEF);
    createEReference(cdoViewDefEClass, CDO_VIEW_DEF__CDO_SESSION_DEF);

    cdoTransactionDefEClass = createEClass(CDO_TRANSACTION_DEF);

    cdoAuditDefEClass = createEClass(CDO_AUDIT_DEF);
    createEAttribute(cdoAuditDefEClass, CDO_AUDIT_DEF__TIME_STAMP);

    cdoSessionDefEClass = createEClass(CDO_SESSION_DEF);
    createEReference(cdoSessionDefEClass, CDO_SESSION_DEF__CONNECTOR_DEF);
    createEAttribute(cdoSessionDefEClass, CDO_SESSION_DEF__REPOSITORY_NAME);
    createEReference(cdoSessionDefEClass, CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF);
    createEAttribute(cdoSessionDefEClass, CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED);

    cdoPackageRegistryDefEClass = createEClass(CDO_PACKAGE_REGISTRY_DEF);
    createEReference(cdoPackageRegistryDefEClass, CDO_PACKAGE_REGISTRY_DEF__PACKAGES);

    cdoEagerPackageRegistryDefEClass = createEClass(CDO_EAGER_PACKAGE_REGISTRY_DEF);

    cdoLazyPackageRegistryDefEClass = createEClass(CDO_LAZY_PACKAGE_REGISTRY_DEF);

    ePackageDefEClass = createEClass(EPACKAGE_DEF);
    createEAttribute(ePackageDefEClass, EPACKAGE_DEF__NS_URI);

    eDynamicPackageDefEClass = createEClass(EDYNAMIC_PACKAGE_DEF);
    createEAttribute(eDynamicPackageDefEClass, EDYNAMIC_PACKAGE_DEF__RESOURCE_URI);

    eGlobalPackageDefEClass = createEClass(EGLOBAL_PACKAGE_DEF);

    cdoClientProtocolFactoryDefEClass = createEClass(CDO_CLIENT_PROTOCOL_FACTORY_DEF);

    cdoResourceDefEClass = createEClass(CDO_RESOURCE_DEF);
    createEReference(cdoResourceDefEClass, CDO_RESOURCE_DEF__CDO_TRANSACTION);
    createEAttribute(cdoResourceDefEClass, CDO_RESOURCE_DEF__RESOURCE_MODE);
    createEAttribute(cdoResourceDefEClass, CDO_RESOURCE_DEF__PATH);

    // Create enums
    resourceModeEEnum = createEEnum(RESOURCE_MODE);
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
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    Net4jUtilDefsPackage theNet4jUtilDefsPackage = (Net4jUtilDefsPackage)EPackage.Registry.INSTANCE
        .getEPackage(Net4jUtilDefsPackage.eNS_URI);
    Net4jDefsPackage theNet4jDefsPackage = (Net4jDefsPackage)EPackage.Registry.INSTANCE
        .getEPackage(Net4jDefsPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    cdoViewDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
    cdoTransactionDefEClass.getESuperTypes().add(this.getCDOViewDef());
    cdoAuditDefEClass.getESuperTypes().add(this.getCDOViewDef());
    cdoSessionDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
    cdoPackageRegistryDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
    cdoEagerPackageRegistryDefEClass.getESuperTypes().add(this.getCDOPackageRegistryDef());
    cdoLazyPackageRegistryDefEClass.getESuperTypes().add(this.getCDOPackageRegistryDef());
    ePackageDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());
    eDynamicPackageDefEClass.getESuperTypes().add(this.getEPackageDef());
    eGlobalPackageDefEClass.getESuperTypes().add(this.getEPackageDef());
    cdoClientProtocolFactoryDefEClass.getESuperTypes().add(theNet4jDefsPackage.getClientProtocolFactoryDef());
    cdoResourceDefEClass.getESuperTypes().add(theNet4jUtilDefsPackage.getDef());

    // Initialize classes and features; add operations and parameters
    initEClass(cdoViewDefEClass, CDOViewDef.class, "CDOViewDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCDOViewDef_CdoSessionDef(), this.getCDOSessionDef(), null, "cdoSessionDef", null, 1, 1,
        CDOViewDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cdoTransactionDefEClass, CDOTransactionDef.class, "CDOTransactionDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(cdoAuditDefEClass, CDOAuditDef.class, "CDOAuditDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCDOAuditDef_TimeStamp(), ecorePackage.getEDate(), "timeStamp", null, 1, 1, CDOAuditDef.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cdoSessionDefEClass, CDOSessionDef.class, "CDOSessionDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCDOSessionDef_ConnectorDef(), theNet4jDefsPackage.getConnectorDef(), null, "connectorDef", null,
        1, 1, CDOSessionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOSessionDef_RepositoryName(), ecorePackage.getEString(), "repositoryName", null, 1, 1,
        CDOSessionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
        !IS_DERIVED, !IS_ORDERED);
    initEReference(getCDOSessionDef_CdoPackageRegistryDef(), this.getCDOPackageRegistryDef(), null,
        "cdoPackageRegistryDef", null, 1, 1, CDOSessionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOSessionDef_LegacySupportEnabled(), ecorePackage.getEBoolean(), "legacySupportEnabled",
        "false", 1, 1, CDOSessionDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cdoPackageRegistryDefEClass, CDOPackageRegistryDef.class, "CDOPackageRegistryDef", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCDOPackageRegistryDef_Packages(), this.getEPackageDef(), null, "packages", null, 0, -1,
        CDOPackageRegistryDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cdoEagerPackageRegistryDefEClass, CDOEagerPackageRegistryDef.class, "CDOEagerPackageRegistryDef",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(cdoLazyPackageRegistryDefEClass, CDOLazyPackageRegistryDef.class, "CDOLazyPackageRegistryDef",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(ePackageDefEClass, EPackageDef.class, "EPackageDef", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEPackageDef_NsURI(), ecorePackage.getEString(), "nsURI", null, 1, 1, EPackageDef.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eDynamicPackageDefEClass, EDynamicPackageDef.class, "EDynamicPackageDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEDynamicPackageDef_ResourceURI(), ecorePackage.getEString(), "resourceURI", null, 1, 1,
        EDynamicPackageDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(eGlobalPackageDefEClass, EGlobalPackageDef.class, "EGlobalPackageDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(cdoClientProtocolFactoryDefEClass, CDOClientProtocolFactoryDef.class, "CDOClientProtocolFactoryDef",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(cdoResourceDefEClass, CDOResourceDef.class, "CDOResourceDef", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCDOResourceDef_CdoTransaction(), this.getCDOTransactionDef(), null, "cdoTransaction", null, 1, 1,
        CDOResourceDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResourceDef_ResourceMode(), this.getResourceMode(), "resourceMode", "null", 1, 1,
        CDOResourceDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCDOResourceDef_Path(), ecorePackage.getEString(), "path", null, 1, 1, CDOResourceDef.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(resourceModeEEnum, ResourceMode.class, "ResourceMode");
    addEEnumLiteral(resourceModeEEnum, ResourceMode.GET);
    addEEnumLiteral(resourceModeEEnum, ResourceMode.CREATE);
    addEEnumLiteral(resourceModeEEnum, ResourceMode.GET_OR_CREATE);

    // Create resource
    createResource(eNS_URI);
  }

} // CDODefsPackageImpl
