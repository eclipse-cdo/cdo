/*
 * Copyright (c) 2009-2012, 2014, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.transaction;

/**
 * Creates a save point in a {@link CDOUserTransaction} that can be used to roll back a part of the transaction.
 * <p>
 * <b>Note:</b> Save points do not flush to disk. Everything is done in memory on the client side.
 *
 * @author Simon McDuff
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOUserSavepoint extends CDORollbackable
{
  /**
   * Returns the transaction that owns this savepoint.
   *
   * @return the owning transaction.
   */
  public CDOUserTransaction getTransaction();

  /**
   * Returns the next savepoint in the transaction's savepoint sequence.
   *
   * @return the next savepoint, or {@code null}.
   */
  public CDOUserSavepoint getNextSavepoint();

  /**
   * Returns the previous savepoint in the transaction's savepoint sequence.
   *
   * @return the previous savepoint, or {@code null}.
   */
  public CDOUserSavepoint getPreviousSavepoint();

  /**
   * Returns this savepoint's sequence number.
   *
   * @return the savepoint number.
   * @since 4.1
   */
  public int getNumber();

  /**
   * Indicates whether this savepoint can still be used.
   *
   * @return {@code true} if the savepoint is valid.
   */
  public boolean isValid();

  /**
   * Indicates whether this is the transaction's current last savepoint.
   *
   * @return {@code true} if this is the last savepoint.
   * @since 4.30
   */
  public boolean isLast();

  /**
   * Rolls the owning transaction back to this savepoint while keeping the
   * owning root transaction active. This restores only the changes made after
   * this savepoint; it does not perform the lifecycle operation represented by
   * {@link CDOTransaction#rollback()} and therefore does not finish the root
   * transaction.
   */
  @Override
  public void rollback();
}
