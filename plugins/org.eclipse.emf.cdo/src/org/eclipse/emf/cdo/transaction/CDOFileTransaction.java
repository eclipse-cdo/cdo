/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;

/**
 * A transaction whose uncommitted changes can be persisted in a local file and explicitly pushed to the repository.
 * A normal {@link #commit()} exports changes to the file and does not commit to the repository. Repository
 * persistence is performed only by {@link #push()}.
 * <p>
 * The inherited {@link #rollback()} operation is not supported, and inherited commit overloads that accept a
 * callable or runnable are rejected because they cannot provide unambiguous local-export semantics.
 *
 * @author Eike Stepper
 * @since 4.30
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOFileTransaction extends CDOTransaction
{
  /**
   * Returns the stable backing file of this transaction.
   *
   * @return the backing file.
   */
  public File getFile();

  /**
   * Commits the transaction's changes to the repository and removes the persisted file.
   *
   * @throws CommitException if the repository commit fails.
   */
  public void push() throws CommitException;

  /**
   * Commits the transaction's changes to the repository and removes the persisted file.
   *
   * @param monitor the progress monitor, or {@code null}.
   * @throws CommitException if the repository commit fails.
   */
  public void push(IProgressMonitor monitor) throws CommitException;
}
