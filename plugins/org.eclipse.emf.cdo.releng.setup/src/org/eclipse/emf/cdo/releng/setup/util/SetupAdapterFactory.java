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
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.setup.ApiBaselineTask;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.BuildPlan;
import org.eclipse.emf.cdo.releng.setup.CommandParameter;
import org.eclipse.emf.cdo.releng.setup.Component;
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
import org.eclipse.emf.cdo.releng.setup.ResourceCopyTask;
import org.eclipse.emf.cdo.releng.setup.ResourceCreationTask;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.SourceLocator;
import org.eclipse.emf.cdo.releng.setup.TargetPlatformTask;
import org.eclipse.emf.cdo.releng.setup.TextModification;
import org.eclipse.emf.cdo.releng.setup.TextModifyTask;
import org.eclipse.emf.cdo.releng.setup.VariableChoice;
import org.eclipse.emf.cdo.releng.setup.WorkingSetTask;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage
 * @generated
 */
@SuppressWarnings("deprecation")
public class SetupAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static SetupPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = SetupPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SetupSwitch<Adapter> modelSwitch = new SetupSwitch<Adapter>()
  {
    @Override
    public Adapter caseMetaIndex(MetaIndex object)
    {
      return createMetaIndexAdapter();
    }

    @Override
    public Adapter caseIndex(Index object)
    {
      return createIndexAdapter();
    }

    @Override
    public Adapter caseEclipse(Eclipse object)
    {
      return createEclipseAdapter();
    }

    @Override
    public Adapter caseConfiguration(Configuration object)
    {
      return createConfigurationAdapter();
    }

    @Override
    public Adapter caseConfigurableItem(ConfigurableItem object)
    {
      return createConfigurableItemAdapter();
    }

    @Override
    public Adapter caseProject(Project object)
    {
      return createProjectAdapter();
    }

    @Override
    public Adapter caseBranch(Branch object)
    {
      return createBranchAdapter();
    }

    @Override
    public Adapter casePreferences(Preferences object)
    {
      return createPreferencesAdapter();
    }

    @Override
    public Adapter caseSetup(Setup object)
    {
      return createSetupAdapter();
    }

    @Override
    public Adapter caseSetupTask(SetupTask object)
    {
      return createSetupTaskAdapter();
    }

    @Override
    public Adapter caseSetupTaskContainer(SetupTaskContainer object)
    {
      return createSetupTaskContainerAdapter();
    }

    @Override
    public Adapter caseScopeRoot(ScopeRoot object)
    {
      return createScopeRootAdapter();
    }

    @Override
    public Adapter caseCompoundSetupTask(CompoundSetupTask object)
    {
      return createCompoundSetupTaskAdapter();
    }

    @Override
    public Adapter caseContextVariableTask(ContextVariableTask object)
    {
      return createContextVariableTaskAdapter();
    }

    @Override
    public Adapter caseVariableChoice(VariableChoice object)
    {
      return createVariableChoiceAdapter();
    }

    @Override
    public Adapter caseEclipseIniTask(EclipseIniTask object)
    {
      return createEclipseIniTaskAdapter();
    }

    @Override
    public Adapter caseLinkLocationTask(LinkLocationTask object)
    {
      return createLinkLocationTaskAdapter();
    }

    @Override
    public Adapter caseP2Task(P2Task object)
    {
      return createP2TaskAdapter();
    }

    @Override
    public Adapter caseInstallableUnit(InstallableUnit object)
    {
      return createInstallableUnitAdapter();
    }

    @Override
    public Adapter caseP2Repository(P2Repository object)
    {
      return createP2RepositoryAdapter();
    }

    @Override
    public Adapter caseBasicMaterializationTask(BasicMaterializationTask object)
    {
      return createBasicMaterializationTaskAdapter();
    }

    @Override
    public Adapter caseBuckminsterImportTask(BuckminsterImportTask object)
    {
      return createBuckminsterImportTaskAdapter();
    }

    @Override
    public Adapter caseMaterializationTask(MaterializationTask object)
    {
      return createMaterializationTaskAdapter();
    }

    @Override
    public Adapter caseComponent(Component object)
    {
      return createComponentAdapter();
    }

    @Override
    public Adapter caseSourceLocator(SourceLocator object)
    {
      return createSourceLocatorAdapter();
    }

    @Override
    public Adapter caseManualSourceLocator(ManualSourceLocator object)
    {
      return createManualSourceLocatorAdapter();
    }

    @Override
    public Adapter caseAutomaticSourceLocator(AutomaticSourceLocator object)
    {
      return createAutomaticSourceLocatorAdapter();
    }

    @Override
    public Adapter caseRedirectionTask(RedirectionTask object)
    {
      return createRedirectionTaskAdapter();
    }

    @Override
    public Adapter caseApiBaselineTask(ApiBaselineTask object)
    {
      return createApiBaselineTaskAdapter();
    }

    @Override
    public Adapter caseGitCloneTask(GitCloneTask object)
    {
      return createGitCloneTaskAdapter();
    }

    @Override
    public Adapter caseProjectSetImportTask(ProjectSetImportTask object)
    {
      return createProjectSetImportTaskAdapter();
    }

    @Override
    public Adapter caseTargetPlatformTask(TargetPlatformTask object)
    {
      return createTargetPlatformTaskAdapter();
    }

    @Override
    public Adapter caseEclipsePreferenceTask(EclipsePreferenceTask object)
    {
      return createEclipsePreferenceTaskAdapter();
    }

    @Override
    public Adapter caseFileAssociationTask(FileAssociationTask object)
    {
      return createFileAssociationTaskAdapter();
    }

    @Override
    public Adapter caseFileEditor(FileEditor object)
    {
      return createFileEditorAdapter();
    }

    @Override
    public Adapter caseWorkingSetTask(WorkingSetTask object)
    {
      return createWorkingSetTaskAdapter();
    }

    @Override
    public Adapter caseResourceCopyTask(ResourceCopyTask object)
    {
      return createResourceCopyTaskAdapter();
    }

    @Override
    public Adapter caseResourceCreationTask(ResourceCreationTask object)
    {
      return createResourceCreationTaskAdapter();
    }

    @Override
    public Adapter caseTextModifyTask(TextModifyTask object)
    {
      return createTextModifyTaskAdapter();
    }

    @Override
    public Adapter caseTextModification(TextModification object)
    {
      return createTextModificationAdapter();
    }

    @Override
    public Adapter caseKeyBindingTask(KeyBindingTask object)
    {
      return createKeyBindingTaskAdapter();
    }

    @Override
    public Adapter caseKeyBindingContext(KeyBindingContext object)
    {
      return createKeyBindingContextAdapter();
    }

    @Override
    public Adapter caseCommandParameter(CommandParameter object)
    {
      return createCommandParameterAdapter();
    }

    @Override
    public Adapter caseMylynQueryTask(MylynQueryTask object)
    {
      return createMylynQueryTaskAdapter();
    }

    @Override
    public Adapter caseMylynQueriesTask(MylynQueriesTask object)
    {
      return createMylynQueriesTaskAdapter();
    }

    @Override
    public Adapter caseQuery(Query object)
    {
      return createQueryAdapter();
    }

    @Override
    public Adapter caseQueryAttribute(Map.Entry<String, String> object)
    {
      return createQueryAttributeAdapter();
    }

    @Override
    public Adapter caseMylynBuildsTask(MylynBuildsTask object)
    {
      return createMylynBuildsTaskAdapter();
    }

    @Override
    public Adapter caseBuildPlan(BuildPlan object)
    {
      return createBuildPlanAdapter();
    }

    @Override
    public Adapter caseJRETask(JRETask object)
    {
      return createJRETaskAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.MetaIndex <em>Meta Index</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.MetaIndex
   * @generated
   */
  public Adapter createMetaIndexAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Index <em>Index</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Index
   * @generated
   */
  public Adapter createIndexAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Eclipse <em>Eclipse</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Eclipse
   * @generated
   */
  public Adapter createEclipseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Configuration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Configuration
   * @generated
   */
  public Adapter createConfigurationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Project <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Project
   * @generated
   */
  public Adapter createProjectAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Branch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Branch
   * @generated
   */
  public Adapter createBranchAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask <em>Api Baseline Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ApiBaselineTask
   * @generated
   */
  public Adapter createApiBaselineTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.GitCloneTask <em>Git Clone Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.GitCloneTask
   * @generated
   */
  public Adapter createGitCloneTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask <em>Project Set Import Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask
   * @generated
   */
  public Adapter createProjectSetImportTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.P2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.P2Task
   * @generated
   */
  public Adapter createP2TaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit <em>Installable Unit</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit
   * @generated
   */
  public Adapter createInstallableUnitAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.P2Repository <em>P2 Repository</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository
   * @generated
   */
  public Adapter createP2RepositoryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Setup <em>Setup</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Setup
   * @generated
   */
  public Adapter createSetupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.SetupTask <em>Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTask
   * @generated
   */
  public Adapter createSetupTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask <em>Working Set Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.WorkingSetTask
   * @generated
   */
  public Adapter createWorkingSetTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ResourceCopyTask <em>Resource Copy Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCopyTask
   * @generated
   */
  public Adapter createResourceCopyTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.TextModifyTask <em>Text Modify Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.TextModifyTask
   * @generated
   */
  public Adapter createTextModifyTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.TextModification <em>Text Modification</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.TextModification
   * @generated
   */
  public Adapter createTextModificationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask <em>Key Binding Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingTask
   * @generated
   */
  public Adapter createKeyBindingTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingContext <em>Key Binding Context</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.KeyBindingContext
   * @generated
   */
  public Adapter createKeyBindingContextAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.CommandParameter <em>Command Parameter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.CommandParameter
   * @generated
   */
  public Adapter createCommandParameterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.MylynQueryTask <em>Mylyn Query Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueryTask
   * @generated
   */
  public Adapter createMylynQueryTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.MylynQueriesTask <em>Mylyn Queries Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask
   * @generated
   */
  public Adapter createMylynQueriesTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Query <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Query
   * @generated
   */
  public Adapter createQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Query Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see java.util.Map.Entry
   * @generated
   */
  public Adapter createQueryAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.MylynBuildsTask <em>Mylyn Builds Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.MylynBuildsTask
   * @generated
   */
  public Adapter createMylynBuildsTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.BuildPlan <em>Build Plan</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.BuildPlan
   * @generated
   */
  public Adapter createBuildPlanAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.JRETask <em>JRE Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.JRETask
   * @generated
   */
  public Adapter createJRETaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.FileAssociationTask <em>File Association Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.FileAssociationTask
   * @generated
   */
  public Adapter createFileAssociationTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.FileEditor <em>File Editor</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.FileEditor
   * @generated
   */
  public Adapter createFileEditorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.TargetPlatformTask <em>Target Platform Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.TargetPlatformTask
   * @generated
   */
  public Adapter createTargetPlatformTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator <em>Automatic Source Locator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator
   * @generated
   */
  public Adapter createAutomaticSourceLocatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.RedirectionTask <em>Redirection Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.RedirectionTask
   * @generated
   */
  public Adapter createRedirectionTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator <em>Manual Source Locator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ManualSourceLocator
   * @generated
   */
  public Adapter createManualSourceLocatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask <em>Context Variable Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ContextVariableTask
   * @generated
   */
  public Adapter createContextVariableTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.VariableChoice <em>Variable Choice</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.VariableChoice
   * @generated
   */
  public Adapter createVariableChoiceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask <em>Resource Creation Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ResourceCreationTask
   * @generated
   */
  public Adapter createResourceCreationTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.MaterializationTask <em>Materialization Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.MaterializationTask
   * @generated
   */
  public Adapter createMaterializationTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.SourceLocator <em>Source Locator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.SourceLocator
   * @generated
   */
  public Adapter createSourceLocatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask <em>Basic Materialization Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.BasicMaterializationTask
   * @generated
   */
  public Adapter createBasicMaterializationTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Component <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Component
   * @generated
   */
  public Adapter createComponentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask <em>Eclipse Ini Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.EclipseIniTask
   * @generated
   */
  public Adapter createEclipseIniTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask <em>Compound Setup Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.CompoundSetupTask
   * @generated
   */
  public Adapter createCompoundSetupTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ConfigurableItem <em>Configurable Item</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ConfigurableItem
   * @generated
   */
  public Adapter createConfigurableItemAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask <em>Buckminster Import Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask
   * @generated
   */
  public Adapter createBuckminsterImportTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.Preferences <em>Preferences</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.Preferences
   * @generated
   */
  public Adapter createPreferencesAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask <em>Link Location Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.LinkLocationTask
   * @generated
   */
  public Adapter createLinkLocationTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.SetupTaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskContainer
   * @generated
   */
  public Adapter createSetupTaskContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.ScopeRoot <em>Scope Root</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.ScopeRoot
   * @generated
   */
  public Adapter createScopeRootAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask <em>Eclipse Preference Task</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask
   * @generated
   */
  public Adapter createEclipsePreferenceTaskAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // SetupAdapterFactory
