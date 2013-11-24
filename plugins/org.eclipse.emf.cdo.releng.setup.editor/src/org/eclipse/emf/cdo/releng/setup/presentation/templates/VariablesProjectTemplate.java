/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation.templates;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.editor.ProjectTemplate;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupModelWizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class VariablesProjectTemplate extends ProjectTemplate
{
  private Branch maintenanceBranch;

  public VariablesProjectTemplate()
  {
    super("variables", "Simple project with variables");

    addBranch("master");
    maintenanceBranch = addBranch("4.2");
  }

  @Override
  public Control createControl(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.verticalSpacing = 10;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);
    SetupModelWizard.applyGridData(composite);

    new Label(composite, SWT.NONE).setText("Maintenance branch name:");

    final Text text = new Text(composite, SWT.BORDER);
    SetupModelWizard.applyGridData(text);
    text.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        maintenanceBranch.setName(text.getText());
      }
    });

    return composite;
  }

  @Override
  public boolean isPageValid()
  {
    return false;
  }
}
