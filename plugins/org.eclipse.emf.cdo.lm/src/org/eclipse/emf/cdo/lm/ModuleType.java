/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Module
 * Type</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.ModuleType#getProcess <em>Process</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.ModuleType#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getModuleType()
 * @model
 * @generated
 */
public interface ModuleType extends ProcessElement
{
  /**
   * Returns the value of the '<em><b>Process</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Process#getModuleTypes <em>Module Types</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Process</em>' container reference.
   * @see #setProcess(org.eclipse.emf.cdo.lm.Process)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getModuleType_Process()
   * @see org.eclipse.emf.cdo.lm.Process#getModuleTypes
   * @model opposite="moduleTypes" required="true" transient="false"
   * @generated
   */
  @Override
  org.eclipse.emf.cdo.lm.Process getProcess();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.ModuleType#getProcess <em>Process</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Process</em>' container reference.
   * @see #getProcess()
   * @generated
   */
  void setProcess(org.eclipse.emf.cdo.lm.Process value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getModuleType_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.ModuleType#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // ModuleType
