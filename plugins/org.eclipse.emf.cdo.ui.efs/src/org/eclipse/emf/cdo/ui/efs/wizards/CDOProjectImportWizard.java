/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.efs.wizards;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.ValidationContext;
import org.eclipse.net4j.util.ui.container.ElementWizardComposite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import java.net.URI;

/**
 * @author Martin Fluegge
 */
public class CDOProjectImportWizard extends Wizard implements IImportWizard
{
  public CDOProjectImportWizard()
  {
  }

  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
  }

  @Override
  public boolean performFinish()
  {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();

    String projectName = "cdo_test1";
    IProject project = workspace.getRoot().getProject(projectName);
    if (project.exists())
    {
      return false;
    }

    IProjectDescription description = workspace.newProjectDescription(projectName);
    description.setLocationURI(URI.create("cdo.net4j.tcp://localhost/repo1/MAIN/@"));

    try
    {
      project.create(description, new NullProgressMonitor());
      if (!project.isOpen())
      {
        project.open(new NullProgressMonitor());
      }
    }
    catch (CoreException ex)
    {
      ex.printStackTrace();
      return false;
    }

    return true;
  }

  @Override
  public void addPages()
  {
    addPage(new Page());
  }

  /**
   * @author Eike Stepper
   */
  public static class Page extends WizardPage implements ValidationContext
  {
    public Page()
    {
      super("CDOProjectImportWizardPage");
    }

    public void createControl(Composite parent)
    {
      Composite composite = new Composite(parent, SWT.FILL);
      // FillLayout layout = new FillLayout(SWT.VERTICAL);
      GridLayout layout = new GridLayout(1, true);

      composite.setLayout(layout);
      // composite.setLayout(new GridLayout(1, true));

      Group repositoryGroup = new Group(composite, SWT.NONE);
      repositoryGroup.setLayout(new GridLayout(2, false));
      repositoryGroup.setLayoutData(UIUtil.createGridData(true, false));

      createRepositoryControl(repositoryGroup);

      Group group = new Group(composite, SWT.NONE);
      group.setText("Connection");
      group.setLayout(new FillLayout());
      group.setLayoutData(UIUtil.createGridData(true, true));

      new ElementWizardComposite.WithRadios(group, SWT.NONE, "org.eclipse.net4j.connectors", "Type:");
      setControl(group);
    }

    private void createRepositoryControl(Composite parent)
    {
      Label repositoryLabel = new Label(parent, SWT.NONE);
      repositoryLabel.setText("Repository:");
      repositoryLabel.setLayoutData(UIUtil.createGridData(false, false));

      Text repositoryText = new Text(parent, SWT.BORDER);
      repositoryText.setLayoutData(UIUtil.createGridData(true, false));
    }

    public void setValidationError(Object source, String message)
    {
      setMessage(message, IMessageProvider.ERROR);
    }
  }
}
