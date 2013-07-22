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
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Configuration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Configuration#getProjects <em>Projects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Configuration#getEclipseVersions <em>Eclipse Versions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getConfiguration()
 * @model
 * @generated
 */
public interface Configuration extends EObject
{
  /**
   * Returns the value of the '<em><b>Projects</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.Project}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.Project#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Projects</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Projects</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getConfiguration_Projects()
   * @see org.eclipse.emf.cdo.releng.setup.Project#getConfiguration
   * @model opposite="configuration" containment="true" required="true"
   * @generated
   */
  EList<Project> getProjects();

  /**
   * Returns the value of the '<em><b>Eclipse Versions</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.EclipseVersion}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Eclipse Versions</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Eclipse Versions</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getConfiguration_EclipseVersions()
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getConfiguration
   * @model opposite="configuration" containment="true" required="true"
   * @generated
   */
  EList<EclipseVersion> getEclipseVersions();

} // Configuration
