/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.Model6Factory;
import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.tests.model6.UnorderedList;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model6PackageImpl extends EPackageImpl implements Model6Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass rootEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass baseObjectEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass referenceObjectEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass containmentObjectEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass unorderedListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertiesMapEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertiesMapEntryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertiesMapEntryValueEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass aEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass bEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass cEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass dEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass eEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass fEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass gEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model6PackageImpl()
  {
    super(eNS_URI, Model6Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link Model6Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model6Package init()
  {
    if (isInited)
      return (Model6Package)EPackage.Registry.INSTANCE.getEPackage(Model6Package.eNS_URI);

    // Obtain or create and register package
    Model6PackageImpl theModel6Package = (Model6PackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof Model6PackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new Model6PackageImpl());

    isInited = true;

    // Create package meta-data objects
    theModel6Package.createPackageContents();

    // Initialize created meta-data
    theModel6Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel6Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Model6Package.eNS_URI, theModel6Package);
    return theModel6Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getRoot()
  {
    return rootEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_ListA()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_ListB()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_ListC()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_ListD()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getBaseObject()
  {
    return baseObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBaseObject_AttributeOptional()
  {
    return (EAttribute)baseObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBaseObject_AttributeRequired()
  {
    return (EAttribute)baseObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBaseObject_AttributeList()
  {
    return (EAttribute)baseObjectEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getReferenceObject()
  {
    return referenceObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getReferenceObject_ReferenceOptional()
  {
    return (EReference)referenceObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getReferenceObject_ReferenceList()
  {
    return (EReference)referenceObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getContainmentObject()
  {
    return containmentObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getContainmentObject_ContainmentOptional()
  {
    return (EReference)containmentObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getContainmentObject_ContainmentList()
  {
    return (EReference)containmentObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getUnorderedList()
  {
    return unorderedListEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getUnorderedList_Contained()
  {
    return (EReference)unorderedListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getUnorderedList_Referenced()
  {
    return (EReference)unorderedListEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPropertiesMap()
  {
    return propertiesMapEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPropertiesMap_Label()
  {
    return (EAttribute)propertiesMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPropertiesMap_PersistentMap()
  {
    return (EReference)propertiesMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPropertiesMap_TransientMap()
  {
    return (EReference)propertiesMapEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPropertiesMapEntry()
  {
    return propertiesMapEntryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPropertiesMapEntry_Key()
  {
    return (EAttribute)propertiesMapEntryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPropertiesMapEntry_Value()
  {
    return (EReference)propertiesMapEntryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPropertiesMapEntryValue()
  {
    return propertiesMapEntryValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPropertiesMapEntryValue_Label()
  {
    return (EAttribute)propertiesMapEntryValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getA()
  {
    return aEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getA_OwnedDs()
  {
    return (EReference)aEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getA_OwnedBs()
  {
    return (EReference)aEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getB()
  {
    return bEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getB_OwnedC()
  {
    return (EReference)bEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getC()
  {
    return cEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getD()
  {
    return dEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getD_Data()
  {
    return (EReference)dEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getE()
  {
    return eEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getE_OwnedAs()
  {
    return (EReference)eEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getF()
  {
    return fEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getF_OwnedEs()
  {
    return (EReference)fEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getG()
  {
    return gEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getG_Dummy()
  {
    return (EAttribute)gEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getG_Reference()
  {
    return (EReference)gEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getG_List()
  {
    return (EReference)gEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model6Factory getModel6Factory()
  {
    return (Model6Factory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    rootEClass = createEClass(ROOT);
    createEReference(rootEClass, ROOT__LIST_A);
    createEReference(rootEClass, ROOT__LIST_B);
    createEReference(rootEClass, ROOT__LIST_C);
    createEReference(rootEClass, ROOT__LIST_D);

    baseObjectEClass = createEClass(BASE_OBJECT);
    createEAttribute(baseObjectEClass, BASE_OBJECT__ATTRIBUTE_OPTIONAL);
    createEAttribute(baseObjectEClass, BASE_OBJECT__ATTRIBUTE_REQUIRED);
    createEAttribute(baseObjectEClass, BASE_OBJECT__ATTRIBUTE_LIST);

    referenceObjectEClass = createEClass(REFERENCE_OBJECT);
    createEReference(referenceObjectEClass, REFERENCE_OBJECT__REFERENCE_OPTIONAL);
    createEReference(referenceObjectEClass, REFERENCE_OBJECT__REFERENCE_LIST);

    containmentObjectEClass = createEClass(CONTAINMENT_OBJECT);
    createEReference(containmentObjectEClass, CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL);
    createEReference(containmentObjectEClass, CONTAINMENT_OBJECT__CONTAINMENT_LIST);

    unorderedListEClass = createEClass(UNORDERED_LIST);
    createEReference(unorderedListEClass, UNORDERED_LIST__CONTAINED);
    createEReference(unorderedListEClass, UNORDERED_LIST__REFERENCED);

    propertiesMapEClass = createEClass(PROPERTIES_MAP);
    createEAttribute(propertiesMapEClass, PROPERTIES_MAP__LABEL);
    createEReference(propertiesMapEClass, PROPERTIES_MAP__PERSISTENT_MAP);
    createEReference(propertiesMapEClass, PROPERTIES_MAP__TRANSIENT_MAP);

    propertiesMapEntryEClass = createEClass(PROPERTIES_MAP_ENTRY);
    createEAttribute(propertiesMapEntryEClass, PROPERTIES_MAP_ENTRY__KEY);
    createEReference(propertiesMapEntryEClass, PROPERTIES_MAP_ENTRY__VALUE);

    propertiesMapEntryValueEClass = createEClass(PROPERTIES_MAP_ENTRY_VALUE);
    createEAttribute(propertiesMapEntryValueEClass, PROPERTIES_MAP_ENTRY_VALUE__LABEL);

    aEClass = createEClass(A);
    createEReference(aEClass, A__OWNED_DS);
    createEReference(aEClass, A__OWNED_BS);

    bEClass = createEClass(B);
    createEReference(bEClass, B__OWNED_C);

    cEClass = createEClass(C);

    dEClass = createEClass(D);
    createEReference(dEClass, D__DATA);

    eEClass = createEClass(E);
    createEReference(eEClass, E__OWNED_AS);

    fEClass = createEClass(F);
    createEReference(fEClass, F__OWNED_ES);

    gEClass = createEClass(G);
    createEAttribute(gEClass, G__DUMMY);
    createEReference(gEClass, G__REFERENCE);
    createEReference(gEClass, G__LIST);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    referenceObjectEClass.getESuperTypes().add(this.getBaseObject());
    containmentObjectEClass.getESuperTypes().add(this.getBaseObject());

    // Initialize classes and features; add operations and parameters
    initEClass(rootEClass, Root.class, "Root", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRoot_ListA(), this.getBaseObject(), null, "listA", null, 0, -1, Root.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getRoot_ListB(), this.getBaseObject(), null, "listB", null, 0, -1, Root.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getRoot_ListC(), this.getBaseObject(), null, "listC", null, 0, -1, Root.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getRoot_ListD(), this.getBaseObject(), null, "listD", null, 0, -1, Root.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(baseObjectEClass, BaseObject.class, "BaseObject", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBaseObject_AttributeOptional(), ecorePackage.getEString(), "attributeOptional", null, 0, 1,
        BaseObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getBaseObject_AttributeRequired(), ecorePackage.getEString(), "attributeRequired", null, 1, 1,
        BaseObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getBaseObject_AttributeList(), ecorePackage.getEString(), "attributeList", null, 0, -1,
        BaseObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(referenceObjectEClass, ReferenceObject.class, "ReferenceObject", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getReferenceObject_ReferenceOptional(), this.getBaseObject(), null, "referenceOptional", null, 0, 1,
        ReferenceObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getReferenceObject_ReferenceList(), this.getBaseObject(), null, "referenceList", null, 0, -1,
        ReferenceObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(containmentObjectEClass, ContainmentObject.class, "ContainmentObject", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getContainmentObject_ContainmentOptional(), this.getBaseObject(), null, "containmentOptional", null,
        0, 1, ContainmentObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getContainmentObject_ContainmentList(), this.getBaseObject(), null, "containmentList", null, 0, -1,
        ContainmentObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unorderedListEClass, UnorderedList.class, "UnorderedList", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getUnorderedList_Contained(), this.getUnorderedList(), null, "contained", null, 0, -1,
        UnorderedList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
    initEReference(getUnorderedList_Referenced(), this.getUnorderedList(), null, "referenced", null, 0, -1,
        UnorderedList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

    initEClass(propertiesMapEClass, PropertiesMap.class, "PropertiesMap", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertiesMap_Label(), ecorePackage.getEString(), "label", null, 0, 1, PropertiesMap.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertiesMap_PersistentMap(), this.getPropertiesMapEntry(), null, "persistentMap", null, 0, -1,
        PropertiesMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertiesMap_TransientMap(), this.getPropertiesMapEntry(), null, "transientMap", null, 0, -1,
        PropertiesMap.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertiesMapEntryEClass, Map.Entry.class, "PropertiesMapEntry", !IS_ABSTRACT, !IS_INTERFACE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertiesMapEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertiesMapEntry_Value(), this.getPropertiesMapEntryValue(), null, "value", null, 0, 1,
        Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertiesMapEntryValueEClass, PropertiesMapEntryValue.class, "PropertiesMapEntryValue", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertiesMapEntryValue_Label(), ecorePackage.getEString(), "label", null, 0, 1,
        PropertiesMapEntryValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(aEClass, org.eclipse.emf.cdo.tests.model6.A.class, "A", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getA_OwnedDs(), this.getD(), null, "ownedDs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.A.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getA_OwnedBs(), this.getB(), null, "ownedBs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.A.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(bEClass, org.eclipse.emf.cdo.tests.model6.B.class, "B", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getB_OwnedC(), this.getC(), null, "ownedC", null, 0, 1, org.eclipse.emf.cdo.tests.model6.B.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(cEClass, org.eclipse.emf.cdo.tests.model6.C.class, "C", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(dEClass, org.eclipse.emf.cdo.tests.model6.D.class, "D", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getD_Data(), ecorePackage.getEObject(), null, "data", null, 0, 1,
        org.eclipse.emf.cdo.tests.model6.D.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eEClass, org.eclipse.emf.cdo.tests.model6.E.class, "E", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getE_OwnedAs(), this.getA(), null, "ownedAs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.E.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(fEClass, org.eclipse.emf.cdo.tests.model6.F.class, "F", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getF_OwnedEs(), this.getE(), null, "ownedEs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.F.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(gEClass, org.eclipse.emf.cdo.tests.model6.G.class, "G", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getG_Dummy(), ecorePackage.getEString(), "dummy", null, 1, 1,
        org.eclipse.emf.cdo.tests.model6.G.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getG_Reference(), this.getBaseObject(), null, "reference", null, 1, 1,
        org.eclipse.emf.cdo.tests.model6.G.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getG_List(), this.getBaseObject(), null, "list", null, 0, -1,
        org.eclipse.emf.cdo.tests.model6.G.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(gEClass, ecorePackage.getEBoolean(), "isAttributeModified", 1, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(gEClass, ecorePackage.getEBoolean(), "isReferenceModified", 1, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(gEClass, ecorePackage.getEBoolean(), "isListModified", 1, 1, IS_UNIQUE, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // Model6PackageImpl
