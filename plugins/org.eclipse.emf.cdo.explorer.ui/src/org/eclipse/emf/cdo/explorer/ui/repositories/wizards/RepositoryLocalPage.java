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

import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.IDGeneration;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository.VersioningMode;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

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
  private Text nameText;

  private Button normalButton;

  private Button auditingButton;

  private Button branchingButton;

  private Button counterButton;

  private Button uuidButton;

  private TextAndDisable portText;

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
    nameText = new Text(container, SWT.BORDER);
    nameText.setText("repo2");
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.addModifyListener(this);

    new Label(container, SWT.NONE);

    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayout(layout);

    Group modeGroup = new Group(composite, SWT.NONE);
    modeGroup.setLayout(new GridLayout(1, false));
    modeGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
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

    Group idGroup = new Group(composite, SWT.NONE);
    idGroup.setLayout(new GridLayout(1, false));
    idGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
    idGroup.setText("ID Generation");

    counterButton = new Button(idGroup, SWT.RADIO);
    counterButton.setText("Counter (efficient)");
    counterButton.setSelection(true);
    counterButton.addSelectionListener(this);

    uuidButton = new Button(idGroup, SWT.RADIO);
    uuidButton.setText("UUID (replicable)");
    uuidButton.addSelectionListener(this);

    createLabel(container, "TCP port:");
    portText = new TextAndDisable(container, SWT.BORDER, null);
    portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    portText.setValue("2037");
    portText.setDisabled(true);
    portText.addModifyListener(this);
    portText.addSelectionListener(this);
  }

  @Override
  protected void doValidate(Properties properties) throws Exception
  {
    super.doValidate(properties);

    String name = nameText.getText();
    if (StringUtil.isEmpty(name))
    {
      throw new Exception("Name is empty.");
    }

    properties.put(LocalCDORepository.PROP_NAME, name);

    if (normalButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Normal.toString());
    }
    else if (auditingButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Auditing.toString());
    }
    else if (branchingButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_VERSIONING_MODE, VersioningMode.Branching.toString());
    }

    if (counterButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_ID_GENERATION, IDGeneration.Counter.toString());
    }
    else if (uuidButton.getSelection())
    {
      properties.put(LocalCDORepository.PROP_ID_GENERATION, IDGeneration.UUID.toString());
    }

    String port = portText.getValue();

    try
    {
      int value = Integer.parseInt(port);
      if (value < 0)
      {
        throw new Exception();
      }
    }
    catch (Exception ex)
    {
      throw new Exception("Invalid TCP port.");
    }

    properties.put(LocalCDORepository.PROP_TCP_DISABLED, Boolean.toString(portText.isDisabled()));
    properties.put(LocalCDORepository.PROP_TCP_PORT, port);

  }
}
