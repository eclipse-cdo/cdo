/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * A fixed point in the transaction's linear change history.
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
    segment = new TransactionSegment(transaction);
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
