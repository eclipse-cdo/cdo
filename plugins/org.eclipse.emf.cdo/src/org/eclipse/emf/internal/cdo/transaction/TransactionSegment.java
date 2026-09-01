/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * The mutable changes after one {@link TransactionBoundary boundary} and before the next boundary.
 * This is deliberately separate from the boundary so that a retained savepoint remains a fixed point.
 *
 * @author Eike Stepper
 */
public final class TransactionSegment
{
  /**
   * These maps form the mutable delta between two fixed boundaries. They are not independently thread-safe; all
   * transaction paths that mutate them must hold the owning transaction's synchronization. The detached-object map
   * additionally routes its lifecycle cleanup through that same synchronization before recording the detachment.
   */
  private final Map<CDOID, CDORevision> baseNewObjects = CDOIDUtil.createMap();

  private final Map<CDOID, CDOObject> newObjects = CDOIDUtil.createMap();

  private final Map<CDOID, CDOObject> reattachedObjects = CDOIDUtil.createMap();

  private final Map<CDOID, CDOObject> dirtyObjects = CDOIDUtil.createMap();

  private final Map<CDOID, CDOObject> detachedObjects;

  private final Map<CDOID, CDORevisionDelta> revisionDeltas;

  private final TransactionBoundary boundary;

  /**
   * Creates an empty segment owned by the given transaction.
   *
   * @param transaction the owning transaction.
   * @param boundary the associated boundary.
   */
  public TransactionSegment(InternalCDOTransaction transaction, TransactionBoundary boundary)
  {
    detachedObjects = new HashMap<>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public CDOObject put(CDOID key, CDOObject object)
      {
        return transaction.sync().supply(() -> {
          baseNewObjects.remove(key);
          newObjects.remove(key);
          reattachedObjects.remove(key);
          dirtyObjects.remove(key);
          revisionDeltas.remove(key);
          return super.put(key, object);
        });
      }
    };

    revisionDeltas = new HashMap<>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public CDORevisionDelta put(CDOID id, CDORevisionDelta delta)
      {
        transaction.clearResourcePathCacheIfNecessary(delta);
        return super.put(id, delta);
      }

      @Override
      public void putAll(Map<? extends CDOID, ? extends CDORevisionDelta> map)
      {
        throw new UnsupportedOperationException();
      }
    };

    this.boundary = boundary;
  }

  public TransactionBoundary getBoundary()
  {
    return boundary;
  }

  /**
   * Returns objects that were new at the segment base.
   *
   * @return the mutable base-new object map.
   */
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    return baseNewObjects;
  }

  /**
   * Returns objects created in this segment.
   *
   * @return the mutable new-object map.
   */
  public Map<CDOID, CDOObject> getNewObjects()
  {
    return newObjects;
  }

  /**
   * Returns objects reattached in this segment.
   *
   * @return the mutable reattached-object map.
   */
  public Map<CDOID, CDOObject> getReattachedObjects()
  {
    return reattachedObjects;
  }

  /**
   * Returns objects detached in this segment.
   *
   * @return the mutable detached-object map.
   */
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return detachedObjects;
  }

  /**
   * Returns objects modified in this segment.
   *
   * @return the mutable dirty-object map.
   */
  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return dirtyObjects;
  }

  /**
   * Returns revision deltas recorded in this segment.
   *
   * @return the mutable revision-delta map.
   */
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return revisionDeltas;
  }

  /**
   * Clears all lifecycle and revision state held by this segment.
   */
  public void clear()
  {
    newObjects.clear();
    dirtyObjects.clear();
    revisionDeltas.clear();
    baseNewObjects.clear();
    detachedObjects.clear();
    reattachedObjects.clear();
  }
}
