/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.actions.CreateBranchAction;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectCommitDialog;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Eike Stepper
 */
public abstract class SwitchToActionProvider extends AbstractActionProvider<CDOCheckout>
{
  public SwitchToActionProvider(String title)
  {
    super(CDOCheckout.class, "switch-to", title, ICommonMenuConstants.GROUP_ADDITIONS);
  }

  @Override
  protected boolean fillSubMenu(ICommonViewerWorkbenchSite viewSite, IMenuManager subMenu, CDOCheckout checkout)
  {
    if (checkout.isOpen())
    {
      IWorkbenchPage page = viewSite.getPage();

      if (checkout.isOnline())
      {
        subMenu.add(new SwitchToNewBranchAction(page, checkout));
      }

      if (checkout.getRepository().isConnected())
      {
        subMenu.add(new Separator("branch-point-history"));

        for (CDOBranchPoint branchPoint : checkout.getBranchPoints())
        {
          subMenu.add(new SwitchToBranchPointAction(page, checkout, branchPoint));
        }
      }

      subMenu.add(new Separator("other"));
      subMenu.add(new SwitchToCommitAction(page, checkout));
      return true;
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Online extends SwitchToActionProvider
  {
    public Online()
    {
      super("Switch To");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Offline extends SwitchToActionProvider
  {
    public Offline()
    {
      super("Replace With");
    }
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

  /**
   * @author Eike Stepper
   */
  private static class SwitchToBranchPointAction extends LongRunningAction
  {
    private static final String ID = OM.BUNDLE_ID + ".SwitchToBranchPointAction"; //$NON-NLS-1$

    private final CDOCheckout checkout;

    private final CDOBranchPoint branchPoint;

    public SwitchToBranchPointAction(IWorkbenchPage page, CDOCheckout checkout, CDOBranchPoint branchPoint)
    {
      super(page);
      this.checkout = checkout;
      this.branchPoint = branchPoint;
      setId(ID);

      String text = branchPoint.getBranch().getPathName();

      long timeStamp = branchPoint.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        setText(text + "  [" + CDOCommonUtil.formatTimeStamp(timeStamp) + "]");
        setToolTipText("Switch to this branch point");
        setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.OBJ_BRANCH_POINT));
      }
      else
      {
        setText(text);
        setToolTipText("Switch to this branch");
        setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.OBJ_BRANCH));
      }
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      checkout.setBranchPoint(branchPoint);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class SwitchToCommitAction extends LongRunningAction
  {
    private static final String ID = OM.BUNDLE_ID + ".SwitchToCommitAction"; //$NON-NLS-1$

    private final CDOCheckout checkout;

    private CDOCommitInfo commitInfo;

    public SwitchToCommitAction(IWorkbenchPage page, CDOCheckout checkout)
    {
      super(page, "Commit...", SharedIcons.getDescriptor(SharedIcons.OBJ_COMMIT));
      this.checkout = checkout;
      setId(ID);
      setToolTipText("Select a commit and switch to it");
    }

    @Override
    protected void preRun() throws Exception
    {
      CDORepository repository = checkout.getRepository();
      CDOSession session = repository.acquireSession();

      SelectCommitDialog dialog = new SelectCommitDialog(getPage(), session);
      if (dialog.open() == SelectCommitDialog.OK)
      {
        commitInfo = dialog.getCommitInfo();
        return;
      }

      repository.releaseSession();
      cancel();
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      try
      {
        checkout.setBranchPoint(commitInfo);
      }
      finally
      {
        checkout.getRepository().releaseSession();
      }
    }
  }
}
