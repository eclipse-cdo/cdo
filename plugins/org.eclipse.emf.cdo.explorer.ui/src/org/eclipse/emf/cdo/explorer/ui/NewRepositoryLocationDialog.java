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
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.container.ElementWizardComposite;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class NewRepositoryLocationDialog extends TitleAreaDialog
{
  private ElementWizardComposite connectorWizard;

  private Text repositoryNameText;

  private String connectorType;

  private String connectorDescription;

  private String repositoryName;

  public NewRepositoryLocationDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public String getConnectorType()
  {
    return connectorType;
  }

  public String getConnectorDescription()
  {
    return connectorDescription;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("New Repository Location");
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("New Repository Location");
    // setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    Group group1 = new Group(composite, SWT.NONE);
    group1.setLayout(new GridLayout(1, false));
    group1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    group1.setText("Connection");
    connectorWizard = new ElementWizardComposite.WithRadios(group1, SWT.NONE, "org.eclipse.net4j.connectors", "Type:");
    connectorWizard.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Group group2 = new Group(composite, SWT.NONE);
    group2.setLayout(new GridLayout(1, false));
    group2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    group2.setText("Repository");
    repositoryNameText = new Text(group2, SWT.BORDER);
    repositoryNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    return composite;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected void okPressed()
  {
    connectorType = connectorWizard.getFactoryType();
    connectorDescription = connectorWizard.getDescription();
    repositoryName = repositoryNameText.getText();
    super.okPressed();
  }
}
