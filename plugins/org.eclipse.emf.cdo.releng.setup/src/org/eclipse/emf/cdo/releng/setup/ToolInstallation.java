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
 * A representation of the model object '<em><b>Tool Installation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ToolInstallation#getDirectorCalls <em>Director Calls</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getToolInstallation()
 * @model
 * @generated
 */
public interface ToolInstallation extends EObject
{
  /**
   * Returns the value of the '<em><b>Director Calls</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.DirectorCall}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Director Calls</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Director Calls</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getToolInstallation_DirectorCalls()
   * @model containment="true"
   * @generated
   */
  EList<DirectorCall> getDirectorCalls();

} // ToolInstallation
