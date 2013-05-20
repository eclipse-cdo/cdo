/*
 * Copyright (c) 2008-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.tests.model3.NodeB;
import org.eclipse.emf.cdo.tests.model3.NodeC;
import org.eclipse.emf.cdo.tests.model3.NodeD;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage;
import org.eclipse.emf.cdo.tests.model3.subpackage.impl.SubpackagePackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model3PackageImpl extends EPackageImpl implements Model3Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass class1EClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass metaRefEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass polygonEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass polygonWithDuplicatesEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass nodeAEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass nodeBEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass nodeCEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass nodeDEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass imageEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass fileEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass classWithIDAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass classWithJavaClassAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass classWithJavaObjectAttributeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EDataType pointEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model3PackageImpl()
  {
    super(eNS_URI, Model3Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link Model3Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model3Package init()
  {
    if (isInited)
      return (Model3Package)EPackage.Registry.INSTANCE.getEPackage(Model3Package.eNS_URI);

    // Obtain or create and register package
    Model3PackageImpl theModel3Package = (Model3PackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof Model3PackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new Model3PackageImpl());

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();

    // Obtain or create and register interdependencies
    SubpackagePackageImpl theSubpackagePackage = (SubpackagePackageImpl)(EPackage.Registry.INSTANCE
        .getEPackage(SubpackagePackage.eNS_URI) instanceof SubpackagePackageImpl ? EPackage.Registry.INSTANCE
        .getEPackage(SubpackagePackage.eNS_URI) : SubpackagePackage.eINSTANCE);

    // Create package meta-data objects
    theModel3Package.createPackageContents();
    theSubpackagePackage.createPackageContents();

    // Initialize created meta-data
    theModel3Package.initializePackageContents();
    theSubpackagePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel3Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Model3Package.eNS_URI, theModel3Package);
    return theModel3Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getClass1()
  {
    return class1EClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getClass1_Class2()
  {
    return (EReference)class1EClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getMetaRef()
  {
    return metaRefEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getMetaRef_EPackageRef()
  {
    return (EReference)metaRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getMetaRef_EClassRef()
  {
    return (EReference)metaRefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getMetaRef_EReferenceRef()
  {
    return (EReference)metaRefEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getPolygon()
  {
    return polygonEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPolygon_Points()
  {
    return (EAttribute)polygonEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getPolygonWithDuplicates()
  {
    return polygonWithDuplicatesEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPolygonWithDuplicates_Points()
  {
    return (EAttribute)polygonWithDuplicatesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getNodeA()
  {
    return nodeAEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeA_Children()
  {
    return (EReference)nodeAEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNodeA_Name()
  {
    return (EAttribute)nodeAEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeA_OtherNodes()
  {
    return (EReference)nodeAEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getNodeB()
  {
    return nodeBEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeB_Children()
  {
    return (EReference)nodeBEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeB_Parent()
  {
    return (EReference)nodeBEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNodeB_Name()
  {
    return (EAttribute)nodeBEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getNodeC()
  {
    return nodeCEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeC_Children()
  {
    return (EReference)nodeCEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeC_Parent()
  {
    return (EReference)nodeCEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNodeC_Name()
  {
    return (EAttribute)nodeCEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeC_OtherNodes()
  {
    return (EReference)nodeCEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeC_OppositeNodes()
  {
    return (EReference)nodeCEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getNodeD()
  {
    return nodeDEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeD_Children()
  {
    return (EReference)nodeDEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeD_Parent()
  {
    return (EReference)nodeDEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNodeD_Name()
  {
    return (EAttribute)nodeDEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeD_OtherNodes()
  {
    return (EReference)nodeDEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference getNodeD_OppositeNode()
  {
    return (EReference)nodeDEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getImage()
  {
    return imageEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getImage_Width()
  {
    return (EAttribute)imageEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getImage_Height()
  {
    return (EAttribute)imageEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getImage_Data()
  {
    return (EAttribute)imageEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getFile()
  {
    return fileEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFile_Name()
  {
    return (EAttribute)fileEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFile_Data()
  {
    return (EAttribute)fileEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass getClassWithIDAttribute()
  {
    return classWithIDAttributeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getClassWithIDAttribute_Id()
  {
    return (EAttribute)classWithIDAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getClassWithJavaClassAttribute()
  {
    return classWithJavaClassAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getClassWithJavaClassAttribute_JavaClass()
  {
    return (EAttribute)classWithJavaClassAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getClassWithJavaObjectAttribute()
  {
    return classWithJavaObjectAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getClassWithJavaObjectAttribute_JavaObject()
  {
    return (EAttribute)classWithJavaObjectAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EDataType getPoint()
  {
    return pointEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model3Factory getModel3Factory()
  {
    return (Model3Factory)getEFactoryInstance();
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
    class1EClass = createEClass(CLASS1);
    createEReference(class1EClass, CLASS1__CLASS2);

    metaRefEClass = createEClass(META_REF);
    createEReference(metaRefEClass, META_REF__EPACKAGE_REF);
    createEReference(metaRefEClass, META_REF__ECLASS_REF);
    createEReference(metaRefEClass, META_REF__EREFERENCE_REF);

    polygonEClass = createEClass(POLYGON);
    createEAttribute(polygonEClass, POLYGON__POINTS);

    polygonWithDuplicatesEClass = createEClass(POLYGON_WITH_DUPLICATES);
    createEAttribute(polygonWithDuplicatesEClass, POLYGON_WITH_DUPLICATES__POINTS);

    nodeAEClass = createEClass(NODE_A);
    createEReference(nodeAEClass, NODE_A__CHILDREN);
    createEAttribute(nodeAEClass, NODE_A__NAME);
    createEReference(nodeAEClass, NODE_A__OTHER_NODES);

    nodeBEClass = createEClass(NODE_B);
    createEReference(nodeBEClass, NODE_B__CHILDREN);
    createEReference(nodeBEClass, NODE_B__PARENT);
    createEAttribute(nodeBEClass, NODE_B__NAME);

    nodeCEClass = createEClass(NODE_C);
    createEReference(nodeCEClass, NODE_C__CHILDREN);
    createEReference(nodeCEClass, NODE_C__PARENT);
    createEAttribute(nodeCEClass, NODE_C__NAME);
    createEReference(nodeCEClass, NODE_C__OTHER_NODES);
    createEReference(nodeCEClass, NODE_C__OPPOSITE_NODES);

    nodeDEClass = createEClass(NODE_D);
    createEReference(nodeDEClass, NODE_D__CHILDREN);
    createEReference(nodeDEClass, NODE_D__PARENT);
    createEAttribute(nodeDEClass, NODE_D__NAME);
    createEReference(nodeDEClass, NODE_D__OTHER_NODES);
    createEReference(nodeDEClass, NODE_D__OPPOSITE_NODE);

    imageEClass = createEClass(IMAGE);
    createEAttribute(imageEClass, IMAGE__WIDTH);
    createEAttribute(imageEClass, IMAGE__HEIGHT);
    createEAttribute(imageEClass, IMAGE__DATA);

    fileEClass = createEClass(FILE);
    createEAttribute(fileEClass, FILE__NAME);
    createEAttribute(fileEClass, FILE__DATA);

    classWithIDAttributeEClass = createEClass(CLASS_WITH_ID_ATTRIBUTE);
    createEAttribute(classWithIDAttributeEClass, CLASS_WITH_ID_ATTRIBUTE__ID);

    classWithJavaClassAttributeEClass = createEClass(CLASS_WITH_JAVA_CLASS_ATTRIBUTE);
    createEAttribute(classWithJavaClassAttributeEClass, CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS);

    classWithJavaObjectAttributeEClass = createEClass(CLASS_WITH_JAVA_OBJECT_ATTRIBUTE);
    createEAttribute(classWithJavaObjectAttributeEClass, CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT);

    // Create data types
    pointEDataType = createEDataType(POINT);
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

    // Obtain other dependent packages
    SubpackagePackage theSubpackagePackage = (SubpackagePackage)EPackage.Registry.INSTANCE
        .getEPackage(SubpackagePackage.eNS_URI);
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);

    // Add subpackages
    getESubpackages().add(theSubpackagePackage);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(class1EClass, Class1.class, "Class1", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getClass1_Class2(), theSubpackagePackage.getClass2(), theSubpackagePackage.getClass2_Class1(),
        "class2", null, 0, -1, Class1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(metaRefEClass, MetaRef.class, "MetaRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMetaRef_EPackageRef(), theEcorePackage.getEPackage(), null, "ePackageRef", null, 0, 1,
        MetaRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMetaRef_EClassRef(), theEcorePackage.getEClass(), null, "eClassRef", null, 0, 1, MetaRef.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getMetaRef_EReferenceRef(), theEcorePackage.getEReference(), null, "eReferenceRef", null, 0, 1,
        MetaRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(polygonEClass, Polygon.class, "Polygon", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPolygon_Points(), this.getPoint(), "points", null, 1, -1, Polygon.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(polygonWithDuplicatesEClass, PolygonWithDuplicates.class, "PolygonWithDuplicates", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPolygonWithDuplicates_Points(), this.getPoint(), "points", null, 1, -1,
        PolygonWithDuplicates.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(nodeAEClass, NodeA.class, "NodeA", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNodeA_Children(), this.getNodeA(), null, "children", null, 0, -1, NodeA.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getNodeA_Name(), theEcorePackage.getEString(), "name", null, 0, 1, NodeA.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeA_OtherNodes(), this.getNodeA(), null, "otherNodes", null, 0, -1, NodeA.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(nodeBEClass, NodeB.class, "NodeB", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNodeB_Children(), this.getNodeB(), this.getNodeB_Parent(), "children", null, 0, -1, NodeB.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeB_Parent(), this.getNodeB(), this.getNodeB_Children(), "parent", null, 0, 1, NodeB.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNodeB_Name(), theEcorePackage.getEString(), "name", null, 0, 1, NodeB.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nodeCEClass, NodeC.class, "NodeC", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNodeC_Children(), this.getNodeC(), this.getNodeC_Parent(), "children", null, 0, -1, NodeC.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeC_Parent(), this.getNodeC(), this.getNodeC_Children(), "parent", null, 0, 1, NodeC.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNodeC_Name(), theEcorePackage.getEString(), "name", null, 0, 1, NodeC.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeC_OtherNodes(), this.getNodeC(), this.getNodeC_OppositeNodes(), "otherNodes", null, 0, -1,
        NodeC.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeC_OppositeNodes(), this.getNodeC(), this.getNodeC_OtherNodes(), "oppositeNodes", null, 0, -1,
        NodeC.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nodeDEClass, NodeD.class, "NodeD", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNodeD_Children(), this.getNodeD(), this.getNodeD_Parent(), "children", null, 0, -1, NodeD.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeD_Parent(), this.getNodeD(), this.getNodeD_Children(), "parent", null, 0, 1, NodeD.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNodeD_Name(), theEcorePackage.getEString(), "name", null, 0, 1, NodeD.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeD_OtherNodes(), this.getNodeD(), this.getNodeD_OppositeNode(), "otherNodes", null, 0, -1,
        NodeD.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeD_OppositeNode(), this.getNodeD(), this.getNodeD_OtherNodes(), "oppositeNode", null, 0, 1,
        NodeD.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(imageEClass, Image.class, "Image", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImage_Width(), theEcorePackage.getEInt(), "width", null, 0, 1, Image.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getImage_Height(), theEcorePackage.getEInt(), "height", null, 0, 1, Image.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getImage_Data(), theEtypesPackage.getBlob(), "data", null, 0, 1, Image.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(fileEClass, File.class, "File", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFile_Name(), theEcorePackage.getEString(), "name", null, 0, 1, File.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFile_Data(), theEtypesPackage.getClob(), "data", null, 0, 1, File.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(classWithIDAttributeEClass, ClassWithIDAttribute.class, "ClassWithIDAttribute", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getClassWithIDAttribute_Id(), theEcorePackage.getEString(), "id", null, 0, 1,
        ClassWithIDAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(classWithJavaClassAttributeEClass, ClassWithJavaClassAttribute.class, "ClassWithJavaClassAttribute",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    EGenericType g1 = createEGenericType(theEcorePackage.getEJavaClass());
    EGenericType g2 = createEGenericType();
    g1.getETypeArguments().add(g2);
    initEAttribute(getClassWithJavaClassAttribute_JavaClass(), g1, "javaClass", null, 0, 1,
        ClassWithJavaClassAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(classWithJavaObjectAttributeEClass, ClassWithJavaObjectAttribute.class, "ClassWithJavaObjectAttribute",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getClassWithJavaObjectAttribute_JavaObject(), theEcorePackage.getEJavaObject(), "javaObject", null,
        0, 1, ClassWithJavaObjectAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(pointEDataType, Point.class, "Point", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // teneo.jpa
    createTeneoAnnotations();
  }

  /**
   * Initializes the annotations for <b>teneo.jpa</b>.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  protected void createTeneoAnnotations()
  {
    String source = "teneo.jpa";
    addAnnotation(pointEDataType, source, new String[] { "value", "@NoAuditing" });
  }

} // Model3PackageImpl
