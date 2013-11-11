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

import org.eclipse.emf.cdo.releng.setup.ApiBaselineTask;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.CommandParameter;
import org.eclipse.emf.cdo.releng.setup.Component;
import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.EclipseIniTask;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.GitCloneTask;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.KeyBindingTask;
import org.eclipse.emf.cdo.releng.setup.LinkLocationTask;
import org.eclipse.emf.cdo.releng.setup.ManualSourceLocator;
import org.eclipse.emf.cdo.releng.setup.MaterializationTask;
import org.eclipse.emf.cdo.releng.setup.MylynQueryTask;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask;
import org.eclipse.emf.cdo.releng.setup.RedirectionTask;
import org.eclipse.emf.cdo.releng.setup.ResourceCopyTask;
import org.eclipse.emf.cdo.releng.setup.ResourceCreationTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.TextModification;
import org.eclipse.emf.cdo.releng.setup.TextModifyTask;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.WorkingSetTask;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
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
    case SetupPackage.REDIRECTION_TASK:
      return createRedirectionTask();
    case SetupPackage.CONTEXT_VARIABLE_TASK:
      return createContextVariableTask();
    case SetupPackage.API_BASELINE_TASK:
      return createApiBaselineTask();
    case SetupPackage.GIT_CLONE_TASK:
      return createGitCloneTask();
    case SetupPackage.PROJECT_SET_IMPORT_TASK:
      return createProjectSetImportTask();
    case SetupPackage.ECLIPSE_PREFERENCE_TASK:
      return createEclipsePreferenceTask();
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
    case SetupPackage.COMMAND_PARAMETER:
      return createCommandParameter();
    case SetupPackage.MYLYN_QUERY_TASK:
      return createMylynQueryTask();
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
    case SetupPackage.TRIGGER_SET:
      return createTriggerSetFromString(eDataType, initialValue);
    case SetupPackage.EXCEPTION:
      return createExceptionFromString(eDataType, initialValue);
    case SetupPackage.URI:
      return createURIFromString(eDataType, initialValue);
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
    case SetupPackage.TRIGGER_SET:
      return convertTriggerSetToString(eDataType, instanceValue);
    case SetupPackage.EXCEPTION:
      return convertExceptionToString(eDataType, instanceValue);
    case SetupPackage.URI:
      return convertURIToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
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
  public CompoundSetupTask createCompoundSetupTask()
  {
    CompoundSetupTaskImpl compoundSetupTask = new CompoundSetupTaskImpl();
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
   * @generated
   */
  public URI createURIFromString(EDataType eDataType, String initialValue)
  {
    return (URI)super.createFromString(eDataType, initialValue);
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
