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
 * A representation of the model object '<em><b>Director Call</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.DirectorCall#getInstallableUnits <em>Installable Units</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.DirectorCall#getP2Repositories <em>P2 Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getDirectorCall()
 * @model
 * @generated
 */
public interface DirectorCall extends EObject
{
  /**
   * Returns the value of the '<em><b>Installable Units</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.InstallableUnit}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getDirectorCall <em>Director Call</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Installable Units</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Installable Units</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getDirectorCall_InstallableUnits()
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getDirectorCall
   * @model opposite="directorCall" containment="true" required="true"
   * @generated
   */
  EList<InstallableUnit> getInstallableUnits();

  /**
   * Returns the value of the '<em><b>P2 Repositories</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.P2Repository}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getDirectorCall <em>Director Call</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>P2 Repositories</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>P2 Repositories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getDirectorCall_P2Repositories()
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getDirectorCall
   * @model opposite="directorCall" containment="true" required="true"
   * @generated
   */
  EList<P2Repository> getP2Repositories();

} // DirectorCall
