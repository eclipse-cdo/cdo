/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.LabelProvider;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPageSite;

/**
 * @author Eike Stepper
 */
public class CDOHistoryPage extends HistoryPage
{
  private CommitHistoryComposite commitHistoryComposite;

  private boolean commitOnDoubleClick;

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
          public void run()
          {
            CDOHistoryPage.this.setInput(null);
          }
        });
      }
    }
  };

  public CDOHistoryPage()
  {
  }

  public String getName()
  {
    return input != null ? input.toString() : null;
  }

  public String getDescription()
  {
    return "";
  }

  @Override
  public CommitHistoryComposite getControl()
  {
    return commitHistoryComposite;
  }

  @Override
  public void createControl(Composite parent)
  {
    commitHistoryComposite = new CommitHistoryComposite(parent, SWT.NONE)
    {
      @Override
      protected void doubleClicked(CDOCommitInfo commitInfo)
      {
        if (commitOnDoubleClick)
        {
          testCommit(commitInfo);
        }
        else
        {
          long previousTimeStamp = commitInfo.getPreviousTimeStamp();
          if (previousTimeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
          {
            CDOSession session = input.getSession();
            CDOBranchPoint previous = CDOBranchUtil.normalizeBranchPoint(commitInfo.getBranch(), previousTimeStamp);
            CDOCompareEditorUtil.openCompareDialog(session, commitInfo, previous);
          }
        }
      }

      private void testCommit(CDOCommitInfo commitInfo)
      {
        CDOTransaction transaction = null;

        try
        {
          CDOSession session = input.getSession();
          CDOBranch branch = commitInfo.getBranch();

          final long[] lastCommitTime = { 0 };
          CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
          commitInfoManager.getCommitInfos(branch, Long.MAX_VALUE, null, null, -1, new CDOCommitInfoHandler()
          {
            public void handleCommitInfo(CDOCommitInfo commitInfo)
            {
              lastCommitTime[0] = commitInfo.getTimeStamp();
            }
          });

          long timeStamp = commitInfo.getTimeStamp();
          if (timeStamp != lastCommitTime[0])
          {
            String name = "branch-" + (timeStamp - session.getRepositoryInfo().getCreationTime()) / 1000;
            branch = branch.createBranch(name, timeStamp);
          }

          transaction = session.openTransaction(branch);
          new CDOTransactionCommentator(transaction);

          CDOResourceFolder folder = transaction.getOrCreateResourceFolder("test");
          folder.addResource("resource-" + folder.getNodes().size());
          transaction.commit();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
        finally
        {
          LifecycleUtil.deactivate(transaction);
        }
      }
    };

    IPageSite site = getSite();
    site.setSelectionProvider(commitHistoryComposite.getTableViewer());

    IActionBars actionBars = site.getActionBars();
    setupToolBar(actionBars.getToolBarManager());
    setupViewMenu(actionBars.getMenuManager());
  }

  @Override
  public void setFocus()
  {
    commitHistoryComposite.setFocus();
  }

  public void refresh()
  {
    commitHistoryComposite.refreshLayout();
  }

  public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
  {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

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
  }

  protected void setupViewMenu(IMenuManager manager)
  {
    manager.add(new Action("Format Time Stamps", SWT.CHECK)
    {
      {
        LabelProvider labelProvider = commitHistoryComposite.getLabelProvider();
        setChecked(labelProvider.isFormatTimeStamps());
      }

      @Override
      public void run()
      {
        LabelProvider labelProvider = commitHistoryComposite.getLabelProvider();
        labelProvider.setFormatTimeStamps(!labelProvider.isFormatTimeStamps());

        TableViewer tableViewer = commitHistoryComposite.getTableViewer();
        tableViewer.refresh(true);
      }
    });

    manager.add(new Action("Test Commit on Double Click", SWT.CHECK)
    {
      @Override
      public void run()
      {
        commitOnDoubleClick = !commitOnDoubleClick;
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
    catch (IllegalStateException ex)
    {
      return false;
    }
  }
}
