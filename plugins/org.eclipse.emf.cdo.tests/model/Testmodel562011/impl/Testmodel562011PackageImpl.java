/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package Testmodel562011.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import Testmodel562011.DocumentRoot;
import Testmodel562011.SomeContentType;
import Testmodel562011.Testmodel562011Factory;
import Testmodel562011.Testmodel562011Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Testmodel562011PackageImpl extends EPackageImpl implements Testmodel562011Package
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass documentRootEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass someContentTypeEClass = null;

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
   * @see Testmodel562011.Testmodel562011Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Testmodel562011PackageImpl()
  {
    super(eNS_URI, Testmodel562011Factory.eINSTANCE);
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
   * <p>This method is used to initialize {@link Testmodel562011Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Testmodel562011Package init()
  {
    if (isInited)
    {
      return (Testmodel562011Package)EPackage.Registry.INSTANCE.getEPackage(Testmodel562011Package.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredTestmodel562011Package = EPackage.Registry.INSTANCE.get(eNS_URI);
    Testmodel562011PackageImpl theTestmodel562011Package = registeredTestmodel562011Package instanceof Testmodel562011PackageImpl
        ? (Testmodel562011PackageImpl)registeredTestmodel562011Package
        : new Testmodel562011PackageImpl();

    isInited = true;

    // Initialize simple dependencies
    XMLTypePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theTestmodel562011Package.createPackageContents();

    // Initialize created meta-data
    theTestmodel562011Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theTestmodel562011Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Testmodel562011Package.eNS_URI, theTestmodel562011Package);
    return theTestmodel562011Package;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDocumentRoot()
  {
    return documentRootEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDocumentRoot_Mixed()
  {
    return (EAttribute)documentRootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDocumentRoot_XMLNSPrefixMap()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDocumentRoot_XSISchemaLocation()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDocumentRoot_SomeContent()
  {
    return (EReference)documentRootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSomeContentType()
  {
    return someContentTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSomeContentType_SomeElem()
  {
    return (EAttribute)someContentTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Testmodel562011Factory getTestmodel562011Factory()
  {
    return (Testmodel562011Factory)getEFactoryInstance();
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
    documentRootEClass = createEClass(DOCUMENT_ROOT);
    createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
    createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
    createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
    createEReference(documentRootEClass, DOCUMENT_ROOT__SOME_CONTENT);

    someContentTypeEClass = createEClass(SOME_CONTENT_TYPE);
    createEAttribute(someContentTypeEClass, SOME_CONTENT_TYPE__SOME_ELEM);
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
    XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes, features, and operations; add parameters
    initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentRoot_SomeContent(), getSomeContentType(), null, "someContent", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(someContentTypeEClass, SomeContentType.class, "SomeContentType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSomeContentType_SomeElem(), theXMLTypePackage.getString(), "someElem", null, 1, 1, SomeContentType.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    addAnnotation(this, source, new String[] { "qualified", "false" });
    addAnnotation(documentRootEClass, source, new String[] { "name", "", "kind", "mixed" });
    addAnnotation(getDocumentRoot_Mixed(), source, new String[] { "kind", "elementWildcard", "name", ":mixed" });
    addAnnotation(getDocumentRoot_XMLNSPrefixMap(), source, new String[] { "kind", "attribute", "name", "xmlns:prefix" });
    addAnnotation(getDocumentRoot_XSISchemaLocation(), source, new String[] { "kind", "attribute", "name", "xsi:schemaLocation" });
    addAnnotation(getDocumentRoot_SomeContent(), source, new String[] { "kind", "element", "name", "SomeContent", "namespace", "##targetNamespace" });
    addAnnotation(someContentTypeEClass, source, new String[] { "name", "SomeContent_._type", "kind", "elementOnly" });
    addAnnotation(getSomeContentType_SomeElem(), source, new String[] { "kind", "element", "name", "SomeElem", "namespace", "##targetNamespace" });
  }

} // Testmodel562011PackageImpl
