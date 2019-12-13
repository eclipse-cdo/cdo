/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction.ChangeSetOutdatedException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 */
public class WorkspaceCheckinHandler extends AbstractBaseHandler<OfflineCDOCheckout>
{
  public WorkspaceCheckinHandler()
  {
    super(OfflineCDOCheckout.class, false);
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    boolean result = super.updateSelection(selection);

    if (result)
    {
      OfflineCDOCheckout checkout = elements.get(0);
      if (!checkout.isDirty())
      {
        return false;
      }
    }

    return result;
  }

  @Override
  protected void doExecute(final ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    OfflineCDOCheckout checkout = elements.get(0);
    CDOWorkspace workspace = checkout.getWorkspace();
    if (workspace != null)
    {
      ChangeSetOutdatedException exception = null;

      try
      {
        // TODO Prompt for checkin comment.
        // TODO Use progress monitor.
        workspace.checkin();
      }
      catch (CommitException ex)
      {
        Throwable cause = ex.getCause();
        if (cause instanceof ChangeSetOutdatedException)
        {
          exception = (ChangeSetOutdatedException)cause;
        }
        else
        {
          throw ex;
        }
      }
      catch (ChangeSetOutdatedException ex)
      {
        exception = ex;
      }

      if (exception != null)
      {
        UIUtil.syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            Shell shell = HandlerUtil.getActiveShell(event);
            if (shell == null)
            {
              shell = UIUtil.getShell();
            }

            MessageDialog.openError(shell, "Checkin Error", "Your checkout is outdated and needs to be updated.");
          }
        });
      }
    }
  }
}
