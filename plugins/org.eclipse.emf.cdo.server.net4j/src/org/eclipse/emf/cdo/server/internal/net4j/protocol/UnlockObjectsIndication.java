/*
 * Copyright (c) 2009-2012, 2016, 2017, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class UnlockObjectsIndication extends CDOServerWriteIndication
{
  private UnlockObjectsResult result;

  public UnlockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS);
  }

  protected UnlockObjectsIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readXInt();
    LockType lockType = in.readCDOLockType();
    boolean recursive = in.readBoolean();
    int size = in.readXInt();

    InternalRepository repository = getRepository();
    IView view = getView(viewID);

    if (size == CDOProtocolConstants.RELEASE_ALL_LOCKS)
    {
      result = repository.unlock((InternalView)view, null, null, false);
    }
    else
    {
      List<CDOID> objectIDs = new LinkedList<>();
      for (int i = 0; i < size; i++)
      {
        objectIDs.add(in.readCDOID());
      }

      result = repository.unlock((InternalView)view, lockType, objectIDs, recursive);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeXLong(result.getTimestamp());
    out.writeCDOLockDeltas(result.getLockDeltas(), null);
    out.writeCDOLockStates(result.getLockStates(), null);
  }
}
