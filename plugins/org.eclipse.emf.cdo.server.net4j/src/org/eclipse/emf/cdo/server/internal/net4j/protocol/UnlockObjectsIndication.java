/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class UnlockObjectsIndication extends CDOReadIndication
{
  public UnlockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    LockType lockType = in.readCDOLockType();
    int size = in.readInt();

    InternalRepository repository = getRepository();
    InternalLockManager lockManager = repository.getLockManager();
    IView view = getSession().getView(viewID);

    if (size == CDOProtocolConstants.RELEASE_ALL_LOCKS)
    {
      lockManager.unlock(true, view);
    }
    else
    {
      boolean supportingBranches = repository.isSupportingBranches();
      CDOBranch branch = view.getBranch();

      List<Object> keys = new ArrayList<Object>(size);
      for (int i = 0; i < size; i++)
      {
        CDOID id = in.readCDOID();
        Object key = supportingBranches ? CDOIDUtil.createIDAndBranch(id, branch) : id;
        keys.add(key);
      }

      lockManager.unlock(true, lockType, view, keys);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
