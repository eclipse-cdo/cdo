/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class CommitTransactionPhase1Indication extends CommitTransactionIndication
{
  public CommitTransactionPhase1Indication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE1;
  }

  @Override
  protected void indicatingCommit()
  {
    // Register transactionContext
    getRepository().getCommitManager().preCommit(commitContext);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    String exceptionMessage = null;

    try
    {
      ((XATransactionCommitContext)commitContext).getState().acquire(XATransactionCommitContext.PHASEAPPLYMAPPING);
    }
    catch (InterruptedException ex)
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
  protected void indicationTransaction(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    commitContext = new XATransactionCommitContext(getTransaction(viewID));
  }
}
