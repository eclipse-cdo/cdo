/*
 * Copyright (c) 2009, 2011, 2012, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Internal coordinator for an XA transaction spanning multiple CDO transactions. It collects participating
 * transactions and their savepoints, then drives their commit or rollback as one coordinated operation.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOXATransaction extends CDOXATransaction, InternalCDOUserTransaction
{
  /**
   * @since 3.0
   */
  @Override
  public InternalCDOXASavepoint setSavepoint();

  /**
   * @since 3.0
   */
  @Override
  public InternalCDOXASavepoint getLastSavepoint();

  /**
   * @since 3.0
   */
  public void rollback(InternalCDOXASavepoint savepoint);

  /**
   * @since 3.0
   */
  public void add(InternalCDOTransaction transaction, CDOID object);

  /**
   * @since 3.0
   */
  public void add(InternalCDOTransaction transaction);

  /**
   * @since 3.0
   */
  public void remove(InternalCDOTransaction transaction);

  public InternalCDOXACommitContext getCommitContext(CDOTransaction transaction);

  /**
   * Per-operation state passed through an XA commit. It gathers the participating transactions' requested object IDs,
   * carries the protocol result, and invokes the state-specific work that advances or rolls back the coordinated commit.
   *
   * @author Eike Stepper
   * @since 2.0
   */
  public interface InternalCDOXACommitContext extends Callable<Object>, CDOIDProvider, InternalCDOCommitContext
  {
    public InternalCDOXATransaction getTransactionManager();

    public Map<CDOIDTempObjectExternalImpl, InternalCDOTransaction> getRequestedIDs();

    public CommitTransactionResult getResult();

    /**
     * @since 3.0
     */
    public void setResult(CommitTransactionResult result);

    /**
     * @since 3.0
     */
    public CDOXAState getState();

    /**
     * @since 3.0
     */
    public void setState(CDOXAState state);

    /**
     * @since 3.0
     */
    public void setProgressMonitor(IProgressMonitor progressMonitor);

    /**
     * A phase in the XA commit state machine. Each state validates the repository result and performs the work needed
     * to advance the shared commit context or report a rollback to the participating transactions.
     *
     * @author Simon McDuff
     * @since 3.0
     */
    public static abstract class CDOXAState
    {
      public void check_result(CommitTransactionResult result)
      {
        if (result != null && result.getRollbackMessage() != null)
        {
          throw new TransactionException(result.getRollbackMessage());
        }
      }

      public abstract void handle(InternalCDOXACommitContext xaContext, IProgressMonitor progressMonitor) throws Exception;
    }
  }
}
