/*
 * Copyright (c) 2009-2012, 2019, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetDataProvider;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * One in a sequence of possibly several points in time of a {@link CDOTransaction transaction} that encapsulates the
 * changes to transactional objects and that later changes can be {@link #rollback() rolled back} to.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSavepoint extends CDOUserSavepoint, CDOTransactionAware, CDOChangeSetDataProvider
{
  /**
   * Returns the transaction that owns this savepoint.
   *
   * @return the owning transaction.
   * @since 3.0
   */
  @Override
  public CDOTransaction getTransaction();

  /**
   * Returns the next savepoint after this one.
   *
   * @return the next savepoint, or {@code null}.
   */
  @Override
  public CDOSavepoint getNextSavepoint();

  /**
   * Returns the savepoint preceding this one.
   *
   * @return the previous savepoint, or {@code null}.
   */
  @Override
  public CDOSavepoint getPreviousSavepoint();

  /**
   * Indicates whether the transaction was dirty when this savepoint was created.
   *
   * @return {@code true} if the transaction was dirty.
   * @since 3.0
   */
  public boolean wasDirty();

  /**
   * Returns objects that were already new at this savepoint's boundary.
   *
   * @return the base-new object map.
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects();

  /**
   * Returns objects created at this savepoint.
   *
   * @return the new-object map.
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * Returns objects detached at this savepoint.
   *
   * @return the detached-object map.
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  /**
   * Returns objects reattached at this savepoint.
   * Bug 283985 (Re-attachment)
   *
   * @return the reattached-object map.
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getReattachedObjects();

  /**
   * Returns objects modified at this savepoint.
   *
   * @return the dirty-object map.
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDirtyObjects();

  /**
   * Returns deltas of the revisions modified at this savepoint.
   *
   * @since 4.2
   */
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas2();

  /**
   * Returns the aggregate of base revisions at this savepoint's fixed boundary.
   * Changes recorded in the mutable segment that follows this savepoint are not included.
   *
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getAllBaseNewObjects();

  /**
   * Returns the aggregate of new objects at this savepoint's fixed boundary, excluding objects
   * that were removed before that boundary. Changes recorded in the mutable segment that follows
   * this savepoint are not included.
   *
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllNewObjects();

  /**
   * Returns the aggregate of detached objects at this savepoint's fixed boundary.
   * Changes recorded in the mutable segment that follows this savepoint are not included.
   *
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDetachedObjects();

  /**
   * Returns the aggregate of dirty objects at this savepoint's fixed boundary.
   * Changes recorded in the mutable segment that follows this savepoint are not included.
   *
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDirtyObjects();

  /**
   * Returns the aggregate of revision deltas at this savepoint's fixed boundary, excluding
   * objects that were removed before that boundary. Changes recorded in the mutable segment that
   * follows this savepoint are not included.
   *
   * @since 3.0
   */
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltas();

  /**
   * Returns the change-set data at this savepoint's fixed boundary.
   * Changes recorded in the mutable segment that follows this savepoint are not included.
   *
   * @since 4.0
   */
  public CDOChangeSetData getAllChangeSetData();

  /**
   * The returned map delegates to {@link #getRevisionDeltas2()} and does <b>not</b> support the following methods:
   * <ul>
   * <li> {@link ConcurrentMap#putIfAbsent(Object, Object)}
   * <li> {@link ConcurrentMap#remove(Object, Object)}
   * <li> {@link ConcurrentMap#replace(Object, Object)}
   * <li> {@link ConcurrentMap#replace(Object, Object, Object)}
   * </ul>
   *
   * @since 3.0
   * @deprecated As of 4.2 use {@link #getRevisionDeltas2()} instead.
   */
  @Deprecated
  public ConcurrentMap<CDOID, CDORevisionDelta> getRevisionDeltas();
}
