/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.workspace;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class CheckoutDialog extends TitleAreaDialog
{
  private Text projectNameText;

  private String projectName;

  public CheckoutDialog(Shell parentShell, String projectName)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
    this.projectName = projectName;
  }

  public String getProjectName()
  {
    return projectName;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Check out");
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("Check out");
    // setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    // Group group1 = new Group(composite, SWT.NONE);
    // group1.setLayout(new GridLayout(1, false));
    // group1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    // group1.setText("Connection");
    // connectorWizard = new ElementWizardComposite.WithRadios(group1, SWT.NONE, "org.eclipse.net4j.connectors",
    // "Type:");
    // connectorWizard.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Group group2 = new Group(composite, SWT.NONE);
    group2.setLayout(new GridLayout(1, false));
    group2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    group2.setText("Repository");
    projectNameText = new Text(group2, SWT.BORDER);
    projectNameText.setText(projectName == null ? "" : projectName);
    projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    projectNameText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        String projectName = projectNameText.getText();
        if (!Path.EMPTY.isValidSegment(projectName))
        {
          setErrorMessage("Invalid project name.");
          return;
        }

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists())
        {
          setErrorMessage("Project name exists.");
          return;
        }

        if (new File(project.getLocation().toString()).exists())
        {
          setErrorMessage("Project location exists.");
          return;
        }

        setErrorMessage(null);
      }
    });

    return composite;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected void okPressed()
  {
    projectName = projectNameText.getText();
    super.okPressed();
  }
}
