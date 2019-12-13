/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl;

import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.INamedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesFactory;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class model4interfacesPackageImpl extends EPackageImpl implements model4interfacesPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iSingleRefContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iSingleRefContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iSingleRefNonContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iSingleRefNonContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iMultiRefContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iMultiRefContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iMultiRefNonContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iMultiRefNonContainedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iNamedElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iContainedElementNoParentLinkEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iSingleRefContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iSingleRefNonContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iMultiRefContainerNPLEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass iMultiRefNonContainerNPLEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private model4interfacesPackageImpl()
  {
    super(eNS_URI, model4interfacesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link model4interfacesPackage#eINSTANCE} when that field is accessed. Clients
   * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static model4interfacesPackage init()
  {
    if (isInited)
    {
      return (model4interfacesPackage)EPackage.Registry.INSTANCE.getEPackage(model4interfacesPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredmodel4interfacesPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    model4interfacesPackageImpl themodel4interfacesPackage = registeredmodel4interfacesPackage instanceof model4interfacesPackageImpl
        ? (model4interfacesPackageImpl)registeredmodel4interfacesPackage
        : new model4interfacesPackageImpl();

    isInited = true;

    // Create package meta-data objects
    themodel4interfacesPackage.createPackageContents();

    // Initialize created meta-data
    themodel4interfacesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    themodel4interfacesPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(model4interfacesPackage.eNS_URI, themodel4interfacesPackage);
    return themodel4interfacesPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getISingleRefContainer()
  {
    return iSingleRefContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getISingleRefContainer_Element()
  {
    return (EReference)iSingleRefContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getISingleRefContainedElement()
  {
    return iSingleRefContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getISingleRefContainedElement_Parent()
  {
    return (EReference)iSingleRefContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getISingleRefNonContainer()
  {
    return iSingleRefNonContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getISingleRefNonContainer_Element()
  {
    return (EReference)iSingleRefNonContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getISingleRefNonContainedElement()
  {
    return iSingleRefNonContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getISingleRefNonContainedElement_Parent()
  {
    return (EReference)iSingleRefNonContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIMultiRefContainer()
  {
    return iMultiRefContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIMultiRefContainer_Elements()
  {
    return (EReference)iMultiRefContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIMultiRefContainedElement()
  {
    return iMultiRefContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIMultiRefContainedElement_Parent()
  {
    return (EReference)iMultiRefContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIMultiRefNonContainer()
  {
    return iMultiRefNonContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIMultiRefNonContainer_Elements()
  {
    return (EReference)iMultiRefNonContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIMultiRefNonContainedElement()
  {
    return iMultiRefNonContainedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIMultiRefNonContainedElement_Parent()
  {
    return (EReference)iMultiRefNonContainedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getINamedElement()
  {
    return iNamedElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getINamedElement_Name()
  {
    return (EAttribute)iNamedElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIContainedElementNoParentLink()
  {
    return iContainedElementNoParentLinkEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getISingleRefContainerNPL()
  {
    return iSingleRefContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getISingleRefContainerNPL_Element()
  {
    return (EReference)iSingleRefContainerNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getISingleRefNonContainerNPL()
  {
    return iSingleRefNonContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getISingleRefNonContainerNPL_Element()
  {
    return (EReference)iSingleRefNonContainerNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIMultiRefContainerNPL()
  {
    return iMultiRefContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIMultiRefContainerNPL_Elements()
  {
    return (EReference)iMultiRefContainerNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIMultiRefNonContainerNPL()
  {
    return iMultiRefNonContainerNPLEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIMultiRefNonContainerNPL_Elements()
  {
    return (EReference)iMultiRefNonContainerNPLEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public model4interfacesFactory getmodel4interfacesFactory()
  {
    return (model4interfacesFactory)getEFactoryInstance();
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
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    iSingleRefContainerEClass = createEClass(ISINGLE_REF_CONTAINER);
    createEReference(iSingleRefContainerEClass, ISINGLE_REF_CONTAINER__ELEMENT);

    iSingleRefContainedElementEClass = createEClass(ISINGLE_REF_CONTAINED_ELEMENT);
    createEReference(iSingleRefContainedElementEClass, ISINGLE_REF_CONTAINED_ELEMENT__PARENT);

    iSingleRefNonContainerEClass = createEClass(ISINGLE_REF_NON_CONTAINER);
    createEReference(iSingleRefNonContainerEClass, ISINGLE_REF_NON_CONTAINER__ELEMENT);

    iSingleRefNonContainedElementEClass = createEClass(ISINGLE_REF_NON_CONTAINED_ELEMENT);
    createEReference(iSingleRefNonContainedElementEClass, ISINGLE_REF_NON_CONTAINED_ELEMENT__PARENT);

    iMultiRefContainerEClass = createEClass(IMULTI_REF_CONTAINER);
    createEReference(iMultiRefContainerEClass, IMULTI_REF_CONTAINER__ELEMENTS);

    iMultiRefContainedElementEClass = createEClass(IMULTI_REF_CONTAINED_ELEMENT);
    createEReference(iMultiRefContainedElementEClass, IMULTI_REF_CONTAINED_ELEMENT__PARENT);

    iMultiRefNonContainerEClass = createEClass(IMULTI_REF_NON_CONTAINER);
    createEReference(iMultiRefNonContainerEClass, IMULTI_REF_NON_CONTAINER__ELEMENTS);

    iMultiRefNonContainedElementEClass = createEClass(IMULTI_REF_NON_CONTAINED_ELEMENT);
    createEReference(iMultiRefNonContainedElementEClass, IMULTI_REF_NON_CONTAINED_ELEMENT__PARENT);

    iNamedElementEClass = createEClass(INAMED_ELEMENT);
    createEAttribute(iNamedElementEClass, INAMED_ELEMENT__NAME);

    iContainedElementNoParentLinkEClass = createEClass(ICONTAINED_ELEMENT_NO_PARENT_LINK);

    iSingleRefContainerNPLEClass = createEClass(ISINGLE_REF_CONTAINER_NPL);
    createEReference(iSingleRefContainerNPLEClass, ISINGLE_REF_CONTAINER_NPL__ELEMENT);

    iSingleRefNonContainerNPLEClass = createEClass(ISINGLE_REF_NON_CONTAINER_NPL);
    createEReference(iSingleRefNonContainerNPLEClass, ISINGLE_REF_NON_CONTAINER_NPL__ELEMENT);

    iMultiRefContainerNPLEClass = createEClass(IMULTI_REF_CONTAINER_NPL);
    createEReference(iMultiRefContainerNPLEClass, IMULTI_REF_CONTAINER_NPL__ELEMENTS);

    iMultiRefNonContainerNPLEClass = createEClass(IMULTI_REF_NON_CONTAINER_NPL);
    createEReference(iMultiRefNonContainerNPLEClass, IMULTI_REF_NON_CONTAINER_NPL__ELEMENTS);
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

    // Initialize classes and features; add operations and parameters
    initEClass(iSingleRefContainerEClass, ISingleRefContainer.class, "ISingleRefContainer", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISingleRefContainer_Element(), getISingleRefContainedElement(), getISingleRefContainedElement_Parent(), "element", null, 0, 1,
        ISingleRefContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(iSingleRefContainedElementEClass, ISingleRefContainedElement.class, "ISingleRefContainedElement", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISingleRefContainedElement_Parent(), getISingleRefContainer(), getISingleRefContainer_Element(), "parent", null, 0, 1,
        ISingleRefContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(iSingleRefNonContainerEClass, ISingleRefNonContainer.class, "ISingleRefNonContainer", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISingleRefNonContainer_Element(), getISingleRefNonContainedElement(), getISingleRefNonContainedElement_Parent(), "element", null, 0, 1,
        ISingleRefNonContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(iSingleRefNonContainedElementEClass, ISingleRefNonContainedElement.class, "ISingleRefNonContainedElement", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISingleRefNonContainedElement_Parent(), getISingleRefNonContainer(), getISingleRefNonContainer_Element(), "parent", null, 0, 1,
        ISingleRefNonContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(iMultiRefContainerEClass, IMultiRefContainer.class, "IMultiRefContainer", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIMultiRefContainer_Elements(), getIMultiRefContainedElement(), getIMultiRefContainedElement_Parent(), "elements", null, 0, -1,
        IMultiRefContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(iMultiRefContainedElementEClass, IMultiRefContainedElement.class, "IMultiRefContainedElement", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIMultiRefContainedElement_Parent(), getIMultiRefContainer(), getIMultiRefContainer_Elements(), "parent", null, 0, 1,
        IMultiRefContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(iMultiRefNonContainerEClass, IMultiRefNonContainer.class, "IMultiRefNonContainer", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIMultiRefNonContainer_Elements(), getIMultiRefNonContainedElement(), getIMultiRefNonContainedElement_Parent(), "elements", null, 0, -1,
        IMultiRefNonContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(iMultiRefNonContainedElementEClass, IMultiRefNonContainedElement.class, "IMultiRefNonContainedElement", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIMultiRefNonContainedElement_Parent(), getIMultiRefNonContainer(), getIMultiRefNonContainer_Elements(), "parent", null, 0, 1,
        IMultiRefNonContainedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(iNamedElementEClass, INamedElement.class, "INamedElement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getINamedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, INamedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(iContainedElementNoParentLinkEClass, IContainedElementNoParentLink.class, "IContainedElementNoParentLink", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(iSingleRefContainerNPLEClass, ISingleRefContainerNPL.class, "ISingleRefContainerNPL", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISingleRefContainerNPL_Element(), getIContainedElementNoParentLink(), null, "element", null, 0, 1, ISingleRefContainerNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(iSingleRefNonContainerNPLEClass, ISingleRefNonContainerNPL.class, "ISingleRefNonContainerNPL", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISingleRefNonContainerNPL_Element(), getIContainedElementNoParentLink(), null, "element", null, 0, 1, ISingleRefNonContainerNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(iMultiRefContainerNPLEClass, IMultiRefContainerNPL.class, "IMultiRefContainerNPL", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIMultiRefContainerNPL_Elements(), getIContainedElementNoParentLink(), null, "elements", null, 0, -1, IMultiRefContainerNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(iMultiRefNonContainerNPLEClass, IMultiRefNonContainerNPL.class, "IMultiRefNonContainerNPL", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIMultiRefNonContainerNPL_Elements(), getIContainedElementNoParentLink(), null, "elements", null, 0, -1, IMultiRefNonContainerNPL.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // model4interfacesPackageImpl
