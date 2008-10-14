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
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;

import org.eclipse.emf.internal.cdo.CDOXATransactionCommitContext;

import java.io.IOException;

/**
 * Phase 1 will send all the modifications to the server.
 * <p>
 * It needs to fill id mappings for objects immediately to be use by other {@link CDOTransaction} involve in that
 * commit.
 * 
 * @author Simon McDuff
 */
public class CommitTransactionPhase1Request extends CommitTransactionRequest
{
  public CommitTransactionPhase1Request(CDOClientProtocol protocol, final CDOXATransactionCommitContext xaTransaction)
  {
    super(protocol, xaTransaction);
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE1;
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in) throws IOException
  {
    CommitTransactionResult result = confirmingCheckError(in);
    if (result != null)
    {
      return result;
    }

    result = confirmingTransactionResult(in);
    confirmingIdMapping(in, result);
    return result;
  }

  @Override
  protected CDOIDProvider getIDProvider()
  {
    return (CDOIDProvider)commitContext;
  }
}
