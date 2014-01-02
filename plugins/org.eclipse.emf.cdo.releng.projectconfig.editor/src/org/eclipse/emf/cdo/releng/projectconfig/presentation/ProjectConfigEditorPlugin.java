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
import org.eclipse.emf.cdo.releng.projectconfig.provider.ProjectConfigEditPlugin;
import org.eclipse.emf.cdo.releng.projectconfig.util.ProjectConfigUtil;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.EclipseUIPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
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

    private static final String EDIT_KEY = "edit";

    private static final String PROPAGATE_KEY = "propagate";

    private static final String PREFERENCES_NODE_NAME = ProjectConfigEditorPlugin.INSTANCE.getSymbolicName();

    private static final Preferences PREFERENCES = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE_NAME);

    public static class ProjectConfigDialog extends TitleAreaDialog
    {
      private WorkspaceConfiguration workspaceConfiguration;

      private InputItem managedPropertiesInput;

      private TreeViewer managedPropertiesViewer;

      private InputItem unmanagedPropertiesInput;

      private TreeViewer unmanagedPropertiesViewer;

      private Composite container;

      private boolean propagate = PREFERENCES.getBoolean(PROPAGATE_KEY, false);

      private boolean edit = PREFERENCES.getBoolean(EDIT_KEY, true);

      public ProjectConfigDialog(Shell parentShell)
      {
        super(parentShell);

        setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
      }

      public boolean hasUnmanagedProperties()
      {
        return unmanagedPropertiesInput != null;
      }

      @Override
      public int open()
      {
        if (managedPropertiesInput != null || unmanagedPropertiesInput != null)
        {
          return super.open();
        }

        return Dialog.OK;
      }

      @Override
      protected Point getInitialSize()
      {
        return new Point(800, 500);
      }

      @Override
      protected void createButtonsForButtonBar(Composite parent)
      {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      }

      public void setWorkspaceConfiguration(WorkspaceConfiguration workspaceConfiguration)
      {
        this.workspaceConfiguration = workspaceConfiguration;

        if (managedPropertiesInput != null)
        {
          managedPropertiesInput.setWorkspaceConfiguration(workspaceConfiguration);
        }

        if (unmanagedPropertiesInput != null)
        {
          unmanagedPropertiesInput.setWorkspaceConfiguration(workspaceConfiguration);
        }
      }

      public void unmanagedProperty(Property property)
      {
        if (unmanagedPropertiesInput == null)
        {
          unmanagedPropertiesInput = new InputItem(workspaceConfiguration);
        }

        unmanagedPropertiesInput.getProperty(property);

        if (unmanagedPropertiesViewer != null)
        {
          unmanagedPropertiesViewer.setInput(unmanagedPropertiesInput);
        }
        else if (container != null)
        {
          for (Control child : container.getChildren())
          {
            child.dispose();
          }

          createUI(container);
          container.layout();
        }
      }

      public void managedProperty(Property managedProperty, Property managingProperty)
      {
        if (managedPropertiesInput == null)
        {
          managedPropertiesInput = new InputItem(workspaceConfiguration);
        }

        managedPropertiesInput.getProperty(managedProperty);

        if (managedPropertiesViewer != null)
        {
          managedPropertiesViewer.setInput(managedPropertiesInput);
        }
        else if (container != null)
        {
          for (Control child : container.getChildren())
          {
            child.dispose();
          }

          createUI(container);
          container.layout();
        }
      }

      @Override
      protected Control createDialogArea(Composite parent)
      {
        Shell shell = getShell();
        shell.setText("Preference Modification Problem");
        shell.setImage(ExtendedImageRegistry.INSTANCE.getImage(ProjectConfigEditorPlugin.INSTANCE
            .getImage("full/obj16/ProjectConfigModelFile")));

        setTitle("bar");

        Composite area = (Composite)super.createDialogArea(parent);

        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;

        container = new Composite(area, SWT.NONE);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        setTitle("Project-specific Preference Modification");
        setErrorMessage("Project-specific properties that are not directly managed by a preference profile of that project have been modified\n");

        createUI(container);

        shell.setActive();

        return area;
      }

      protected void createUI(Composite container)
      {
        if (managedPropertiesInput != null)
        {
          Group group = new Group(container, SWT.NONE);
          group.setText("Modified Managed Properties");
          group.setLayout(new GridLayout(1, false));
          group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

          {
            Composite composite = new Composite(group, SWT.NONE);
            composite.setLayout(new GridLayout(2, false));
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            Button overWriteButton = new Button(composite, SWT.RADIO);
            overWriteButton.setText("Overwrite with managing property");
            if (!propagate)
            {
              overWriteButton.setSelection(true);
            }
            overWriteButton.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                propagate = false;
                PREFERENCES.putBoolean(PROPAGATE_KEY, false);
                try
                {
                  PREFERENCES.flush();
                }
                catch (BackingStoreException ex)
                {
                  ProjectConfigEditorPlugin.INSTANCE.log(ex);
                }
              }
            });

            Button propogateButton = new Button(composite, SWT.RADIO);
            propogateButton.setText("Propagate to managing property");
            if (propagate)
            {
              propogateButton.setSelection(true);
            }
            propogateButton.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                propagate = true;
                PREFERENCES.putBoolean(PROPAGATE_KEY, true);
                try
                {
                  PREFERENCES.flush();
                }
                catch (BackingStoreException ex)
                {
                  ProjectConfigEditorPlugin.INSTANCE.log(ex);
                }
              }
            });
          }

          managedPropertiesViewer = new TreeViewer(group, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

          ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
              ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
          managedPropertiesViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
          managedPropertiesViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
          managedPropertiesViewer.setInput(managedPropertiesInput);
          managedPropertiesViewer.expandToLevel(2);

          final Tree tree = managedPropertiesViewer.getTree();
          tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        }

        if (unmanagedPropertiesInput != null)
        {
          Group group = new Group(container, SWT.NONE);
          group.setText("Unmanaged Properties");
          group.setLayout(new GridLayout(1, false));
          group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

          Button editButton = new Button(group, SWT.CHECK);
          editButton.setText("Edit");
          editButton.setSelection(edit);
          editButton.addSelectionListener(new SelectionAdapter()
          {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
              edit = !edit;
              PREFERENCES.putBoolean(EDIT_KEY, edit);
              PREFERENCES.putBoolean(PROPAGATE_KEY, false);
              try
              {
                PREFERENCES.flush();
              }
              catch (BackingStoreException ex)
              {
                ProjectConfigEditorPlugin.INSTANCE.log(ex);
              }
            }
          });

          unmanagedPropertiesViewer = new TreeViewer(group, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

          ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
              ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
          unmanagedPropertiesViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
          unmanagedPropertiesViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
          unmanagedPropertiesViewer.setInput(unmanagedPropertiesInput);
          unmanagedPropertiesViewer.expandToLevel(2);

          final Tree tree = unmanagedPropertiesViewer.getTree();
          tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        }
      }

      public static class InputItem extends ItemProvider
      {
        private WorkspaceConfiguration workspaceConfiguration;

        public void setWorkspaceConfiguration(WorkspaceConfiguration workspaceConfiguration)
        {
          this.workspaceConfiguration = workspaceConfiguration;
        }

        public InputItem(WorkspaceConfiguration workspaceConfiguration)
        {
          this.workspaceConfiguration = workspaceConfiguration;
        }

        public ProjectItem getProject(Project project)
        {
          String name = project.getPreferenceNode().getName();
          EList<Object> children = getChildren();
          for (Object child : children)
          {
            ProjectItem projectItem = (ProjectItem)child;
            if (name.equals(projectItem.getText()))
            {
              return projectItem;
            }
          }

          ProjectItem projectItem = new ProjectItem(project);
          getChildren().add(projectItem);
          return projectItem;
        }

        public PreferenceNodeItem getPreferenceNode(PreferenceNode preferenceNode)
        {
          PreferenceNode projectPreferenceNode = preferenceNode.getScope();
          PreferenceNode parentPreferenceNode = preferenceNode.getParent();

          ItemProvider parentItem;
          if (projectPreferenceNode == parentPreferenceNode)
          {
            parentItem = getProject(workspaceConfiguration.getProject(projectPreferenceNode.getName()));
          }
          else
          {
            parentItem = getPreferenceNode(parentPreferenceNode);
          }

          String name = preferenceNode.getName();
          EList<Object> children = parentItem.getChildren();
          for (Object child : children)
          {
            if (child instanceof PreferenceNodeItem)
            {
              PreferenceNodeItem preferenceNodeItem = (PreferenceNodeItem)child;
              if (name.equals(preferenceNodeItem.getText()))
              {
                return preferenceNodeItem;
              }
            }
          }

          PreferenceNodeItem preferenceNodeItem = new PreferenceNodeItem(preferenceNode);
          children.add(preferenceNodeItem);
          return preferenceNodeItem;
        }

        public PropertyItem getProperty(Property property)
        {
          PreferenceNodeItem preferenceNodeItem = getPreferenceNode(property.getParent());

          String name = property.getName();
          EList<Object> children = preferenceNodeItem.getChildren();
          for (Object child : children)
          {
            if (child instanceof PropertyItem)
            {
              PropertyItem propertyItem = (PropertyItem)child;
              if (name.equals(propertyItem.getText()))
              {
                return propertyItem;
              }
            }
          }

          PropertyItem propertyItem = new PropertyItem(property);
          children.add(propertyItem);
          return propertyItem;
        }
      }

      public static class ProjectItem extends ItemProvider
      {
        private static final Object IMAGE = ProjectConfigEditPlugin.INSTANCE.getImage("full/obj16/Project");

        private Project project;

        public ProjectItem(Project project)
        {
          super(project.getPreferenceNode().getName(), IMAGE);

          this.project = project;
        }

        public Project getProject()
        {
          return project;
        }
      }

      public static class PreferenceNodeItem extends ItemProvider
      {
        private static final Object IMAGE = PreferencesEditPlugin.INSTANCE.getImage("full/obj16/PreferenceNode");

        private PreferenceNode preferenceNode;

        public PreferenceNodeItem(PreferenceNode preferenceNode)
        {
          super(preferenceNode.getName(), IMAGE);

          this.preferenceNode = preferenceNode;
        }

        public PreferenceNode getPreferenceNode()
        {
          return preferenceNode;
        }
      }

      public static class PropertyItem extends ItemProvider
      {
        private static final Object IMAGE = PreferencesEditPlugin.INSTANCE.getImage("full/obj16/Property");

        private Property property;

        public PropertyItem(Property property)
        {
          super(property.getName(), IMAGE);

          this.property = property;
        }

        public Property getProperty()
        {
          return property;
        }
      }
    }

    public static final class EarlyStartup implements IStartup
    {
      private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

      private static final IWorkspaceRoot WORKSPACE_ROOT = WORKSPACE.getRoot();

      private WorkspaceConfiguration workspaceConfiguration;

      private ProjectConfigDialog projectConfigDialog;

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
                    // Visit the folder's children only if its the .settings folder.
                    IPath fullPath = delta.getFullPath();
                    if (!".settings".equals(fullPath.lastSegment()))
                    {
                      return false;
                    }
                  }
                  else if (type == IResource.FILE)
                  {
                    // Include only the *.prefs resources in the .settings folder.
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
                    try
                    {
                      newWorkspaceConfiguration.updatePreferenceProfileReferences();
                      Map<Property, Property> propertyMap = new LinkedHashMap<Property, Property>();
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
                                  propertyMap.put(property, preferenceProfileProperty);
                                }
                              }
                              else
                              {
                                propertyMap.put(property, null);
                              }
                            }
                          }
                        }
                      }

                      if (!propertyMap.isEmpty())
                      {
                        boolean dialogCreator = false;
                        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                        if (projectConfigDialog == null
                            && (configurationValidationPrompt || propertyModificationHandling == PropertyModificationHandling.PROMPT))
                        {
                          projectConfigDialog = new ProjectConfigDialog(shell);
                          projectConfigDialog.setWorkspaceConfiguration(newWorkspaceConfiguration);
                          dialogCreator = true;
                        }

                        Map<Property, Property> managedProperties = new HashMap<Property, Property>();
                        for (Map.Entry<Property, Property> propertyEntry : propertyMap.entrySet())
                        {
                          Property property = propertyEntry.getKey();
                          Property managingProperty = propertyEntry.getValue();
                          if (managingProperty != null)
                          {
                            if (propertyModificationHandling == PropertyModificationHandling.PROMPT)
                            {
                              projectConfigDialog.managedProperty(property, managingProperty);
                            }

                            managedProperties.put(property, managingProperty);
                          }
                          else
                          {
                            if (configurationValidationPrompt)
                            {
                              projectConfigDialog.unmanagedProperty(property);
                            }
                          }
                        }

                        if (projectConfigDialog != null)
                        {
                          if (dialogCreator)
                          {
                            ProjectConfigDialog dialog = projectConfigDialog;
                            projectConfigDialog.open();
                            if (dialog.hasUnmanagedProperties() && PREFERENCES.getBoolean(EDIT_KEY, false))
                            {
                              PreferenceDialog preferenceDialog = org.eclipse.ui.dialogs.PreferencesUtil
                                  .createPreferenceDialogOn(
                                      shell,
                                      "org.eclipse.emf.cdo.releng.projectconfig.presentation.ProjectConfigPreferencePage",
                                      null, null);
                              preferenceDialog.open();
                            }
                          }
                          else
                          {
                            Shell dialogShell = projectConfigDialog.getShell();
                            Display display = dialogShell.getDisplay();
                            while (!dialogShell.isDisposed())
                            {
                              if (!display.readAndDispatch())
                              {
                                display.sleep();
                              }
                            }
                          }
                        }

                        if (!managedProperties.isEmpty()
                            && (propertyModificationHandling == PropertyModificationHandling.PROPAGATE || propertyModificationHandling == PropertyModificationHandling.PROMPT
                                && PREFERENCES.getBoolean(PROPAGATE_KEY, false)))
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

                          // We expect this to cause another delta which will apply the preference profiles anyway, so
                          // don't bother doing it now.
                          return;
                        }
                      }

                      newWorkspaceConfiguration.applyPreferenceProfiles();
                    }
                    finally
                    {
                      projectConfigDialog = null;
                      workspaceConfiguration = newWorkspaceConfiguration;
                    }
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
