/*
 * Copyright (c) 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranch.BranchDeletedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.bundle.OM;

import org.eclipse.net4j.util.collection.GrowingRandomAccessList;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.FinishedEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOCommitHistoryImpl extends Container<CDOCommitInfo> implements CDOCommitHistory
{
  private static final CDOCommitInfo[] NO_ELEMENTS = {};

  private final TriggerLoadElement triggerLoadElement = new TriggerLoadElementImpl();

  private final CDOCommitInfoManager manager;

  private final CDOBranch branch;

  private final IListener branchListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof BranchDeletedEvent)
      {
        deactivate();
      }
    }
  };

  private final GrowingRandomAccessList<CDOCommitInfo> commitInfos = new GrowingRandomAccessList<>(CDOCommitInfo.class, DEFAULT_LOAD_COUNT);

  private final Object loadLock = new Object();

  private int loadCount = DEFAULT_LOAD_COUNT;

  private CDOCommitInfo[] elements;

  private boolean appendingTriggerLoadElement;

  private boolean full;

  private boolean loading;

  public CDOCommitHistoryImpl(CDOCommitInfoManager manager, CDOBranch branch)
  {
    super(true);
    this.manager = manager;
    this.branch = branch;
  }

  @Override
  public final CDOCommitInfoManager getManager()
  {
    return manager;
  }

  @Override
  public final CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  public final int getLoadCount()
  {
    synchronized (loadLock)
    {
      return loadCount;
    }
  }

  @Override
  public final void setLoadCount(int loadCount)
  {
    synchronized (loadLock)
    {
      this.loadCount = loadCount;
    }
  }

  @Override
  public final boolean isAppendingTriggerLoadElement()
  {
    synchronized (loadLock)
    {
      return appendingTriggerLoadElement;
    }
  }

  @Override
  public final void setAppendingTriggerLoadElement(boolean appendingTriggerLoadElement)
  {
    int event = 0;
    synchronized (loadLock)
    {
      if (this.appendingTriggerLoadElement != appendingTriggerLoadElement)
      {
        this.appendingTriggerLoadElement = appendingTriggerLoadElement;
        elements = null;

        if (!full)
        {
          if (appendingTriggerLoadElement)
          {
            event = 1;
          }
          else
          {
            event = 2;
          }
        }
      }
    }

    switch (event)
    {
    case 1:
      fireElementAddedEvent(triggerLoadElement);
      break;

    case 2:
      fireElementRemovedEvent(triggerLoadElement);
      break;
    }
  }

  @Override
  public final CDOCommitInfo getFirstElement()
  {
    checkActive();
    synchronized (loadLock)
    {
      if (loading)
      {
        if (elements == null)
        {
          return null;
        }

        return elements[0];
      }

      if (commitInfos.isEmpty())
      {
        return null;
      }

      return commitInfos.getFirst();
    }
  }

  @Override
  public final CDOCommitInfo getLastElement()
  {
    checkActive();
    synchronized (loadLock)
    {
      if (loading)
      {
        if (elements == null)
        {
          return null;
        }

        return elements[elements.length - 1];
      }

      if (commitInfos.isEmpty())
      {
        return null;
      }

      return commitInfos.getLast();
    }
  }

  @Override
  public final CDOCommitInfo getElement(int index)
  {
    checkActive();
    synchronized (loadLock)
    {
      if (loading)
      {
        if (elements == null)
        {
          return null;
        }

        return elements[index];
      }

      return commitInfos.get(index);
    }
  }

  @Override
  public final CDOCommitInfo[] getElements()
  {
    checkActive();
    synchronized (loadLock)
    {
      if (loading)
      {
        if (elements == null)
        {
          return NO_ELEMENTS;
        }

        return elements;
      }

      if (elements == null)
      {
        int size = commitInfos.size();

        if (!full && appendingTriggerLoadElement)
        {
          elements = commitInfos.toArray(new CDOCommitInfo[size + 1]);
          elements[size] = triggerLoadElement;
        }
        else
        {
          elements = commitInfos.toArray(new CDOCommitInfo[size]);
        }
      }

      return elements;
    }
  }

  @Override
  public final int size()
  {
    checkActive();
    synchronized (loadLock)
    {
      if (loading)
      {
        if (elements == null)
        {
          return 0;
        }

        return elements.length;
      }

      int size = commitInfos.size();
      if (!full && appendingTriggerLoadElement)
      {
        ++size;
      }

      return size;
    }
  }

  @Override
  public final boolean isEmpty()
  {
    checkActive();
    synchronized (loadLock)
    {
      if (loading)
      {
        if (elements == null)
        {
          return true;
        }

        return elements.length == 0;
      }

      if (!full && appendingTriggerLoadElement)
      {
        return false;
      }

      return commitInfos.isEmpty();
    }
  }

  @Override
  public final boolean isFull()
  {
    synchronized (loadLock)
    {
      return full;
    }
  }

  @Override
  public final boolean isLoading()
  {
    synchronized (loadLock)
    {
      return loading;
    }
  }

  @Override
  public final void waitWhileLoading(long timeout)
  {
    long end = System.currentTimeMillis() + timeout;

    synchronized (loadLock)
    {
      while (loading)
      {
        try
        {
          long remaining = end - System.currentTimeMillis();
          if (remaining <= 0)
          {
            return;
          }

          loadLock.wait(remaining);
        }
        catch (InterruptedException ex)
        {
          return;
        }
      }
    }
  }

  @Override
  public final void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    synchronized (loadLock)
    {
      loading = true;
    }

    List<CDOCommitInfo> addedCommitInfos = Collections.emptyList();

    try
    {
      if (addCommitInfo(commitInfo))
      {
        addedCommitInfos = Collections.singletonList(commitInfo);
      }
    }
    finally
    {
      finishLoading(addedCommitInfos, null);
    }
  }

  @Override
  public final boolean triggerLoad()
  {
    return triggerLoad(null);
  }

  @Override
  public final boolean triggerLoad(final CDOCommitInfoHandler handler)
  {
    synchronized (loadLock)
    {
      if (full || loading)
      {
        return false;
      }

      loading = true;

      new Thread("CDOCommitHistoryLoader")
      {
        @Override
        public void run()
        {
          final List<CDOCommitInfo> addedCommitInfos = new ArrayList<>();

          try
          {
            loadCommitInfos(loadCount, addedCommitInfos);
          }
          catch (Throwable ex)
          {
            OM.LOG.error(ex);
          }
          finally
          {
            finishLoading(addedCommitInfos, handler);
          }
        }
      }.start();
    }

    return true;
  }

  protected void loadCommitInfos(int loadCount, final List<CDOCommitInfo> addedCommitInfos)
  {
    final CDOCommitInfo[] lastCommitInfo = { getLastCommitInfo() };
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    int delivered = 0;

    if (lastCommitInfo[0] == null)
    {
      timeStamp = manager.getLastCommitOfBranch(branch, false);
    }
    else
    {
      timeStamp = lastCommitInfo[0].getPreviousTimeStamp();
    }

    // ==============================================
    // Fill history with already loaded commit infos.
    // ==============================================

    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      CDOCommitInfo commitInfo;
      while ((commitInfo = manager.getCommitInfo(timeStamp, false)) != null)
      {
        if (addCommitInfo(commitInfo))
        {
          addedCommitInfos.add(commitInfo);
          lastCommitInfo[0] = commitInfo;
          ++delivered;
        }

        if (commitInfo.isInitialCommit())
        {
          setFull();
          break;
        }

        timeStamp = commitInfo.getPreviousTimeStamp();
      }
    }

    // ================================================================
    // Load commit infos from server if load count is not reached, yet.
    // ================================================================

    if (delivered < loadCount && !isFull())
    {
      final int[] loaded = { 0 }; // Used to detect premature end of loading.

      manager.getCommitInfos(branch, timeStamp, null, null, -loadCount, new CDOCommitInfoHandler()
      {
        @Override
        public void handleCommitInfo(CDOCommitInfo commitInfo)
        {
          ++loaded[0];

          if (addCommitInfo(commitInfo))
          {
            addedCommitInfos.add(commitInfo);
            lastCommitInfo[0] = commitInfo;
          }
        }
      });

      if (loaded[0] < loadCount)
      {
        setFull();
      }
      else
      {
        // ==================================================
        // Complete history with already loaded commit infos.
        // ==================================================

        if (lastCommitInfo[0] != null)
        {
          long previousTimeStamp = lastCommitInfo[0].getPreviousTimeStamp();
          while ((lastCommitInfo[0] = manager.getCommitInfo(previousTimeStamp, false)) != null)
          {
            if (addCommitInfo(lastCommitInfo[0]))
            {
              addedCommitInfos.add(lastCommitInfo[0]);
            }

            if (lastCommitInfo[0].isInitialCommit())
            {
              setFull();
              break;
            }

            previousTimeStamp = lastCommitInfo[0].getPreviousTimeStamp();
          }
        }
      }
    }

    if (appendingTriggerLoadElement && !isFull())
    {
      addedCommitInfos.add(triggerLoadElement);
    }
  }

  protected boolean filterCommitInfo(CDOCommitInfo commitInfo)
  {
    CDOBranch commitBranch = commitInfo.getBranch();
    return commitBranch.isDeleted() || branch != null && commitBranch != branch;
  }

  protected final boolean addCommitInfo(CDOCommitInfo commitInfo)
  {
    checkLoading();

    if (commitInfo == null || filterCommitInfo(commitInfo))
    {
      return false;
    }

    long timeStamp = commitInfo.getTimeStamp();
    if (commitInfos.isEmpty() || timeStamp > commitInfos.getFirst().getTimeStamp())
    {
      commitInfos.addFirst(commitInfo);
    }
    else if (timeStamp < commitInfos.getLast().getTimeStamp())
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
          // Ignore duplicate commit infos.
          return false;
        }

        if (timeStamp < currentTimeStamp)
        {
          it.add(commitInfo);
          break;
        }
      }
    }

    return true;
  }

  protected final void deliverFinish(List<CDOCommitInfo> addedCommitInfos, CDOCommitInfoHandler handler)
  {
    if (handler instanceof IListener)
    {
      IListener listener = (IListener)handler;
      listener.notifyEvent(new FinishedEvent(addedCommitInfos));
    }
  }

  protected final void setFull()
  {
    full = true;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    EventUtil.addListener(branch, branchListener);
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
    EventUtil.removeListener(branch, branchListener);
    super.doDeactivate();
  }

  private void checkLoading()
  {
    if (!loading)
    {
      throw new IllegalStateException("Not loading");
    }
  }

  private void finishLoading(final List<CDOCommitInfo> addedCommitInfos, final CDOCommitInfoHandler handler)
  {
    synchronized (loadLock)
    {
      elements = null;
      loading = false;
      loadLock.notifyAll();
    }

    if (!addedCommitInfos.isEmpty())
    {
      fireElementsAddedEvent(addedCommitInfos.toArray(new CDOCommitInfo[addedCommitInfos.size()]));

      if (handler != null)
      {
        for (CDOCommitInfo commitInfo : addedCommitInfos)
        {
          handler.handleCommitInfo(commitInfo);
        }

        deliverFinish(addedCommitInfos, handler);
      }
    }
  }

  private CDOCommitInfo getLastCommitInfo()
  {
    checkLoading();
    if (commitInfos.isEmpty())
    {
      return null;
    }

    return commitInfos.getLast();
  }

  /**
   * @author Eike Stepper
   */
  private final class TriggerLoadElementImpl implements TriggerLoadElement
  {
    @Override
    public CDOCommitHistory getHistory()
    {
      return CDOCommitHistoryImpl.this;
    }

    @Override
    public CDOCommitInfoManager getCommitInfoManager()
    {
      return manager;
    }

    @Override
    public CDOBranch getBranch()
    {
      return null;
    }

    @Override
    public long getTimeStamp()
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    @Override
    public long getPreviousTimeStamp()
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    @Override
    public CDOCommitInfo getPreviousCommitInfo()
    {
      return null;
    }

    @Override
    public String getUserID()
    {
      return null;
    }

    @Override
    public String getComment()
    {
      return "Load more history elements";
    }

    @Override
    public CDOBranchPoint getMergeSource()
    {
      return null;
    }

    @Override
    public CDOCommitInfo getMergedCommitInfo()
    {
      return null;
    }

    @Override
    public boolean isEmpty()
    {
      return true;
    }

    @Override
    public boolean isInitialCommit()
    {
      return false;
    }

    @Override
    public boolean isCommitDataLoaded()
    {
      return true;
    }

    @Override
    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return null;
    }

    @Override
    public List<CDOID> getAffectedIDs()
    {
      return null;
    }

    @Override
    public List<CDOIDAndVersion> getNewObjects()
    {
      return null;
    }

    @Override
    public List<CDORevisionKey> getChangedObjects()
    {
      return null;
    }

    @Override
    public List<CDOIDAndVersion> getDetachedObjects()
    {
      return null;
    }

    @Override
    public Map<CDOID, CDOChangeKind> getChangeKinds()
    {
      return null;
    }

    @Override
    public CDOChangeKind getChangeKind(CDOID id)
    {
      return null;
    }

    @Override
    public void merge(CDOChangeSetData changeSetData)
    {
    }

    @Override
    public CDOChangeSetData copy()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Empty extends Container<CDOCommitInfo> implements CDOCommitHistory
  {
    @Override
    public CDOCommitInfo[] getElements()
    {
      return NO_ELEMENTS;
    }

    @Override
    public void handleCommitInfo(CDOCommitInfo commitInfo)
    {
    }

    @Override
    public CDOCommitInfoManager getManager()
    {
      return null;
    }

    @Override
    public CDOBranch getBranch()
    {
      return null;
    }

    @Override
    public boolean isAppendingTriggerLoadElement()
    {
      return false;
    }

    @Override
    public void setAppendingTriggerLoadElement(boolean appendingTriggerLoadElement)
    {
    }

    @Override
    public CDOCommitInfo getFirstElement()
    {
      return null;
    }

    @Override
    public CDOCommitInfo getLastElement()
    {
      return null;
    }

    @Override
    public CDOCommitInfo getElement(int index)
    {
      return null;
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public boolean isLoading()
    {
      return false;
    }

    @Override
    public void waitWhileLoading(long timeout)
    {
    }

    @Override
    public int getLoadCount()
    {
      return 0;
    }

    @Override
    public void setLoadCount(int loadCount)
    {
    }

    @Override
    public boolean triggerLoad()
    {
      return false;
    }

    @Override
    public boolean triggerLoad(CDOCommitInfoHandler handler)
    {
      return false;
    }

    @Override
    public boolean isFull()
    {
      return true;
    }
  }
}
