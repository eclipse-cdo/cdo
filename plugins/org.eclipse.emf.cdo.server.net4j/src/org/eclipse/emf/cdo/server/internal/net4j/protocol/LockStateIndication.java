/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Caspar De Groot
 */
public class LockStateIndication extends CDOServerReadIndication
{
  private Collection<CDOLockState> existingLockStates;

  public LockStateIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    InternalView view = getSession().getView(viewID);
    if (view == null)
    {
      throw new IllegalStateException("View not found");
    }

    InternalLockManager lockManager = getRepository().getLockingManager();

    existingLockStates = new ArrayList<CDOLockState>();
    int n = in.readInt();
    if (n == 0)
    {
      Collection<LockState<Object, IView>> lockStates = lockManager.getLockStates();
      for (LockState<Object, IView> lockState : lockStates)
      {
        existingLockStates.add(CDOLockUtil.createLockState(lockState));
      }
    }
    else
    {
      for (int i = 0; i < n; i++)
      {
        Object key = indicatingCDOID(in, view.getBranch());
        LockState<Object, IView> lockState = lockManager.getLockState(key);
        if (lockState != null)
        {
          existingLockStates.add(CDOLockUtil.createLockState(lockState));
        }
      }
    }
  }

  private Object indicatingCDOID(CDODataInput in, CDOBranch viewedBranch) throws IOException
  {
    CDOID id = in.readCDOID();
    if (getRepository().isSupportingBranches())
    {
      return CDOIDUtil.createIDAndBranch(id, viewedBranch);
    }

    return id;
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeInt(existingLockStates.size());
    for (CDOLockState lockState : existingLockStates)
    {
      out.writeCDOLockState(lockState);
    }
  }
}
