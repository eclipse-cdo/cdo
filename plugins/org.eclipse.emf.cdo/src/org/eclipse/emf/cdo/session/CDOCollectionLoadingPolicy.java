/*
 * Copyright (c) 2009-2012, 2014, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOListResolver;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A legacy strategy that specifies which list elements must be present (loaded) in a {@link CDOID} list of a
 * {@link CDORevision revision} when a certain list index is accessed. Implementations of this interface can control the
 * exact characteristics of a certain <em>partial collection loading</em> strategy.
 *
 * @author Simon McDuff
 * @since 2.0
 * @deprecated As of 4.31 use {@link CDOCollectionLoadingConfig}. Legacy
 * {@link #resolveProxy(CDORevision, EStructuralFeature, int, int) execution methods} are no longer
 * supported and policies overriding them are rejected when installed in a session.
 */
@Deprecated
public interface CDOCollectionLoadingPolicy extends CDOListResolver
{
  /**
   * Retained for source compatibility with policies written before collection loading execution was moved to the
   * session resolver. New code must use the session's resolver instead.
   *
   * @deprecated As of 4.31 the policy is declarative and must not execute collection loading.
   */
  @Deprecated
  @Override
  public default Object resolveProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    throw new UnsupportedOperationException("Collection loading policies do not execute loading");
  }

  /**
   * Retained for source compatibility with policies written before collection loading execution was moved to the
   * session resolver.
   *
   * @deprecated As of 4.31 the policy is declarative and must not execute collection loading.
   */
  @Deprecated
  @Override
  public default void resolveAllProxies(CDORevision revision, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Collection loading policies do not execute loading");
  }

  /**
   * @since 4.0
   * @deprecated As of 4.31 legacy policy API; use {@link CDOCollectionLoadingConfig}.
   */
  @Deprecated
  public CDOSession getSession();

  /**
   * @since 4.0
   * @deprecated As of 4.31 legacy policy API; use {@link CDOCollectionLoadingConfig}.
   */
  @Deprecated
  public void setSession(CDOSession session);

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when the owning object is loaded initially, i.e.
   * <b>before</b> any of the collection elements is actually accessed. The remaining elements will be initialized as
   * {@link CDOElementProxy proxies}.
   *
   * @deprecated As of 4.31 legacy policy API; use {@link CDOCollectionLoadingConfig}.
   */
  @Deprecated
  public int getInitialChunkSize();

  /**
   * Returns the maximum number of CDOIDs to be loaded initially for the given many-valued feature. The default
   * implementation preserves the revision-independent policy represented by {@link #getInitialChunkSize()}.
   *
   * <p>
   * Current revision loading requests carry one initial chunk size for the complete revision. Consequently, this
   * feature-aware form is available for policy composition and future per-feature initial loading, but it is not used
   * to split a single revision request into different initial chunks.
   *
   * @param revision
   *          the revision that owns the feature
   * @param feature
   *          the many-valued feature
   * @return the maximum number of initially loaded elements
   * @since 4.30
   * @deprecated As of 4.31 legacy policy API; use {@link CDOCollectionLoadingConfig}.
   */
  @Deprecated
  public default int getInitialChunkSize(CDORevision revision, EStructuralFeature feature)
  {
    return getInitialChunkSize();
  }

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when the owning object is already loaded but the
   * actually accessed element is still a {@link CDOElementProxy proxy}.
   *
   * @since 4.0
   * @deprecated As of 4.31 legacy policy API; use {@link CDOCollectionLoadingConfig}.
   */
  @Deprecated
  public int getResolveChunkSize();

  /**
   * Returns the maximum number of CDOIDs to be loaded around an accessed proxy in the given many-valued feature. This
   * is the feature-aware policy decision used by lazy collection resolution.
   *
   * @param revision
   *          the revision that owns the feature
   * @param feature
   *          the many-valued feature
   * @return the maximum number of elements to load around the accessed element
   * @since 4.30
   * @deprecated As of 4.31 legacy policy API; use {@link CDOCollectionLoadingConfig}.
   */
  @Deprecated
  public default int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
  {
    return getResolveChunkSize();
  }
}
