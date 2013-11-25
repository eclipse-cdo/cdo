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
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
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
  public VariablesProjectTemplate()
  {
    super("variables", "Simple project with variables");
  }

  @Override
  public Control createControl(Composite parent, final Container container, Project project)
  {
    Branch master = SetupFactory.eINSTANCE.createBranch();
    master.setName("master");
    project.getBranches().add(master);

    final Branch maintenance = SetupFactory.eINSTANCE.createBranch();
    project.getBranches().add(maintenance);

    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.verticalSpacing = 10;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);
    SetupModelWizard.applyGridData(composite);

    new Label(composite, SWT.NONE).setText("Maintenance branch name:");

    final Text branchText = new Text(composite, SWT.BORDER);
    SetupModelWizard.applyGridData(branchText);
    branchText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        maintenance.setName(branchText.getText());
        container.validate();
      }
    });

    branchText.setText("4.2");
    return composite;
  }
}
