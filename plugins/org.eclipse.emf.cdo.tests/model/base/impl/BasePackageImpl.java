/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package base.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import base.BaseClass;
import base.BaseFactory;
import base.BasePackage;
import base.Document;
import base.Element;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class BasePackageImpl extends EPackageImpl implements BasePackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass baseClassEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass documentEClass = null;

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  private EClass elementEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see base.BasePackage#eNS_URI
   * @see #init()
   * @generated
   */
  private BasePackageImpl()
  {
    super(eNS_URI, BaseFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link BasePackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static BasePackage init()
  {
    if (isInited)
    {
      return (BasePackage)EPackage.Registry.INSTANCE.getEPackage(BasePackage.eNS_URI);
    }

    // Obtain or create and register package
    BasePackageImpl theBasePackage = (BasePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof BasePackageImpl
        ? EPackage.Registry.INSTANCE.get(eNS_URI)
        : new BasePackageImpl());

    isInited = true;

    // Create package meta-data objects
    theBasePackage.createPackageContents();

    // Initialize created meta-data
    theBasePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theBasePackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(BasePackage.eNS_URI, theBasePackage);
    return theBasePackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getBaseClass()
  {
    return baseClassEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBaseClass_Couter()
  {
    return (EAttribute)baseClassEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDocument()
  {
    return documentEClass;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public EReference getDocument_Root()
  {
    return (EReference)documentEClass.getEStructuralFeatures().get(0);
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public EClass getElement()
  {
    return elementEClass;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public EReference getElement_Subelements()
  {
    return (EReference)elementEClass.getEStructuralFeatures().get(0);
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public EReference getElement_Parent()
  {
    return (EReference)elementEClass.getEStructuralFeatures().get(1);
  }

  /**
  	 * <!-- begin-user-doc --> <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public BaseFactory getBaseFactory()
  {
    return (BaseFactory)getEFactoryInstance();
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
    baseClassEClass = createEClass(BASE_CLASS);
    createEAttribute(baseClassEClass, BASE_CLASS__COUTER);

    documentEClass = createEClass(DOCUMENT);
    createEReference(documentEClass, DOCUMENT__ROOT);

    elementEClass = createEClass(ELEMENT);
    createEReference(elementEClass, ELEMENT__SUBELEMENTS);
    createEReference(elementEClass, ELEMENT__PARENT);
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
    initEClass(baseClassEClass, BaseClass.class, "BaseClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBaseClass_Couter(), ecorePackage.getEInt(), "couter", null, 0, 1, BaseClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(baseClassEClass, null, "increment", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(documentEClass, Document.class, "Document", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDocument_Root(), getElement(), null, "root", null, 0, 1, Document.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(elementEClass, Element.class, "Element", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getElement_Subelements(), getElement(), getElement_Parent(), "subelements", null, 0, -1, Element.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getElement_Parent(), getElement(), getElement_Subelements(), "parent", null, 0, 1, Element.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // BasePackageImpl
