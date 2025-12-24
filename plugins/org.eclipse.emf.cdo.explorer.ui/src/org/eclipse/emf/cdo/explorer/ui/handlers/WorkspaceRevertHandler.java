/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.explorer.ui.ObjectListController;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class WorkspaceRevertHandler extends AbstractBaseHandler<OfflineCDOCheckout>
{
  private List<CDOTransaction> dirtyTransactions;

  public WorkspaceRevertHandler()
  {
    super(OfflineCDOCheckout.class, null);
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    boolean result = super.updateSelection(selection);

    if (result)
    {
      OfflineCDOCheckout checkout = elements.get(0);
      if (!checkout.isDirty())
      {
        return false;
      }
    }

    return result;
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    dirtyTransactions = new ArrayList<>();

    InternalCDOWorkspace workspace = getWorkspace();
    if (workspace != null)
    {
      for (InternalCDOView view : workspace.getViews())
      {
        if (view.isDirty())
        {
          dirtyTransactions.add((CDOTransaction)view);
        }
      }

      Shell shell = HandlerUtil.getActiveShell(event);

      int size = dirtyTransactions.size();
      if (size != 0)
      {
        String plural = size == 1 ? "" : "s";
        String message = size == 1 ? "is 1" : "are " + size;

        if (!MessageDialog.openQuestion(shell, "Uncommitted Transaction" + plural,
            "There " + message + " uncommitted transaction" + plural + ".\n\n" + "Are you sure you want to rollback the transaction" + plural + ", too?"))
        {
          cancel();
          return;
        }
      }

      OfflineCDOCheckout checkout = elements.get(0);
      CDOChangeSetData revertData = workspace.getLocalChanges(false);

      RevertConfirmationDialog dialog = new RevertConfirmationDialog(shell, checkout, revertData);
      if (dialog.open() == RevertConfirmationDialog.OK)
      {
        return;
      }
    }

    cancel();
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    try
    {
      for (CDOTransaction transaction : dirtyTransactions)
      {
        transaction.rollback();
      }
    }
    finally
    {
      dirtyTransactions = null;
    }

    InternalCDOWorkspace workspace = getWorkspace();
    if (workspace != null)
    {
      // TODO Use progress monitor.
      workspace.revert();
    }
  }

  private InternalCDOWorkspace getWorkspace()
  {
    OfflineCDOCheckout checkout = elements.get(0);
    return checkout.getWorkspace();
  }

  /**
   * @author Eike Stepper
   */
  private static class RevertConfirmationDialog extends TitleAreaDialog
  {
    private static final String TITLE = "Revert Local Changes";

    private final OfflineCDOCheckout checkout;

    private final CDOChangeSetData revertData;

    public RevertConfirmationDialog(Shell parentShell, OfflineCDOCheckout checkout, CDOChangeSetData revertData)
    {
      super(parentShell);
      this.checkout = checkout;
      this.revertData = revertData;

      setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Composite area = (Composite)super.createDialogArea(parent);

      getShell().setText(TITLE);
      setTitle(TITLE);
      setTitleImage(OM.getImage("icons/wiz/revert.gif"));
      setMessage("Are you sure you want to revert your local changes?");

      Composite container = new Composite(area, SWT.NONE);
      container.setLayoutData(new GridData(GridData.FILL_BOTH));
      GridLayout containerGridLayout = new GridLayout();
      containerGridLayout.marginWidth = 10;
      containerGridLayout.marginHeight = 10;
      container.setLayout(containerGridLayout);

      ObjectListController objectListController = new ObjectListController(checkout);
      CDOView view = checkout.getView();

      for (CDORevisionKey key : revertData.getChangedObjects())
      {
        CDOObject object = view.getObject(key.getID());
        objectListController.addObject(object, false);
      }

      for (CDOIDAndVersion key : revertData.getDetachedObjects())
      {
        CDOObject object = view.getObject(key.getID());
        objectListController.addObject(object, true);
      }

      TreeViewer treeViewer = new TreeViewer(container);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      objectListController.configure(treeViewer);

      return area;
    }

    @Override
    protected Point getInitialSize()
    {
      return new Point(500, 400);
    }
  }
}
