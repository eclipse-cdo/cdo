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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;

import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Objects;

/**
 * Provides access to {@link CDORevision revisions} in a CDO {@link CDOCommonRepository repository} by demand loading
 * and caching them.
 * <p>
 * Revisions are generally queried by:
 * <p>
 * <ul>
 * <li>their object {@link CDOID ID} <b>and</b>
 * <li>their {@link CDOBranch branch} plus <b>either</b>:
 * <ul>
 * <li>a timestamp <b>or</b>
 * <li>a version
 * </ul>
 * </ul>
 * <p>
 * If querying by timestamp it's also possible to ask for multiple revisions (identified by a list of object IDs) in one
 * round trip (to the server if this revision manager is contained by a {@link CDOCommonSession session} or to the
 * backend store if it is contained by a {@link CDOCommonRepository repository}.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionManager extends INotifier
{
  /**
   * Loads revisions using the canonical request configuration.
   *
   * @param id the ID of the requested object
   * @param branchPoint the branch point at which to load the revision
   * @param config the immutable loading configuration
   * @return the revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
   * @see #request() for a fluent request API
   * @since 4.37
   */
  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, Request.Config config);

  /**
   * Loads revisions using the canonical request configuration. Options that do not apply to this operation are ignored.
   *
   * @param ids the IDs of the requested objects
   * @param branchPoint the branch point at which to load the revisions
   * @param config the immutable loading configuration
   * @return the requested revisions, with {@code null} entries for revisions that could not be found
   * @see #request() for a fluent request API
   * @since 4.37
   */
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config);

  /**
   * Loads revisions using the canonical request configuration and collects additionally prefetched revisions in the
   * supplied result channel. Options that do not apply to this operation are ignored.
   *
   * @param ids the IDs of the requested objects
   * @param branchPoint the branch point at which to load the revisions
   * @param config the immutable loading configuration
   * @param additionalRevisions the optional result channel for additionally prefetched revisions
   * @return the requested revisions, with {@code null} entries for revisions that could not be found
   * @see #request() for a fluent request API
   * @since 4.37
   */
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, List<CDORevision> additionalRevisions);

  /**
   * Loads a revision by version using the canonical request configuration. Containment prefetch depth is ignored by
   * this operation because it does not support containment prefetching.
   *
   * @param id the ID of the requested object
   * @param branchVersion the branch version at which to load the revision
   * @param config the immutable loading configuration
   * @return the revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
   * @see #request() for a fluent request API
   * @since 4.37
   */
  public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config);

  /**
   * Returns the {@link CDORevision#getEClass() type} of an object if a revision for that object is in the revision
   * cache, <code>null</code> otherwise.
   * <p>
   * Same as calling {@link #getObjectType(CDOID, CDOBranchManager) getObjectType(id, null)}.
   *
   * @see EObject#eClass()
   * @see #getObjectType(CDOID, CDOBranchManager)
   */
  public EClass getObjectType(CDOID id);

  /**
   * Returns the {@link CDORevision#getEClass() type} of an object.
   * <p>
   * If no revision for that object is found in the revision cache the following is tried:
   * <ol>
   * <li>If <code>branchManagerForLoadOnDemand</code> is not <code>null</code> the first revision of the object in the
   * main branch is loaded from the server and its type is returned.
   * <li>Otherwise (i.e., if <code>branchManagerForLoadOnDemand</code> is <code>null</code> or the object does not
   * exist) <code>null</code> is returned.
   * </ol>
   * <p>
   * A {@link CDOBranchManager branch manager} is required instead of just a boolean value to specify whether to
   * demand-load or not because this revision manager must be able to access the
   * {@link CDOBranchManager#getMainBranch() main branch} while demand-loading.
   *
   * @see EObject#eClass()
   * @see #getObjectType(CDOID)
   * @since 4.1
   */
  public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand);

  /**
   * Returns <code>true</code> if the {@link CDORevisionCache revision cache} contains a {@link CDORevision revision}
   * with the given {@link CDOID ID} at the given {@link CDOBranchPoint branch point} (branch + timestamp),
   * <code>false</code> otherwise.
   *
   * @see CDORevisionManager#getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   * @see CDORevisionManager#getRevisions(List, CDOBranchPoint, int, int, boolean)
   */
  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint);

  /**
   * @since 4.4
   */
  public CDOBranchPointRange getObjectLifetime(CDOID id, CDOBranchPoint branchPoint);

  /**
   * @since 4.3
   */
  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler);

  /**
   * Creates a manager-bound fluent request for loading revisions. Each terminal operation takes an immutable snapshot of
   * the current options, so subsequent changes to the request do not affect an operation already in progress.
   *
   * @since 4.37
   */
  public default Request request()
  {
    return new Request()
    {
      private LookupMode lookupMode = Config.DEFAULT.getLookupMode();

      private int prefetchDepth = Config.DEFAULT.getPrefetchDepth();

      private boolean prefetchLockStates = Config.DEFAULT.isPrefetchLockStates();

      @Override
      public Request lookupMode(LookupMode lookupMode)
      {
        this.lookupMode = Objects.requireNonNull(lookupMode, "lookupMode"); //$NON-NLS-1$
        return this;
      }

      @Override
      public Request prefetchDepth(int prefetchDepth)
      {
        this.prefetchDepth = prefetchDepth;
        return this;
      }

      @Override
      public Request prefetchLockStates(boolean prefetchLockStates)
      {
        this.prefetchLockStates = prefetchLockStates;
        return this;
      }

      @Override
      public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
      {
        return CDORevisionManager.this.getRevision(id, branchPoint, snapshot());
      }

      @Override
      public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint)
      {
        return CDORevisionManager.this.getRevisions(ids, branchPoint, snapshot());
      }

      @Override
      public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
      {
        return CDORevisionManager.this.getRevisionByVersion(id, branchVersion, snapshot());
      }

      private Config snapshot()
      {
        return new Config(lookupMode, prefetchDepth, prefetchLockStates, CDORevision.UNCHUNKED);
      }
    };
  }

  /**
   * A fluent, manager-bound request for loading one or more revisions.
   *
   * @author Eike Stepper
   * @since 4.37
   */
  public interface Request
  {
    /**
     * Sets the lookup mode.
     *
     * @param lookupMode the cache and loader source-selection mode
     * @return this request for further configuration
     */
    public Request lookupMode(LookupMode lookupMode);

    /**
     * Sets the lookup mode to {@link LookupMode#CACHE_ONLY}.
     *
     * @return this request for further configuration
     */
    public default Request lookupCacheOnly()
    {
      return lookupMode(LookupMode.CACHE_ONLY);
    }

    /**
     * Sets the lookup mode to {@link LookupMode#LOADER_ONLY}.
     *
     * @return this request for further configuration
     */
    public default Request lookupLoaderOnly()
    {
      return lookupMode(LookupMode.LOADER_ONLY);
    }

    /**
     * Sets the lookup mode to {@link LookupMode#CACHE_THEN_LOADER}.
     *
     * @return this request for further configuration
     */
    public default Request lookupCacheThenLoader()
    {
      return lookupMode(LookupMode.CACHE_THEN_LOADER);
    }

    /**
     * Sets the containment prefetch depth.
     *
     * @param prefetchDepth the number of containment levels to prefetch, or a {@link CDORevision} depth constant
     * @return this request for further configuration
     */
    public Request prefetchDepth(int prefetchDepth);

    /**
     * Sets the containment prefetch depth to {@link CDORevision#DEPTH_NONE}.
     *
     * @return this request for further configuration
     */
    public default Request prefetchDepthNone()
    {
      return prefetchDepth(CDORevision.DEPTH_NONE);
    }

    /**
     * Sets the containment prefetch depth to {@link CDORevision#DEPTH_ONE}.
     *
     * @return this request for further configuration
     */
    public default Request prefetchDepthOne()
    {
      return prefetchDepth(CDORevision.DEPTH_ONE);
    }

    /**
     * Sets the containment prefetch depth to {@link CDORevision#DEPTH_INFINITE}.
     *
     * @return this request for further configuration
     */
    public default Request prefetchDepthInfinite()
    {
      return prefetchDepth(CDORevision.DEPTH_INFINITE);
    }

    /**
     * Sets whether lock-state prefetch is enabled.
     *
     * @param prefetchLockStates {@code true} to request lock-state prefetch where supported
     * @return this request for further configuration
     */
    public Request prefetchLockStates(boolean prefetchLockStates);

    /**
     * Enables lock-state prefetch.
     *
     * @return this request for further configuration
     */
    public default Request prefetchLockStates()
    {
      return prefetchLockStates(true);
    }

    /**
     * Executes a single-revision request at a branch point.
     *
     * @param id the ID of the requested object
     * @param branchPoint the branch point at which to load the revision
     * @return the revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
     */
    public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint);

    /**
     * Executes a multi-revision request at a branch point.
     *
     * @param ids the IDs of the requested objects
     * @param branchPoint the branch point at which to load the revisions
     * @return the requested revisions, with {@code null} entries for revisions that could not be found
     */
    public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint);

    /**
     * Executes a revision request by branch version.
     *
     * @param id the ID of the requested object
     * @param branchVersion the branch version at which to load the revision
     * @return the revision, or {@code null} if it does not exist or lookup is cache-only and the cache misses
     */
    public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion);

    /**
     * Immutable loading options shared by revision manager operations and modern revision loaders. The configuration is
     * intentionally a superset: an operation or loader ignores options that it does not support. The collection policy
     * is retained by reference, so concurrent reuse of a configuration also requires that policy implementation to be
     * thread-safe.
     *
     * @author Eike Stepper
     * @since 4.37
     */
    public static final class Config
    {
      /**
       * The default revision-loading configuration: cache then loader, no containment or lock-state prefetch, and the
       * effective session collection policy.
       *
       * @since 4.37
       */
      public static final Config DEFAULT = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false);

      public static final int REFERENCE_CHUNK_UNSPECIFIED = Integer.MIN_VALUE;

      private final LookupMode lookupMode;

      private final int prefetchDepth;

      private final boolean prefetchLockStates;

      private final int referenceChunk;

      /**
       * Creates an immutable configuration using an unchunked scalar reference chunk.
       *
       * @param lookupMode the source-selection mode
       * @param prefetchDepth the containment prefetch depth
       * @param prefetchLockStates whether lock states are prefetched where supported
       */
      public Config(LookupMode lookupMode, int prefetchDepth, boolean prefetchLockStates)
      {
        this(lookupMode, prefetchDepth, prefetchLockStates, REFERENCE_CHUNK_UNSPECIFIED);
      }

      /**
       * Creates an immutable configuration.
       *
       * @param lookupMode the source-selection mode
       * @param prefetchDepth the containment prefetch depth; operations without containment prefetch support ignore it
       * @param prefetchLockStates whether lock states are prefetched where supported
       * @param referenceChunk the scalar reference chunk for legacy loading paths
       */
      public Config(LookupMode lookupMode, int prefetchDepth, boolean prefetchLockStates, int referenceChunk)
      {
        this.lookupMode = Objects.requireNonNull(lookupMode, "lookupMode"); //$NON-NLS-1$
        this.prefetchDepth = prefetchDepth;
        this.prefetchLockStates = prefetchLockStates;
        this.referenceChunk = referenceChunk;
      }

      /**
       * Returns the lookup mode.
       *
       * @return the cache and loader source-selection mode
       */
      public LookupMode getLookupMode()
      {
        return lookupMode;
      }

      /**
       * Returns the containment prefetch depth, which is ignored by operations that do not support containment
       * prefetch.
       *
       * @return the number of containment levels to prefetch, or a {@link CDORevision} depth constant
       */
      public int getPrefetchDepth()
      {
        return prefetchDepth;
      }

      /**
       * Returns whether lock states should be prefetched where supported.
       *
       * @return {@code true} if lock states should be prefetched
       */
      public boolean isPrefetchLockStates()
      {
        return prefetchLockStates;
      }

      /**
       * Returns the scalar reference chunk for legacy loading paths.
       *
       * @return the scalar reference chunk
       */
      public int getReferenceChunk()
      {
        return referenceChunk;
      }

      /**
       * Controls whether a revision manager consults its cache before invoking its loader. A loader receives this value
       * but does not interpret it because source selection has already been made by the manager.
       *
       * @author Eike Stepper
       * @since 4.37
       */
      public enum LookupMode
      {
        /**
         * Consults the revision cache and returns {@code null} on a cache miss without invoking the loader.
         */
        CACHE_ONLY,

        /**
         * Bypasses the cache as a lookup source and invokes the loader directly. A normally loaded result may still be
         * cached.
         */
        LOADER_ONLY,

        /**
         * Consults the cache first and invokes the loader only after a cache miss.
         */
        CACHE_THEN_LOADER
      }
    }
  }

  /**
   * Returns the {@link CDORevision revision} with the given {@link CDOID ID} at the given {@link CDOBranchPoint branch
   * point} (branch + timestamp), optionally demand loading it if it is not already in the {@link CDORevisionCache
   * cache}.
   *
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revision, or
   *          {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param prefetchDepth
   *          The number of nested containment levels to load revisions for in one round trip. Use the symbolic
   *          constants {@link CDORevision#DEPTH_INFINITE} to prefetch all contained revisions or
   *          {@link CDORevision#DEPTH_NONE} to not prefetch anything. Only the explicitly requested revision is
   *          returned by this method. If additional revisions are prefetched they are placed in the revision cache to
   *          speed up subsequent calls to this method.
   * @param loadOnDemand
   *          If the requested revision is not contained in the revision cache it depends on this parameter's value
   *          whether the revision is loaded from the server or <code>null</code> is returned.
   * @deprecated As of 4.37 use {@link #request()} to configure and execute a revision request.
   * @see #getRevisions(List, CDOBranchPoint, int, int, boolean)
   * @see #getRevisionByVersion(CDOID, CDOBranchVersion, int, boolean)
   */
  @Deprecated
  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand);

  /**
   * Returns the {@link CDORevision revisions} with the given {@link CDOID IDs} at the given {@link CDOBranchPoint
   * branch point} (branch + timestamp), optionally demand loading them if they are not already in the
   * {@link CDORevisionCache cache}.
   *
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revisions,
   *          or {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param prefetchDepth
   *          The number of nested containment levels to load revisions for in one round trip. Use the symbolic
   *          constants {@link CDORevision#DEPTH_INFINITE} to prefetch all contained revisions or
   *          {@link CDORevision#DEPTH_NONE} to not prefetch anything. Only the explicitly requested revisions are
   *          returned by this method. If additional revisions are prefetched they are placed in the revision cache to
   *          speed up subsequent calls to this method.
   * @param loadOnDemand
   *          If one or more of the requested revisions is/are not contained in the revision cache it depends on this
   *          parameter's value whether the revision(s) is/are loaded from the server or <code>null</code> is placed in
   *          the list that is returned.
   * @deprecated As of 4.37 use {@link #request()} to configure and execute a revision request.
   * @see #getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   */
  @Deprecated
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand);

  /**
   * Returns the {@link CDORevision revisions} with the given {@link CDOID IDs} at the given {@link CDOBranchPoint
   * branch point} (branch + timestamp), optionally demand loading them if they are not already in the
   * {@link CDORevisionCache cache}.
   *
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revisions,
   *          or {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param prefetchDepth
   *          The number of nested containment levels to load revisions for in one round trip. Use the symbolic
   *          constants {@link CDORevision#DEPTH_INFINITE} to prefetch all contained revisions or
   *          {@link CDORevision#DEPTH_NONE} to not prefetch anything. Only the explicitly requested revisions are
   *          returned by this method. If additional revisions are prefetched they are placed in the revision cache to
   *          speed up subsequent calls to this method.
   * @param loadOnDemand
   *          If one or more of the requested revisions is/are not contained in the revision cache it depends on this
   *          parameter's value whether the revision(s) is/are loaded from the server or <code>null</code> is placed in
   *          the list that is returned.
   * @param additionalRevisions If non-<code>null</code>, a list to add additionally prefetched revisions to.
   * @deprecated As of 4.37 use {@link #request()} to configure and execute a revision request.
   * @see #getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   * @since 4.15
   */
  @Deprecated
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      List<CDORevision> additionalRevisions);

  /**
   * Returns <code>true</code> if the {@link CDORevisionCache revision cache} contains a {@link CDORevision revision}
   * with the given {@link CDOID ID} at the given {@link CDOBranchVersion branch version} (branch + version),
   * <code>false</code> otherwise.
   *
   * @see #getRevisionByVersion(CDOID, CDOBranchVersion, int, boolean)
   */
  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion);

  /**
   * Returns the {@link CDORevision revision} with the given {@link CDOID ID} at the given {@link CDOBranchVersion
   * branch version} (branch + version), optionally demand loading it if it is not already in the
   * {@link CDORevisionCache cache}.
   * <p>
   * Prefetching of nested containment levels is not support by this method because the version of a particular revision
   * can not serve as a reasonable baseline criterion for a consistent graph of multiple revisions.
   *
   * @param referenceChunk
   *          The number of target {@link CDOID IDs} to load for each many-valued reference in the returned revision, or
   *          {@link CDORevision#UNCHUNKED} for all such list elements (IDs).
   * @param loadOnDemand
   *          If the requested revision is not contained in the revision cache it depends on this parameter's value
   *          whether the revision is loaded from the server or <code>null</code> is returned.
   * @deprecated As of 4.37 use {@link #request()} to configure and execute a revision request.
   * @see #getRevision(CDOID, CDOBranchPoint, int, int, boolean)
   */
  @Deprecated
  public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand);
}
