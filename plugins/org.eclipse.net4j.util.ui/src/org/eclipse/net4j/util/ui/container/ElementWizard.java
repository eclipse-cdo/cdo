/*
 * Copyright (c) 2010-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public abstract class ElementWizard extends Notifier implements IElementWizard, ValidationContext
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

  @Override
  public String getResultDescription()
  {
    return resultDescription;
  }

  @Override
  public Object getResultElement()
  {
    return container.getElement(productGroup, factoryType, resultDescription);
  }

  @Override
  public void create(Composite parent, IManagedContainer container, String productGroup, String factoryType, String defaultDescription,
      ValidationContext context)
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
    String oldDescription = this.resultDescription;
    if (!ObjectUtil.equals(resultDescription, oldDescription))
    {
      this.resultDescription = resultDescription;
      fireEvent();
    }
  }

  @Override
  public void setValidationError(Object source, String message)
  {
    if (context != null)
    {
      context.setValidationError(source, message);
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

  /**
   * @since 3.2
   */
  public static Combo addCombo(Composite parent, String label, List<String> choices)
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

  public static Combo addCombo(Composite parent, String label, String... choices)
  {
    return addCombo(parent, label, Arrays.asList(choices));
  }
}
