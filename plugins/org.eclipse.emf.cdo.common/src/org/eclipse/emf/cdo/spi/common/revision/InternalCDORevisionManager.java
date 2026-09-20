/*
 * Copyright (c) 2009-2016, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionInterner;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.ecore.EClass;

import java.util.List;
import java.util.function.Consumer;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface InternalCDORevisionManager extends CDORevisionManager, CDORevisionInterner, CDORevisionCacheAdder, ILifecycle
{
  /**
   * @since 4.0
   */
  public boolean isSupportingAudits();

  /**
   * @since 4.0
   */
  public void setSupportingAudits(boolean on);

  public boolean isSupportingBranches();

  public void setSupportingBranches(boolean on);

  public RevisionLoader getRevisionLoader();

  public void setRevisionLoader(RevisionLoader revisionLoader);

  public RevisionLocker getRevisionLocker();

  public void setRevisionLocker(RevisionLocker revisionLocker);

  public CDORevisionFactory getFactory();

  public void setFactory(CDORevisionFactory factory);

  public InternalCDORevisionCache getCache();

  /**
   * @since 4.0
   */
  public void setCache(CDORevisionCache cache);

  /**
   * Called on client via postCommit when there is no version of detached objects available.
   */
  public void reviseLatest(CDOID id, CDOBranch branch);

  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp);

  /**
   * @since 4.15
   */
  public void prefetchRevisions(CDOID id, CDOBranchPoint branchPoint, int prefetchDepth, boolean prefetchLockStates, Consumer<CDORevision> consumer);

  /**
   * Loads revisions through the canonical configuration-based path. Synthetic revisions are a result channel and are
   * deliberately kept separate from the request configuration.
   *
   * @param ids the IDs of the requested objects
   * @param branchPoint the branch point at which to load the revisions
   * @param config the immutable loading options
   * @param synthetics an optional array through which synthetic revisions are supplied
   * @return the requested revisions, with {@code null} entries for revisions that could not be found
   * @since 4.37
   */
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics);

  /**
   * Loads revisions through the canonical configuration-based path, preserving both result channels separately from
   * the request configuration.
   *
   * @param ids the IDs of the requested objects
   * @param branchPoint the branch point at which to load the revisions
   * @param config the immutable loading options
   * @param synthetics an optional array through which synthetic revisions are supplied
   * @param additionalRevisions an optional list for revisions prefetched in addition to the requested revisions
   * @return the requested revisions, with {@code null} entries for revisions that could not be found
   * @since 4.37
   */
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics,
      List<CDORevision> additionalRevisions);

  /**
   * Loads one revision through the canonical configuration-based path.
   *
   * @param id the ID of the requested object
   * @param branchPoint the branch point at which to load the revision
   * @param config the immutable loading options
   * @param synthetics an optional array through which a synthetic revision is supplied
   * @return the revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
   * @since 4.37
   */
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics);

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, Request.Config config);

  /**
   * Loads a revision by version through the canonical configuration-based path.
   *
   * @param id the ID of the requested object
   * @param branchVersion the branch version at which to load the revision
   * @param config the immutable loading options
   * @return the revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
   * @since 4.37
   */
  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config);

  /**
   * Loads the base revision using the canonical configuration-based path.
   *
   * @param revision the revision whose base revision is requested
   * @param config the immutable loading options
   * @return the base revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
   * @since 4.37
   */
  public InternalCDORevision getBaseRevision(CDORevision revision, Request.Config config);

  /**
   * @deprecated As of 4.37 use {@link #getRevisions(List, CDOBranchPoint, CDORevisionManager.Request.Config, SyntheticCDORevision[])}.
   */
  @Deprecated
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics);

  /**
   * @since 4.15
   * @deprecated As of 4.37 use the configuration-based {@link #getRevisions(List, CDOBranchPoint, CDORevisionManager.Request.Config, SyntheticCDORevision[])}.
   */
  @Deprecated
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean prefetchLockStates,
      boolean loadOnDemand, SyntheticCDORevision[] synthetics);

  /**
   * @deprecated As of 4.37 use {@link #getRevision(CDOID, CDOBranchPoint, CDORevisionManager.Request.Config, SyntheticCDORevision[])}.
   */
  @Deprecated
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics);

  /**
   * @deprecated As of 4.37 use {@link #getRevision(CDOID, CDOBranchPoint, CDORevisionManager.Request.Config)}.
   */
  @Deprecated
  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand);

  /**
   * @deprecated As of 4.37 use {@link #getRevisionByVersion(CDOID, CDOBranchVersion, CDORevisionManager.Request.Config)}.
   */
  @Deprecated
  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand);

  /**
   * @since 4.8
   * @deprecated As of 4.37 use {@link #getBaseRevision(CDORevision, CDORevisionManager.Request.Config)}.
   */
  @Deprecated
  public InternalCDORevision getBaseRevision(CDORevision revision, int referenceChunk, boolean loadOnDemand);

  /**
   * @deprecated As of 4.15 use {@link #internRevision(CDORevision)}.
   */
  @Deprecated
  @Override
  public void addRevision(CDORevision revision);

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 3.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface RevisionLoader
  {
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk);

    /**
     * @since 4.3
     */
    public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler);

    /**
     * @deprecated As of 4.15 use {@link RevisionLoader3#loadRevisions(List, CDOBranchPoint, int, int, boolean)}.
     */
    @Deprecated
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth);
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 4.4
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface RevisionLoader2 extends RevisionLoader
  {
    public CDOBranchPointRange loadObjectLifetime(CDOID id, CDOBranchPoint branchPoint);
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 4.15
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface RevisionLoader3 extends RevisionLoader2
  {
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
        boolean prefetchLockStates);
  }

  /**
   * The canonical revision-loading extension. Lookup mode is present in the configuration for consistency, but source
   * selection has already been made by the revision manager and must not be interpreted by this loader.
   *
   * @author Eike Stepper
   * @since 4.37
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface RevisionLoader4 extends RevisionLoader
  {
    /**
     * Loads one revision by version using the loading-related values in {@code config}.
     *
     * @param id the ID of the requested object
     * @param branchVersion the branch version at which to load the revision
     * @param config the immutable loading options; lookup mode is ignored because source selection belongs to the manager
     * @return the loaded revision, or {@code null} if no such revision exists
     * @since 4.37
     */
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config);

    /**
     * Loads the supplied revision infos using the loading-related values in {@code config}.
     *
     * @param infos the revision information records to load or update
     * @param branchPoint the branch point at which to load the revisions
     * @param config the immutable loading options; lookup mode is ignored because source selection belongs to the manager
     * @return the supplied records updated with loaded revision data, as defined by the loader contract
     * @since 4.37
     */
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, Request.Config config);
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 3.0
   */
  public interface RevisionLocker
  {
    public void acquireAtomicRequestLock(Object key);

    public void releaseAtomicRequestLock(Object key);
  }
}
