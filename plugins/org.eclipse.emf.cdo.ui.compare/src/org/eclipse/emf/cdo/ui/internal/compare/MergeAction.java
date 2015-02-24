/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectBranchPointDialog;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class MergeAction extends AbstractCompareAction<CDOTransaction>
{
  private final boolean allowTimeStamp;

  public MergeAction(boolean allowTimeStamp)
  {
    super(CDOTransaction.class);
    this.allowTimeStamp = allowTimeStamp;
  }

  @Override
  protected void run(List<CDOTransaction> targets, IProgressMonitor progressMonitor)
  {
    if (targets.size() == 1)
    {
      IWorkbenchPage page = getTargetPart().getSite().getPage();
      CDOTransaction leftView = targets.get(0);
      CDOSession session = leftView.getSession();

      SelectBranchPointDialog dialog = new SelectBranchPointDialog(page, session, leftView, allowTimeStamp);
      if (dialog.open() == SelectBranchPointDialog.OK)
      {
        CDOView rightView = openView(session, dialog.getBranchPoint());
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

  private CDOView openView(CDOSession session, CDOBranchPoint branchPoint)
  {
    if (branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
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
