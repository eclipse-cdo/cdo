/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOUserSavepoint;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOSingleTransactionStrategyImpl implements CDOTransactionStrategy
{
  public static final CDOSingleTransactionStrategyImpl INSTANCE = new CDOSingleTransactionStrategyImpl();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION,
      CDOSingleTransactionStrategyImpl.class);

  public CDOSingleTransactionStrategyImpl()
  {
  }

  public CDOCommitInfo commit(InternalCDOTransaction transaction, IProgressMonitor progressMonitor) throws Exception
  {
    InternalCDOCommitContext commitContext = transaction.createCommitContext();
    if (TRACER.isEnabled())
    {
      TRACER.format("CDOCommitContext.preCommit"); //$NON-NLS-1$
    }

    commitContext.preCommit();

    CommitTransactionResult result = null;
    if (commitContext.getTransaction().isDirty())
    {
      OMMonitor monitor = new EclipseMonitor(progressMonitor);
      result = transaction.getSession().getSessionProtocol().commitTransaction(commitContext, monitor);

      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("CDOCommitContext.postCommit"); //$NON-NLS-1$
    }

    // Needed even for non-dirty transactions to release locks
    commitContext.postCommit(result);

    if (result == null)
    {
      return null;
    }

    InternalCDOCommitInfoManager commitInfoManager = transaction.getSession().getCommitInfoManager();
    return commitInfoManager.createCommitInfo(transaction.getBranch(), result.getTimeStamp(), transaction.getSession()
        .getUserID(), transaction.getCommitComment(), null);
  }

  public void rollback(InternalCDOTransaction transaction, InternalCDOUserSavepoint savepoint)
  {
    transaction.handleRollback((InternalCDOSavepoint)savepoint);
  }

  public InternalCDOUserSavepoint setSavepoint(InternalCDOTransaction transaction)
  {
    return transaction.handleSetSavepoint();
  }

  public void setTarget(InternalCDOTransaction transaction)
  {
    // Do nothing
  }

  public void unsetTarget(InternalCDOTransaction transaction)
  {
    // Do nothing
  }
}
