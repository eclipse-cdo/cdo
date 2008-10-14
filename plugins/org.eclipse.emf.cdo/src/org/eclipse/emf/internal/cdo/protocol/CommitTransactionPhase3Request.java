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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.CDOXATransactionCommitContext;

import java.io.IOException;

/**
 * Determine at which moment the server side can complete the transaction.
 * <p>
 * At this stage, everything on the database was done except to flush on the disk.
 * <p>
 * It is useful to assure that all {@link CDOTransaction} involve in that commit are synchronize.
 * 
 * @author Simon McDuff
 */
public class CommitTransactionPhase3Request extends CommitTransactionRequest
{
  public CommitTransactionPhase3Request(CDOClientProtocol protocol, final CDOXATransactionCommitContext xaTransaction)
  {
    super(protocol, xaTransaction);
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE3;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    requestingTransactionInfo(out);
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in) throws IOException
  {
    CommitTransactionResult result = confirmingCheckError(in);
    if (result != null)
    {
      return result;
    }

    CDOXATransactionCommitContext context = (CDOXATransactionCommitContext)getCommitContext();
    confirmingNewPackage(in, context.getResult());
    return context.getResult();
  }
}
