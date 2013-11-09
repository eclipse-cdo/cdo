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
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Project#getConfiguration <em>Configuration</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Project#getBranches <em>Branches</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Project#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getProject()
 * @model
 * @generated
 */
public interface Project extends ConfigurableItem, TopLevelElement
{
  /**
   * Returns the value of the '<em><b>Configuration</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.Configuration#getProjects <em>Projects</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Configuration</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Configuration</em>' container reference.
   * @see #setConfiguration(Configuration)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getProject_Configuration()
   * @see org.eclipse.emf.cdo.releng.setup.Configuration#getProjects
   * @model opposite="projects"
   * @generated
   */
  Configuration getConfiguration();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Project#getConfiguration <em>Configuration</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Configuration</em>' container reference.
   * @see #getConfiguration()
   * @generated
   */
  void setConfiguration(Configuration value);

  /**
   * Returns the value of the '<em><b>Branches</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.Branch}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.Branch#getProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Branches</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Branches</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getProject_Branches()
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getProject
   * @model opposite="project" containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<Branch> getBranches();

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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getProject_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Project#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Project
