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
import org.eclipse.emf.cdo.releng.internal.setup.ui.PropertyField.ValueListener;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.VariableChoice;
import org.eclipse.emf.cdo.releng.setup.VariableType;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.provider.ExtendedFontRegistry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

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
    headerFont = ExtendedFontRegistry.INSTANCE.getFont(parent.getFont(), URI.createURI("font:///+2/bold"));

    GridLayout layout = (GridLayout)parent.getLayout();
    layout.numColumns = 4;
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
          PropertyField<?, ?> field = createField(variable);
          field.fill(parent);
        }
      }
    }
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    super.createButtonsForButtonBar(parent);
    validate();
  }

  private void createHeader(Composite parent, SetupTaskPerformer setupTaskPerformer)
  {
    GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
    gd.heightHint = 32;

    Label header = new Label(parent, SWT.NONE);
    header.setText((String)setupTaskPerformer.get(SetupConstants.KEY_BRANCH_LABEL));
    header.setLayoutData(gd);
    header.setFont(headerFont);
  }

  private PropertyField<?, ?> createField(final ContextVariableTask variable)
  {
    PropertyField<?, ?> field;
    EList<VariableChoice> choices = variable.getChoices();
    if (!choices.isEmpty())
    {
      field = new PropertyField.ChoiceField<Control>(choices);
    }
    else
    {
      field = createField(variable.getType());
    }

    String label = variable.getLabel();
    field.setLabelText(StringUtil.isEmpty(label) ? variable.getName() : label);
    field.setDescriptionText(variable.getDocumentation());

    String documentation = variable.getDocumentation();
    if (!StringUtil.isEmpty(documentation))
    {
      field.setToolTip(documentation);
    }

    field.addValueListener(new ValueListener()
    {
      public void valueChanged(String oldValue, String newValue) throws Exception
      {
        variable.setValue(newValue);
        validate();
      }
    });

    return field;
  }

  private PropertyField<?, ?> createField(VariableType type)
  {
    switch (type)
    {
    case FOLDER:
      PropertyField.FileField fileField = new PropertyField.FileField();
      fileField.setDialogText("Folder Selection");
      fileField.setDialogMessage("Select a folder.");
      return fileField;
    }

    return new PropertyField.TextField<Control>();
  }

  private void validate()
  {
    Button okButton = getButton(IDialogConstants.OK_ID);
    if (okButton != null)
    {
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
  }
}
