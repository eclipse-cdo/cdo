/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation 
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.BranchSelectionDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Victor Roldan Betancort
 */
public class ChangeViewTargetAction extends ViewAction
{
  public static final String ID = "change-target"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("ChangeViewTargetAction_0"); //$NON-NLS-1$

  private CDOBranchPoint targetBranchPoint;

  public ChangeViewTargetAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, Messages.getString("ChangeViewTargetAction.0"), null, view); //$NON-NLS-1$
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    BranchSelectionDialog dialog = new BranchSelectionDialog(getPage(), getView());
    if (dialog.open() == Dialog.OK)
    {
      targetBranchPoint = dialog.getTargetBranchPoint();
      if (targetBranchPoint == null)
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
    view.setBranchPoint(targetBranchPoint.getBranch(), targetBranchPoint.getTimeStamp());
  }
}
