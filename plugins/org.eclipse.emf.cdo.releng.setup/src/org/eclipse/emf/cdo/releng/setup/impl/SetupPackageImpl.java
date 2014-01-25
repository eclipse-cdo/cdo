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
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.ApiBaselineTask;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.BuildPlan;
import org.eclipse.emf.cdo.releng.setup.CommandParameter;
import org.eclipse.emf.cdo.releng.setup.Component;
import org.eclipse.emf.cdo.releng.setup.ComponentDefinition;
import org.eclipse.emf.cdo.releng.setup.ComponentExtension;
import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.EclipseIniTask;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.FileAssociationTask;
import org.eclipse.emf.cdo.releng.setup.FileEditor;
import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.Index;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.JRETask;
import org.eclipse.emf.cdo.releng.setup.KeyBindingContext;
import org.eclipse.emf.cdo.releng.setup.KeyBindingTask;
import org.eclipse.emf.cdo.releng.setup.LicenseInfo;
import org.eclipse.emf.cdo.releng.setup.LinkLocationTask;
import org.eclipse.emf.cdo.releng.setup.ManualSourceLocator;
import org.eclipse.emf.cdo.releng.setup.MaterializationTask;
import org.eclipse.emf.cdo.releng.setup.MetaIndex;
import org.eclipse.emf.cdo.releng.setup.MylynBuildsTask;
import org.eclipse.emf.cdo.releng.setup.MylynQueriesTask;
import org.eclipse.emf.cdo.releng.setup.MylynQueryTask;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask;
import org.eclipse.emf.cdo.releng.setup.Query;
import org.eclipse.emf.cdo.releng.setup.RedirectionTask;
import org.eclipse.emf.cdo.releng.setup.RepositoryList;
import org.eclipse.emf.cdo.releng.setup.ResourceCopyTask;
import org.eclipse.emf.cdo.releng.setup.ResourceCreationTask;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.SourceLocator;
import org.eclipse.emf.cdo.releng.setup.TargetPlatformTask;
import org.eclipse.emf.cdo.releng.setup.Targlet;
import org.eclipse.emf.cdo.releng.setup.TargletContainer;
import org.eclipse.emf.cdo.releng.setup.TargletData;
import org.eclipse.emf.cdo.releng.setup.TargletImportTask;
import org.eclipse.emf.cdo.releng.setup.TargletTask;
import org.eclipse.emf.cdo.releng.setup.TextModification;
import org.eclipse.emf.cdo.releng.setup.TextModifyTask;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.VariableChoice;
import org.eclipse.emf.cdo.releng.setup.VariableType;
import org.eclipse.emf.cdo.releng.setup.WorkingSetTask;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
@SuppressWarnings("deprecation")
public class SetupPackageImpl extends EPackageImpl implements SetupPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass metaIndexEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass indexEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass eclipseEClass = null;

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
  private EClass apiBaselineTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass gitCloneTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass projectSetImportTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass p2TaskEClass = null;

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
  private EClass setupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass setupTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass workingSetTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass resourceCopyTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass textModifyTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass textModificationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass keyBindingTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass keyBindingContextEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass commandParameterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mylynQueryTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mylynQueriesTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mylynBuildsTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass buildPlanEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jreTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass componentExtensionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass componentDefinitionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass targletImportTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass fileAssociationTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass fileEditorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass targetPlatformTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass automaticSourceLocatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass targletTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass targletContainerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass targletEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass targletDataEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass repositoryListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass redirectionTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass manualSourceLocatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass contextVariableTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass variableChoiceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass resourceCreationTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass materializationTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sourceLocatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass basicMaterializationTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass componentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass eclipseIniTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum setupTaskScopeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum triggerEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum componentTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum variableTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass compoundSetupTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configurableItemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass buckminsterImportTaskEClass = null;

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
  private EClass linkLocationTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass setupTaskContainerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass scopeRootEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass eclipsePreferenceTaskEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType uriEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType licenseInfoEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType versionEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType versionRangeEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType exceptionEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType triggerSetEDataType = null;

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
    {
      return (SetupPackage)EPackage.Registry.INSTANCE.getEPackage(SetupPackage.eNS_URI);
    }

    // Obtain or create and register package
    SetupPackageImpl theSetupPackage = (SetupPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SetupPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new SetupPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    WorkingSetsPackage.eINSTANCE.eClass();

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
  public EClass getMetaIndex()
  {
    return metaIndexEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMetaIndex_Indexes()
  {
    return (EReference)metaIndexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIndex()
  {
    return indexEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIndex_Name()
  {
    return (EAttribute)indexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIndex_URI()
  {
    return (EAttribute)indexEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIndex_OldURIs()
  {
    return (EAttribute)indexEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEclipse()
  {
    return eclipseEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEclipse_Configuration()
  {
    return (EReference)eclipseEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipse_Version()
  {
    return (EAttribute)eclipseEClass.getEStructuralFeatures().get(1);
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
    return (EReference)configurationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfiguration_EclipseVersions()
  {
    return (EReference)configurationEClass.getEStructuralFeatures().get(0);
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
  public EAttribute getProject_Label()
  {
    return (EAttribute)projectEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProject_Restrictions()
  {
    return (EReference)projectEClass.getEStructuralFeatures().get(4);
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
  public EReference getBranch_Restrictions()
  {
    return (EReference)branchEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getApiBaselineTask()
  {
    return apiBaselineTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getApiBaselineTask_Version()
  {
    return (EAttribute)apiBaselineTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getApiBaselineTask_ContainerFolder()
  {
    return (EAttribute)apiBaselineTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getApiBaselineTask_ZipLocation()
  {
    return (EAttribute)apiBaselineTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGitCloneTask()
  {
    return gitCloneTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitCloneTask_Location()
  {
    return (EAttribute)gitCloneTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitCloneTask_RemoteName()
  {
    return (EAttribute)gitCloneTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitCloneTask_RemoteURI()
  {
    return (EAttribute)gitCloneTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitCloneTask_CheckoutBranch()
  {
    return (EAttribute)gitCloneTaskEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGitCloneTask_UserID()
  {
    return (EAttribute)gitCloneTaskEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getProjectSetImportTask()
  {
    return projectSetImportTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProjectSetImportTask_URL()
  {
    return (EAttribute)projectSetImportTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getP2Task()
  {
    return p2TaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getP2Task_InstallableUnits()
  {
    return (EReference)p2TaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getP2Task_LicenseConfirmationDisabled()
  {
    return (EAttribute)p2TaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getP2Task_MergeDisabled()
  {
    return (EAttribute)p2TaskEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getP2Task_P2Repositories()
  {
    return (EReference)p2TaskEClass.getEStructuralFeatures().get(1);
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
  public EAttribute getInstallableUnit_ID()
  {
    return (EAttribute)installableUnitEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getInstallableUnit_VersionRange()
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
  public EAttribute getP2Repository_URL()
  {
    return (EAttribute)p2RepositoryEClass.getEStructuralFeatures().get(0);
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
  public EClass getSetupTask()
  {
    return setupTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetupTask_Requirements()
  {
    return (EReference)setupTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetupTask_Restrictions()
  {
    return (EReference)setupTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSetupTask_Scope()
  {
    return (EAttribute)setupTaskEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSetupTask_ExcludedTriggers()
  {
    return (EAttribute)setupTaskEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSetupTask_Documentation()
  {
    return (EAttribute)setupTaskEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSetupTask_Disabled()
  {
    return (EAttribute)setupTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWorkingSetTask()
  {
    return workingSetTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWorkingSetTask_WorkingSets()
  {
    return (EReference)workingSetTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getResourceCopyTask()
  {
    return resourceCopyTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getResourceCopyTask_SourceURL()
  {
    return (EAttribute)resourceCopyTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getResourceCopyTask_TargetURL()
  {
    return (EAttribute)resourceCopyTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTextModifyTask()
  {
    return textModifyTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextModifyTask_URL()
  {
    return (EAttribute)textModifyTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTextModifyTask_Modifications()
  {
    return (EReference)textModifyTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextModifyTask_Encoding()
  {
    return (EAttribute)textModifyTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTextModification()
  {
    return textModificationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextModification_Pattern()
  {
    return (EAttribute)textModificationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTextModification_Substitutions()
  {
    return (EAttribute)textModificationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getKeyBindingTask()
  {
    return keyBindingTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getKeyBindingTask_Scheme()
  {
    return (EAttribute)keyBindingTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getKeyBindingTask_Contexts()
  {
    return (EReference)keyBindingTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getKeyBindingTask_Platform()
  {
    return (EAttribute)keyBindingTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getKeyBindingTask_Locale()
  {
    return (EAttribute)keyBindingTaskEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getKeyBindingTask_Keys()
  {
    return (EAttribute)keyBindingTaskEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getKeyBindingTask_Command()
  {
    return (EAttribute)keyBindingTaskEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getKeyBindingTask_CommandParameters()
  {
    return (EReference)keyBindingTaskEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getKeyBindingContext()
  {
    return keyBindingContextEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getKeyBindingContext_ID()
  {
    return (EAttribute)keyBindingContextEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCommandParameter()
  {
    return commandParameterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCommandParameter_ID()
  {
    return (EAttribute)commandParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCommandParameter_Value()
  {
    return (EAttribute)commandParameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMylynQueryTask()
  {
    return mylynQueryTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynQueryTask_ConnectorKind()
  {
    return (EAttribute)mylynQueryTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynQueryTask_Summary()
  {
    return (EAttribute)mylynQueryTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynQueryTask_RepositoryURL()
  {
    return (EAttribute)mylynQueryTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynQueryTask_RelativeURL()
  {
    return (EAttribute)mylynQueryTaskEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMylynQueriesTask()
  {
    return mylynQueriesTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynQueriesTask_ConnectorKind()
  {
    return (EAttribute)mylynQueriesTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynQueriesTask_RepositoryURL()
  {
    return (EAttribute)mylynQueriesTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMylynQueriesTask_Queries()
  {
    return (EReference)mylynQueriesTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getQuery()
  {
    return queryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getQuery_Task()
  {
    return (EReference)queryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getQuery_Summary()
  {
    return (EAttribute)queryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getQuery_URL()
  {
    return (EAttribute)queryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getQuery_Attributes()
  {
    return (EReference)queryEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getQueryAttribute()
  {
    return queryAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getQueryAttribute_Key()
  {
    return (EAttribute)queryAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getQueryAttribute_Value()
  {
    return (EAttribute)queryAttributeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMylynBuildsTask()
  {
    return mylynBuildsTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynBuildsTask_ConnectorKind()
  {
    return (EAttribute)mylynBuildsTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMylynBuildsTask_ServerURL()
  {
    return (EAttribute)mylynBuildsTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMylynBuildsTask_BuildPlans()
  {
    return (EReference)mylynBuildsTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBuildPlan()
  {
    return buildPlanEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBuildPlan_Name()
  {
    return (EAttribute)buildPlanEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getJRETask()
  {
    return jreTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getJRETask_Version()
  {
    return (EAttribute)jreTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getJRETask_Location()
  {
    return (EAttribute)jreTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComponentExtension()
  {
    return componentExtensionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponentExtension_Dependencies()
  {
    return (EReference)componentExtensionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComponentDefinition()
  {
    return componentDefinitionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComponentDefinition_ID()
  {
    return (EAttribute)componentDefinitionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComponentDefinition_Version()
  {
    return (EAttribute)componentDefinitionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTargletImportTask()
  {
    return targletImportTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTargletImportTask_TargletURI()
  {
    return (EAttribute)targletImportTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFileAssociationTask()
  {
    return fileAssociationTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFileAssociationTask_FilePattern()
  {
    return (EAttribute)fileAssociationTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFileAssociationTask_DefaultEditorID()
  {
    return (EAttribute)fileAssociationTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFileAssociationTask_Editors()
  {
    return (EReference)fileAssociationTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFileEditor()
  {
    return fileEditorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFileEditor_ID()
  {
    return (EAttribute)fileEditorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTargetPlatformTask()
  {
    return targetPlatformTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTargetPlatformTask_Name()
  {
    return (EAttribute)targetPlatformTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAutomaticSourceLocator()
  {
    return automaticSourceLocatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAutomaticSourceLocator_RootFolder()
  {
    return (EAttribute)automaticSourceLocatorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAutomaticSourceLocator_LocateNestedProjects()
  {
    return (EAttribute)automaticSourceLocatorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTargletTask()
  {
    return targletTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTargletContainer()
  {
    return targletContainerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTargletContainer_Targlets()
  {
    return (EReference)targletContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTarglet()
  {
    return targletEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTargletData()
  {
    return targletDataEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTargletData_Name()
  {
    return (EAttribute)targletDataEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTargletData_Roots()
  {
    return (EReference)targletDataEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTargletData_SourceLocators()
  {
    return (EReference)targletDataEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTargletData_RepositoryLists()
  {
    return (EReference)targletDataEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTargletData_ActiveRepositoryList()
  {
    return (EAttribute)targletDataEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTargletData_ActiveP2Repositories()
  {
    return (EReference)targletDataEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRepositoryList()
  {
    return repositoryListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRepositoryList_P2Repositories()
  {
    return (EReference)repositoryListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRepositoryList_Name()
  {
    return (EAttribute)repositoryListEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRedirectionTask()
  {
    return redirectionTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRedirectionTask_SourceURL()
  {
    return (EAttribute)redirectionTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRedirectionTask_TargetURL()
  {
    return (EAttribute)redirectionTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getManualSourceLocator()
  {
    return manualSourceLocatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getManualSourceLocator_Location()
  {
    return (EAttribute)manualSourceLocatorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getManualSourceLocator_ComponentNamePattern()
  {
    return (EAttribute)manualSourceLocatorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getManualSourceLocator_ComponentTypes()
  {
    return (EAttribute)manualSourceLocatorEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getContextVariableTask()
  {
    return contextVariableTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getContextVariableTask_Type()
  {
    return (EAttribute)contextVariableTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getContextVariableTask_Name()
  {
    return (EAttribute)contextVariableTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getContextVariableTask_Value()
  {
    return (EAttribute)contextVariableTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getContextVariableTask_StringSubstitution()
  {
    return (EAttribute)contextVariableTaskEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getContextVariableTask_Label()
  {
    return (EAttribute)contextVariableTaskEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getContextVariableTask_Choices()
  {
    return (EReference)contextVariableTaskEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVariableChoice()
  {
    return variableChoiceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVariableChoice_Value()
  {
    return (EAttribute)variableChoiceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getVariableChoice_Label()
  {
    return (EAttribute)variableChoiceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getResourceCreationTask()
  {
    return resourceCreationTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getResourceCreationTask_Content()
  {
    return (EAttribute)resourceCreationTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getResourceCreationTask_TargetURL()
  {
    return (EAttribute)resourceCreationTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getResourceCreationTask_Encoding()
  {
    return (EAttribute)resourceCreationTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMaterializationTask()
  {
    return materializationTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMaterializationTask_SourceLocators()
  {
    return (EReference)materializationTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMaterializationTask_RootComponents()
  {
    return (EReference)materializationTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSourceLocator()
  {
    return sourceLocatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMaterializationTask_P2Repositories()
  {
    return (EReference)materializationTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBasicMaterializationTask()
  {
    return basicMaterializationTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBasicMaterializationTask_TargetPlatform()
  {
    return (EAttribute)basicMaterializationTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComponent()
  {
    return componentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComponent_Name()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComponent_Type()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComponent_VersionRange()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEclipseIniTask()
  {
    return eclipseIniTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipseIniTask_Option()
  {
    return (EAttribute)eclipseIniTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipseIniTask_Value()
  {
    return (EAttribute)eclipseIniTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipseIniTask_Vm()
  {
    return (EAttribute)eclipseIniTaskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getSetupTaskScope()
  {
    return setupTaskScopeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getTrigger()
  {
    return triggerEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getComponentType()
  {
    return componentTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getVariableType()
  {
    return variableTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCompoundSetupTask()
  {
    return compoundSetupTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCompoundSetupTask_Name()
  {
    return (EAttribute)compoundSetupTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfigurableItem()
  {
    return configurableItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBuckminsterImportTask()
  {
    return buckminsterImportTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBuckminsterImportTask_Mspec()
  {
    return (EAttribute)buckminsterImportTaskEClass.getEStructuralFeatures().get(0);
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
  public EAttribute getPreferences_InstallFolder()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_BundlePoolFolder()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_BundlePoolFolderTP()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferences_AcceptedLicenses()
  {
    return (EAttribute)preferencesEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLinkLocationTask()
  {
    return linkLocationTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLinkLocationTask_Path()
  {
    return (EAttribute)linkLocationTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLinkLocationTask_Name()
  {
    return (EAttribute)linkLocationTaskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSetupTaskContainer()
  {
    return setupTaskContainerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSetupTaskContainer_SetupTasks()
  {
    return (EReference)setupTaskContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getScopeRoot()
  {
    return scopeRootEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEclipsePreferenceTask()
  {
    return eclipsePreferenceTaskEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipsePreferenceTask_Key()
  {
    return (EAttribute)eclipsePreferenceTaskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEclipsePreferenceTask_Value()
  {
    return (EAttribute)eclipsePreferenceTaskEClass.getEStructuralFeatures().get(1);
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
  public EDataType getLicenseInfo()
  {
    return licenseInfoEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getVersion()
  {
    return versionEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getVersionRange()
  {
    return versionRangeEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getException()
  {
    return exceptionEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getTriggerSet()
  {
    return triggerSetEDataType;
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
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    metaIndexEClass = createEClass(META_INDEX);
    createEReference(metaIndexEClass, META_INDEX__INDEXES);

    indexEClass = createEClass(INDEX);
    createEAttribute(indexEClass, INDEX__NAME);
    createEAttribute(indexEClass, INDEX__URI);
    createEAttribute(indexEClass, INDEX__OLD_UR_IS);

    eclipseEClass = createEClass(ECLIPSE);
    createEReference(eclipseEClass, ECLIPSE__CONFIGURATION);
    createEAttribute(eclipseEClass, ECLIPSE__VERSION);

    configurationEClass = createEClass(CONFIGURATION);
    createEReference(configurationEClass, CONFIGURATION__ECLIPSE_VERSIONS);
    createEReference(configurationEClass, CONFIGURATION__PROJECTS);

    configurableItemEClass = createEClass(CONFIGURABLE_ITEM);

    projectEClass = createEClass(PROJECT);
    createEReference(projectEClass, PROJECT__CONFIGURATION);
    createEReference(projectEClass, PROJECT__BRANCHES);
    createEAttribute(projectEClass, PROJECT__NAME);
    createEAttribute(projectEClass, PROJECT__LABEL);
    createEReference(projectEClass, PROJECT__RESTRICTIONS);

    branchEClass = createEClass(BRANCH);
    createEReference(branchEClass, BRANCH__PROJECT);
    createEAttribute(branchEClass, BRANCH__NAME);
    createEReference(branchEClass, BRANCH__RESTRICTIONS);

    preferencesEClass = createEClass(PREFERENCES);
    createEAttribute(preferencesEClass, PREFERENCES__INSTALL_FOLDER);
    createEAttribute(preferencesEClass, PREFERENCES__BUNDLE_POOL_FOLDER);
    createEAttribute(preferencesEClass, PREFERENCES__BUNDLE_POOL_FOLDER_TP);
    createEAttribute(preferencesEClass, PREFERENCES__ACCEPTED_LICENSES);

    setupEClass = createEClass(SETUP);
    createEReference(setupEClass, SETUP__BRANCH);
    createEReference(setupEClass, SETUP__ECLIPSE_VERSION);
    createEReference(setupEClass, SETUP__PREFERENCES);

    setupTaskEClass = createEClass(SETUP_TASK);
    createEReference(setupTaskEClass, SETUP_TASK__REQUIREMENTS);
    createEReference(setupTaskEClass, SETUP_TASK__RESTRICTIONS);
    createEAttribute(setupTaskEClass, SETUP_TASK__DISABLED);
    createEAttribute(setupTaskEClass, SETUP_TASK__SCOPE);
    createEAttribute(setupTaskEClass, SETUP_TASK__EXCLUDED_TRIGGERS);
    createEAttribute(setupTaskEClass, SETUP_TASK__DOCUMENTATION);

    setupTaskContainerEClass = createEClass(SETUP_TASK_CONTAINER);
    createEReference(setupTaskContainerEClass, SETUP_TASK_CONTAINER__SETUP_TASKS);

    scopeRootEClass = createEClass(SCOPE_ROOT);

    compoundSetupTaskEClass = createEClass(COMPOUND_SETUP_TASK);
    createEAttribute(compoundSetupTaskEClass, COMPOUND_SETUP_TASK__NAME);

    contextVariableTaskEClass = createEClass(CONTEXT_VARIABLE_TASK);
    createEAttribute(contextVariableTaskEClass, CONTEXT_VARIABLE_TASK__TYPE);
    createEAttribute(contextVariableTaskEClass, CONTEXT_VARIABLE_TASK__NAME);
    createEAttribute(contextVariableTaskEClass, CONTEXT_VARIABLE_TASK__VALUE);
    createEAttribute(contextVariableTaskEClass, CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION);
    createEAttribute(contextVariableTaskEClass, CONTEXT_VARIABLE_TASK__LABEL);
    createEReference(contextVariableTaskEClass, CONTEXT_VARIABLE_TASK__CHOICES);

    variableChoiceEClass = createEClass(VARIABLE_CHOICE);
    createEAttribute(variableChoiceEClass, VARIABLE_CHOICE__VALUE);
    createEAttribute(variableChoiceEClass, VARIABLE_CHOICE__LABEL);

    eclipseIniTaskEClass = createEClass(ECLIPSE_INI_TASK);
    createEAttribute(eclipseIniTaskEClass, ECLIPSE_INI_TASK__OPTION);
    createEAttribute(eclipseIniTaskEClass, ECLIPSE_INI_TASK__VALUE);
    createEAttribute(eclipseIniTaskEClass, ECLIPSE_INI_TASK__VM);

    linkLocationTaskEClass = createEClass(LINK_LOCATION_TASK);
    createEAttribute(linkLocationTaskEClass, LINK_LOCATION_TASK__PATH);
    createEAttribute(linkLocationTaskEClass, LINK_LOCATION_TASK__NAME);

    p2TaskEClass = createEClass(P2_TASK);
    createEReference(p2TaskEClass, P2_TASK__INSTALLABLE_UNITS);
    createEReference(p2TaskEClass, P2_TASK__P2_REPOSITORIES);
    createEAttribute(p2TaskEClass, P2_TASK__LICENSE_CONFIRMATION_DISABLED);
    createEAttribute(p2TaskEClass, P2_TASK__MERGE_DISABLED);

    installableUnitEClass = createEClass(INSTALLABLE_UNIT);
    createEAttribute(installableUnitEClass, INSTALLABLE_UNIT__ID);
    createEAttribute(installableUnitEClass, INSTALLABLE_UNIT__VERSION_RANGE);

    p2RepositoryEClass = createEClass(P2_REPOSITORY);
    createEAttribute(p2RepositoryEClass, P2_REPOSITORY__URL);

    basicMaterializationTaskEClass = createEClass(BASIC_MATERIALIZATION_TASK);
    createEAttribute(basicMaterializationTaskEClass, BASIC_MATERIALIZATION_TASK__TARGET_PLATFORM);

    buckminsterImportTaskEClass = createEClass(BUCKMINSTER_IMPORT_TASK);
    createEAttribute(buckminsterImportTaskEClass, BUCKMINSTER_IMPORT_TASK__MSPEC);

    materializationTaskEClass = createEClass(MATERIALIZATION_TASK);
    createEReference(materializationTaskEClass, MATERIALIZATION_TASK__ROOT_COMPONENTS);
    createEReference(materializationTaskEClass, MATERIALIZATION_TASK__SOURCE_LOCATORS);
    createEReference(materializationTaskEClass, MATERIALIZATION_TASK__P2_REPOSITORIES);

    componentEClass = createEClass(COMPONENT);
    createEAttribute(componentEClass, COMPONENT__NAME);
    createEAttribute(componentEClass, COMPONENT__TYPE);
    createEAttribute(componentEClass, COMPONENT__VERSION_RANGE);

    sourceLocatorEClass = createEClass(SOURCE_LOCATOR);

    manualSourceLocatorEClass = createEClass(MANUAL_SOURCE_LOCATOR);
    createEAttribute(manualSourceLocatorEClass, MANUAL_SOURCE_LOCATOR__LOCATION);
    createEAttribute(manualSourceLocatorEClass, MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN);
    createEAttribute(manualSourceLocatorEClass, MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES);

    automaticSourceLocatorEClass = createEClass(AUTOMATIC_SOURCE_LOCATOR);
    createEAttribute(automaticSourceLocatorEClass, AUTOMATIC_SOURCE_LOCATOR__ROOT_FOLDER);
    createEAttribute(automaticSourceLocatorEClass, AUTOMATIC_SOURCE_LOCATOR__LOCATE_NESTED_PROJECTS);

    componentExtensionEClass = createEClass(COMPONENT_EXTENSION);
    createEReference(componentExtensionEClass, COMPONENT_EXTENSION__DEPENDENCIES);

    componentDefinitionEClass = createEClass(COMPONENT_DEFINITION);
    createEAttribute(componentDefinitionEClass, COMPONENT_DEFINITION__ID);
    createEAttribute(componentDefinitionEClass, COMPONENT_DEFINITION__VERSION);

    targletImportTaskEClass = createEClass(TARGLET_IMPORT_TASK);
    createEAttribute(targletImportTaskEClass, TARGLET_IMPORT_TASK__TARGLET_URI);

    targletTaskEClass = createEClass(TARGLET_TASK);

    targletContainerEClass = createEClass(TARGLET_CONTAINER);
    createEReference(targletContainerEClass, TARGLET_CONTAINER__TARGLETS);

    targletEClass = createEClass(TARGLET);

    targletDataEClass = createEClass(TARGLET_DATA);
    createEAttribute(targletDataEClass, TARGLET_DATA__NAME);
    createEReference(targletDataEClass, TARGLET_DATA__ROOTS);
    createEReference(targletDataEClass, TARGLET_DATA__SOURCE_LOCATORS);
    createEReference(targletDataEClass, TARGLET_DATA__REPOSITORY_LISTS);
    createEAttribute(targletDataEClass, TARGLET_DATA__ACTIVE_REPOSITORY_LIST);
    createEReference(targletDataEClass, TARGLET_DATA__ACTIVE_P2_REPOSITORIES);

    repositoryListEClass = createEClass(REPOSITORY_LIST);
    createEReference(repositoryListEClass, REPOSITORY_LIST__P2_REPOSITORIES);
    createEAttribute(repositoryListEClass, REPOSITORY_LIST__NAME);

    redirectionTaskEClass = createEClass(REDIRECTION_TASK);
    createEAttribute(redirectionTaskEClass, REDIRECTION_TASK__SOURCE_URL);
    createEAttribute(redirectionTaskEClass, REDIRECTION_TASK__TARGET_URL);

    apiBaselineTaskEClass = createEClass(API_BASELINE_TASK);
    createEAttribute(apiBaselineTaskEClass, API_BASELINE_TASK__VERSION);
    createEAttribute(apiBaselineTaskEClass, API_BASELINE_TASK__CONTAINER_FOLDER);
    createEAttribute(apiBaselineTaskEClass, API_BASELINE_TASK__ZIP_LOCATION);

    gitCloneTaskEClass = createEClass(GIT_CLONE_TASK);
    createEAttribute(gitCloneTaskEClass, GIT_CLONE_TASK__LOCATION);
    createEAttribute(gitCloneTaskEClass, GIT_CLONE_TASK__REMOTE_NAME);
    createEAttribute(gitCloneTaskEClass, GIT_CLONE_TASK__REMOTE_URI);
    createEAttribute(gitCloneTaskEClass, GIT_CLONE_TASK__USER_ID);
    createEAttribute(gitCloneTaskEClass, GIT_CLONE_TASK__CHECKOUT_BRANCH);

    projectSetImportTaskEClass = createEClass(PROJECT_SET_IMPORT_TASK);
    createEAttribute(projectSetImportTaskEClass, PROJECT_SET_IMPORT_TASK__URL);

    targetPlatformTaskEClass = createEClass(TARGET_PLATFORM_TASK);
    createEAttribute(targetPlatformTaskEClass, TARGET_PLATFORM_TASK__NAME);

    eclipsePreferenceTaskEClass = createEClass(ECLIPSE_PREFERENCE_TASK);
    createEAttribute(eclipsePreferenceTaskEClass, ECLIPSE_PREFERENCE_TASK__KEY);
    createEAttribute(eclipsePreferenceTaskEClass, ECLIPSE_PREFERENCE_TASK__VALUE);

    fileAssociationTaskEClass = createEClass(FILE_ASSOCIATION_TASK);
    createEAttribute(fileAssociationTaskEClass, FILE_ASSOCIATION_TASK__FILE_PATTERN);
    createEAttribute(fileAssociationTaskEClass, FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID);
    createEReference(fileAssociationTaskEClass, FILE_ASSOCIATION_TASK__EDITORS);

    fileEditorEClass = createEClass(FILE_EDITOR);
    createEAttribute(fileEditorEClass, FILE_EDITOR__ID);

    workingSetTaskEClass = createEClass(WORKING_SET_TASK);
    createEReference(workingSetTaskEClass, WORKING_SET_TASK__WORKING_SETS);

    resourceCopyTaskEClass = createEClass(RESOURCE_COPY_TASK);
    createEAttribute(resourceCopyTaskEClass, RESOURCE_COPY_TASK__SOURCE_URL);
    createEAttribute(resourceCopyTaskEClass, RESOURCE_COPY_TASK__TARGET_URL);

    resourceCreationTaskEClass = createEClass(RESOURCE_CREATION_TASK);
    createEAttribute(resourceCreationTaskEClass, RESOURCE_CREATION_TASK__CONTENT);
    createEAttribute(resourceCreationTaskEClass, RESOURCE_CREATION_TASK__TARGET_URL);
    createEAttribute(resourceCreationTaskEClass, RESOURCE_CREATION_TASK__ENCODING);

    textModifyTaskEClass = createEClass(TEXT_MODIFY_TASK);
    createEAttribute(textModifyTaskEClass, TEXT_MODIFY_TASK__URL);
    createEReference(textModifyTaskEClass, TEXT_MODIFY_TASK__MODIFICATIONS);
    createEAttribute(textModifyTaskEClass, TEXT_MODIFY_TASK__ENCODING);

    textModificationEClass = createEClass(TEXT_MODIFICATION);
    createEAttribute(textModificationEClass, TEXT_MODIFICATION__PATTERN);
    createEAttribute(textModificationEClass, TEXT_MODIFICATION__SUBSTITUTIONS);

    keyBindingTaskEClass = createEClass(KEY_BINDING_TASK);
    createEAttribute(keyBindingTaskEClass, KEY_BINDING_TASK__SCHEME);
    createEReference(keyBindingTaskEClass, KEY_BINDING_TASK__CONTEXTS);
    createEAttribute(keyBindingTaskEClass, KEY_BINDING_TASK__PLATFORM);
    createEAttribute(keyBindingTaskEClass, KEY_BINDING_TASK__LOCALE);
    createEAttribute(keyBindingTaskEClass, KEY_BINDING_TASK__KEYS);
    createEAttribute(keyBindingTaskEClass, KEY_BINDING_TASK__COMMAND);
    createEReference(keyBindingTaskEClass, KEY_BINDING_TASK__COMMAND_PARAMETERS);

    keyBindingContextEClass = createEClass(KEY_BINDING_CONTEXT);
    createEAttribute(keyBindingContextEClass, KEY_BINDING_CONTEXT__ID);

    commandParameterEClass = createEClass(COMMAND_PARAMETER);
    createEAttribute(commandParameterEClass, COMMAND_PARAMETER__ID);
    createEAttribute(commandParameterEClass, COMMAND_PARAMETER__VALUE);

    mylynQueryTaskEClass = createEClass(MYLYN_QUERY_TASK);
    createEAttribute(mylynQueryTaskEClass, MYLYN_QUERY_TASK__CONNECTOR_KIND);
    createEAttribute(mylynQueryTaskEClass, MYLYN_QUERY_TASK__SUMMARY);
    createEAttribute(mylynQueryTaskEClass, MYLYN_QUERY_TASK__REPOSITORY_URL);
    createEAttribute(mylynQueryTaskEClass, MYLYN_QUERY_TASK__RELATIVE_URL);

    mylynQueriesTaskEClass = createEClass(MYLYN_QUERIES_TASK);
    createEAttribute(mylynQueriesTaskEClass, MYLYN_QUERIES_TASK__CONNECTOR_KIND);
    createEAttribute(mylynQueriesTaskEClass, MYLYN_QUERIES_TASK__REPOSITORY_URL);
    createEReference(mylynQueriesTaskEClass, MYLYN_QUERIES_TASK__QUERIES);

    queryEClass = createEClass(QUERY);
    createEReference(queryEClass, QUERY__TASK);
    createEAttribute(queryEClass, QUERY__SUMMARY);
    createEAttribute(queryEClass, QUERY__URL);
    createEReference(queryEClass, QUERY__ATTRIBUTES);

    queryAttributeEClass = createEClass(QUERY_ATTRIBUTE);
    createEAttribute(queryAttributeEClass, QUERY_ATTRIBUTE__KEY);
    createEAttribute(queryAttributeEClass, QUERY_ATTRIBUTE__VALUE);

    mylynBuildsTaskEClass = createEClass(MYLYN_BUILDS_TASK);
    createEAttribute(mylynBuildsTaskEClass, MYLYN_BUILDS_TASK__CONNECTOR_KIND);
    createEAttribute(mylynBuildsTaskEClass, MYLYN_BUILDS_TASK__SERVER_URL);
    createEReference(mylynBuildsTaskEClass, MYLYN_BUILDS_TASK__BUILD_PLANS);

    buildPlanEClass = createEClass(BUILD_PLAN);
    createEAttribute(buildPlanEClass, BUILD_PLAN__NAME);

    jreTaskEClass = createEClass(JRE_TASK);
    createEAttribute(jreTaskEClass, JRE_TASK__VERSION);
    createEAttribute(jreTaskEClass, JRE_TASK__LOCATION);

    // Create enums
    setupTaskScopeEEnum = createEEnum(SETUP_TASK_SCOPE);
    triggerEEnum = createEEnum(TRIGGER);
    componentTypeEEnum = createEEnum(COMPONENT_TYPE);
    variableTypeEEnum = createEEnum(VARIABLE_TYPE);

    // Create data types
    triggerSetEDataType = createEDataType(TRIGGER_SET);
    exceptionEDataType = createEDataType(EXCEPTION);
    uriEDataType = createEDataType(URI);
    licenseInfoEDataType = createEDataType(LICENSE_INFO);
    versionEDataType = createEDataType(VERSION);
    versionRangeEDataType = createEDataType(VERSION_RANGE);
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
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    WorkingSetsPackage theWorkingSetsPackage = (WorkingSetsPackage)EPackage.Registry.INSTANCE
        .getEPackage(WorkingSetsPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    eclipseEClass.getESuperTypes().add(getConfigurableItem());
    configurationEClass.getESuperTypes().add(getScopeRoot());
    configurableItemEClass.getESuperTypes().add(getScopeRoot());
    projectEClass.getESuperTypes().add(getConfigurableItem());
    branchEClass.getESuperTypes().add(getConfigurableItem());
    preferencesEClass.getESuperTypes().add(getScopeRoot());
    scopeRootEClass.getESuperTypes().add(getSetupTaskContainer());
    compoundSetupTaskEClass.getESuperTypes().add(getSetupTask());
    compoundSetupTaskEClass.getESuperTypes().add(getSetupTaskContainer());
    contextVariableTaskEClass.getESuperTypes().add(getSetupTask());
    eclipseIniTaskEClass.getESuperTypes().add(getSetupTask());
    linkLocationTaskEClass.getESuperTypes().add(getSetupTask());
    p2TaskEClass.getESuperTypes().add(getSetupTask());
    basicMaterializationTaskEClass.getESuperTypes().add(getSetupTask());
    buckminsterImportTaskEClass.getESuperTypes().add(getBasicMaterializationTask());
    materializationTaskEClass.getESuperTypes().add(getBasicMaterializationTask());
    manualSourceLocatorEClass.getESuperTypes().add(getSourceLocator());
    automaticSourceLocatorEClass.getESuperTypes().add(getSourceLocator());
    componentDefinitionEClass.getESuperTypes().add(getComponentExtension());
    targletImportTaskEClass.getESuperTypes().add(getSetupTask());
    targletTaskEClass.getESuperTypes().add(getSetupTask());
    targletTaskEClass.getESuperTypes().add(getTargletData());
    targletEClass.getESuperTypes().add(getTargletData());
    redirectionTaskEClass.getESuperTypes().add(getSetupTask());
    apiBaselineTaskEClass.getESuperTypes().add(getSetupTask());
    gitCloneTaskEClass.getESuperTypes().add(getSetupTask());
    projectSetImportTaskEClass.getESuperTypes().add(getSetupTask());
    targetPlatformTaskEClass.getESuperTypes().add(getSetupTask());
    eclipsePreferenceTaskEClass.getESuperTypes().add(getSetupTask());
    fileAssociationTaskEClass.getESuperTypes().add(getSetupTask());
    workingSetTaskEClass.getESuperTypes().add(getSetupTask());
    resourceCopyTaskEClass.getESuperTypes().add(getSetupTask());
    resourceCreationTaskEClass.getESuperTypes().add(getSetupTask());
    textModifyTaskEClass.getESuperTypes().add(getSetupTask());
    keyBindingTaskEClass.getESuperTypes().add(getSetupTask());
    mylynQueryTaskEClass.getESuperTypes().add(getSetupTask());
    mylynQueriesTaskEClass.getESuperTypes().add(getSetupTask());
    mylynBuildsTaskEClass.getESuperTypes().add(getSetupTask());
    jreTaskEClass.getESuperTypes().add(getSetupTask());

    // Initialize classes and features; add operations and parameters
    initEClass(metaIndexEClass, MetaIndex.class, "MetaIndex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMetaIndex_Indexes(), getIndex(), null, "indexes", null, 0, -1, MetaIndex.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(indexEClass, Index.class, "Index", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIndex_Name(), ecorePackage.getEString(), "name", null, 1, 1, Index.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getIndex_URI(), getURI(), "uRI", null, 1, 1, Index.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getIndex_OldURIs(), getURI(), "oldURIs", null, 0, -1, Index.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eclipseEClass, Eclipse.class, "Eclipse", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEclipse_Configuration(), getConfiguration(), getConfiguration_EclipseVersions(), "configuration",
        null, 0, 1, Eclipse.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEclipse_Version(), ecorePackage.getEString(), "version", null, 1, 1, Eclipse.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(configurationEClass, Configuration.class, "Configuration", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfiguration_EclipseVersions(), getEclipse(), getEclipse_Configuration(), "eclipseVersions",
        null, 1, -1, Configuration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    getConfiguration_EclipseVersions().getEKeys().add(getEclipse_Version());
    initEReference(getConfiguration_Projects(), getProject(), getProject_Configuration(), "projects", null, 1, -1,
        Configuration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    getConfiguration_Projects().getEKeys().add(getProject_Name());

    initEClass(configurableItemEClass, ConfigurableItem.class, "ConfigurableItem", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(projectEClass, Project.class, "Project", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getProject_Configuration(), getConfiguration(), getConfiguration_Projects(), "configuration", null,
        0, 1, Project.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProject_Branches(), getBranch(), getBranch_Project(), "branches", null, 1, -1, Project.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    getProject_Branches().getEKeys().add(getBranch_Name());
    initEAttribute(getProject_Name(), ecorePackage.getEString(), "name", null, 1, 1, Project.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProject_Label(), ecorePackage.getEString(), "label", null, 0, 1, Project.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProject_Restrictions(), getEclipse(), null, "restrictions", null, 0, -1, Project.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(branchEClass, Branch.class, "Branch", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBranch_Project(), getProject(), getProject_Branches(), "project", null, 0, 1, Branch.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBranch_Name(), ecorePackage.getEString(), "name", null, 1, 1, Branch.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getBranch_Restrictions(), getEclipse(), null, "restrictions", null, 0, -1, Branch.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(preferencesEClass, Preferences.class, "Preferences", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPreferences_InstallFolder(), ecorePackage.getEString(), "installFolder", null, 1, 1,
        Preferences.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getPreferences_BundlePoolFolder(), ecorePackage.getEString(), "bundlePoolFolder", null, 0, 1,
        Preferences.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getPreferences_BundlePoolFolderTP(), ecorePackage.getEString(), "bundlePoolFolderTP", null, 0, 1,
        Preferences.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getPreferences_AcceptedLicenses(), getLicenseInfo(), "acceptedLicenses", null, 0, -1,
        Preferences.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(setupEClass, Setup.class, "Setup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSetup_Branch(), getBranch(), null, "branch", null, 1, 1, Setup.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getSetup_EclipseVersion(), getEclipse(), null, "eclipseVersion", null, 1, 1, Setup.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getSetup_Preferences(), getPreferences(), null, "preferences", null, 1, 1, Setup.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    EOperation op = addEOperation(setupEClass, getSetupTask(), "getSetupTasks", 0, -1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEBoolean(), "filterRestrictions", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, getTrigger(), "trigger", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(setupTaskEClass, SetupTask.class, "SetupTask", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSetupTask_Requirements(), getSetupTask(), null, "requirements", null, 0, -1, SetupTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getSetupTask_Restrictions(), getConfigurableItem(), null, "restrictions", null, 0, -1,
        SetupTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSetupTask_Disabled(), ecorePackage.getEBoolean(), "disabled", null, 0, 1, SetupTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSetupTask_Scope(), getSetupTaskScope(), "scope", null, 0, 1, SetupTask.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getSetupTask_ExcludedTriggers(), getTriggerSet(), "excludedTriggers", "", 1, 1, SetupTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSetupTask_Documentation(), ecorePackage.getEString(), "documentation", null, 0, 1,
        SetupTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    addEOperation(setupTaskEClass, getScopeRoot(), "getScopeRoot", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = addEOperation(setupTaskEClass, ecorePackage.getEBoolean(), "requires", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, getSetupTask(), "setupTask", 0, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(setupTaskEClass, getTriggerSet(), "getValidTriggers", 1, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(setupTaskEClass, getTriggerSet(), "getTriggers", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(setupTaskContainerEClass, SetupTaskContainer.class, "SetupTaskContainer", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSetupTaskContainer_SetupTasks(), getSetupTask(), null, "setupTasks", null, 0, -1,
        SetupTaskContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(scopeRootEClass, ScopeRoot.class, "ScopeRoot", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(scopeRootEClass, getSetupTaskScope(), "getScope", 1, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(scopeRootEClass, getScopeRoot(), "getParentScopeRoot", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(compoundSetupTaskEClass, CompoundSetupTask.class, "CompoundSetupTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCompoundSetupTask_Name(), ecorePackage.getEString(), "name", null, 1, 1, CompoundSetupTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(contextVariableTaskEClass, ContextVariableTask.class, "ContextVariableTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getContextVariableTask_Type(), getVariableType(), "type", "STRING", 1, 1, ContextVariableTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getContextVariableTask_Name(), ecorePackage.getEString(), "name", null, 1, 1,
        ContextVariableTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getContextVariableTask_Value(), ecorePackage.getEString(), "value", null, 0, 1,
        ContextVariableTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getContextVariableTask_StringSubstitution(), ecorePackage.getEBoolean(), "stringSubstitution", null,
        0, 1, ContextVariableTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getContextVariableTask_Label(), ecorePackage.getEString(), "label", null, 0, 1,
        ContextVariableTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getContextVariableTask_Choices(), getVariableChoice(), null, "choices", null, 0, -1,
        ContextVariableTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(variableChoiceEClass, VariableChoice.class, "VariableChoice", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getVariableChoice_Value(), ecorePackage.getEString(), "value", null, 1, 1, VariableChoice.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getVariableChoice_Label(), ecorePackage.getEString(), "label", null, 0, 1, VariableChoice.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eclipseIniTaskEClass, EclipseIniTask.class, "EclipseIniTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEclipseIniTask_Option(), ecorePackage.getEString(), "option", null, 1, 1, EclipseIniTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEclipseIniTask_Value(), ecorePackage.getEString(), "value", null, 0, 1, EclipseIniTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEclipseIniTask_Vm(), ecorePackage.getEBoolean(), "vm", null, 0, 1, EclipseIniTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(linkLocationTaskEClass, LinkLocationTask.class, "LinkLocationTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLinkLocationTask_Path(), ecorePackage.getEString(), "path", null, 1, 1, LinkLocationTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLinkLocationTask_Name(), ecorePackage.getEString(), "name", null, 0, 1, LinkLocationTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(p2TaskEClass, P2Task.class, "P2Task", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getP2Task_InstallableUnits(), getInstallableUnit(), null, "installableUnits", null, 1, -1,
        P2Task.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getP2Task_P2Repositories(), getP2Repository(), null, "p2Repositories", null, 1, -1, P2Task.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getP2Task_LicenseConfirmationDisabled(), ecorePackage.getEBoolean(), "licenseConfirmationDisabled",
        null, 0, 1, P2Task.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getP2Task_MergeDisabled(), ecorePackage.getEBoolean(), "mergeDisabled", null, 0, 1, P2Task.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(installableUnitEClass, InstallableUnit.class, "InstallableUnit", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getInstallableUnit_ID(), ecorePackage.getEString(), "iD", null, 1, 1, InstallableUnit.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getInstallableUnit_VersionRange(), getVersionRange(), "versionRange", "0.0.0", 0, 1,
        InstallableUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(p2RepositoryEClass, P2Repository.class, "P2Repository", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getP2Repository_URL(), ecorePackage.getEString(), "uRL", null, 1, 1, P2Repository.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(basicMaterializationTaskEClass, BasicMaterializationTask.class, "BasicMaterializationTask", IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBasicMaterializationTask_TargetPlatform(), ecorePackage.getEString(), "targetPlatform",
        "${setup.branch.dir/tp}", 1, 1, BasicMaterializationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(buckminsterImportTaskEClass, BuckminsterImportTask.class, "BuckminsterImportTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBuckminsterImportTask_Mspec(), ecorePackage.getEString(), "mspec", null, 1, 1,
        BuckminsterImportTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(materializationTaskEClass, MaterializationTask.class, "MaterializationTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMaterializationTask_RootComponents(), getComponent(), null, "rootComponents", null, 1, -1,
        MaterializationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMaterializationTask_SourceLocators(), getSourceLocator(), null, "sourceLocators", null, 0, -1,
        MaterializationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMaterializationTask_P2Repositories(), getP2Repository(), null, "p2Repositories", null, 0, -1,
        MaterializationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(componentEClass, Component.class, "Component", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getComponent_Name(), ecorePackage.getEString(), "name", null, 1, 1, Component.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponent_Type(), getComponentType(), "type", null, 1, 1, Component.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponent_VersionRange(), getVersionRange(), "versionRange", "0.0.0", 0, 1, Component.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sourceLocatorEClass, SourceLocator.class, "SourceLocator", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(manualSourceLocatorEClass, ManualSourceLocator.class, "ManualSourceLocator", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getManualSourceLocator_Location(), ecorePackage.getEString(), "location", null, 1, 1,
        ManualSourceLocator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getManualSourceLocator_ComponentNamePattern(), ecorePackage.getEString(), "componentNamePattern",
        null, 0, 1, ManualSourceLocator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getManualSourceLocator_ComponentTypes(), getComponentType(), "componentTypes", null, 1, -1,
        ManualSourceLocator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(automaticSourceLocatorEClass, AutomaticSourceLocator.class, "AutomaticSourceLocator", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAutomaticSourceLocator_RootFolder(), ecorePackage.getEString(), "rootFolder", null, 1, 1,
        AutomaticSourceLocator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAutomaticSourceLocator_LocateNestedProjects(), ecorePackage.getEBoolean(),
        "locateNestedProjects", null, 0, 1, AutomaticSourceLocator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(componentExtensionEClass, ComponentExtension.class, "ComponentExtension", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComponentExtension_Dependencies(), getInstallableUnit(), null, "dependencies", null, 0, -1,
        ComponentExtension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(componentDefinitionEClass, ComponentDefinition.class, "ComponentDefinition", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getComponentDefinition_ID(), ecorePackage.getEString(), "iD", null, 1, 1, ComponentDefinition.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComponentDefinition_Version(), getVersion(), "version", "1.0.0", 0, 1, ComponentDefinition.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(targletImportTaskEClass, TargletImportTask.class, "TargletImportTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTargletImportTask_TargletURI(), ecorePackage.getEString(), "targletURI", null, 1, 1,
        TargletImportTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(targletTaskEClass, TargletTask.class, "TargletTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(targletContainerEClass, TargletContainer.class, "TargletContainer", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTargletContainer_Targlets(), getTarglet(), null, "targlets", null, 0, -1, TargletContainer.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(targletEClass, Targlet.class, "Targlet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(targletDataEClass, TargletData.class, "TargletData", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTargletData_Name(), ecorePackage.getEString(), "name", null, 1, 1, TargletData.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTargletData_Roots(), getInstallableUnit(), null, "roots", null, 1, -1, TargletData.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getTargletData_SourceLocators(), getAutomaticSourceLocator(), null, "sourceLocators", null, 0, -1,
        TargletData.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTargletData_RepositoryLists(), getRepositoryList(), null, "repositoryLists", null, 0, -1,
        TargletData.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTargletData_ActiveRepositoryList(), ecorePackage.getEString(), "activeRepositoryList", null, 0,
        1, TargletData.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getTargletData_ActiveP2Repositories(), getP2Repository(), null, "activeP2Repositories", null, 0, -1,
        TargletData.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(repositoryListEClass, RepositoryList.class, "RepositoryList", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRepositoryList_P2Repositories(), getP2Repository(), null, "p2Repositories", null, 0, -1,
        RepositoryList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRepositoryList_Name(), ecorePackage.getEString(), "name", null, 1, 1, RepositoryList.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(redirectionTaskEClass, RedirectionTask.class, "RedirectionTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRedirectionTask_SourceURL(), ecorePackage.getEString(), "sourceURL", null, 1, 1,
        RedirectionTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRedirectionTask_TargetURL(), ecorePackage.getEString(), "targetURL", null, 1, 1,
        RedirectionTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(apiBaselineTaskEClass, ApiBaselineTask.class, "ApiBaselineTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getApiBaselineTask_Version(), ecorePackage.getEString(), "version", null, 1, 1,
        ApiBaselineTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getApiBaselineTask_ContainerFolder(), ecorePackage.getEString(), "containerFolder",
        "${setup.project.dir/.baselines}", 1, 1, ApiBaselineTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getApiBaselineTask_ZipLocation(), ecorePackage.getEString(), "zipLocation", null, 1, 1,
        ApiBaselineTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(gitCloneTaskEClass, GitCloneTask.class, "GitCloneTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGitCloneTask_Location(), ecorePackage.getEString(), "location", null, 1, 1, GitCloneTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getGitCloneTask_RemoteName(), ecorePackage.getEString(), "remoteName", "origin", 1, 1,
        GitCloneTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getGitCloneTask_RemoteURI(), ecorePackage.getEString(), "remoteURI", null, 1, 1, GitCloneTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getGitCloneTask_UserID(), ecorePackage.getEString(), "userID", "${git.user.id}", 0, 1,
        GitCloneTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getGitCloneTask_CheckoutBranch(), ecorePackage.getEString(), "checkoutBranch", null, 1, 1,
        GitCloneTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(projectSetImportTaskEClass, ProjectSetImportTask.class, "ProjectSetImportTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProjectSetImportTask_URL(), ecorePackage.getEString(), "uRL", null, 1, 1,
        ProjectSetImportTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(targetPlatformTaskEClass, TargetPlatformTask.class, "TargetPlatformTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTargetPlatformTask_Name(), ecorePackage.getEString(), "name", null, 1, 1,
        TargetPlatformTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(eclipsePreferenceTaskEClass, EclipsePreferenceTask.class, "EclipsePreferenceTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEclipsePreferenceTask_Key(), ecorePackage.getEString(), "key", null, 1, 1,
        EclipsePreferenceTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEclipsePreferenceTask_Value(), ecorePackage.getEString(), "value", null, 0, 1,
        EclipsePreferenceTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(fileAssociationTaskEClass, FileAssociationTask.class, "FileAssociationTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFileAssociationTask_FilePattern(), ecorePackage.getEString(), "filePattern", null, 1, 1,
        FileAssociationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFileAssociationTask_DefaultEditorID(), ecorePackage.getEString(), "defaultEditorID", null, 0, 1,
        FileAssociationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getFileAssociationTask_Editors(), getFileEditor(), null, "editors", null, 0, -1,
        FileAssociationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(fileEditorEClass, FileEditor.class, "FileEditor", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFileEditor_ID(), ecorePackage.getEString(), "iD", null, 1, 1, FileEditor.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(workingSetTaskEClass, WorkingSetTask.class, "WorkingSetTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWorkingSetTask_WorkingSets(), theWorkingSetsPackage.getWorkingSet(), null, "workingSets", null,
        0, -1, WorkingSetTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(resourceCopyTaskEClass, ResourceCopyTask.class, "ResourceCopyTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getResourceCopyTask_SourceURL(), ecorePackage.getEString(), "sourceURL", null, 1, 1,
        ResourceCopyTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceCopyTask_TargetURL(), ecorePackage.getEString(), "targetURL", null, 1, 1,
        ResourceCopyTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(resourceCreationTaskEClass, ResourceCreationTask.class, "ResourceCreationTask", !IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getResourceCreationTask_Content(), ecorePackage.getEString(), "content", null, 1, 1,
        ResourceCreationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceCreationTask_TargetURL(), ecorePackage.getEString(), "targetURL", null, 1, 1,
        ResourceCreationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceCreationTask_Encoding(), ecorePackage.getEString(), "encoding", null, 0, 1,
        ResourceCreationTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(textModifyTaskEClass, TextModifyTask.class, "TextModifyTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTextModifyTask_URL(), ecorePackage.getEString(), "uRL", null, 1, 1, TextModifyTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTextModifyTask_Modifications(), getTextModification(), null, "modifications", null, 0, -1,
        TextModifyTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTextModifyTask_Encoding(), ecorePackage.getEString(), "encoding", null, 0, 1,
        TextModifyTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(textModificationEClass, TextModification.class, "TextModification", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTextModification_Pattern(), ecorePackage.getEString(), "pattern", null, 1, 1,
        TextModification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTextModification_Substitutions(), ecorePackage.getEString(), "substitutions", null, 0, -1,
        TextModification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(keyBindingTaskEClass, KeyBindingTask.class, "KeyBindingTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getKeyBindingTask_Scheme(), ecorePackage.getEString(), "scheme",
        "org.eclipse.ui.defaultAcceleratorConfiguration", 1, 1, KeyBindingTask.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getKeyBindingTask_Contexts(), getKeyBindingContext(), null, "contexts", null, 1, -1,
        KeyBindingTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getKeyBindingTask_Platform(), ecorePackage.getEString(), "platform", null, 0, 1,
        KeyBindingTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getKeyBindingTask_Locale(), ecorePackage.getEString(), "locale", null, 0, 1, KeyBindingTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getKeyBindingTask_Keys(), ecorePackage.getEString(), "keys", null, 1, 1, KeyBindingTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getKeyBindingTask_Command(), ecorePackage.getEString(), "command", null, 1, 1, KeyBindingTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getKeyBindingTask_CommandParameters(), getCommandParameter(), null, "commandParameters", null, 0,
        -1, KeyBindingTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(keyBindingContextEClass, KeyBindingContext.class, "KeyBindingContext", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getKeyBindingContext_ID(), ecorePackage.getEString(), "iD", "org.eclipse.ui.contexts.window", 0, 1,
        KeyBindingContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(commandParameterEClass, CommandParameter.class, "CommandParameter", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCommandParameter_ID(), ecorePackage.getEString(), "iD", null, 1, 1, CommandParameter.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCommandParameter_Value(), ecorePackage.getEString(), "value", null, 1, 1, CommandParameter.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mylynQueryTaskEClass, MylynQueryTask.class, "MylynQueryTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMylynQueryTask_ConnectorKind(), ecorePackage.getEString(), "connectorKind", "bugzilla", 1, 1,
        MylynQueryTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMylynQueryTask_Summary(), ecorePackage.getEString(), "summary", null, 1, 1, MylynQueryTask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMylynQueryTask_RepositoryURL(), ecorePackage.getEString(), "repositoryURL", null, 1, 1,
        MylynQueryTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMylynQueryTask_RelativeURL(), ecorePackage.getEString(), "relativeURL", null, 1, 1,
        MylynQueryTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(mylynQueriesTaskEClass, MylynQueriesTask.class, "MylynQueriesTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMylynQueriesTask_ConnectorKind(), ecorePackage.getEString(), "connectorKind", "bugzilla", 1, 1,
        MylynQueriesTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMylynQueriesTask_RepositoryURL(), ecorePackage.getEString(), "repositoryURL", null, 1, 1,
        MylynQueriesTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getMylynQueriesTask_Queries(), getQuery(), getQuery_Task(), "queries", null, 0, -1,
        MylynQueriesTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(queryEClass, Query.class, "Query", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQuery_Task(), getMylynQueriesTask(), getMylynQueriesTask_Queries(), "task", null, 0, 1,
        Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getQuery_Summary(), ecorePackage.getEString(), "summary", null, 1, 1, Query.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getQuery_URL(), ecorePackage.getEString(), "uRL", null, 0, 1, Query.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getQuery_Attributes(), getQueryAttribute(), null, "attributes", null, 0, -1, Query.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(queryAttributeEClass, Map.Entry.class, "QueryAttribute", !IS_ABSTRACT, !IS_INTERFACE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getQueryAttribute_Key(), ecorePackage.getEString(), "key", null, 1, 1, Map.Entry.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getQueryAttribute_Value(), ecorePackage.getEString(), "value", null, 0, 1, Map.Entry.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mylynBuildsTaskEClass, MylynBuildsTask.class, "MylynBuildsTask", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMylynBuildsTask_ConnectorKind(), ecorePackage.getEString(), "connectorKind",
        "org.eclipse.mylyn.hudson", 1, 1, MylynBuildsTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMylynBuildsTask_ServerURL(), ecorePackage.getEString(), "serverURL", null, 1, 1,
        MylynBuildsTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getMylynBuildsTask_BuildPlans(), getBuildPlan(), null, "buildPlans", null, 1, -1,
        MylynBuildsTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(buildPlanEClass, BuildPlan.class, "BuildPlan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBuildPlan_Name(), ecorePackage.getEString(), "name", null, 1, 1, BuildPlan.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jreTaskEClass, JRETask.class, "JRETask", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJRETask_Version(), ecorePackage.getEString(), "version", null, 1, 1, JRETask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getJRETask_Location(), ecorePackage.getEString(), "location", null, 1, 1, JRETask.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(setupTaskScopeEEnum, SetupTaskScope.class, "SetupTaskScope");
    addEEnumLiteral(setupTaskScopeEEnum, SetupTaskScope.NONE);
    addEEnumLiteral(setupTaskScopeEEnum, SetupTaskScope.ECLIPSE);
    addEEnumLiteral(setupTaskScopeEEnum, SetupTaskScope.PROJECT);
    addEEnumLiteral(setupTaskScopeEEnum, SetupTaskScope.BRANCH);
    addEEnumLiteral(setupTaskScopeEEnum, SetupTaskScope.USER);
    addEEnumLiteral(setupTaskScopeEEnum, SetupTaskScope.CONFIGURATION);

    initEEnum(triggerEEnum, Trigger.class, "Trigger");
    addEEnumLiteral(triggerEEnum, Trigger.BOOTSTRAP);
    addEEnumLiteral(triggerEEnum, Trigger.STARTUP);
    addEEnumLiteral(triggerEEnum, Trigger.MANUAL);

    initEEnum(componentTypeEEnum, ComponentType.class, "ComponentType");
    addEEnumLiteral(componentTypeEEnum, ComponentType.ECLIPSE_FEATURE);
    addEEnumLiteral(componentTypeEEnum, ComponentType.OSGI_BUNDLE);
    addEEnumLiteral(componentTypeEEnum, ComponentType.BUCKMINSTER);
    addEEnumLiteral(componentTypeEEnum, ComponentType.JAR);
    addEEnumLiteral(componentTypeEEnum, ComponentType.BOM);
    addEEnumLiteral(componentTypeEEnum, ComponentType.UNKNOWN);

    initEEnum(variableTypeEEnum, VariableType.class, "VariableType");
    addEEnumLiteral(variableTypeEEnum, VariableType.STRING);
    addEEnumLiteral(variableTypeEEnum, VariableType.TEXT);
    addEEnumLiteral(variableTypeEEnum, VariableType.PASSWORD);
    addEEnumLiteral(variableTypeEEnum, VariableType.PATTERN);
    addEEnumLiteral(variableTypeEEnum, VariableType.URI);
    addEEnumLiteral(variableTypeEEnum, VariableType.FILE);
    addEEnumLiteral(variableTypeEEnum, VariableType.FOLDER);
    addEEnumLiteral(variableTypeEEnum, VariableType.RESOURCE);
    addEEnumLiteral(variableTypeEEnum, VariableType.CONTAINER);
    addEEnumLiteral(variableTypeEEnum, VariableType.PROJECT);
    addEEnumLiteral(variableTypeEEnum, VariableType.BOOLEAN);
    addEEnumLiteral(variableTypeEEnum, VariableType.INTEGER);
    addEEnumLiteral(variableTypeEEnum, VariableType.FLOAT);

    // Initialize data types
    initEDataType(triggerSetEDataType, Set.class, "TriggerSet", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS,
        "java.util.Set<org.eclipse.emf.cdo.releng.setup.Trigger>");
    initEDataType(exceptionEDataType, Exception.class, "Exception", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(licenseInfoEDataType, LicenseInfo.class, "LicenseInfo", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(versionEDataType, Version.class, "Version", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(versionRangeEDataType, VersionRange.class, "VersionRange", IS_SERIALIZABLE,
        !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
    // http://www.eclipse.org/CDO/releng/setup/enablement
    createEnablementAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    addAnnotation(getIndex_URI(), source, new String[] { "kind", "attribute", "name", "uri" });
    addAnnotation(getInstallableUnit_ID(), source, new String[] { "kind", "attribute", "name", "id" });
    addAnnotation(getP2Repository_URL(), source, new String[] { "kind", "attribute", "name", "url" });
    addAnnotation(getComponentDefinition_ID(), source, new String[] { "kind", "attribute", "name", "id" });
    addAnnotation(getFileEditor_ID(), source, new String[] { "kind", "attribute", "name", "id" });
    addAnnotation(getTextModifyTask_URL(), source, new String[] { "kind", "attribute", "name", "url" });
    addAnnotation(getKeyBindingContext_ID(), source, new String[] { "kind", "attribute", "name", "id" });
    addAnnotation(getCommandParameter_ID(), source, new String[] { "kind", "attribute", "name", "id" });
    addAnnotation(getQuery_URL(), source, new String[] { "kind", "attribute", "name", "url" });
  }

  /**
   * Initializes the annotations for <b>http://www.eclipse.org/CDO/releng/setup/enablement</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createEnablementAnnotations()
  {
    String source = "http://www.eclipse.org/CDO/releng/setup/enablement";
    addAnnotation(
        basicMaterializationTaskEClass,
        source,
        new String[] {
            "variableName",
            "setup.buckminster.p2",
            "repository",
            "http://download.eclipse.org/tools/buckminster/updates-4.3",
            "installableUnits",
            "org.eclipse.buckminster.ant org.eclipse.buckminster.core org.eclipse.buckminster.sax org.eclipse.buckminster.download org.eclipse.buckminster.generic org.eclipse.buckminster.executor org.eclipse.buckminster.fetcher org.eclipse.buckminster.osgi.filter org.eclipse.buckminster.jarprocessor org.eclipse.buckminster.jdt org.eclipse.buckminster.junit org.eclipse.buckminster.pde org.eclipse.buckminster.git org.eclipse.buckminster.mspec org.eclipse.buckminster.rmap" });
    addAnnotation(gitCloneTaskEClass, source, new String[] { "variableName", "setup.egit.p2", "repository",
        "http://download.eclipse.org/egit/updates", "installableUnits", "org.eclipse.egit.feature.group" });
    addAnnotation(mylynQueryTaskEClass, source, new String[] { "variableName", "setup.mylyn.p2", "repository",
        "http://download.eclipse.org/mylyn/releases/latest", "installableUnits",
        "org.eclipse.mylyn.tasks.core org.eclipse.mylyn.tasks.ui" });
    addAnnotation(mylynQueriesTaskEClass, source, new String[] { "variableName", "setup.mylyn.p2", "repository",
        "http://download.eclipse.org/mylyn/releases/latest", "installableUnits",
        "org.eclipse.mylyn.tasks.core org.eclipse.mylyn.tasks.ui" });
    addAnnotation(mylynBuildsTaskEClass, source, new String[] { "variableName", "setup.mylyn.p2", "repository",
        "http://download.eclipse.org/mylyn/releases/latest", "installableUnits",
        "org.eclipse.mylyn.tasks.core org.eclipse.mylyn.tasks.ui" });
  }

} // SetupPackageImpl
