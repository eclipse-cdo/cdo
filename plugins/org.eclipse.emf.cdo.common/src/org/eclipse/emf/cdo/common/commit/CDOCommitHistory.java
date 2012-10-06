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
public class CDOCommitHistory extends Container<CDOCommitInfo> implements CDOCommitInfoHandler
{
  public static final int DEFAULT_LOAD_COUNT = 25;

  private final CDOCommitInfoManager manager;

  private final CDOBranch branch;

  private int loadCount = DEFAULT_LOAD_COUNT;

  private LinkedList<CDOCommitInfo> commitInfos = new LinkedList<CDOCommitInfo>();

  private CDOCommitInfo[] elements;

  private Thread loaderThread;

  private Object loaderThreadLock = new Object();

  public CDOCommitHistory(CDOCommitInfoManager manager, CDOBranch branch)
  {
    super(true);
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

  public final int getLoadCount()
  {
    return loadCount;
  }

  public void setLoadCount(int loadCount)
  {
    this.loadCount = loadCount;
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    synchronized (commitInfos)
    {
      return commitInfos.isEmpty();
    }
  }

  public CDOCommitInfo[] getElements()
  {
    checkActive();
    synchronized (commitInfos)
    {
      if (elements == null)
      {
        elements = commitInfos.toArray(new CDOCommitInfo[commitInfos.size()]);
      }

      return elements;
    }
  }

  public boolean loadCommitInfos()
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
          doLoadCommitInfos();

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

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    synchronized (commitInfos)
    {
      commitInfos.addFirst(commitInfo);
      elements = null;
    }

    fireElementAddedEvent(commitInfo);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    manager.addCommitInfoHandler(this);
    loadCommitInfos();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    manager.removeCommitInfoHandler(this);

    synchronized (commitInfos)
    {
      commitInfos.clear();
      elements = null;
    }

    super.doDeactivate();
  }

  protected void doLoadCommitInfos()
  {
    final long startTime;
    synchronized (commitInfos)
    {
      startTime = commitInfos.isEmpty() ? CDOBranchPoint.UNSPECIFIED_DATE : commitInfos.getLast().getTimeStamp();
    }

    manager.getCommitInfos(branch, startTime, null, null, -loadCount, new CDOCommitInfoHandler()
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
