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
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.net4j.util.container.Container;

import java.util.LinkedList;

/**
 * A cache for the {@link CDOCommitInfo commit infos} of a branch or of an entire repository.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOCommitHistory extends Container<CDOCommitInfo>
{
  private final CDOCommitInfoManager manager;

  private final CDOBranch branch;

  private LinkedList<CDOCommitInfo> commitInfos = new LinkedList<CDOCommitInfo>();

  private CDOCommitInfo[] elements;

  private Thread loaderThread;

  private Object loaderThreadLock = new Object();

  public CDOCommitHistory(CDOCommitInfoManager manager, CDOBranch branch)
  {
    this.manager = manager;
    this.branch = branch;
  }

  public final CDOCommitInfoManager getManager()
  {
    return manager;
  }

  public final CDOBranch getBranch()
  {
    return branch;
  }

  public CDOCommitInfo[] getElements()
  {
    synchronized (commitInfos)
    {
      if (elements == null)
      {
        elements = commitInfos.toArray(new CDOCommitInfo[commitInfos.size()]);
      }

      return elements;
    }
  }

  public boolean loadCommitInfos(final int count)
  {
    synchronized (loaderThreadLock)
    {
      if (loaderThread != null)
      {
        return false;
      }

      loaderThread = new Thread("CDOCommitHistoryLoader")
      {
        @Override
        public void run()
        {
          doLoadCommitInfos(count);

          synchronized (loaderThreadLock)
          {
            loaderThread = null;
          }
        }
      };
    }

    loaderThread.start();
    return true;
  }

  protected void doLoadCommitInfos(int count)
  {
    final long startTime;
    synchronized (commitInfos)
    {
      startTime = commitInfos.isEmpty() ? CDOBranchPoint.UNSPECIFIED_DATE : commitInfos.getLast().getTimeStamp();
    }

    manager.getCommitInfos(branch, startTime, null, null, -count, new CDOCommitInfoHandler()
    {
      private boolean ignore = startTime != CDOBranchPoint.UNSPECIFIED_DATE;

      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        if (ignore)
        {
          ignore = false;
          return;
        }

        synchronized (commitInfos)
        {
          commitInfos.addLast(commitInfo);
          elements = null;
        }

        fireElementAddedEvent(commitInfo);
      }
    });
  }
}
