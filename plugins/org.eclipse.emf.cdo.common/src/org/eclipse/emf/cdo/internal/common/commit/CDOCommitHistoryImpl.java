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
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.internal.common.bundle.OM;

import org.eclipse.net4j.util.collection.GrowingRandomAccessList;
import org.eclipse.net4j.util.container.Container;

import java.util.ListIterator;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOCommitHistoryImpl extends Container<CDOCommitInfo> implements CDOCommitHistory
{
  private final CDOCommitInfoManager manager;

  private final CDOBranch branch;

  private int loadCount = DEFAULT_LOAD_COUNT;

  private GrowingRandomAccessList<CDOCommitInfo> commitInfos = new GrowingRandomAccessList<CDOCommitInfo>(
      CDOCommitInfo.class, DEFAULT_LOAD_COUNT);

  private CDOCommitInfo[] elements;

  private Thread loaderThread;

  private Object loaderThreadLock = new Object();

  public CDOCommitHistoryImpl(CDOCommitInfoManager manager, CDOBranch branch)
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

  public CDOCommitInfo getFirstElement()
  {
    checkActive();
    synchronized (commitInfos)
    {
      if (commitInfos.isEmpty())
      {
        return null;
      }

      return commitInfos.getFirst();
    }
  }

  public CDOCommitInfo getLastElement()
  {
    checkActive();
    synchronized (commitInfos)
    {
      if (commitInfos.isEmpty())
      {
        return null;
      }

      return commitInfos.getLast();
    }
  }

  public CDOCommitInfo getElement(int index)
  {
    checkActive();
    synchronized (commitInfos)
    {
      return commitInfos.get(index);
    }
  }

  public int size()
  {
    checkActive();
    synchronized (commitInfos)
    {
      return commitInfos.size();
    }
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

  public boolean triggerLoad()
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
          try
          {
            doLoadCommitInfos();
          }
          catch (Throwable ex)
          {
            OM.LOG.error(ex);
          }
          finally
          {
            synchronized (loaderThreadLock)
            {
              loaderThread = null;
            }
          }
        }
      };
    }

    loaderThread.start();
    return true;
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    long timeStamp = commitInfo.getTimeStamp();
    synchronized (commitInfos)
    {
      if (commitInfos.isEmpty() || timeStamp > commitInfos.getFirst().getTimeStamp())
      {
        commitInfos.addFirst(commitInfo);
      }
      else
      {
        if (timeStamp < commitInfos.getLast().getTimeStamp())
        {
          commitInfos.addLast(commitInfo);
        }
        else
        {
          for (ListIterator<CDOCommitInfo> it = commitInfos.listIterator(); it.hasNext();)
          {
            CDOCommitInfo current = it.next();
            long currentTimeStamp = current.getTimeStamp();
            if (timeStamp == currentTimeStamp)
            {
              // Ignore duplicate commit infos
              return;
            }

            if (timeStamp < currentTimeStamp)
            {
              it.add(commitInfo);
              break;
            }
          }
        }
      }

      elements = null;
    }

    fireElementAddedEvent(commitInfo);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    manager.addCommitInfoHandler(this);
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();
    triggerLoad();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    manager.removeCommitInfoHandler(this);
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

        CDOCommitHistoryImpl.this.handleCommitInfo(commitInfo);
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public static final class Empty extends Container<CDOCommitInfo> implements CDOCommitHistory
  {
    private static final CDOCommitInfo[] NO_ELEMENTS = {};

    public CDOCommitInfo[] getElements()
    {
      return NO_ELEMENTS;
    }

    public void handleCommitInfo(CDOCommitInfo commitInfo)
    {
    }

    public CDOCommitInfoManager getManager()
    {
      return null;
    }

    public CDOBranch getBranch()
    {
      return null;
    }

    public CDOCommitInfo getFirstElement()
    {
      return null;
    }

    public CDOCommitInfo getLastElement()
    {
      return null;
    }

    public CDOCommitInfo getElement(int index)
    {
      return null;
    }

    public int size()
    {
      return 0;
    }

    public int getLoadCount()
    {
      return 0;
    }

    public void setLoadCount(int loadCount)
    {
    }

    public boolean triggerLoad()
    {
      return false;
    }
  }
}
