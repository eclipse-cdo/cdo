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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class SyncRepositoryRequest extends CDOClientRequest<Boolean>
{
  private CDOReplicationContext context;

  public SyncRepositoryRequest(CDOClientProtocol protocol, CDOReplicationContext context)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SYNC_REPOSITORY);
    this.context = context;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(context.getLastReplicatedBranchID());
    out.writeLong(context.getLastReplicatedCommitTime());
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    for (;;)
    {
      byte opcode = in.readByte();
      switch (opcode)
      {
      case CDOProtocolConstants.SYNC_FINISHED:
        return true;

      case CDOProtocolConstants.SYNC_BRANCH:
        context.handleBranch(in.readCDOBranch());
        break;

      case CDOProtocolConstants.SYNC_COMMIT:
        context.handleCommitInfo(in.readCDOCommitInfo());
        break;

      default:
        throw new IOException("Invalid sync opcode: " + opcode);
      }
    }
  }
}
