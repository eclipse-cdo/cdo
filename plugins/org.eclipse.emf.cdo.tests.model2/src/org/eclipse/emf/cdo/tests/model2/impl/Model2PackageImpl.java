/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.PersistentContainment;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.model2.TransientContainer;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class Model2PackageImpl extends EPackageImpl implements Model2Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass specialPurchaseOrderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass taskContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass taskEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass unsettable1EClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass unsettable2WithDefaultEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass persistentContainmentEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass transientContainerEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model2PackageImpl()
  {
    super(eNS_URI, Model2Factory.eINSTANCE);
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
   * This method is used to initialize {@link Model2Package#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model2Package init()
  {
    if (isInited)
      return (Model2Package)EPackage.Registry.INSTANCE.getEPackage(Model2Package.eNS_URI);

    // Obtain or create and register package
    Model2PackageImpl theModel2Package = (Model2PackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof Model2PackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI)
        : new Model2PackageImpl());

    isInited = true;

    // Initialize simple dependencies
    Model1Package.eINSTANCE.eClass();

    // Create package meta-data objects
    theModel2Package.createPackageContents();

    // Initialize created meta-data
    theModel2Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel2Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Model2Package.eNS_URI, theModel2Package);
    return theModel2Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getSpecialPurchaseOrder()
  {
    return specialPurchaseOrderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getSpecialPurchaseOrder_DiscountCode()
  {
    return (EAttribute)specialPurchaseOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getSpecialPurchaseOrder_ShippingAddress()
  {
    return (EReference)specialPurchaseOrderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getTaskContainer()
  {
    return taskContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getTaskContainer_Tasks()
  {
    return (EReference)taskContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getTask()
  {
    return taskEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getTask_TaskContainer()
  {
    return (EReference)taskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getTask_Description()
  {
    return (EAttribute)taskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getTask_Done()
  {
    return (EAttribute)taskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getUnsettable1()
  {
    return unsettable1EClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableBoolean()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableByte()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableChar()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableDate()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableDouble()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableFloat()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableInt()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableLong()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableShort()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableString()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable1_UnsettableVAT()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getUnsettable2WithDefault()
  {
    return unsettable2WithDefaultEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableBoolean()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableByte()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableChar()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableDate()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableDouble()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableFloat()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableInt()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableLong()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableShort()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableString()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getUnsettable2WithDefault_UnsettableVAT()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getPersistentContainment()
  {
    return persistentContainmentEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getPersistentContainment_AttrBefore()
  {
    return (EAttribute)persistentContainmentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getPersistentContainment_Children()
  {
    return (EReference)persistentContainmentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getPersistentContainment_AttrAfter()
  {
    return (EAttribute)persistentContainmentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getTransientContainer()
  {
    return transientContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getTransientContainer_AttrBefore()
  {
    return (EAttribute)transientContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getTransientContainer_Parent()
  {
    return (EReference)transientContainerEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getTransientContainer_AttrAfter()
  {
    return (EAttribute)transientContainerEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model2Factory getModel2Factory()
  {
    return (Model2Factory)getEFactoryInstance();
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
    specialPurchaseOrderEClass = createEClass(SPECIAL_PURCHASE_ORDER);
    createEAttribute(specialPurchaseOrderEClass, SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE);
    createEReference(specialPurchaseOrderEClass, SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS);

    taskContainerEClass = createEClass(TASK_CONTAINER);
    createEReference(taskContainerEClass, TASK_CONTAINER__TASKS);

    taskEClass = createEClass(TASK);
    createEReference(taskEClass, TASK__TASK_CONTAINER);
    createEAttribute(taskEClass, TASK__DESCRIPTION);
    createEAttribute(taskEClass, TASK__DONE);

    unsettable1EClass = createEClass(UNSETTABLE1);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_BOOLEAN);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_BYTE);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_CHAR);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_DATE);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_DOUBLE);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_FLOAT);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_INT);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_LONG);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_SHORT);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_STRING);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_VAT);

    unsettable2WithDefaultEClass = createEClass(UNSETTABLE2_WITH_DEFAULT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BOOLEAN);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BYTE);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_CHAR);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DATE);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DOUBLE);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_FLOAT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_INT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_LONG);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_SHORT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_STRING);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_VAT);

    persistentContainmentEClass = createEClass(PERSISTENT_CONTAINMENT);
    createEAttribute(persistentContainmentEClass, PERSISTENT_CONTAINMENT__ATTR_BEFORE);
    createEReference(persistentContainmentEClass, PERSISTENT_CONTAINMENT__CHILDREN);
    createEAttribute(persistentContainmentEClass, PERSISTENT_CONTAINMENT__ATTR_AFTER);

    transientContainerEClass = createEClass(TRANSIENT_CONTAINER);
    createEAttribute(transientContainerEClass, TRANSIENT_CONTAINER__ATTR_BEFORE);
    createEReference(transientContainerEClass, TRANSIENT_CONTAINER__PARENT);
    createEAttribute(transientContainerEClass, TRANSIENT_CONTAINER__ATTR_AFTER);
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
    Model1Package theModel1Package = (Model1Package)EPackage.Registry.INSTANCE.getEPackage(Model1Package.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    specialPurchaseOrderEClass.getESuperTypes().add(theModel1Package.getPurchaseOrder());

    // Initialize classes and features; add operations and parameters
    initEClass(specialPurchaseOrderEClass, SpecialPurchaseOrder.class, "SpecialPurchaseOrder", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSpecialPurchaseOrder_DiscountCode(), ecorePackage.getEString(), "discountCode", null, 0, 1,
        SpecialPurchaseOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getSpecialPurchaseOrder_ShippingAddress(), theModel1Package.getAddress(), null, "shippingAddress",
        null, 0, 1, SpecialPurchaseOrder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(taskContainerEClass, TaskContainer.class, "TaskContainer", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTaskContainer_Tasks(), this.getTask(), this.getTask_TaskContainer(), "tasks", null, 0, -1,
        TaskContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(taskEClass, Task.class, "Task", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTask_TaskContainer(), this.getTaskContainer(), this.getTaskContainer_Tasks(), "taskContainer",
        null, 0, 1, Task.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTask_Description(), ecorePackage.getEString(), "description", null, 0, 1, Task.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTask_Done(), ecorePackage.getEBoolean(), "done", null, 0, 1, Task.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unsettable1EClass, Unsettable1.class, "Unsettable1", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUnsettable1_UnsettableBoolean(), ecorePackage.getEBoolean(), "unsettableBoolean", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableByte(), ecorePackage.getEByte(), "unsettableByte", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableChar(), ecorePackage.getEChar(), "unsettableChar", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableDate(), ecorePackage.getEDate(), "unsettableDate", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableDouble(), ecorePackage.getEDouble(), "unsettableDouble", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableFloat(), ecorePackage.getEFloat(), "unsettableFloat", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableInt(), ecorePackage.getEInt(), "unsettableInt", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableLong(), ecorePackage.getELong(), "unsettableLong", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableShort(), ecorePackage.getEShort(), "unsettableShort", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableString(), ecorePackage.getEString(), "unsettableString", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableVAT(), theModel1Package.getVAT(), "unsettableVAT", null, 0, 1,
        Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(unsettable2WithDefaultEClass, Unsettable2WithDefault.class, "Unsettable2WithDefault", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUnsettable2WithDefault_UnsettableBoolean(), ecorePackage.getEBoolean(), "unsettableBoolean",
        "true", 0, 1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableByte(), ecorePackage.getEByte(), "unsettableByte", "3", 0, 1,
        Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableChar(), ecorePackage.getEChar(), "unsettableChar", "\'x\'", 0,
        1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableDate(), ecorePackage.getEDate(), "unsettableDate",
        "2009-12-21T15:12:59", 0, 1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableDouble(), ecorePackage.getEDouble(), "unsettableDouble", "3.3",
        0, 1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableFloat(), ecorePackage.getEFloat(), "unsettableFloat", "4.4", 0,
        1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableInt(), ecorePackage.getEInt(), "unsettableInt", "5", 0, 1,
        Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableLong(), ecorePackage.getELong(), "unsettableLong", "6", 0, 1,
        Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableShort(), ecorePackage.getEShort(), "unsettableShort", "7", 0, 1,
        Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableString(), ecorePackage.getEString(), "unsettableString",
        "\"eike\"", 0, 1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableVAT(), theModel1Package.getVAT(), "unsettableVAT", "VAT15", 0,
        1, Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(persistentContainmentEClass, PersistentContainment.class, "PersistentContainment", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPersistentContainment_AttrBefore(), ecorePackage.getEString(), "attrBefore", null, 0, 1,
        PersistentContainment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getPersistentContainment_Children(), this.getTransientContainer(), this
        .getTransientContainer_Parent(), "children", null, 0, -1, PersistentContainment.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        !IS_ORDERED);
    initEAttribute(getPersistentContainment_AttrAfter(), ecorePackage.getEString(), "attrAfter", null, 0, 1,
        PersistentContainment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(transientContainerEClass, TransientContainer.class, "TransientContainer", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTransientContainer_AttrBefore(), ecorePackage.getEString(), "attrBefore", null, 0, 1,
        TransientContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getTransientContainer_Parent(), this.getPersistentContainment(), this
        .getPersistentContainment_Children(), "parent", null, 0, 1, TransientContainer.class, IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getTransientContainer_AttrAfter(), ecorePackage.getEString(), "attrAfter", null, 0, 1,
        TransientContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // Model2PackageImpl
