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
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Orchestrates (adds, changes, removes) per-view {@link Prefetcher prefetchers} for all views in a {@link CDOViewSet view set}.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public class CDOPrefetcherManager extends CDOViewSetHandler
{
  public static final long NO_TIMEOUT = -1L;

  public static final long DEFAULT_TIMEOUT = 30000L;

  private final ExecutorService executorService;

  private final boolean prefetchLockStates;

  private final Map<CDOView, Prefetcher> prefetchers = Collections.synchronizedMap(new HashMap<>());

  @ExcludeFromDump
  private final Object lock = new Object();

  private int runnables;

  public CDOPrefetcherManager(ResourceSet resourceSet, boolean prefetchLockStates)
  {
    super(resourceSet);
    this.prefetchLockStates = prefetchLockStates;
    executorService = ConcurrencyUtil.getExecutorService(getViewSet());
  }

  public boolean isPrefetchLockStates()
  {
    return prefetchLockStates;
  }

  public final Prefetcher[] getPrefetchers()
  {
    // Don't use prefetchers.size() because that wouldn't be thread-safe anymore.
    return prefetchers.values().toArray(new Prefetcher[0]);
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
          if (wait <= 0)
          {
            return false;
          }

          lock.wait(wait);
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
    Prefetcher[] prefetchers = getPrefetchers();
    CountDownLatch finished = new CountDownLatch(prefetchers.length);

    for (Prefetcher prefetcher : prefetchers)
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
    return new Prefetcher(view, prefetchLockStates);
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
   * Prefetches {@link CDORevision revisions} and, optionally, {@link CDOLockState lock states} for a given, single {@link CDOView view}.
   *
   * @author Eike Stepper
   */
  public static class Prefetcher
  {
    private final CDOView view;

    private final CDOID rootID;

    private final Map<CDORevisionKey, CDORevision> revisions = new HashMap<>();

    private final InternalCDORevisionManager revisionManager;

    private final boolean prefetchLockStates;

    private final IListener cacheListener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (prefetching)
        {
          return;
        }

        if (event instanceof CDORevisionCache.AdditionEvent)
        {
          CDORevisionCache.AdditionEvent e = (CDORevisionCache.AdditionEvent)event;
          CDORevision revision = e.getRevision();
          CDOBranchPoint branchPoint = Prefetcher.this.branchPoint;

          if (isValidRevision(revision, branchPoint))
          {
            notifyValidRevision(revision);
          }
          else
          {
            revisionIgnored(revision);
          }
        }
      }
    };

    private volatile CDOBranchPoint branchPoint;

    private volatile boolean prefetching;

    private volatile boolean cancelCleanup;

    public Prefetcher(CDOView view, CDOID rootID, boolean prefetchLockStates)
    {
      this.view = view;

      CDOSession session = view.getSession();
      this.rootID = rootID != null ? rootID : session.getRepositoryInfo().getRootResourceID();
      this.prefetchLockStates = prefetchLockStates;

      revisionManager = (InternalCDORevisionManager)session.getRevisionManager();
      revisionManager.getCache().addListener(cacheListener);

      branchPoint = CDOBranchUtil.copyBranchPoint(view);
    }

    public Prefetcher(CDOView view, boolean prefetchLockStates)
    {
      this(view, null, prefetchLockStates);
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

    public final synchronized void handleRevisions(CDORevisionHandler handler)
    {
      for (CDORevision revision : revisions.values())
      {
        if (!handler.handleRevision(revision))
        {
          break;
        }
      }
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
      Set<CDORevisionKey> keysToKeep = new HashSet<>();

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
          keysToKeep.add(key);

          if (!cancelCleanup && revision instanceof PointerCDORevision)
          {
            PointerCDORevision pointer = (PointerCDORevision)revision;

            CDOBranchVersion target = pointer.getTarget();
            if (target instanceof CDORevision)
            {
              keysToKeep.add((CDORevision)target);
            }
            else if (target != null)
            {
              keysToKeep.add(CDORevisionUtil.createRevisionKey(key.getID(), target.getBranch(), target.getVersion()));
            }
          }
        }
      }

      // Iterate over the entry set, so that remove() has an effect.
      for (Iterator<Map.Entry<CDORevisionKey, CDORevision>> it = revisions.entrySet().iterator(); !cancelCleanup && it.hasNext();)
      {
        Map.Entry<CDORevisionKey, CDORevision> entry = it.next();
        CDORevisionKey key = entry.getKey();
        if (!keysToKeep.contains(key))
        {
          CDORevision revision = entry.getValue();
          it.remove();

          try
          {
            revisionRemoved(revision);
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

    /**
     * The caller must synchronize on this prefetcher.
     */
    protected CDORevision addRevision(CDORevision revision)
    {
      return revisions.put(CDORevisionUtil.copyRevisionKey(revision), revision);
    }

    /**
     * Subclasses may override.
     * <p>
     * When this method is called the calling thread is holding the prefetcher lock.
     * Do not pass control to a different thread which might attempt to access the prefetcher!
     */
    protected void revisionRemoved(CDORevision revision)
    {
      // Do nothing.
    }

    /**
     * Subclasses may override.
     */
    protected void revisionIgnored(CDORevision revision)
    {
      // Do nothing.
    }

    private boolean isValidRevision(CDORevision revision, CDOBranchPoint branchPoint)
    {
      return revision.isValid(branchPoint);
    }

    private void handleValidRevision(CDORevision revision)
    {
      addRevision(revision);

      if (revision instanceof PointerCDORevision)
      {
        PointerCDORevision pointer = (PointerCDORevision)revision;

        CDOBranchVersion target = pointer.getTarget();
        if (target instanceof CDORevision)
        {
          addRevision((CDORevision)target);
        }
      }
    }

    private synchronized void notifyValidRevision(CDORevision revision)
    {
      handleValidRevision(revision);
    }

    private synchronized void doPrefetch()
    {
      prefetching = true;

      try
      {
        revisionManager.prefetchRevisions(rootID, branchPoint, CDORevision.DEPTH_INFINITE, prefetchLockStates, r -> handleValidRevision(r));
      }
      finally
      {
        prefetching = false;
      }
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
