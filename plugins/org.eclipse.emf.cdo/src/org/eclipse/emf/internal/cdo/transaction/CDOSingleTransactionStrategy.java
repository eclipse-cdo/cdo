/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Simon McDuff - initial API and implementation
 *   Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOSavepoint;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOSingleTransactionStrategy implements CDOTransactionStrategy
{
  public static final CDOSingleTransactionStrategy INSTANCE = new CDOSingleTransactionStrategy();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOSingleTransactionStrategy.class);

  public CDOSingleTransactionStrategy()
  {
  }

  public void commit(InternalCDOTransaction transaction, IProgressMonitor progressMonitor) throws Exception
  {
    InternalCDOCommitContext commitContext = transaction.createCommitContext();
    if (TRACER.isEnabled())
    {
      TRACER.format("CDOCommitContext.preCommit");
    }

    commitContext.preCommit();
    CommitTransactionResult result = null;
    if (commitContext.getTransaction().isDirty())
    {
      CDOClientProtocol protocol = (CDOClientProtocol)transaction.getSession().getProtocol();
      CommitTransactionRequest request = new CommitTransactionRequest(protocol, commitContext);
      if (TRACER.isEnabled())
      {
        TRACER.format("Sending commit request");
      }

      result = request.send(new EclipseMonitor(progressMonitor));
      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }
    }
    if (TRACER.isEnabled())
    {
      TRACER.format("CDOCommitContext.postCommit");
    }
    commitContext.postCommit(result);
  }

  public void rollback(InternalCDOTransaction transaction, CDOSavepoint savepoint)
  {
    transaction.handleRollback(savepoint);
  }

  public CDOSavepoint setSavepoint(InternalCDOTransaction transaction)
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
