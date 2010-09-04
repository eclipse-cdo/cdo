/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.container.IElementWizard.ValidationContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public abstract class ElementWizard implements IElementWizard, ValidationContext
{
  private String factoryType;

  private String defaultDescription;

  private String resultDescription;

  private ValidationContext validationContext;

  public ElementWizard()
  {
  }

  protected String getFactoryType()
  {
    return factoryType;
  }

  protected String getDefaultDescription()
  {
    return defaultDescription;
  }

  public String getResultDescription()
  {
    return resultDescription;
  }

  public void create(Composite parent, String factoryType, String defaultDescription,
      ValidationContext validationContext)
  {
    this.factoryType = factoryType;
    this.defaultDescription = defaultDescription;
    this.validationContext = validationContext;

    create(parent);
  }

  protected void setResultDescription(String resultDescription)
  {
    this.resultDescription = resultDescription;
  }

  public void setErrorMessage(String message)
  {
    if (validationContext != null)
    {
      validationContext.setErrorMessage(message);
    }
  }

  protected abstract void create(Composite parent);

  protected Label addLabel(Composite parent, String label)
  {
    Label control = new Label(parent, SWT.NONE);
    control.setText(label);
    control.setLayoutData(UIUtil.createGridData(false, false));
    return control;
  }

  protected Text addText(Composite parent, String label)
  {
    addLabel(parent, label);

    Text control = new Text(parent, SWT.BORDER);
    control.setLayoutData(UIUtil.createGridData(true, false));
    return control;
  }

  protected Combo addCombo(Composite parent, String label, String... choices)
  {
    addLabel(parent, label);

    Combo control = new Combo(parent, SWT.SINGLE);
    control.setLayoutData(UIUtil.createGridData(true, false));
    for (String choice : choices)
    {
      control.add(choice);
    }

    return control;
  }
}
