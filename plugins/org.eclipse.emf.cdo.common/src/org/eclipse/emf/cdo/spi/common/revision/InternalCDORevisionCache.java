/*
 * Copyright (c) 2010-2013, 2015, 2021, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionInterner;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.List;

/**
 * Internal cache and interner for revisions shared by a revision manager. It indexes revisions by object and branch
 * version, supports branch-scoped eviction, and participates in the manager's lifecycle.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDORevisionCache extends CDORevisionCache, CDORevisionInterner, CDOAllRevisionsProvider, ILifecycle
{
  public InternalCDORevisionCache instantiate(CDORevision revision);

  public CDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion);

  /**
   * @since 4.15
   */
  public void removeRevisions(CDOBranch... branches);

  public void clear();

  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint);

  /**
   * Fills a list with all {@link CDORevision revisions}.
   *
   * @since 4.3
   * @see #getAllRevisions()
   */
  public void getAllRevisions(List<InternalCDORevision> result);

  /**
   * Interns a revision atomically and returns any cache event for delivery after the caller's publication lock has been
   * released. Implementations must not execute the returned callback as part of this method.
   *
   * @param revision the revision to intern
   * @return the canonical revision and an optional deferred event callback
   * @since 4.29
   */
  public InternResult internRevisionDeferredEvents(CDORevision revision);

  /**
   * Result of an atomic cache interning operation whose event delivery is deferred to the caller.
   *
   * @author Eike Stepper
   * @since 4.29
   */
  public static final class InternResult
  {
    private final CDORevision revision;

    private final Runnable eventDispatcher;

    /**
     * Creates an interning result.
     *
     * @param revision the canonical revision returned by the cache
     * @param eventDispatcher the optional callback that delivers the cache event
     */
    public InternResult(CDORevision revision, Runnable eventDispatcher)
    {
      this.revision = revision;
      this.eventDispatcher = eventDispatcher;
    }

    /**
     * Returns the canonical revision selected by the cache.
     *
     * @return the canonical revision
     */
    public CDORevision getRevision()
    {
      return revision;
    }

    /**
     * Returns the optional deferred cache-event callback.
     *
     * @return the callback, or {@code null} if interning did not add a revision
     */
    public Runnable getEventDispatcher()
    {
      return eventDispatcher;
    }
  }
}
