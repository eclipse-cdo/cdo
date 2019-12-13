/*
 * Copyright (c) 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class ElementWizardAction extends LongRunningAction
{
  private Shell shell;

  private String title;

  private String toolTip;

  private String productGroup;

  private String factoryType;

  private String description;

  private String defaultFactoryType;

  private IManagedContainer container;

  public ElementWizardAction(Shell shell, String title, String toolTip, ImageDescriptor image, String productGroup)
  {
    this(shell, title, toolTip, image, productGroup, IPluginContainer.INSTANCE);
  }

  public ElementWizardAction(Shell shell, String title, String toolTip, ImageDescriptor image, String productGroup, IManagedContainer container)
  {
    this(shell, title, toolTip, image, productGroup, container, null);
  }

  public ElementWizardAction(Shell shell, String title, String toolTip, ImageDescriptor image, String productGroup, IManagedContainer container,
      String defaultFactoryType)
  {
    super(title, toolTip, image);
    this.shell = shell;
    this.title = title;
    this.toolTip = toolTip;
    this.productGroup = productGroup;
    this.container = container;
    this.defaultFactoryType = defaultFactoryType;
  }

  public String getDefaultFactoryType()
  {
    return defaultFactoryType;
  }

  /**
   * Can be overridden by subclasses.
   */
  public String getDefaultDescription(String factoryType)
  {
    return null;
  }

  @Override
  protected void preRun() throws Exception
  {
    ElementWizardDialog dialog = new ElementWizardDialog(shell, title, toolTip, productGroup, defaultFactoryType)
    {
      @Override
      protected IManagedContainer getContainer()
      {
        return container;
      }

      @Override
      protected String getDefaultDescription(String factoryType)
      {
        return ElementWizardAction.this.getDefaultDescription(factoryType);
      }
    };

    if (dialog.open() == ElementWizardDialog.OK)
    {
      factoryType = dialog.getFactoryType();
      description = dialog.getDescription();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      container.getElement(productGroup, factoryType, description);
    }
    catch (final RuntimeException ex)
    {
      OM.LOG.error(ex);
      getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          MessageDialog.openError(shell, title, "An error occured: " + ex.getMessage() + "\n\nThe error log may contain more information about the problem.");
        }
      });
    }
  }
}
