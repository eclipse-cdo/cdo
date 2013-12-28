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
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;
import org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter;
import org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration;
import org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPlugin;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    case ProjectConfigPackage.PROPERTY_FILTER:
      return validatePropertyFilter((PropertyFilter)value, diagnostics, context);
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

  static Map<PreferenceNode, Set<Property>> collectUnmanagedPreferences(Project project)
  {
    Map<PreferenceNode, Set<Property>> result = new LinkedHashMap<PreferenceNode, Set<Property>>();
    PreferenceNode projectPreferenceNode = project.getPreferenceNode();
    if (projectPreferenceNode != null)
    {
      collectPreferenceNodes(project.getConfiguration(), result, projectPreferenceNode.getChildren());

      result.remove(projectPreferenceNode.getNode(ProjectConfigUtil.PROJECT_CONF_NODE_NAME));

      for (PreferenceProfile preferenceProfile : project.getPreferenceProfiles())
      {
        for (PreferenceFilter preferenceFilter : preferenceProfile.getPreferenceFilters())
        {
          PreferenceNode preferenceNode = preferenceFilter.getPreferenceNode();
          if (preferenceNode != null)
          {
            Set<Property> properties = result.get(preferenceNode);
            if (properties != null)
            {
              for (Iterator<Property> it = properties.iterator(); it.hasNext();)
              {
                if (preferenceFilter.matches(it.next().getName()))
                {
                  it.remove();
                }
              }

              if (properties.isEmpty())
              {
                result.remove(preferenceNode);
              }
            }
          }
        }
      }

      for (PreferenceProfile preferenceProfile : project.getPreferenceProfileReferences())
      {
        PreferenceNode otherProjectPreferenceNode = preferenceProfile.getProject().getPreferenceNode();
        for (PreferenceFilter preferenceFilter : preferenceProfile.getPreferenceFilters())
        {
          final PreferenceNode referencedPreferenceNode = preferenceFilter.getPreferenceNode();
          if (referencedPreferenceNode != null)
          {
            Set<Property> properties = null;
            PreferenceNode preferenceNode = null;

            LOOP: for (Map.Entry<PreferenceNode, Set<Property>> entry : result.entrySet())
            {
              for (PreferenceNode targetPreferenceNode = entry.getKey(), otherPreferenceNode = referencedPreferenceNode; targetPreferenceNode
                  .getName().equals(otherPreferenceNode.getName())
                  && targetPreferenceNode != projectPreferenceNode
                  && otherPreferenceNode != otherProjectPreferenceNode;)
              {
                targetPreferenceNode = targetPreferenceNode.getParent();
                otherPreferenceNode = otherPreferenceNode.getParent();

                if (targetPreferenceNode == projectPreferenceNode && otherPreferenceNode == otherProjectPreferenceNode)
                {
                  preferenceNode = entry.getKey();
                  properties = entry.getValue();
                  break LOOP;
                }
              }
            }

            if (properties != null)
            {
              for (Iterator<Property> it = properties.iterator(); it.hasNext();)
              {
                String name = it.next().getName();
                if (preferenceFilter.matches(name) && referencedPreferenceNode.getProperty(name) != null)
                {
                  it.remove();
                }
              }

              if (properties.isEmpty())
              {
                result.remove(preferenceNode);
              }
            }
          }
        }
      }
    }

    return result;
  }

  private static void collectPreferenceNodes(WorkspaceConfiguration workspaceConfiguration,
      Map<PreferenceNode, Set<Property>> result, List<PreferenceNode> preferenceNodes)
  {
    for (PreferenceNode child : preferenceNodes)
    {
      EList<Property> properties = child.getProperties();
      if (!properties.isEmpty())
      {
        Set<Property> propertySet = new LinkedHashSet<Property>(properties);
        for (Property property : properties)
        {
          for (PropertyFilter propertyFilter : workspaceConfiguration.getPropertyFilters())
          {
            if (propertyFilter.matches(property.getAbsolutePath()))
            {
              propertySet.remove(property);
            }
          }
        }
        if (!propertySet.isEmpty())
        {
          result.put(child, propertySet);
        }
      }

      collectPreferenceNodes(workspaceConfiguration, result, child.getChildren());
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
    Map<PreferenceNode, Set<Property>> unmanagedPreferences = collectUnmanagedPreferences(project);

    if (!unmanagedPreferences.isEmpty())
    {
      if (diagnostics != null)
      {
        PreferenceNode projectPreferenceNode = project.getPreferenceNode();
        StringBuilder substitution = new StringBuilder();
        List<PreferenceNode> preferenceNodes = new ArrayList<PreferenceNode>(unmanagedPreferences.keySet());
        for (int i = 0, size = preferenceNodes.size(); i < size; ++i)
        {
          if (i == size - 1 && size > 1)
          {
            substitution.append(" and ");
          }
          else if (i != 0)
          {
            substitution.append(", ");
          }

          int index = substitution.length();
          int count = 0;
          for (PreferenceNode preferenceNode = preferenceNodes.get(i); preferenceNode != projectPreferenceNode; preferenceNode = preferenceNode
              .getParent())
          {
            if (count++ > 0)
            {
              substitution.insert(index, "/");
            }

            substitution.insert(index, preferenceNode.getName());
          }
        }

        List<Object> data = new ArrayList<Object>();
        data.add(project);
        data.add(ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES);
        data.addAll(preferenceNodes);
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
  public boolean validatePropertyFilter(PropertyFilter propertyFilter, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(propertyFilter, diagnostics, context);
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
