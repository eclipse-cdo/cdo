/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.transaction;

/**
 * A closed-nested lifecycle region in a {@link CDOTransaction transaction}.
 * <p>
 * A scope shares its transaction's view, repository session, resource set, object identities, object cache, dirty
 * state, and locks. Changes made in a scope are immediately visible in the containing transaction and remain part of
 * its effective state until the scope or an enclosing transaction rolls them back.
 * <p>
 * Committing a scope accepts its changes into its outer scope or the root transaction. It never commits to the
 * repository, assigns permanent IDs, establishes a repository baseline, or starts a repository commit epoch. Only
 * the root transaction's repository commit persists the effective changes.
 * Scopes belong to the transaction rather than to a thread: they may be opened and completed on different threads.
 * Individual lifecycle operations are atomic; callers that need a larger isolated sequence must use the transaction's
 * existing synchronization mechanism.
 *
 * @author Eike Stepper
 * @since 4.30
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransactionScope extends CDOTransactionAware, CDORollbackable, AutoCloseable
{
  /**
   * Returns the root transaction that contains this scope.
   *
   * @return the containing root transaction.
   */
  @Override
  public CDOTransaction getTransaction();

  /**
   * Returns the immediately enclosing scope, or {@code null} for the outermost scope.
   *
   * @return the outer scope, or {@code null}.
   */
  public CDOTransactionScope getOuterScope();

  /**
   * Returns the immediately nested scope, or {@code null} if this is currently innermost.
   *
   * @return the inner scope, or {@code null}.
   */
  public CDOTransactionScope getInnerScope();

  /**
   * Returns this scope's one-based nesting depth.
   *
   * @return the scope depth.
   */
  public int getDepth();

  /**
   * Indicates whether this scope is still open.
   *
   * @return {@code true} if the scope can still be completed.
   */
  public boolean isOpen();

  /**
   * Opens a child scope relative to this scope.
   *
   * @return the newly opened child scope.
   * @throws IllegalStateException if this scope is not the current innermost scope.
   */
  public CDOTransactionScope openScope();

  /**
   * Accepts this scope into its outer scope or root transaction.
   * <p>
   * This operation is a nested-scope commit only. It does not persist changes, assign permanent IDs, establish a
   * repository baseline, or close the root transaction's repository commit epoch. A later root transaction commit is
   * required for persistence.
   *
   * @throws IllegalStateException if this scope is not the current innermost open scope or is already closed.
   */
  public void commit();

  /**
   * Rolls this scope and all of its open child scopes back to the state at this scope's beginning.
   * <p>
   * Changes made before this scope remain in the containing transaction. The rollback does not affect the repository
   * commit epoch.
   *
   * @throws IllegalStateException if this scope is already closed.
   */
  @Override
  public void rollback();

  /**
   * Rolls this scope back when it is still open.
   * <p>
   * Calling this method after the scope has closed has no effect. In particular, closing an active scope rolls back
   * its changes, while closing an already closed scope is idempotent.
   */
  @Override
  public void close();

  /**
   * Returns the transaction-compatible facade for this scope. Repeated calls return the same facade instance.
   *
   * @return the stable nested transaction facade.
   */
  public CDONestedTransaction asTransaction();
}
