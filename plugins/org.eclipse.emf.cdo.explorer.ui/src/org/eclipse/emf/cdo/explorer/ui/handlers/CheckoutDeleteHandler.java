/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.DeleteElementsDialog;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 */
public class CheckoutDeleteHandler extends AbstractCheckoutHandler
{
  private boolean deleteContents;

  public CheckoutDeleteHandler()
  {
    super(null, null);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    AbstractElement[] checkouts = AbstractElement.collect(elements);
    DeleteElementsDialog dialog = new DeleteElementsDialog(HandlerUtil.getActiveShell(event), checkouts);

    if (dialog.open() == DeleteElementsDialog.OK)
    {
      deleteContents = dialog.isDeleteContents();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doExecute(IProgressMonitor monitor) throws Exception
  {
    for (CDOCheckout checkout : elements)
    {
      checkout.delete(deleteContents);
    }
  }
}
