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

import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Working Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getPredicates <em>Predicates</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getWorkingSet()
 * @model
 * @generated
 */
public interface WorkingSet extends EObject
{
  /**
   * Returns the value of the '<em><b>Predicates</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.predicates.Predicate}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Predicates</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Predicates</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getWorkingSet_Predicates()
   * @model containment="true"
   * @generated
   */
  EList<Predicate> getPredicates();

  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(String)
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getWorkingSet_Id()
   * @model
   * @generated
   */
  String getId();

  /**
  	 * Sets the value of the '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getId <em>Id</em>}' attribute.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @param value the new value of the '<em>Id</em>' attribute.
  	 * @see #getId()
  	 * @generated
  	 */
  void setId(String value);

  /**
  	 * Returns the value of the '<em><b>Name</b></em>' attribute.
  	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
  	 * @return the value of the '<em>Name</em>' attribute.
  	 * @see #setName(String)
  	 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getWorkingSet_Name()
  	 * @model required="true"
  	 * @generated
  	 */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // WorkingSet
