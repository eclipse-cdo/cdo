/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.util.LMMerger;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionOpener;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.InputHolder;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewOpener;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public final class InteractiveDeliveryMerger implements LMMerger
{
  public InteractiveDeliveryMerger()
  {
  }

  @Override
  public long mergeDelivery(CDOSession session, CDOBranchPoint sourceBranchPoint, CDOBranch targetBranch)
  {
    CDOBranchPoint targetBranchPoint = targetBranch.getHead();
    CDOView[] views = new CDOView[3]; // Origin + left + right

    CDOViewOpener viewOpener = new CDOViewOpener()
    {
      @Override
      public CDOView openView(CDOBranchPoint target, ResourceSet resourceSet)
      {
        return views[1] = session.openView(target, resourceSet);
      }

      @Override
      public CDOView openView(String durableLockingID, ResourceSet resourceSet)
      {
        throw new UnsupportedOperationException();
      }
    };

    CDOTransactionOpener transactionOpener = new CDOTransactionOpener()
    {
      @Override
      public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
      {
        return (CDOTransaction)(views[2] = session.openTransaction(target, resourceSet));
      }

      @Override
      public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
      {
        throw new UnsupportedOperationException();
      }
    };

    try
    {
      InputHolder inputHolder = new InputHolder();
      CDOCompareEditorUtil.setInputConsumer(inputHolder);

      if (CDOCompareEditorUtil.openDialog(viewOpener, transactionOpener, sourceBranchPoint, targetBranchPoint, views))
      {
        return inputHolder.getInput().getCommitInfo().getTimeStamp();
      }
    }
    finally
    {
      CDOCompareEditorUtil.setInputConsumer(null);

      for (CDOView view : views)
      {
        LifecycleUtil.deactivate(view);
      }
    }

    return CDOBranchPoint.INVALID_DATE;
  }
}
