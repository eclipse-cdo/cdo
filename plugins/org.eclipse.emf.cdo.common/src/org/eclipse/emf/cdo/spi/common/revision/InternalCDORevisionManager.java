/*
 * Copyright (c) 2009-2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics);

  /**
   * @since 4.15
   */
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean prefetchLockStates,
      boolean loadOnDemand, SyntheticCDORevision[] synthetics);

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics);

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand);

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand);

  /**
   * @since 4.8
   */
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
