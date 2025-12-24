/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
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
public interface CDOSavepoint extends CDOUserSavepoint, CDOChangeSetDataProvider
{
  /**
   * @since 3.0
   */
  @Override
  public CDOTransaction getTransaction();

  @Override
  public CDOSavepoint getNextSavepoint();

  @Override
  public CDOSavepoint getPreviousSavepoint();

  /**
   * @since 3.0
   */
  public boolean wasDirty();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  /**
   * Bug 283985 (Re-attachment)
   *
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getReattachedObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDirtyObjects();

  /**
   * The returned map delegates to {@link #getRevisionDeltas2()} and does <b>not</b> support the following methods:
   *
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

  /**
   * @since 4.2
   */
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas2();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getAllBaseNewObjects();

  /**
   * Return the list of new objects from this point without objects that are removed.
   *
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDetachedObjects();

  /**
   * Return the list of new objects from this point.
   *
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDirtyObjects();

  /**
   * Return the list of all deltas without objects that are removed.
   *
   * @since 3.0
   */
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltas();

  /**
   * @since 4.0
   */
  public CDOChangeSetData getAllChangeSetData();
}
