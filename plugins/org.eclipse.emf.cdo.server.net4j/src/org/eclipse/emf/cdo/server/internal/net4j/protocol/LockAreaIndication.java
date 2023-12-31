/*
 * Copyright (c) 2011, 2012, 2016, 2017, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalView;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LockAreaIndication extends CDOServerWriteIndication
{
  private String result;

  public LockAreaIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_AREA);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalLockManager lockManager = getRepository().getLockingManager();

    int viewID = in.readXInt();
    InternalView view = getView(viewID);

    boolean create = in.readBoolean();
    if (create)
    {
      CDOLockOwner oldOwner = view.getLockOwner();
      LockArea area = lockManager.createLockArea(view);

      result = area.getDurableLockingID();
      view.setDurableLockingID(result);

      CDOLockOwner newOwner = view.getLockOwner();
      if (newOwner != oldOwner)
      {
        CDOBranch branch = view.getBranch();
        InternalSession session = view.getSession();
        InternalSessionManager manager = session.getManager();
        manager.sendLockOwnerRemappedNotification(session, branch, oldOwner, newOwner);
      }
    }
    else
    {
      String durableLockingID = view.getDurableLockingID();
      lockManager.deleteLockArea(durableLockingID);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeString(result);
  }
}
