/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.LegacyModeRegistry;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectBranchPointDialog;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 */
public abstract class MergeAction implements IObjectActionDelegate
{
  private final boolean allowTimeStamp;

  private IWorkbenchPart targetPart;

  private CDOTransaction leftView;

  public MergeAction(boolean allowTimeStamp)
  {
    this.allowTimeStamp = allowTimeStamp;
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    this.targetPart = targetPart;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    leftView = null;
    if (selection instanceof IStructuredSelection)
    {
      Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
      if (selectedElement instanceof CDOTransaction)
      {
        leftView = (CDOTransaction)selectedElement;
      }
    }
  }

  public void run(IAction action)
  {
    if (leftView != null)
    {
      SelectBranchPointDialog dialog = new SelectBranchPointDialog(targetPart.getSite().getPage(),
          leftView.getSession(), leftView, allowTimeStamp);
      if (dialog.open() == SelectBranchPointDialog.OK)
      {
        CDOView rightView = openView(dialog.getBranchPoint());
        CDOView[] originView = { null };

        try
        {
          CDOCompareEditorUtil.openDialog(leftView, rightView, originView);
        }
        finally
        {
          LifecycleUtil.deactivate(originView[0]);
          if (!rightView.isDirty())
          {
            LifecycleUtil.deactivate(rightView);
          }
        }
      }
    }
  }

  private CDOView openView(CDOBranchPoint branchPoint)
  {
    CDOSession session = leftView.getSession();
    if (branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      CDOUtil.setLegacyModeDefault(LegacyModeRegistry.isLegacyEnabled(session));
      return session.openTransaction(branchPoint.getBranch());
    }

    return session.openView(branchPoint);
  }

  /**
   * @author Eike Stepper
   */
  public static class FromBranch extends MergeAction
  {
    public FromBranch()
    {
      super(false);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FromBranchPoint extends MergeAction
  {
    public FromBranchPoint()
    {
      super(true);
    }
  }
}
