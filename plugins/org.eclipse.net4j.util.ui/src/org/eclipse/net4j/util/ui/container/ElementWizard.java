/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.container.IElementWizard.ValidationContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public abstract class ElementWizard implements IElementWizard, ValidationContext
{
  private IManagedContainer container;

  private String productGroup;

  private String factoryType;

  private String defaultDescription;

  private String resultDescription;

  private ValidationContext context;

  public ElementWizard()
  {
  }

  protected IManagedContainer getContainer()
  {
    return container;
  }

  protected String getProductGroup()
  {
    return productGroup;
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

  public Object getResultElement()
  {
    return container.getElement(productGroup, factoryType, resultDescription);
  }

  public void create(Composite parent, IManagedContainer container, String productGroup, String factoryType,
      String defaultDescription, ValidationContext context)
  {
    this.container = container;
    this.productGroup = productGroup;
    this.factoryType = factoryType;
    this.defaultDescription = defaultDescription;
    this.context = context;

    create(parent);
  }

  protected void setResultDescription(String resultDescription)
  {
    this.resultDescription = resultDescription;
  }

  public void setValidationOK()
  {
    if (context != null)
    {
      context.setValidationOK();
    }
  }

  public void setValidationError(Control control, String message)
  {
    if (context != null)
    {
      context.setValidationError(control, message);
    }
  }

  protected abstract void create(Composite parent);

  public static Label addLabel(Composite parent, String label)
  {
    Label control = new Label(parent, SWT.NONE);
    control.setText(label);
    control.setLayoutData(UIUtil.createGridData(false, false));
    return control;
  }

  public static Text addText(Composite parent, String label)
  {
    addLabel(parent, label);

    Text control = new Text(parent, SWT.BORDER);
    control.setLayoutData(UIUtil.createGridData(true, false));
    return control;
  }

  public static Combo addCombo(Composite parent, String label, String... choices)
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
