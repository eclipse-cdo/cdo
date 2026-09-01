/*
 * Copyright (c) 2009-2012, 2016, 2019, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;

import java.util.Map;
import java.util.Set;

/**
 * Adds transaction-internal access to the mutable segment owned by a savepoint. The public
 * {@link CDOSavepoint} aggregate methods expose only the fixed boundary represented by a returned
 * savepoint. Transaction internals use the {@code IncludingCurrent} methods when they need the
 * active transaction state, including the mutable segment after that boundary.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOSavepoint extends CDOSavepoint, InternalCDOUserSavepoint
{
  @Override
  public InternalCDOTransaction getTransaction();

  @Override
  public InternalCDOSavepoint getFirstSavePoint();

  @Override
  public InternalCDOSavepoint getPreviousSavepoint();

  @Override
  public InternalCDOSavepoint getNextSavepoint();

  public void clear();

  @Deprecated
  public Set<CDOID> getSharedDetachedObjects();

  @Deprecated
  public void recalculateSharedDetachedObjects();

  /**
   * @since 4.1
   */
  public boolean isNewObject(CDOID id);

  /**
   * @since 4.6
   */
  public CDOObject getDirtyObject(CDOID id);

  /**
   * @since 4.6
   */
  public CDOObject getDetachedObject(CDOID id);

  /**
   * Returns the aggregate through this savepoint, including its mutable current segment.
   * This method is for transaction internals; clients must use the fixed-boundary public API.
   *
   * @since 4.30
   */
  public Map<CDOID, CDOObject> getAllNewObjectsIncludingCurrent();

  /**
   * Returns the aggregate through this savepoint, including its mutable current segment.
   * This method is for transaction internals; clients must use the fixed-boundary public API.
   *
   * @since 4.30
   */
  public Map<CDOID, CDOObject> getAllDirtyObjectsIncludingCurrent();

  /**
   * Returns the aggregate through this savepoint, including its mutable current segment.
   * This method is for transaction internals; clients must use the fixed-boundary public API.
   *
   * @since 4.30
   */
  public Map<CDOID, CDORevision> getAllBaseNewObjectsIncludingCurrent();

  /**
   * Returns the aggregate through this savepoint, including its mutable current segment.
   * This method is for transaction internals; clients must use the fixed-boundary public API.
   *
   * @since 4.30
   */
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltasIncludingCurrent();

  /**
   * Returns the aggregate through this savepoint, including its mutable current segment.
   * This method is for transaction internals; clients must use the fixed-boundary public API.
   *
   * @since 4.30
   */
  public Map<CDOID, CDOObject> getAllDetachedObjectsIncludingCurrent();

  /**
   * Returns change-set data through this savepoint, including its mutable current segment.
   * This method is for transaction internals; clients must use the fixed-boundary public API.
   *
   * @since 4.30
   */
  public CDOChangeSetData getAllChangeSetDataIncludingCurrent();
}
