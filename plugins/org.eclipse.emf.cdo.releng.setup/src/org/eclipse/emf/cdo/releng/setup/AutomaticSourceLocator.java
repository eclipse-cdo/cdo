/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.setup.util.ProjectProvider;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Automatic Source Locator</b></em>'.
 * @extends ProjectProvider
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#getRootFolder <em>Root Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#isLocateNestedProjects <em>Locate Nested Projects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#getPredicates <em>Predicates</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getAutomaticSourceLocator()
 * @model
 * @generated
 */
public interface AutomaticSourceLocator extends SourceLocator, ProjectProvider
{
  /**
   * Returns the value of the '<em><b>Root Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Root Folder</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Root Folder</em>' attribute.
   * @see #setRootFolder(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getAutomaticSourceLocator_RootFolder()
   * @model required="true"
   * @generated
   */
  String getRootFolder();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#getRootFolder <em>Root Folder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Root Folder</em>' attribute.
   * @see #getRootFolder()
   * @generated
   */
  void setRootFolder(String value);

  /**
   * Returns the value of the '<em><b>Locate Nested Projects</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Locate Nested Projects</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Locate Nested Projects</em>' attribute.
   * @see #setLocateNestedProjects(boolean)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getAutomaticSourceLocator_LocateNestedProjects()
   * @model
   * @generated
   */
  boolean isLocateNestedProjects();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#isLocateNestedProjects <em>Locate Nested Projects</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Locate Nested Projects</em>' attribute.
   * @see #isLocateNestedProjects()
   * @generated
   */
  void setLocateNestedProjects(boolean value);

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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getAutomaticSourceLocator_Predicates()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Predicate> getPredicates();

} // AutomaticSourceLocator
