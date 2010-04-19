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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ReplicateRepositoryRawIndication extends CDOReadIndication
{
  private int lastReplicatedBranchID;

  private long lastReplicatedCommitTime;

  public ReplicateRepositoryRawIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY_RAW);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lastReplicatedBranchID = in.readInt();
    lastReplicatedCommitTime = in.readLong();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    CDOReplicationInfo info = getRepository().replicateRaw(out, lastReplicatedBranchID, lastReplicatedCommitTime);
    out.writeInt(info.getLastReplicatedBranchID());
    out.writeLong(info.getLastReplicatedCommitTime());
  }
}
