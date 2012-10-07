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
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Andre Dietisheim
 */
public class CDOCommitInfoManagerImpl extends CDOCommitHistoryProviderImpl<CDOBranch, CDOCommitHistory> implements
    InternalCDOCommitInfoManager
{
  private final Map<CDOCommitInfo, CDOCommitInfo> cache;

  private final Object cacheLock = new Object();

  private CommitInfoLoader loader;

  private List<CDOCommitInfoHandler> handlers = new ArrayList<CDOCommitInfoHandler>();

  public CDOCommitInfoManagerImpl(boolean caching)
  {
    if (caching)
    {
      cache = new WeakHashMap<CDOCommitInfo, CDOCommitInfo>();
    }
    else
    {
      cache = null;
    }
  }

  public CommitInfoLoader getCommitInfoLoader()
  {
    return loader;
  }

  public void setCommitInfoLoader(CommitInfoLoader commitInfoLoader)
  {
    checkInactive();
    loader = commitInfoLoader;
  }

  public CDOCommitInfoHandler[] getCommitInfoHandlers()
  {
    synchronized (handlers)
    {
      return handlers.toArray(new CDOCommitInfoHandler[handlers.size()]);
    }
  }

  /**
   * @since 4.0
   */
  public void addCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    synchronized (handlers)
    {
      handlers.add(handler);
    }
  }

  /**
   * @since 4.0
   */
  public void removeCommitInfoHandler(CDOCommitInfoHandler handler)
  {
    synchronized (handlers)
    {
      handlers.remove(handler);
    }
  }

  public void notifyCommitInfoHandlers(CDOCommitInfo commitInfo)
  {
    for (CDOCommitInfoHandler handler : getCommitInfoHandlers())
    {
      try
      {
        handler.handleCommitInfo(commitInfo);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, CDOCommitData commitData)
  {
    checkActive();
    CDOCommitInfo commitInfo = new CDOCommitInfoImpl(this, branch, timeStamp, previousTimeStamp, userID, comment,
        commitData);
    return intern(commitInfo);
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
    if (cache != null)
    {
      final CDOCommitInfoHandler delegate = handler;
      handler = new CDOCommitInfoHandler()
      {
        public void handleCommitInfo(CDOCommitInfo commitInfo)
        {
          delegate.handleCommitInfo(intern(commitInfo));
        }
      };
    }

    loader.loadCommitInfos(branch, startTime, endTime, handler);
  }

  public void getCommitInfos(CDOBranch branch, long startTime, String userID, String comment, int count,
      CDOCommitInfoHandler handler)
  {
    if (userID != null || comment != null)
    {
      throw new IllegalArgumentException("The parameters userID and comment are not supported");
    }

    long endTime = CDOCommitInfoUtil.encodeCount(count);
    getCommitInfos(branch, startTime, endTime, handler);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(loader, "commitInfoLoader"); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (cache != null)
    {
      synchronized (cacheLock)
      {
        cache.clear();
      }
    }

    super.doDeactivate();
  }

  @Override
  protected CDOCommitHistory createHistory(CDOBranch key)
  {
    return new CDOCommitHistoryImpl(this, key);
  }

  private CDOCommitInfo intern(CDOCommitInfo commitInfo)
  {
    if (cache != null && commitInfo != null)
    {
      synchronized (cacheLock)
      {
        CDOCommitInfo cachedCommitInfo = cache.get(commitInfo);
        if (cachedCommitInfo != null)
        {
          return cachedCommitInfo;
        }

        cache.put(commitInfo, commitInfo);
      }
    }

    return commitInfo;
  }
}
