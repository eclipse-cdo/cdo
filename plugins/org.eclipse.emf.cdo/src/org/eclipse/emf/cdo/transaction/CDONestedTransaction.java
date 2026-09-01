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
 * A transaction-compatible facade for a {@link CDOTransactionScope transaction scope}.
 * <p>
 * The facade shares the enclosing transaction's object identities, object cache, resource set, dirty state, locks,
 * and repository session. Changes made through the facade are immediately part of the enclosing transaction's
 * effective state.
 * <p>
 * The facade's transaction commit operations are unsupported. A nested scope is accepted with
 * {@link CDOTransactionScope#commit()}, which does not persist changes, assign permanent IDs, establish a repository
 * baseline, or start a repository commit epoch. Only a commit on the containing root {@link CDOTransaction} persists
 * the effective changes.
 * <p>
 * {@link CDOTransactionScope#rollback()} restores the shared transaction to the state at the beginning of the scope.
 * Nested scopes are stack-disciplined, and a root transaction commit remains unavailable while a nested scope is open.
 *
 * @author Eike Stepper
 * @since 4.30
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDONestedTransaction extends CDOTransaction
{
  /**
   * Returns the lifecycle scope represented by this transaction-like facade.
   *
   * @return the represented scope.
   */
  public CDOTransactionScope getScope();
}
