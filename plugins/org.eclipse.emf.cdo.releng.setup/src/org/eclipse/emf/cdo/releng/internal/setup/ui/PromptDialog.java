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
import org.eclipse.emf.cdo.releng.setup.SetupConstants;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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

  private Font headerFont;

  public PromptDialog(Shell parentShell, List<SetupTaskPerformer> setupTaskPerformers)
  {
    super(parentShell, "Unspecified Variables", 400, 400);
    this.setupTaskPerformers = setupTaskPerformers;
  }

  @Override
  public boolean close()
  {
    if (headerFont != null)
    {
      headerFont.dispose();
      headerFont = null;
    }

    return super.close();
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Enter values for the unspecified variables.";
  }

  @Override
  protected int getContainerMargin()
  {
    return 10;
  }

  @Override
  protected void createUI(Composite parent)
  {
    headerFont = getFont(parent, 16, SWT.BOLD);

    GridLayout layout = (GridLayout)parent.getLayout();
    layout.numColumns = 2;
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;

    for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
    {
      List<ContextVariableTask> variables = setupTaskPerformer.getUnresolvedVariables();
      if (!variables.isEmpty())
      {
        createHeader(parent, setupTaskPerformer);

        for (ContextVariableTask variable : variables)
        {
          createField(parent, variable);
        }
      }
    }

    validate();
  }

  private void createHeader(Composite parent, SetupTaskPerformer setupTaskPerformer)
  {
    GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
    gd.heightHint = 32;

    Label header = new Label(parent, SWT.NONE);
    header.setText((String)setupTaskPerformer.get(SetupConstants.KEY_BRANCH_LABEL));
    header.setLayoutData(gd);
    header.setFont(headerFont);
  }

  private void createField(Composite parent, final ContextVariableTask variable)
  {
    Label label = new Label(parent, SWT.NONE);
    label.setText(StringUtil.isEmpty(variable.getLabel()) ? variable.getName() : variable.getLabel() + ":");
    label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

    final Text text = new Text(parent, SWT.BORDER);
    text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    text.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        variable.setValue(text.getText());
        validate();
      }
    });

    setToolTip(variable, label);
    setToolTip(variable, text);
  }

  private void setToolTip(ContextVariableTask variable, Control control)
  {
    String documentation = variable.getDocumentation();
    if (!StringUtil.isEmpty(documentation))
    {
      control.setToolTipText(documentation);
    }
  }

  private void validate()
  {
    Button okButton = getButton(IDialogConstants.OK_ID);
    for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
    {
      for (ContextVariableTask variable : setupTaskPerformer.getUnresolvedVariables())
      {
        if (StringUtil.isEmpty(variable.getValue()))
        {
          okButton.setEnabled(false);
          return;
        }
      }
    }

    okButton.setEnabled(true);
  }

  private static Font getFont(Control control, int height, int style)
  {
    FontData[] datas = control.getFont().getFontData().clone();
    datas[0].setHeight(height);
    datas[0].setStyle(style);

    Display display = control.getShell().getDisplay();
    Font font = new Font(display, datas);
    return font;
  }
}
