/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.ui;

import org.eclipse.emf.cdo.releng.version.VersionBuilder;
import org.eclipse.emf.cdo.releng.version.VersionNature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class AddNatureAction extends AbstractAction<Map<String, String>>
{
  public AddNatureAction()
  {
    super("Add version tool nature");
  }

  @Override
  protected Map<String, String> promptArguments()
  {
    Map<String, String> arguments = null;

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    BuilderConfigurationDialog dialog = new BuilderConfigurationDialog(shell, root);

    if (dialog.open() == BuilderConfigurationDialog.OK)
    {
      arguments = new HashMap<String, String>();

      IResource target = (IResource)dialog.getResult()[0];
      String releasePath = target.getFullPath().toString();
      arguments.put(VersionBuilder.RELEASE_PATH_ARGUMENT, releasePath);

      boolean ignoreMissingDependencyRanges = dialog.isIgnoreMissingDependencyRanges();
      if (ignoreMissingDependencyRanges)
      {
        arguments.put(VersionBuilder.DEPENDENCY_RANGES_ARGUMENT, "true");
      }

      boolean ignoreMissingExportVersions = dialog.isIgnoreMissingExportVersions();
      if (ignoreMissingExportVersions)
      {
        arguments.put(VersionBuilder.EXPORT_VERSIONS_ARGUMENT, "true");
      }
    }

    return arguments;
  }

  @Override
  protected void runWithArguments(Map<String, String> arguments) throws CoreException
  {
    for (Iterator<?> it = ((IStructuredSelection)selection).iterator(); it.hasNext();)
    {
      Object element = it.next();
      if (element instanceof IProject)
      {
        IProject project = (IProject)element;
        addNature(project, arguments);
      }
    }
  }

  protected void addNature(IProject project, Map<String, String> arguments) throws CoreException
  {
    IProjectDescription description = project.getDescription();

    String[] natureIds = description.getNatureIds();
    List<String> ids = new ArrayList<String>(Arrays.asList(natureIds));
    ids.add(VersionNature.NATURE_ID);
    description.setNatureIds(ids.toArray(new String[ids.size()]));

    ICommand[] buildSpec = description.getBuildSpec();
    List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(buildSpec));
    commands.add(createBuildCommand(description, arguments));
    description.setBuildSpec(commands.toArray(new ICommand[commands.size()]));

    project.setDescription(description, new NullProgressMonitor());
  }

  protected ICommand createBuildCommand(IProjectDescription description, Map<String, String> arguments)
  {
    ICommand command = description.newCommand();
    command.setBuilderName(VersionBuilder.BUILDER_ID);
    command.setArguments(arguments);
    return command;
  }

  /**
   * @author Eike Stepper
   */
  public class BuilderConfigurationDialog extends FilteredResourcesSelectionDialog
  {
    private Composite content;

    private Button ignoreMissingDependencyRangesButton;

    private boolean ignoreMissingDependencyRanges;

    private Button ignoreMissingExportVersionsButton;

    private boolean ignoreMissingExportVersions;

    public BuilderConfigurationDialog(Shell shell, IContainer container)
    {
      super(shell, false, container, IResource.FILE);
      setTitle("Builder Configuration");
      setMessage("Select a release specification file and check additional settings.");
      setInitialPattern("release.xml", CARET_BEGINNING);
      setHelpAvailable(false);
    }

    public boolean isIgnoreMissingDependencyRanges()
    {
      return ignoreMissingDependencyRanges;
    }

    public boolean isIgnoreMissingExportVersions()
    {
      return ignoreMissingExportVersions;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Control dialogArea = super.createDialogArea(parent);

      ignoreMissingDependencyRangesButton = new Button(content, SWT.CHECK);
      ignoreMissingDependencyRangesButton.setText("Ignore missing dependency version ranges");

      ignoreMissingExportVersionsButton = new Button(content, SWT.CHECK);
      ignoreMissingExportVersionsButton.setText("Ignore missing package export versions");

      return dialogArea;
    }

    @Override
    protected Control createExtendedContentArea(Composite content)
    {
      this.content = content;
      return super.createExtendedContentArea(content);
    }

    @Override
    protected void okPressed()
    {
      ignoreMissingDependencyRanges = ignoreMissingDependencyRangesButton.getSelection();
      ignoreMissingExportVersions = ignoreMissingExportVersionsButton.getSelection();
      super.okPressed();
    }
  }
}
