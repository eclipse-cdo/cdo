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
package org.eclipse.emf.cdo.releng.workingsets;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsFactory
 * @model kind="package"
 * @generated
 */
public interface WorkingSetsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "workingsets";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/CDO/releng/workingsets/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "workingsets";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  WorkingSetsPackage eINSTANCE = org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetImpl <em>Working Set</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetImpl
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getWorkingSet()
   * @generated
   */
  int WORKING_SET = 0;

  /**
   * The feature id for the '<em><b>Predicates</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET__PREDICATES = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET__NAME = 1;

  /**
   * The number of structural features of the '<em>Working Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Working Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetGroupImpl <em>Working Set Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetGroupImpl
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getWorkingSetGroup()
   * @generated
   */
  int WORKING_SET_GROUP = 1;

  /**
   * The feature id for the '<em><b>Working Sets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_GROUP__WORKING_SETS = 0;

  /**
   * The number of structural features of the '<em>Working Set Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_GROUP_FEATURE_COUNT = 1;

  /**
   * The number of operations of the '<em>Working Set Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_GROUP_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.workingsets.Predicate <em>Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.workingsets.Predicate
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getPredicate()
   * @generated
   */
  int PREDICATE = 2;

  /**
   * The number of structural features of the '<em>Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREDICATE_FEATURE_COUNT = 0;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREDICATE___MATCHES__IPROJECT = 0;

  /**
   * The number of operations of the '<em>Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREDICATE_OPERATION_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.workingsets.impl.NamePredicateImpl <em>Name Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.NamePredicateImpl
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getNamePredicate()
   * @generated
   */
  int NAME_PREDICATE = 3;

  /**
   * The feature id for the '<em><b>Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_PREDICATE__PATTERN = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Name Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The number of operations of the '<em>Name Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>Project</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.core.resources.IProject
   * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getProject()
   * @generated
   */
  int PROJECT = 4;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet <em>Working Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Working Set</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSet
   * @generated
   */
  EClass getWorkingSet();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getPredicates <em>Predicates</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Predicates</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getPredicates()
   * @see #getWorkingSet()
   * @generated
   */
  EReference getWorkingSet_Predicates();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getName()
   * @see #getWorkingSet()
   * @generated
   */
  EAttribute getWorkingSet_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup <em>Working Set Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Working Set Group</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup
   * @generated
   */
  EClass getWorkingSetGroup();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup#getWorkingSets <em>Working Sets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Working Sets</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup#getWorkingSets()
   * @see #getWorkingSetGroup()
   * @generated
   */
  EReference getWorkingSetGroup_WorkingSets();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.workingsets.Predicate <em>Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.Predicate
   * @generated
   */
  EClass getPredicate();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.workingsets.Predicate#matches(org.eclipse.core.resources.IProject) <em>Matches</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Matches</em>' operation.
   * @see org.eclipse.emf.cdo.releng.workingsets.Predicate#matches(org.eclipse.core.resources.IProject)
   * @generated
   */
  EOperation getPredicate__Matches__IProject();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.workingsets.NamePredicate <em>Name Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Name Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.NamePredicate
   * @generated
   */
  EClass getNamePredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.workingsets.NamePredicate#getPattern <em>Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.workingsets.NamePredicate#getPattern()
   * @see #getNamePredicate()
   * @generated
   */
  EAttribute getNamePredicate_Pattern();

  /**
   * Returns the meta object for data type '{@link org.eclipse.core.resources.IProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Project</em>'.
   * @see org.eclipse.core.resources.IProject
   * @model instanceClass="org.eclipse.core.resources.IProject" serializeable="false"
   * @generated
   */
  EDataType getProject();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  WorkingSetsFactory getWorkingSetsFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetImpl <em>Working Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetImpl
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getWorkingSet()
     * @generated
     */
    EClass WORKING_SET = eINSTANCE.getWorkingSet();

    /**
     * The meta object literal for the '<em><b>Predicates</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKING_SET__PREDICATES = eINSTANCE.getWorkingSet_Predicates();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute WORKING_SET__NAME = eINSTANCE.getWorkingSet_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetGroupImpl <em>Working Set Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetGroupImpl
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getWorkingSetGroup()
     * @generated
     */
    EClass WORKING_SET_GROUP = eINSTANCE.getWorkingSetGroup();

    /**
     * The meta object literal for the '<em><b>Working Sets</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKING_SET_GROUP__WORKING_SETS = eINSTANCE.getWorkingSetGroup_WorkingSets();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.workingsets.Predicate <em>Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.workingsets.Predicate
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getPredicate()
     * @generated
     */
    EClass PREDICATE = eINSTANCE.getPredicate();

    /**
     * The meta object literal for the '<em><b>Matches</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREDICATE___MATCHES__IPROJECT = eINSTANCE.getPredicate__Matches__IProject();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.workingsets.impl.NamePredicateImpl <em>Name Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.NamePredicateImpl
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getNamePredicate()
     * @generated
     */
    EClass NAME_PREDICATE = eINSTANCE.getNamePredicate();

    /**
     * The meta object literal for the '<em><b>Pattern</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NAME_PREDICATE__PATTERN = eINSTANCE.getNamePredicate_Pattern();

    /**
     * The meta object literal for the '<em>Project</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.core.resources.IProject
     * @see org.eclipse.emf.cdo.releng.workingsets.impl.WorkingSetsPackageImpl#getProject()
     * @generated
     */
    EDataType PROJECT = eINSTANCE.getProject();

  }

} // WorkingSetsPackage
