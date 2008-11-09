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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class LockObjectsIndication extends CDOReadIndication
{
  public LockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    RWLockManager.LockType lockType = in.readCDOLockType();
    long timeout = in.readLong();

    int size = in.readInt();
    List<CDOID> ids = new ArrayList<CDOID>(size);
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      ids.add(id);
    }

    try
    {
      IView view = getSession().getView(viewID);
      getRepository().getLockManager().lock(lockType, view, ids, timeout);
    }
    catch (InterruptedException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
