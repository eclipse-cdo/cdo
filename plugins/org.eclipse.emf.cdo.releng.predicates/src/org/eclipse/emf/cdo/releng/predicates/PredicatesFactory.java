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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage
 * @generated
 */
public interface PredicatesFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PredicatesFactory eINSTANCE = org.eclipse.emf.cdo.releng.predicates.impl.PredicatesFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Name Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Name Predicate</em>'.
   * @generated
   */
  NamePredicate createNamePredicate();

  /**
   * Returns a new object of class '<em>Repository Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Repository Predicate</em>'.
   * @generated
   */
  RepositoryPredicate createRepositoryPredicate();

  /**
   * Returns a new object of class '<em>And Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Predicate</em>'.
   * @generated
   */
  AndPredicate createAndPredicate();

  /**
   * Returns a new object of class '<em>Or Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Predicate</em>'.
   * @generated
   */
  OrPredicate createOrPredicate();

  /**
   * Returns a new object of class '<em>Not Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Not Predicate</em>'.
   * @generated
   */
  NotPredicate createNotPredicate();

  /**
   * Returns a new object of class '<em>Nature Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nature Predicate</em>'.
   * @generated
   */
  NaturePredicate createNaturePredicate();

  /**
   * Returns a new object of class '<em>Builder Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Builder Predicate</em>'.
   * @generated
   */
  BuilderPredicate createBuilderPredicate();

  /**
   * Returns a new object of class '<em>File Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>File Predicate</em>'.
   * @generated
   */
  FilePredicate createFilePredicate();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  PredicatesPackage getPredicatesPackage();

} // PredicatesFactory
