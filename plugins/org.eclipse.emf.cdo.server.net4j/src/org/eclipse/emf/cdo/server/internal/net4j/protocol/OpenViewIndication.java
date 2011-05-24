/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenViewIndication extends CDOServerReadIndication
{
  private CDOBranchPoint result;

  private String message;

  public OpenViewIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalSession session = getSession();

    int viewID = in.readInt();
    boolean readOnly = in.readBoolean();

    if (in.readBoolean())
    {
      CDOBranchPoint branchPoint = in.readCDOBranchPoint();
      if (readOnly)
      {
        session.openView(viewID, branchPoint);
      }
      else
      {
        session.openTransaction(viewID, branchPoint);
      }
    }
    else
    {
      InternalLockManager lockManager = getRepository().getLockManager();

      try
      {
        String durableLockingID = in.readString();
        result = lockManager.openView(session, viewID, readOnly, durableLockingID);
      }
      catch (LockAreaNotFoundException ex)
      {
        // Client uses durableLockingID!=null && result==null to detect exceptional case
      }
      catch (IllegalStateException ex)
      {
        message = ex.getMessage();
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    if (result != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranchPoint(result);
    }
    else
    {
      out.writeBoolean(false);
      out.writeString(message);
    }
  }
}
