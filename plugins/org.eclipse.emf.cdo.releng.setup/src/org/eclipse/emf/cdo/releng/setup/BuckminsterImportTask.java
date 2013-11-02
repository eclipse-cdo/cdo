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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Buckminster Import Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getMspec <em>Mspec</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBuckminsterImportTask()
 * @model
 * @generated
 */
public interface BuckminsterImportTask extends BasicMaterializationTask
{
  /**
   * Returns the value of the '<em><b>Mspec</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Mspec</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Mspec</em>' attribute.
   * @see #setMspec(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getBuckminsterImportTask_Mspec()
   * @model
   * @generated
   */
  String getMspec();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getMspec <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Mspec</em>' attribute.
   * @see #getMspec()
   * @generated
   */
  void setMspec(String value);

} // BuckminsterImportTask
