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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Installable Unit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getP2Task <em>P2 Task</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getInstallableUnit()
 * @model
 * @generated
 */
public interface InstallableUnit extends EObject
{
  /**
   * Returns the value of the '<em><b>P2 Task</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits <em>Installable Units</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>P2 Task</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>P2 Task</em>' container reference.
   * @see #setP2Task(P2Task)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getInstallableUnit_P2Task()
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits
   * @model opposite="installableUnits" transient="false"
   * @generated
   */
  P2Task getP2Task();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getP2Task <em>P2 Task</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>P2 Task</em>' container reference.
   * @see #getP2Task()
   * @generated
   */
  void setP2Task(P2Task value);

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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getInstallableUnit_Id()
   * @model
   * @generated
   */
  String getId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(String value);

} // InstallableUnit
