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
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public class CDOPrefetcherManager extends CDOViewSetHandler
{
  public static final long NO_TIMEOUT = -1L;

  public static final long DEFAULT_TIMEOUT = 30000L;

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

  public final Prefetcher getPrefetcher(CDOView view)
  {
    return prefetchers.get(view);
  }

  public boolean waitUntilPrefetched()
  {
    return waitUntilPrefetched(DEFAULT_TIMEOUT);
  }

  public boolean waitUntilPrefetched(long timeout)
  {
    synchronized (lock)
    {
      long end = timeout == NO_TIMEOUT ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;

      while (runnables > 0)
      {
        try
        {
          long wait = end - System.currentTimeMillis();
          if (wait > 0)
          {
            lock.wait(wait);
          }
        }
        catch (InterruptedException ex)
        {
          return false;
        }
      }
    }

    return true;
  }

  public void cleanup()
  {
    Prefetcher[] array = prefetchers.values().toArray(new Prefetcher[0]);
    CountDownLatch finished = new CountDownLatch(array.length);

    for (Prefetcher prefetcher : array)
    {
      executorService.submit(() -> {
        try
        {
          prefetcher.cleanup();
        }
        finally
        {
          finished.countDown();
        }
      });
    }

    try
    {
      finished.await();
    }
    catch (InterruptedException ex)
    {
      OM.LOG.error(ex);
    }
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
    Prefetcher prefetcher = getPrefetcher(view);
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

    executorService.submit(() -> execute(runnable));
  }

  protected void execute(Runnable runnable)
  {
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
  }

  /**
   * @author Eike Stepper
   */
  public static class Prefetcher
  {
    private final CDOView view;

    private final CDOID rootID;

    private final InternalCDORevisionManager revisionManager;

    private final IListener cacheListener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDORevisionCache.AdditionEvent)
        {
          CDORevisionCache.AdditionEvent e = (CDORevisionCache.AdditionEvent)event;
          CDORevision revision = e.getRevision();
          handleRevsion(revision);
        }
      }
    };

    private volatile Map<CDORevisionKey, CDORevision> revisions = new HashMap<>();

    private volatile CDOBranchPoint branchPoint;

    private volatile boolean cancelCleanup;

    public Prefetcher(CDOView view, CDOID rootID)
    {
      this.view = view;
      CDOSession session = view.getSession();
      this.rootID = rootID != null ? rootID : session.getRepositoryInfo().getRootResourceID();

      revisionManager = (InternalCDORevisionManager)session.getRevisionManager();
      revisionManager.getCache().addListener(cacheListener);

      branchPoint = CDOBranchUtil.copyBranchPoint(view);
    }

    public Prefetcher(CDOView view)
    {
      this(view, null);
    }

    public final CDOView getView()
    {
      return view;
    }

    public final synchronized int getSize()
    {
      return revisions.size();
    }

    public final synchronized boolean isDisposed()
    {
      return branchPoint == null;
    }

    public final List<CDORevision> getRevisions(CDOID id)
    {
      List<CDORevision> result = new ArrayList<>();
      collectRevisions(id, result);
      return result;
    }

    protected void prefetch()
    {
      cancelCleanup = true;

      try
      {
        doPrefetch();
      }
      finally
      {
        cancelCleanup = false;
      }
    }

    protected void changeBranchPoint()
    {
      doChangeBranchPoint();
    }

    protected synchronized void cleanup()
    {
      Set<CDORevisionKey> revisionsToKeep = new HashSet<>();

      for (Map.Entry<CDORevisionKey, CDORevision> entry : revisions.entrySet())
      {
        if (cancelCleanup)
        {
          break;
        }

        CDORevision revision = entry.getValue();
        if (isValidRevision(revision, branchPoint))
        {
          CDORevisionKey key = entry.getKey();
          revisionsToKeep.add(key);

          while (!cancelCleanup && revision instanceof PointerCDORevision)
          {
            PointerCDORevision pointer = (PointerCDORevision)revision;
            CDOBranchVersion target = pointer.getTarget();
            key = CDORevisionUtil.createRevisionKey(key.getID(), target.getBranch(), target.getVersion());
            revisionsToKeep.add(key);

            // Walk up the branch tree.
            revision = revisions.get(key);
          }
        }
      }

      // Iterate over the entry set, so that remove() has an effect.
      for (Iterator<Map.Entry<CDORevisionKey, CDORevision>> it = revisions.entrySet().iterator(); !cancelCleanup && it.hasNext();)
      {
        Map.Entry<CDORevisionKey, CDORevision> entry = it.next();
        CDORevisionKey key = entry.getKey();
        if (!revisionsToKeep.contains(key))
        {
          CDORevision revision = entry.getValue();
          it.remove();

          try
          {
            revisionEvicted(revision);
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
    }

    protected void dispose()
    {
      cancelCleanup = true;
      revisionManager.getCache().removeListener(cacheListener);
      doDispose();
    }

    protected boolean isValidRevision(CDORevision revision, CDOBranchPoint branchPoint)
    {
      return revision.isValid(branchPoint);
    }

    protected CDORevision cacheRevision(CDORevision revision)
    {
      return revisions.put(CDORevisionUtil.copyRevisionKey(revision), revision);
    }

    /**
     * Subclasses may override.
     */
    protected void revisionEvicted(CDORevision revision)
    {
      // Do nothing.
    }

    private synchronized void handleRevsion(CDORevision revision)
    {
      if (isValidRevision(revision, branchPoint))
      {
        cacheRevision(revision);
      }
    }

    private synchronized void collectRevisions(CDOID id, List<CDORevision> result)
    {
      for (CDORevision revision : revisions.values())
      {
        if (revision.getID() == id)
        {
          result.add(revision);
        }
      }
    }

    private synchronized void doPrefetch()
    {
      revisionManager.getRevision(rootID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_INFINITE, true);
    }

    private synchronized void doChangeBranchPoint()
    {
      branchPoint = CDOBranchUtil.copyBranchPoint(view);
      prefetch();
    }

    private synchronized void doDispose()
    {
      revisions.clear();
      branchPoint = null;
    }
  }
}
