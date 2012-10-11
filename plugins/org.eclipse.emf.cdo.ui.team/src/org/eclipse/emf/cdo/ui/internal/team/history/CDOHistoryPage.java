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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.ui.history.HistoryPage;

/**
 * @author Eike Stepper
 */
public class CDOHistoryPage extends HistoryPage
{
  private CommitHistoryComposite commitHistoryComposite;

  private Input input;

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
            branch = branch
                .createBranch("branch-" + (timeStamp - session.getRepositoryInfo().getCreationTime()) / 1000);
          }

          transaction = session.openTransaction(branch);
          CDOResourceFolder folder = transaction.getOrCreateResourceFolder("/folder");
          folder.addResource("folder-" + folder.getNodes().size() + 1);
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

        // long previousTimeStamp = commitInfo.getPreviousTimeStamp();
        // if (previousTimeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        // {
        // CDOSession session = input.getSession();
        // CDOBranchPoint previous = CDOBranchUtil.normalizeBranchPoint(commitInfo.getBranch(), previousTimeStamp);
        // CDOCompareEditorInput.openCompareDialog(session, commitInfo, previous);
        // }
      }
    };

    getSite().setSelectionProvider(commitHistoryComposite.getTableViewer());
  }

  @Override
  public void setFocus()
  {
    commitHistoryComposite.setFocus();
  }

  public void refresh()
  {
    commitHistoryComposite.getTableViewer().refresh();
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
    Object object = getInput();

    try
    {
      input = new CommitHistoryComposite.Input(object);
      return true;
    }
    catch (IllegalStateException ex)
    {
      input = null;
      return false;
    }
    finally
    {
      commitHistoryComposite.setInput(input);
    }
  }

  public static boolean canShowHistoryFor(Object object)
  {
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
