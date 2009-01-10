/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 * $Id: Model2PackageImpl.java,v 1.10 2009-01-10 14:55:14 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;

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
  public static Model2Package init()
  {
    if (isInited)
      return (Model2Package)EPackage.Registry.INSTANCE.getEPackage(Model2Package.eNS_URI);

    // Obtain or create and register package
    Model2PackageImpl theModel2Package = (Model2PackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof Model2PackageImpl ? EPackage.Registry.INSTANCE
        .getEPackage(eNS_URI)
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

    // Create resource
    createResource(eNS_URI);
  }

} // Model2PackageImpl
