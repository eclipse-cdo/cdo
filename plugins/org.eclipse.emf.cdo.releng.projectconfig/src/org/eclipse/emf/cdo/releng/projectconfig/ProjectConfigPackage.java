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
package org.eclipse.emf.cdo.releng.projectconfig;

import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
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
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigFactory
 * @model kind="package"
 * @generated
 */
public interface ProjectConfigPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "projectconfig";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/CDO/releng/projectconfig/1.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "projectconfig";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  ProjectConfigPackage eINSTANCE = org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl <em>Workspace Configuration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getWorkspaceConfiguration()
   * @generated
   */
  int WORKSPACE_CONFIGURATION = 0;

  /**
   * The feature id for the '<em><b>Property Filters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION__PROPERTY_FILTERS = 0;

  /**
   * The feature id for the '<em><b>Projects</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION__PROJECTS = 1;

  /**
   * The feature id for the '<em><b>Default Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE = 2;

  /**
   * The feature id for the '<em><b>Instance Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE = 3;

  /**
   * The number of structural features of the '<em>Workspace Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION_FEATURE_COUNT = 4;

  /**
   * The operation id for the '<em>Apply Preference Profiles</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION___APPLY_PREFERENCE_PROFILES = 0;

  /**
   * The operation id for the '<em>Update Preference Profile References</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION___UPDATE_PREFERENCE_PROFILE_REFERENCES = 1;

  /**
   * The operation id for the '<em>Get Project</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION___GET_PROJECT__STRING = 2;

  /**
   * The operation id for the '<em>Is Omitted</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION___IS_OMITTED__PROPERTY = 3;

  /**
   * The number of operations of the '<em>Workspace Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int WORKSPACE_CONFIGURATION_OPERATION_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectImpl <em>Project</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getProject()
   * @generated
   */
  int PROJECT = 1;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__CONFIGURATION = 0;

  /**
   * The feature id for the '<em><b>Preference Profiles</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__PREFERENCE_PROFILES = 1;

  /**
   * The feature id for the '<em><b>Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__PREFERENCE_NODE = 2;

  /**
   * The feature id for the '<em><b>Preference Profile References</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT__PREFERENCE_PROFILE_REFERENCES = 3;

  /**
   * The number of structural features of the '<em>Project</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_FEATURE_COUNT = 4;

  /**
   * The number of operations of the '<em>Project</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROJECT_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl <em>Preference Profile</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPreferenceProfile()
   * @generated
   */
  int PREFERENCE_PROFILE = 2;

  /**
   * The feature id for the '<em><b>Preference Filters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE__PREFERENCE_FILTERS = 0;

  /**
   * The feature id for the '<em><b>Referent Projects</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE__REFERENT_PROJECTS = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE__NAME = 2;

  /**
   * The feature id for the '<em><b>Project</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE__PROJECT = 3;

  /**
   * The feature id for the '<em><b>Predicates</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE__PREDICATES = 4;

  /**
   * The feature id for the '<em><b>Prerequisites</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE__PREREQUISITES = 5;

  /**
   * The number of structural features of the '<em>Preference Profile</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE_FEATURE_COUNT = 6;

  /**
   * The operation id for the '<em>Requires</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE___REQUIRES__PREFERENCEPROFILE = 0;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE___MATCHES__IPROJECT = 1;

  /**
   * The number of operations of the '<em>Preference Profile</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_PROFILE_OPERATION_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceFilterImpl <em>Preference Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceFilterImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPreferenceFilter()
   * @generated
   */
  int PREFERENCE_FILTER = 3;

  /**
   * The feature id for the '<em><b>Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER__PREFERENCE_NODE = 0;

  /**
   * The feature id for the '<em><b>Preference Profile</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER__PREFERENCE_PROFILE = 1;

  /**
   * The feature id for the '<em><b>Inclusions</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER__INCLUSIONS = 2;

  /**
   * The feature id for the '<em><b>Exclusions</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER__EXCLUSIONS = 3;

  /**
   * The feature id for the '<em><b>Properties</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER__PROPERTIES = 4;

  /**
   * The number of structural features of the '<em>Preference Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER_FEATURE_COUNT = 5;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER___MATCHES__STRING = 0;

  /**
   * The number of operations of the '<em>Preference Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERENCE_FILTER_OPERATION_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl <em>Property Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPropertyFilter()
   * @generated
   */
  int PROPERTY_FILTER = 4;

  /**
   * The feature id for the '<em><b>Omissions</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER__OMISSIONS = 0;

  /**
   * The feature id for the '<em><b>Predicates</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER__PREDICATES = 1;

  /**
   * The feature id for the '<em><b>Properties</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER__PROPERTIES = 2;

  /**
   * The feature id for the '<em><b>Configuration</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER__CONFIGURATION = 3;

  /**
   * The number of structural features of the '<em>Property Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER_FEATURE_COUNT = 4;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER___MATCHES__STRING = 0;

  /**
   * The number of operations of the '<em>Property Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_FILTER_OPERATION_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.InclusionPredicateImpl <em>Inclusion Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.InclusionPredicateImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getInclusionPredicate()
   * @generated
   */
  int INCLUSION_PREDICATE = 5;

  /**
   * The feature id for the '<em><b>Included Preference Profiles</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES = PredicatesPackage.PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Inclusion Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INCLUSION_PREDICATE_FEATURE_COUNT = PredicatesPackage.PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INCLUSION_PREDICATE___MATCHES__IPROJECT = PredicatesPackage.PREDICATE___MATCHES__IPROJECT;

  /**
   * The number of operations of the '<em>Inclusion Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INCLUSION_PREDICATE_OPERATION_COUNT = PredicatesPackage.PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.ExclusionPredicateImpl <em>Exclusion Predicate</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ExclusionPredicateImpl
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getExclusionPredicate()
   * @generated
   */
  int EXCLUSION_PREDICATE = 6;

  /**
   * The feature id for the '<em><b>Excluded Preference Profiles</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES = PredicatesPackage.PREDICATE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Exclusion Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_PREDICATE_FEATURE_COUNT = PredicatesPackage.PREDICATE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Matches</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_PREDICATE___MATCHES__IPROJECT = PredicatesPackage.PREDICATE___MATCHES__IPROJECT;

  /**
   * The number of operations of the '<em>Exclusion Predicate</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_PREDICATE_OPERATION_COUNT = PredicatesPackage.PREDICATE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>Pattern</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.util.regex.Pattern
   * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPattern()
   * @generated
   */
  int PATTERN = 7;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration <em>Workspace Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Workspace Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration
   * @generated
   */
  EClass getWorkspaceConfiguration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getPropertyFilters <em>Property Filters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Property Filters</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getPropertyFilters()
   * @see #getWorkspaceConfiguration()
   * @generated
   */
  EReference getWorkspaceConfiguration_PropertyFilters();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getProjects <em>Projects</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Projects</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getProjects()
   * @see #getWorkspaceConfiguration()
   * @generated
   */
  EReference getWorkspaceConfiguration_Projects();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getDefaultPreferenceNode <em>Default Preference Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Default Preference Node</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getDefaultPreferenceNode()
   * @see #getWorkspaceConfiguration()
   * @generated
   */
  EReference getWorkspaceConfiguration_DefaultPreferenceNode();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getInstancePreferenceNode <em>Instance Preference Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Instance Preference Node</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getInstancePreferenceNode()
   * @see #getWorkspaceConfiguration()
   * @generated
   */
  EReference getWorkspaceConfiguration_InstancePreferenceNode();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#applyPreferenceProfiles() <em>Apply Preference Profiles</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Apply Preference Profiles</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#applyPreferenceProfiles()
   * @generated
   */
  EOperation getWorkspaceConfiguration__ApplyPreferenceProfiles();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#updatePreferenceProfileReferences() <em>Update Preference Profile References</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Update Preference Profile References</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#updatePreferenceProfileReferences()
   * @generated
   */
  EOperation getWorkspaceConfiguration__UpdatePreferenceProfileReferences();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getProject(java.lang.String) <em>Get Project</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Project</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#getProject(java.lang.String)
   * @generated
   */
  EOperation getWorkspaceConfiguration__GetProject__String();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#isOmitted(org.eclipse.emf.cdo.releng.preferences.Property) <em>Is Omitted</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Is Omitted</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration#isOmitted(org.eclipse.emf.cdo.releng.preferences.Property)
   * @generated
   */
  EOperation getWorkspaceConfiguration__IsOmitted__Property();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.Project <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.Project
   * @generated
   */
  EClass getProject();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.projectconfig.Project#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.Project#getConfiguration()
   * @see #getProject()
   * @generated
   */
  EReference getProject_Configuration();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.Project#getPreferenceProfiles <em>Preference Profiles</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Preference Profiles</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.Project#getPreferenceProfiles()
   * @see #getProject()
   * @generated
   */
  EReference getProject_PreferenceProfiles();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.projectconfig.Project#getPreferenceNode <em>Preference Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Preference Node</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.Project#getPreferenceNode()
   * @see #getProject()
   * @generated
   */
  EReference getProject_PreferenceNode();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.Project#getPreferenceProfileReferences <em>Preference Profile References</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Preference Profile References</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.Project#getPreferenceProfileReferences()
   * @see #getProject()
   * @generated
   */
  EReference getProject_PreferenceProfileReferences();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile <em>Preference Profile</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Preference Profile</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile
   * @generated
   */
  EClass getPreferenceProfile();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPreferenceFilters <em>Preference Filters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Preference Filters</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPreferenceFilters()
   * @see #getPreferenceProfile()
   * @generated
   */
  EReference getPreferenceProfile_PreferenceFilters();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getReferentProjects <em>Referent Projects</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Referent Projects</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getReferentProjects()
   * @see #getPreferenceProfile()
   * @generated
   */
  EReference getPreferenceProfile_ReferentProjects();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getName()
   * @see #getPreferenceProfile()
   * @generated
   */
  EAttribute getPreferenceProfile_Name();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getProject <em>Project</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Project</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getProject()
   * @see #getPreferenceProfile()
   * @generated
   */
  EReference getPreferenceProfile_Project();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPredicates <em>Predicates</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Predicates</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPredicates()
   * @see #getPreferenceProfile()
   * @generated
   */
  EReference getPreferenceProfile_Predicates();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPrerequisites <em>Prerequisites</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Prerequisites</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPrerequisites()
   * @see #getPreferenceProfile()
   * @generated
   */
  EReference getPreferenceProfile_Prerequisites();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#requires(org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile) <em>Requires</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Requires</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#requires(org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile)
   * @generated
   */
  EOperation getPreferenceProfile__Requires__PreferenceProfile();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#matches(org.eclipse.core.resources.IProject) <em>Matches</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Matches</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#matches(org.eclipse.core.resources.IProject)
   * @generated
   */
  EOperation getPreferenceProfile__Matches__IProject();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter <em>Preference Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Preference Filter</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter
   * @generated
   */
  EClass getPreferenceFilter();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceNode <em>Preference Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Preference Node</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceNode()
   * @see #getPreferenceFilter()
   * @generated
   */
  EReference getPreferenceFilter_PreferenceNode();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceProfile <em>Preference Profile</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Preference Profile</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceProfile()
   * @see #getPreferenceFilter()
   * @generated
   */
  EReference getPreferenceFilter_PreferenceProfile();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getInclusions <em>Inclusions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Inclusions</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getInclusions()
   * @see #getPreferenceFilter()
   * @generated
   */
  EAttribute getPreferenceFilter_Inclusions();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getExclusions <em>Exclusions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exclusions</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getExclusions()
   * @see #getPreferenceFilter()
   * @generated
   */
  EAttribute getPreferenceFilter_Exclusions();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getProperties <em>Properties</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Properties</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getProperties()
   * @see #getPreferenceFilter()
   * @generated
   */
  EReference getPreferenceFilter_Properties();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#matches(java.lang.String) <em>Matches</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Matches</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#matches(java.lang.String)
   * @generated
   */
  EOperation getPreferenceFilter__Matches__String();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter <em>Property Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Property Filter</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter
   * @generated
   */
  EClass getPropertyFilter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getOmissions <em>Omissions</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Omissions</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getOmissions()
   * @see #getPropertyFilter()
   * @generated
   */
  EAttribute getPropertyFilter_Omissions();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getPredicates <em>Predicates</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Predicates</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getPredicates()
   * @see #getPropertyFilter()
   * @generated
   */
  EReference getPropertyFilter_Predicates();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getProperties <em>Properties</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Properties</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getProperties()
   * @see #getPropertyFilter()
   * @generated
   */
  EReference getPropertyFilter_Properties();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getConfiguration <em>Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configuration</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getConfiguration()
   * @see #getPropertyFilter()
   * @generated
   */
  EReference getPropertyFilter_Configuration();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#matches(java.lang.String) <em>Matches</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Matches</em>' operation.
   * @see org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#matches(java.lang.String)
   * @generated
   */
  EOperation getPropertyFilter__Matches__String();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.InclusionPredicate <em>Inclusion Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Inclusion Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.InclusionPredicate
   * @generated
   */
  EClass getInclusionPredicate();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.InclusionPredicate#getIncludedPreferenceProfiles <em>Included Preference Profiles</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Included Preference Profiles</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.InclusionPredicate#getIncludedPreferenceProfiles()
   * @see #getInclusionPredicate()
   * @generated
   */
  EReference getInclusionPredicate_IncludedPreferenceProfiles();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.releng.projectconfig.ExclusionPredicate <em>Exclusion Predicate</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Exclusion Predicate</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.ExclusionPredicate
   * @generated
   */
  EClass getExclusionPredicate();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.releng.projectconfig.ExclusionPredicate#getExcludedPreferenceProfiles <em>Excluded Preference Profiles</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Excluded Preference Profiles</em>'.
   * @see org.eclipse.emf.cdo.releng.projectconfig.ExclusionPredicate#getExcludedPreferenceProfiles()
   * @see #getExclusionPredicate()
   * @generated
   */
  EReference getExclusionPredicate_ExcludedPreferenceProfiles();

  /**
   * Returns the meta object for data type '{@link java.util.regex.Pattern <em>Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Pattern</em>'.
   * @see java.util.regex.Pattern
   * @model instanceClass="java.util.regex.Pattern"
   * @generated
   */
  EDataType getPattern();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  ProjectConfigFactory getProjectConfigFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl <em>Workspace Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getWorkspaceConfiguration()
     * @generated
     */
    EClass WORKSPACE_CONFIGURATION = eINSTANCE.getWorkspaceConfiguration();

    /**
     * The meta object literal for the '<em><b>Property Filters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKSPACE_CONFIGURATION__PROPERTY_FILTERS = eINSTANCE.getWorkspaceConfiguration_PropertyFilters();

    /**
     * The meta object literal for the '<em><b>Projects</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKSPACE_CONFIGURATION__PROJECTS = eINSTANCE.getWorkspaceConfiguration_Projects();

    /**
     * The meta object literal for the '<em><b>Default Preference Node</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE = eINSTANCE
        .getWorkspaceConfiguration_DefaultPreferenceNode();

    /**
     * The meta object literal for the '<em><b>Instance Preference Node</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE = eINSTANCE
        .getWorkspaceConfiguration_InstancePreferenceNode();

    /**
     * The meta object literal for the '<em><b>Apply Preference Profiles</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation WORKSPACE_CONFIGURATION___APPLY_PREFERENCE_PROFILES = eINSTANCE
        .getWorkspaceConfiguration__ApplyPreferenceProfiles();

    /**
     * The meta object literal for the '<em><b>Update Preference Profile References</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation WORKSPACE_CONFIGURATION___UPDATE_PREFERENCE_PROFILE_REFERENCES = eINSTANCE
        .getWorkspaceConfiguration__UpdatePreferenceProfileReferences();

    /**
     * The meta object literal for the '<em><b>Get Project</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation WORKSPACE_CONFIGURATION___GET_PROJECT__STRING = eINSTANCE
        .getWorkspaceConfiguration__GetProject__String();

    /**
     * The meta object literal for the '<em><b>Is Omitted</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation WORKSPACE_CONFIGURATION___IS_OMITTED__PROPERTY = eINSTANCE
        .getWorkspaceConfiguration__IsOmitted__Property();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectImpl <em>Project</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getProject()
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
     * The meta object literal for the '<em><b>Preference Profiles</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__PREFERENCE_PROFILES = eINSTANCE.getProject_PreferenceProfiles();

    /**
     * The meta object literal for the '<em><b>Preference Node</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__PREFERENCE_NODE = eINSTANCE.getProject_PreferenceNode();

    /**
     * The meta object literal for the '<em><b>Preference Profile References</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROJECT__PREFERENCE_PROFILE_REFERENCES = eINSTANCE.getProject_PreferenceProfileReferences();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl <em>Preference Profile</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPreferenceProfile()
     * @generated
     */
    EClass PREFERENCE_PROFILE = eINSTANCE.getPreferenceProfile();

    /**
     * The meta object literal for the '<em><b>Preference Filters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_PROFILE__PREFERENCE_FILTERS = eINSTANCE.getPreferenceProfile_PreferenceFilters();

    /**
     * The meta object literal for the '<em><b>Referent Projects</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_PROFILE__REFERENT_PROJECTS = eINSTANCE.getPreferenceProfile_ReferentProjects();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCE_PROFILE__NAME = eINSTANCE.getPreferenceProfile_Name();

    /**
     * The meta object literal for the '<em><b>Project</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_PROFILE__PROJECT = eINSTANCE.getPreferenceProfile_Project();

    /**
     * The meta object literal for the '<em><b>Predicates</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_PROFILE__PREDICATES = eINSTANCE.getPreferenceProfile_Predicates();

    /**
     * The meta object literal for the '<em><b>Prerequisites</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_PROFILE__PREREQUISITES = eINSTANCE.getPreferenceProfile_Prerequisites();

    /**
     * The meta object literal for the '<em><b>Requires</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREFERENCE_PROFILE___REQUIRES__PREFERENCEPROFILE = eINSTANCE
        .getPreferenceProfile__Requires__PreferenceProfile();

    /**
     * The meta object literal for the '<em><b>Matches</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREFERENCE_PROFILE___MATCHES__IPROJECT = eINSTANCE.getPreferenceProfile__Matches__IProject();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceFilterImpl <em>Preference Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceFilterImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPreferenceFilter()
     * @generated
     */
    EClass PREFERENCE_FILTER = eINSTANCE.getPreferenceFilter();

    /**
     * The meta object literal for the '<em><b>Preference Node</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_FILTER__PREFERENCE_NODE = eINSTANCE.getPreferenceFilter_PreferenceNode();

    /**
     * The meta object literal for the '<em><b>Preference Profile</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_FILTER__PREFERENCE_PROFILE = eINSTANCE.getPreferenceFilter_PreferenceProfile();

    /**
     * The meta object literal for the '<em><b>Inclusions</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCE_FILTER__INCLUSIONS = eINSTANCE.getPreferenceFilter_Inclusions();

    /**
     * The meta object literal for the '<em><b>Exclusions</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PREFERENCE_FILTER__EXCLUSIONS = eINSTANCE.getPreferenceFilter_Exclusions();

    /**
     * The meta object literal for the '<em><b>Properties</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERENCE_FILTER__PROPERTIES = eINSTANCE.getPreferenceFilter_Properties();

    /**
     * The meta object literal for the '<em><b>Matches</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PREFERENCE_FILTER___MATCHES__STRING = eINSTANCE.getPreferenceFilter__Matches__String();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl <em>Property Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPropertyFilter()
     * @generated
     */
    EClass PROPERTY_FILTER = eINSTANCE.getPropertyFilter();

    /**
     * The meta object literal for the '<em><b>Omissions</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTY_FILTER__OMISSIONS = eINSTANCE.getPropertyFilter_Omissions();

    /**
     * The meta object literal for the '<em><b>Predicates</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTY_FILTER__PREDICATES = eINSTANCE.getPropertyFilter_Predicates();

    /**
     * The meta object literal for the '<em><b>Properties</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTY_FILTER__PROPERTIES = eINSTANCE.getPropertyFilter_Properties();

    /**
     * The meta object literal for the '<em><b>Configuration</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTY_FILTER__CONFIGURATION = eINSTANCE.getPropertyFilter_Configuration();

    /**
     * The meta object literal for the '<em><b>Matches</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PROPERTY_FILTER___MATCHES__STRING = eINSTANCE.getPropertyFilter__Matches__String();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.InclusionPredicateImpl <em>Inclusion Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.InclusionPredicateImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getInclusionPredicate()
     * @generated
     */
    EClass INCLUSION_PREDICATE = eINSTANCE.getInclusionPredicate();

    /**
     * The meta object literal for the '<em><b>Included Preference Profiles</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES = eINSTANCE
        .getInclusionPredicate_IncludedPreferenceProfiles();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.releng.projectconfig.impl.ExclusionPredicateImpl <em>Exclusion Predicate</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ExclusionPredicateImpl
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getExclusionPredicate()
     * @generated
     */
    EClass EXCLUSION_PREDICATE = eINSTANCE.getExclusionPredicate();

    /**
     * The meta object literal for the '<em><b>Excluded Preference Profiles</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES = eINSTANCE
        .getExclusionPredicate_ExcludedPreferenceProfiles();

    /**
     * The meta object literal for the '<em>Pattern</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.regex.Pattern
     * @see org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPackageImpl#getPattern()
     * @generated
     */
    EDataType PATTERN = eINSTANCE.getPattern();

  }

} // ProjectConfigPackage
