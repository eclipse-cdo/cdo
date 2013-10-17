/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.workingsets.impl;

import org.eclipse.emf.cdo.releng.workingsets.WorkingSet;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetsFactory;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WorkingSetsPackageImpl extends EPackageImpl implements WorkingSetsPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass workingSetEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass workingSetGroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType projectEDataType = null;

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
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private WorkingSetsPackageImpl()
  {
    super(eNS_URI, WorkingSetsFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link WorkingSetsPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static WorkingSetsPackage init()
  {
    if (isInited)
      return (WorkingSetsPackage)EPackage.Registry.INSTANCE.getEPackage(WorkingSetsPackage.eNS_URI);

    // Obtain or create and register package
    WorkingSetsPackageImpl theWorkingSetsPackage = (WorkingSetsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof WorkingSetsPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new WorkingSetsPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    PredicatesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theWorkingSetsPackage.createPackageContents();

    // Initialize created meta-data
    theWorkingSetsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theWorkingSetsPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(WorkingSetsPackage.eNS_URI, theWorkingSetsPackage);
    return theWorkingSetsPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWorkingSet()
  {
    return workingSetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkingSet_Predicates()
  {
    return (EReference)workingSetEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getWorkingSet_Id()
  {
    return (EAttribute)workingSetEClass.getEStructuralFeatures().get(2);
  }

  /**
  	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
  	 * @generated
  	 */
  public EAttribute getWorkingSet_Name()
  {
    return (EAttribute)workingSetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWorkingSetGroup()
  {
    return workingSetGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkingSetGroup_WorkingSets()
  {
    return (EReference)workingSetGroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getProject()
  {
    return projectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkingSetsFactory getWorkingSetsFactory()
  {
    return (WorkingSetsFactory)getEFactoryInstance();
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
      return;
    isCreated = true;

    // Create classes and their features
    workingSetEClass = createEClass(WORKING_SET);
    createEAttribute(workingSetEClass, WORKING_SET__NAME);
    createEReference(workingSetEClass, WORKING_SET__PREDICATES);
    createEAttribute(workingSetEClass, WORKING_SET__ID);

    workingSetGroupEClass = createEClass(WORKING_SET_GROUP);
    createEReference(workingSetGroupEClass, WORKING_SET_GROUP__WORKING_SETS);

    // Create data types
    projectEDataType = createEDataType(PROJECT);
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
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    PredicatesPackage thePredicatesPackage = (PredicatesPackage)EPackage.Registry.INSTANCE
        .getEPackage(PredicatesPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes, features, and operations; add parameters
    initEClass(workingSetEClass, WorkingSet.class, "WorkingSet", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getWorkingSet_Name(), ecorePackage.getEString(), "name", null, 1, 1, WorkingSet.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWorkingSet_Predicates(), thePredicatesPackage.getPredicate(), null, "predicates", null, 0, -1,
        WorkingSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getWorkingSet_Id(), ecorePackage.getEString(), "id", null, 0, 1, WorkingSet.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(workingSetGroupEClass, WorkingSetGroup.class, "WorkingSetGroup", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWorkingSetGroup_WorkingSets(), this.getWorkingSet(), null, "workingSets", null, 0, -1,
        WorkingSetGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    getWorkingSetGroup_WorkingSets().getEKeys().add(this.getWorkingSet_Name());

    // Initialize data types
    initEDataType(projectEDataType, IProject.class, "Project", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // WorkingSetsPackageImpl
