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

import java.util.Set;

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
   * Returns a new object of class '<em>Eclipse</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Eclipse</em>'.
   * @generated
   */
  Eclipse createEclipse();

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
   * Returns a new object of class '<em>Api Baseline Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Api Baseline Task</em>'.
   * @generated
   */
  ApiBaselineTask createApiBaselineTask();

  /**
   * Returns a new object of class '<em>Git Clone Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Git Clone Task</em>'.
   * @generated
   */
  GitCloneTask createGitCloneTask();

  /**
   * Returns a new object of class '<em>Project Set Import Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Project Set Import Task</em>'.
   * @generated
   */
  ProjectSetImportTask createProjectSetImportTask();

  /**
   * Returns a new object of class '<em>P2 Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>P2 Task</em>'.
   * @generated
   */
  P2Task createP2Task();

  P2Task createP2Task(String[] ius, String[] repositories);

  P2Task createP2Task(String[] ius, String[] repositories, Set<String> existingIUs);

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
   * Returns a new object of class '<em>Setup</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Setup</em>'.
   * @generated
   */
  Setup createSetup();

  /**
   * Returns a new object of class '<em>Working Set Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Working Set Task</em>'.
   * @generated
   */
  WorkingSetTask createWorkingSetTask();

  /**
   * Returns a new object of class '<em>Resource Copy Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Resource Copy Task</em>'.
   * @generated
   */
  ResourceCopyTask createResourceCopyTask();

  /**
   * Returns a new object of class '<em>Text Modify Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Text Modify Task</em>'.
   * @generated
   */
  TextModifyTask createTextModifyTask();

  /**
   * Returns a new object of class '<em>Text Modification</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Text Modification</em>'.
   * @generated
   */
  TextModification createTextModification();

  /**
   * Returns a new object of class '<em>Key Binding Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Key Binding Task</em>'.
   * @generated
   */
  KeyBindingTask createKeyBindingTask();

  /**
   * Returns a new object of class '<em>Key Binding Context</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Key Binding Context</em>'.
   * @generated
   */
  KeyBindingContext createKeyBindingContext();

  /**
   * Returns a new object of class '<em>Command Parameter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Command Parameter</em>'.
   * @generated
   */
  CommandParameter createCommandParameter();

  /**
   * Returns a new object of class '<em>Mylyn Query Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Mylyn Query Task</em>'.
   * @generated
   */
  @SuppressWarnings("deprecation")
  MylynQueryTask createMylynQueryTask();

  /**
   * Returns a new object of class '<em>Mylyn Queries Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Mylyn Queries Task</em>'.
   * @generated
   */
  MylynQueriesTask createMylynQueriesTask();

  /**
   * Returns a new object of class '<em>Query</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query</em>'.
   * @generated
   */
  Query createQuery();

  /**
   * Returns a new object of class '<em>JRE Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>JRE Task</em>'.
   * @generated
   */
  JRETask createJRETask();

  /**
   * Returns a new object of class '<em>File Association Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>File Association Task</em>'.
   * @generated
   */
  FileAssociationTask createFileAssociationTask();

  /**
   * Returns a new object of class '<em>File Editor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>File Editor</em>'.
   * @generated
   */
  FileEditor createFileEditor();

  /**
   * Returns a new object of class '<em>Target Platform Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Target Platform Task</em>'.
   * @generated
   */
  TargetPlatformTask createTargetPlatformTask();

  /**
   * Returns a new object of class '<em>Automatic Source Locator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Automatic Source Locator</em>'.
   * @generated
   */
  AutomaticSourceLocator createAutomaticSourceLocator();

  /**
   * Returns a new object of class '<em>Redirection Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Redirection Task</em>'.
   * @generated
   */
  RedirectionTask createRedirectionTask();

  /**
   * Returns a new object of class '<em>Manual Source Locator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Manual Source Locator</em>'.
   * @generated
   */
  ManualSourceLocator createManualSourceLocator();

  /**
   * Returns a new object of class '<em>Context Variable Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Context Variable Task</em>'.
   * @generated
   */
  ContextVariableTask createContextVariableTask();

  /**
   * Returns a new object of class '<em>Variable Choice</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Variable Choice</em>'.
   * @generated
   */
  VariableChoice createVariableChoice();

  /**
   * Returns a new object of class '<em>Resource Creation Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Resource Creation Task</em>'.
   * @generated
   */
  ResourceCreationTask createResourceCreationTask();

  /**
   * Returns a new object of class '<em>Materialization Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Materialization Task</em>'.
   * @generated
   */
  MaterializationTask createMaterializationTask();

  /**
   * Returns a new object of class '<em>Component</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Component</em>'.
   * @generated
   */
  Component createComponent();

  /**
   * Returns a new object of class '<em>Eclipse Ini Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Eclipse Ini Task</em>'.
   * @generated
   */
  EclipseIniTask createEclipseIniTask();

  /**
   * Returns a new object of class '<em>Compound Setup Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Compound Setup Task</em>'.
   * @generated
   */
  CompoundSetupTask createCompoundSetupTask();

  /**
   * Returns a new object of class '<em>Buckminster Import Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Buckminster Import Task</em>'.
   * @generated
   */
  BuckminsterImportTask createBuckminsterImportTask();

  /**
   * Returns a new object of class '<em>Preferences</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Preferences</em>'.
   * @generated
   */
  Preferences createPreferences();

  /**
   * Returns a new object of class '<em>Link Location Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Link Location Task</em>'.
   * @generated
   */
  LinkLocationTask createLinkLocationTask();

  /**
   * Returns a new object of class '<em>Eclipse Preference Task</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Eclipse Preference Task</em>'.
   * @generated
   */
  EclipsePreferenceTask createEclipsePreferenceTask();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  SetupPackage getSetupPackage();

} // SetupFactory
