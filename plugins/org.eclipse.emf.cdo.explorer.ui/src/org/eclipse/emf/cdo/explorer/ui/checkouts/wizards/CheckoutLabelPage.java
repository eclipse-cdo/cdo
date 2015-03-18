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
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.swt.widgets.Composite;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutLabelPage extends CheckoutWizardPage
{
  private String label;

  public CheckoutLabelPage()
  {
    super("Label", "Enter the label of the new checkout.");
  }

  public final String getLabel()
  {
    return label;
  }

  public final void setLabel(String label)
  {
    this.label = label;
  }

  @Override
  protected void createUI(Composite parent)
  {
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty("label", label);
  }
}
