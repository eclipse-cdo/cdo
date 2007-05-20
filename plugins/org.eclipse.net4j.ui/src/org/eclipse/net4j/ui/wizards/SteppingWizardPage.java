/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SteppingWizardPage extends WizardPage
{
  private int index;

  private List<Step> steps = new ArrayList(0);

  public SteppingWizardPage(int index)
  {
    super("page" + index);
    this.index = index;
  }

  public SteppingWizardPage(int index, String title, ImageDescriptor titleImage)
  {
    super("page" + index, title, titleImage);
  }

  public final int getIndex()
  {
    return index;
  }

  public List<Step> getWizardSteps()
  {
    return Collections.unmodifiableList(steps);
  }

  public boolean isEmpty()
  {
    for (Step step : steps)
    {
      if (step instanceof ValueStep)
      {
        return false;
      }
    }

    return true;
  }

  public void createControl(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));

    for (Step step : steps)
    {
      if (step instanceof ValueStep)
      {
        ValueStep valueStep = (ValueStep)step;
        if (valueStep.getControl() != null)
        {
          addValueControl(composite, valueStep);
        }
      }
      else if (step instanceof DecisionStep)
      {
        DecisionStep decisionStep = (DecisionStep)step;
        Group group = new Group(composite, SWT.BORDER);
        group.setText(decisionStep.getLabel());
        // for (Step decision : decisionStep)
        // {
        //
        // }
        //
        // decisionStep.createControl(composite);
      }
    }

    validate();
    setControl(composite);
  }

  protected void addValueControl(Composite composite, ValueStep valueStep)
  {
    new Label(composite, SWT.NONE).setText(valueStep.getLabel() + ":");
    Control control = valueStep.createControl(composite);
    valueStep.setControl(control);
  }

  protected void validate()
  {
    String errorMessage = null;
    for (Step step : steps)
    {
      if (step instanceof ValueStep)
      {
        errorMessage = ((ValueStep)step).validate();
        if (errorMessage != null)
        {
          break;
        }
      }
    }

    setErrorMessage(errorMessage);
    setPageComplete(errorMessage == null);
  }

  void addWizardStep(Step step)
  {
    steps.add(step);
    step.setWizardPage(this);
  }
}
