/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A fixed point in the transaction's linear change history. An invisible
 * boundary can be structurally significant for rollback and scope ownership
 * while remaining transparent to current-state aggregation. Nested scopes use
 * such boundaries for rollback; nested rollback discards segments since the
 * boundary, while nested commit leaves the history in the enclosing repository
 * commit epoch. Only a root repository commit ends that epoch.
 *
 * @author Eike Stepper
 */
public final class TransactionBoundary
{
  /*
   * Boundary links and the associated savepoint are transaction-owned state. Their callers mutate them only from code
   * already executing under InternalCDOTransaction.sync(); this class intentionally does not add a second lock.
   */
  private final TransactionSegment segment;

  private final boolean wasDirty;

  /**
   * The tracked resources that were modified when this fixed point was
   * created. The map deliberately records only positive state: a partial
   * rollback clears every currently tracked resource that is not in this
   * identity set. This keeps history proportional to modified resources.
   */
  private final Map<CDOResource, Boolean> modifiedResources = new IdentityHashMap<>();

  private TransactionBoundary previous;

  private TransactionBoundary next;

  private CDOSavepointImpl savepoint;

  /**
   * Creates a boundary and its mutable segment.
   *
   * @param transaction the owning transaction.
   * @param previous the preceding boundary, or {@code null} for the first boundary.
   */
  public TransactionBoundary(InternalCDOTransaction transaction, TransactionBoundary previous)
  {
    this.previous = previous;
    segment = new TransactionSegment(transaction, this);
    wasDirty = transaction.isDirty();

    ResourceSet resourceSet = transaction.getResourceSet();
    if (resourceSet == null)
    {
      return;
    }

    for (Resource resource : resourceSet.getResources())
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        if (cdoResource.isTrackingModification() && cdoResource.isModified())
        {
          modifiedResources.put(cdoResource, Boolean.TRUE);
        }
      }
    }
  }

  /**
   * Indicates whether the transaction was dirty when this boundary was
   * created. Restoration uses this internal history state rather than a
   * public savepoint handle.
   */
  public boolean wasDirty()
  {
    return wasDirty;
  }

  /**
   * Returns the mutable segment following this boundary.
   *
   * @return the segment.
   */
  public TransactionSegment getSegment()
  {
    return segment;
  }

  /**
   * Returns the preceding boundary.
   *
   * @return the previous boundary, or {@code null}.
   */
  public TransactionBoundary getPrevious()
  {
    return previous;
  }

  /**
   * Sets the preceding boundary link.
   *
   * @param previous the previous boundary, or {@code null}.
   */
  public void setPrevious(TransactionBoundary previous)
  {
    this.previous = previous;
  }

  /**
   * Returns the following boundary.
   *
   * @return the next boundary, or {@code null}.
   */
  public TransactionBoundary getNext()
  {
    return next;
  }

  /**
   * Sets the following boundary link.
   *
   * @param next the next boundary, or {@code null}.
   */
  public void setNext(TransactionBoundary next)
  {
    this.next = next;
  }

  /**
   * Restores modification tracking to the state captured at this boundary.
   * This is separate from transaction rollback lifecycle callbacks because a
   * savepoint or scope rollback is not a root transaction rollback.
   *
   * @param transaction the owning transaction.
   */
  public void restoreModifiedResources(InternalCDOTransaction transaction)
  {
    ResourceSet resourceSet = transaction.getResourceSet();
    if (resourceSet == null)
    {
      return;
    }

    for (Resource resource : resourceSet.getResources())
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        if (cdoResource.isTrackingModification())
        {
          cdoResource.setModified(modifiedResources.containsKey(cdoResource));
        }
      }
    }
  }

  /**
   * Returns the savepoint fixed at this boundary.
   *
   * @return the savepoint, or {@code null} until one is associated.
   */
  public CDOSavepointImpl getSavepoint()
  {
    return savepoint;
  }

  /**
   * Associates a savepoint with this boundary.
   *
   * @param savepoint the savepoint to associate, or {@code null}.
   */
  public void setSavepoint(CDOSavepointImpl savepoint)
  {
    this.savepoint = savepoint;
  }
}
