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
 * A scope shares the transaction's view, resource set, object identities, and locks. Committing a scope accepts its
 * changes into its outer scope; it never commits to the repository.
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
   * Returns the transaction that contains this scope.
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
   */
  public void commit();

  /**
   * Rolls this scope and all of its open child scopes back to the scope boundary.
   *
   * @throws IllegalStateException if this scope is already closed.
   */
  @Override
  public void rollback();

  /**
   * Rolls this scope back when it is still open. Calling this method after the scope has closed has no effect.
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
