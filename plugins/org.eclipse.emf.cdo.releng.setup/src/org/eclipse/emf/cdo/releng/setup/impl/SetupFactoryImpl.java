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
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.BuildPlan;
import org.eclipse.emf.cdo.releng.setup.CommandParameter;
import org.eclipse.emf.cdo.releng.setup.Component;
import org.eclipse.emf.cdo.releng.setup.ComponentDefinition;
import org.eclipse.emf.cdo.releng.setup.ComponentExtension;
import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.EclipseIniTask;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.FileAssociationTask;
import org.eclipse.emf.cdo.releng.setup.FileAssociationsTask;
import org.eclipse.emf.cdo.releng.setup.FileEditor;
import org.eclipse.emf.cdo.releng.setup.FileMapping;
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
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.TargetPlatformTask;
import org.eclipse.emf.cdo.releng.setup.Targlet;
import org.eclipse.emf.cdo.releng.setup.TargletData;
import org.eclipse.emf.cdo.releng.setup.TargletImportTask;
import org.eclipse.emf.cdo.releng.setup.TargletTask;
import org.eclipse.emf.cdo.releng.setup.TextModification;
import org.eclipse.emf.cdo.releng.setup.TextModifyTask;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.VariableChoice;
import org.eclipse.emf.cdo.releng.setup.VariableType;
import org.eclipse.emf.cdo.releng.setup.WorkingSetTask;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
@SuppressWarnings("deprecation")
public class SetupFactoryImpl extends EFactoryImpl implements SetupFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SetupFactory init()
  {
    try
    {
      SetupFactory theSetupFactory = (SetupFactory)EPackage.Registry.INSTANCE.getEFactory(SetupPackage.eNS_URI);
      if (theSetupFactory != null)
      {
        return theSetupFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SetupFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case SetupPackage.META_INDEX:
      return createMetaIndex();
    case SetupPackage.INDEX:
      return createIndex();
    case SetupPackage.ECLIPSE:
      return createEclipse();
    case SetupPackage.CONFIGURATION:
      return createConfiguration();
    case SetupPackage.PROJECT:
      return createProject();
    case SetupPackage.BRANCH:
      return createBranch();
    case SetupPackage.PREFERENCES:
      return createPreferences();
    case SetupPackage.SETUP:
      return createSetup();
    case SetupPackage.COMPOUND_SETUP_TASK:
      return createCompoundSetupTask();
    case SetupPackage.CONTEXT_VARIABLE_TASK:
      return createContextVariableTask();
    case SetupPackage.VARIABLE_CHOICE:
      return createVariableChoice();
    case SetupPackage.ECLIPSE_INI_TASK:
      return createEclipseIniTask();
    case SetupPackage.LINK_LOCATION_TASK:
      return createLinkLocationTask();
    case SetupPackage.P2_TASK:
      return createP2Task();
    case SetupPackage.INSTALLABLE_UNIT:
      return createInstallableUnit();
    case SetupPackage.P2_REPOSITORY:
      return createP2Repository();
    case SetupPackage.BUCKMINSTER_IMPORT_TASK:
      return createBuckminsterImportTask();
    case SetupPackage.MATERIALIZATION_TASK:
      return createMaterializationTask();
    case SetupPackage.COMPONENT:
      return createComponent();
    case SetupPackage.MANUAL_SOURCE_LOCATOR:
      return createManualSourceLocator();
    case SetupPackage.AUTOMATIC_SOURCE_LOCATOR:
      return createAutomaticSourceLocator();
    case SetupPackage.COMPONENT_EXTENSION:
      return createComponentExtension();
    case SetupPackage.COMPONENT_DEFINITION:
      return createComponentDefinition();
    case SetupPackage.TARGLET_IMPORT_TASK:
      return createTargletImportTask();
    case SetupPackage.TARGLET_TASK:
      return createTargletTask();
    case SetupPackage.TARGLET:
      return createTarglet();
    case SetupPackage.REPOSITORY_LIST:
      return createRepositoryList();
    case SetupPackage.REDIRECTION_TASK:
      return createRedirectionTask();
    case SetupPackage.API_BASELINE_TASK:
      return createApiBaselineTask();
    case SetupPackage.GIT_CLONE_TASK:
      return createGitCloneTask();
    case SetupPackage.PROJECT_SET_IMPORT_TASK:
      return createProjectSetImportTask();
    case SetupPackage.TARGET_PLATFORM_TASK:
      return createTargetPlatformTask();
    case SetupPackage.ECLIPSE_PREFERENCE_TASK:
      return createEclipsePreferenceTask();
    case SetupPackage.FILE_ASSOCIATION_TASK:
      return createFileAssociationTask();
    case SetupPackage.FILE_ASSOCIATIONS_TASK:
      return createFileAssociationsTask();
    case SetupPackage.FILE_MAPPING:
      return createFileMapping();
    case SetupPackage.FILE_EDITOR:
      return createFileEditor();
    case SetupPackage.WORKING_SET_TASK:
      return createWorkingSetTask();
    case SetupPackage.RESOURCE_COPY_TASK:
      return createResourceCopyTask();
    case SetupPackage.RESOURCE_CREATION_TASK:
      return createResourceCreationTask();
    case SetupPackage.TEXT_MODIFY_TASK:
      return createTextModifyTask();
    case SetupPackage.TEXT_MODIFICATION:
      return createTextModification();
    case SetupPackage.KEY_BINDING_TASK:
      return createKeyBindingTask();
    case SetupPackage.KEY_BINDING_CONTEXT:
      return createKeyBindingContext();
    case SetupPackage.COMMAND_PARAMETER:
      return createCommandParameter();
    case SetupPackage.MYLYN_QUERY_TASK:
      return createMylynQueryTask();
    case SetupPackage.MYLYN_QUERIES_TASK:
      return createMylynQueriesTask();
    case SetupPackage.QUERY:
      return createQuery();
    case SetupPackage.QUERY_ATTRIBUTE:
      return (EObject)createQueryAttribute();
    case SetupPackage.MYLYN_BUILDS_TASK:
      return createMylynBuildsTask();
    case SetupPackage.BUILD_PLAN:
      return createBuildPlan();
    case SetupPackage.JRE_TASK:
      return createJRETask();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case SetupPackage.SETUP_TASK_SCOPE:
      return createSetupTaskScopeFromString(eDataType, initialValue);
    case SetupPackage.TRIGGER:
      return createTriggerFromString(eDataType, initialValue);
    case SetupPackage.COMPONENT_TYPE:
      return createComponentTypeFromString(eDataType, initialValue);
    case SetupPackage.VARIABLE_TYPE:
      return createVariableTypeFromString(eDataType, initialValue);
    case SetupPackage.TRIGGER_SET:
      return createTriggerSetFromString(eDataType, initialValue);
    case SetupPackage.EXCEPTION:
      return createExceptionFromString(eDataType, initialValue);
    case SetupPackage.URI:
      return createURIFromString(eDataType, initialValue);
    case SetupPackage.LICENSE_INFO:
      return createLicenseInfoFromString(eDataType, initialValue);
    case SetupPackage.VERSION:
      return createVersionFromString(eDataType, initialValue);
    case SetupPackage.VERSION_RANGE:
      return createVersionRangeFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case SetupPackage.SETUP_TASK_SCOPE:
      return convertSetupTaskScopeToString(eDataType, instanceValue);
    case SetupPackage.TRIGGER:
      return convertTriggerToString(eDataType, instanceValue);
    case SetupPackage.COMPONENT_TYPE:
      return convertComponentTypeToString(eDataType, instanceValue);
    case SetupPackage.VARIABLE_TYPE:
      return convertVariableTypeToString(eDataType, instanceValue);
    case SetupPackage.TRIGGER_SET:
      return convertTriggerSetToString(eDataType, instanceValue);
    case SetupPackage.EXCEPTION:
      return convertExceptionToString(eDataType, instanceValue);
    case SetupPackage.URI:
      return convertURIToString(eDataType, instanceValue);
    case SetupPackage.LICENSE_INFO:
      return convertLicenseInfoToString(eDataType, instanceValue);
    case SetupPackage.VERSION:
      return convertVersionToString(eDataType, instanceValue);
    case SetupPackage.VERSION_RANGE:
      return convertVersionRangeToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MetaIndex createMetaIndex()
  {
    MetaIndexImpl metaIndex = new MetaIndexImpl();
    return metaIndex;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Index createIndex()
  {
    IndexImpl index = new IndexImpl();
    return index;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Eclipse createEclipse()
  {
    EclipseImpl eclipse = new EclipseImpl();
    return eclipse;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configuration createConfiguration()
  {
    ConfigurationImpl configuration = new ConfigurationImpl();
    return configuration;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Project createProject()
  {
    ProjectImpl project = new ProjectImpl();
    return project;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Branch createBranch()
  {
    BranchImpl branch = new BranchImpl();
    return branch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ApiBaselineTask createApiBaselineTask()
  {
    ApiBaselineTaskImpl apiBaselineTask = new ApiBaselineTaskImpl();
    return apiBaselineTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GitCloneTask createGitCloneTask()
  {
    GitCloneTaskImpl gitCloneTask = new GitCloneTaskImpl();
    return gitCloneTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ProjectSetImportTask createProjectSetImportTask()
  {
    ProjectSetImportTaskImpl projectSetImportTask = new ProjectSetImportTaskImpl();
    return projectSetImportTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public P2Task createP2Task()
  {
    P2TaskImpl p2Task = new P2TaskImpl();
    return p2Task;
  }

  public P2Task createP2Task(String[] ius, String[] repositories, Set<String> existingIUs)
  {
    P2Task p2Task = createP2Task();

    EList<InstallableUnit> installableUnits = p2Task.getInstallableUnits();
    for (String id : ius)
    {
      if (existingIUs == null || !existingIUs.contains(id))
      {
        InstallableUnit iu = createInstallableUnit();
        iu.setID(id);
        installableUnits.add(iu);
      }
    }

    if (installableUnits.isEmpty())
    {
      return null;
    }

    EList<P2Repository> p2Repositories = p2Task.getP2Repositories();
    for (String url : repositories)
    {
      P2Repository repository = createP2Repository();
      repository.setURL(url);
      p2Repositories.add(repository);
    }

    return p2Task;
  }

  public P2Task createP2Task(String[] ius, String[] repositories)
  {
    return createP2Task(ius, repositories, null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InstallableUnit createInstallableUnit()
  {
    InstallableUnitImpl installableUnit = new InstallableUnitImpl();
    return installableUnit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public P2Repository createP2Repository()
  {
    P2RepositoryImpl p2Repository = new P2RepositoryImpl();
    return p2Repository;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Setup createSetup()
  {
    SetupImpl setup = new SetupImpl();
    return setup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkingSetTask createWorkingSetTask()
  {
    WorkingSetTaskImpl workingSetTask = new WorkingSetTaskImpl();
    return workingSetTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ResourceCopyTask createResourceCopyTask()
  {
    ResourceCopyTaskImpl resourceCopyTask = new ResourceCopyTaskImpl();
    return resourceCopyTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TextModifyTask createTextModifyTask()
  {
    TextModifyTaskImpl textModifyTask = new TextModifyTaskImpl();
    return textModifyTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TextModification createTextModification()
  {
    TextModificationImpl textModification = new TextModificationImpl();
    return textModification;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public KeyBindingTask createKeyBindingTask()
  {
    KeyBindingTaskImpl keyBindingTask = new KeyBindingTaskImpl();
    return keyBindingTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public KeyBindingContext createKeyBindingContext()
  {
    KeyBindingContextImpl keyBindingContext = new KeyBindingContextImpl();
    return keyBindingContext;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CommandParameter createCommandParameter()
  {
    CommandParameterImpl commandParameter = new CommandParameterImpl();
    return commandParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MylynQueryTask createMylynQueryTask()
  {
    MylynQueryTaskImpl mylynQueryTask = new MylynQueryTaskImpl();
    return mylynQueryTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MylynQueriesTask createMylynQueriesTask()
  {
    MylynQueriesTaskImpl mylynQueriesTask = new MylynQueriesTaskImpl();
    return mylynQueriesTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Query createQuery()
  {
    QueryImpl query = new QueryImpl();
    return query;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, String> createQueryAttribute()
  {
    QueryAttributeImpl queryAttribute = new QueryAttributeImpl();
    return queryAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MylynBuildsTask createMylynBuildsTask()
  {
    MylynBuildsTaskImpl mylynBuildsTask = new MylynBuildsTaskImpl();
    return mylynBuildsTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BuildPlan createBuildPlan()
  {
    BuildPlanImpl buildPlan = new BuildPlanImpl();
    return buildPlan;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JRETask createJRETask()
  {
    JRETaskImpl jreTask = new JRETaskImpl();
    return jreTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComponentExtension createComponentExtension()
  {
    ComponentExtensionImpl componentExtension = new ComponentExtensionImpl();
    return componentExtension;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComponentDefinition createComponentDefinition()
  {
    ComponentDefinitionImpl componentDefinition = new ComponentDefinitionImpl();
    return componentDefinition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TargletImportTask createTargletImportTask()
  {
    TargletImportTaskImpl targletImportTask = new TargletImportTaskImpl();
    return targletImportTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FileAssociationTask createFileAssociationTask()
  {
    FileAssociationTaskImpl fileAssociationTask = new FileAssociationTaskImpl();
    return fileAssociationTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FileAssociationsTask createFileAssociationsTask()
  {
    FileAssociationsTaskImpl fileAssociationsTask = new FileAssociationsTaskImpl();
    return fileAssociationsTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FileMapping createFileMapping()
  {
    FileMappingImpl fileMapping = new FileMappingImpl();
    return fileMapping;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FileEditor createFileEditor()
  {
    FileEditorImpl fileEditor = new FileEditorImpl();
    return fileEditor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TargetPlatformTask createTargetPlatformTask()
  {
    TargetPlatformTaskImpl targetPlatformTask = new TargetPlatformTaskImpl();
    return targetPlatformTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AutomaticSourceLocator createAutomaticSourceLocator()
  {
    AutomaticSourceLocatorImpl automaticSourceLocator = new AutomaticSourceLocatorImpl();
    return automaticSourceLocator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TargletTask createTargletTask()
  {
    TargletTaskImpl targletTask = new TargletTaskImpl();
    return targletTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Targlet createTarglet()
  {
    TargletImpl targlet = new TargletImpl();
    return targlet;
  }

  public Targlet createTarglet(TargletData source)
  {
    String activeRepositoryList = source.getActiveRepositoryList();
    if (activeRepositoryList != null && activeRepositoryList.length() == 0)
    {
      activeRepositoryList = null;
    }

    Targlet targlet = SetupFactory.eINSTANCE.createTarglet();
    targlet.setName(source.getName());
    targlet.setActiveRepositoryList(activeRepositoryList);
    targlet.setIncludeSources(source.isIncludeSources());
    targlet.setIncludeAllPlatforms(source.isIncludeAllPlatforms());

    for (InstallableUnit root : source.getRoots())
    {
      targlet.getRoots().add(EcoreUtil.copy(root));
    }

    for (AutomaticSourceLocator sourceLocator : source.getSourceLocators())
    {
      targlet.getSourceLocators().add(EcoreUtil.copy(sourceLocator));
    }

    for (RepositoryList repositoryList : source.getRepositoryLists())
    {
      targlet.getRepositoryLists().add(EcoreUtil.copy(repositoryList));
    }

    return targlet;
  }

  public EList<Targlet> createTarglets(Collection<? extends TargletData> targlets)
  {
    EList<Targlet> result = new BasicEList<Targlet>();
    for (TargletData source : targlets)
    {
      result.add(createTarglet(source));
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RepositoryList createRepositoryList()
  {
    RepositoryListImpl repositoryList = new RepositoryListImpl();
    return repositoryList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RedirectionTask createRedirectionTask()
  {
    RedirectionTaskImpl redirectionTask = new RedirectionTaskImpl();
    return redirectionTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ManualSourceLocator createManualSourceLocator()
  {
    ManualSourceLocatorImpl manualSourceLocator = new ManualSourceLocatorImpl();
    return manualSourceLocator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ContextVariableTask createContextVariableTask()
  {
    ContextVariableTaskImpl contextVariableTask = new ContextVariableTaskImpl();
    return contextVariableTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VariableChoice createVariableChoice()
  {
    VariableChoiceImpl variableChoice = new VariableChoiceImpl();
    return variableChoice;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ResourceCreationTask createResourceCreationTask()
  {
    ResourceCreationTaskImpl resourceCreationTask = new ResourceCreationTaskImpl();
    return resourceCreationTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MaterializationTask createMaterializationTask()
  {
    MaterializationTaskImpl materializationTask = new MaterializationTaskImpl();
    return materializationTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component createComponent()
  {
    ComponentImpl component = new ComponentImpl();
    return component;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclipseIniTask createEclipseIniTask()
  {
    EclipseIniTaskImpl eclipseIniTask = new EclipseIniTaskImpl();
    return eclipseIniTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupTaskScope createSetupTaskScopeFromString(EDataType eDataType, String initialValue)
  {
    SetupTaskScope result = SetupTaskScope.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
          + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertSetupTaskScopeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Trigger createTriggerFromString(EDataType eDataType, String initialValue)
  {
    Trigger result = Trigger.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
          + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertTriggerToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComponentType createComponentTypeFromString(EDataType eDataType, String initialValue)
  {
    ComponentType result = ComponentType.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
          + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertComponentTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VariableType createVariableTypeFromString(EDataType eDataType, String initialValue)
  {
    VariableType result = VariableType.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
          + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertVariableTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CompoundSetupTask createCompoundSetupTask()
  {
    CompoundSetupTaskImpl compoundSetupTask = new CompoundSetupTaskImpl();
    return compoundSetupTask;
  }

  public CompoundSetupTask createCompoundSetupTask(String name)
  {
    CompoundSetupTask compoundSetupTask = createCompoundSetupTask();
    compoundSetupTask.setName(name);
    return compoundSetupTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BuckminsterImportTask createBuckminsterImportTask()
  {
    BuckminsterImportTaskImpl buckminsterImportTask = new BuckminsterImportTaskImpl();
    return buckminsterImportTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Preferences createPreferences()
  {
    PreferencesImpl preferences = new PreferencesImpl();
    return preferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LinkLocationTask createLinkLocationTask()
  {
    LinkLocationTaskImpl linkLocationTask = new LinkLocationTaskImpl();
    return linkLocationTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclipsePreferenceTask createEclipsePreferenceTask()
  {
    EclipsePreferenceTaskImpl eclipsePreferenceTask = new EclipsePreferenceTaskImpl();
    return eclipsePreferenceTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public URI createURIFromString(EDataType eDataType, String initialValue)
  {
    return URI.createURI(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertURIToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LicenseInfo createLicenseInfoFromString(EDataType eDataType, String initialValue)
  {
    return (LicenseInfo)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertLicenseInfoToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Version createVersionFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : Version.create(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertVersionToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((Version)instanceValue).toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public VersionRange createVersionRangeFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : new VersionRange(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertVersionRangeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((VersionRange)instanceValue).toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Exception createExceptionFromString(EDataType eDataType, String initialValue)
  {
    return (Exception)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertExceptionToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Set<Trigger> createTriggerSetFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    Set<Trigger> result = new HashSet<Trigger>();
    for (String value : split(initialValue))
    {
      result.add(Trigger.get(value));
    }

    return Trigger.intern(result);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertTriggerSetToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    @SuppressWarnings("unchecked")
    Set<Trigger> triggerSet = (Set<Trigger>)instanceValue;
    return Trigger.LITERALS.get(triggerSet);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupPackage getSetupPackage()
  {
    return (SetupPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static SetupPackage getPackage()
  {
    return SetupPackage.eINSTANCE;
  }

} // SetupFactoryImpl
