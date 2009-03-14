/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Simon McDuff
 */
public class CommitTransactionPhase1Indication extends CommitTransactionIndication
{
  public CommitTransactionPhase1Indication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE1);
  }

  @Override
  protected void indicatingCommit(OMMonitor monitor)
  {
    // Register transactionContext
    getRepository().getCommitManager().preCommit(commitContext, monitor);
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

    boolean success = respondingException(out, exceptionMessage);
    if (success)
    {
      respondingTimestamp(out);
      respondingMappingNewObjects(out);
    }
  }

  @Override
  protected void indicatingTransaction(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = new XATransactionCommitContext(getTransaction(viewID));

  }
}
