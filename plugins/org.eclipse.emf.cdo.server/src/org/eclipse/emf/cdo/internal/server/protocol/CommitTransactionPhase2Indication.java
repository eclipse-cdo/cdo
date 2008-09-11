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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext.CommitState;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Simon McDuff
 */
public class CommitTransactionPhase2Indication extends CommitTransactionIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitTransactionPhase2Indication.class);

  public CommitTransactionPhase2Indication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE2;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    indicationTransaction(in);
    XATransactionCommitContext xaTransactionContext = (XATransactionCommitContext)commitContext;

    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Receiving {0} mapping informations", size);
    }

    for (int i = 0; i < size; i++)
    {
      CDOIDTemp oldID = (CDOIDTemp)in.readCDOID();
      CDOID newID = in.readCDOID();
      xaTransactionContext.addIDMapping(oldID, newID);
    }

    // Mapping information from others CDOTransactions was added. Notify the commit process to continue.
    xaTransactionContext.getState().set(CommitState.APPLY_ID_MAPPING_DONE);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    String exceptionMessage = null;

    try
    {
      // Return to the client only when the process is ready to commit
      getRepository().getCommitManager().waitForTermination(commitContext.getTransaction());
    }
    catch (InterruptedException ex)
    {
      exceptionMessage = ex.getMessage();
    }
    catch (ExecutionException ex)
    {
      exceptionMessage = ex.getMessage();
    }

    if (exceptionMessage == null)
    {
      exceptionMessage = commitContext.getRollbackMessage();
    }

    respondingException(out, exceptionMessage);
  }

  @Override
  protected void indicationTransaction(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    commitContext = getRepository().getCommitManager().get(getTransaction(viewID));
  }
}
