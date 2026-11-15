/*
 * Copyright (c) 2009-2017, 2019-2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionsLoadedEvent;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache.InternResult;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends Lifecycle implements InternalCDORevisionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionManagerImpl.class);

  private static final Config OBJECT_TYPE_CONFIG = legacyConfig(0, CDORevision.DEPTH_NONE, false, true);

  private static final Config CONTAINS_REVISION_CONFIG = legacyConfig(0, CDORevision.DEPTH_NONE, false, false);

  private static final int MAX_HEAD_RESOLUTION_RETRIES = 8;

  private boolean supportingAudits;

  private boolean supportingBranches;

  private RevisionLoader revisionLoader;

  private CDORevisionFactory factory;

  private InternalCDORevisionCache cache;

  private final Object revisionStateLock = new Object();

  private final List<LoadBatch> loadBatches = new ArrayList<>();

  private final ThreadLocal<LoadBatch> currentLoadBatch = new ThreadLocal<>();

  private final ArrayDeque<Runnable> cacheEvents = new ArrayDeque<>();

  private boolean dispatchingCacheEvents;

  private int cacheEventDeferrals;

  private long permissionGeneration;

  private int permissionUpdates;

  private boolean permissionUpdatesClosed;

  private final ThreadLocal<Integer> permissionUpdateDepth = ThreadLocal.withInitial(() -> 0);

  public CDORevisionManagerImpl()
  {
  }

  @Override
  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  @Override
  public void setSupportingAudits(boolean on)
  {
    checkInactive();
    supportingAudits = on;
  }

  @Override
  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  @Override
  public void setSupportingBranches(boolean on)
  {
    checkInactive();
    supportingBranches = on;
  }

  @Override
  public RevisionLoader getRevisionLoader()
  {
    return revisionLoader;
  }

  @Override
  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    checkInactive();
    this.revisionLoader = revisionLoader;
  }

  @Override
  @Deprecated
  public RevisionLocker getRevisionLocker()
  {
    return null;
  }

  @Override
  @Deprecated
  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    // Does nothing.
  }

  @Override
  public CDORevisionFactory getFactory()
  {
    return factory;
  }

  @Override
  public void setFactory(CDORevisionFactory factory)
  {
    checkInactive();
    this.factory = factory;
  }

  @Override
  public InternalCDORevisionCache getCache()
  {
    return cache;
  }

  @Override
  public void setCache(CDORevisionCache cache)
  {
    checkInactive();
    this.cache = (InternalCDORevisionCache)cache;
  }

  @Override
  public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand)
  {
    EClass type = cache.getObjectType(id);
    if (type == null && branchManagerForLoadOnDemand != null)
    {
      CDOBranch mainBranch = branchManagerForLoadOnDemand.getMainBranch();
      CDORevision revision = getRevisionByVersion(id, mainBranch.getVersion(CDOBranchVersion.FIRST_VERSION), OBJECT_TYPE_CONFIG);
      if (revision != null)
      {
        type = revision.getEClass();
      }
    }

    return type;
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    return getObjectType(id, null);
  }

  @Override
  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    if (supportingBranches)
    {
      return getRevision(id, branchPoint, CONTAINS_REVISION_CONFIG, null) != null;
    }

    return getCachedRevision(id, branchPoint) != null;
  }

  @Override
  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return cache.getRevisionByVersion(id, branchVersion) != null;
  }

  @Override
  public void reviseLatest(CDOID id, CDOBranch branch)
  {
    synchronized (revisionStateLock)
    {
      recordInvalidation(new RevisionInvalidation(id, branch, CDOBranchVersion.UNSPECIFIED_VERSION, CDORevision.UNSPECIFIED_DATE, true, false));

      InternalCDORevision revision = (InternalCDORevision)cache.getRevision(id, branch.getHead());
      if (revision != null)
      {
        CDOBranchVersion version = branch.getVersion(revision.getVersion());
        cache.removeRevision(id, version);
      }
    }
  }

  @Override
  public void reviseDetached(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    if (branchVersion.getVersion() == CDOBranchVersion.UNSPECIFIED_VERSION)
    {
      CDOBranch branch = branchVersion.getBranch();

      synchronized (revisionStateLock)
      {
        recordInvalidation(new RevisionInvalidation(id, branch, CDOBranchVersion.UNSPECIFIED_VERSION, timeStamp, true, false));

        InternalCDORevision revision = getCachedRevision(id, branch.getHead());
        if (revision != null)
        {
          cache.removeRevision(id, branch.getVersion(revision.getVersion()));
        }

        if (supportingBranches && !branch.isMainBranch())
        {
          EClass eClass = revision == null ? null : revision.getEClass();
          if (eClass == null)
          {
            InternalCDORevision inheritedRevision = getCachedRevisionRecursively(id, branch.getBase());
            eClass = inheritedRevision == null ? null : inheritedRevision.getEClass();
          }

          if (eClass != null)
          {
            publishDetachedRevision(id, branch, eClass, timeStamp);
          }
        }
      }

      return;
    }

    synchronized (revisionStateLock)
    {
      CDOBranch branch = branchVersion.getBranch();
      recordInvalidation(new RevisionInvalidation(id, branch, branchVersion.getVersion(), timeStamp, true, false));

      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision == null && supportingBranches && !branch.isMainBranch())
      {
        revision = getCachedRevision(id, branch.getHead());
      }

      if (revision != null)
      {
        if (supportingAudits && timeStamp != CDORevision.UNSPECIFIED_DATE)
        {
          revision.setRevised(timeStamp - 1);
        }
        else
        {
          cache.removeRevision(id, branchVersion);
        }
      }

      if (supportingBranches && !branch.isMainBranch())
      {
        EClass eClass = revision == null ? null : revision.getEClass();
        if (eClass == null)
        {
          InternalCDORevision inheritedRevision = getCachedRevisionRecursively(id, branch.getBase());
          eClass = inheritedRevision == null ? null : inheritedRevision.getEClass();
        }

        if (eClass != null)
        {
          publishDetachedRevision(id, branch, eClass, timeStamp);
        }
      }
    }
  }

  private void publishDetachedRevision(CDOID id, CDOBranch branch, EClass eClass, long timeStamp)
  {
    InternalCDORevision revision = getCachedRevision(id, branch.getPoint(timeStamp));
    if (revision instanceof PointerCDORevision)
    {
      cache.removeRevision(id, branch.getVersion(revision.getVersion()));
    }

    DetachedCDORevision detachedRevision = new DetachedCDORevision(eClass, id, branch, CDOBranchVersion.UNSPECIFIED_VERSION, timeStamp);
    publishRevision(detachedRevision, currentLoadBatch.get());
  }

  @Override
  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    synchronized (revisionStateLock)
    {
      recordInvalidation(new RevisionInvalidation(id, branchVersion.getBranch(), branchVersion.getVersion(), timeStamp, false, false));

      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision != null)
      {
        if (timeStamp == CDORevision.UNSPECIFIED_DATE || !supportingAudits)
        {
          cache.removeRevision(id, branchVersion);
        }

        revision.setRevised(timeStamp - 1);
      }
    }
  }

  @Override
  public void evictRevision(CDORevision revision)
  {
    synchronized (revisionStateLock)
    {
      CDOID id = revision.getID();
      CDOBranchVersion branchVersion = revision.getBranch().getVersion(revision.getVersion());
      recordInvalidation(new RevisionInvalidation(id, branchVersion.getBranch(), branchVersion.getVersion(), CDORevision.UNSPECIFIED_DATE, false, true));
      cache.removeRevision(id, branchVersion);
    }
  }

  @Override
  public void removeRevisions(CDOBranch... branches)
  {
    synchronized (revisionStateLock)
    {
      if (branches != null)
      {
        for (LoadBatch batch : loadBatches)
        {
          for (CDOBranch branch : branches)
          {
            if (branch != null && !batch.invalidatedBranches.contains(branch))
            {
              batch.invalidatedBranches.add(branch);
            }
          }
        }
      }

      cache.removeRevisions(branches);
    }
  }

  @Override
  public void clearCache()
  {
    synchronized (revisionStateLock)
    {
      for (LoadBatch batch : loadBatches)
      {
        batch.invalidatedAll = true;
      }

      cache.clear();
    }
  }

  @Override
  public void beginCacheEventDeferral()
  {
    synchronized (revisionStateLock)
    {
      if (permissionUpdatesClosed)
      {
        throw new CancellationException("Revision manager is closed"); //$NON-NLS-1$
      }

      ++cacheEventDeferrals;
    }
  }

  @Override
  public void endCacheEventDeferral()
  {
    boolean dispatchEvents;

    synchronized (revisionStateLock)
    {
      if (cacheEventDeferrals <= 0)
      {
        throw new IllegalStateException("No cache event deferral is active"); //$NON-NLS-1$
      }

      --cacheEventDeferrals;
      dispatchEvents = cacheEventDeferrals == 0 && permissionUpdates == 0 && !permissionUpdatesClosed;
    }

    if (dispatchEvents)
    {
      dispatchCacheEvents();
    }
  }

  @Override
  public void beginPermissionUpdate()
  {
    synchronized (revisionStateLock)
    {
      if (permissionUpdatesClosed)
      {
        throw new CancellationException("Revision manager is closed"); //$NON-NLS-1$
      }

      ++permissionGeneration;
      ++permissionUpdates;
    }
  }

  @Override
  public void enterPermissionUpdater()
  {
    synchronized (revisionStateLock)
    {
      if (permissionUpdates <= 0 || permissionUpdatesClosed)
      {
        throw new IllegalStateException("A permission updater requires an active permission update"); //$NON-NLS-1$
      }

      permissionUpdateDepth.set(permissionUpdateDepth.get() + 1);
    }
  }

  @Override
  public void exitPermissionUpdater()
  {
    synchronized (revisionStateLock)
    {
      int depth = permissionUpdateDepth.get();
      if (depth <= 0)
      {
        throw new IllegalStateException("Permission updater execution must be exited by its starting thread"); //$NON-NLS-1$
      }

      if (depth == 1)
      {
        permissionUpdateDepth.remove();
      }
      else
      {
        permissionUpdateDepth.set(depth - 1);
      }
    }
  }

  @Override
  public void endPermissionUpdate()
  {
    boolean dispatchEvents;

    synchronized (revisionStateLock)
    {
      if (permissionUpdates <= 0)
      {
        throw new IllegalStateException("No permission update is active"); //$NON-NLS-1$
      }

      --permissionUpdates;
      ++permissionGeneration;
      dispatchEvents = permissionUpdates == 0 && !permissionUpdatesClosed;
      revisionStateLock.notifyAll();
    }

    if (dispatchEvents)
    {
      dispatchCacheEvents();
    }
  }

  private long awaitStablePermissionGeneration()
  {
    synchronized (revisionStateLock)
    {
      if (permissionUpdates != 0 && permissionUpdateDepth.get() != 0)
      {
        throw new IllegalStateException("A permission updater must not load revisions while its permission update is active"); //$NON-NLS-1$
      }

      boolean interrupted = false;

      while (permissionUpdates != 0 && !permissionUpdatesClosed)
      {
        try
        {
          revisionStateLock.wait();
        }
        catch (InterruptedException ex)
        {
          interrupted = true;
          break;
        }
      }

      if (interrupted)
      {
        Thread.currentThread().interrupt();
        throw new CancellationException("Interrupted while waiting for a permission update"); //$NON-NLS-1$
      }

      if (permissionUpdatesClosed)
      {
        throw new CancellationException("Revision manager is closed"); //$NON-NLS-1$
      }

      return permissionGeneration;
    }
  }

  private boolean isStablePermissionGeneration(long generation)
  {
    return permissionUpdates == 0 && permissionGeneration == generation && !permissionUpdatesClosed;
  }

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config)
  {
    checkArg(branchVersion.getVersion() >= CDOBranchVersion.FIRST_VERSION, "Invalid version: " + branchVersion.getVersion()); //$NON-NLS-1$

    if (config.getLookupMode() == LookupMode.CACHE_ONLY)
    {
      synchronized (revisionStateLock)
      {
        return getCachedRevisionByVersion(id, branchVersion);
      }
    }

    for (;;)
    {
      long generation = awaitStablePermissionGeneration();
      LoadBatch batch = beginLoadBatch();

      try
      {
        InternalCDORevision revision;

        synchronized (revisionStateLock)
        {
          if (!isStablePermissionGeneration(generation))
          {
            continue;
          }

          revision = config.getLookupMode() == LookupMode.LOADER_ONLY ? null : getCachedRevisionByVersion(id, branchVersion);
          if (revision != null || config.getLookupMode() == LookupMode.CACHE_ONLY)
          {
            return revision;
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Loading revision {0} from {1}", id, branchVersion); //$NON-NLS-1$
        }

        if (revisionLoader instanceof RevisionLoader4)
        {
          revision = ((RevisionLoader4)revisionLoader).loadRevisionByVersion(id, branchVersion, config);
        }
        else
        {
          int referenceChunk = config.getReferenceChunk() == CDORevisionManager.Request.Config.REFERENCE_CHUNK_UNSPECIFIED ? CDORevision.UNCHUNKED
              : config.getReferenceChunk();
          revision = revisionLoader.loadRevisionByVersion(id, branchVersion, referenceChunk);
        }

        synchronized (revisionStateLock)
        {
          if (!isStablePermissionGeneration(generation))
          {
            continue;
          }

          revision = (InternalCDORevision)publishRevision(revision, batch);
        }

        dispatchCacheEvents();

        synchronized (revisionStateLock)
        {
          if (!isStablePermissionGeneration(generation))
          {
            continue;
          }

        }

        return revision;
      }
      finally
      {
        endLoadBatch(batch);
      }
    }
  }

  @Override
  public InternalCDORevision getBaseRevision(CDORevision revision, Request.Config config)
  {
    CDOID id = revision.getID();
    CDOBranch branch = revision.getBranch();
    int version = revision.getVersion();

    if (version == CDOBranchVersion.FIRST_VERSION)
    {
      if (branch.isMainBranch())
      {
        return null;
      }

      CDOBranchPoint basePoint = branch.getBase();
      return getRevision(id, basePoint, config, null);
    }

    CDOBranchVersion baseVersion = branch.getVersion(version - 1);
    return getRevisionByVersion(id, baseVersion, config);
  }

  @Override
  public CDOBranchPointRange getObjectLifetime(CDOID id, CDOBranchPoint branchPoint)
  {
    if (revisionLoader instanceof RevisionLoader2)
    {
      RevisionLoader2 revisionLoader2 = (RevisionLoader2)revisionLoader;
      return revisionLoader2.loadObjectLifetime(id, branchPoint);
    }

    return null;
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, Request.Config config)
  {
    return getRevision(id, branchPoint, config, null);
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics)
  {
    List<CDORevision> results = getRevisions(Collections.singletonList(id), branchPoint, config, synthetics);
    return results.get(0) instanceof InternalCDORevision ? (InternalCDORevision)results.get(0) : null;
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config)
  {
    return getRevisions(ids, branchPoint, config, (SyntheticCDORevision[])null);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, List<CDORevision> additionalRevisions)
  {
    return getRevisions(ids, branchPoint, config, null, additionalRevisions);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics)
  {
    return getRevisions(ids, branchPoint, config, synthetics, null);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics,
      List<CDORevision> additionalRevisions)
  {
    return getRevisions(ids, branchPoint, config, synthetics, additionalRevisions, null);
  }

  private List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics,
      List<CDORevision> additionalRevisions, Consumer<CDORevision> consumer)
  {
    if (additionalRevisions == null)
    {
      additionalRevisions = new ArrayList<>();
    }

    if (config.getLookupMode() == LookupMode.CACHE_ONLY)
    {
      RevisionInfo[] allInfos = new RevisionInfo[ids.size()];
      List<CDORevision> primaryRevisions;
      SyntheticCDORevision[] attemptSynthetics = synthetics == null ? null : new SyntheticCDORevision[synthetics.length];

      synchronized (revisionStateLock)
      {
        createRevisionInfos(ids, branchPoint, config, allInfos);
        primaryRevisions = processResults(allInfos, attemptSynthetics, null);
      }

      dispatchCacheEvents();

      if (synthetics != null && attemptSynthetics != null)
      {
        System.arraycopy(attemptSynthetics, 0, synthetics, 0, synthetics.length);
      }

      if (consumer != null)
      {
        for (RevisionInfo info : allInfos)
        {
          deliverResult(info, consumer);
        }
      }

      return primaryRevisions;
    }

    int headResolutionRetries = 0;

    for (;;)
    {
      long generation = awaitStablePermissionGeneration();
      LoadBatch batch = beginLoadBatch();
      batch.requestedBranchPoint = branchPoint;

      try
      {
        RevisionInfo[] allInfos = new RevisionInfo[ids.size()];
        List<RevisionInfo> infosToLoad;

        synchronized (revisionStateLock)
        {
          if (!isStablePermissionGeneration(generation))
          {
            continue;
          }

          infosToLoad = createRevisionInfos(ids, branchPoint, config, allInfos);
        }

        List<RevisionInfo> additionalInfos = null;

        if (infosToLoad != null)
        {
          additionalInfos = loadRevisionInfos(infosToLoad, branchPoint, config);
        }

        List<CDORevision> loadedAdditionalRevisions = new ArrayList<>();
        SyntheticCDORevision[] attemptSynthetics = synthetics == null ? null : new SyntheticCDORevision[synthetics.length];
        List<CDORevision> primaryRevisions;

        synchronized (revisionStateLock)
        {
          if (!isStablePermissionGeneration(generation))
          {
            continue;
          }

          if (additionalInfos != null)
          {
            for (RevisionInfo info : additionalInfos)
            {
              processResult(info, loadedAdditionalRevisions, null, 0, null);
            }
          }

          primaryRevisions = processResults(allInfos, attemptSynthetics, null);
        }

        dispatchCacheEvents();

        synchronized (revisionStateLock)
        {
          if (!isStablePermissionGeneration(generation))
          {
            continue;
          }

          if (requiresHeadResolution(allInfos, additionalInfos, batch))
          {
            if (++headResolutionRetries > MAX_HEAD_RESOLUTION_RETRIES)
            {
              throw new IllegalStateException("Revision results kept becoming invalid at the requested branch head"); //$NON-NLS-1$
            }

            continue;
          }
        }

        additionalRevisions.addAll(loadedAdditionalRevisions);

        if (synthetics != null && attemptSynthetics != null)
        {
          System.arraycopy(attemptSynthetics, 0, synthetics, 0, synthetics.length);
        }

        if (consumer != null)
        {
          if (additionalInfos != null)
          {
            for (RevisionInfo info : additionalInfos)
            {
              deliverResult(info, consumer);
            }
          }

          for (RevisionInfo info : allInfos)
          {
            deliverResult(info, consumer);
          }
        }

        if (infosToLoad != null && (!ObjectUtil.isEmpty(primaryRevisions) || !ObjectUtil.isEmpty(additionalRevisions)))
        {
          if (primaryRevisions == null)
          {
            primaryRevisions = Collections.emptyList();
          }

          List<CDORevision> loadedRevisions = Collections.unmodifiableList(new ArrayList<>(additionalRevisions));
          fireEvent(new RevisionsLoadedEvent(this, primaryRevisions, loadedRevisions, config.getPrefetchDepth()));
        }

        return primaryRevisions;
      }
      finally
      {
        endLoadBatch(batch);
      }
    }
  }

  /**
   * @since 4.15
   */
  @Override
  public void prefetchRevisions(CDOID id, CDOBranchPoint branchPoint, int prefetchDepth, boolean prefetchLockStates, Consumer<CDORevision> consumer)
  {
    List<CDOID> ids = Collections.singletonList(id);
    Request.Config config = new Request.Config(LookupMode.CACHE_THEN_LOADER, prefetchDepth, prefetchLockStates, CDORevision.UNCHUNKED);
    getRevisions(ids, branchPoint, config, null, null, consumer);
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    revisionLoader.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
  }

  @Override
  public String toString()
  {
    return "RevisionManager";
  }

  /**
   * Creates revision infos for all ids in the allInfos[] array and returns only those that need loading (or null).
   */
  private List<RevisionInfo> createRevisionInfos(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, RevisionInfo[] allInfos)
  {
    List<RevisionInfo> infosToLoad = null;
    boolean prefetching = config.getPrefetchDepth() != CDORevision.DEPTH_NONE;
    LookupMode lookupMode = config.getLookupMode();

    Iterator<CDOID> idIterator = ids.iterator();
    for (int i = 0; i < allInfos.length; i++)
    {
      CDOID id = idIterator.next();
      RevisionInfo info = lookupMode == LookupMode.LOADER_ONLY ? createRevisionInfoMissing(id, branchPoint) : createRevisionInfo(id, branchPoint);
      allInfos[i] = info;

      if (lookupMode != LookupMode.CACHE_ONLY && (lookupMode == LookupMode.LOADER_ONLY || prefetching || info.isLoadNeeded()))
      {
        if (infosToLoad == null)
        {
          infosToLoad = new ArrayList<>(1);
        }

        infosToLoad.add(info);
      }
    }

    return infosToLoad;
  }

  private RevisionInfo createRevisionInfo(CDOID id, CDOBranchPoint branchPoint)
  {
    InternalCDORevision revision = getCachedRevision(id, branchPoint);
    if (revision != null)
    {
      return createRevisionInfoAvailable(revision, branchPoint);
    }

    if (supportingBranches)
    {
      revision = getCachedRevisionRecursively(id, branchPoint);
      if (revision != null)
      {
        return createRevisionInfoAvailable(revision, branchPoint);
      }
    }

    return createRevisionInfoMissing(id, branchPoint);
  }

  private RevisionInfo.Available createRevisionInfoAvailable(InternalCDORevision revision, CDOBranchPoint requestedBranchPoint)
  {
    CDOID id = revision.getID();

    if (revision instanceof PointerCDORevision)
    {
      CDOBranchVersion target = ((PointerCDORevision)revision).getTarget();
      InternalCDORevision targetRevision = target == null ? null : getCachedRevisionByVersion(id, target);
      if (targetRevision != null)
      {
        target = targetRevision;
      }

      return new RevisionInfo.Available.Pointer(id, requestedBranchPoint, revision, target);
    }

    if (revision instanceof DetachedCDORevision)
    {
      return new RevisionInfo.Available.Detached(id, requestedBranchPoint, revision);
    }

    return new RevisionInfo.Available.Normal(id, requestedBranchPoint, revision);
  }

  private RevisionInfo.Missing createRevisionInfoMissing(CDOID id, CDOBranchPoint requestedBranchPoint)
  {
    return new RevisionInfo.Missing(id, requestedBranchPoint);
  }

  /**
   * Loads the requested revision infos, then processes and returns additional revisions.
   */
  protected List<RevisionInfo> loadRevisionInfos(List<RevisionInfo> infosToLoad, CDOBranchPoint branchPoint, Request.Config config)
  {
    List<RevisionInfo> additionalRevisionInfos;

    if (revisionLoader instanceof RevisionLoader4)
    {
      additionalRevisionInfos = ((RevisionLoader4)revisionLoader).loadRevisions(infosToLoad, branchPoint, config);
    }
    else
    {
      int referenceChunk = config.getReferenceChunk() == CDORevisionManager.Request.Config.REFERENCE_CHUNK_UNSPECIFIED //
          ? CDORevision.UNCHUNKED //
          : config.getReferenceChunk();

      additionalRevisionInfos = ((RevisionLoader3)revisionLoader).loadRevisions(infosToLoad, branchPoint, referenceChunk, config.getPrefetchDepth(),
          config.isPrefetchLockStates());
    }

    return additionalRevisionInfos;
  }

  private static Request.Config legacyConfig(int referenceChunk, int prefetchDepth, boolean prefetchLockStates, boolean loadOnDemand)
  {
    LookupMode lookupMode = loadOnDemand ? LookupMode.CACHE_THEN_LOADER : LookupMode.CACHE_ONLY;
    return new Request.Config(lookupMode, prefetchDepth, prefetchLockStates, referenceChunk);
  }

  private List<CDORevision> processResults(RevisionInfo[] infos, SyntheticCDORevision[] synthetics, Consumer<CDORevision> consumer)
  {
    List<CDORevision> results = new ArrayList<>(infos.length);
    for (int i = 0; i < infos.length; i++)
    {
      processResult(infos[i], results, synthetics, i, consumer);
    }

    return results;
  }

  private void processResult(RevisionInfo info, List<CDORevision> results, SyntheticCDORevision[] synthetics, int i, Consumer<CDORevision> consumer)
  {
    info.processResult(this, results, synthetics, i);

    if (consumer != null)
    {
      InternalCDORevision revision = info.getSynthetic();
      if (revision == null)
      {
        revision = info.getResult();
      }

      if (revision != null)
      {
        consumer.accept(revision);
      }
    }
  }

  private void deliverResult(RevisionInfo info, Consumer<CDORevision> consumer)
  {
    InternalCDORevision revision = info.getSynthetic();
    if (revision == null)
    {
      revision = info.getResult();
    }

    if (revision != null)
    {
      consumer.accept(revision);
    }
  }

  @Override
  public CDORevision internRevision(CDORevision revision)
  {
    return publishRevision(revision, currentLoadBatch.get());
  }

  private boolean requiresHeadResolution(RevisionInfo[] primaryInfos, List<RevisionInfo> additionalInfos, LoadBatch batch)
  {
    for (RevisionInfo info : primaryInfos)
    {
      if (requiresHeadResolution(info, batch))
      {
        return true;
      }
    }

    if (additionalInfos != null)
    {
      for (RevisionInfo info : additionalInfos)
      {
        if (requiresHeadResolution(info, batch))
        {
          return true;
        }
      }
    }

    return false;
  }

  private boolean requiresHeadResolution(RevisionInfo info, LoadBatch batch)
  {
    CDOBranchPoint requestedBranchPoint = info.getRequestedBranchPoint();
    if (requestedBranchPoint == null)
    {
      requestedBranchPoint = batch.requestedBranchPoint;
    }

    if (requestedBranchPoint == null || requestedBranchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return false;
    }

    InternalCDORevision revision = info.getResult();
    SyntheticCDORevision synthetic = info.getSynthetic();

    if (revision != null && !isValidAtRequestedPoint(revision, requestedBranchPoint))
    {
      return true;
    }

    if (synthetic != null && !isValidAtRequestedPoint(synthetic, requestedBranchPoint))
    {
      return true;
    }

    for (RevisionInvalidation invalidation : batch.invalidations)
    {
      if (invalidation.evicted || !invalidation.id.equals(info.getID()))
      {
        continue;
      }

      CDOBranchPoint effectivePoint = getEffectiveBranchPoint(requestedBranchPoint, invalidation.branch);
      if (effectivePoint == null || !isAtOrAfter(effectivePoint, invalidation))
      {
        continue;
      }

      if (invalidation.version == CDOBranchVersion.UNSPECIFIED_VERSION)
      {
        if (revision == null && synthetic == null || revision != null && !revision.isHistorical())
        {
          return true;
        }
      }
      else if (revision == null && synthetic == null)
      {
        return true;
      }
      else if (revision != null && revision.getBranch() == invalidation.branch && revision.getVersion() == invalidation.version)
      {
        return true;
      }
      else if (synthetic != null && synthetic.getBranch() == invalidation.branch && synthetic.getVersion() == invalidation.version)
      {
        if (invalidation.detached //
            && synthetic instanceof DetachedCDORevision //
            && invalidation.timeStamp != CDORevision.UNSPECIFIED_DATE //
            && synthetic.getTimeStamp() >= invalidation.timeStamp)
        {
          continue;
        }

        return true;
      }
    }

    return false;
  }

  private boolean isValidAtRequestedPoint(CDORevision revision, CDOBranchPoint requestedBranchPoint)
  {
    CDOBranchPoint effectivePoint = getEffectiveBranchPoint(requestedBranchPoint, revision.getBranch());
    if (effectivePoint == null)
    {
      return false;
    }

    if (effectivePoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return !revision.isHistorical();
    }

    return revision.isValid(effectivePoint);
  }

  private CDOBranchPoint getEffectiveBranchPoint(CDOBranchPoint branchPoint, CDOBranch targetBranch)
  {
    CDOBranchPoint effectivePoint = branchPoint;

    while (effectivePoint.getBranch().getID() != targetBranch.getID())
    {
      CDOBranch branch = effectivePoint.getBranch();
      if (branch.isMainBranch())
      {
        return null;
      }

      effectivePoint = branch.getBase();
    }

    return effectivePoint;
  }

  private boolean isAtOrAfter(CDOBranchPoint branchPoint, RevisionInvalidation invalidation)
  {
    if (invalidation.timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      return branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE && branchPoint.getBranch().getID() == invalidation.branch.getID();
    }

    return branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE || branchPoint.getTimeStamp() >= invalidation.timeStamp;
  }

  private CDORevision publishRevision(CDORevision revision, LoadBatch batch)
  {
    if (revision == null)
    {
      return null;
    }

    boolean dispatchEvents = !Thread.holdsLock(revisionStateLock);
    CDORevision publishedRevision = revision;

    synchronized (revisionStateLock)
    {
      boolean suppressPublication = batch != null && batch.invalidatedAll;

      if (batch != null && batch.invalidatedBranches.contains(revision.getBranch()))
      {
        suppressPublication = true;
      }

      for (RevisionInvalidation invalidation : batch == null ? Collections.<RevisionInvalidation> emptyList() : batch.invalidations)
      {
        if (suppressPublication)
        {
          break;
        }

        if (invalidation.id.equals(revision.getID()) && invalidation.branch == revision.getBranch())
        {
          if (invalidation.detached)
          {
            if (revision instanceof DetachedCDORevision && revision.getTimeStamp() == invalidation.timeStamp)
            {
              continue;
            }

            if (invalidation.version == CDOBranchVersion.UNSPECIFIED_VERSION)
            {
              if (!revision.isHistorical())
              {
                suppressPublication = true;
                break;
              }
            }
            else if (invalidation.version == revision.getVersion())
            {
              if (supportingAudits && invalidation.timeStamp != CDORevision.UNSPECIFIED_DATE)
              {
                ((InternalCDORevision)revision).setRevised(invalidation.timeStamp - 1);
              }
              else
              {
                suppressPublication = true;
                break;
              }
            }
          }

          if (invalidation.evicted && invalidation.version == revision.getVersion())
          {
            suppressPublication = true;
            break;
          }

          if (!invalidation.detached && !invalidation.evicted && invalidation.version == revision.getVersion())
          {
            if (invalidation.timeStamp == CDORevision.UNSPECIFIED_DATE)
            {
              suppressPublication = true;
              break;
            }

            ((InternalCDORevision)revision).setRevised(invalidation.timeStamp - 1);

            if (!supportingAudits)
            {
              suppressPublication = true;
              break;
            }
          }
        }
      }

      if (!suppressPublication)
      {
        int oldVersion = revision.getVersion() - 1;
        if (oldVersion >= CDORevision.UNSPECIFIED_VERSION)
        {
          CDOID id = revision.getID();
          CDOBranchVersion old = revision.getBranch().getVersion(oldVersion);

          if (oldVersion >= CDOBranchVersion.FIRST_VERSION //
              && !(revision instanceof SyntheticCDORevision) //
              && revision.getTimeStamp() != CDORevision.UNSPECIFIED_DATE)
          {
            recordInvalidation(new RevisionInvalidation(id, revision.getBranch(), oldVersion, revision.getTimeStamp(), false, false));
          }

          InternalCDORevision oldRevision = getCachedRevisionByVersion(id, old);

          if (!revision.isHistorical())
          {
            if (oldRevision != null)
            {
              oldRevision.setRevised(revision.getTimeStamp() - 1);
            }
            else
            {
              InternalCDORevision cachedLatestRevision = getCachedRevision(id, revision);
              if (cachedLatestRevision != null && cachedLatestRevision.getVersion() != revision.getVersion() && !cachedLatestRevision.isHistorical())
              {
                cache.removeRevision(id, cachedLatestRevision);
              }
            }
          }
        }

        InternResult internResult = cache.internRevisionDeferredEvents(revision);
        publishedRevision = internResult.getRevision();

        Runnable eventDispatcher = internResult.getEventDispatcher();
        if (eventDispatcher != null)
        {
          cacheEvents.addLast(eventDispatcher);
        }
      }
    }

    if (dispatchEvents)
    {
      dispatchCacheEvents();
    }

    return publishedRevision;
  }

  private void dispatchCacheEvents()
  {
    synchronized (revisionStateLock)
    {
      if (dispatchingCacheEvents || cacheEventDeferrals != 0 || permissionUpdates != 0 || permissionUpdatesClosed)
      {
        return;
      }

      dispatchingCacheEvents = true;
    }

    Throwable failure = null;
    boolean completed = false;

    try
    {
      for (;;)
      {
        Runnable eventDispatcher;

        synchronized (revisionStateLock)
        {
          if (cacheEventDeferrals != 0 || permissionUpdates != 0 || permissionUpdatesClosed)
          {
            dispatchingCacheEvents = false;
            completed = true;
            break;
          }

          eventDispatcher = cacheEvents.pollFirst();
          if (eventDispatcher == null)
          {
            dispatchingCacheEvents = false;
            completed = true;
            break;
          }
        }

        try
        {
          eventDispatcher.run();
        }
        catch (RuntimeException | Error ex)
        {
          if (failure == null)
          {
            failure = ex;
          }
        }
      }
    }
    finally
    {
      if (!completed)
      {
        synchronized (revisionStateLock)
        {
          dispatchingCacheEvents = false;
        }
      }
    }

    if (failure instanceof RuntimeException)
    {
      throw (RuntimeException)failure;
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();

    synchronized (revisionStateLock)
    {
      permissionUpdatesClosed = false;
    }

    if (factory == null)
    {
      factory = CDORevisionFactory.DEFAULT;
    }

    if (cache == null)
    {
      cache = (InternalCDORevisionCache)CDORevisionUtil.createRevisionCache(supportingAudits, supportingBranches);
    }

    if (cache instanceof AbstractCDORevisionCache)
    {
      String name = revisionLoader.toString();
      ((AbstractCDORevisionCache)cache).setName(name);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(cache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (revisionStateLock)
    {
      permissionUpdatesClosed = true;
      cacheEvents.clear();
      revisionStateLock.notifyAll();
    }

    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  private InternalCDORevision getCachedRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return (InternalCDORevision)cache.getRevisionByVersion(id, branchVersion);
  }

  private InternalCDORevision getCachedRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return (InternalCDORevision)cache.getRevision(id, branchPoint);
  }

  private InternalCDORevision getCachedRevisionRecursively(CDOID id, CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    if (!branch.isMainBranch())
    {
      CDOBranchPoint base = branch.getBase();
      InternalCDORevision revision = getCachedRevision(id, base);
      if (revision != null)
      {
        return revision;
      }

      // Recurse
      return getCachedRevisionRecursively(id, base);
    }

    // Reached main branch
    return null;
  }

  private LoadBatch beginLoadBatch()
  {
    LoadBatch batch = new LoadBatch(currentLoadBatch.get());

    synchronized (revisionStateLock)
    {
      loadBatches.add(batch);
    }

    currentLoadBatch.set(batch);
    return batch;
  }

  private void endLoadBatch(LoadBatch batch)
  {
    synchronized (revisionStateLock)
    {
      loadBatches.remove(batch);
    }

    if (batch.parent == null)
    {
      currentLoadBatch.remove();
    }
    else
    {
      currentLoadBatch.set(batch.parent);
    }
  }

  private void recordInvalidation(RevisionInvalidation invalidation)
  {
    for (LoadBatch batch : loadBatches)
    {
      batch.invalidations.add(invalidation);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LoadBatch
  {
    public final LoadBatch parent;

    public final List<RevisionInvalidation> invalidations = new ArrayList<>();

    public final List<CDOBranch> invalidatedBranches = new ArrayList<>();

    public CDOBranchPoint requestedBranchPoint;

    public boolean invalidatedAll;

    public LoadBatch(LoadBatch parent)
    {
      this.parent = parent;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RevisionInvalidation
  {
    public final CDOID id;

    public final CDOBranch branch;

    public final int version;

    public final long timeStamp;

    public final boolean detached;

    public final boolean evicted;

    public RevisionInvalidation(CDOID id, CDOBranch branch, int version, long timeStamp, boolean detached, boolean evicted)
    {
      this.id = id;
      this.branch = branch;
      this.version = version;
      this.timeStamp = timeStamp;
      this.detached = detached;
      this.evicted = evicted;
    }
  }

  /**
   * @author Esteban Dugueperoux
   */
  private static final class RevisionsLoadedEvent extends Event implements CDORevisionsLoadedEvent
  {
    private static final long serialVersionUID = 1L;

    private List<? extends CDORevision> primaryLoadedRevisions;

    private List<? extends CDORevision> additionalLoadedRevisions;

    private int prefetchDepth;

    public RevisionsLoadedEvent(CDORevisionManager revisionManager, List<? extends CDORevision> primaryLoadedRevisions,
        List<? extends CDORevision> additionalLoadedRevisions, int prefetchDepth)
    {
      super(revisionManager);
      this.primaryLoadedRevisions = primaryLoadedRevisions;
      this.additionalLoadedRevisions = additionalLoadedRevisions;
      this.prefetchDepth = prefetchDepth;
    }

    @Override
    public CDORevisionManager getSource()
    {
      return (CDORevisionManager)super.getSource();
    }

    @Override
    public List<? extends CDORevision> getPrimaryLoadedRevisions()
    {
      return primaryLoadedRevisions;
    }

    @Override
    public List<? extends CDORevision> getAdditionalLoadedRevisions()
    {
      return additionalLoadedRevisions;
    }

    @Override
    public int getPrefetchDepth()
    {
      return prefetchDepth;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "prefetchDepth=" + prefetchDepth + ", primaryLoadedRevisions=" + primaryLoadedRevisions + ", additionalLoadedRevisions="
          + additionalLoadedRevisions;
    }
  }

  @Deprecated
  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand)
  {
    return getRevisionByVersion(id, branchVersion, legacyConfig(referenceChunk, CDORevision.DEPTH_NONE, false, loadOnDemand));
  }

  @Deprecated
  @Override
  public InternalCDORevision getBaseRevision(CDORevision revision, int referenceChunk, boolean loadOnDemand)
  {
    return getBaseRevision(revision, legacyConfig(referenceChunk, CDORevision.DEPTH_NONE, false, loadOnDemand));
  }

  @Deprecated
  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    return getRevision(id, branchPoint, legacyConfig(referenceChunk, prefetchDepth, false, loadOnDemand), null);
  }

  @Deprecated
  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    return getRevision(id, branchPoint, legacyConfig(referenceChunk, prefetchDepth, false, loadOnDemand), synthetics);
  }

  @Deprecated
  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    return getRevisions(ids, branchPoint, legacyConfig(referenceChunk, prefetchDepth, false, loadOnDemand), (SyntheticCDORevision[])null);
  }

  @Deprecated
  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    return getRevisions(ids, branchPoint, legacyConfig(referenceChunk, prefetchDepth, false, loadOnDemand), synthetics);
  }

  @Deprecated
  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean prefetchLockStates,
      boolean loadOnDemand, SyntheticCDORevision[] synthetics)
  {
    return getRevisions(ids, branchPoint, legacyConfig(referenceChunk, prefetchDepth, prefetchLockStates, loadOnDemand), synthetics);
  }

  @Deprecated
  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      List<CDORevision> additionalRevisions)
  {
    return getRevisions(ids, branchPoint, legacyConfig(referenceChunk, prefetchDepth, false, loadOnDemand), null, additionalRevisions);
  }

  @Deprecated
  @Override
  public void addRevision(CDORevision revision)
  {
    AbstractCDORevisionCache.addRevision(revision, this);
  }
}
