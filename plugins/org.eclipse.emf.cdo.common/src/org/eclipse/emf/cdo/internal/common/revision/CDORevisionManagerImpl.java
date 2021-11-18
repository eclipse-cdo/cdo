/*
 * Copyright (c) 2009-2017, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionsLoadedEvent;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends Lifecycle implements InternalCDORevisionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionManagerImpl.class);

  private boolean supportingAudits;

  private boolean supportingBranches;

  private RevisionLoader revisionLoader;

  private RevisionLocker revisionLocker;

  private CDORevisionFactory factory;

  private InternalCDORevisionCache cache;

  @ExcludeFromDump
  private transient Object loadAndAddLock = new Object()
  {
    @Override
    public String toString()
    {
      return "LoadAndAddLock"; //$NON-NLS-1$
    }
  };

  @ExcludeFromDump
  private transient Object reviseLock = new Object()
  {
    @Override
    public String toString()
    {
      return "ReviseLock"; //$NON-NLS-1$
    }
  };

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
  public RevisionLocker getRevisionLocker()
  {
    return revisionLocker;
  }

  @Override
  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    checkInactive();
    this.revisionLocker = revisionLocker;
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
      CDORevision revision = getRevisionByVersion(id, mainBranch.getVersion(CDOBranchVersion.FIRST_VERSION), 0, true);
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
      return getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, false, (SyntheticCDORevision[])null) != null;
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
    acquireAtomicRequestLock(reviseLock);

    try
    {
      InternalCDORevision revision = (InternalCDORevision)cache.getRevision(id, branch.getHead());
      if (revision != null)
      {
        CDOBranchVersion version = branch.getVersion(revision.getVersion());
        cache.removeRevision(id, version);
      }
    }
    finally
    {
      releaseAtomicRequestLock(reviseLock);
    }
  }

  @Override
  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    acquireAtomicRequestLock(reviseLock);

    try
    {
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
    finally
    {
      releaseAtomicRequestLock(reviseLock);
    }
  }

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand)
  {
    checkArg(branchVersion.getVersion() >= CDOBranchVersion.FIRST_VERSION, "Invalid version: " + branchVersion.getVersion());
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} from {1}", id, branchVersion); //$NON-NLS-1$
          }

          revision = revisionLoader.loadRevisionByVersion(id, branchVersion, referenceChunk);
          revision = (InternalCDORevision)internRevision(revision);
        }
      }

      return revision;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  @Override
  public InternalCDORevision getBaseRevision(CDORevision revision, int referenceChunk, boolean loadOnDemand)
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
      return getRevision(id, basePoint, referenceChunk, CDORevision.DEPTH_NONE, loadOnDemand);
    }

    CDOBranchVersion baseVersion = branch.getVersion(version - 1);
    return getRevisionByVersion(id, baseVersion, referenceChunk, loadOnDemand);
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
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    return getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, (SyntheticCDORevision[])null);
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    List<CDOID> ids = Collections.singletonList(id);
    List<CDORevision> results = getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
    return (InternalCDORevision)results.get(0);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    return getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, (SyntheticCDORevision[])null);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    return getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, false, loadOnDemand, synthetics, null, null);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean prefetchLockStates,
      boolean loadOnDemand, SyntheticCDORevision[] synthetics)
  {
    return getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, prefetchLockStates, loadOnDemand, synthetics, null, null);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      List<CDORevision> additionalRevisions)
  {
    return getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, false, loadOnDemand, null, additionalRevisions, null);
  }

  private List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean prefetchLockStates,
      boolean loadOnDemand, SyntheticCDORevision[] synthetics, List<CDORevision> additionalRevisions, Consumer<CDORevision> consumer)
  {
    RevisionInfo[] allInfos = new RevisionInfo[ids.size()];

    if (additionalRevisions == null)
    {
      additionalRevisions = new ArrayList<>();
    }

    // Create revision infos for all ids in the allInfos[] array and return only those that need loading (or null).
    List<RevisionInfo> infosToLoad = createRevisionInfos(ids, branchPoint, prefetchDepth, loadOnDemand, allInfos);
    if (infosToLoad != null)
    {
      // Load the requested revision infos, then process the additional revisions.
      loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth, prefetchLockStates, additionalRevisions, consumer);
    }

    List<CDORevision> primaryRevisions = processResults(allInfos, synthetics, consumer);

    if (infosToLoad != null)
    {
      if (!ObjectUtil.isEmpty(primaryRevisions) || !ObjectUtil.isEmpty(additionalRevisions))
      {
        if (primaryRevisions == null)
        {
          primaryRevisions = Collections.emptyList();
        }

        additionalRevisions = Collections.unmodifiableList(additionalRevisions);
        fireEvent(new RevisionsLoadedEvent(this, primaryRevisions, additionalRevisions, prefetchDepth));
      }
    }

    return primaryRevisions;
  }

  /**
   * @since 4.15
   */
  @Override
  public void prefetchRevisions(CDOID id, CDOBranchPoint branchPoint, int prefetchDepth, boolean prefetchLockStates, Consumer<CDORevision> consumer)
  {
    List<CDOID> ids = Collections.singletonList(id);
    getRevisions(ids, branchPoint, CDORevision.UNCHUNKED, prefetchDepth, prefetchLockStates, true, null, null, consumer);
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
  private List<RevisionInfo> createRevisionInfos(List<CDOID> ids, CDOBranchPoint branchPoint, int prefetchDepth, boolean loadOnDemand, RevisionInfo[] allInfos)
  {
    List<RevisionInfo> infosToLoad = null;
    boolean prefetching = prefetchDepth != CDORevision.DEPTH_NONE;

    Iterator<CDOID> idIterator = ids.iterator();
    for (int i = 0; i < allInfos.length; i++)
    {
      CDOID id = idIterator.next();
      RevisionInfo info = createRevisionInfo(id, branchPoint);
      allInfos[i] = info;

      if (loadOnDemand && (prefetching || info.isLoadNeeded()))
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
  protected void loadRevisions(List<RevisionInfo> infosToLoad, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean prefetchLockStates,
      List<CDORevision> additionalRevisions, Consumer<CDORevision> consumer)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      @SuppressWarnings("deprecation")
      List<RevisionInfo> additionalRevisionInfos = revisionLoader instanceof RevisionLoader3
          ? ((RevisionLoader3)revisionLoader).loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth, prefetchLockStates)
          : revisionLoader.loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth);

      if (additionalRevisionInfos != null)
      {
        for (RevisionInfo info : additionalRevisionInfos)
        {
          processResult(info, additionalRevisions, null, 0, consumer);
        }
      }
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
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

  @Override
  public CDORevision internRevision(CDORevision revision)
  {
    if (revision != null)
    {
      acquireAtomicRequestLock(loadAndAddLock);

      try
      {
        int oldVersion = revision.getVersion() - 1;
        if (oldVersion >= CDORevision.UNSPECIFIED_VERSION)
        {
          CDOID id = revision.getID();
          CDOBranchVersion old = revision.getBranch().getVersion(oldVersion);

          InternalCDORevision oldRevision = getCachedRevisionByVersion(id, old);
          if (!revision.isHistorical())
          {
            if (oldRevision != null)
            {
              oldRevision.setRevised(revision.getTimeStamp() - 1);
            }
            else
            {
              // Remove last revision from cache, which is not revised
              InternalCDORevision cachedLatestRevision = getCachedRevision(id, revision);
              if (cachedLatestRevision != null && !cachedLatestRevision.isHistorical())
              {
                // Found revision is stale.
                // We cannot revise it now because of lack information, thus remove it from the cache
                cache.removeRevision(id, cachedLatestRevision);
              }
            }
          }
        }

        revision = cache.internRevision(revision);
      }
      finally
      {
        releaseAtomicRequestLock(loadAndAddLock);
      }
    }

    return revision;
  }

  @Deprecated
  @Override
  public void addRevision(CDORevision revision)
  {
    AbstractCDORevisionCache.addRevision(revision, this);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
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
    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  private void acquireAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.acquireAtomicRequestLock(key);
    }
  }

  private void releaseAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.releaseAtomicRequestLock(key);
    }
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

  /**
   * @author Esteban Dugueperoux
   */
  private static class RevisionsLoadedEvent extends Event implements CDORevisionsLoadedEvent
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
}
