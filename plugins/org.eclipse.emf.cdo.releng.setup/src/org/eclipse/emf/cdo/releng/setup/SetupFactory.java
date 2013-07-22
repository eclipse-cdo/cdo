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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage
 * @generated
 */
public interface SetupFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SetupFactory eINSTANCE = org.eclipse.emf.cdo.releng.setup.impl.SetupFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Configuration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Configuration</em>'.
   * @generated
   */
  Configuration createConfiguration();

  /**
   * Returns a new object of class '<em>Project</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Project</em>'.
   * @generated
   */
  Project createProject();

  /**
   * Returns a new object of class '<em>Branch</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Branch</em>'.
   * @generated
   */
  Branch createBranch();

  /**
   * Returns a new object of class '<em>Tool Installation</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Tool Installation</em>'.
   * @generated
   */
  ToolInstallation createToolInstallation();

  /**
   * Returns a new object of class '<em>Eclipse Version</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Eclipse Version</em>'.
   * @generated
   */
  EclipseVersion createEclipseVersion();

  /**
   * Returns a new object of class '<em>Director Call</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Director Call</em>'.
   * @generated
   */
  DirectorCall createDirectorCall();

  /**
   * Returns a new object of class '<em>Installable Unit</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Installable Unit</em>'.
   * @generated
   */
  InstallableUnit createInstallableUnit();

  /**
   * Returns a new object of class '<em>P2 Repository</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>P2 Repository</em>'.
   * @generated
   */
  P2Repository createP2Repository();

  /**
   * Returns a new object of class '<em>Api Baseline</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Api Baseline</em>'.
   * @generated
   */
  ApiBaseline createApiBaseline();

  /**
   * Returns a new object of class '<em>Git Clone</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Git Clone</em>'.
   * @generated
   */
  GitClone createGitClone();

  /**
   * Returns a new object of class '<em>Setup</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Setup</em>'.
   * @generated
   */
  Setup createSetup();

  /**
   * Returns a new object of class '<em>Preferences</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Preferences</em>'.
   * @generated
   */
  Preferences createPreferences();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  SetupPackage getSetupPackage();

} // SetupFactory
