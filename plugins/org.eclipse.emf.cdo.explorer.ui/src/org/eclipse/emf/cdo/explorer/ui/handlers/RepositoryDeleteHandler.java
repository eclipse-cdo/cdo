/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.DeleteElementsDialog;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryDeleteHandler extends AbstractRepositoryHandler
{
  private List<CDOCheckout> checkouts;

  private boolean deleteCheckoutContents;

  private boolean deleteContents;

  public RepositoryDeleteHandler()
  {
    super(null, null);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    checkouts = new ArrayList<>();
    deleteCheckoutContents = false;
    deleteContents = false;

    Shell shell = HandlerUtil.getActiveShell(event);
    AbstractElement[] repositories = AbstractElement.collect(elements);

    for (CDORepository repository : elements)
    {
      checkouts.addAll(Arrays.asList(repository.getCheckouts()));
    }

    int size = checkouts.size();
    if (size != 0)
    {
      String plural = size == 1 ? "" : "s";
      String message = size == 1 ? "is 1" : "are " + size;

      if (MessageDialog.openQuestion(shell, "Existing Checkouts",
          "There " + message + " existing checkout" + plural + ".\n\n" + "Are you sure you want to delete the checkout" + plural + ", too?"))
      {
        DeleteElementsDialog dialog = new DeleteElementsDialog(shell, checkouts.toArray(new AbstractElement[size]));
        if (dialog.open() == DeleteElementsDialog.OK)
        {
          deleteCheckoutContents = dialog.isDeleteContents();
        }
        else
        {
          cancel();
          return;
        }
      }
      else
      {
        cancel();
        return;
      }
    }

    DeleteElementsDialog dialog = new DeleteElementsDialog(shell, repositories);
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
    for (CDOCheckout checkout : checkouts)
    {
      checkout.delete(deleteCheckoutContents);
    }

    checkouts = null;

    for (CDORepository repository : elements)
    {
      repository.delete(deleteContents);
    }
  }
}
