/*
 * Copyright (c) 2016, 2017, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.UnitOpcode;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class UnitRequest extends CDOClientRequestWithMonitoring<Boolean>
{
  private int viewID;

  private CDOID rootID;

  private UnitOpcode opcode;

  private boolean prefetchLockStates;

  private CDORevisionHandler revisionHandler;

  public UnitRequest(CDOClientProtocol protocol, int viewID, CDOID rootID, UnitOpcode opcode, boolean prefetchLockStates, CDORevisionHandler revisionHandler)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNIT);
    this.viewID = viewID;
    this.rootID = rootID;
    this.opcode = opcode;
    this.prefetchLockStates = prefetchLockStates;
    this.revisionHandler = revisionHandler;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeXInt(viewID);
    out.writeCDOID(rootID);
    out.writeByte(opcode.ordinal());

    if (opcode.canPrefetchLockStates())
    {
      out.writeBoolean(prefetchLockStates);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    if (opcode.isOpen())
    {
      for (;;)
      {
        CDORevision revision = in.readCDORevision();
        if (revision == null)
        {
          break;
        }

        revisionHandler.handleRevision(revision);
      }
    }

    boolean result = in.readBoolean();

    if (opcode.canPrefetchLockStates() && result)
    {
      InternalCDOSession session = getSession();
      CDOView view = session.getView(viewID);
      CDOBranch branch = view.getBranch();

      CDOClientProtocol.readAndCacheLockStates(in, session, branch);
    }

    return result;
  }
}
