/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.workspace;

import org.eclipse.emf.cdo.location.ICheckoutSource;

import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class CheckoutAsAction extends CheckoutAction
{
  public CheckoutAsAction()
  {
  }

  @Override
  protected void checkout(ICheckoutSource checkoutSource, String projectName)
  {
    Shell shell = getPart().getSite().getShell();
    CheckoutDialog dialog = new CheckoutDialog(shell, projectName);
    if (dialog.open() == CheckoutDialog.OK)
    {
      super.checkout(checkoutSource, dialog.getProjectName());
    }
  }
}
