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
package org.eclipse.emf.cdo.releng.predicates;

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
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesFactory
 * @model kind="package"
 * @generated
 */
public interface PredicatesPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "predicates";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/CDO/releng/predicates/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "predicates";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PredicatesPackage eINSTANCE = org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.PredicateImpl <em>Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getPredicate()
   * @generated
   */
  int PREDICATE = 0;

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
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREDICATE___MATCHES__FILE = 1;

  /**
   * The number of operations of the '<em>Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREDICATE_OPERATION_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.NamePredicateImpl <em>Name Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.NamePredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getNamePredicate()
   * @generated
   */
  int NAME_PREDICATE = 1;

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
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>Name Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.RepositoryPredicateImpl <em>Repository Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.RepositoryPredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getRepositoryPredicate()
   * @generated
   */
  int REPOSITORY_PREDICATE = 2;

  /**
   * The feature id for the '<em><b>Project</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_PREDICATE__PROJECT = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Repository Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>Repository Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.AndPredicateImpl <em>And Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.AndPredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getAndPredicate()
   * @generated
   */
  int AND_PREDICATE = 3;

  /**
   * The feature id for the '<em><b>Operands</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_PREDICATE__OPERANDS = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>And Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>And Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.OrPredicateImpl <em>Or Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.OrPredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getOrPredicate()
   * @generated
   */
  int OR_PREDICATE = 4;

  /**
   * The feature id for the '<em><b>Operands</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_PREDICATE__OPERANDS = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Or Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>Or Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.NotPredicateImpl <em>Not Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.NotPredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getNotPredicate()
   * @generated
   */
  int NOT_PREDICATE = 5;

  /**
   * The feature id for the '<em><b>Operand</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOT_PREDICATE__OPERAND = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Not Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOT_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOT_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOT_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>Not Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOT_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.NaturePredicateImpl <em>Nature Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.NaturePredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getNaturePredicate()
   * @generated
   */
  int NATURE_PREDICATE = 6;

  /**
   * The feature id for the '<em><b>Nature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NATURE_PREDICATE__NATURE = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Nature Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NATURE_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NATURE_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NATURE_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>Nature Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NATURE_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.BuilderPredicateImpl <em>Builder Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.BuilderPredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getBuilderPredicate()
   * @generated
   */
  int BUILDER_PREDICATE = 7;

  /**
   * The feature id for the '<em><b>Builder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILDER_PREDICATE__BUILDER = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Builder Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILDER_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILDER_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILDER_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>Builder Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILDER_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.FilePredicateImpl <em>File Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.predicates.impl.FilePredicateImpl
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getFilePredicate()
   * @generated
   */
  int FILE_PREDICATE = 8;

  /**
   * The feature id for the '<em><b>File Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_PREDICATE__FILE_PATTERN = PREDICATE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Content Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_PREDICATE__CONTENT_PATTERN = PREDICATE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>File Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_PREDICATE_FEATURE_COUNT = PREDICATE_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_PREDICATE___MATCHES__IPROJECT = PREDICATE___MATCHES__IPROJECT;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_PREDICATE___MATCHES__FILE = PREDICATE___MATCHES__FILE;

  /**
   * The number of operations of the '<em>File Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_PREDICATE_OPERATION_COUNT = PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>Project</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.core.resources.IProject
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getProject()
   * @generated
   */
  int PROJECT = 9;

  /**
   * The meta object id for the '<em>File</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.io.File
   * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getFile()
   * @generated
   */
  int FILE = 10;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.Predicate <em>Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.Predicate
   * @generated
   */
  EClass getPredicate();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.predicates.Predicate#matches(org.eclipse.core.resources.IProject) <em>Matches</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Matches</em>' operation.
   * @see org.eclipse.emf.cdo.releng.predicates.Predicate#matches(org.eclipse.core.resources.IProject)
   * @generated
   */
  EOperation getPredicate__Matches__IProject();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.predicates.Predicate#matches(java.io.File) <em>Matches</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Matches</em>' operation.
   * @see org.eclipse.emf.cdo.releng.predicates.Predicate#matches(java.io.File)
   * @generated
   */
  EOperation getPredicate__Matches__File();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.NamePredicate <em>Name Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Name Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.NamePredicate
   * @generated
   */
  EClass getNamePredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.predicates.NamePredicate#getPattern <em>Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.NamePredicate#getPattern()
   * @see #getNamePredicate()
   * @generated
   */
  EAttribute getNamePredicate_Pattern();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate <em>Repository Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Repository Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate
   * @generated
   */
  EClass getRepositoryPredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate#getProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate#getProject()
   * @see #getRepositoryPredicate()
   * @generated
   */
  EAttribute getRepositoryPredicate_Project();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.AndPredicate <em>And Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.AndPredicate
   * @generated
   */
  EClass getAndPredicate();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.predicates.AndPredicate#getOperands <em>Operands</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Operands</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.AndPredicate#getOperands()
   * @see #getAndPredicate()
   * @generated
   */
  EReference getAndPredicate_Operands();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.OrPredicate <em>Or Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.OrPredicate
   * @generated
   */
  EClass getOrPredicate();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.predicates.OrPredicate#getOperands <em>Operands</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Operands</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.OrPredicate#getOperands()
   * @see #getOrPredicate()
   * @generated
   */
  EReference getOrPredicate_Operands();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.NotPredicate <em>Not Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Not Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.NotPredicate
   * @generated
   */
  EClass getNotPredicate();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.releng.predicates.NotPredicate#getOperand <em>Operand</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Operand</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.NotPredicate#getOperand()
   * @see #getNotPredicate()
   * @generated
   */
  EReference getNotPredicate_Operand();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.NaturePredicate <em>Nature Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nature Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.NaturePredicate
   * @generated
   */
  EClass getNaturePredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.predicates.NaturePredicate#getNature <em>Nature</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Nature</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.NaturePredicate#getNature()
   * @see #getNaturePredicate()
   * @generated
   */
  EAttribute getNaturePredicate_Nature();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.BuilderPredicate <em>Builder Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Builder Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.BuilderPredicate
   * @generated
   */
  EClass getBuilderPredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.predicates.BuilderPredicate#getBuilder <em>Builder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Builder</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.BuilderPredicate#getBuilder()
   * @see #getBuilderPredicate()
   * @generated
   */
  EAttribute getBuilderPredicate_Builder();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate <em>File Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>File Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.FilePredicate
   * @generated
   */
  EClass getFilePredicate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate#getFilePattern <em>File Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>File Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.FilePredicate#getFilePattern()
   * @see #getFilePredicate()
   * @generated
   */
  EAttribute getFilePredicate_FilePattern();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.predicates.FilePredicate#getContentPattern <em>Content Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Content Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.predicates.FilePredicate#getContentPattern()
   * @see #getFilePredicate()
   * @generated
   */
  EAttribute getFilePredicate_ContentPattern();

  /**
   * Returns the meta object for data type '{@link org.eclipse.core.resources.IProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Project</em>'.
   * @see org.eclipse.core.resources.IProject
   * @model instanceClass="org.eclipse.core.resources.IProject"
   * @generated
   */
  EDataType getProject();

  /**
   * Returns the meta object for data type '{@link java.io.File <em>File</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>File</em>'.
   * @see java.io.File
   * @model instanceClass="java.io.File"
   * @generated
   */
  EDataType getFile();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  PredicatesFactory getPredicatesFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.PredicateImpl <em>Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getPredicate()
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
     * The meta object literal for the '<em><b>Matches</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREDICATE___MATCHES__FILE = eINSTANCE.getPredicate__Matches__File();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.NamePredicateImpl <em>Name Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.NamePredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getNamePredicate()
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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.RepositoryPredicateImpl <em>Repository Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.RepositoryPredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getRepositoryPredicate()
     * @generated
     */
    EClass REPOSITORY_PREDICATE = eINSTANCE.getRepositoryPredicate();

    /**
     * The meta object literal for the '<em><b>Project</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REPOSITORY_PREDICATE__PROJECT = eINSTANCE.getRepositoryPredicate_Project();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.AndPredicateImpl <em>And Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.AndPredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getAndPredicate()
     * @generated
     */
    EClass AND_PREDICATE = eINSTANCE.getAndPredicate();

    /**
     * The meta object literal for the '<em><b>Operands</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_PREDICATE__OPERANDS = eINSTANCE.getAndPredicate_Operands();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.OrPredicateImpl <em>Or Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.OrPredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getOrPredicate()
     * @generated
     */
    EClass OR_PREDICATE = eINSTANCE.getOrPredicate();

    /**
     * The meta object literal for the '<em><b>Operands</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_PREDICATE__OPERANDS = eINSTANCE.getOrPredicate_Operands();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.NotPredicateImpl <em>Not Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.NotPredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getNotPredicate()
     * @generated
     */
    EClass NOT_PREDICATE = eINSTANCE.getNotPredicate();

    /**
     * The meta object literal for the '<em><b>Operand</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NOT_PREDICATE__OPERAND = eINSTANCE.getNotPredicate_Operand();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.NaturePredicateImpl <em>Nature Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.NaturePredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getNaturePredicate()
     * @generated
     */
    EClass NATURE_PREDICATE = eINSTANCE.getNaturePredicate();

    /**
     * The meta object literal for the '<em><b>Nature</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NATURE_PREDICATE__NATURE = eINSTANCE.getNaturePredicate_Nature();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.BuilderPredicateImpl <em>Builder Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.BuilderPredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getBuilderPredicate()
     * @generated
     */
    EClass BUILDER_PREDICATE = eINSTANCE.getBuilderPredicate();

    /**
     * The meta object literal for the '<em><b>Builder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BUILDER_PREDICATE__BUILDER = eINSTANCE.getBuilderPredicate_Builder();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.predicates.impl.FilePredicateImpl <em>File Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.predicates.impl.FilePredicateImpl
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getFilePredicate()
     * @generated
     */
    EClass FILE_PREDICATE = eINSTANCE.getFilePredicate();

    /**
     * The meta object literal for the '<em><b>File Pattern</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FILE_PREDICATE__FILE_PATTERN = eINSTANCE.getFilePredicate_FilePattern();

    /**
     * The meta object literal for the '<em><b>Content Pattern</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FILE_PREDICATE__CONTENT_PATTERN = eINSTANCE.getFilePredicate_ContentPattern();

    /**
     * The meta object literal for the '<em>Project</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.core.resources.IProject
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getProject()
     * @generated
     */
    EDataType PROJECT = eINSTANCE.getProject();

    /**
     * The meta object literal for the '<em>File</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.io.File
     * @see org.eclipse.emf.cdo.releng.predicates.impl.PredicatesPackageImpl#getFile()
     * @generated
     */
    EDataType FILE = eINSTANCE.getFile();

  }

} // PredicatesPackage
