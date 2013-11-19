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
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class PromptDialog extends AbstractSetupDialog
{
  private final List<SetupTaskPerformer> setupTaskPerformers;

  public PromptDialog(Shell parentShell, List<SetupTaskPerformer> setupTaskPerformers)
  {
    super(parentShell, "Unspecified Variables", 400, 400);
    this.setupTaskPerformers = setupTaskPerformers;
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Enter values for the unspecified variables.";
  }

  @Override
  protected void createUI(Composite parent)
  {
    GridLayout layout = (GridLayout)parent.getLayout();
    layout.numColumns = 2;

    for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
    {
      List<ContextVariableTask> unresolvedVariables = setupTaskPerformer.getUnresolvedVariables();
      if (!unresolvedVariables.isEmpty())
      {
        Label header = new Label(parent, SWT.NONE);
        header.setText(setupTaskPerformer.getBranchDir().toString());
        header.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1));

        for (final ContextVariableTask contextVariableTask : unresolvedVariables)
        {
          Label variableLabel = new Label(parent, SWT.NONE);
          variableLabel.setText(contextVariableTask.getName() + ":");
          variableLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

          final Text text = new Text(parent, SWT.BORDER);
          text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
          text.addModifyListener(new ModifyListener()
          {
            public void modifyText(ModifyEvent e)
            {
              contextVariableTask.setValue(text.getText());
              validate();
            }
          });
        }
      }
    }
  }

  private void validate()
  {
    Button okButton = getButton(IDialogConstants.OK_ID);
    for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
    {
      List<ContextVariableTask> unresolvedVariables = setupTaskPerformer.getUnresolvedVariables();
      for (final ContextVariableTask contextVariableTask : unresolvedVariables)
      {
        if (StringUtil.isEmpty(contextVariableTask.getValue()))
        {
          okButton.setEnabled(false);
          return;
        }
      }
    }

    okButton.setEnabled(true);
  }
}