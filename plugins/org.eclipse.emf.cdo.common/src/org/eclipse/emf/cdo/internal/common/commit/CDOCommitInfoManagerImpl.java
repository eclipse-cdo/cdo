/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Andre Dietisheim
 */
public class CDOCommitInfoManagerImpl extends Lifecycle implements InternalCDOCommitInfoManager
{
  private CommitInfoLoader commitInfoLoader;

  private Map<CDOCommitHistory, Boolean> histories = new WeakHashMap<CDOCommitHistory, Boolean>();

  public CDOCommitInfoManagerImpl()
  {
  }

  public CommitInfoLoader getCommitInfoLoader()
  {
    return commitInfoLoader;
  }

  public void setCommitInfoLoader(CommitInfoLoader commitInfoLoader)
  {
    checkInactive();
    this.commitInfoLoader = commitInfoLoader;
  }

  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, CDOCommitData commitData)
  {
    checkActive();
    return new CDOCommitInfoImpl(this, branch, timeStamp, previousTimeStamp, userID, comment, commitData);
  }

  public CDOCommitHistory getHistory()
  {
    return getHistory(null);
  }

  public CDOCommitHistory getHistory(CDOBranch branch)
  {
    synchronized (histories)
    {
      for (CDOCommitHistory history : histories.keySet())
      {
        if (history.getBranch() == branch)
        {
          return history;
        }
      }

      CDOCommitHistory history = new CDOCommitHistory(this, branch);
      histories.put(history, Boolean.TRUE);
      return history;
    }
  }

  public CDOCommitInfo getCommitInfo(long timeStamp)
  {
    checkActive();
    final CDOCommitInfo[] result = { null };
    getCommitInfos(null, timeStamp, timeStamp, new CDOCommitInfoHandler()
    {
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        result[0] = commitInfo;
      }
    });

    return result[0];
  }

  public void getCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    checkActive();
    commitInfoLoader.loadCommitInfos(branch, startTime, endTime, handler);
  }

  public void getCommitInfos(CDOBranch branch, long startTime, String userID, String comment, int count,
      CDOCommitInfoHandler handler)
  {
    checkActive();

    if (userID != null || comment != null)
    {
      throw new IllegalArgumentException("The parameters userID and comment are not supported");
    }

    long endTime = CDOCommitInfoUtil.encodeCount(count);
    commitInfoLoader.loadCommitInfos(branch, startTime, endTime, handler);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(commitInfoLoader, "commitInfoLoader"); //$NON-NLS-1$
  }
}
