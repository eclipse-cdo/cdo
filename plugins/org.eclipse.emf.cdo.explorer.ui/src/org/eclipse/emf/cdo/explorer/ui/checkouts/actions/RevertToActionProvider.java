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
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RevertToActionProvider extends AbstractBranchPointActionProvider
{
  private static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.RevertToActions";

  public RevertToActionProvider()
  {
    super(ID, "Revert To");
  }

  @Override
  protected void fillHistorizedAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout, CDOBranchPoint branchPoint)
  {
    if (CDOBranchUtil.isContainedBy(branchPoint, checkout.getBranchPoint()))
    {
      super.fillHistorizedAction(page, subMenu, checkout, branchPoint);
    }
  }

  @Override
  protected void fillOtherBranchAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout)
  {
    // Do nothing.
  }

  @Override
  protected void fillOtherCheckoutAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout, CDOCheckout otherCheckout)
  {
    if (CDOBranchUtil.isContainedBy(otherCheckout.getBranchPoint(), checkout.getBranchPoint()))
    {
      super.fillOtherCheckoutAction(page, subMenu, checkout, otherCheckout);
    }
  }

  @Override
  protected String getHistorizedBranchPointToolTip(boolean allowTimeStamp)
  {
    return "Revert to this branch point";
  }

  @Override
  protected String getOtherBranchPointToolTip(boolean allowTimeStamp)
  {
    return "Select a branch point and revert to it";
  }

  @Override
  protected String getCommitBranchPointToolTip()
  {
    return "Select a commit and revert to it";
  }

  @Override
  protected String getOtherCheckoutToolTip()
  {
    return "Revert to the branch point of this checkout";
  }

  @Override
  protected void execute(CDOCheckout checkout, CDOBranchPoint branchPoint) throws Exception
  {
    revertTo(checkout, branchPoint);
  }

  public static void revertTo(CDOCheckout checkout, CDOBranchPoint branchPoint) throws ConcurrentAccessException, CommitException
  {
    CDOTransaction transaction = checkout.openTransaction();

    try
    {
      transaction.revertTo(branchPoint);

      StringBuilder commentBuilder = new StringBuilder("Revert to ");
      CDOTransactionCommentator.appendBranchPoint(commentBuilder, branchPoint);
      transaction.setCommitComment(commentBuilder.toString());

      transaction.commit();
    }
    finally
    {
      transaction.close();
    }
  }
}
