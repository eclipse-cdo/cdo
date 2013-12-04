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
package org.eclipse.emf.cdo.releng.projectconfig.util;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;
import org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration;
import org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPlugin;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage
 * @generated
 */
public class ProjectConfigValidator extends EObjectValidator
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final ProjectConfigValidator INSTANCE = new ProjectConfigValidator();

  /**
   * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.Diagnostic#getSource()
   * @see org.eclipse.emf.common.util.Diagnostic#getCode()
   * @generated
   */
  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.cdo.releng.projectconfig";

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ProjectConfigValidator()
  {
    super();
  }

  /**
   * Returns the package of this validator switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EPackage getEPackage()
  {
    return ProjectConfigPackage.eINSTANCE;
  }

  /**
   * Calls <code>validateXXX</code> for the corresponding classifier of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    switch (classifierID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION:
      return validateWorkspaceConfiguration((WorkspaceConfiguration)value, diagnostics, context);
    case ProjectConfigPackage.PROJECT:
      return validateProject((Project)value, diagnostics, context);
    case ProjectConfigPackage.PREFERENCE_PROFILE:
      return validatePreferenceProfile((PreferenceProfile)value, diagnostics, context);
    case ProjectConfigPackage.PREFERENCE_FILTER:
      return validatePreferenceFilter((PreferenceFilter)value, diagnostics, context);
    case ProjectConfigPackage.PATTERN:
      return validatePattern((Pattern)value, diagnostics, context);
    default:
      return true;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateWorkspaceConfiguration(WorkspaceConfiguration workspaceConfiguration,
      DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(workspaceConfiguration, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateProject(Project project, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(project, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(project, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(project, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateProject_AllPreferencesManaged(project, diagnostics, context);
    }
    return result;
  }

  static Map<String, PreferenceNode> collectUnmanagedPreferences(Project project)
  {
    Map<String, PreferenceNode> result = new LinkedHashMap<String, PreferenceNode>();
    PreferenceNode preferenceNode = project.getPreferenceNode();
    if (preferenceNode != null)
    {
      collectPreferenceNodes(null, result, preferenceNode.getChildren());

      result.remove(ProjectConfigUtil.PROJECT_CONF_NODE_NAME);

      for (PreferenceProfile preferenceProfile : project.getPreferenceProfiles())
      {
        for (PreferenceFilter preferenceFilter : preferenceProfile.getPreferenceFilters())
        {
          PreferenceNode otherPreferenceNode = preferenceFilter.getPreferenceNode();
          if (otherPreferenceNode != null)
          {
            result.remove(otherPreferenceNode.getName());
          }
        }
      }

      for (PreferenceProfile preferenceProfile : project.getPreferenceProfileReferences())
      {
        for (PreferenceFilter preferenceFilter : preferenceProfile.getPreferenceFilters())
        {
          PreferenceNode otherPreferenceNode = preferenceFilter.getPreferenceNode();
          if (otherPreferenceNode != null)
          {
            result.remove(otherPreferenceNode.getName());
          }
        }
      }
    }
    return result;
  }

  private static void collectPreferenceNodes(String qualifiedName, Map<String, PreferenceNode> result,
      List<PreferenceNode> preferenceNodes)
  {
    for (PreferenceNode child : preferenceNodes)
    {
      String name = child.getName();
      String nodeName = qualifiedName == null ? name : qualifiedName + "/" + name;
      if (!child.getProperties().isEmpty())
      {
        result.put(name, child);
      }

      collectPreferenceNodes(nodeName, result, child.getChildren());
    }
  }

  /**
   * Validates the AllPreferencesManaged constraint of '<em>Project</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateProject_AllPreferencesManaged(Project project, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    Map<String, PreferenceNode> unmanagedPreferences = collectUnmanagedPreferences(project);

    if (!unmanagedPreferences.isEmpty())
    {
      if (diagnostics != null)
      {
        String substitution = unmanagedPreferences.keySet().toString();
        substitution = substitution.substring(1, substitution.length() - 1);
        int lastComma = substitution.lastIndexOf(',');
        if (lastComma != -1)
        {
          substitution = substitution.substring(0, lastComma) + " and " + substitution.substring(lastComma + 1);
        }
        List<Object> data = new ArrayList<Object>();
        data.add(project);
        data.add(ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES);
        data.addAll(unmanagedPreferences.values());
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
            "_UI_AllPreferencesManaged_diagnostic", new Object[] { substitution }, data.toArray(), context));
      }
      return false;
    }
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePreferenceProfile(PreferenceProfile preferenceProfile, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(preferenceProfile, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePreferenceFilter(PreferenceFilter preferenceFilter, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(preferenceFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePattern(Pattern pattern, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return ProjectConfigPlugin.INSTANCE;
  }

} // ProjectConfigValidator
