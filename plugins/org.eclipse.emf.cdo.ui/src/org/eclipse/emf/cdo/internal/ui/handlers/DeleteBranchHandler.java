/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.handlers;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.internal.ui.dialogs.DeleteBranchDialog;
import org.eclipse.emf.cdo.ui.AbstractAuthorizingHandler;

import org.eclipse.net4j.util.om.monitor.EclipseMonitor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 */
public class DeleteBranchHandler extends AbstractAuthorizingHandler<CDOBranch>
{
  private CDOBranch branch;

  public DeleteBranchHandler()
  {
    super(CDOBranch.class, "org.eclipse.emf.cdo.ui.DeleteBranches");
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    if (elements.size() == 1)
    {
      branch = elements.get(0);

      Shell shell = HandlerUtil.getActiveShell(event);
      DeleteBranchDialog dialog = new DeleteBranchDialog(shell, branch);
      if (dialog.open() == DeleteBranchDialog.OK)
      {
        return;
      }
    }

    branch = null;
    cancel();
  }

  @Override
  protected void doExecute(IProgressMonitor monitor) throws Exception
  {
    branch.delete(new EclipseMonitor(monitor));
  }

  @Override
  protected String getErrorMessage(Exception ex)
  {
    return "Branch " + branch.getName() + " could not be deleted.";
  }
}
