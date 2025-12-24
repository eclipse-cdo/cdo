/*
 * Copyright (c) 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.actions.CreateBranchAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class SwitchToActionProvider extends AbstractBranchPointActionProvider
{
  private static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.SwitchToActions";

  public SwitchToActionProvider()
  {
    super(ID, "Switch To");
  }

  @Override
  protected void fillSubMenu(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout)
  {
    if (CDOCheckout.TYPE_ONLINE_TRANSACTIONAL.equals(checkout.getType()))
    {
      subMenu.add(new SwitchToNewBranchAction(page, checkout));
    }

    super.fillSubMenu(page, subMenu, checkout);
  }

  @Override
  protected void fillHistorizedAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout, CDOBranchPoint branchPoint)
  {
    if (checkout.isReadOnly() || branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      super.fillHistorizedAction(page, subMenu, checkout, branchPoint);
    }
  }

  @Override
  protected void fillOtherBranchAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout)
  {
    if (!checkout.isReadOnly())
    {
      super.fillOtherBranchAction(page, subMenu, checkout);
    }
  }

  @Override
  protected void fillOtherBranchPointAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout)
  {
    if (checkout.isReadOnly())
    {
      super.fillOtherBranchPointAction(page, subMenu, checkout);
    }
  }

  @Override
  protected void fillCommitAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout)
  {
    if (checkout.isReadOnly())
    {
      super.fillCommitAction(page, subMenu, checkout);
    }
  }

  @Override
  protected void fillOtherCheckoutAction(IWorkbenchPage page, IMenuManager subMenu, CDOCheckout checkout, CDOCheckout otherCheckout)
  {
    if (checkout.isReadOnly() == otherCheckout.isReadOnly())
    {
      super.fillOtherCheckoutAction(page, subMenu, checkout, otherCheckout);
    }
  }

  @Override
  protected String getHistorizedBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Switch to this branch point" : "Switch to this branch";
  }

  @Override
  protected String getOtherBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Select a branch point and switch to it" : "Select a branch and switch to it";
  }

  @Override
  protected String getCommitBranchPointToolTip()
  {
    return "Select a commit and switch to it";
  }

  @Override
  protected String getOtherCheckoutToolTip()
  {
    return "Switch to the branch point of this checkout";
  }

  @Override
  protected void execute(CDOCheckout checkout, CDOBranchPoint branchPoint) throws Exception
  {
    switchTo(checkout, branchPoint);
  }

  public static void switchTo(CDOCheckout checkout, CDOBranchPoint branchPoint)
  {
    checkout.setBranchPoint(branchPoint);
  }

  /**
   * @author Eike Stepper
   */
  private static class SwitchToNewBranchAction extends CreateBranchAction
  {
    private static final String ID = OM.BUNDLE_ID + ".SwitchToNewBranchAction"; //$NON-NLS-1$

    private final CDOCheckout checkout;

    public SwitchToNewBranchAction(IWorkbenchPage page, CDOCheckout checkout)
    {
      super(page, checkout.getView());
      this.checkout = checkout;
      setId(ID);

      setText("New Branch...");
      setImageDescriptor(OM.getImageDescriptor("icons/branch.gif"));
      setToolTipText("Create a new branch and switch this checkout to it");
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      super.doRun(progressMonitor);

      CDOBranchPoint base = getBase();
      CDOBranch newBranch = base.getBranch().getBranch(getName());
      checkout.setBranchPoint(newBranch.getHead());
    }
  }
}
