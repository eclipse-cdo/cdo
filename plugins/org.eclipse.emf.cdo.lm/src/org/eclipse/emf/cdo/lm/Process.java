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

import org.eclipse.emf.common.util.EList;

import org.eclipse.equinox.p2.metadata.Version;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Process</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Process#getSystem <em>System</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Process#getModuleTypes <em>Module Types</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Process#getDropTypes <em>Drop Types</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Process#getModuleDefinitionPath <em>Module Definition Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Process#getInitialModuleVersion <em>Initial Module Version</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getProcess()
 * @model
 * @generated
 */
public interface Process extends SystemElement
{
  /**
   * Returns the value of the '<em><b>System</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.System#getProcess <em>Process</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>System</em>' container reference.
   * @see #setSystem(org.eclipse.emf.cdo.lm.System)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getProcess_System()
   * @see org.eclipse.emf.cdo.lm.System#getProcess
   * @model opposite="process" required="true" transient="false"
   * @generated
   */
  @Override
  org.eclipse.emf.cdo.lm.System getSystem();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Process#getSystem <em>System</em>}' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>System</em>' container reference.
   * @see #getSystem()
   * @generated
   */
  void setSystem(org.eclipse.emf.cdo.lm.System value);

  /**
   * Returns the value of the '<em><b>Drop Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.DropType}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.DropType#getProcess <em>Process</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Drop Types</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getProcess_DropTypes()
   * @see org.eclipse.emf.cdo.lm.DropType#getProcess
   * @model opposite="process" containment="true"
   * @generated
   */
  EList<DropType> getDropTypes();

  /**
   * Returns the value of the '<em><b>Module Definition Path</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Module Definition Path</em>' attribute.
   * @see #setModuleDefinitionPath(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getProcess_ModuleDefinitionPath()
   * @model required="true"
   * @generated
   */
  String getModuleDefinitionPath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Process#getModuleDefinitionPath <em>Module Definition Path</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Module Definition Path</em>' attribute.
   * @see #getModuleDefinitionPath()
   * @generated
   */
  void setModuleDefinitionPath(String value);

  /**
   * Returns the value of the '<em><b>Initial Module Version</b></em>' attribute.
   * The default value is <code>"0.1.0"</code>.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the value of the '<em>Initial Module Version</em>' attribute.
   * @see #setInitialModuleVersion(Version)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getProcess_InitialModuleVersion()
   * @model default="0.1.0" dataType="org.eclipse.emf.cdo.lm.modules.Version" required="true"
   * @generated
   */
  Version getInitialModuleVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Process#getInitialModuleVersion <em>Initial Module Version</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Initial Module Version</em>' attribute.
   * @see #getInitialModuleVersion()
   * @generated
   */
  void setInitialModuleVersion(Version value);

  /**
   * Returns the value of the '<em><b>Module Types</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.ModuleType}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.ModuleType#getProcess <em>Process</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Module Types</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getProcess_ModuleTypes()
   * @see org.eclipse.emf.cdo.lm.ModuleType#getProcess
   * @model opposite="process" containment="true"
   * @generated
   */
  EList<ModuleType> getModuleTypes();

  /**
   * @since 1.1
   */
  public boolean addDropType(String name, boolean release);

  public DropType getDropType(String name);

  /**
   * @since 1.1
   */
  public boolean addModuleType(String name);

  /**
   * @since 1.1
   */
  public ModuleType getModuleType(String name);

} // Process
