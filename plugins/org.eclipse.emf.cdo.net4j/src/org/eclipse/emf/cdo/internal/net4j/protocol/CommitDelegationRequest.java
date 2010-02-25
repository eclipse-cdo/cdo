/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CommitDelegationRequest extends CommitTransactionRequest
{
  private static final int UNKNOWN_TRANSACTION_ID = 0;

  private CDOBranch branch;

  private String userID;

  public CommitDelegationRequest(CDOClientProtocol protocol, CDOBranch branch, String userID, String comment,
      CDOCommitData commitData)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_DELEGATION, UNKNOWN_TRANSACTION_ID, comment, false,
        CDOIDProvider.NOOP, commitData);

    this.branch = branch;
    this.userID = userID;
  }

  @Override
  protected void requestingTransactionInfo(CDODataOutput out) throws IOException
  {
    out.writeCDOBranch(branch);
    out.writeString(userID);
  }
}
