/*
 * Copyright (c) 2010-2013, 2015, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Simon McDuff
 */
public class CommitXATransactionPhase1Indication extends CommitTransactionIndication
{
  public CommitXATransactionPhase1Indication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE1);
  }

  @Override
  protected boolean closeInputStreamAfterMe()
  {
    // The commit manager processes phase1 asynchronously, so don't close the input stream on him.
    return false;
  }

  @Override
  protected void indicatingCommit(CDODataInput in, OMMonitor monitor)
  {
    // Register transactionContext
    getRepository().getCommitManager().preCommit(commitContext, in, monitor);
  }

  @Override
  protected void initializeCommitContext(CDODataInput in) throws Exception
  {
    int viewID = in.readXInt();
    InternalTransaction transaction = getTransaction(viewID);
    commitContext = new XATransactionCommitContext(transaction);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    String exceptionMessage = null;

    try
    {
      ((XATransactionCommitContext)commitContext).getState().acquire(XATransactionCommitContext.PHASEAPPLYMAPPING);
    }
    catch (Throwable ex)
    {
      exceptionMessage = ex.getMessage();
    }

    if (exceptionMessage == null)
    {
      exceptionMessage = commitContext.getRollbackMessage();
    }

    boolean success = respondingException(out, CDOProtocolConstants.ROLLBACK_REASON_UNKNOWN, exceptionMessage, null);
    if (success)
    {
      respondingResult(out);
      respondingMappingNewObjects(out);
    }
  }
}
