/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class ElementWizardDialog extends TitleAreaDialog implements ValidationContext
{
  private String title;

  private String toolTip;

  private String productGroup;

  private String factoryType;

  private String description;

  private String defaultFactoryType;

  private ElementWizardComposite wizardComposite;

  public ElementWizardDialog(Shell parentShell, String title, String toolTip, String productGroup, String defaultFactoryType)
  {
    super(parentShell);
    this.title = title;
    this.toolTip = toolTip;
    this.productGroup = productGroup;
    this.defaultFactoryType = defaultFactoryType;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public String getTitle()
  {
    return title;
  }

  public String getToolTip()
  {
    return toolTip;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public String getFactoryType()
  {
    return factoryType;
  }

  public String getDescription()
  {
    return description;
  }

  @Override
  public void setValidationError(Object source, String message)
  {
    setMessage(message, IMessageProvider.ERROR);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(title);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle(title);
    if (toolTip != null)
    {
      setMessage(toolTip);
    }

    // setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    composite.setLayout(new GridLayout(1, false));

    wizardComposite = new ElementWizardComposite.WithRadios(composite, SWT.NONE, productGroup, "Type:", this, defaultFactoryType)
    {
      @Override
      public String getDefaultDescription(String factoryType)
      {
        return ElementWizardDialog.this.getDefaultDescription(factoryType);
      }
    };

    wizardComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    return composite;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected String getDefaultDescription(String factoryType)
  {
    return null;
  }

  @Override
  protected void okPressed()
  {
    factoryType = wizardComposite.getFactoryType();
    description = wizardComposite.getDescription();
    super.okPressed();
  }
}
