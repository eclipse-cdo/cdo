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

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class CommitTransactionPhase3Indication extends CommitTransactionIndication
{
  public CommitTransactionPhase3Indication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE3);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    indicationTransaction(in);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    commitContext.commit();
    boolean success = respondingException(out, commitContext.getRollbackMessage());
    if (success)
    {
      respondingMappingNewPackages(out);
    }

    commitContext.postCommit(success);
  }

  @Override
  protected void indicationTransaction(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    commitContext = getRepository().getCommitManager().get(getTransaction(viewID));
  }
}
