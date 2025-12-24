/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.AbstractBranchPointDialog;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class Merge extends CompareActionDelegate<CDOTransaction>
{
  private final boolean allowTimeStamp;

  public Merge(boolean allowTimeStamp)
  {
    super(CDOTransaction.class);
    this.allowTimeStamp = allowTimeStamp;
  }

  @Override
  protected void run(List<CDOTransaction> targets, IProgressMonitor progressMonitor)
  {
    if (targets.size() == 1)
    {
      Shell shell = getTargetPart().getSite().getShell();
      CDOTransaction leftView = targets.get(0);
      CDOSession session = leftView.getSession();

      CDOBranchPoint branchPoint = AbstractBranchPointDialog.select(shell, allowTimeStamp, leftView);
      if (branchPoint != null)
      {
        final CDOView rightView = openView(session, branchPoint);
        final CDOView[] originView = { null };

        CDOCompareEditorUtil.addDisposeRunnables(new Runnable()
        {
          @Override
          public void run()
          {
            LifecycleUtil.deactivate(originView[0]);
            if (!rightView.isDirty())
            {
              LifecycleUtil.deactivate(rightView);
            }
          }
        });

        CDOCompareEditorUtil.openEditor(leftView, rightView, originView, true);
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
  public static class FromBranch extends Merge
  {
    public FromBranch()
    {
      super(false);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FromBranchPoint extends Merge
  {
    public FromBranchPoint()
    {
      super(true);
    }
  }
}
