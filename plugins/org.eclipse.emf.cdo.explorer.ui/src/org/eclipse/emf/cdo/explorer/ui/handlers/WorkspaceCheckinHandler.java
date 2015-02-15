/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;

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
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    OfflineCDOCheckout checkout = elements.get(0);
    CDOWorkspace workspace = checkout.getWorkspace();
    if (workspace != null)
    {
      // TODO Prompt for checkin comment.
      // TODO Use progress monitor.
      workspace.checkin();
    }
  }
}
