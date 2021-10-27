/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public class CDOPrefetcherManager extends CDOViewSetHandler
{
  public static final long EXECUTION_TIMEOUT = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.view.CDOPrefetcherManager.EXECUTION_TIMEOUT", 30000L);

  private final ExecutorService executorService;

  private final Map<CDOView, Prefetcher> prefetchers = Collections.synchronizedMap(new HashMap<>());

  @ExcludeFromDump
  private final Object lock = new Object();

  private int runnables;

  public CDOPrefetcherManager(ResourceSet resourceSet)
  {
    super(resourceSet);
    executorService = ConcurrencyUtil.getExecutorService(getViewSet());
  }

  public boolean waitUntilPrefetched()
  {
    long end = System.currentTimeMillis() + getExecutionTimeout();

    synchronized (lock)
    {
      while (runnables > 0)
      {
        try
        {
          lock.wait(end - System.currentTimeMillis());
        }
        catch (InterruptedException ex)
        {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  protected void viewAdded(CDOView view)
  {
    Prefetcher prefetcher = createPrefetcher(view);
    prefetchers.put(view, prefetcher);
    schedule(prefetcher::prefetch);
  }

  @Override
  protected void viewChanged(CDOView view, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
  {
    Prefetcher prefetcher = prefetchers.get(view);
    if (prefetcher != null)
    {
      schedule(prefetcher::changeBranchPoint);
    }
  }

  @Override
  protected void viewRemoved(CDOView view)
  {
    Prefetcher prefetcher = prefetchers.remove(view);
    if (prefetcher != null)
    {
      schedule(prefetcher::dispose);
    }
  }

  protected Prefetcher createPrefetcher(CDOView view)
  {
    return new Prefetcher(view);
  }

  protected void schedule(Runnable runnable)
  {
    synchronized (lock)
    {
      ++runnables;
    }

    executorService.submit(() -> {
      try
      {
        runnable.run();
      }
      catch (Error ex)
      {
        OM.LOG.error(ex);
        throw ex;
      }
      catch (Throwable ex)
      {
        OM.LOG.error(ex);
      }
      finally
      {
        synchronized (lock)
        {
          if (--runnables == 0)
          {
            lock.notifyAll();
          }
        }
      }
    });
  }

  protected long getExecutionTimeout()
  {
    return EXECUTION_TIMEOUT;
  }

  /**
   * @author Eike Stepper
   */
  protected static class Prefetcher implements IListener
  {
    private final CDOView view;

    private final CDOID rootID;

    /**
     * A set is sufficient (instead of Map<CDORevisionKey, CDORevision>) because AbstractCDORevision.equals()
     * determines equality based on the revision key.
     */
    private final Map<CDORevisionKey, CDORevision> revisions = new HashMap<>();

    private final InternalCDORevisionManager revisionManager;

    private CDOBranchPoint branchPoint;

    public Prefetcher(CDOView view)
    {
      this.view = view;
      CDOSession session = view.getSession();
      rootID = session.getRepositoryInfo().getRootResourceID();

      revisionManager = (InternalCDORevisionManager)session.getRevisionManager();
      revisionManager.getCache().addListener(this);

      branchPoint = CDOBranchUtil.copyBranchPoint(view);
    }

    public void prefetch()
    {
      revisionManager.getRevision(rootID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_INFINITE, true);
    }

    public void changeBranchPoint()
    {
      branchPoint = CDOBranchUtil.copyBranchPoint(view);
      prefetch();
    }

    public void dispose()
    {
      revisionManager.getCache().removeListener(this);
      revisions.clear();
      branchPoint = null;
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDORevisionCache.AdditionEvent)
      {
        CDORevisionCache.AdditionEvent e = (CDORevisionCache.AdditionEvent)event;

        CDORevision revision = e.getRevision();
        if (revision.isValid(branchPoint))
        {
          cacheRevision(revision);
        }
      }
    }

    protected CDORevision cacheRevision(CDORevision revision)
    {
      return revisions.put(CDORevisionUtil.copyRevisionKey(revision), revision);
    }

    protected int getSize()
    {
      return revisions.size();
    }
  }
}
