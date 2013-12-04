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
package org.eclipse.emf.cdo.releng.projectconfig.impl;

import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;
import org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workspace Configuration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl#getProjects <em>Projects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl#getDefaultPreferenceNode <em>Default Preference Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.WorkspaceConfigurationImpl#getInstancePreferenceNode <em>Instance Preference Node</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkspaceConfigurationImpl extends MinimalEObjectImpl.Container implements WorkspaceConfiguration
{
  private static final IWorkspaceRoot WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot();

  /**
   * The cached value of the '{@link #getProjects() <em>Projects</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProjects()
   * @generated
   * @ordered
   */
  protected EList<Project> projects;

  /**
   * The cached value of the '{@link #getDefaultPreferenceNode() <em>Default Preference Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultPreferenceNode()
   * @generated
   * @ordered
   */
  protected PreferenceNode defaultPreferenceNode;

  /**
   * The cached value of the '{@link #getInstancePreferenceNode() <em>Instance Preference Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstancePreferenceNode()
   * @generated
   * @ordered
   */
  protected PreferenceNode instancePreferenceNode;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WorkspaceConfigurationImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ProjectConfigPackage.Literals.WORKSPACE_CONFIGURATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Project> getProjects()
  {
    if (projects == null)
    {
      projects = new EObjectContainmentWithInverseEList<Project>(Project.class, this,
          ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS, ProjectConfigPackage.PROJECT__CONFIGURATION);
    }
    return projects;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceNode getDefaultPreferenceNode()
  {
    if (defaultPreferenceNode != null && defaultPreferenceNode.eIsProxy())
    {
      InternalEObject oldDefaultPreferenceNode = (InternalEObject)defaultPreferenceNode;
      defaultPreferenceNode = (PreferenceNode)eResolveProxy(oldDefaultPreferenceNode);
      if (defaultPreferenceNode != oldDefaultPreferenceNode)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              ProjectConfigPackage.WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE, oldDefaultPreferenceNode,
              defaultPreferenceNode));
        }
      }
    }
    return defaultPreferenceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceNode basicGetDefaultPreferenceNode()
  {
    return defaultPreferenceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultPreferenceNode(PreferenceNode newDefaultPreferenceNode)
  {
    PreferenceNode oldDefaultPreferenceNode = defaultPreferenceNode;
    defaultPreferenceNode = newDefaultPreferenceNode;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          ProjectConfigPackage.WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE, oldDefaultPreferenceNode,
          defaultPreferenceNode));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceNode getInstancePreferenceNode()
  {
    if (instancePreferenceNode != null && instancePreferenceNode.eIsProxy())
    {
      InternalEObject oldInstancePreferenceNode = (InternalEObject)instancePreferenceNode;
      instancePreferenceNode = (PreferenceNode)eResolveProxy(oldInstancePreferenceNode);
      if (instancePreferenceNode != oldInstancePreferenceNode)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              ProjectConfigPackage.WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE, oldInstancePreferenceNode,
              instancePreferenceNode));
        }
      }
    }
    return instancePreferenceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceNode basicGetInstancePreferenceNode()
  {
    return instancePreferenceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInstancePreferenceNode(PreferenceNode newInstancePreferenceNode)
  {
    PreferenceNode oldInstancePreferenceNode = instancePreferenceNode;
    instancePreferenceNode = newInstancePreferenceNode;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          ProjectConfigPackage.WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE, oldInstancePreferenceNode,
          instancePreferenceNode));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void applyPreferenceProfiles()
  {
    try
    {
      WORKSPACE_ROOT.getWorkspace().run(new IWorkspaceRunnable()
      {
        public void run(IProgressMonitor monitor) throws CoreException
        {
          Preferences projectsPreferences = null;
          for (Project project : getProjects())
          {
            try
            {
              Preferences projectPreferences = PreferencesUtil.getPreferences(project.getPreferenceNode(), true);
              projectsPreferences = projectPreferences.parent();

              Set<Preferences> clearedPreferences = new HashSet<Preferences>();
              for (PreferenceProfile preferenceProfile : project.getPreferenceProfileReferences())
              {
                if (preferenceProfile.getProject() != project)
                {
                  for (PreferenceFilter preferenceFilter : preferenceProfile.getPreferenceFilters())
                  {
                    PreferenceNode preferenceNode = preferenceFilter.getPreferenceNode();
                    Preferences sourcePreferences = PreferencesUtil.getPreferences(preferenceNode, true);
                    if (projectPreferences == null)
                    {
                      projectPreferences = PreferencesUtil.getPreferences(project.getPreferenceNode(), true);
                    }

                    Preferences targetPreferences = projectPreferences;
                    List<PreferenceNode> path = PreferencesUtil.getPath(preferenceNode);
                    for (int i = 3, size = path.size(); i < size; ++i)
                    {
                      targetPreferences = targetPreferences.node(path.get(i).getName());
                    }

                    if (clearedPreferences.add(targetPreferences))
                    {
                      targetPreferences.clear();
                    }

                    for (String key : sourcePreferences.keys())
                    {
                      if (preferenceFilter.matches(key))
                      {
                        targetPreferences.put(key, sourcePreferences.get(key, null));
                      }
                    }
                  }
                }
              }

              if (!clearedPreferences.isEmpty())
              {
                for (Preferences preferences : clearedPreferences)
                {
                  if (preferences.keys().length == 0)
                  {
                    preferences.removeNode();
                  }
                }
              }
            }
            catch (BackingStoreException ex)
            {
              ProjectConfigPlugin.INSTANCE.log(ex);
            }
          }

          if (projectsPreferences != null)
          {
            try
            {
              projectsPreferences.flush();
            }
            catch (BackingStoreException ex)
            {
              ProjectConfigPlugin.INSTANCE.log(ex);
            }
          }
        }
      }, null);
    }
    catch (CoreException ex)
    {
      ProjectConfigPlugin.INSTANCE.log(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void updatePreferenceProfileReferences()
  {
    boolean sort = false;
    for (Project project : getProjects())
    {
      for (PreferenceProfile preferenceProfile : project.getPreferenceProfiles())
      {
        if (!preferenceProfile.getPrerequisites().isEmpty())
        {
          sort = true;
        }

        EList<Predicate> predicates = preferenceProfile.getPredicates();
        if (!predicates.isEmpty())
        {
          List<Project> referents = new ArrayList<Project>();
          for (Predicate predicate : predicates)
          {
            for (Project referencedProject : getProjects())
            {
              IProject iProject = WORKSPACE_ROOT.getProject(referencedProject.getPreferenceNode().getName());
              if (iProject.isAccessible())
              {
                if (predicate.matches(iProject))
                {
                  referents.add(referencedProject);
                }
              }
            }
          }
          ECollections.setEList(preferenceProfile.getReferentProjects(), referents);
        }
      }
    }

    if (sort)
    {
      for (Project project : getProjects())
      {
        ECollections.sort(project.getPreferenceProfileReferences(), new Comparator<PreferenceProfile>()
        {
          public int compare(PreferenceProfile p1, PreferenceProfile p2)
          {
            if (p2.requires(p1))
            {
              if (!p1.requires(p2))
              {
                return -1;
              }
            }
            else if (p1.requires(p2))
            {
              return 1;
            }
            return p1.getName().compareTo(p2.getName());
          }
        });
      }
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getProjects()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS:
      return ((InternalEList<?>)getProjects()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS:
      return getProjects();
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE:
      if (resolve)
      {
        return getDefaultPreferenceNode();
      }
      return basicGetDefaultPreferenceNode();
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE:
      if (resolve)
      {
        return getInstancePreferenceNode();
      }
      return basicGetInstancePreferenceNode();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS:
      getProjects().clear();
      getProjects().addAll((Collection<? extends Project>)newValue);
      return;
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE:
      setDefaultPreferenceNode((PreferenceNode)newValue);
      return;
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE:
      setInstancePreferenceNode((PreferenceNode)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS:
      getProjects().clear();
      return;
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE:
      setDefaultPreferenceNode((PreferenceNode)null);
      return;
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE:
      setInstancePreferenceNode((PreferenceNode)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROJECTS:
      return projects != null && !projects.isEmpty();
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__DEFAULT_PREFERENCE_NODE:
      return defaultPreferenceNode != null;
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION__INSTANCE_PREFERENCE_NODE:
      return instancePreferenceNode != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION___APPLY_PREFERENCE_PROFILES:
      applyPreferenceProfiles();
      return null;
    case ProjectConfigPackage.WORKSPACE_CONFIGURATION___UPDATE_PREFERENCE_PROFILE_REFERENCES:
      updatePreferenceProfileReferences();
      return null;
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public String eURIFragmentSegment(EStructuralFeature eStructuralFeature, EObject eObject)
  {
    if (eStructuralFeature == ProjectConfigPackage.Literals.WORKSPACE_CONFIGURATION__PROJECTS)
    {
      Project child = (Project)eObject;
      PreferenceNode preferenceNode = child.getPreferenceNode();
      if (preferenceNode != null)
      {
        String name = preferenceNode.getName();
        if (name != null)
        {
          String encodedName = URI.encodeSegment(name, false);
          if (encodedName.startsWith("@"))
          {
            encodedName = "%40" + encodedName.substring(1);
          }
          return name;
        }
      }
    }

    return super.eURIFragmentSegment(eStructuralFeature, eObject);
  }

  @Override
  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    if (!uriFragmentSegment.startsWith("@"))
    {
      String preferenceNodeName = URI.decode(uriFragmentSegment);
      for (Project project : getProjects())
      {
        PreferenceNode preferenceNode = project.getPreferenceNode();
        if (preferenceNode != null && preferenceNodeName.equals(preferenceNode.getName()))
        {
          return project;
        }
      }
      return null;
    }
    return super.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

} // WorkspaceConfigurationImpl
