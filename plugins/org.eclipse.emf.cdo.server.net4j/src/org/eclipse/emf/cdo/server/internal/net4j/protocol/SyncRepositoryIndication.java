/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;

import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class SyncRepositoryIndication extends CDOReadIndication
{
  private int lastReplicatedBranchID;

  private long lastReplicatedCommitTime;

  public SyncRepositoryIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SYNC_REPOSITORY);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lastReplicatedBranchID = in.readInt();
    lastReplicatedCommitTime = in.readLong();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    getRepository().sync(new CDOReplicationContext()
    {
      public int getLastReplicatedBranchID()
      {
        return lastReplicatedBranchID;
      }

      public long getLastReplicatedCommitTime()
      {
        return lastReplicatedCommitTime;
      }

      public void handleBranch(CDOBranch branch)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.SYNC_BRANCH);
          out.writeCDOBranch(branch);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.SYNC_COMMIT);
          out.writeCDOCommitInfo(commitInfo);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    out.writeByte(CDOProtocolConstants.SYNC_FINISHED);
  }
}
