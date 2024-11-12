/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.util.LMMerger2;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionOpener;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.InputHolder;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewOpener;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public class InteractiveDeliveryMerger implements LMMerger2
{
  private static final int ORIGIN = 0;

  private static final int SOURCE = 1;

  private static final int TARGET = 2;

  public InteractiveDeliveryMerger()
  {
  }

  @Override
  public long mergeDelivery(LMMergeInfos infos)
  {
    Baseline sourceBaseline = infos.getSourceBaseline();
    FloatingBaseline targetBaseline = infos.getTargetBaseline();

    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(sourceBaseline);
    if (systemDescriptor != ISystemManager.INSTANCE.getDescriptor(targetBaseline))
    {
      throw new IllegalArgumentException("Target baseline is not in system " + systemDescriptor);
    }

    CDOView[] views = new CDOView[3]; // ORIGIN, SOURCE, TARGET

    CDOViewOpener viewOpener = new CDOViewOpener()
    {
      @Override
      public CDOView openView(CDOBranchPoint branchPoint, ResourceSet resourceSet)
      {
        if (views[SOURCE] == null)
        {
          // First call: This is the SOURCE view.
          try
          {
            return views[SOURCE] = openSourceView(systemDescriptor, infos, branchPoint, resourceSet);
          }
          catch (Exception ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        if (views[ORIGIN] == null)
        {
          // Second call: This is the ORIGIN view.
          try
          {
            return views[ORIGIN] = openOriginView(systemDescriptor, infos, branchPoint, resourceSet);
          }
          catch (Exception ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        throw new IllegalStateException("Source and origin views have already been opened");
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
      public CDOTransaction openTransaction(CDOBranchPoint branchPoint, ResourceSet resourceSet)
      {
        // This is the TARGET transaction.
        try
        {
          return (CDOTransaction)(views[TARGET] = openTargetTransaction(systemDescriptor, infos, branchPoint, resourceSet));
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
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

      CDOBranchPoint sourceBranchPoint = infos.getSourceBranchPoint();
      CDOBranchPoint targetBranchPoint = infos.getTargetBranch().getHead();

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

  protected CDOView openOriginView(ISystemDescriptor systemDescriptor, LMMergeInfos infos, CDOBranchPoint branchPoint, ResourceSet resourceSet) throws Exception
  {
    CDOSession session = infos.getSession();
    return session.openView(branchPoint, resourceSet);
  }

  protected CDOView openSourceView(ISystemDescriptor systemDescriptor, LMMergeInfos infos, CDOBranchPoint branchPoint, ResourceSet resourceSet) throws Exception
  {
    CDOSession session = infos.getSession();
    return session.openView(branchPoint, resourceSet);
  }

  protected CDOTransaction openTargetTransaction(ISystemDescriptor systemDescriptor, LMMergeInfos infos, CDOBranchPoint branchPoint, ResourceSet resourceSet)
      throws Exception
  {
    CDOSession session = infos.getSession();
    return session.openTransaction(branchPoint, resourceSet);
  }
}
