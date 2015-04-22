/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.impl;

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClassChild;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.AParameter;
import org.eclipse.emf.cdo.dawn.examples.acore.AccessType;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class AcorePackageImpl extends EPackageImpl implements AcorePackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aClassEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aInterfaceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aCoreRootEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aAttributeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aOperationEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aBasicClassEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aParameterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass aClassChildEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EEnum accessTypeEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EDataType accessTypeObjectEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#eNS_URI
   * @see #init()
   * @generated
   */
  private AcorePackageImpl()
  {
    super(eNS_URI, AcoreFactory.eINSTANCE);
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
   * This method is used to initialize {@link AcorePackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static AcorePackage init()
  {
    if (isInited)
    {
      return (AcorePackage)EPackage.Registry.INSTANCE.getEPackage(AcorePackage.eNS_URI);
    }

    // Obtain or create and register package
    AcorePackageImpl theAcorePackage = (AcorePackageImpl)(EPackage.Registry.INSTANCE
        .get(eNS_URI) instanceof AcorePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new AcorePackageImpl());

    isInited = true;

    // Create package meta-data objects
    theAcorePackage.createPackageContents();

    // Initialize created meta-data
    theAcorePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theAcorePackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(AcorePackage.eNS_URI, theAcorePackage);
    return theAcorePackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getAClass()
  {
    return aClassEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getAClass_SubClasses()
  {
    return (EReference)aClassEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getAClass_ImplementedInterfaces()
  {
    return (EReference)aClassEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getAClass_Associations()
  {
    return (EReference)aClassEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getAClass_Compositions()
  {
    return (EReference)aClassEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getAClass_Aggregations()
  {
    return (EReference)aClassEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getAInterface()
  {
    return aInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getACoreRoot()
  {
    return aCoreRootEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getACoreRoot_Title()
  {
    return (EAttribute)aCoreRootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getACoreRoot_Classes()
  {
    return (EReference)aCoreRootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getACoreRoot_Interfaces()
  {
    return (EReference)aCoreRootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getAAttribute()
  {
    return aAttributeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getAOperation()
  {
    return aOperationEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getAOperation_Parameters()
  {
    return (EReference)aOperationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getABasicClass()
  {
    return aBasicClassEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getABasicClass_Operations()
  {
    return (EReference)aBasicClassEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EReference getABasicClass_Attributes()
  {
    return (EReference)aBasicClassEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getABasicClass_Name()
  {
    return (EAttribute)aBasicClassEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getAParameter()
  {
    return aParameterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getAParameter_Name()
  {
    return (EAttribute)aParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getAParameter_Type()
  {
    return (EAttribute)aParameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EClass getAClassChild()
  {
    return aClassChildEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getAClassChild_Name()
  {
    return (EAttribute)aClassChildEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getAClassChild_Accessright()
  {
    return (EAttribute)aClassChildEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EAttribute getAClassChild_DataType()
  {
    return (EAttribute)aClassChildEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EEnum getAccessType()
  {
    return accessTypeEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EDataType getAccessTypeObject()
  {
    return accessTypeObjectEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AcoreFactory getAcoreFactory()
  {
    return (AcoreFactory)getEFactoryInstance();
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
    aClassEClass = createEClass(ACLASS);
    createEReference(aClassEClass, ACLASS__SUB_CLASSES);
    createEReference(aClassEClass, ACLASS__IMPLEMENTED_INTERFACES);
    createEReference(aClassEClass, ACLASS__ASSOCIATIONS);
    createEReference(aClassEClass, ACLASS__COMPOSITIONS);
    createEReference(aClassEClass, ACLASS__AGGREGATIONS);

    aInterfaceEClass = createEClass(AINTERFACE);

    aCoreRootEClass = createEClass(ACORE_ROOT);
    createEAttribute(aCoreRootEClass, ACORE_ROOT__TITLE);
    createEReference(aCoreRootEClass, ACORE_ROOT__CLASSES);
    createEReference(aCoreRootEClass, ACORE_ROOT__INTERFACES);

    aAttributeEClass = createEClass(AATTRIBUTE);

    aOperationEClass = createEClass(AOPERATION);
    createEReference(aOperationEClass, AOPERATION__PARAMETERS);

    aBasicClassEClass = createEClass(ABASIC_CLASS);
    createEReference(aBasicClassEClass, ABASIC_CLASS__OPERATIONS);
    createEReference(aBasicClassEClass, ABASIC_CLASS__ATTRIBUTES);
    createEAttribute(aBasicClassEClass, ABASIC_CLASS__NAME);

    aParameterEClass = createEClass(APARAMETER);
    createEAttribute(aParameterEClass, APARAMETER__NAME);
    createEAttribute(aParameterEClass, APARAMETER__TYPE);

    aClassChildEClass = createEClass(ACLASS_CHILD);
    createEAttribute(aClassChildEClass, ACLASS_CHILD__NAME);
    createEAttribute(aClassChildEClass, ACLASS_CHILD__ACCESSRIGHT);
    createEAttribute(aClassChildEClass, ACLASS_CHILD__DATA_TYPE);

    // Create enums
    accessTypeEEnum = createEEnum(ACCESS_TYPE);

    // Create data types
    accessTypeObjectEDataType = createEDataType(ACCESS_TYPE_OBJECT);
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

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    aClassEClass.getESuperTypes().add(getABasicClass());
    aInterfaceEClass.getESuperTypes().add(getABasicClass());
    aAttributeEClass.getESuperTypes().add(getAClassChild());
    aOperationEClass.getESuperTypes().add(getAClassChild());

    // Initialize classes and features; add operations and parameters
    initEClass(aClassEClass, AClass.class, "AClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAClass_SubClasses(), getAClass(), null, "subClasses", null, 0, -1, AClass.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getAClass_ImplementedInterfaces(), getAInterface(), null, "implementedInterfaces", null, 0, -1,
        AClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAClass_Associations(), getAClass(), null, "associations", null, 0, -1, AClass.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getAClass_Compositions(), getAClass(), null, "compositions", null, 0, -1, AClass.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getAClass_Aggregations(), getAClass(), null, "aggregations", null, 0, -1, AClass.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(aInterfaceEClass, AInterface.class, "AInterface", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(aCoreRootEClass, ACoreRoot.class, "ACoreRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getACoreRoot_Title(), ecorePackage.getEString(), "title", null, 0, 1, ACoreRoot.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getACoreRoot_Classes(), getAClass(), null, "classes", null, 0, -1, ACoreRoot.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getACoreRoot_Interfaces(), getAInterface(), null, "interfaces", null, 0, -1, ACoreRoot.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(aAttributeEClass, AAttribute.class, "AAttribute", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(aOperationEClass, AOperation.class, "AOperation", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAOperation_Parameters(), getAParameter(), null, "parameters", null, 0, -1, AOperation.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(aBasicClassEClass, ABasicClass.class, "ABasicClass", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getABasicClass_Operations(), getAOperation(), null, "operations", null, 0, -1, ABasicClass.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getABasicClass_Attributes(), getAAttribute(), null, "attributes", null, 0, -1, ABasicClass.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getABasicClass_Name(), ecorePackage.getEString(), "name", null, 0, 1, ABasicClass.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(aParameterEClass, AParameter.class, "AParameter", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, AParameter.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAParameter_Type(), ecorePackage.getEString(), "type", null, 0, 1, AParameter.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(aClassChildEClass, AClassChild.class, "AClassChild", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAClassChild_Name(), ecorePackage.getEString(), "name", "", 0, 1, AClassChild.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAClassChild_Accessright(), getAccessType(), "accessright", "public", 0, 1, AClassChild.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAClassChild_DataType(), ecorePackage.getEString(), "dataType", null, 0, 1, AClassChild.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(accessTypeEEnum, AccessType.class, "AccessType");
    addEEnumLiteral(accessTypeEEnum, AccessType.PUBLIC);
    addEEnumLiteral(accessTypeEEnum, AccessType.PRIVATE);
    addEEnumLiteral(accessTypeEEnum, AccessType.PROECTED);
    addEEnumLiteral(accessTypeEEnum, AccessType.PACKAGE);

    // Initialize data types
    initEDataType(accessTypeObjectEDataType, AccessType.class, "AccessTypeObject", IS_SERIALIZABLE,
        IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    addAnnotation(accessTypeEEnum, source, new String[] { "name", "AccessType" });
    addAnnotation(accessTypeObjectEDataType, source,
        new String[] { "name", "AccessType:Object", "baseType", "AccessType" });
  }

} // AcorePackageImpl
