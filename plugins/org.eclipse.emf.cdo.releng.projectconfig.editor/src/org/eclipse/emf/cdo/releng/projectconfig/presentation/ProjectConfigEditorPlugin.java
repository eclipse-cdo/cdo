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
package org.eclipse.emf.cdo.releng.projectconfig.presentation;

import org.eclipse.emf.cdo.releng.predicates.provider.PredicatesEditPlugin;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.preferences.provider.PreferencesEditPlugin;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration;
import org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigPlugin;
import org.eclipse.emf.cdo.releng.projectconfig.presentation.ProjectConfigPreferencePage.PropertyModificationHandling;
import org.eclipse.emf.cdo.releng.projectconfig.util.ProjectConfigUtil;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.EclipseUIPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the central singleton for the Project Config editor plugin.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public final class ProjectConfigEditorPlugin extends EMFPlugin
{
  /**
   * Keep track of the singleton.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final ProjectConfigEditorPlugin INSTANCE = new ProjectConfigEditorPlugin();

  /**
   * Keep track of the singleton.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static Implementation plugin;

  /**
   * Create the instance.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ProjectConfigEditorPlugin()
  {
    super(new ResourceLocator[] { PredicatesEditPlugin.INSTANCE, PreferencesEditPlugin.INSTANCE, });
  }

  /**
   * Returns the singleton instance of the Eclipse plugin.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the singleton instance.
   * @generated
   */
  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return plugin;
  }

  /**
   * Returns the singleton instance of the Eclipse plugin.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the singleton instance.
   * @generated
   */
  public static Implementation getPlugin()
  {
    return plugin;
  }

  /**
   * The actual implementation of the Eclipse <b>Plugin</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static class Implementation extends EclipseUIPlugin
  {
    private static EarlyStartup earlyStartup;

    public static final class EarlyStartup implements IStartup
    {
      private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

      private static final IWorkspaceRoot WORKSPACE_ROOT = WORKSPACE.getRoot();

      private WorkspaceConfiguration workspaceConfiguration;

      protected IResourceChangeListener resourceChangeListener = new IResourceChangeListener()
      {
        public void resourceChanged(IResourceChangeEvent event)
        {
          IResourceDelta delta = event.getDelta();
          if (delta != null)
          {
            try
            {
              class ResourceDeltaVisitor implements IResourceDeltaVisitor
              {
                private Collection<IPath> changedPaths = new HashSet<IPath>();

                public Collection<IPath> getChangedPaths()
                {
                  return changedPaths;
                }

                public boolean visit(final IResourceDelta delta)
                {
                  int type = delta.getResource().getType();
                  if (type == IResource.FOLDER)
                  {
                    // Visit the folder's children only if it's the .settings folder.
                    IPath fullPath = delta.getFullPath();
                    if (!".settings".equals(fullPath.lastSegment()))
                    {
                      return false;
                    }
                  }
                  else if (type == IResource.FILE)
                  {
                    // Include only the *.prefs resources in the .settings folder.
                    //
                    IPath fullPath = delta.getFullPath();
                    if (fullPath.segmentCount() > 2
                        && "prefs".equals(fullPath.getFileExtension())
                        && !ProjectConfigUtil.PROJECT_CONF_NODE_NAME.equals(fullPath.removeFileExtension()
                            .lastSegment()))
                    {
                      changedPaths.add(fullPath);
                    }

                    return false;
                  }

                  // Recursively visit only the workspace root, the projects, and the .settings folders in projects.
                  return true;
                }
              }

              ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
              delta.accept(visitor);
              final Collection<IPath> changedPaths = visitor.getChangedPaths();

              if (!changedPaths.isEmpty())
              {
                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
                {
                  public void run()
                  {
                    final WorkspaceConfiguration newWorkspaceConfiguration = ProjectConfigUtil
                        .getWorkspaceConfiguration();
                    newWorkspaceConfiguration.updatePreferenceProfileReferences();
                    Map<Project, Map<Property, Property>> result = new LinkedHashMap<Project, Map<Property, Property>>();
                    for (IPath preferenceNodePath : changedPaths)
                    {
                      String projectName = preferenceNodePath.segment(0);
                      Project oldProject = workspaceConfiguration.getProject(projectName);
                      Project newProject = newWorkspaceConfiguration.getProject(projectName);
                      if (newProject != null)
                      {
                        String preferenceNodeName = preferenceNodePath.removeFileExtension().lastSegment();
                        PreferenceNode oldPreferenceNode = oldProject == null ? null : oldProject.getPreferenceNode()
                            .getNode(preferenceNodeName);
                        PreferenceNode newPreferenceNode = newProject.getPreferenceNode().getNode(preferenceNodeName);
                        Map<Property, String> modifiedProperties = collectModifiedProperties(workspaceConfiguration,
                            oldPreferenceNode, newPreferenceNode);
                        if (!modifiedProperties.isEmpty())
                        {
                          Map<Property, Property> propertyMap = null;
                          for (Property property : modifiedProperties.keySet())
                          {
                            String value = property.getValue();

                            Property preferenceProfileProperty = newProject.getProperty(property.getRelativePath());
                            if (preferenceProfileProperty != null)
                            {
                              String preferenceProfilePropertyValue = preferenceProfileProperty.getValue();
                              if (value == null ? preferenceProfilePropertyValue != null : !value
                                  .equals(preferenceProfilePropertyValue))
                              {
                                if (propertyMap == null)
                                {
                                  propertyMap = new LinkedHashMap<Property, Property>();
                                  result.put(newProject, propertyMap);
                                }

                                propertyMap.put(property, preferenceProfileProperty);
                              }
                            }
                            else
                            {
                              if (propertyMap == null)
                              {
                                propertyMap = new LinkedHashMap<Property, Property>();
                                result.put(newProject, propertyMap);
                              }

                              propertyMap.put(property, null);
                            }
                          }
                        }
                      }
                    }

                    if (!result.isEmpty())
                    {

                      StringBuilder modifiedUnmanagedProperty = new StringBuilder();
                      StringBuilder modifiedManagedProperty = new StringBuilder();
                      Map<Property, Property> managedProperties = new HashMap<Property, Property>();
                      for (Map.Entry<Project, Map<Property, Property>> entry : result.entrySet())
                      {
                        Project project = entry.getKey();
                        boolean isUnmanaged = false;
                        boolean isManaged = false;
                        for (Map.Entry<Property, Property> propertyEntry : entry.getValue().entrySet())
                        {
                          Property property = propertyEntry.getKey();
                          Property managingProperty = propertyEntry.getValue();
                          if (managingProperty != null)
                          {
                            if (!isManaged)
                            {
                              isManaged = true;
                              modifiedManagedProperty.append("\nThe following managed properties of "
                                  + project.getPreferenceNode().getName() + " been modified\n");
                            }

                            URI relativePath = property.getRelativePath();
                            modifiedManagedProperty.append("    >  " + relativePath + "=" + property.getValue() + "\n");
                            modifiedManagedProperty.append("    <  =" + managingProperty.getValue() + "\n");

                            managedProperties.put(property, managingProperty);
                          }
                          else
                          {
                            if (!isUnmanaged)
                            {
                              isUnmanaged = true;
                              modifiedUnmanagedProperty.append("\nThe following unmanaged properties of "
                                  + project.getPreferenceNode().getName() + " been modified\n");
                            }

                            URI relativePath = property.getRelativePath();
                            modifiedUnmanagedProperty.append("     " + relativePath + "=" + property.getValue() + "\n");
                          }
                        }
                      }

                      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                      if (modifiedManagedProperty.length() != 0)
                      {
                        if (propertyModificationHandling == ProjectConfigPreferencePage.PropertyModificationHandling.PROMPT
                            && MessageDialog.openConfirm(
                                shell,
                                "Managed Preference Modification",
                                modifiedManagedProperty.toString()
                                    + "\n\nEach of these properties will be overwritten by its managing property's value."
                                    + "  Would you instead like to propogate each of these changes to its managing property?")
                            || propertyModificationHandling == ProjectConfigPreferencePage.PropertyModificationHandling.PROPAGATE)
                        {
                          for (Map.Entry<Property, Property> entry : managedProperties.entrySet())
                          {
                            Property managedProperty = entry.getKey();
                            Property managingProperty = entry.getValue();
                            try
                            {
                              Preferences preferences = PreferencesUtil.getPreferences(managingProperty.getParent(),
                                  false);
                              preferences.put(managingProperty.getName(), managedProperty.getValue());
                            }
                            catch (BackingStoreException ex)
                            {
                              INSTANCE.log(ex);
                            }
                          }

                          try
                          {
                            WORKSPACE_ROOT.getWorkspace().run(new IWorkspaceRunnable()
                            {
                              public void run(IProgressMonitor monitor) throws CoreException
                              {
                                try
                                {
                                  PreferencesUtil.getPreferences(
                                      newWorkspaceConfiguration.getInstancePreferenceNode().getParent()
                                          .getNode("project"), false).flush();
                                }
                                catch (BackingStoreException ex)
                                {
                                  ProjectConfigPlugin.INSTANCE.log(ex);
                                }
                              }
                            }, new NullProgressMonitor());
                          }
                          catch (CoreException ex)
                          {
                            ProjectConfigPlugin.INSTANCE.log(ex);
                          }

                          return;
                        }
                      }

                      if (modifiedUnmanagedProperty.length() != 0)
                      {
                        if (configurationValidationPrompt
                            && MessageDialog
                                .openConfirm(
                                    shell,
                                    "Project Configuration",
                                    modifiedUnmanagedProperty
                                        + "\n\nWould you like to open the Project Configuration preferences to manage the properties?"))
                        {
                          PreferenceDialog dialog = org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(
                              shell,
                              "org.eclipse.emf.cdo.releng.projectconfig.presentation.ProjectConfigPreferencePage",
                              null, null);
                          dialog.open();
                        }
                      }

                      // ErrorDialog.openError(shell, "Unmanaged Preference Modification", "The preferences for "
                      // + unmanagedPreferenceNodePaths + " have been modified but those preferences are unmanaged",
                      // new Status(IStatus.ERROR, INSTANCE.getSymbolicName(), "Foo"));
                    }

                    newWorkspaceConfiguration.applyPreferenceProfiles();

                    workspaceConfiguration = newWorkspaceConfiguration;
                  }

                  private Map<Property, String> collectModifiedProperties(
                      WorkspaceConfiguration workspaceConfiguration, PreferenceNode oldPreferenceNode,
                      PreferenceNode newPreferenceNode)
                  {
                    LinkedHashMap<Property, String> result = new LinkedHashMap<Property, String>();
                    collectModifiedProperties(result, oldPreferenceNode, newPreferenceNode);
                    return result;
                  }

                  private void collectModifiedProperties(Map<Property, String> result,
                      PreferenceNode oldPreferenceNode, PreferenceNode newPreferenceNode)
                  {
                    for (PreferenceNode newChild : newPreferenceNode.getChildren())
                    {
                      PreferenceNode oldChild = oldPreferenceNode == null ? null : oldPreferenceNode.getNode(newChild
                          .getName());
                      if (oldChild == null)
                      {
                        for (Property newProperty : newChild.getProperties())
                        {
                          if (!workspaceConfiguration.isOmitted(newProperty))
                          {
                            result.put(newProperty, null);
                          }
                        }
                      }
                      else
                      {
                        collectModifiedProperties(result, oldChild, newChild);
                      }
                    }

                    for (Property newProperty : newPreferenceNode.getProperties())
                    {
                      Property oldProperty = oldPreferenceNode == null ? null : oldPreferenceNode
                          .getProperty(newProperty.getName());
                      if (oldProperty == null)
                      {
                        result.put(newProperty, null);
                      }
                      else
                      {
                        String newValue = newProperty.getValue();
                        String oldValue = oldProperty.getValue();
                        if (newValue == null ? oldValue != null : !newValue.equals(oldValue))
                        {
                          result.put(newProperty, oldValue);
                        }
                      }
                    }
                  }
                });
              }
            }
            catch (CoreException exception)
            {
              ProjectConfigEditorPlugin.INSTANCE.log(exception);
            }
          }
        }
      };

      private boolean configurationValidationPrompt = ProjectConfigPreferencePage.isConfigurationValidationPrompt();

      private PropertyModificationHandling propertyModificationHandling = ProjectConfigPreferencePage
          .getPropertyModificationHandling();

      public EarlyStartup()
      {
        earlyStartup = this;
        new ProjectConfigUtil.CompletenessChecker()
        {
          @Override
          protected void complete(WorkspaceConfiguration workspaceConfiguration)
          {
            EarlyStartup.this.workspaceConfiguration = workspaceConfiguration;
            workspaceConfiguration.updatePreferenceProfileReferences();
          }
        };
      }

      public void earlyStartup()
      {
        update();
      }

      public void stop()
      {
        WORKSPACE.removeResourceChangeListener(resourceChangeListener);
      }

      public void update()
      {
        if (ProjectConfigPreferencePage.isConfigurationManagementAutomatic())
        {
          WORKSPACE.addResourceChangeListener(resourceChangeListener);
        }
        else
        {
          WORKSPACE.removeResourceChangeListener(resourceChangeListener);
        }

        configurationValidationPrompt = ProjectConfigPreferencePage.isConfigurationValidationPrompt();
        propertyModificationHandling = ProjectConfigPreferencePage.getPropertyModificationHandling();
      }
    }

    /**
     * Creates an instance.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Implementation()
    {
      super();

      // Remember the static instance.
      //
      plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
      earlyStartup.stop();

      super.stop(context);
    }

    public void update()
    {
      earlyStartup.update();
    }
  }
}
