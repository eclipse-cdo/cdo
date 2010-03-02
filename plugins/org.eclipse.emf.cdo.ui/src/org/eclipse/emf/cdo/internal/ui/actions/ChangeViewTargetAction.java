/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.internal.ui.dialogs.BranchSelectionDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ChangeViewTargetAction extends ViewAction
{
  public static final String ID = "change-target"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("ChangeViewTargetAction_0"); //$NON-NLS-1$

  private CDOBranch targerBranch;

  public ChangeViewTargetAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, "Change the target CDOBranch of a CDOView", null, view);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    BranchSelectionDialog dialog = new BranchSelectionDialog(getPage(), getView());
    if (dialog.open() == Dialog.OK)
    {
      targerBranch = dialog.getTargetBranch();
      if (targerBranch == null)
      {
        cancel();
      }
    }
    else
    {
      cancel();
    }

    super.preRun();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOView view = getView();
    view.setBranch(targerBranch);
  }
}
