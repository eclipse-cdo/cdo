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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupFactory
 * @model kind="package"
 * @generated
 */
public interface SetupPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "setup";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/CDO/releng/setup/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "setup";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SetupPackage eINSTANCE = org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl <em>Configuration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfiguration()
   * @generated
   */
  int CONFIGURATION = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ToolInstallationImpl <em>Tool Installation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ToolInstallationImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getToolInstallation()
   * @generated
   */
  int TOOL_INSTALLATION = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl <em>Project</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProject()
   * @generated
   */
  int PROJECT = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl <em>Branch</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BranchImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBranch()
   * @generated
   */
  int BRANCH = 8;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl <em>Eclipse Version</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseVersion()
   * @generated
   */
  int ECLIPSE_VERSION = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.DirectorCallImpl <em>Director Call</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.DirectorCallImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getDirectorCall()
   * @generated
   */
  int DIRECTOR_CALL = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl <em>Installable Unit</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallableUnit()
   * @generated
   */
  int INSTALLABLE_UNIT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl <em>P2 Repository</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Repository()
   * @generated
   */
  int P2_REPOSITORY = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineImpl <em>Api Baseline</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getApiBaseline()
   * @generated
   */
  int API_BASELINE = 9;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneImpl <em>Git Clone</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.GitCloneImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getGitClone()
   * @generated
   */
  int GIT_CLONE = 10;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl <em>Preferences</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getPreferences()
   * @generated
   */
  int PREFERENCES = 0;

  /**
   * The feature id for the '<em><b>Director Calls</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOOL_INSTALLATION__DIRECTOR_CALLS = 0;

  /**
   * The feature id for the '<em><b>Tool Preferences</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOOL_INSTALLATION__TOOL_PREFERENCES = 1;

  /**
   * The number of structural features of the '<em>Tool Installation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOOL_INSTALLATION_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Director Calls</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__DIRECTOR_CALLS = TOOL_INSTALLATION__DIRECTOR_CALLS;

  /**
   * The feature id for the '<em><b>Tool Preferences</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__TOOL_PREFERENCES = TOOL_INSTALLATION__TOOL_PREFERENCES;

  /**
   * The feature id for the '<em><b>User Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__USER_NAME = TOOL_INSTALLATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Bundle Pool</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__BUNDLE_POOL = TOOL_INSTALLATION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Install Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__INSTALL_FOLDER = TOOL_INSTALLATION_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Git Prefix</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__GIT_PREFIX = TOOL_INSTALLATION_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Preferences</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES_FEATURE_COUNT = TOOL_INSTALLATION_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION__CONFIGURATION = 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION__VERSION = 1;

  /**
   * The feature id for the '<em><b>Director Call</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION__DIRECTOR_CALL = 2;

  /**
   * The number of structural features of the '<em>Eclipse Version</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_VERSION_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Installable Units</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTOR_CALL__INSTALLABLE_UNITS = 0;

  /**
   * The feature id for the '<em><b>P2 Repositories</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTOR_CALL__P2_REPOSITORIES = 1;

  /**
   * The number of structural features of the '<em>Director Call</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTOR_CALL_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Director Call</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT__DIRECTOR_CALL = 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT__ID = 1;

  /**
   * The number of structural features of the '<em>Installable Unit</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Director Call</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY__DIRECTOR_CALL = 0;

  /**
   * The feature id for the '<em><b>Url</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY__URL = 1;

  /**
   * The number of structural features of the '<em>P2 Repository</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Projects</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__PROJECTS = 0;

  /**
   * The feature id for the '<em><b>Eclipse Versions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__ECLIPSE_VERSIONS = 1;

  /**
   * The number of structural features of the '<em>Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Director Calls</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__DIRECTOR_CALLS = TOOL_INSTALLATION__DIRECTOR_CALLS;

  /**
   * The feature id for the '<em><b>Tool Preferences</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__TOOL_PREFERENCES = TOOL_INSTALLATION__TOOL_PREFERENCES;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__CONFIGURATION = TOOL_INSTALLATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Branches</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__BRANCHES = TOOL_INSTALLATION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__NAME = TOOL_INSTALLATION_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Api Baselines</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__API_BASELINES = TOOL_INSTALLATION_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Project</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_FEATURE_COUNT = TOOL_INSTALLATION_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Director Calls</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__DIRECTOR_CALLS = TOOL_INSTALLATION__DIRECTOR_CALLS;

  /**
   * The feature id for the '<em><b>Tool Preferences</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__TOOL_PREFERENCES = TOOL_INSTALLATION__TOOL_PREFERENCES;

  /**
   * The feature id for the '<em><b>Project</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__PROJECT = TOOL_INSTALLATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__NAME = TOOL_INSTALLATION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Git Clones</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__GIT_CLONES = TOOL_INSTALLATION_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Api Baseline</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__API_BASELINE = TOOL_INSTALLATION_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Mspec File Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__MSPEC_FILE_PATH = TOOL_INSTALLATION_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Clone Variable Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__CLONE_VARIABLE_NAME = TOOL_INSTALLATION_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Java Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__JAVA_VERSION = TOOL_INSTALLATION_FEATURE_COUNT + 6;

  /**
   * The number of structural features of the '<em>Branch</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH_FEATURE_COUNT = TOOL_INSTALLATION_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Project</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE__PROJECT = 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE__VERSION = 1;

  /**
   * The feature id for the '<em><b>Zip Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE__ZIP_LOCATION = 2;

  /**
   * The number of structural features of the '<em>Api Baseline</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Branch</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE__BRANCH = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE__NAME = 1;

  /**
   * The feature id for the '<em><b>Remote URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE__REMOTE_URI = 2;

  /**
   * The feature id for the '<em><b>Checkout Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE__CHECKOUT_BRANCH = 3;

  /**
   * The number of structural features of the '<em>Git Clone</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl <em>Setup</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetup()
   * @generated
   */
  int SETUP = 11;

  /**
   * The feature id for the '<em><b>Branch</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__BRANCH = 0;

  /**
   * The feature id for the '<em><b>Eclipse Version</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__ECLIPSE_VERSION = 1;

  /**
   * The feature id for the '<em><b>Preferences</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__PREFERENCES = 2;

  /**
   * The feature id for the '<em><b>Update Locations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__UPDATE_LOCATIONS = 3;

  /**
   * The number of structural features of the '<em>Setup</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ToolPreferenceImpl <em>Tool Preference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ToolPreferenceImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getToolPreference()
   * @generated
   */
  int TOOL_PREFERENCE = 12;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOOL_PREFERENCE__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOOL_PREFERENCE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Tool Preference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOOL_PREFERENCE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.JRE <em>JRE</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.JRE
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getJRE()
   * @generated
   */
  int JRE = 13;

  /**
   * The meta object id for the '<em>URI</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getURI()
   * @generated
   */
  int URI = 14;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Configuration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration
   * @generated
   */
  EClass getConfiguration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Configuration#getProjects <em>Projects</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Projects</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration#getProjects()
   * @see #getConfiguration()
   * @generated
   */
  EReference getConfiguration_Projects();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Configuration#getEclipseVersions <em>Eclipse Versions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Eclipse Versions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration#getEclipseVersions()
   * @see #getConfiguration()
   * @generated
   */
  EReference getConfiguration_EclipseVersions();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Project <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project
   * @generated
   */
  EClass getProject();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.Project#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getConfiguration()
   * @see #getProject()
   * @generated
   */
  EReference getProject_Configuration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Project#getBranches <em>Branches</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Branches</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getBranches()
   * @see #getProject()
   * @generated
   */
  EReference getProject_Branches();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Project#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getName()
   * @see #getProject()
   * @generated
   */
  EAttribute getProject_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Project#getApiBaselines <em>Api Baselines</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Api Baselines</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getApiBaselines()
   * @see #getProject()
   * @generated
   */
  EReference getProject_ApiBaselines();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Branch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch
   * @generated
   */
  EClass getBranch();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.Branch#getProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getProject()
   * @see #getBranch()
   * @generated
   */
  EReference getBranch_Project();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Branch#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getName()
   * @see #getBranch()
   * @generated
   */
  EAttribute getBranch_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Branch#getGitClones <em>Git Clones</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Git Clones</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getGitClones()
   * @see #getBranch()
   * @generated
   */
  EReference getBranch_GitClones();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Branch#getApiBaseline <em>Api Baseline</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Api Baseline</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getApiBaseline()
   * @see #getBranch()
   * @generated
   */
  EReference getBranch_ApiBaseline();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Branch#getMspecFilePath <em>Mspec File Path</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Mspec File Path</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getMspecFilePath()
   * @see #getBranch()
   * @generated
   */
  EAttribute getBranch_MspecFilePath();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Branch#getCloneVariableName <em>Clone Variable Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Clone Variable Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getCloneVariableName()
   * @see #getBranch()
   * @generated
   */
  EAttribute getBranch_CloneVariableName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Branch#getJavaVersion <em>Java Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Java Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getJavaVersion()
   * @see #getBranch()
   * @generated
   */
  EAttribute getBranch_JavaVersion();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ToolInstallation <em>Tool Installation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Tool Installation</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ToolInstallation
   * @generated
   */
  EClass getToolInstallation();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.ToolInstallation#getDirectorCalls <em>Director Calls</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Director Calls</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ToolInstallation#getDirectorCalls()
   * @see #getToolInstallation()
   * @generated
   */
  EReference getToolInstallation_DirectorCalls();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.ToolInstallation#getToolPreferences <em>Tool Preferences</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Tool Preferences</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ToolInstallation#getToolPreferences()
   * @see #getToolInstallation()
   * @generated
   */
  EReference getToolInstallation_ToolPreferences();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion <em>Eclipse Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion
   * @generated
   */
  EClass getEclipseVersion();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getConfiguration()
   * @see #getEclipseVersion()
   * @generated
   */
  EReference getEclipseVersion_Configuration();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getVersion()
   * @see #getEclipseVersion()
   * @generated
   */
  EAttribute getEclipseVersion_Version();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.releng.setup.EclipseVersion#getDirectorCall <em>Director Call</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Director Call</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseVersion#getDirectorCall()
   * @see #getEclipseVersion()
   * @generated
   */
  EReference getEclipseVersion_DirectorCall();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.DirectorCall <em>Director Call</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Director Call</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.DirectorCall
   * @generated
   */
  EClass getDirectorCall();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.DirectorCall#getInstallableUnits <em>Installable Units</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Installable Units</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.DirectorCall#getInstallableUnits()
   * @see #getDirectorCall()
   * @generated
   */
  EReference getDirectorCall_InstallableUnits();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.DirectorCall#getP2Repositories <em>P2 Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>P2 Repositories</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.DirectorCall#getP2Repositories()
   * @see #getDirectorCall()
   * @generated
   */
  EReference getDirectorCall_P2Repositories();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit <em>Installable Unit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Installable Unit</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit
   * @generated
   */
  EClass getInstallableUnit();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getDirectorCall <em>Director Call</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Director Call</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getDirectorCall()
   * @see #getInstallableUnit()
   * @generated
   */
  EReference getInstallableUnit_DirectorCall();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getId()
   * @see #getInstallableUnit()
   * @generated
   */
  EAttribute getInstallableUnit_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.P2Repository <em>P2 Repository</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>P2 Repository</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository
   * @generated
   */
  EClass getP2Repository();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getDirectorCall <em>Director Call</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Director Call</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getDirectorCall()
   * @see #getP2Repository()
   * @generated
   */
  EReference getP2Repository_DirectorCall();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getUrl <em>Url</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Url</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getUrl()
   * @see #getP2Repository()
   * @generated
   */
  EAttribute getP2Repository_Url();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ApiBaseline <em>Api Baseline</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Api Baseline</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaseline
   * @generated
   */
  EClass getApiBaseline();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.ApiBaseline#getProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaseline#getProject()
   * @see #getApiBaseline()
   * @generated
   */
  EReference getApiBaseline_Project();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaseline#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaseline#getVersion()
   * @see #getApiBaseline()
   * @generated
   */
  EAttribute getApiBaseline_Version();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaseline#getZipLocation <em>Zip Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Zip Location</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaseline#getZipLocation()
   * @see #getApiBaseline()
   * @generated
   */
  EAttribute getApiBaseline_ZipLocation();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.GitClone <em>Git Clone</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Git Clone</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitClone
   * @generated
   */
  EClass getGitClone();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.GitClone#getBranch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitClone#getBranch()
   * @see #getGitClone()
   * @generated
   */
  EReference getGitClone_Branch();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitClone#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitClone#getName()
   * @see #getGitClone()
   * @generated
   */
  EAttribute getGitClone_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitClone#getRemoteURI <em>Remote URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Remote URI</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitClone#getRemoteURI()
   * @see #getGitClone()
   * @generated
   */
  EAttribute getGitClone_RemoteURI();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitClone#getCheckoutBranch <em>Checkout Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Checkout Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitClone#getCheckoutBranch()
   * @see #getGitClone()
   * @generated
   */
  EAttribute getGitClone_CheckoutBranch();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Setup <em>Setup</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Setup</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup
   * @generated
   */
  EClass getSetup();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getBranch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getBranch()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_Branch();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getEclipseVersion <em>Eclipse Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Eclipse Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getEclipseVersion()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_EclipseVersion();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getPreferences <em>Preferences</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Preferences</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getPreferences()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_Preferences();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.Setup#getUpdateLocations <em>Update Locations</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Update Locations</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getUpdateLocations()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_UpdateLocations();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ToolPreference <em>Tool Preference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Tool Preference</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ToolPreference
   * @generated
   */
  EClass getToolPreference();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ToolPreference#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ToolPreference#getKey()
   * @see #getToolPreference()
   * @generated
   */
  EAttribute getToolPreference_Key();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ToolPreference#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ToolPreference#getValue()
   * @see #getToolPreference()
   * @generated
   */
  EAttribute getToolPreference_Value();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.releng.setup.JRE <em>JRE</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>JRE</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.JRE
   * @generated
   */
  EEnum getJRE();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Preferences <em>Preferences</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Preferences</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences
   * @generated
   */
  EClass getPreferences();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getUserName <em>User Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getUserName()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_UserName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePool <em>Bundle Pool</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Bundle Pool</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePool()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_BundlePool();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getInstallFolder <em>Install Folder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Install Folder</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getInstallFolder()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_InstallFolder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getGitPrefix <em>Git Prefix</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Git Prefix</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getGitPrefix()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_GitPrefix();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>URI</em>'.
   * @see org.eclipse.emf.common.util.URI
   * @model instanceClass="org.eclipse.emf.common.util.URI"
   * @generated
   */
  EDataType getURI();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  SetupFactory getSetupFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl <em>Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfiguration()
     * @generated
     */
    EClass CONFIGURATION = eINSTANCE.getConfiguration();

    /**
     * The meta object literal for the '<em><b>Projects</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIGURATION__PROJECTS = eINSTANCE.getConfiguration_Projects();

    /**
     * The meta object literal for the '<em><b>Eclipse Versions</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIGURATION__ECLIPSE_VERSIONS = eINSTANCE.getConfiguration_EclipseVersions();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl <em>Project</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProject()
     * @generated
     */
    EClass PROJECT = eINSTANCE.getProject();

    /**
     * The meta object literal for the '<em><b>Configuration</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__CONFIGURATION = eINSTANCE.getProject_Configuration();

    /**
     * The meta object literal for the '<em><b>Branches</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__BRANCHES = eINSTANCE.getProject_Branches();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROJECT__NAME = eINSTANCE.getProject_Name();

    /**
     * The meta object literal for the '<em><b>Api Baselines</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__API_BASELINES = eINSTANCE.getProject_ApiBaselines();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl <em>Branch</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.BranchImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBranch()
     * @generated
     */
    EClass BRANCH = eINSTANCE.getBranch();

    /**
     * The meta object literal for the '<em><b>Project</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BRANCH__PROJECT = eINSTANCE.getBranch_Project();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BRANCH__NAME = eINSTANCE.getBranch_Name();

    /**
     * The meta object literal for the '<em><b>Git Clones</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BRANCH__GIT_CLONES = eINSTANCE.getBranch_GitClones();

    /**
     * The meta object literal for the '<em><b>Api Baseline</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BRANCH__API_BASELINE = eINSTANCE.getBranch_ApiBaseline();

    /**
     * The meta object literal for the '<em><b>Mspec File Path</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BRANCH__MSPEC_FILE_PATH = eINSTANCE.getBranch_MspecFilePath();

    /**
     * The meta object literal for the '<em><b>Clone Variable Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BRANCH__CLONE_VARIABLE_NAME = eINSTANCE.getBranch_CloneVariableName();

    /**
     * The meta object literal for the '<em><b>Java Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BRANCH__JAVA_VERSION = eINSTANCE.getBranch_JavaVersion();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ToolInstallationImpl <em>Tool Installation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ToolInstallationImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getToolInstallation()
     * @generated
     */
    EClass TOOL_INSTALLATION = eINSTANCE.getToolInstallation();

    /**
     * The meta object literal for the '<em><b>Director Calls</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOOL_INSTALLATION__DIRECTOR_CALLS = eINSTANCE.getToolInstallation_DirectorCalls();

    /**
     * The meta object literal for the '<em><b>Tool Preferences</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOOL_INSTALLATION__TOOL_PREFERENCES = eINSTANCE.getToolInstallation_ToolPreferences();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl <em>Eclipse Version</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseVersionImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseVersion()
     * @generated
     */
    EClass ECLIPSE_VERSION = eINSTANCE.getEclipseVersion();

    /**
     * The meta object literal for the '<em><b>Configuration</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ECLIPSE_VERSION__CONFIGURATION = eINSTANCE.getEclipseVersion_Configuration();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_VERSION__VERSION = eINSTANCE.getEclipseVersion_Version();

    /**
     * The meta object literal for the '<em><b>Director Call</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ECLIPSE_VERSION__DIRECTOR_CALL = eINSTANCE.getEclipseVersion_DirectorCall();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.DirectorCallImpl <em>Director Call</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.DirectorCallImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getDirectorCall()
     * @generated
     */
    EClass DIRECTOR_CALL = eINSTANCE.getDirectorCall();

    /**
     * The meta object literal for the '<em><b>Installable Units</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DIRECTOR_CALL__INSTALLABLE_UNITS = eINSTANCE.getDirectorCall_InstallableUnits();

    /**
     * The meta object literal for the '<em><b>P2 Repositories</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DIRECTOR_CALL__P2_REPOSITORIES = eINSTANCE.getDirectorCall_P2Repositories();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl <em>Installable Unit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallableUnit()
     * @generated
     */
    EClass INSTALLABLE_UNIT = eINSTANCE.getInstallableUnit();

    /**
     * The meta object literal for the '<em><b>Director Call</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INSTALLABLE_UNIT__DIRECTOR_CALL = eINSTANCE.getInstallableUnit_DirectorCall();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INSTALLABLE_UNIT__ID = eINSTANCE.getInstallableUnit_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl <em>P2 Repository</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Repository()
     * @generated
     */
    EClass P2_REPOSITORY = eINSTANCE.getP2Repository();

    /**
     * The meta object literal for the '<em><b>Director Call</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference P2_REPOSITORY__DIRECTOR_CALL = eINSTANCE.getP2Repository_DirectorCall();

    /**
     * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute P2_REPOSITORY__URL = eINSTANCE.getP2Repository_Url();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineImpl <em>Api Baseline</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getApiBaseline()
     * @generated
     */
    EClass API_BASELINE = eINSTANCE.getApiBaseline();

    /**
     * The meta object literal for the '<em><b>Project</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference API_BASELINE__PROJECT = eINSTANCE.getApiBaseline_Project();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE__VERSION = eINSTANCE.getApiBaseline_Version();

    /**
     * The meta object literal for the '<em><b>Zip Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE__ZIP_LOCATION = eINSTANCE.getApiBaseline_ZipLocation();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneImpl <em>Git Clone</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.GitCloneImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getGitClone()
     * @generated
     */
    EClass GIT_CLONE = eINSTANCE.getGitClone();

    /**
     * The meta object literal for the '<em><b>Branch</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GIT_CLONE__BRANCH = eINSTANCE.getGitClone_Branch();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE__NAME = eINSTANCE.getGitClone_Name();

    /**
     * The meta object literal for the '<em><b>Remote URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE__REMOTE_URI = eINSTANCE.getGitClone_RemoteURI();

    /**
     * The meta object literal for the '<em><b>Checkout Branch</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE__CHECKOUT_BRANCH = eINSTANCE.getGitClone_CheckoutBranch();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl <em>Setup</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetup()
     * @generated
     */
    EClass SETUP = eINSTANCE.getSetup();

    /**
     * The meta object literal for the '<em><b>Branch</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__BRANCH = eINSTANCE.getSetup_Branch();

    /**
     * The meta object literal for the '<em><b>Eclipse Version</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__ECLIPSE_VERSION = eINSTANCE.getSetup_EclipseVersion();

    /**
     * The meta object literal for the '<em><b>Preferences</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__PREFERENCES = eINSTANCE.getSetup_Preferences();

    /**
     * The meta object literal for the '<em><b>Update Locations</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__UPDATE_LOCATIONS = eINSTANCE.getSetup_UpdateLocations();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ToolPreferenceImpl <em>Tool Preference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ToolPreferenceImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getToolPreference()
     * @generated
     */
    EClass TOOL_PREFERENCE = eINSTANCE.getToolPreference();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOOL_PREFERENCE__KEY = eINSTANCE.getToolPreference_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOOL_PREFERENCE__VALUE = eINSTANCE.getToolPreference_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.JRE <em>JRE</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.JRE
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getJRE()
     * @generated
     */
    EEnum JRE = eINSTANCE.getJRE();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl <em>Preferences</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getPreferences()
     * @generated
     */
    EClass PREFERENCES = eINSTANCE.getPreferences();

    /**
     * The meta object literal for the '<em><b>User Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__USER_NAME = eINSTANCE.getPreferences_UserName();

    /**
     * The meta object literal for the '<em><b>Bundle Pool</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__BUNDLE_POOL = eINSTANCE.getPreferences_BundlePool();

    /**
     * The meta object literal for the '<em><b>Install Folder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__INSTALL_FOLDER = eINSTANCE.getPreferences_InstallFolder();

    /**
     * The meta object literal for the '<em><b>Git Prefix</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__GIT_PREFIX = eINSTANCE.getPreferences_GitPrefix();

    /**
     * The meta object literal for the '<em>URI</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

  }

} // SetupPackage
