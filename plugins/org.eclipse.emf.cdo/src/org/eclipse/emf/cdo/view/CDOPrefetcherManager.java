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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ParallelRunner;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public final class CDOPrefetcherManager extends CDOViewSetHandler.Transactional
{
  public static final long EXECUTION_TIMEOUT = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.view.CDOPrefetcherManager.EXECUTION_TIMEOUT", 30000L);

  private ExecutorService executorService;

  private Map<CDOView, Prefetcher> prefetchers;

  public CDOPrefetcherManager(ResourceSet resourceSet)
  {
    super(resourceSet);
  }

  @Override
  protected void init(CDOViewSet viewSet)
  {
    executorService = ConcurrencyUtil.getExecutorService(IPluginContainer.INSTANCE);
    prefetchers = new HashMap<>();
    super.init(viewSet);
  }

  @Override
  protected void viewSetCommitted(Set<CDOView> addedViews, Set<CDOView> changedViews, Set<CDOView> removedViews)
  {
    List<Runnable> runnables = new ArrayList<>();

    synchronized (prefetchers)
    {
      for (CDOView view : removedViews)
      {
        Prefetcher prefetcher = prefetchers.remove(view);
        if (prefetcher != null)
        {
          runnables.add(() -> prefetcher.dispose());
        }
      }

      for (CDOView view : addedViews)
      {
        Prefetcher prefetcher = createPrefetcher(view);
        prefetchers.put(view, prefetcher);
        runnables.add(prefetcher);
      }

      for (CDOView view : changedViews)
      {
        Prefetcher prefetcher = prefetchers.get(view);
        if (prefetcher != null)
        {
          runnables.add(() -> prefetcher.changeBranchPoint(view));
        }
      }
    }

    execute(executorService, runnables);
  }

  protected Prefetcher createPrefetcher(CDOView view)
  {
    return new Prefetcher(view);
  }

  protected void execute(ExecutorService executorService, List<Runnable> runnables)
  {
    ParallelRunner runner = new ParallelRunner(runnables);

    try
    {
      runner.run(executorService, EXECUTION_TIMEOUT);
    }
    catch (InterruptedException ex)
    {
      OM.LOG.warn(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class Prefetcher implements Runnable, IListener
  {
    private final CDOID rootID;

    /**
     * A set is sufficient (instead of Map<CDORevisionKey, CDORevision>) because AbstractCDORevision.equals()
     * determines equality based on the revision key.
     */
    private final Set<CDORevision> revisions = new HashSet<>();

    private final InternalCDORevisionManager revisionManager;

    private CDOBranchPoint branchPoint;

    public Prefetcher(CDOView view)
    {
      CDOSession session = view.getSession();
      rootID = session.getRepositoryInfo().getRootResourceID();

      revisionManager = (InternalCDORevisionManager)session.getRevisionManager();
      revisionManager.getCache().addListener(this);

      branchPoint = CDOBranchUtil.copyBranchPoint(view);
    }

    public void changeBranchPoint(CDOBranchPoint newBranchPoint)
    {
      branchPoint = CDOBranchUtil.copyBranchPoint(newBranchPoint);
      run();
    }

    @Override
    public void run()
    {
      revisionManager.getRevision(rootID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_INFINITE, true);
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
          revisions.add(revision);
        }
      }
    }
  }
}
