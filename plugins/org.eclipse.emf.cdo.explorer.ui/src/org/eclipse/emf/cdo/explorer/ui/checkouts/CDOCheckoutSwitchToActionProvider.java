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
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.actions.CreateBranchAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutSwitchToActionProvider extends CommonActionProvider
{
  private ICommonViewerWorkbenchSite viewSite;

  @Override
  public void init(ICommonActionExtensionSite aConfig)
  {
    if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite)
    {
      viewSite = (ICommonViewerWorkbenchSite)aConfig.getViewSite();
    }
  }

  @Override
  public void fillContextMenu(IMenuManager menu)
  {
    if (viewSite == null)
    {
      return;
    }

    IStructuredSelection selection = (IStructuredSelection)getContext().getSelection();
    if (selection.size() != 1)
    {
      return;
    }

    Object selectedElement = selection.getFirstElement();
    addSwitchToActions(viewSite.getPage(), menu, selectedElement);
  }

  public static void addSwitchToActions(IWorkbenchPage page, IMenuManager menu, Object selectedElement)
  {
    List<IAction> actions = new ArrayList<IAction>();

    if (selectedElement instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)selectedElement;
      if (checkout.isOpen())
      {
        actions.add(new SwitchToNewBranchAction(page, checkout));
      }
    }

    if (!actions.isEmpty())
    {
      IMenuManager submenu = new MenuManager("Switch To", ICommonMenuConstants.GROUP_OPEN_WITH);
      submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

      for (IAction action : actions)
      {
        submenu.add(action);
      }

      submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
      menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
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
      String name = getName();

      CDOBranch branch = base.getBranch().getBranch(name);
      checkout.setBranchID(branch.getID());
    }
  }
}
