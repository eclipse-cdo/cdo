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
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.ApiBaseline;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.DirectorCall;
import org.eclipse.emf.cdo.releng.setup.EclipseVersion;
import org.eclipse.emf.cdo.releng.setup.GitClone;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.ToolInstallation;

import org.eclipse.emf.cdo.releng.setup.ToolPreference;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SetupPackageImpl extends EPackageImpl implements SetupPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configurationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass projectEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass branchEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass toolInstallationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass eclipseVersionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass directorCallEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass installableUnitEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass p2RepositoryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass apiBaselineEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass gitCloneEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass setupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass toolPreferenceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum jreEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass preferencesEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType uriEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private SetupPackageImpl()
  {
    super(eNS_URI, SetupFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link SetupPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static SetupPackage init()
  {
    if (isInited)
      return (SetupPackage)EPackage.Registry.INSTANCE.getEPackage(SetupPackage.eNS_URI);

    // Obtain or create and register package
    SetupPackageImpl theSetupPackage = (SetupPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SetupPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new SetupPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theSetupPackage.createPackageContents();

    // Initialize created meta-data
    theSetupPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theSetupPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(SetupPackage.eNS_URI, theSetupPackage);
    return theSetupPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfiguration()
  {
    return configurationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfiguration_Projects()
  {
    return (EReference)configurationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfiguration_EclipseVersions()
  {
    return (EReference)configurationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getProject()
  {
    return projectEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProject_Configuration()
  {
    return (EReference)projectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProject_Branches()
  {
    return (EReference)projectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProject_Name()
  {
    return (EAttribute)projectEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProject_ApiBaselines()
  {
    return (EReference)projectEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBranch()
  {
    return branchEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBranch_Project()
  {
    return (EReference)branchEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBranch_Name()
  {
    return (EAttribute)branchEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBranch_GitClones()
  {
    return (EReference)branchEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBranch_ApiBaseline()
  {
    return (EReference)branchEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBranch_MspecFilePath()
  {
    return (EAttribute)branchEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBranch_CloneVariableName()
  {
    return (EAttribute)branchEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBranch_JavaVersion()
  {
    return (EAttribute)branchEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getToolInstallation()
  {
    return toolInstallationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getToolInstallation_DirectorCalls()
  {
    return (EReference)toolInstallationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getToolInstallation_ToolPreferences()
  {
    return (EReference)toolInstallationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEclipseVersion()
  {
    return eclipseVersionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEclipseVersion_Configuration()
  {
    return (EReference)eclipseVersionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipseVersion_Version()
  {
    return (EAttribute)eclipseVersionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEclipseVersion_DirectorCall()
  {
    return (EReference)eclipseVersionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDirectorCall()
  {
    return directorCallEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDirectorCall_InstallableUnits()
  {
    return (EReference)directorCallEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDirectorCall_P2Repositories()
  {
    return (EReference)directorCallEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInstallableUnit()
  {
    return installableUnitEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getInstallableUnit_DirectorCall()
  {
    return (EReference)installableUnitEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getInstallableUnit_Id()
  {
    return (EAttribute)installableUnitEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getP2Repository()
  {
    return p2RepositoryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getP2Repository_DirectorCall()
  {
    return (EReference)p2RepositoryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getP2Repository_Url()
  {
    return (EAttribute)p2RepositoryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getApiBaseline()
  {
    return apiBaselineEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getApiBaseline_Project()
  {
    return (EReference)apiBaselineEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getApiBaseline_Version()
  {
    return (EAttribute)apiBaselineEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getApiBaseline_ZipLocation()
  {
    return (EAttribute)apiBaselineEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGitClone()
  {
    return gitCloneEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGitClone_Branch()
  {
    return (EReference)gitCloneEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitClone_Name()
  {
    return (EAttribute)gitCloneEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitClone_RemoteURI()
  {
    return (EAttribute)gitCloneEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitClone_CheckoutBranch()
  {
    return (EAttribute)gitCloneEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSetup()
  {
    return setupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetup_Branch()
  {
    return (EReference)setupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetup_EclipseVersion()
  {
    return (EReference)setupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetup_Preferences()
  {
    return (EReference)setupEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetup_UpdateLocations()
  {
    return (EReference)setupEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getToolPreference()
  {
    return toolPreferenceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getToolPreference_Key()
  {
    return (EAttribute)toolPreferenceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getToolPreference_Value()
  {
    return (EAttribute)toolPreferenceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getJRE()
  {
    return jreEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPreferences()
  {
    return preferencesEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_UserName()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_BundlePool()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_InstallFolder()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_GitPrefix()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getURI()
  {
    return uriEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupFactory getSetupFactory()
  {
    return (SetupFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    preferencesEClass = createEClass(PREFERENCES);
    createEAttribute(preferencesEClass, PREFERENCES__USER_NAME);
    createEAttribute(preferencesEClass, PREFERENCES__BUNDLE_POOL);
    createEAttribute(preferencesEClass, PREFERENCES__INSTALL_FOLDER);
    createEAttribute(preferencesEClass, PREFERENCES__GIT_PREFIX);

    eclipseVersionEClass = createEClass(ECLIPSE_VERSION);
    createEReference(eclipseVersionEClass, ECLIPSE_VERSION__CONFIGURATION);
    createEAttribute(eclipseVersionEClass, ECLIPSE_VERSION__VERSION);
    createEReference(eclipseVersionEClass, ECLIPSE_VERSION__DIRECTOR_CALL);

    directorCallEClass = createEClass(DIRECTOR_CALL);
    createEReference(directorCallEClass, DIRECTOR_CALL__INSTALLABLE_UNITS);
    createEReference(directorCallEClass, DIRECTOR_CALL__P2_REPOSITORIES);

    installableUnitEClass = createEClass(INSTALLABLE_UNIT);
    createEReference(installableUnitEClass, INSTALLABLE_UNIT__DIRECTOR_CALL);
    createEAttribute(installableUnitEClass, INSTALLABLE_UNIT__ID);

    p2RepositoryEClass = createEClass(P2_REPOSITORY);
    createEReference(p2RepositoryEClass, P2_REPOSITORY__DIRECTOR_CALL);
    createEAttribute(p2RepositoryEClass, P2_REPOSITORY__URL);

    configurationEClass = createEClass(CONFIGURATION);
    createEReference(configurationEClass, CONFIGURATION__PROJECTS);
    createEReference(configurationEClass, CONFIGURATION__ECLIPSE_VERSIONS);

    toolInstallationEClass = createEClass(TOOL_INSTALLATION);
    createEReference(toolInstallationEClass, TOOL_INSTALLATION__DIRECTOR_CALLS);
    createEReference(toolInstallationEClass, TOOL_INSTALLATION__TOOL_PREFERENCES);

    projectEClass = createEClass(PROJECT);
    createEReference(projectEClass, PROJECT__CONFIGURATION);
    createEReference(projectEClass, PROJECT__BRANCHES);
    createEAttribute(projectEClass, PROJECT__NAME);
    createEReference(projectEClass, PROJECT__API_BASELINES);

    branchEClass = createEClass(BRANCH);
    createEReference(branchEClass, BRANCH__PROJECT);
    createEAttribute(branchEClass, BRANCH__NAME);
    createEReference(branchEClass, BRANCH__GIT_CLONES);
    createEReference(branchEClass, BRANCH__API_BASELINE);
    createEAttribute(branchEClass, BRANCH__MSPEC_FILE_PATH);
    createEAttribute(branchEClass, BRANCH__CLONE_VARIABLE_NAME);
    createEAttribute(branchEClass, BRANCH__JAVA_VERSION);

    apiBaselineEClass = createEClass(API_BASELINE);
    createEReference(apiBaselineEClass, API_BASELINE__PROJECT);
    createEAttribute(apiBaselineEClass, API_BASELINE__VERSION);
    createEAttribute(apiBaselineEClass, API_BASELINE__ZIP_LOCATION);

    gitCloneEClass = createEClass(GIT_CLONE);
    createEReference(gitCloneEClass, GIT_CLONE__BRANCH);
    createEAttribute(gitCloneEClass, GIT_CLONE__NAME);
    createEAttribute(gitCloneEClass, GIT_CLONE__REMOTE_URI);
    createEAttribute(gitCloneEClass, GIT_CLONE__CHECKOUT_BRANCH);

    setupEClass = createEClass(SETUP);
    createEReference(setupEClass, SETUP__BRANCH);
    createEReference(setupEClass, SETUP__ECLIPSE_VERSION);
    createEReference(setupEClass, SETUP__PREFERENCES);
    createEReference(setupEClass, SETUP__UPDATE_LOCATIONS);

    toolPreferenceEClass = createEClass(TOOL_PREFERENCE);
    createEAttribute(toolPreferenceEClass, TOOL_PREFERENCE__KEY);
    createEAttribute(toolPreferenceEClass, TOOL_PREFERENCE__VALUE);

    // Create enums
    jreEEnum = createEEnum(JRE);

    // Create data types
    uriEDataType = createEDataType(URI);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    preferencesEClass.getESuperTypes().add(this.getToolInstallation());
    projectEClass.getESuperTypes().add(this.getToolInstallation());
    branchEClass.getESuperTypes().add(this.getToolInstallation());

    // Initialize classes and features; add operations and parameters
    initEClass(preferencesEClass, Preferences.class, "Preferences", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPreferences_UserName(), ecorePackage.getEString(), "userName", null, 0, 1, Preferences.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPreferences_BundlePool(), ecorePackage.getEString(), "bundlePool", null, 0, 1, Preferences.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPreferences_InstallFolder(), ecorePackage.getEString(), "installFolder", null, 0, 1,
        Preferences.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getPreferences_GitPrefix(), ecorePackage.getEString(), "gitPrefix", null, 0, 1, Preferences.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eclipseVersionEClass, EclipseVersion.class, "EclipseVersion", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEclipseVersion_Configuration(), this.getConfiguration(), this.getConfiguration_EclipseVersions(),
        "configuration", null, 0, 1, EclipseVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEclipseVersion_Version(), ecorePackage.getEString(), "version", null, 0, 1, EclipseVersion.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEclipseVersion_DirectorCall(), this.getDirectorCall(), null, "directorCall", null, 1, 1,
        EclipseVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(directorCallEClass, DirectorCall.class, "DirectorCall", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDirectorCall_InstallableUnits(), this.getInstallableUnit(),
        this.getInstallableUnit_DirectorCall(), "installableUnits", null, 1, -1, DirectorCall.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getDirectorCall_P2Repositories(), this.getP2Repository(), this.getP2Repository_DirectorCall(),
        "p2Repositories", null, 1, -1, DirectorCall.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(installableUnitEClass, InstallableUnit.class, "InstallableUnit", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getInstallableUnit_DirectorCall(), this.getDirectorCall(), this.getDirectorCall_InstallableUnits(),
        "directorCall", null, 0, 1, InstallableUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getInstallableUnit_Id(), ecorePackage.getEString(), "id", null, 0, 1, InstallableUnit.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(p2RepositoryEClass, P2Repository.class, "P2Repository", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getP2Repository_DirectorCall(), this.getDirectorCall(), this.getDirectorCall_P2Repositories(),
        "directorCall", null, 0, 1, P2Repository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getP2Repository_Url(), ecorePackage.getEString(), "url", null, 0, 1, P2Repository.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(configurationEClass, Configuration.class, "Configuration", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfiguration_Projects(), this.getProject(), this.getProject_Configuration(), "projects", null,
        1, -1, Configuration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConfiguration_EclipseVersions(), this.getEclipseVersion(),
        this.getEclipseVersion_Configuration(), "eclipseVersions", null, 1, -1, Configuration.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(toolInstallationEClass, ToolInstallation.class, "ToolInstallation", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getToolInstallation_DirectorCalls(), this.getDirectorCall(), null, "directorCalls", null, 0, -1,
        ToolInstallation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getToolInstallation_ToolPreferences(), this.getToolPreference(), null, "toolPreferences", null, 0,
        -1, ToolInstallation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(projectEClass, Project.class, "Project", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getProject_Configuration(), this.getConfiguration(), this.getConfiguration_Projects(),
        "configuration", null, 0, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProject_Branches(), this.getBranch(), this.getBranch_Project(), "branches", null, 1, -1,
        Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProject_Name(), ecorePackage.getEString(), "name", null, 0, 1, Project.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProject_ApiBaselines(), this.getApiBaseline(), this.getApiBaseline_Project(), "apiBaselines",
        null, 0, -1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(branchEClass, Branch.class, "Branch", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBranch_Project(), this.getProject(), this.getProject_Branches(), "project", null, 0, 1,
        Branch.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBranch_Name(), ecorePackage.getEString(), "name", null, 0, 1, Branch.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBranch_GitClones(), this.getGitClone(), this.getGitClone_Branch(), "gitClones", null, 1, -1,
        Branch.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBranch_ApiBaseline(), this.getApiBaseline(), null, "apiBaseline", null, 0, 1, Branch.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBranch_MspecFilePath(), ecorePackage.getEString(), "mspecFilePath", null, 0, 1, Branch.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBranch_CloneVariableName(), ecorePackage.getEString(), "cloneVariableName", null, 0, 1,
        Branch.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getBranch_JavaVersion(), this.getJRE(), "javaVersion", null, 0, 1, Branch.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    EOperation op = addEOperation(branchEClass, ecorePackage.getEBoolean(), "isInstalled", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "installFolder", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = addEOperation(branchEClass, this.getURI(), "getURI", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "installFolder", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(apiBaselineEClass, ApiBaseline.class, "ApiBaseline", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getApiBaseline_Project(), this.getProject(), this.getProject_ApiBaselines(), "project", null, 0, 1,
        ApiBaseline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getApiBaseline_Version(), ecorePackage.getEString(), "version", null, 0, 1, ApiBaseline.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getApiBaseline_ZipLocation(), ecorePackage.getEString(), "zipLocation", null, 0, 1,
        ApiBaseline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(gitCloneEClass, GitClone.class, "GitClone", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGitClone_Branch(), this.getBranch(), this.getBranch_GitClones(), "branch", null, 0, 1,
        GitClone.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getGitClone_Name(), ecorePackage.getEString(), "name", null, 0, 1, GitClone.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getGitClone_RemoteURI(), ecorePackage.getEString(), "remoteURI", null, 0, 1, GitClone.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getGitClone_CheckoutBranch(), ecorePackage.getEString(), "checkoutBranch", null, 0, 1,
        GitClone.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(setupEClass, Setup.class, "Setup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSetup_Branch(), this.getBranch(), null, "branch", null, 1, 1, Setup.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getSetup_EclipseVersion(), this.getEclipseVersion(), null, "eclipseVersion", null, 1, 1,
        Setup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSetup_Preferences(), this.getPreferences(), null, "preferences", null, 0, 1, Setup.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getSetup_UpdateLocations(), this.getP2Repository(), null, "updateLocations", null, 0, -1,
        Setup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(toolPreferenceEClass, ToolPreference.class, "ToolPreference", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getToolPreference_Key(), ecorePackage.getEString(), "key", null, 0, 1, ToolPreference.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getToolPreference_Value(), ecorePackage.getEString(), "value", null, 0, 1, ToolPreference.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.class, "JRE");
    addEEnumLiteral(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.JRE_13);
    addEEnumLiteral(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.JRE_14);
    addEEnumLiteral(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.JRE_15);
    addEEnumLiteral(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.JRE_16);
    addEEnumLiteral(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.JRE_17);
    addEEnumLiteral(jreEEnum, org.eclipse.emf.cdo.releng.setup.JRE.JRE_18);

    // Initialize data types
    initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE,
        !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // SetupPackageImpl
