/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.IDGeneration;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.VersioningMode;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RepositoryLocalPage extends AbstractRepositoryPage
{
  private Text repositoryNameText;

  private Button normalButton;

  private Button auditingButton;

  private Button branchingButton;

  private Button counterButton;

  private Button uuidButton;

  public RepositoryLocalPage()
  {
    super("local", "Local Repository 1");
    setTitle("New Local Repository");
    setMessage("Enter the label and the connection parameters of the new remote location.");
  }

  @Override
  protected void fillPage(Composite container)
  {
    createLabel(container, "Repository name:");
    repositoryNameText = new Text(container, SWT.BORDER);
    repositoryNameText.setText("localhost");
    repositoryNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    repositoryNameText.addModifyListener(this);

    new Label(container, SWT.NONE);
    Group modeGroup = new Group(container, SWT.NONE);
    modeGroup.setLayout(new GridLayout(1, false));
    modeGroup.setText("Versioning Mode");

    normalButton = new Button(modeGroup, SWT.RADIO);
    normalButton.setText("Normal (no history)");
    normalButton.setSelection(true);
    normalButton.addSelectionListener(this);

    auditingButton = new Button(modeGroup, SWT.RADIO);
    auditingButton.setText("Auditing (linear history)");
    auditingButton.addSelectionListener(this);

    branchingButton = new Button(modeGroup, SWT.RADIO);
    branchingButton.setText("Branching (history tree)");
    branchingButton.addSelectionListener(this);

    new Label(container, SWT.NONE);
    Group idGroup = new Group(container, SWT.NONE);
    idGroup.setLayout(new GridLayout(1, false));
    idGroup.setText("ID Generation");

    counterButton = new Button(idGroup, SWT.RADIO);
    counterButton.setText("Counter (efficient)");
    counterButton.setSelection(true);
    counterButton.addSelectionListener(this);

    uuidButton = new Button(idGroup, SWT.RADIO);
    uuidButton.setText("UUID (replicable)");
    uuidButton.addSelectionListener(this);
  }

  @Override
  protected void doValidate(Properties properties) throws Exception
  {
    super.doValidate(properties);

    String repositoryName = repositoryNameText.getText();
    if (StringUtil.isEmpty(repositoryName))
    {
      throw new Exception("Repository name is empty.");
    }

    properties.put("repositoryName", repositoryName);

    if (normalButton.getSelection())
    {
      properties.put("versioningMode", VersioningMode.Normal.toString());
    }
    else if (auditingButton.getSelection())
    {
      properties.put("versioningMode", VersioningMode.Auditing.toString());
    }
    else if (branchingButton.getSelection())
    {
      properties.put("versioningMode", VersioningMode.Branching.toString());
    }

    if (counterButton.getSelection())
    {
      properties.put("idGeneration", IDGeneration.Counter.toString());
    }
    else if (uuidButton.getSelection())
    {
      properties.put("idGeneration", IDGeneration.UUID.toString());
    }
  }
}
