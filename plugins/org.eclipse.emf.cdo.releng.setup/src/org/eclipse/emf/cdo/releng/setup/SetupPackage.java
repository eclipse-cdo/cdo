/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
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
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MetaIndexImpl <em>Meta Index</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.MetaIndexImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMetaIndex()
   * @generated
   */
  int META_INDEX = 0;

  /**
   * The feature id for the '<em><b>Indexes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int META_INDEX__INDEXES = 0;

  /**
   * The number of structural features of the '<em>Meta Index</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int META_INDEX_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.IndexImpl <em>Index</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.IndexImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getIndex()
   * @generated
   */
  int INDEX = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INDEX__NAME = 0;

  /**
   * The feature id for the '<em><b>URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INDEX__URI = 1;

  /**
   * The feature id for the '<em><b>Old UR Is</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INDEX__OLD_UR_IS = 2;

  /**
   * The number of structural features of the '<em>Index</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INDEX_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl <em>Task Container</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskContainer()
   * @generated
   */
  int SETUP_TASK_CONTAINER = 10;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl <em>Configuration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfiguration()
   * @generated
   */
  int CONFIGURATION = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl <em>Project</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProject()
   * @generated
   */
  int PROJECT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl <em>Branch</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BranchImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBranch()
   * @generated
   */
  int BRANCH = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl <em>Installable Unit</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getInstallableUnit()
   * @generated
   */
  int INSTALLABLE_UNIT = 18;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl <em>P2 Repository</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.P2RepositoryImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Repository()
   * @generated
   */
  int P2_REPOSITORY = 19;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl <em>Preferences</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getPreferences()
   * @generated
   */
  int PREFERENCES = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl <em>Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTask()
   * @generated
   */
  int SETUP_TASK = 9;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl <em>Link Location Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getLinkLocationTask()
   * @generated
   */
  int LINK_LOCATION_TASK = 16;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl <em>Eclipse Preference Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipsePreferenceTask()
   * @generated
   */
  int ECLIPSE_PREFERENCE_TASK = 40;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl <em>Setup</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetup()
   * @generated
   */
  int SETUP = 8;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl <em>P2 Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Task()
   * @generated
   */
  int P2_TASK = 17;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK_CONTAINER__SETUP_TASKS = 0;

  /**
   * The number of structural features of the '<em>Task Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.ScopeRoot <em>Scope Root</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.ScopeRoot
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getScopeRoot()
   * @generated
   */
  int SCOPE_ROOT = 11;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SCOPE_ROOT__SETUP_TASKS = SETUP_TASK_CONTAINER__SETUP_TASKS;

  /**
   * The number of structural features of the '<em>Scope Root</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SCOPE_ROOT_FEATURE_COUNT = SETUP_TASK_CONTAINER_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl <em>Configurable Item</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfigurableItem()
   * @generated
   */
  int CONFIGURABLE_ITEM = 4;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE_ITEM__SETUP_TASKS = SCOPE_ROOT__SETUP_TASKS;

  /**
   * The number of structural features of the '<em>Configurable Item</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE_ITEM_FEATURE_COUNT = SCOPE_ROOT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseImpl <em>Eclipse</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipse()
   * @generated
   */
  int ECLIPSE = 2;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE__SETUP_TASKS = CONFIGURABLE_ITEM__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE__CONFIGURATION = CONFIGURABLE_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE__VERSION = CONFIGURABLE_ITEM_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Eclipse</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_FEATURE_COUNT = CONFIGURABLE_ITEM_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__SETUP_TASKS = SCOPE_ROOT__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Eclipse Versions</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__ECLIPSE_VERSIONS = SCOPE_ROOT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Projects</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION__PROJECTS = SCOPE_ROOT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURATION_FEATURE_COUNT = SCOPE_ROOT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__SETUP_TASKS = CONFIGURABLE_ITEM__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__CONFIGURATION = CONFIGURABLE_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Branches</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__BRANCHES = CONFIGURABLE_ITEM_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__NAME = CONFIGURABLE_ITEM_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__LABEL = CONFIGURABLE_ITEM_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__RESTRICTIONS = CONFIGURABLE_ITEM_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Project</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_FEATURE_COUNT = CONFIGURABLE_ITEM_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__SETUP_TASKS = CONFIGURABLE_ITEM__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Project</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__PROJECT = CONFIGURABLE_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__NAME = CONFIGURABLE_ITEM_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH__RESTRICTIONS = CONFIGURABLE_ITEM_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Branch</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BRANCH_FEATURE_COUNT = CONFIGURABLE_ITEM_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__SETUP_TASKS = SCOPE_ROOT__SETUP_TASKS;

  /**
   * The feature id for the '<em><b>Install Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__INSTALL_FOLDER = SCOPE_ROOT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Bundle Pool Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__BUNDLE_POOL_FOLDER = SCOPE_ROOT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Bundle Pool Folder TP</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__BUNDLE_POOL_FOLDER_TP = SCOPE_ROOT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Accepted Licenses</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES__ACCEPTED_LICENSES = SCOPE_ROOT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Preferences</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCES_FEATURE_COUNT = SCOPE_ROOT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl <em>Api Baseline Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getApiBaselineTask()
   * @generated
   */
  int API_BASELINE_TASK = 36;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl <em>Git Clone Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getGitCloneTask()
   * @generated
   */
  int GIT_CLONE_TASK = 37;

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
   * The feature id for the '<em><b>Preferences</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP__PREFERENCES = 2;

  /**
   * The number of structural features of the '<em>Setup</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_FEATURE_COUNT = 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__REQUIREMENTS = 0;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__RESTRICTIONS = 1;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__DISABLED = 2;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__SCOPE = 3;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__EXCLUDED_TRIGGERS = 4;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK__DOCUMENTATION = 5;

  /**
   * The number of structural features of the '<em>Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SETUP_TASK_FEATURE_COUNT = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl <em>Working Set Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getWorkingSetTask()
   * @generated
   */
  int WORKING_SET_TASK = 43;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl <em>Compound Setup Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getCompoundSetupTask()
   * @generated
   */
  int COMPOUND_SETUP_TASK = 12;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Setup Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__SETUP_TASKS = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Compound Setup Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOUND_SETUP_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl <em>Buckminster Import Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBuckminsterImportTask()
   * @generated
   */
  int BUCKMINSTER_IMPORT_TASK = 21;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl <em>Eclipse Ini Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseIniTask()
   * @generated
   */
  int ECLIPSE_INI_TASK = 15;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BasicMaterializationTaskImpl <em>Basic Materialization Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BasicMaterializationTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBasicMaterializationTask()
   * @generated
   */
  int BASIC_MATERIALIZATION_TASK = 20;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ResourceCopyTaskImpl <em>Resource Copy Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ResourceCopyTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getResourceCopyTask()
   * @generated
   */
  int RESOURCE_COPY_TASK = 44;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TextModifyTaskImpl <em>Text Modify Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TextModifyTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTextModifyTask()
   * @generated
   */
  int TEXT_MODIFY_TASK = 46;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TextModificationImpl <em>Text Modification</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TextModificationImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTextModification()
   * @generated
   */
  int TEXT_MODIFICATION = 47;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl <em>Key Binding Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getKeyBindingTask()
   * @generated
   */
  int KEY_BINDING_TASK = 48;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.CommandParameterImpl <em>Command Parameter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.CommandParameterImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getCommandParameter()
   * @generated
   */
  int COMMAND_PARAMETER = 50;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl <em>Context Variable Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getContextVariableTask()
   * @generated
   */
  int CONTEXT_VARIABLE_TASK = 13;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ResourceCreationTaskImpl <em>Resource Creation Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ResourceCreationTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getResourceCreationTask()
   * @generated
   */
  int RESOURCE_CREATION_TASK = 45;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl <em>Materialization Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMaterializationTask()
   * @generated
   */
  int MATERIALIZATION_TASK = 22;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SourceLocatorImpl <em>Source Locator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.SourceLocatorImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSourceLocator()
   * @generated
   */
  int SOURCE_LOCATOR = 24;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ComponentImpl <em>Component</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ComponentImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponent()
   * @generated
   */
  int COMPONENT = 23;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl <em>Mylyn Query Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMylynQueryTask()
   * @generated
   */
  int MYLYN_QUERY_TASK = 51;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.AutomaticSourceLocatorImpl <em>Automatic Source Locator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.AutomaticSourceLocatorImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getAutomaticSourceLocator()
   * @generated
   */
  int AUTOMATIC_SOURCE_LOCATOR = 26;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl <em>Manual Source Locator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getManualSourceLocator()
   * @generated
   */
  int MANUAL_SOURCE_LOCATOR = 25;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.RedirectionTaskImpl <em>Redirection Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.RedirectionTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getRedirectionTask()
   * @generated
   */
  int REDIRECTION_TASK = 35;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__TYPE = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__VALUE = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>String Substitution</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__LABEL = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Choices</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK__CHOICES = SETUP_TASK_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Context Variable Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_VARIABLE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.VariableChoiceImpl <em>Variable Choice</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.VariableChoiceImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVariableChoice()
   * @generated
   */
  int VARIABLE_CHOICE = 14;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_CHOICE__VALUE = 0;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_CHOICE__LABEL = 1;

  /**
   * The number of structural features of the '<em>Variable Choice</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_CHOICE_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Option</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__OPTION = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__VALUE = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Vm</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK__VM = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Eclipse Ini Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_INI_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__PATH = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Link Location Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_LOCATION_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Installable Units</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__INSTALLABLE_UNITS = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>P2 Repositories</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__P2_REPOSITORIES = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>License Confirmation Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__LICENSE_CONFIRMATION_DISABLED = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Merge Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK__MERGE_DISABLED = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>P2 Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT__ID = 0;

  /**
   * The feature id for the '<em><b>Version Range</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT__VERSION_RANGE = 1;

  /**
   * The number of structural features of the '<em>Installable Unit</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INSTALLABLE_UNIT_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY__URL = 0;

  /**
   * The number of structural features of the '<em>P2 Repository</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int P2_REPOSITORY_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Target Platform</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK__TARGET_PLATFORM = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Basic Materialization Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASIC_MATERIALIZATION_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__REQUIREMENTS = BASIC_MATERIALIZATION_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__RESTRICTIONS = BASIC_MATERIALIZATION_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__DISABLED = BASIC_MATERIALIZATION_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__SCOPE = BASIC_MATERIALIZATION_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__EXCLUDED_TRIGGERS = BASIC_MATERIALIZATION_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__DOCUMENTATION = BASIC_MATERIALIZATION_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Target Platform</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__TARGET_PLATFORM = BASIC_MATERIALIZATION_TASK__TARGET_PLATFORM;

  /**
   * The feature id for the '<em><b>Mspec</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK__MSPEC = BASIC_MATERIALIZATION_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Buckminster Import Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUCKMINSTER_IMPORT_TASK_FEATURE_COUNT = BASIC_MATERIALIZATION_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__REQUIREMENTS = BASIC_MATERIALIZATION_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__RESTRICTIONS = BASIC_MATERIALIZATION_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__DISABLED = BASIC_MATERIALIZATION_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__SCOPE = BASIC_MATERIALIZATION_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__EXCLUDED_TRIGGERS = BASIC_MATERIALIZATION_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__DOCUMENTATION = BASIC_MATERIALIZATION_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Target Platform</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__TARGET_PLATFORM = BASIC_MATERIALIZATION_TASK__TARGET_PLATFORM;

  /**
   * The feature id for the '<em><b>Root Components</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__ROOT_COMPONENTS = BASIC_MATERIALIZATION_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Source Locators</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__SOURCE_LOCATORS = BASIC_MATERIALIZATION_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>P2 Repositories</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK__P2_REPOSITORIES = BASIC_MATERIALIZATION_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Materialization Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATERIALIZATION_TASK_FEATURE_COUNT = BASIC_MATERIALIZATION_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__TYPE = 1;

  /**
   * The feature id for the '<em><b>Version Range</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__VERSION_RANGE = 2;

  /**
   * The number of structural features of the '<em>Component</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_FEATURE_COUNT = 3;

  /**
   * The number of structural features of the '<em>Source Locator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SOURCE_LOCATOR_FEATURE_COUNT = 0;

  /**
   * The feature id for the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MANUAL_SOURCE_LOCATOR__LOCATION = SOURCE_LOCATOR_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Component Name Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN = SOURCE_LOCATOR_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Component Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES = SOURCE_LOCATOR_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Manual Source Locator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MANUAL_SOURCE_LOCATOR_FEATURE_COUNT = SOURCE_LOCATOR_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Root Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTOMATIC_SOURCE_LOCATOR__ROOT_FOLDER = SOURCE_LOCATOR_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Locate Nested Projects</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTOMATIC_SOURCE_LOCATOR__LOCATE_NESTED_PROJECTS = SOURCE_LOCATOR_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Automatic Source Locator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTOMATIC_SOURCE_LOCATOR_FEATURE_COUNT = SOURCE_LOCATOR_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletTaskImpl <em>Targlet Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TargletTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletTask()
   * @generated
   */
  int TARGLET_TASK = 30;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl <em>Targlet</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TargletImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTarglet()
   * @generated
   */
  int TARGLET = 32;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.RepositoryListImpl <em>Repository List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.RepositoryListImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getRepositoryList()
   * @generated
   */
  int REPOSITORY_LIST = 34;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectSetImportTaskImpl <em>Project Set Import Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectSetImportTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProjectSetImportTask()
   * @generated
   */
  int PROJECT_SET_IMPORT_TASK = 38;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingContextImpl <em>Key Binding Context</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.KeyBindingContextImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getKeyBindingContext()
   * @generated
   */
  int KEY_BINDING_CONTEXT = 49;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargetPlatformTaskImpl <em>Target Platform Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TargetPlatformTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargetPlatformTask()
   * @generated
   */
  int TARGET_PLATFORM_TASK = 39;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.JRETaskImpl <em>JRE Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.JRETaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getJRETask()
   * @generated
   */
  int JRE_TASK = 57;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl <em>File Association Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getFileAssociationTask()
   * @generated
   */
  int FILE_ASSOCIATION_TASK = 41;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.FileEditorImpl <em>File Editor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.FileEditorImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getFileEditor()
   * @generated
   */
  int FILE_EDITOR = 42;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl <em>Mylyn Queries Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMylynQueriesTask()
   * @generated
   */
  int MYLYN_QUERIES_TASK = 52;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.QueryImpl <em>Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.QueryImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getQuery()
   * @generated
   */
  int QUERY = 53;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.QueryAttributeImpl <em>Query Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.QueryAttributeImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getQueryAttribute()
   * @generated
   */
  int QUERY_ATTRIBUTE = 54;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl <em>Mylyn Builds Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMylynBuildsTask()
   * @generated
   */
  int MYLYN_BUILDS_TASK = 55;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BuildPlanImpl <em>Build Plan</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.BuildPlanImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBuildPlan()
   * @generated
   */
  int BUILD_PLAN = 56;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ComponentExtensionImpl <em>Component Extension</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ComponentExtensionImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponentExtension()
   * @generated
   */
  int COMPONENT_EXTENSION = 27;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_EXTENSION__DEPENDENCIES = 0;

  /**
   * The number of structural features of the '<em>Component Extension</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_EXTENSION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ComponentDefinitionImpl <em>Component Definition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.ComponentDefinitionImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponentDefinition()
   * @generated
   */
  int COMPONENT_DEFINITION = 28;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_DEFINITION__DEPENDENCIES = COMPONENT_EXTENSION__DEPENDENCIES;

  /**
   * The feature id for the '<em><b>ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_DEFINITION__ID = COMPONENT_EXTENSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_DEFINITION__VERSION = COMPONENT_EXTENSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Component Definition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_DEFINITION_FEATURE_COUNT = COMPONENT_EXTENSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImportTaskImpl <em>Targlet Import Task</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TargletImportTaskImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletImportTask()
   * @generated
   */
  int TARGLET_IMPORT_TASK = 29;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Targlet URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK__TARGLET_URI = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Targlet Import Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_IMPORT_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Roots</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__ROOTS = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Source Locators</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__SOURCE_LOCATORS = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Repository Lists</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__REPOSITORY_LISTS = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Active Repository List</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__ACTIVE_REPOSITORY_LIST = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Active P2 Repositories</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK__ACTIVE_P2_REPOSITORIES = SETUP_TASK_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Targlet Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletContainerImpl <em>Targlet Container</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TargletContainerImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletContainer()
   * @generated
   */
  int TARGLET_CONTAINER = 31;

  /**
   * The feature id for the '<em><b>Targlets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_CONTAINER__TARGLETS = 0;

  /**
   * The number of structural features of the '<em>Targlet Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletDataImpl <em>Targlet Data</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.impl.TargletDataImpl
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletData()
   * @generated
   */
  int TARGLET_DATA = 33;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA__NAME = 0;

  /**
   * The feature id for the '<em><b>Roots</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA__ROOTS = 1;

  /**
   * The feature id for the '<em><b>Source Locators</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA__SOURCE_LOCATORS = 2;

  /**
   * The feature id for the '<em><b>Repository Lists</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA__REPOSITORY_LISTS = 3;

  /**
   * The feature id for the '<em><b>Active Repository List</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA__ACTIVE_REPOSITORY_LIST = 4;

  /**
   * The feature id for the '<em><b>Active P2 Repositories</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA__ACTIVE_P2_REPOSITORIES = 5;

  /**
   * The number of structural features of the '<em>Targlet Data</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_DATA_FEATURE_COUNT = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET__NAME = TARGLET_DATA__NAME;

  /**
   * The feature id for the '<em><b>Roots</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET__ROOTS = TARGLET_DATA__ROOTS;

  /**
   * The feature id for the '<em><b>Source Locators</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET__SOURCE_LOCATORS = TARGLET_DATA__SOURCE_LOCATORS;

  /**
   * The feature id for the '<em><b>Repository Lists</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET__REPOSITORY_LISTS = TARGLET_DATA__REPOSITORY_LISTS;

  /**
   * The feature id for the '<em><b>Active Repository List</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET__ACTIVE_REPOSITORY_LIST = TARGLET_DATA__ACTIVE_REPOSITORY_LIST;

  /**
   * The feature id for the '<em><b>Active P2 Repositories</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET__ACTIVE_P2_REPOSITORIES = TARGLET_DATA__ACTIVE_P2_REPOSITORIES;

  /**
   * The number of structural features of the '<em>Targlet</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGLET_FEATURE_COUNT = TARGLET_DATA_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>P2 Repositories</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_LIST__P2_REPOSITORIES = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_LIST__NAME = 1;

  /**
   * The number of structural features of the '<em>Repository List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_LIST_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Source URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__SOURCE_URL = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK__TARGET_URL = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Redirection Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REDIRECTION_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__VERSION = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Container Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__CONTAINER_FOLDER = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Zip Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK__ZIP_LOCATION = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Api Baseline Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int API_BASELINE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__LOCATION = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Remote Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__REMOTE_NAME = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Remote URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__REMOTE_URI = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>User ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__USER_ID = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Checkout Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK__CHECKOUT_BRANCH = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Git Clone Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GIT_CLONE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK__URL = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Project Set Import Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_SET_IMPORT_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK__NAME = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Target Platform Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TARGET_PLATFORM_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__KEY = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK__VALUE = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Eclipse Preference Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECLIPSE_PREFERENCE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>File Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__FILE_PATTERN = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Default Editor ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Editors</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK__EDITORS = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>File Association Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_ASSOCIATION_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_EDITOR__ID = 0;

  /**
   * The number of structural features of the '<em>File Editor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILE_EDITOR_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Working Sets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK__WORKING_SETS = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Working Set Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKING_SET_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Source URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__SOURCE_URL = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK__TARGET_URL = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Resource Copy Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_COPY_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Content</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__CONTENT = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__TARGET_URL = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Encoding</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK__ENCODING = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Resource Creation Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RESOURCE_CREATION_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__URL = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Modifications</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__MODIFICATIONS = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Encoding</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK__ENCODING = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Text Modify Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFY_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFICATION__PATTERN = 0;

  /**
   * The feature id for the '<em><b>Substitutions</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFICATION__SUBSTITUTIONS = 1;

  /**
   * The number of structural features of the '<em>Text Modification</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEXT_MODIFICATION_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Scheme</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__SCHEME = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Contexts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__CONTEXTS = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Platform</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__PLATFORM = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Locale</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__LOCALE = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Keys</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__KEYS = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Command</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__COMMAND = SETUP_TASK_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Command Parameters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK__COMMAND_PARAMETERS = SETUP_TASK_FEATURE_COUNT + 6;

  /**
   * The number of structural features of the '<em>Key Binding Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_CONTEXT__ID = 0;

  /**
   * The number of structural features of the '<em>Key Binding Context</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int KEY_BINDING_CONTEXT_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMAND_PARAMETER__ID = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMAND_PARAMETER__VALUE = 1;

  /**
   * The number of structural features of the '<em>Command Parameter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMAND_PARAMETER_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__CONNECTOR_KIND = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Summary</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__SUMMARY = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Repository URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__REPOSITORY_URL = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Relative URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK__RELATIVE_URL = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Mylyn Query Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERY_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__CONNECTOR_KIND = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Repository URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__REPOSITORY_URL = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Queries</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK__QUERIES = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Mylyn Queries Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_QUERIES_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Task</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__TASK = 0;

  /**
   * The feature id for the '<em><b>Summary</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__SUMMARY = 1;

  /**
   * The feature id for the '<em><b>URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__URL = 2;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__ATTRIBUTES = 3;

  /**
   * The number of structural features of the '<em>Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_FEATURE_COUNT = 4;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_ATTRIBUTE__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_ATTRIBUTE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Query Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_ATTRIBUTE_FEATURE_COUNT = 2;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__CONNECTOR_KIND = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Server URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__SERVER_URL = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Build Plans</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK__BUILD_PLANS = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Mylyn Builds Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MYLYN_BUILDS_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILD_PLAN__NAME = 0;

  /**
   * The number of structural features of the '<em>Build Plan</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BUILD_PLAN_FEATURE_COUNT = 1;

  /**
   * The feature id for the '<em><b>Requirements</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__REQUIREMENTS = SETUP_TASK__REQUIREMENTS;

  /**
   * The feature id for the '<em><b>Restrictions</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__RESTRICTIONS = SETUP_TASK__RESTRICTIONS;

  /**
   * The feature id for the '<em><b>Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__DISABLED = SETUP_TASK__DISABLED;

  /**
   * The feature id for the '<em><b>Scope</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__SCOPE = SETUP_TASK__SCOPE;

  /**
   * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__EXCLUDED_TRIGGERS = SETUP_TASK__EXCLUDED_TRIGGERS;

  /**
   * The feature id for the '<em><b>Documentation</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__DOCUMENTATION = SETUP_TASK__DOCUMENTATION;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__VERSION = SETUP_TASK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK__LOCATION = SETUP_TASK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>JRE Task</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JRE_TASK_FEATURE_COUNT = SETUP_TASK_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope <em>Task Scope</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskScope()
   * @generated
   */
  int SETUP_TASK_SCOPE = 58;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.Trigger <em>Trigger</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.Trigger
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTrigger()
   * @generated
   */
  int TRIGGER = 59;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.ComponentType <em>Component Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.ComponentType
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponentType()
   * @generated
   */
  int COMPONENT_TYPE = 60;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.setup.VariableType <em>Variable Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.VariableType
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVariableType()
   * @generated
   */
  int VARIABLE_TYPE = 61;

  /**
   * The meta object id for the '<em>URI</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getURI()
   * @generated
   */
  int URI = 64;

  /**
   * The meta object id for the '<em>License Info</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.setup.LicenseInfo
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getLicenseInfo()
   * @generated
   */
  int LICENSE_INFO = 65;

  /**
   * The meta object id for the '<em>Version</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.equinox.p2.metadata.Version
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVersion()
   * @generated
   */
  int VERSION = 66;

  /**
   * The meta object id for the '<em>Version Range</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.equinox.p2.metadata.VersionRange
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVersionRange()
   * @generated
   */
  int VERSION_RANGE = 67;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.MetaIndex <em>Meta Index</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Meta Index</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MetaIndex
   * @generated
   */
  EClass getMetaIndex();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.MetaIndex#getIndexes <em>Indexes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Indexes</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MetaIndex#getIndexes()
   * @see #getMetaIndex()
   * @generated
   */
  EReference getMetaIndex_Indexes();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Index <em>Index</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Index</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Index
   * @generated
   */
  EClass getIndex();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Index#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Index#getName()
   * @see #getIndex()
   * @generated
   */
  EAttribute getIndex_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Index#getURI <em>URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>URI</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Index#getURI()
   * @see #getIndex()
   * @generated
   */
  EAttribute getIndex_URI();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.releng.setup.Index#getOldURIs <em>Old UR Is</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Old UR Is</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Index#getOldURIs()
   * @see #getIndex()
   * @generated
   */
  EAttribute getIndex_OldURIs();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Eclipse <em>Eclipse</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Eclipse
   * @generated
   */
  EClass getEclipse();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.Eclipse#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Eclipse#getConfiguration()
   * @see #getEclipse()
   * @generated
   */
  EReference getEclipse_Configuration();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Eclipse#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Eclipse#getVersion()
   * @see #getEclipse()
   * @generated
   */
  EAttribute getEclipse_Version();

  /**
   * The meta object id for the '<em>Exception</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.Exception
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getException()
   * @generated
   */
  int EXCEPTION = 63;

  /**
   * The meta object id for the '<em>Trigger Set</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.util.Set
   * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTriggerSet()
   * @generated
   */
  int TRIGGER_SET = 62;

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
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Project#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getLabel()
   * @see #getProject()
   * @generated
   */
  EAttribute getProject_Label();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.Project#getRestrictions <em>Restrictions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Restrictions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Project#getRestrictions()
   * @see #getProject()
   * @generated
   */
  EReference getProject_Restrictions();

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
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.Branch#getRestrictions <em>Restrictions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Restrictions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Branch#getRestrictions()
   * @see #getBranch()
   * @generated
   */
  EReference getBranch_Restrictions();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask <em>Api Baseline Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Api Baseline Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask
   * @generated
   */
  EClass getApiBaselineTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getVersion()
   * @see #getApiBaselineTask()
   * @generated
   */
  EAttribute getApiBaselineTask_Version();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getContainerFolder <em>Container Folder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Container Folder</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getContainerFolder()
   * @see #getApiBaselineTask()
   * @generated
   */
  EAttribute getApiBaselineTask_ContainerFolder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getZipLocation <em>Zip Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Zip Location</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask#getZipLocation()
   * @see #getApiBaselineTask()
   * @generated
   */
  EAttribute getApiBaselineTask_ZipLocation();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask <em>Git Clone Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Git Clone Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask
   * @generated
   */
  EClass getGitCloneTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getLocation <em>Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Location</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getLocation()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_Location();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName <em>Remote Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Remote Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteName()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_RemoteName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI <em>Remote URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Remote URI</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getRemoteURI()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_RemoteURI();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch <em>Checkout Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Checkout Branch</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getCheckoutBranch()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_CheckoutBranch();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask#getUserID <em>User ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>User ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask#getUserID()
   * @see #getGitCloneTask()
   * @generated
   */
  EAttribute getGitCloneTask_UserID();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask <em>Project Set Import Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Project Set Import Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask
   * @generated
   */
  EClass getProjectSetImportTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask#getURL <em>URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask#getURL()
   * @see #getProjectSetImportTask()
   * @generated
   */
  EAttribute getProjectSetImportTask_URL();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.P2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>P2 Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task
   * @generated
   */
  EClass getP2Task();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits <em>Installable Units</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Installable Units</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits()
   * @see #getP2Task()
   * @generated
   */
  EReference getP2Task_InstallableUnits();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.P2Task#isLicenseConfirmationDisabled <em>License Confirmation Disabled</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>License Confirmation Disabled</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#isLicenseConfirmationDisabled()
   * @see #getP2Task()
   * @generated
   */
  EAttribute getP2Task_LicenseConfirmationDisabled();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.P2Task#isMergeDisabled <em>Merge Disabled</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Merge Disabled</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#isMergeDisabled()
   * @see #getP2Task()
   * @generated
   */
  EAttribute getP2Task_MergeDisabled();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.P2Task#getP2Repositories <em>P2 Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>P2 Repositories</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task#getP2Repositories()
   * @see #getP2Task()
   * @generated
   */
  EReference getP2Task_P2Repositories();

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
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getID <em>ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getID()
   * @see #getInstallableUnit()
   * @generated
   */
  EAttribute getInstallableUnit_ID();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getVersionRange <em>Version Range</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version Range</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getVersionRange()
   * @see #getInstallableUnit()
   * @generated
   */
  EAttribute getInstallableUnit_VersionRange();

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
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getURL <em>URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getURL()
   * @see #getP2Repository()
   * @generated
   */
  EAttribute getP2Repository_URL();

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
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.setup.Setup#getPreferences <em>Preferences</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Preferences</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Setup#getPreferences()
   * @see #getSetup()
   * @generated
   */
  EReference getSetup_Preferences();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.SetupTask <em>Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask
   * @generated
   */
  EClass getSetupTask();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getRequirements <em>Requirements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Requirements</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getRequirements()
   * @see #getSetupTask()
   * @generated
   */
  EReference getSetupTask_Requirements();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getRestrictions <em>Restrictions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Restrictions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getRestrictions()
   * @see #getSetupTask()
   * @generated
   */
  EReference getSetupTask_Restrictions();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getScope <em>Scope</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Scope</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getScope()
   * @see #getSetupTask()
   * @generated
   */
  EAttribute getSetupTask_Scope();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getExcludedTriggers <em>Excluded Triggers</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Excluded Triggers</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getExcludedTriggers()
   * @see #getSetupTask()
   * @generated
   */
  EAttribute getSetupTask_ExcludedTriggers();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getDocumentation <em>Documentation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Documentation</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#getDocumentation()
   * @see #getSetupTask()
   * @generated
   */
  EAttribute getSetupTask_Documentation();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#isDisabled <em>Disabled</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Disabled</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask#isDisabled()
   * @see #getSetupTask()
   * @generated
   */
  EAttribute getSetupTask_Disabled();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask <em>Working Set Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Working Set Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.WorkingSetTask
   * @generated
   */
  EClass getWorkingSetTask();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask#getWorkingSets <em>Working Sets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Working Sets</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.WorkingSetTask#getWorkingSets()
   * @see #getWorkingSetTask()
   * @generated
   */
  EReference getWorkingSetTask_WorkingSets();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ResourceCopyTask <em>Resource Copy Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Resource Copy Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCopyTask
   * @generated
   */
  EClass getResourceCopyTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ResourceCopyTask#getSourceURL <em>Source URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Source URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCopyTask#getSourceURL()
   * @see #getResourceCopyTask()
   * @generated
   */
  EAttribute getResourceCopyTask_SourceURL();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ResourceCopyTask#getTargetURL <em>Target URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCopyTask#getTargetURL()
   * @see #getResourceCopyTask()
   * @generated
   */
  EAttribute getResourceCopyTask_TargetURL();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TextModifyTask <em>Text Modify Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Text Modify Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModifyTask
   * @generated
   */
  EClass getTextModifyTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TextModifyTask#getURL <em>URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModifyTask#getURL()
   * @see #getTextModifyTask()
   * @generated
   */
  EAttribute getTextModifyTask_URL();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.TextModifyTask#getModifications <em>Modifications</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Modifications</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModifyTask#getModifications()
   * @see #getTextModifyTask()
   * @generated
   */
  EReference getTextModifyTask_Modifications();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TextModifyTask#getEncoding <em>Encoding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Encoding</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModifyTask#getEncoding()
   * @see #getTextModifyTask()
   * @generated
   */
  EAttribute getTextModifyTask_Encoding();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TextModification <em>Text Modification</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Text Modification</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModification
   * @generated
   */
  EClass getTextModification();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TextModification#getPattern <em>Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModification#getPattern()
   * @see #getTextModification()
   * @generated
   */
  EAttribute getTextModification_Pattern();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.releng.setup.TextModification#getSubstitutions <em>Substitutions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Substitutions</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TextModification#getSubstitutions()
   * @see #getTextModification()
   * @generated
   */
  EAttribute getTextModification_Substitutions();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask <em>Key Binding Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Key Binding Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask
   * @generated
   */
  EClass getKeyBindingTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getScheme <em>Scheme</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Scheme</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getScheme()
   * @see #getKeyBindingTask()
   * @generated
   */
  EAttribute getKeyBindingTask_Scheme();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getContexts <em>Contexts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Contexts</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getContexts()
   * @see #getKeyBindingTask()
   * @generated
   */
  EReference getKeyBindingTask_Contexts();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getPlatform <em>Platform</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Platform</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getPlatform()
   * @see #getKeyBindingTask()
   * @generated
   */
  EAttribute getKeyBindingTask_Platform();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getLocale <em>Locale</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Locale</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getLocale()
   * @see #getKeyBindingTask()
   * @generated
   */
  EAttribute getKeyBindingTask_Locale();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getKeys <em>Keys</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Keys</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getKeys()
   * @see #getKeyBindingTask()
   * @generated
   */
  EAttribute getKeyBindingTask_Keys();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommand <em>Command</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Command</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommand()
   * @see #getKeyBindingTask()
   * @generated
   */
  EAttribute getKeyBindingTask_Command();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommandParameters <em>Command Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Command Parameters</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommandParameters()
   * @see #getKeyBindingTask()
   * @generated
   */
  EReference getKeyBindingTask_CommandParameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingContext <em>Key Binding Context</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Key Binding Context</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingContext
   * @generated
   */
  EClass getKeyBindingContext();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingContext#getID <em>ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingContext#getID()
   * @see #getKeyBindingContext()
   * @generated
   */
  EAttribute getKeyBindingContext_ID();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.CommandParameter <em>Command Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Command Parameter</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CommandParameter
   * @generated
   */
  EClass getCommandParameter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.CommandParameter#getID <em>ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CommandParameter#getID()
   * @see #getCommandParameter()
   * @generated
   */
  EAttribute getCommandParameter_ID();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.CommandParameter#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CommandParameter#getValue()
   * @see #getCommandParameter()
   * @generated
   */
  EAttribute getCommandParameter_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask <em>Mylyn Query Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Mylyn Query Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueryTask
   * @generated
   */
  EClass getMylynQueryTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getConnectorKind <em>Connector Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Connector Kind</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getConnectorKind()
   * @see #getMylynQueryTask()
   * @generated
   */
  EAttribute getMylynQueryTask_ConnectorKind();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getSummary <em>Summary</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Summary</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getSummary()
   * @see #getMylynQueryTask()
   * @generated
   */
  EAttribute getMylynQueryTask_Summary();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRepositoryURL <em>Repository URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Repository URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRepositoryURL()
   * @see #getMylynQueryTask()
   * @generated
   */
  EAttribute getMylynQueryTask_RepositoryURL();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRelativeURL <em>Relative URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Relative URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueryTask#getRelativeURL()
   * @see #getMylynQueryTask()
   * @generated
   */
  EAttribute getMylynQueryTask_RelativeURL();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.MylynQueriesTask <em>Mylyn Queries Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Mylyn Queries Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask
   * @generated
   */
  EClass getMylynQueriesTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getConnectorKind <em>Connector Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Connector Kind</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getConnectorKind()
   * @see #getMylynQueriesTask()
   * @generated
   */
  EAttribute getMylynQueriesTask_ConnectorKind();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getRepositoryURL <em>Repository URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Repository URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getRepositoryURL()
   * @see #getMylynQueriesTask()
   * @generated
   */
  EAttribute getMylynQueriesTask_RepositoryURL();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getQueries <em>Queries</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Queries</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getQueries()
   * @see #getMylynQueriesTask()
   * @generated
   */
  EReference getMylynQueriesTask_Queries();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Query <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Query</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Query
   * @generated
   */
  EClass getQuery();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.setup.Query#getTask <em>Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Query#getTask()
   * @see #getQuery()
   * @generated
   */
  EReference getQuery_Task();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Query#getSummary <em>Summary</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Summary</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Query#getSummary()
   * @see #getQuery()
   * @generated
   */
  EAttribute getQuery_Summary();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Query#getURL <em>URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Query#getURL()
   * @see #getQuery()
   * @generated
   */
  EAttribute getQuery_URL();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.releng.setup.Query#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>Attributes</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Query#getAttributes()
   * @see #getQuery()
   * @generated
   */
  EReference getQuery_Attributes();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>Query Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Query Attribute</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EString" keyRequired="true"
   *        valueDataType="org.eclipse.emf.ecore.EString"
   * @generated
   */
  EClass getQueryAttribute();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getQueryAttribute()
   * @generated
   */
  EAttribute getQueryAttribute_Key();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getQueryAttribute()
   * @generated
   */
  EAttribute getQueryAttribute_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.MylynBuildsTask <em>Mylyn Builds Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Mylyn Builds Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynBuildsTask
   * @generated
   */
  EClass getMylynBuildsTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynBuildsTask#getConnectorKind <em>Connector Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Connector Kind</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynBuildsTask#getConnectorKind()
   * @see #getMylynBuildsTask()
   * @generated
   */
  EAttribute getMylynBuildsTask_ConnectorKind();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.MylynBuildsTask#getServerURL <em>Server URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Server URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynBuildsTask#getServerURL()
   * @see #getMylynBuildsTask()
   * @generated
   */
  EAttribute getMylynBuildsTask_ServerURL();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.MylynBuildsTask#getBuildPlans <em>Build Plans</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Build Plans</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MylynBuildsTask#getBuildPlans()
   * @see #getMylynBuildsTask()
   * @generated
   */
  EReference getMylynBuildsTask_BuildPlans();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.BuildPlan <em>Build Plan</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Build Plan</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuildPlan
   * @generated
   */
  EClass getBuildPlan();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.BuildPlan#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuildPlan#getName()
   * @see #getBuildPlan()
   * @generated
   */
  EAttribute getBuildPlan_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.JRETask <em>JRE Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>JRE Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.JRETask
   * @generated
   */
  EClass getJRETask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.JRETask#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.JRETask#getVersion()
   * @see #getJRETask()
   * @generated
   */
  EAttribute getJRETask_Version();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.JRETask#getLocation <em>Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Location</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.JRETask#getLocation()
   * @see #getJRETask()
   * @generated
   */
  EAttribute getJRETask_Location();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ComponentExtension <em>Component Extension</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component Extension</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentExtension
   * @generated
   */
  EClass getComponentExtension();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.ComponentExtension#getDependencies <em>Dependencies</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Dependencies</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentExtension#getDependencies()
   * @see #getComponentExtension()
   * @generated
   */
  EReference getComponentExtension_Dependencies();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ComponentDefinition <em>Component Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component Definition</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentDefinition
   * @generated
   */
  EClass getComponentDefinition();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ComponentDefinition#getID <em>ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentDefinition#getID()
   * @see #getComponentDefinition()
   * @generated
   */
  EAttribute getComponentDefinition_ID();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ComponentDefinition#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentDefinition#getVersion()
   * @see #getComponentDefinition()
   * @generated
   */
  EAttribute getComponentDefinition_Version();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TargletImportTask <em>Targlet Import Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Targlet Import Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletImportTask
   * @generated
   */
  EClass getTargletImportTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TargletImportTask#getTargletURI <em>Targlet URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Targlet URI</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletImportTask#getTargletURI()
   * @see #getTargletImportTask()
   * @generated
   */
  EAttribute getTargletImportTask_TargletURI();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.FileAssociationTask <em>File Association Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>File Association Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.FileAssociationTask
   * @generated
   */
  EClass getFileAssociationTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.FileAssociationTask#getFilePattern <em>File Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>File Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.FileAssociationTask#getFilePattern()
   * @see #getFileAssociationTask()
   * @generated
   */
  EAttribute getFileAssociationTask_FilePattern();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.FileAssociationTask#getDefaultEditorID <em>Default Editor ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Default Editor ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.FileAssociationTask#getDefaultEditorID()
   * @see #getFileAssociationTask()
   * @generated
   */
  EAttribute getFileAssociationTask_DefaultEditorID();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.FileAssociationTask#getEditors <em>Editors</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Editors</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.FileAssociationTask#getEditors()
   * @see #getFileAssociationTask()
   * @generated
   */
  EReference getFileAssociationTask_Editors();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.FileEditor <em>File Editor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>File Editor</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.FileEditor
   * @generated
   */
  EClass getFileEditor();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.FileEditor#getID <em>ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>ID</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.FileEditor#getID()
   * @see #getFileEditor()
   * @generated
   */
  EAttribute getFileEditor_ID();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TargetPlatformTask <em>Target Platform Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Target Platform Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargetPlatformTask
   * @generated
   */
  EClass getTargetPlatformTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TargetPlatformTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargetPlatformTask#getName()
   * @see #getTargetPlatformTask()
   * @generated
   */
  EAttribute getTargetPlatformTask_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator <em>Automatic Source Locator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Automatic Source Locator</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator
   * @generated
   */
  EClass getAutomaticSourceLocator();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#getRootFolder <em>Root Folder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Root Folder</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#getRootFolder()
   * @see #getAutomaticSourceLocator()
   * @generated
   */
  EAttribute getAutomaticSourceLocator_RootFolder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#isLocateNestedProjects <em>Locate Nested Projects</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Locate Nested Projects</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator#isLocateNestedProjects()
   * @see #getAutomaticSourceLocator()
   * @generated
   */
  EAttribute getAutomaticSourceLocator_LocateNestedProjects();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TargletTask <em>Targlet Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Targlet Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletTask
   * @generated
   */
  EClass getTargletTask();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TargletContainer <em>Targlet Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Targlet Container</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletContainer
   * @generated
   */
  EClass getTargletContainer();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.TargletContainer#getTarglets <em>Targlets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Targlets</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletContainer#getTarglets()
   * @see #getTargletContainer()
   * @generated
   */
  EReference getTargletContainer_Targlets();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Targlet <em>Targlet</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Targlet</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Targlet
   * @generated
   */
  EClass getTarglet();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.TargletData <em>Targlet Data</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Targlet Data</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData
   * @generated
   */
  EClass getTargletData();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData#getName()
   * @see #getTargletData()
   * @generated
   */
  EAttribute getTargletData_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getRoots <em>Roots</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Roots</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData#getRoots()
   * @see #getTargletData()
   * @generated
   */
  EReference getTargletData_Roots();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getSourceLocators <em>Source Locators</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Source Locators</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData#getSourceLocators()
   * @see #getTargletData()
   * @generated
   */
  EReference getTargletData_SourceLocators();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getRepositoryLists <em>Repository Lists</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Repository Lists</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData#getRepositoryLists()
   * @see #getTargletData()
   * @generated
   */
  EReference getTargletData_RepositoryLists();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getActiveRepositoryList <em>Active Repository List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active Repository List</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData#getActiveRepositoryList()
   * @see #getTargletData()
   * @generated
   */
  EAttribute getTargletData_ActiveRepositoryList();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getActiveP2Repositories <em>Active P2 Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Active P2 Repositories</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.TargletData#getActiveP2Repositories()
   * @see #getTargletData()
   * @generated
   */
  EReference getTargletData_ActiveP2Repositories();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.RepositoryList <em>Repository List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Repository List</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.RepositoryList
   * @generated
   */
  EClass getRepositoryList();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.RepositoryList#getP2Repositories <em>P2 Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>P2 Repositories</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.RepositoryList#getP2Repositories()
   * @see #getRepositoryList()
   * @generated
   */
  EReference getRepositoryList_P2Repositories();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.RepositoryList#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.RepositoryList#getName()
   * @see #getRepositoryList()
   * @generated
   */
  EAttribute getRepositoryList_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.RedirectionTask <em>Redirection Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Redirection Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.RedirectionTask
   * @generated
   */
  EClass getRedirectionTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.RedirectionTask#getSourceURL <em>Source URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Source URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.RedirectionTask#getSourceURL()
   * @see #getRedirectionTask()
   * @generated
   */
  EAttribute getRedirectionTask_SourceURL();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.RedirectionTask#getTargetURL <em>Target URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.RedirectionTask#getTargetURL()
   * @see #getRedirectionTask()
   * @generated
   */
  EAttribute getRedirectionTask_TargetURL();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator <em>Manual Source Locator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Manual Source Locator</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ManualSourceLocator
   * @generated
   */
  EClass getManualSourceLocator();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getLocation <em>Location</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Location</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getLocation()
   * @see #getManualSourceLocator()
   * @generated
   */
  EAttribute getManualSourceLocator_Location();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentNamePattern <em>Component Name Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Component Name Pattern</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentNamePattern()
   * @see #getManualSourceLocator()
   * @generated
   */
  EAttribute getManualSourceLocator_ComponentNamePattern();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentTypes <em>Component Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Component Types</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentTypes()
   * @see #getManualSourceLocator()
   * @generated
   */
  EAttribute getManualSourceLocator_ComponentTypes();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask <em>Context Variable Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Context Variable Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask
   * @generated
   */
  EClass getContextVariableTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getType()
   * @see #getContextVariableTask()
   * @generated
   */
  EAttribute getContextVariableTask_Type();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getName()
   * @see #getContextVariableTask()
   * @generated
   */
  EAttribute getContextVariableTask_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getValue()
   * @see #getContextVariableTask()
   * @generated
   */
  EAttribute getContextVariableTask_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#isStringSubstitution <em>String Substitution</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>String Substitution</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask#isStringSubstitution()
   * @see #getContextVariableTask()
   * @generated
   */
  EAttribute getContextVariableTask_StringSubstitution();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getLabel()
   * @see #getContextVariableTask()
   * @generated
   */
  EAttribute getContextVariableTask_Label();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getChoices <em>Choices</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Choices</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getChoices()
   * @see #getContextVariableTask()
   * @generated
   */
  EReference getContextVariableTask_Choices();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.VariableChoice <em>Variable Choice</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Variable Choice</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.VariableChoice
   * @generated
   */
  EClass getVariableChoice();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.VariableChoice#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.VariableChoice#getValue()
   * @see #getVariableChoice()
   * @generated
   */
  EAttribute getVariableChoice_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.VariableChoice#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.VariableChoice#getLabel()
   * @see #getVariableChoice()
   * @generated
   */
  EAttribute getVariableChoice_Label();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask <em>Resource Creation Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Resource Creation Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCreationTask
   * @generated
   */
  EClass getResourceCreationTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask#getContent <em>Content</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Content</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCreationTask#getContent()
   * @see #getResourceCreationTask()
   * @generated
   */
  EAttribute getResourceCreationTask_Content();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask#getTargetURL <em>Target URL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target URL</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCreationTask#getTargetURL()
   * @see #getResourceCreationTask()
   * @generated
   */
  EAttribute getResourceCreationTask_TargetURL();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask#getEncoding <em>Encoding</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Encoding</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCreationTask#getEncoding()
   * @see #getResourceCreationTask()
   * @generated
   */
  EAttribute getResourceCreationTask_Encoding();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask <em>Materialization Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Materialization Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MaterializationTask
   * @generated
   */
  EClass getMaterializationTask();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask#getSourceLocators <em>Source Locators</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Source Locators</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MaterializationTask#getSourceLocators()
   * @see #getMaterializationTask()
   * @generated
   */
  EReference getMaterializationTask_SourceLocators();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask#getRootComponents <em>Root Components</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Root Components</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MaterializationTask#getRootComponents()
   * @see #getMaterializationTask()
   * @generated
   */
  EReference getMaterializationTask_RootComponents();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.SourceLocator <em>Source Locator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Source Locator</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SourceLocator
   * @generated
   */
  EClass getSourceLocator();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask#getP2Repositories <em>P2 Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>P2 Repositories</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.MaterializationTask#getP2Repositories()
   * @see #getMaterializationTask()
   * @generated
   */
  EReference getMaterializationTask_P2Repositories();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask <em>Basic Materialization Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Basic Materialization Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask
   * @generated
   */
  EClass getBasicMaterializationTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask#getTargetPlatform <em>Target Platform</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Platform</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask#getTargetPlatform()
   * @see #getBasicMaterializationTask()
   * @generated
   */
  EAttribute getBasicMaterializationTask_TargetPlatform();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.Component <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Component
   * @generated
   */
  EClass getComponent();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Component#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Component#getName()
   * @see #getComponent()
   * @generated
   */
  EAttribute getComponent_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Component#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Component#getType()
   * @see #getComponent()
   * @generated
   */
  EAttribute getComponent_Type();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Component#getVersionRange <em>Version Range</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version Range</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Component#getVersionRange()
   * @see #getComponent()
   * @generated
   */
  EAttribute getComponent_VersionRange();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask <em>Eclipse Ini Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse Ini Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask
   * @generated
   */
  EClass getEclipseIniTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getOption <em>Option</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Option</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getOption()
   * @see #getEclipseIniTask()
   * @generated
   */
  EAttribute getEclipseIniTask_Option();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask#getValue()
   * @see #getEclipseIniTask()
   * @generated
   */
  EAttribute getEclipseIniTask_Value();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask#isVm <em>Vm</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Vm</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask#isVm()
   * @see #getEclipseIniTask()
   * @generated
   */
  EAttribute getEclipseIniTask_Vm();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope <em>Task Scope</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Task Scope</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
   * @generated
   */
  EEnum getSetupTaskScope();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.releng.setup.Trigger <em>Trigger</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Trigger</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Trigger
   * @generated
   */
  EEnum getTrigger();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.releng.setup.ComponentType <em>Component Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Component Type</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentType
   * @generated
   */
  EEnum getComponentType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.releng.setup.VariableType <em>Variable Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Variable Type</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.VariableType
   * @generated
   */
  EEnum getVariableType();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask <em>Compound Setup Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Compound Setup Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CompoundSetupTask
   * @generated
   */
  EClass getCompoundSetupTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.CompoundSetupTask#getName()
   * @see #getCompoundSetupTask()
   * @generated
   */
  EAttribute getCompoundSetupTask_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ConfigurableItem <em>Configurable Item</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Configurable Item</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ConfigurableItem
   * @generated
   */
  EClass getConfigurableItem();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask <em>Buckminster Import Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Buckminster Import Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask
   * @generated
   */
  EClass getBuckminsterImportTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getMspec <em>Mspec</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Mspec</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask#getMspec()
   * @see #getBuckminsterImportTask()
   * @generated
   */
  EAttribute getBuckminsterImportTask_Mspec();

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
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolder <em>Bundle Pool Folder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Bundle Pool Folder</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolder()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_BundlePoolFolder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolderTP <em>Bundle Pool Folder TP</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Bundle Pool Folder TP</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolderTP()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_BundlePoolFolderTP();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getAcceptedLicenses <em>Accepted Licenses</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Accepted Licenses</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences#getAcceptedLicenses()
   * @see #getPreferences()
   * @generated
   */
  EAttribute getPreferences_AcceptedLicenses();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask <em>Link Location Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Link Location Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask
   * @generated
   */
  EClass getLinkLocationTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getPath <em>Path</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Path</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getPath()
   * @see #getLinkLocationTask()
   * @generated
   */
  EAttribute getLinkLocationTask_Path();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask#getName()
   * @see #getLinkLocationTask()
   * @generated
   */
  EAttribute getLinkLocationTask_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Task Container</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskContainer
   * @generated
   */
  EClass getSetupTaskContainer();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskContainer#getSetupTasks <em>Setup Tasks</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Setup Tasks</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskContainer#getSetupTasks()
   * @see #getSetupTaskContainer()
   * @generated
   */
  EReference getSetupTaskContainer_SetupTasks();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.ScopeRoot <em>Scope Root</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Scope Root</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.ScopeRoot
   * @generated
   */
  EClass getScopeRoot();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask <em>Eclipse Preference Task</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Eclipse Preference Task</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask
   * @generated
   */
  EClass getEclipsePreferenceTask();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getKey()
   * @see #getEclipsePreferenceTask()
   * @generated
   */
  EAttribute getEclipsePreferenceTask_Key();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask#getValue()
   * @see #getEclipsePreferenceTask()
   * @generated
   */
  EAttribute getEclipsePreferenceTask_Value();

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
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.releng.setup.LicenseInfo <em>License Info</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>License Info</em>'.
   * @see org.eclipse.emf.cdo.releng.setup.LicenseInfo
   * @model instanceClass="org.eclipse.emf.cdo.releng.setup.LicenseInfo"
   * @generated
   */
  EDataType getLicenseInfo();

  /**
   * Returns the meta object for data type '{@link org.eclipse.equinox.p2.metadata.Version <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Version</em>'.
   * @see org.eclipse.equinox.p2.metadata.Version
   * @model instanceClass="org.eclipse.equinox.p2.metadata.Version"
   * @generated
   */
  EDataType getVersion();

  /**
   * Returns the meta object for data type '{@link org.eclipse.equinox.p2.metadata.VersionRange <em>Version Range</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Version Range</em>'.
   * @see org.eclipse.equinox.p2.metadata.VersionRange
   * @model instanceClass="org.eclipse.equinox.p2.metadata.VersionRange"
   * @generated
   */
  EDataType getVersionRange();

  /**
   * Returns the meta object for data type '{@link java.lang.Exception <em>Exception</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Exception</em>'.
   * @see java.lang.Exception
   * @model instanceClass="java.lang.Exception"
   * @generated
   */
  EDataType getException();

  /**
   * Returns the meta object for data type '{@link java.util.Set <em>Trigger Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Trigger Set</em>'.
   * @see java.util.Set
   * @model instanceClass="java.util.Set<org.eclipse.emf.cdo.releng.setup.Trigger>"
   * @generated
   */
  EDataType getTriggerSet();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MetaIndexImpl <em>Meta Index</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.MetaIndexImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMetaIndex()
     * @generated
     */
    EClass META_INDEX = eINSTANCE.getMetaIndex();

    /**
     * The meta object literal for the '<em><b>Indexes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference META_INDEX__INDEXES = eINSTANCE.getMetaIndex_Indexes();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.IndexImpl <em>Index</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.IndexImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getIndex()
     * @generated
     */
    EClass INDEX = eINSTANCE.getIndex();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INDEX__NAME = eINSTANCE.getIndex_Name();

    /**
     * The meta object literal for the '<em><b>URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INDEX__URI = eINSTANCE.getIndex_URI();

    /**
     * The meta object literal for the '<em><b>Old UR Is</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INDEX__OLD_UR_IS = eINSTANCE.getIndex_OldURIs();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseImpl <em>Eclipse</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipse()
     * @generated
     */
    EClass ECLIPSE = eINSTANCE.getEclipse();

    /**
     * The meta object literal for the '<em><b>Configuration</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ECLIPSE__CONFIGURATION = eINSTANCE.getEclipse_Configuration();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE__VERSION = eINSTANCE.getEclipse_Version();

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
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROJECT__LABEL = eINSTANCE.getProject_Label();

    /**
     * The meta object literal for the '<em><b>Restrictions</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__RESTRICTIONS = eINSTANCE.getProject_Restrictions();

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
     * The meta object literal for the '<em><b>Restrictions</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BRANCH__RESTRICTIONS = eINSTANCE.getBranch_Restrictions();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl <em>Api Baseline Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ApiBaselineTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getApiBaselineTask()
     * @generated
     */
    EClass API_BASELINE_TASK = eINSTANCE.getApiBaselineTask();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE_TASK__VERSION = eINSTANCE.getApiBaselineTask_Version();

    /**
     * The meta object literal for the '<em><b>Container Folder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE_TASK__CONTAINER_FOLDER = eINSTANCE.getApiBaselineTask_ContainerFolder();

    /**
     * The meta object literal for the '<em><b>Zip Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute API_BASELINE_TASK__ZIP_LOCATION = eINSTANCE.getApiBaselineTask_ZipLocation();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl <em>Git Clone Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.GitCloneTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getGitCloneTask()
     * @generated
     */
    EClass GIT_CLONE_TASK = eINSTANCE.getGitCloneTask();

    /**
     * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__LOCATION = eINSTANCE.getGitCloneTask_Location();

    /**
     * The meta object literal for the '<em><b>Remote Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__REMOTE_NAME = eINSTANCE.getGitCloneTask_RemoteName();

    /**
     * The meta object literal for the '<em><b>Remote URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__REMOTE_URI = eINSTANCE.getGitCloneTask_RemoteURI();

    /**
     * The meta object literal for the '<em><b>Checkout Branch</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__CHECKOUT_BRANCH = eINSTANCE.getGitCloneTask_CheckoutBranch();

    /**
     * The meta object literal for the '<em><b>User ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GIT_CLONE_TASK__USER_ID = eINSTANCE.getGitCloneTask_UserID();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectSetImportTaskImpl <em>Project Set Import Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ProjectSetImportTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getProjectSetImportTask()
     * @generated
     */
    EClass PROJECT_SET_IMPORT_TASK = eINSTANCE.getProjectSetImportTask();

    /**
     * The meta object literal for the '<em><b>URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROJECT_SET_IMPORT_TASK__URL = eINSTANCE.getProjectSetImportTask_URL();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl <em>P2 Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.P2TaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getP2Task()
     * @generated
     */
    EClass P2_TASK = eINSTANCE.getP2Task();

    /**
     * The meta object literal for the '<em><b>Installable Units</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference P2_TASK__INSTALLABLE_UNITS = eINSTANCE.getP2Task_InstallableUnits();

    /**
     * The meta object literal for the '<em><b>License Confirmation Disabled</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute P2_TASK__LICENSE_CONFIRMATION_DISABLED = eINSTANCE.getP2Task_LicenseConfirmationDisabled();

    /**
     * The meta object literal for the '<em><b>Merge Disabled</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute P2_TASK__MERGE_DISABLED = eINSTANCE.getP2Task_MergeDisabled();

    /**
     * The meta object literal for the '<em><b>P2 Repositories</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference P2_TASK__P2_REPOSITORIES = eINSTANCE.getP2Task_P2Repositories();

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
     * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INSTALLABLE_UNIT__ID = eINSTANCE.getInstallableUnit_ID();

    /**
     * The meta object literal for the '<em><b>Version Range</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INSTALLABLE_UNIT__VERSION_RANGE = eINSTANCE.getInstallableUnit_VersionRange();

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
     * The meta object literal for the '<em><b>URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute P2_REPOSITORY__URL = eINSTANCE.getP2Repository_URL();

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
     * The meta object literal for the '<em><b>Preferences</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP__PREFERENCES = eINSTANCE.getSetup_Preferences();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl <em>Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTask()
     * @generated
     */
    EClass SETUP_TASK = eINSTANCE.getSetupTask();

    /**
     * The meta object literal for the '<em><b>Requirements</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP_TASK__REQUIREMENTS = eINSTANCE.getSetupTask_Requirements();

    /**
     * The meta object literal for the '<em><b>Restrictions</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP_TASK__RESTRICTIONS = eINSTANCE.getSetupTask_Restrictions();

    /**
     * The meta object literal for the '<em><b>Scope</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SETUP_TASK__SCOPE = eINSTANCE.getSetupTask_Scope();

    /**
     * The meta object literal for the '<em><b>Excluded Triggers</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SETUP_TASK__EXCLUDED_TRIGGERS = eINSTANCE.getSetupTask_ExcludedTriggers();

    /**
     * The meta object literal for the '<em><b>Documentation</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SETUP_TASK__DOCUMENTATION = eINSTANCE.getSetupTask_Documentation();

    /**
     * The meta object literal for the '<em><b>Disabled</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SETUP_TASK__DISABLED = eINSTANCE.getSetupTask_Disabled();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl <em>Working Set Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getWorkingSetTask()
     * @generated
     */
    EClass WORKING_SET_TASK = eINSTANCE.getWorkingSetTask();

    /**
     * The meta object literal for the '<em><b>Working Sets</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKING_SET_TASK__WORKING_SETS = eINSTANCE.getWorkingSetTask_WorkingSets();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ResourceCopyTaskImpl <em>Resource Copy Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ResourceCopyTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getResourceCopyTask()
     * @generated
     */
    EClass RESOURCE_COPY_TASK = eINSTANCE.getResourceCopyTask();

    /**
     * The meta object literal for the '<em><b>Source URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RESOURCE_COPY_TASK__SOURCE_URL = eINSTANCE.getResourceCopyTask_SourceURL();

    /**
     * The meta object literal for the '<em><b>Target URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RESOURCE_COPY_TASK__TARGET_URL = eINSTANCE.getResourceCopyTask_TargetURL();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TextModifyTaskImpl <em>Text Modify Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TextModifyTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTextModifyTask()
     * @generated
     */
    EClass TEXT_MODIFY_TASK = eINSTANCE.getTextModifyTask();

    /**
     * The meta object literal for the '<em><b>URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_MODIFY_TASK__URL = eINSTANCE.getTextModifyTask_URL();

    /**
     * The meta object literal for the '<em><b>Modifications</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEXT_MODIFY_TASK__MODIFICATIONS = eINSTANCE.getTextModifyTask_Modifications();

    /**
     * The meta object literal for the '<em><b>Encoding</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_MODIFY_TASK__ENCODING = eINSTANCE.getTextModifyTask_Encoding();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TextModificationImpl <em>Text Modification</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TextModificationImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTextModification()
     * @generated
     */
    EClass TEXT_MODIFICATION = eINSTANCE.getTextModification();

    /**
     * The meta object literal for the '<em><b>Pattern</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_MODIFICATION__PATTERN = eINSTANCE.getTextModification_Pattern();

    /**
     * The meta object literal for the '<em><b>Substitutions</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEXT_MODIFICATION__SUBSTITUTIONS = eINSTANCE.getTextModification_Substitutions();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl <em>Key Binding Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getKeyBindingTask()
     * @generated
     */
    EClass KEY_BINDING_TASK = eINSTANCE.getKeyBindingTask();

    /**
     * The meta object literal for the '<em><b>Scheme</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute KEY_BINDING_TASK__SCHEME = eINSTANCE.getKeyBindingTask_Scheme();

    /**
     * The meta object literal for the '<em><b>Contexts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference KEY_BINDING_TASK__CONTEXTS = eINSTANCE.getKeyBindingTask_Contexts();

    /**
     * The meta object literal for the '<em><b>Platform</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute KEY_BINDING_TASK__PLATFORM = eINSTANCE.getKeyBindingTask_Platform();

    /**
     * The meta object literal for the '<em><b>Locale</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute KEY_BINDING_TASK__LOCALE = eINSTANCE.getKeyBindingTask_Locale();

    /**
     * The meta object literal for the '<em><b>Keys</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute KEY_BINDING_TASK__KEYS = eINSTANCE.getKeyBindingTask_Keys();

    /**
     * The meta object literal for the '<em><b>Command</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute KEY_BINDING_TASK__COMMAND = eINSTANCE.getKeyBindingTask_Command();

    /**
     * The meta object literal for the '<em><b>Command Parameters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference KEY_BINDING_TASK__COMMAND_PARAMETERS = eINSTANCE.getKeyBindingTask_CommandParameters();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingContextImpl <em>Key Binding Context</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.KeyBindingContextImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getKeyBindingContext()
     * @generated
     */
    EClass KEY_BINDING_CONTEXT = eINSTANCE.getKeyBindingContext();

    /**
     * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute KEY_BINDING_CONTEXT__ID = eINSTANCE.getKeyBindingContext_ID();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.CommandParameterImpl <em>Command Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.CommandParameterImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getCommandParameter()
     * @generated
     */
    EClass COMMAND_PARAMETER = eINSTANCE.getCommandParameter();

    /**
     * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMAND_PARAMETER__ID = eINSTANCE.getCommandParameter_ID();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMAND_PARAMETER__VALUE = eINSTANCE.getCommandParameter_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl <em>Mylyn Query Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMylynQueryTask()
     * @generated
     */
    EClass MYLYN_QUERY_TASK = eINSTANCE.getMylynQueryTask();

    /**
     * The meta object literal for the '<em><b>Connector Kind</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_QUERY_TASK__CONNECTOR_KIND = eINSTANCE.getMylynQueryTask_ConnectorKind();

    /**
     * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_QUERY_TASK__SUMMARY = eINSTANCE.getMylynQueryTask_Summary();

    /**
     * The meta object literal for the '<em><b>Repository URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_QUERY_TASK__REPOSITORY_URL = eINSTANCE.getMylynQueryTask_RepositoryURL();

    /**
     * The meta object literal for the '<em><b>Relative URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_QUERY_TASK__RELATIVE_URL = eINSTANCE.getMylynQueryTask_RelativeURL();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl <em>Mylyn Queries Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMylynQueriesTask()
     * @generated
     */
    EClass MYLYN_QUERIES_TASK = eINSTANCE.getMylynQueriesTask();

    /**
     * The meta object literal for the '<em><b>Connector Kind</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_QUERIES_TASK__CONNECTOR_KIND = eINSTANCE.getMylynQueriesTask_ConnectorKind();

    /**
     * The meta object literal for the '<em><b>Repository URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_QUERIES_TASK__REPOSITORY_URL = eINSTANCE.getMylynQueriesTask_RepositoryURL();

    /**
     * The meta object literal for the '<em><b>Queries</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MYLYN_QUERIES_TASK__QUERIES = eINSTANCE.getMylynQueriesTask_Queries();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.QueryImpl <em>Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.QueryImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getQuery()
     * @generated
     */
    EClass QUERY = eINSTANCE.getQuery();

    /**
     * The meta object literal for the '<em><b>Task</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference QUERY__TASK = eINSTANCE.getQuery_Task();

    /**
     * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute QUERY__SUMMARY = eINSTANCE.getQuery_Summary();

    /**
     * The meta object literal for the '<em><b>URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute QUERY__URL = eINSTANCE.getQuery_URL();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference QUERY__ATTRIBUTES = eINSTANCE.getQuery_Attributes();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.QueryAttributeImpl <em>Query Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.QueryAttributeImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getQueryAttribute()
     * @generated
     */
    EClass QUERY_ATTRIBUTE = eINSTANCE.getQueryAttribute();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute QUERY_ATTRIBUTE__KEY = eINSTANCE.getQueryAttribute_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute QUERY_ATTRIBUTE__VALUE = eINSTANCE.getQueryAttribute_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl <em>Mylyn Builds Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMylynBuildsTask()
     * @generated
     */
    EClass MYLYN_BUILDS_TASK = eINSTANCE.getMylynBuildsTask();

    /**
     * The meta object literal for the '<em><b>Connector Kind</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_BUILDS_TASK__CONNECTOR_KIND = eINSTANCE.getMylynBuildsTask_ConnectorKind();

    /**
     * The meta object literal for the '<em><b>Server URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MYLYN_BUILDS_TASK__SERVER_URL = eINSTANCE.getMylynBuildsTask_ServerURL();

    /**
     * The meta object literal for the '<em><b>Build Plans</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MYLYN_BUILDS_TASK__BUILD_PLANS = eINSTANCE.getMylynBuildsTask_BuildPlans();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BuildPlanImpl <em>Build Plan</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.BuildPlanImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBuildPlan()
     * @generated
     */
    EClass BUILD_PLAN = eINSTANCE.getBuildPlan();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BUILD_PLAN__NAME = eINSTANCE.getBuildPlan_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.JRETaskImpl <em>JRE Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.JRETaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getJRETask()
     * @generated
     */
    EClass JRE_TASK = eINSTANCE.getJRETask();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute JRE_TASK__VERSION = eINSTANCE.getJRETask_Version();

    /**
     * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute JRE_TASK__LOCATION = eINSTANCE.getJRETask_Location();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ComponentExtensionImpl <em>Component Extension</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ComponentExtensionImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponentExtension()
     * @generated
     */
    EClass COMPONENT_EXTENSION = eINSTANCE.getComponentExtension();

    /**
     * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT_EXTENSION__DEPENDENCIES = eINSTANCE.getComponentExtension_Dependencies();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ComponentDefinitionImpl <em>Component Definition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ComponentDefinitionImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponentDefinition()
     * @generated
     */
    EClass COMPONENT_DEFINITION = eINSTANCE.getComponentDefinition();

    /**
     * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPONENT_DEFINITION__ID = eINSTANCE.getComponentDefinition_ID();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPONENT_DEFINITION__VERSION = eINSTANCE.getComponentDefinition_Version();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImportTaskImpl <em>Targlet Import Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TargletImportTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletImportTask()
     * @generated
     */
    EClass TARGLET_IMPORT_TASK = eINSTANCE.getTargletImportTask();

    /**
     * The meta object literal for the '<em><b>Targlet URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TARGLET_IMPORT_TASK__TARGLET_URI = eINSTANCE.getTargletImportTask_TargletURI();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl <em>File Association Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getFileAssociationTask()
     * @generated
     */
    EClass FILE_ASSOCIATION_TASK = eINSTANCE.getFileAssociationTask();

    /**
     * The meta object literal for the '<em><b>File Pattern</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FILE_ASSOCIATION_TASK__FILE_PATTERN = eINSTANCE.getFileAssociationTask_FilePattern();

    /**
     * The meta object literal for the '<em><b>Default Editor ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID = eINSTANCE.getFileAssociationTask_DefaultEditorID();

    /**
     * The meta object literal for the '<em><b>Editors</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FILE_ASSOCIATION_TASK__EDITORS = eINSTANCE.getFileAssociationTask_Editors();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.FileEditorImpl <em>File Editor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.FileEditorImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getFileEditor()
     * @generated
     */
    EClass FILE_EDITOR = eINSTANCE.getFileEditor();

    /**
     * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FILE_EDITOR__ID = eINSTANCE.getFileEditor_ID();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargetPlatformTaskImpl <em>Target Platform Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TargetPlatformTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargetPlatformTask()
     * @generated
     */
    EClass TARGET_PLATFORM_TASK = eINSTANCE.getTargetPlatformTask();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TARGET_PLATFORM_TASK__NAME = eINSTANCE.getTargetPlatformTask_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.AutomaticSourceLocatorImpl <em>Automatic Source Locator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.AutomaticSourceLocatorImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getAutomaticSourceLocator()
     * @generated
     */
    EClass AUTOMATIC_SOURCE_LOCATOR = eINSTANCE.getAutomaticSourceLocator();

    /**
     * The meta object literal for the '<em><b>Root Folder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AUTOMATIC_SOURCE_LOCATOR__ROOT_FOLDER = eINSTANCE.getAutomaticSourceLocator_RootFolder();

    /**
     * The meta object literal for the '<em><b>Locate Nested Projects</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AUTOMATIC_SOURCE_LOCATOR__LOCATE_NESTED_PROJECTS = eINSTANCE
        .getAutomaticSourceLocator_LocateNestedProjects();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletTaskImpl <em>Targlet Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TargletTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletTask()
     * @generated
     */
    EClass TARGLET_TASK = eINSTANCE.getTargletTask();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletContainerImpl <em>Targlet Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TargletContainerImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletContainer()
     * @generated
     */
    EClass TARGLET_CONTAINER = eINSTANCE.getTargletContainer();

    /**
     * The meta object literal for the '<em><b>Targlets</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TARGLET_CONTAINER__TARGLETS = eINSTANCE.getTargletContainer_Targlets();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl <em>Targlet</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TargletImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTarglet()
     * @generated
     */
    EClass TARGLET = eINSTANCE.getTarglet();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.TargletDataImpl <em>Targlet Data</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.TargletDataImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTargletData()
     * @generated
     */
    EClass TARGLET_DATA = eINSTANCE.getTargletData();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TARGLET_DATA__NAME = eINSTANCE.getTargletData_Name();

    /**
     * The meta object literal for the '<em><b>Roots</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TARGLET_DATA__ROOTS = eINSTANCE.getTargletData_Roots();

    /**
     * The meta object literal for the '<em><b>Source Locators</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TARGLET_DATA__SOURCE_LOCATORS = eINSTANCE.getTargletData_SourceLocators();

    /**
     * The meta object literal for the '<em><b>Repository Lists</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TARGLET_DATA__REPOSITORY_LISTS = eINSTANCE.getTargletData_RepositoryLists();

    /**
     * The meta object literal for the '<em><b>Active Repository List</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TARGLET_DATA__ACTIVE_REPOSITORY_LIST = eINSTANCE.getTargletData_ActiveRepositoryList();

    /**
     * The meta object literal for the '<em><b>Active P2 Repositories</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TARGLET_DATA__ACTIVE_P2_REPOSITORIES = eINSTANCE.getTargletData_ActiveP2Repositories();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.RepositoryListImpl <em>Repository List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.RepositoryListImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getRepositoryList()
     * @generated
     */
    EClass REPOSITORY_LIST = eINSTANCE.getRepositoryList();

    /**
     * The meta object literal for the '<em><b>P2 Repositories</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REPOSITORY_LIST__P2_REPOSITORIES = eINSTANCE.getRepositoryList_P2Repositories();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REPOSITORY_LIST__NAME = eINSTANCE.getRepositoryList_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.RedirectionTaskImpl <em>Redirection Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.RedirectionTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getRedirectionTask()
     * @generated
     */
    EClass REDIRECTION_TASK = eINSTANCE.getRedirectionTask();

    /**
     * The meta object literal for the '<em><b>Source URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REDIRECTION_TASK__SOURCE_URL = eINSTANCE.getRedirectionTask_SourceURL();

    /**
     * The meta object literal for the '<em><b>Target URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REDIRECTION_TASK__TARGET_URL = eINSTANCE.getRedirectionTask_TargetURL();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl <em>Manual Source Locator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getManualSourceLocator()
     * @generated
     */
    EClass MANUAL_SOURCE_LOCATOR = eINSTANCE.getManualSourceLocator();

    /**
     * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MANUAL_SOURCE_LOCATOR__LOCATION = eINSTANCE.getManualSourceLocator_Location();

    /**
     * The meta object literal for the '<em><b>Component Name Pattern</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN = eINSTANCE.getManualSourceLocator_ComponentNamePattern();

    /**
     * The meta object literal for the '<em><b>Component Types</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES = eINSTANCE.getManualSourceLocator_ComponentTypes();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl <em>Context Variable Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getContextVariableTask()
     * @generated
     */
    EClass CONTEXT_VARIABLE_TASK = eINSTANCE.getContextVariableTask();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTEXT_VARIABLE_TASK__TYPE = eINSTANCE.getContextVariableTask_Type();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTEXT_VARIABLE_TASK__NAME = eINSTANCE.getContextVariableTask_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTEXT_VARIABLE_TASK__VALUE = eINSTANCE.getContextVariableTask_Value();

    /**
     * The meta object literal for the '<em><b>String Substitution</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION = eINSTANCE.getContextVariableTask_StringSubstitution();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTEXT_VARIABLE_TASK__LABEL = eINSTANCE.getContextVariableTask_Label();

    /**
     * The meta object literal for the '<em><b>Choices</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONTEXT_VARIABLE_TASK__CHOICES = eINSTANCE.getContextVariableTask_Choices();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.VariableChoiceImpl <em>Variable Choice</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.VariableChoiceImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVariableChoice()
     * @generated
     */
    EClass VARIABLE_CHOICE = eINSTANCE.getVariableChoice();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VARIABLE_CHOICE__VALUE = eINSTANCE.getVariableChoice_Value();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute VARIABLE_CHOICE__LABEL = eINSTANCE.getVariableChoice_Label();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ResourceCreationTaskImpl <em>Resource Creation Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ResourceCreationTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getResourceCreationTask()
     * @generated
     */
    EClass RESOURCE_CREATION_TASK = eINSTANCE.getResourceCreationTask();

    /**
     * The meta object literal for the '<em><b>Content</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RESOURCE_CREATION_TASK__CONTENT = eINSTANCE.getResourceCreationTask_Content();

    /**
     * The meta object literal for the '<em><b>Target URL</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RESOURCE_CREATION_TASK__TARGET_URL = eINSTANCE.getResourceCreationTask_TargetURL();

    /**
     * The meta object literal for the '<em><b>Encoding</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RESOURCE_CREATION_TASK__ENCODING = eINSTANCE.getResourceCreationTask_Encoding();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl <em>Materialization Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getMaterializationTask()
     * @generated
     */
    EClass MATERIALIZATION_TASK = eINSTANCE.getMaterializationTask();

    /**
     * The meta object literal for the '<em><b>Source Locators</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MATERIALIZATION_TASK__SOURCE_LOCATORS = eINSTANCE.getMaterializationTask_SourceLocators();

    /**
     * The meta object literal for the '<em><b>Root Components</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MATERIALIZATION_TASK__ROOT_COMPONENTS = eINSTANCE.getMaterializationTask_RootComponents();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SourceLocatorImpl <em>Source Locator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SourceLocatorImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSourceLocator()
     * @generated
     */
    EClass SOURCE_LOCATOR = eINSTANCE.getSourceLocator();

    /**
     * The meta object literal for the '<em><b>P2 Repositories</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MATERIALIZATION_TASK__P2_REPOSITORIES = eINSTANCE.getMaterializationTask_P2Repositories();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BasicMaterializationTaskImpl <em>Basic Materialization Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.BasicMaterializationTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBasicMaterializationTask()
     * @generated
     */
    EClass BASIC_MATERIALIZATION_TASK = eINSTANCE.getBasicMaterializationTask();

    /**
     * The meta object literal for the '<em><b>Target Platform</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BASIC_MATERIALIZATION_TASK__TARGET_PLATFORM = eINSTANCE.getBasicMaterializationTask_TargetPlatform();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ComponentImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponent()
     * @generated
     */
    EClass COMPONENT = eINSTANCE.getComponent();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPONENT__NAME = eINSTANCE.getComponent_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPONENT__TYPE = eINSTANCE.getComponent_Type();

    /**
     * The meta object literal for the '<em><b>Version Range</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPONENT__VERSION_RANGE = eINSTANCE.getComponent_VersionRange();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl <em>Eclipse Ini Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipseIniTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipseIniTask()
     * @generated
     */
    EClass ECLIPSE_INI_TASK = eINSTANCE.getEclipseIniTask();

    /**
     * The meta object literal for the '<em><b>Option</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_INI_TASK__OPTION = eINSTANCE.getEclipseIniTask_Option();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_INI_TASK__VALUE = eINSTANCE.getEclipseIniTask_Value();

    /**
     * The meta object literal for the '<em><b>Vm</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_INI_TASK__VM = eINSTANCE.getEclipseIniTask_Vm();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope <em>Task Scope</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskScope()
     * @generated
     */
    EEnum SETUP_TASK_SCOPE = eINSTANCE.getSetupTaskScope();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.Trigger <em>Trigger</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.Trigger
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTrigger()
     * @generated
     */
    EEnum TRIGGER = eINSTANCE.getTrigger();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.ComponentType <em>Component Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.ComponentType
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getComponentType()
     * @generated
     */
    EEnum COMPONENT_TYPE = eINSTANCE.getComponentType();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.VariableType <em>Variable Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.VariableType
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVariableType()
     * @generated
     */
    EEnum VARIABLE_TYPE = eINSTANCE.getVariableType();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl <em>Compound Setup Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.CompoundSetupTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getCompoundSetupTask()
     * @generated
     */
    EClass COMPOUND_SETUP_TASK = eINSTANCE.getCompoundSetupTask();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPOUND_SETUP_TASK__NAME = eINSTANCE.getCompoundSetupTask_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl <em>Configurable Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.ConfigurableItemImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getConfigurableItem()
     * @generated
     */
    EClass CONFIGURABLE_ITEM = eINSTANCE.getConfigurableItem();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl <em>Buckminster Import Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getBuckminsterImportTask()
     * @generated
     */
    EClass BUCKMINSTER_IMPORT_TASK = eINSTANCE.getBuckminsterImportTask();

    /**
     * The meta object literal for the '<em><b>Mspec</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BUCKMINSTER_IMPORT_TASK__MSPEC = eINSTANCE.getBuckminsterImportTask_Mspec();

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
     * The meta object literal for the '<em><b>Install Folder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__INSTALL_FOLDER = eINSTANCE.getPreferences_InstallFolder();

    /**
     * The meta object literal for the '<em><b>Bundle Pool Folder</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__BUNDLE_POOL_FOLDER = eINSTANCE.getPreferences_BundlePoolFolder();

    /**
     * The meta object literal for the '<em><b>Bundle Pool Folder TP</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__BUNDLE_POOL_FOLDER_TP = eINSTANCE.getPreferences_BundlePoolFolderTP();

    /**
     * The meta object literal for the '<em><b>Accepted Licenses</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCES__ACCEPTED_LICENSES = eINSTANCE.getPreferences_AcceptedLicenses();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl <em>Link Location Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.LinkLocationTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getLinkLocationTask()
     * @generated
     */
    EClass LINK_LOCATION_TASK = eINSTANCE.getLinkLocationTask();

    /**
     * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINK_LOCATION_TASK__PATH = eINSTANCE.getLinkLocationTask_Path();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINK_LOCATION_TASK__NAME = eINSTANCE.getLinkLocationTask_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl <em>Task Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupTaskContainerImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getSetupTaskContainer()
     * @generated
     */
    EClass SETUP_TASK_CONTAINER = eINSTANCE.getSetupTaskContainer();

    /**
     * The meta object literal for the '<em><b>Setup Tasks</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SETUP_TASK_CONTAINER__SETUP_TASKS = eINSTANCE.getSetupTaskContainer_SetupTasks();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.ScopeRoot <em>Scope Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.ScopeRoot
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getScopeRoot()
     * @generated
     */
    EClass SCOPE_ROOT = eINSTANCE.getScopeRoot();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl <em>Eclipse Preference Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.impl.EclipsePreferenceTaskImpl
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getEclipsePreferenceTask()
     * @generated
     */
    EClass ECLIPSE_PREFERENCE_TASK = eINSTANCE.getEclipsePreferenceTask();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_PREFERENCE_TASK__KEY = eINSTANCE.getEclipsePreferenceTask_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ECLIPSE_PREFERENCE_TASK__VALUE = eINSTANCE.getEclipsePreferenceTask_Value();

    /**
     * The meta object literal for the '<em>URI</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

    /**
     * The meta object literal for the '<em>License Info</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.setup.LicenseInfo
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getLicenseInfo()
     * @generated
     */
    EDataType LICENSE_INFO = eINSTANCE.getLicenseInfo();

    /**
     * The meta object literal for the '<em>Version</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.equinox.p2.metadata.Version
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVersion()
     * @generated
     */
    EDataType VERSION = eINSTANCE.getVersion();

    /**
     * The meta object literal for the '<em>Version Range</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.equinox.p2.metadata.VersionRange
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getVersionRange()
     * @generated
     */
    EDataType VERSION_RANGE = eINSTANCE.getVersionRange();

    /**
     * The meta object literal for the '<em>Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Exception
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getException()
     * @generated
     */
    EDataType EXCEPTION = eINSTANCE.getException();

    /**
     * The meta object literal for the '<em>Trigger Set</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.Set
     * @see org.eclipse.emf.cdo.releng.setup.impl.SetupPackageImpl#getTriggerSet()
     * @generated
     */
    EDataType TRIGGER_SET = eINSTANCE.getTriggerSet();

  }

} // SetupPackage
