/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.team.history;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.internal.ui.history.Net;
import org.eclipse.emf.cdo.internal.ui.history.NetRenderer;
import org.eclipse.emf.cdo.internal.ui.history.Track;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.internal.team.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.team.history.DropConfirmationDialog.Operation;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input.IllegalInputException;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.LabelProvider;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.StackComposite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;

/**
 * @author Eike Stepper
 */
public class CDOHistoryPage extends HistoryPage
{
  private static final String POPUP_ID = "#PopupMenu";

  private static final boolean DEBUG = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.team.history.debug");

  private static final boolean TEST = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.team.history.test");

  private StackComposite stackComposite;

  private Control offlineControl;

  private CommitHistoryComposite commitHistoryComposite;

  private Input input;

  private IListener inputListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      if (!commitHistoryComposite.isDisposed())
      {
        commitHistoryComposite.getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            if (!commitHistoryComposite.isDisposed())
            {
              CDOHistoryPage.this.setInput(null);
            }
          }
        });
      }
    }
  };

  public CDOHistoryPage()
  {
  }

  @Override
  public String getName()
  {
    return input != null ? input.toString() : null;
  }

  @Override
  public String getDescription()
  {
    return "";
  }

  @Override
  public Control getControl()
  {
    return stackComposite;
  }

  @Override
  public void createControl(Composite parent)
  {
    stackComposite = new StackComposite(parent, SWT.NONE);

    Label label = new Label(stackComposite, SWT.NONE);
    label.setText("The selected session belongs to an offline workspace.\nThe remote history cannot be provided.");
    label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
    offlineControl = label;

    commitHistoryComposite = new CommitHistoryComposite(stackComposite, SWT.NONE)
    {
      @Override
      protected void doubleClicked(CDOCommitInfo commitInfo)
      {
        if (TEST)
        {
          new TransactionalBranchPointOperation()
          {
            @Override
            protected void run(CDOTransaction transaction)
            {
              CDOResourceFolder folder = transaction.getOrCreateResourceFolder("test");
              folder.addResource("resource-" + folder.getNodes().size());
            }
          }.execute(commitInfo);
        }
      }
    };

    stackComposite.setTopControl(commitHistoryComposite);

    IPageSite site = getSite();
    final TableViewer tableViewer = commitHistoryComposite.getTableViewer();

    UIUtil.addDragSupport(tableViewer);

    tableViewer.addDropSupport(DND.DROP_MOVE, new Transfer[] { LocalSelectionTransfer.getTransfer() }, new ViewerDropAdapter(tableViewer)
    {
      {
        // We don't want it to look like you can insert new elements, only drop onto existing elements.
        setFeedbackEnabled(false);
      }

      @Override
      public boolean validateDrop(Object target, int operation, TransferData transferType)
      {
        if (target instanceof CDOCommitInfo && LocalSelectionTransfer.getTransfer().isSupportedType(transferType))
        {
          CDOCommitInfo commitInfo = (CDOCommitInfo)target;
          CDOBranch branch = commitInfo.getBranch();

          CDOCommitInfoManager commitInfoManager = commitInfo.getCommitInfoManager();
          long lastCommitOfBranch = commitInfoManager.getLastCommitOfBranch(branch, true);
          if (commitInfo.getTimeStamp() == lastCommitOfBranch)
          {
            return true;
          }
        }

        return false;
      }

      @Override
      public boolean performDrop(Object data)
      {
        CDOCommitInfo sourcePoint = UIUtil.getElement((ISelection)data, CDOCommitInfo.class);
        CDOBranch targetBranch = ((CDOCommitInfo)getCurrentTarget()).getBranch();

        Shell shell = stackComposite.getShell();

        DropConfirmationDialog dialog = new DropConfirmationDialog(shell, sourcePoint, targetBranch);
        if (dialog.open() != DropConfirmationDialog.OK)
        {
          return false;
        }

        Operation operation = dialog.getOperation();
        String label = StringUtil.capAll(operation.getLabel());
        CDOSession session = CDOUtil.getSession(targetBranch);

        if (operation == Operation.MERGE_FROM)
        {
          CDOCompareEditorUtil.openEditor(session, session, sourcePoint, targetBranch.getHead(), null, true);
        }
        else
        {
          new Job(label)
          {
            @Override
            protected IStatus run(IProgressMonitor progressMonitor)
            {
              CDOTransaction transaction = null;

              try
              {

                transaction = session.openTransaction(targetBranch);
                CDOUtil.configureView(transaction);

                transaction.revertTo(sourcePoint);
                CDOTransactionCommentator.setRevertComment(transaction, sourcePoint);

                transaction.commit(progressMonitor);
              }
              catch (Exception ex)
              {
                OM.LOG.error(ex);

                String message = ex.getMessage();
                Status status = new Status(IStatus.ERROR, OM.BUNDLE_ID, message, ex);
                UIUtil.asyncExec(() -> ErrorDialog.openError(shell, label, message, status));
              }
              finally
              {
                transaction.close();
              }

              return Status.OK_STATUS;
            }
          }.schedule();
        }

        return true;
      }
    });

    if (DEBUG)
    {
      ((LabelProvider)tableViewer.getLabelProvider()).setFormatTimeStamps(false);
    }

    MenuManager menuManager = new MenuManager(POPUP_ID);
    menuManager.add(new Separator("compare"));
    menuManager.add(new Separator("branching"));
    menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    Menu menu = menuManager.createContextMenu(tableViewer.getControl());
    tableViewer.getControl().setMenu(menu);

    site.registerContextMenu(POPUP_ID, menuManager, tableViewer);
    site.setSelectionProvider(tableViewer);

    IActionBars actionBars = site.getActionBars();
    setupToolBar(actionBars.getToolBarManager());
    setupViewMenu(actionBars.getMenuManager());
  }

  @Override
  public void setFocus()
  {
    commitHistoryComposite.setFocus();
  }

  @Override
  public void refresh()
  {
    commitHistoryComposite.refreshLayout(true);
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public boolean isValidInput(Object object)
  {
    return canShowHistoryFor(object);
  }

  @Override
  public boolean inputSet()
  {
    if (input != null)
    {
      input.removeListener(inputListener);
      input.deactivate();
      input = null;
    }

    Object object = getInput();

    try
    {
      input = new CommitHistoryComposite.Input(object);
      input.addListener(inputListener);
      input.activate();

      if (input.isOffline())
      {
        stackComposite.setTopControl(offlineControl);
      }
      else
      {
        stackComposite.setTopControl(commitHistoryComposite);
      }

      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
    finally
    {
      commitHistoryComposite.setInput(input);
    }
  }

  @Override
  public void dispose()
  {
    if (input != null)
    {
      input.deactivate();
      input = null;
    }

    super.dispose();
  }

  protected void setupToolBar(IToolBarManager manager)
  {
    if (DEBUG)
    {
      Action action = new Action("DEBUG", IAction.AS_PUSH_BUTTON)
      {
        @SuppressWarnings("unused")
        @Override
        public void run()
        {
          NetRenderer netRenderer = (NetRenderer)ReflectUtil.getValue(ReflectUtil.getField(CommitHistoryComposite.class, "netRenderer"),
              commitHistoryComposite);

          Net net = netRenderer.getNet();
          Track[] tracks = net.getTracks();
          CDOSession session = net.getSession();
          System.out.println("Debug " + net); // Set a breakpoint on this line to inspect the net.
        }
      };

      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      action.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
      manager.add(action);
    }
  }

  protected void setupViewMenu(IMenuManager manager)
  {
    manager.add(new TableRedrawingAction("Format Time Stamps", SWT.CHECK)
    {
      @Override
      protected boolean getInitialCheckState(LabelProvider labelProvider)
      {
        return labelProvider.isFormatTimeStamps();
      }

      @Override
      protected void doRun(LabelProvider labelProvider)
      {
        labelProvider.setFormatTimeStamps(!labelProvider.isFormatTimeStamps());
      }
    });

    manager.add(new TableRedrawingAction("Shorten Branch Paths", SWT.CHECK)
    {
      @Override
      protected boolean getInitialCheckState(LabelProvider labelProvider)
      {
        return labelProvider.isShortenBranchPaths();
      }

      @Override
      protected void doRun(LabelProvider labelProvider)
      {
        labelProvider.setShortenBranchPaths(!labelProvider.isShortenBranchPaths());
      }
    });
  }

  public static boolean canShowHistoryFor(Object object)
  {
    if (object == null)
    {
      return false;
    }

    try
    {
      new CommitHistoryComposite.Input(object);
      return true;
    }
    catch (IllegalInputException ex)
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class TableRedrawingAction extends Action
  {
    private final LabelProvider labelProvider = commitHistoryComposite.getLabelProvider();

    public TableRedrawingAction(String text, int style)
    {
      super(text, style);
      setChecked(getInitialCheckState(labelProvider));
    }

    @Override
    public void run()
    {
      doRun(labelProvider);
      commitHistoryComposite.getTableViewer().getTable().redraw();
    }

    protected abstract void doRun(LabelProvider labelProvider);

    protected abstract boolean getInitialCheckState(LabelProvider labelProvider);
  }

  /**
   * @author Eike Stepper
   */
  private abstract class TransactionalBranchPointOperation
  {
    public boolean execute(CDOBranchPoint branchPoint)
    {
      CDOTransaction transaction = null;

      try
      {
        CDOSession session = input.getSession();
        CDOBranch branch = branchPoint.getBranch();

        final long[] lastCommitTime = { 0 };
        CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
        commitInfoManager.getCommitInfos(branch, Long.MAX_VALUE, null, null, -1, new CDOCommitInfoHandler()
        {
          @Override
          public void handleCommitInfo(CDOCommitInfo commitInfo)
          {
            lastCommitTime[0] = commitInfo.getTimeStamp();
          }
        });

        long timeStamp = branchPoint.getTimeStamp();
        if (timeStamp != lastCommitTime[0])
        {
          String name = "branch" + (timeStamp - session.getRepositoryInfo().getCreationTime()) / 1000;
          branch = branch.createBranch(name, timeStamp);
        }

        transaction = session.openTransaction(branch);
        CDOUtil.configureView(transaction);

        try
        {
          run(transaction);
          transaction.commit();
          return true;
        }
        catch (Throwable ex)
        {
          ex.printStackTrace();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      finally
      {
        LifecycleUtil.deactivate(transaction);
      }

      return false;
    }

    protected abstract void run(CDOTransaction transaction) throws Exception;
  }
}
